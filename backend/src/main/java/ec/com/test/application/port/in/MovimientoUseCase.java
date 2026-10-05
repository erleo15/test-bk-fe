package ec.com.test.application.port.in;

import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.model.Movimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoUseCase {
    Movimiento registrarMovimientoExitoso(Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal monto, String observacion, String estado, String motivoEstado);

    Movimiento registrarMovimientoRechazado(Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal monto, String observacion, String motivoRechazo);

    List<Movimiento> consultarHistorial(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado, Integer page, Integer pageSize);

    long contarHistorial(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado);
}
