package ec.com.test.application.port.in;

import ec.com.test.domain.model.Movimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransferenciaUseCase {
    Movimiento ejecutarTransferencia(String numeroCuentaOrigen, String numeroCuentaDestino, BigDecimal monto, String observacion);

    List<Movimiento> consultarHistorial(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado, Integer page, Integer pageSize);

    long contarHistorial(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado);
}
