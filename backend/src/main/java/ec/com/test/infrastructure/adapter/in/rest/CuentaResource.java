package ec.com.test.infrastructure.adapter.in.rest;

import ec.com.test.application.port.in.CuentaUseCase;
import ec.com.test.domain.constant.DomainConstants;
import ec.com.test.domain.model.Cliente;
import ec.com.test.domain.model.Cuenta;
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

import java.util.List;
import java.util.UUID;

@Path("/api/cuentas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cuentas", description = "Operaciones sobre cuentas bancarias")
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CuentaResource {

    private final CuentaUseCase cuentaUseCase;
    private final DtoMapper mapper;

    @Context
    ContainerRequestContext requestContext;

    @POST
    @Operation(summary = "Registrar nueva cuenta bancaria", description = "Registra una cuenta de ahorros o corriente para un cliente. Si el cliente no existe previamente, es registrado automáticamente en el sistema.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Cuenta bancaria y cliente asociados creados exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ResponseFrame.class))),
            @APIResponse(responseCode = "400", description = "Petición inválida: formato numérico o de cédula incorrecto, campos obligatorios faltantes o saldo inicial menor a cero",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"El número de cuenta debe tener exactamente 10 dígitos numéricos\",\"codigoHttp\":400,\"timestamp\":\"2026-10-04T22:00:00\"}"))),
            @APIResponse(responseCode = "409", description = "Conflicto: ya existe una cuenta bancaria registrada con el número proporcionado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"Ya existe una cuenta registrada con el número: 1234567890\",\"codigoHttp\":409,\"timestamp\":\"2026-10-04T22:00:00\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno inesperado del servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"Ocurrió un error interno en el servidor\",\"codigoHttp\":500,\"timestamp\":\"2026-10-04T22:00:00\"}")))
    })
    public Response crearCuenta(@Valid RequestFrame<CrearCuentaRequest> requestFrame) {
        String processId = getProcessId(requestFrame != null ? requestFrame.getProcess() : null);
        Cuenta cuentaDomain = mapper.toDomain(requestFrame.getCtl());
        Cliente clienteDomain = mapper.toClienteDomain(requestFrame.getCtl());
        CuentaResponse response = mapper.toCuentaResponse(cuentaUseCase.crearCuenta(cuentaDomain, clienteDomain));

        return Response.status(Response.Status.CREATED)
                .entity(ResponseFrame.exitoso(processId, DomainConstants.HTTP_CREATED, "Cuenta bancaria creada exitosamente", response))
                .build();
    }

    @GET
    @Operation(summary = "Listar cuentas bancarias", description = "Lista todas las cuentas bancarias registradas dentro del wrapper ResponseFrame.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Listado de cuentas recuperado con éxito",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ResponseFrame.class))),
            @APIResponse(responseCode = "500", description = "Error interno inesperado del servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response listarCuentas(
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("sortBySaldo") String sortBySaldo) {
        String processId = getProcessId(null);
        List<CuentaResponse> response = mapper.toCuentaResponseList(cuentaUseCase.listarCuentas(page, pageSize, sortBySaldo));
        long total = cuentaUseCase.contarCuentas();

        return Response.ok(ResponseFrame.paginado(processId, DomainConstants.HTTP_OK, "Listado de cuentas obtenido con éxito", page, pageSize, total, response)).build();
    }

    @GET
    @Path("/{id}/resumen")
    @Operation(summary = "Obtener el resumen de cuenta", description = "Devuelve el saldo actual, total transferido hoy, cantidad de transferencias del día y sospechosas dentro de ResponseFrame.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Resumen de la cuenta generado exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ResponseFrame.class))),
            @APIResponse(responseCode = "404", description = "Cuenta no encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"No se encontró la cuenta con ID: 99\",\"codigoHttp\":404,\"timestamp\":\"2026-10-04T22:00:00\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno inesperado del servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class,
                            example = "{\"processId\":\"a3f2b1c4-0000-0000-0000-000000000000\",\"mensaje\":\"Ocurrió un error interno en el servidor\",\"codigoHttp\":500,\"timestamp\":\"2026-10-04T22:00:00\"}")))
    })
    public Response obtenerResumen(
            @Parameter(description = "Número de cuenta bancaria (ej. 1234567890) o ID interno")
            @PathParam("id") String id) {
        String processId = getProcessId(null);
        ResumenCuentaResponse response = mapper.toResumenCuentaResponse(cuentaUseCase.obtenerResumenCuenta(id));

        return Response.ok(ResponseFrame.exitoso(processId, DomainConstants.HTTP_OK, "Resumen de cuenta obtenido con éxito", response)).build();
    }

    private String getProcessId(String fallbackProcess) {
        String processId = (String) requestContext.getProperty(DomainConstants.HEADER_PROCESS_ID);
        if (processId == null || processId.isBlank()) {
            return fallbackProcess != null ? fallbackProcess : UUID.randomUUID().toString();
        }
        return processId;
    }
}
