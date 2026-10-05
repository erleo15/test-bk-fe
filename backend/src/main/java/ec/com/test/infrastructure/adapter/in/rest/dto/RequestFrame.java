package ec.com.test.infrastructure.adapter.in.rest.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@RegisterForReflection
@Schema(name = "RequestFrame", description = "Trama / Wrapper estándar de entrada para peticiones REST")
public class RequestFrame<T> {

    @NotBlank(message = "El campo userapp es obligatorio")
    @Schema(description = "Usuario o aplicación consumidora", example = "WEB_ANGULAR", required = true)
    private String userapp;

    @NotBlank(message = "El campo process es obligatorio")
    @Schema(description = "Identificador único de proceso o trazabilidad", example = "PROC-8921378", required = true)
    private String process;

    @NotBlank(message = "El campo channel es obligatorio")
    @Schema(description = "Canal de origen de la petición", example = "WEB", required = true)
    private String channel;

    @NotBlank(message = "El campo operation es obligatorio")
    @Schema(description = "Operación solicitada", example = "CREAR_CUENTA", required = true)
    private String operation;

    @Schema(description = "Dirección IP origen de la petición", example = "192.168.1.50")
    private String ip;

    @Valid
    @NotNull(message = "El objeto ctl es obligatorio")
    @Schema(description = "Payload/DTO específico del endpoint", required = true)
    private T ctl;
}
