package ec.com.test.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ErrorResponse", description = "Respuesta de error estándar")
public class ErrorResponse {
    @Schema(description = "Identificador único de trazabilidad de la petición", example = "a3f2b1c4-0000-0000-0000-000000000000")
    private String processId;
    @Schema(description = "Descripción detallada del error ocurrido", example = "Descripción del error ocurrido en la operación")
    private String mensaje;
    @Schema(description = "Código de estado HTTP de la respuesta de error", example = "400")
    private int codigoHttp;
    @Schema(description = "Fecha y hora del error", example = "2026-10-04T00:42:00")
    private LocalDateTime timestamp;
}
