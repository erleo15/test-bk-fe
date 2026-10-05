package ec.com.test.infrastructure.adapter.in.rest;

import ec.com.test.application.port.in.TransferenciaUseCase;
import ec.com.test.domain.constant.DomainConstants;
import ec.com.test.infrastructure.adapter.in.rest.dto.*;
import ec.com.test.infrastructure.adapter.in.rest.mapper.DtoMapper;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Path("/api/transferencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Transferencias", description = "Operaciones de transferencias interbancarias")
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class TransferenciaResource {

    private final TransferenciaUseCase transferenciaUseCase;
    private final DtoMapper mapper;

    @Context
    ContainerRequestContext requestContext;

    @POST
    @Operation(summary = "Ejecutar una transferencia interbancaria", description = "Procesa una transferencia entre dos cuentas. Valida formato de solicitud, existencia y estado de cuentas, saldo disponible, límite diario acumulado y reglas de prevención de fraude.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Transferencia ejecutada exitosamente (o en estado REQUIERE_REVISION si activó reglas de monitoreo de fraude)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ResponseFrame.class))),
            @APIResponse(responseCode = "400", description = "Petición inválida: formato JSON erróneo, campos obligatorios ausentes o violación de restricciones de validación",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"El monto de la transferencia debe ser mayor a cero\",\"codigoHttp\":400,\"timestamp\":\"2026-10-04T22:00:00\"}"))),
            @APIResponse(responseCode = "404", description = "Recurso no encontrado: la cuenta de origen o destino no existe",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"La cuenta de origen no existe: 1234567890\",\"codigoHttp\":404,\"timestamp\":\"2026-10-04T22:00:00\"}"))),
            @APIResponse(responseCode = "422", description = "Entidad no procesable: cuenta inactiva, saldo insuficiente, o límite diario acumulado ($10,000.00) excedido",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"Excede el límite diario permitido de $10000.00. Monto restante disponible para hoy: $0.00\",\"codigoHttp\":422,\"timestamp\":\"2026-10-04T22:00:00\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno inesperado del servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"Ocurrió un error interno en el servidor\",\"codigoHttp\":500,\"timestamp\":\"2026-10-04T22:00:00\"}")))
    })
    public Response ejecutarTransferencia(@Valid RequestFrame<TransferenciaRequest> requestFrame) {
        String processId = getProcessId(requestFrame != null ? requestFrame.getProcess() : null);
        TransferenciaRequest req = requestFrame.getCtl();
        MovimientoResponse response = mapper.toMovimientoResponse(
                transferenciaUseCase.ejecutarTransferencia(req.getNumeroCuentaOrigen(), req.getNumeroCuentaDestino(), req.getMonto(), req.getObservacion())
        );

        return Response.ok(ResponseFrame.exitoso(processId, DomainConstants.HTTP_OK, "Transferencia procesada correctamente", response)).build();
    }

    @GET
    @Operation(summary = "Consultar el historial de transferencias", description = "Permite consultar el historial de transferencias aplicando filtros opcionales por cuenta origen, cuenta destino, rango de fechas y estado (COMPLETADA, RECHAZADA, REQUIERE_REVISION), retornando resultados ordenados por fecha descendente.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Historial obtenido correctamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ResponseFrame.class))),
            @APIResponse(responseCode = "500", description = "Error interno inesperado del servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response consultarHistorial(
            @Parameter(description = "Número o ID de cuenta de origen para filtrar")
            @QueryParam("cuentaOrigen") String cuentaOrigen,
            @Parameter(description = "Alias compatible para ID de cuenta de origen")
            @QueryParam("cuentaOrigenId") String cuentaOrigenId,
            @Parameter(description = "Número o ID de cuenta de destino para filtrar")
            @QueryParam("cuentaDestino") String cuentaDestino,
            @Parameter(description = "Alias compatible para ID de cuenta de destino")
            @QueryParam("cuentaDestinoId") String cuentaDestinoId,
            @Parameter(description = "Fecha inicial de filtro en formato ISO (ej. 2026-10-01T00:00:00)")
            @QueryParam("fechaInicio") String fechaInicio,
            @Parameter(description = "Fecha final de filtro en formato ISO (ej. 2026-10-31T23:59:59)")
            @QueryParam("fechaFin") String fechaFin,
            @Parameter(description = "Estado de la transferencia (COMPLETADA, RECHAZADA, REQUIERE_REVISION)")
            @QueryParam("estado") String estado,
            @Parameter(description = "Número de página (0-indexed, opcional; si se omite devuelve todas)")
            @QueryParam("page") Integer page,
            @Parameter(description = "Tamaño de página (opcional; si se omite devuelve todas)")
            @QueryParam("pageSize") Integer pageSize) {
        String processId = getProcessId(null);
        String origenFiltro = (cuentaOrigen != null && !cuentaOrigen.isBlank()) ? cuentaOrigen : cuentaOrigenId;
        String destinoFiltro = (cuentaDestino != null && !cuentaDestino.isBlank()) ? cuentaDestino : cuentaDestinoId;

        LocalDateTime start = fechaInicio != null && !fechaInicio.isBlank() ? LocalDateTime.parse(fechaInicio) : null;
        LocalDateTime end = fechaFin != null && !fechaFin.isBlank() ? LocalDateTime.parse(fechaFin) : null;

        List<MovimientoResponse> response = mapper.toMovimientoResponseList(
                transferenciaUseCase.consultarHistorial(origenFiltro, destinoFiltro, start, end, estado, page, pageSize)
        );
        long total = transferenciaUseCase.contarHistorial(origenFiltro, destinoFiltro, start, end, estado);

        return Response.ok(ResponseFrame.paginado(processId, DomainConstants.HTTP_OK, "Historial de transferencias obtenido con éxito", page, pageSize, total, response)).build();
    }

    private String getProcessId(String fallbackProcess) {
        String processId = (String) requestContext.getProperty(DomainConstants.HEADER_PROCESS_ID);
        if (processId == null || processId.isBlank()) {
            return fallbackProcess != null ? fallbackProcess : UUID.randomUUID().toString();
        }
        return processId;
    }
}
