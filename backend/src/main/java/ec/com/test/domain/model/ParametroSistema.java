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
public class ParametroSistema {
    private Long idParametro;
    private String codigoParametro;
    private String canal;
    private String codigoTransaccion;
    private BigDecimal valor;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
}
