package ec.com.test.infrastructure.adapter.in.rest.handler;

import ec.com.test.domain.constant.DomainConstants;
import ec.com.test.domain.exception.BusinessException;
import ec.com.test.infrastructure.adapter.in.rest.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(Throwable exception) {
        int statusCode = DomainConstants.HTTP_INTERNAL_SERVER_ERROR;
        String mensaje = "Ocurrió un error interno en el servidor: " + exception.getMessage();

        if (exception instanceof BusinessException be) {
            statusCode = be.getStatusCode();
            mensaje = be.getMessage();
            log.warn("Business exception [HTTP {}]: {}", statusCode, mensaje);
        } else if (exception instanceof ConstraintViolationException cve) {
            statusCode = DomainConstants.HTTP_BAD_REQUEST;
            mensaje = cve.getConstraintViolations().stream()
                    .map(constraintViolation -> constraintViolation.getMessage())
                    .collect(Collectors.joining("; "));
            log.warn("Constraint violation [HTTP {}]: {}", statusCode, mensaje);
        } else if (exception instanceof jakarta.ws.rs.WebApplicationException wae) {
            statusCode = wae.getResponse() != null ? wae.getResponse().getStatus() : DomainConstants.HTTP_NOT_FOUND;
            if (statusCode == DomainConstants.HTTP_NOT_FOUND) {
                mensaje = "El recurso o endpoint solicitado no existe";
            } else {
                mensaje = wae.getMessage();
            }
            log.warn("Web application exception [HTTP {}]: {}", statusCode, mensaje);
        } else if (exception instanceof NumberFormatException || exception instanceof IllegalArgumentException) {
            statusCode = DomainConstants.HTTP_BAD_REQUEST;
            mensaje = "Parámetro inválido o formato de dato incorrecto: " + exception.getMessage();
            log.warn("Invalid parameter [HTTP {}]: {}", statusCode, mensaje, exception);
        } else {
            log.error("Unexpected exception [HTTP {}]: {}", statusCode, exception.getMessage(), exception);
        }

        String processId = null;
        try {
            if (requestContext != null) {
                processId = (String) requestContext.getProperty(DomainConstants.HEADER_PROCESS_ID);
            }
        } catch (Exception ignored) {
        }
        if (processId == null || processId.isBlank()) {
            processId = UUID.randomUUID().toString();
        }

        ErrorResponse errorResponse = new ErrorResponse(processId, mensaje, statusCode, DomainConstants.nowEcuador());
        return Response.status(statusCode).entity(errorResponse).build();
    }
}
