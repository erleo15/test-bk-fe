package ec.com.test.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Movimiento {
    private Long idMovimiento;
    private String numeroMovimiento;
    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
    private BigDecimal monto;
    private String observacion;
    private String estado;
    private LocalDateTime fechaCreacion;
    private String motivoEstado;
}
