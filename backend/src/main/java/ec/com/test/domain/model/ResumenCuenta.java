package ec.com.test.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class ResumenCuenta {
    private BigDecimal saldoActual;
    private BigDecimal totalTransferidoHoy;
    private long cantidadTransferenciasDia;
    private long cantidadSospechosas;
}
