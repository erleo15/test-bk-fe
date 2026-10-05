package ec.com.test.domain.port;

import ec.com.test.domain.model.Movimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepositoryPort {
    Movimiento guardar(Movimiento movimiento);

    Movimiento guardarEnNuevaTransaccion(Movimiento movimiento);

    List<Movimiento> buscarConFiltros(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado, int page, int pageSize);

    long contarConFiltros(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado);

    BigDecimal calcularTotalTransferidoHoy(Long cuentaOrigenId, LocalDateTime inicioDia, LocalDateTime finDia);

    long contarTransferenciasUltimaHora(Long cuentaOrigenId, LocalDateTime haceUnaHora);

    long contarTransferenciasDia(Long cuentaOrigenId, LocalDateTime inicioDia, LocalDateTime finDia);

    long contarSospechosasDia(Long cuentaOrigenId, LocalDateTime inicioDia, LocalDateTime finDia);
}
