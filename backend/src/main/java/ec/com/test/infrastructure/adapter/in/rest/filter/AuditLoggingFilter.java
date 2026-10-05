package ec.com.test.infrastructure.adapter.in.rest.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.com.test.domain.constant.DomainConstants;
import ec.com.test.infrastructure.audit.AuditService;
import ec.com.test.infrastructure.audit.entity.LogAuditoriaEntity;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Provider
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AuditLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String PROCESS_ID_PROPERTY = DomainConstants.HEADER_PROCESS_ID;
    private static final String START_TIME_PROPERTY = DomainConstants.HEADER_START_TIME;
    private static final String REQUEST_BODY_PROPERTY = DomainConstants.HEADER_REQUEST_BODY;
    private static final String HEADERS_PROPERTY = DomainConstants.HEADER_HEADERS_JSON;

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String processId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        requestContext.setProperty(PROCESS_ID_PROPERTY, processId);
        requestContext.setProperty(START_TIME_PROPERTY, startTime);

        String headersJson = "{}";
        try {
            headersJson = objectMapper.writeValueAsString(requestContext.getHeaders());
        } catch (Exception e) {
            log.warn("Error serializando headers a JSON", e);
        }
        requestContext.setProperty(HEADERS_PROPERTY, headersJson);

        String requestBody = "";
        if (requestContext.hasEntity()) {
            InputStream is = requestContext.getEntityStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] bytes = baos.toByteArray();
            requestBody = new String(bytes, StandardCharsets.UTF_8);
            requestContext.setEntityStream(new ByteArrayInputStream(bytes));
        }

        requestContext.setProperty(REQUEST_BODY_PROPERTY, requestBody);
        requestContext.getHeaders().add(DomainConstants.HEADER_PROCESS_ID, processId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String processId = (String) requestContext.getProperty(PROCESS_ID_PROPERTY);
        Long startTime = (Long) requestContext.getProperty(START_TIME_PROPERTY);
        String requestBody = (String) requestContext.getProperty(REQUEST_BODY_PROPERTY);
        String headersJson = (String) requestContext.getProperty(HEADERS_PROPERTY);

        if (processId == null) {
            processId = UUID.randomUUID().toString();
        }

        long executionTime = startTime != null ? (System.currentTimeMillis() - startTime) : 0;
        responseContext.getHeaders().add(DomainConstants.HEADER_PROCESS_ID, processId);

        String responseBody = "";
        if (responseContext.hasEntity()) {
            Object entity = responseContext.getEntity();
            if (entity instanceof String) {
                responseBody = (String) entity;
            } else if (entity != null) {
                try {
                    responseBody = objectMapper.writeValueAsString(entity);
                } catch (Exception e) {
                    responseBody = entity.toString();
                }
            }
        }

        String fullUrl = requestContext.getUriInfo().getRequestUri().toString();

        LogAuditoriaEntity logInstance = new LogAuditoriaEntity();
        logInstance.setProcessId(processId);
        logInstance.setOperacion(requestContext.getMethod() + " " + fullUrl);
        logInstance.setHeaders(headersJson != null && headersJson.length() > DomainConstants.MAX_AUDIT_PAYLOAD_LENGTH
                ? headersJson.substring(0, DomainConstants.MAX_AUDIT_PAYLOAD_LENGTH)
                : headersJson);
        logInstance.setPayloadEntrada(
                requestBody != null && requestBody.length() > DomainConstants.MAX_AUDIT_PAYLOAD_LENGTH
                        ? requestBody.substring(0, DomainConstants.MAX_AUDIT_PAYLOAD_LENGTH)
                        : requestBody);
        logInstance.setPayloadSalida(
                responseBody != null && responseBody.length() > DomainConstants.MAX_AUDIT_PAYLOAD_LENGTH
                        ? responseBody.substring(0, DomainConstants.MAX_AUDIT_PAYLOAD_LENGTH)
                        : responseBody);
        logInstance.setCodigoHttp(responseContext.getStatus());
        logInstance.setTiempoEjecucionMs((int) executionTime);
        logInstance.setFechaRegistro(DomainConstants.nowEcuador());

        auditService.saveAuditLog(logInstance);
    }
}
