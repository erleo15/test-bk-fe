package ec.com.test.application.service;

import ec.com.test.application.port.in.MovimientoUseCase;
import ec.com.test.application.port.in.TransferenciaUseCase;
import ec.com.test.domain.constant.DomainConstants;
import ec.com.test.domain.exception.BusinessException;
import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.model.Movimiento;
import ec.com.test.domain.port.CuentaRepositoryPort;
import ec.com.test.domain.port.MovimientoRepositoryPort;
import ec.com.test.domain.port.ParametroSistemaRepositoryPort;
import ec.com.test.domain.service.MovimientoDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransferenciaServiceTest {

    private CuentaRepositoryPort cuentaRepository;
    private MovimientoRepositoryPort movimientoRepository;
    private ParametroSistemaRepositoryPort parametroRepository;
    private MovimientoUseCase movimientoUseCase;
    private TransferenciaUseCase transferenciaUseCase;

    @BeforeEach
    void setUp() {
        cuentaRepository = mock(CuentaRepositoryPort.class);
        movimientoRepository = mock(MovimientoRepositoryPort.class);
        parametroRepository = mock(ParametroSistemaRepositoryPort.class);
        movimientoUseCase = new MovimientoDomainService(movimientoRepository, cuentaRepository);

        transferenciaUseCase = new TransferenciaService(cuentaRepository, movimientoRepository, parametroRepository, movimientoUseCase);
    }

    @Test
    void transferenciaExitosa() {
        Cuenta origen = new Cuenta(1L, 100L, "1723456789", "Juan", "Carlos", "Pérez", "Gómez", "Juan Carlos Pérez Gómez", "1234567890", "AHORRO", new BigDecimal("1000.00"), "ACTIVA");
        Cuenta destino = new Cuenta(2L, 101L, "0987654321", "María", "Fernanda", "López", "Martínez", "María Fernanda López Martínez", "0987654321", "CORRIENTE", new BigDecimal("500.00"), "ACTIVA");

        when(cuentaRepository.buscarPorNumeroCuentaConBloqueo("1234567890")).thenReturn(Optional.of(origen));
        when(cuentaRepository.buscarPorNumeroCuentaConBloqueo("0987654321")).thenReturn(Optional.of(destino));
        when(movimientoRepository.calcularTotalTransferidoHoy(anyLong(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(movimientoRepository.contarTransferenciasUltimaHora(anyLong(), any())).thenReturn(0L);
        when(movimientoRepository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Movimiento resultado = transferenciaUseCase.ejecutarTransferencia("1234567890", "0987654321", new BigDecimal("200.00"), "Pago de servicios");

        assertNotNull(resultado);
        assertEquals("COMPLETADA", resultado.getEstado());
        assertEquals("1234567890", resultado.getNumeroCuentaOrigen());
        assertEquals("0987654321", resultado.getNumeroCuentaDestino());
        assertEquals(new BigDecimal("800.00"), origen.getSaldo());
        assertEquals(new BigDecimal("700.00"), destino.getSaldo());
        verify(cuentaRepository).actualizar(origen);
        verify(cuentaRepository).actualizar(destino);
    }

    @Test
    void transferenciaMarcaSospechosaPorMonto() {
        Cuenta origen = new Cuenta(1L, 100L, "1723456789", "Juan", "Carlos", "Pérez", "Gómez", "Juan Carlos Pérez Gómez", "1234567890", "AHORRO", new BigDecimal("10000.00"), "ACTIVA");
        Cuenta destino = new Cuenta(2L, 101L, "0987654321", "María", "Fernanda", "López", "Martínez", "María Fernanda López Martínez", "0987654321", "CORRIENTE", new BigDecimal("500.00"), "ACTIVA");

        when(cuentaRepository.buscarPorNumeroCuentaConBloqueo("1234567890")).thenReturn(Optional.of(origen));
        when(cuentaRepository.buscarPorNumeroCuentaConBloqueo("0987654321")).thenReturn(Optional.of(destino));
        when(movimientoRepository.calcularTotalTransferidoHoy(anyLong(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(movimientoRepository.contarTransferenciasUltimaHora(anyLong(), any())).thenReturn(0L);
        when(movimientoRepository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Movimiento resultado = transferenciaUseCase.ejecutarTransferencia("1234567890", "0987654321", new BigDecimal("6000.00"), "Compra de equipo");

        assertNotNull(resultado);
        assertEquals("REQUIERE_REVISION", resultado.getEstado());
        assertEquals(DomainConstants.MOTIVO_SOSPECHOSA_MONTO, resultado.getMotivoEstado());
    }

    @Test
    void transferenciaRechazadaPorLimiteDiario() {
        Cuenta origen = new Cuenta(1L, 100L, "1723456789", "Juan", "Carlos", "Pérez", "Gómez", "Juan Carlos Pérez Gómez", "1234567890", "AHORRO", new BigDecimal("15000.00"), "ACTIVA");
        Cuenta destino = new Cuenta(2L, 101L, "0987654321", "María", "Fernanda", "López", "Martínez", "María Fernanda López Martínez", "0987654321", "CORRIENTE", new BigDecimal("500.00"), "ACTIVA");

        when(cuentaRepository.buscarPorNumeroCuentaConBloqueo("1234567890")).thenReturn(Optional.of(origen));
        when(cuentaRepository.buscarPorNumeroCuentaConBloqueo("0987654321")).thenReturn(Optional.of(destino));
        when(movimientoRepository.calcularTotalTransferidoHoy(anyLong(), any(), any())).thenReturn(new BigDecimal("9000.00"));
        when(movimientoRepository.guardarEnNuevaTransaccion(any())).thenAnswer(inv -> inv.getArgument(0));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                transferenciaUseCase.ejecutarTransferencia("1234567890", "0987654321", new BigDecimal("2000.00"), "Pago de arriendo"));

        assertTrue(ex.getMessage().contains("Excede el límite diario permitido"));
        assertEquals(DomainConstants.HTTP_UNPROCESSABLE_ENTITY, ex.getStatusCode());
        assertEquals(new BigDecimal("15000.00"), origen.getSaldo());
        assertEquals(new BigDecimal("500.00"), destino.getSaldo());
        verify(cuentaRepository, never()).actualizar(any());
        verify(movimientoRepository).guardarEnNuevaTransaccion(argThat(m ->
                "RECHAZADA".equals(m.getEstado()) &&
                        m.getMotivoEstado().contains("Excede el límite diario permitido")
        ));
    }

    @Test
    void transferenciaRechazadaPorCuentaInactiva() {
        Cuenta origenInactiva = new Cuenta(1L, 100L, "1723456789", "Juan", "Carlos", "Pérez", "Gómez", "Juan Carlos Pérez Gómez", "1234567890", "AHORRO", new BigDecimal("1000.00"), "INACTIVA");
        Cuenta destino = new Cuenta(2L, 101L, "0987654321", "María", "Fernanda", "López", "Martínez", "María Fernanda López Martínez", "0987654321", "CORRIENTE", new BigDecimal("500.00"), "ACTIVA");

        when(cuentaRepository.buscarPorNumeroCuentaConBloqueo("1234567890")).thenReturn(Optional.of(origenInactiva));
        when(cuentaRepository.buscarPorNumeroCuentaConBloqueo("0987654321")).thenReturn(Optional.of(destino));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                transferenciaUseCase.ejecutarTransferencia("1234567890", "0987654321", new BigDecimal("200.00"), "Prueba inactiva"));

        assertEquals("La cuenta de origen se encuentra inactiva", ex.getMessage());
    }
}
