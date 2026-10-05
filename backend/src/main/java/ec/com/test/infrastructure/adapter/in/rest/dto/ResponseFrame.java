package ec.com.test.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ec.com.test.domain.constant.DomainConstants;
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
@Schema(name = "ResponseFrame", description = "Trama / Wrapper estándar de respuesta REST")
public class ResponseFrame<T> {

    @Schema(description = "Identificador único de proceso o trazabilidad", example = "PROC-8921378")
    private String process;

    @Schema(description = "Código HTTP o de respuesta", example = "200")
    private int code;

    @Schema(description = "Mensaje informativo o de estado", example = "Operación ejecutada con éxito")
    private String message;

    @Schema(description = "Fecha y hora del procesamiento", example = "2026-10-03T21:25:00")
    private LocalDateTime timestamp;

    @Schema(description = "Número de página solicitada (0-indexed)", example = "0")
    private Integer page;

    @Schema(description = "Tamaño de la página solicitada", example = "10")
    private Integer size;

    @Schema(description = "Total de registros disponibles", example = "50")
    private Long total;

    @Schema(description = "Payload/DTO de respuesta de la operación")
    private T ctl;

    public ResponseFrame(String process, int code, String message, LocalDateTime timestamp, T ctl) {
        this.process = process;
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.ctl = ctl;
    }

    public static <T> ResponseFrame<T> exitoso(String process, int code, String message, T ctl) {
        return new ResponseFrame<>(process, code, message, DomainConstants.nowEcuador(), ctl);
    }

    public static <T> ResponseFrame<T> paginado(String process, int code, String message, Integer page, Integer size, Long total, T ctl) {
        return new ResponseFrame<>(process, code, message, DomainConstants.nowEcuador(), page, size, total, ctl);
    }
}
