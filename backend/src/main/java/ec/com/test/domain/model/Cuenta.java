package ec.com.test.domain.model;

import ec.com.test.domain.constant.DomainConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Cuenta {
    private Long idCuenta;
    private Long clienteId;
    private String identificacionTitular;
    private String primerNombreTitular;
    private String segundoNombreTitular;
    private String apellidoPaternoTitular;
    private String apellidoMaternoTitular;
    private String nombreLegalTitular;
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldo;
    private String estado;

    public boolean isActiva() {
        return DomainConstants.ESTADO_CUENTA_ACTIVA.equalsIgnoreCase(this.estado);
    }

    public boolean tieneSaldoSuficiente(BigDecimal monto) {
        return this.saldo != null && this.saldo.compareTo(monto) >= 0;
    }

    public void debitar(BigDecimal monto) {
        this.saldo = this.saldo.subtract(monto);
    }

    public void mechar(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }
}
