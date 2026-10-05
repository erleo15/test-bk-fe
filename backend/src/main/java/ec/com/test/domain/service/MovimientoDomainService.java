package ec.com.test.domain.service;

import ec.com.test.application.port.in.MovimientoUseCase;
import ec.com.test.domain.constant.DomainConstants;
import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.model.Movimiento;
import ec.com.test.domain.port.CuentaRepositoryPort;
import ec.com.test.domain.port.MovimientoRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class MovimientoDomainService implements MovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepository;
    private final CuentaRepositoryPort cuentaRepository;

    public MovimientoDomainService(MovimientoRepositoryPort movimientoRepository, CuentaRepositoryPort cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public Movimiento registrarMovimientoExitoso(Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal monto,
                                                 String observacion, String estado, String motivoEstado) {
        actualizarSaldos(cuentaOrigen, cuentaDestino, monto);
        Movimiento movimiento = construirMovimiento(cuentaOrigen, cuentaDestino, monto, observacion, estado, motivoEstado);
        Movimiento guardado = movimientoRepository.guardar(movimiento);
        asignarNumerosCuentas(guardado, cuentaOrigen, cuentaDestino);
        log.info("Movimiento exitoso registrado con ID: {}", guardado.getIdMovimiento());
        return guardado;
    }

    @Override
    public Movimiento registrarMovimientoRechazado(Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal monto,
                                                   String observacion, String motivoRechazo) {
        Movimiento movimiento = construirMovimiento(cuentaOrigen, cuentaDestino, monto, observacion,
                DomainConstants.ESTADO_MOVIMIENTO_RECHAZADA, motivoRechazo);
        Movimiento guardado = movimientoRepository.guardarEnNuevaTransaccion(movimiento);
        asignarNumerosCuentas(guardado, cuentaOrigen, cuentaDestino);
        log.warn("Movimiento rechazado guardado con ID: {}. Motivo: {}", guardado.getIdMovimiento(), motivoRechazo);
        return guardado;
    }

    private void actualizarSaldos(Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal monto) {
        if (cuentaOrigen != null) {
            cuentaOrigen.debitar(monto);
            cuentaRepository.actualizar(cuentaOrigen);
        }
        if (cuentaDestino != null) {
            cuentaDestino.mechar(monto);
            cuentaRepository.actualizar(cuentaDestino);
        }
    }

    private Movimiento construirMovimiento(Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal monto,
                                           String observacion, String estado, String motivoEstado) {
        Movimiento movimiento = new Movimiento();
        if (cuentaOrigen != null) {
            movimiento.setNumeroCuentaOrigen(cuentaOrigen.getNumeroCuenta());
        }
        if (cuentaDestino != null) {
            movimiento.setNumeroCuentaDestino(cuentaDestino.getNumeroCuenta());
        }
        movimiento.setMonto(monto);
        movimiento.setObservacion(observacion);
        movimiento.setEstado(estado);
        movimiento.setFechaCreacion(DomainConstants.nowEcuador());
        movimiento.setMotivoEstado(motivoEstado);
        return movimiento;
    }

    private void asignarNumerosCuentas(Movimiento movimiento, Cuenta cuentaOrigen, Cuenta cuentaDestino) {
        if (cuentaOrigen != null) {
            movimiento.setNumeroCuentaOrigen(cuentaOrigen.getNumeroCuenta());
        }
        if (cuentaDestino != null) {
            movimiento.setNumeroCuentaDestino(cuentaDestino.getNumeroCuenta());
        }
    }

    @Override
    public List<Movimiento> consultarHistorial(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio,
                                               LocalDateTime fechaFin, String estado, Integer page, Integer pageSize) {
        int targetPage = (page != null && page >= 0) ? page : 0;
        int targetPageSize = (pageSize != null && pageSize > 0) ? pageSize : 0;
        return movimientoRepository.buscarConFiltros(cuentaOrigen, cuentaDestino, fechaInicio, fechaFin, estado, targetPage, targetPageSize);
    }

    @Override
    public long contarHistorial(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio,
                                LocalDateTime fechaFin, String estado) {
        return movimientoRepository.contarConFiltros(cuentaOrigen, cuentaDestino, fechaInicio, fechaFin, estado);
    }
}
