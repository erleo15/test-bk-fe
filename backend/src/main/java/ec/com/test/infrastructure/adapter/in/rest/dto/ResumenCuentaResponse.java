package ec.com.test.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@RegisterForReflection
@Schema(name = "ResumenCuentaResponse", description = "Resumen operativo diario de una cuenta")
public class ResumenCuentaResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Saldo actual disponible en la cuenta", example = "1500.00")
    private BigDecimal saldoActual;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Total acumulado transferido durante el día", example = "500.00")
    private BigDecimal totalTransferidoHoy;

    @Schema(description = "Cantidad de transferencias realizadas el día de hoy", example = "2")
    private long cantidadTransferenciasDia;

    @Schema(description = "Cantidad de transferencias marcadas como sospechosas hoy", example = "0")
    private long cantidadSospechosas;
}
