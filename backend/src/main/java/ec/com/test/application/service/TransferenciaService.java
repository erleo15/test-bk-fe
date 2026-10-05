package ec.com.test.application.service;

import ec.com.test.application.port.in.MovimientoUseCase;
import ec.com.test.application.port.in.TransferenciaUseCase;
import ec.com.test.domain.constant.DomainConstants;
import ec.com.test.domain.exception.BusinessException;
import ec.com.test.domain.exception.ResourceNotFoundException;
import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.model.Movimiento;
import ec.com.test.domain.model.ParametroSistema;
import ec.com.test.domain.port.CuentaRepositoryPort;
import ec.com.test.domain.port.MovimientoRepositoryPort;
import ec.com.test.domain.port.ParametroSistemaRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@ApplicationScoped
public class TransferenciaService implements TransferenciaUseCase {

    private final CuentaRepositoryPort cuentaRepository;
    private final MovimientoRepositoryPort movimientoRepository;
    private final ParametroSistemaRepositoryPort parametroRepository;
    private final MovimientoUseCase movimientoUseCase;

    public TransferenciaService(CuentaRepositoryPort cuentaRepository,
                                MovimientoRepositoryPort movimientoRepository,
                                ParametroSistemaRepositoryPort parametroRepository,
                                MovimientoUseCase movimientoUseCase) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.parametroRepository = parametroRepository;
        this.movimientoUseCase = movimientoUseCase;
    }

    @Override
    @Transactional
    public Movimiento ejecutarTransferencia(String numeroCuentaOrigen, String numeroCuentaDestino, BigDecimal monto, String observacion) {
        log.info("Iniciando solicitud de transferencia: Origen={}, Destino={}, Monto={}, Observacion={}", numeroCuentaOrigen, numeroCuentaDestino, monto, observacion);
        validarParametrosEntrada(numeroCuentaOrigen, numeroCuentaDestino, monto);
        ParametrosTransferencia parametros = obtenerParametrosTransferencia();

        boolean origenMenor = numeroCuentaOrigen.compareTo(numeroCuentaDestino) < 0;
        String primera = origenMenor ? numeroCuentaOrigen : numeroCuentaDestino;
        String segunda = origenMenor ? numeroCuentaDestino : numeroCuentaOrigen;
        String rolPrimera = origenMenor ? "origen" : "destino";
        String rolSegunda = origenMenor ? "destino" : "origen";

        Cuenta cuentaPrimera = buscarYBloquearCuenta(primera, rolPrimera);
        Cuenta cuentaSegunda = buscarYBloquearCuenta(segunda, rolSegunda);

        Cuenta cuentaOrigen = origenMenor ? cuentaPrimera : cuentaSegunda;
        Cuenta cuentaDestino = origenMenor ? cuentaSegunda : cuentaPrimera;
        validarCuentasActivas(cuentaOrigen, cuentaDestino);

        String motivoRechazoLimite = evaluarLimiteDiario(cuentaOrigen, monto, parametros.limiteDiario());
        if (motivoRechazoLimite != null) {
            movimientoUseCase.registrarMovimientoRechazado(cuentaOrigen, cuentaDestino, monto, observacion, motivoRechazoLimite);
            throw new BusinessException(motivoRechazoLimite, DomainConstants.HTTP_UNPROCESSABLE_ENTITY);
        }

        validarSaldo(cuentaOrigen, monto);

        String motivoSospecha = evaluarSospechaFraude(cuentaOrigen, numeroCuentaOrigen, monto, parametros);
        String estado = motivoSospecha != null ? DomainConstants.ESTADO_MOVIMIENTO_REQUIERE_REVISION : DomainConstants.ESTADO_MOVIMIENTO_COMPLETADA;
        String motivoFinal = motivoSospecha != null ? motivoSospecha : DomainConstants.MOTIVO_TRANSACCION_EXITOSA;

        return movimientoUseCase.registrarMovimientoExitoso(cuentaOrigen, cuentaDestino, monto, observacion, estado, motivoFinal);
    }

    private void validarParametrosEntrada(String origen, String destino, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Rechazo por monto inválido: {}", monto);
            throw new BusinessException("El monto de la transferencia debe ser mayor a cero", DomainConstants.HTTP_BAD_REQUEST);
        }
        if (origen != null && origen.equals(destino)) {
            log.warn("Rechazo por cuentas idénticas: Origen={}, Destino={}", origen, destino);
            throw new BusinessException("La cuenta de origen y destino deben ser diferentes", DomainConstants.HTTP_BAD_REQUEST);
        }
    }

    private ParametrosTransferencia obtenerParametrosTransferencia() {
        BigDecimal limiteDiario = parametroRepository.buscarPorCodigo(DomainConstants.PARAM_LIMITE_DIARIO_ACUMULADO, DomainConstants.CANAL_API, DomainConstants.TRANSACCION_TRF_INT)
                .map(ParametroSistema::getValor).orElse(DomainConstants.DEFAULT_LIMITE_DIARIO);
        BigDecimal maxMonto = parametroRepository.buscarPorCodigo(DomainConstants.PARAM_MAX_MONTO_POR_TRANSFERENCIA, DomainConstants.CANAL_API, DomainConstants.TRANSACCION_TRF_INT)
                .map(ParametroSistema::getValor).orElse(DomainConstants.DEFAULT_MAX_MONTO_TRANSFERENCIA);
        BigDecimal maxTransaccionesHora = parametroRepository.buscarPorCodigo(DomainConstants.PARAM_MAX_TRANSFERENCIAS_POR_HORA, DomainConstants.CANAL_API, DomainConstants.TRANSACCION_TRF_INT)
                .map(ParametroSistema::getValor).orElse(DomainConstants.DEFAULT_MAX_TRANSFERENCIAS_HORA);
        return new ParametrosTransferencia(limiteDiario, maxMonto, maxTransaccionesHora);
    }

    private Cuenta buscarYBloquearCuenta(String numeroCuenta, String tipo) {
        log.info("Adquiriendo bloqueo pesimista (PESSIMISTIC_WRITE) para cuenta de {}: {}", tipo, numeroCuenta);
        Cuenta cuenta = cuentaRepository.buscarPorNumeroCuentaConBloqueo(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("La cuenta de " + tipo + " no existe: " + numeroCuenta));
        log.info("Bloqueo pesimista adquirido exitosamente sobre cuenta de {} ID={} numeroCuenta={}", tipo, cuenta.getIdCuenta(), cuenta.getNumeroCuenta());
        return cuenta;
    }

    private void validarCuentasActivas(Cuenta cuentaOrigen, Cuenta cuentaDestino) {
        if (!cuentaOrigen.isActiva()) {
            log.warn("Cuenta de origen inactiva ID={} cuentaNumero={}", cuentaOrigen.getIdCuenta(), cuentaOrigen.getNumeroCuenta());
            throw new BusinessException("La cuenta de origen se encuentra inactiva", DomainConstants.HTTP_UNPROCESSABLE_ENTITY);
        }
        if (!cuentaDestino.isActiva()) {
            log.warn("Cuenta de destino inactiva ID={} cuentaNumero={}", cuentaDestino.getIdCuenta(), cuentaDestino.getNumeroCuenta());
            throw new BusinessException("La cuenta de destino se encuentra inactiva", DomainConstants.HTTP_UNPROCESSABLE_ENTITY);
        }
    }

    private String evaluarLimiteDiario(Cuenta cuentaOrigen, BigDecimal monto, BigDecimal limiteDiario) {
        LocalDateTime inicioDia = LocalDateTime.of(DomainConstants.todayEcuador(), LocalTime.MIN);
        LocalDateTime finDia = LocalDateTime.of(DomainConstants.todayEcuador(), LocalTime.MAX);
        BigDecimal acumuladoHoy = movimientoRepository.calcularTotalTransferidoHoy(cuentaOrigen.getIdCuenta(), inicioDia, finDia);
        BigDecimal nuevoAcumulado = acumuladoHoy.add(monto);
        log.debug("Validación límite diario: acumuladoHoy={}, nuevoAcumulado={}, limitePermitido={}", acumuladoHoy, nuevoAcumulado, limiteDiario);
        if (nuevoAcumulado.compareTo(limiteDiario) > 0) {
            BigDecimal disponible = limiteDiario.subtract(acumuladoHoy);
            if (disponible.compareTo(BigDecimal.ZERO) < 0) {
                disponible = BigDecimal.ZERO;
            }
            log.warn("Límite diario superado para cuenta ID={}, cuentaNumero={}. Disponible: {}", cuentaOrigen.getIdCuenta(), cuentaOrigen.getNumeroCuenta(), disponible);
            return "Excede el límite diario permitido de $" + limiteDiario + ". Monto restante disponible para hoy: $" + disponible;
        }
        return null;
    }

    private void validarSaldo(Cuenta cuentaOrigen, BigDecimal monto) {
        if (!cuentaOrigen.tieneSaldoSuficiente(monto)) {
            log.warn("Saldo insuficiente en cuenta ID={}: SaldoActual={}, MontoRequerido={}", cuentaOrigen.getIdCuenta(), cuentaOrigen.getSaldo(), monto);
            throw new BusinessException("La cuenta de origen no dispone de saldo suficiente para realizar la transferencia", DomainConstants.HTTP_UNPROCESSABLE_ENTITY);
        }
    }

    private String evaluarSospechaFraude(Cuenta cuentaOrigen, String numeroCuentaOrigen, BigDecimal monto, ParametrosTransferencia parametros) {
        LocalDateTime haceUnaHora = DomainConstants.nowEcuador().minusHours(1);
        long transaccionesUltimaHora = movimientoRepository.contarTransferenciasUltimaHora(cuentaOrigen.getIdCuenta(), haceUnaHora);
        log.debug("Verificación de reglas de fraude: monto={}, maxMonto={}, txHora={}, maxTxHora={}", monto, parametros.maxMonto(), transaccionesUltimaHora, parametros.maxTransaccionesHora());
        boolean superaMonto = monto.compareTo(parametros.maxMonto()) > 0;
        boolean superaFrecuencia = transaccionesUltimaHora >= parametros.maxTransaccionesHora().longValue();

        if (superaMonto && superaFrecuencia) {
            log.info("Operación marcada como SOSPECHOSA (Monto y Frecuencia) para cuenta Origen={}", numeroCuentaOrigen);
            return DomainConstants.MOTIVO_SOSPECHOSA_AMBOS;
        }
        if (superaMonto) {
            log.info("Operación marcada como SOSPECHOSA (Monto) para cuenta Origen={}", numeroCuentaOrigen);
            return DomainConstants.MOTIVO_SOSPECHOSA_MONTO;
        }
        if (superaFrecuencia) {
            log.info("Operación marcada como SOSPECHOSA (Frecuencia) para cuenta Origen={}", numeroCuentaOrigen);
            return DomainConstants.MOTIVO_SOSPECHOSA_FRECUENCIA;
        }
        return null;
    }

    @Override
    public List<Movimiento> consultarHistorial(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado, Integer page, Integer pageSize) {
        return movimientoUseCase.consultarHistorial(cuentaOrigen, cuentaDestino, fechaInicio, fechaFin, estado, page, pageSize);
    }

    @Override
    public long contarHistorial(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado) {
        return movimientoUseCase.contarHistorial(cuentaOrigen, cuentaDestino, fechaInicio, fechaFin, estado);
    }

    private record ParametrosTransferencia(BigDecimal limiteDiario, BigDecimal maxMonto,
                                           BigDecimal maxTransaccionesHora) {
    }
}
