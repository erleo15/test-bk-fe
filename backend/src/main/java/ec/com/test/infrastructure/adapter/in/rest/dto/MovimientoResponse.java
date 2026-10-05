package ec.com.test.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@RegisterForReflection
@Schema(name = "MovimientoResponse", description = "Respuesta con la información del movimiento registrado")
public class MovimientoResponse {

    @Schema(description = "Número de movimiento para trazabilidad", example = "TRF-A3F2B1C4")
    private String numeroMovimiento;

    @Schema(description = "Número de la cuenta de origen", example = "1234567890")
    private String numeroCuentaOrigen;

    @Schema(description = "Número de la cuenta de destino", example = "0987654321")
    private String numeroCuentaDestino;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Monto de la transferencia", example = "250.00")
    private BigDecimal monto;

    @Schema(description = "Concepto u observación ingresada por el cliente", example = "Pago de cena")
    private String observacion;

    @Schema(description = "Estado resultante (COMPLETADA, RECHAZADA, REQUIERE_REVISION)", example = "COMPLETADA")
    private String estado;

    @Schema(description = "Fecha y hora de creación", example = "2026-10-03T21:00:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Motivo del estado de la transferencia", example = "Transferencia ejecutada con éxito")
    private String motivoEstado;
}
