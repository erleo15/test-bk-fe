package ec.com.test.application.service;

import ec.com.test.application.port.in.CuentaUseCase;
import ec.com.test.domain.exception.BusinessException;
import ec.com.test.domain.model.Cliente;
import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.port.ClienteRepositoryPort;
import ec.com.test.domain.port.CuentaRepositoryPort;
import ec.com.test.domain.port.MovimientoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CuentaServiceTest {

    private CuentaRepositoryPort cuentaRepository;
    private ClienteRepositoryPort clienteRepository;
    private MovimientoRepositoryPort movimientoRepository;
    private CuentaUseCase cuentaUseCase;

    @BeforeEach
    void setUp() {
        cuentaRepository = mock(CuentaRepositoryPort.class);
        clienteRepository = mock(ClienteRepositoryPort.class);
        movimientoRepository = mock(MovimientoRepositoryPort.class);

        cuentaUseCase = new CuentaService(cuentaRepository, clienteRepository, movimientoRepository);
    }

    @Test
    void crearCuentaExitosa() {
        Cuenta cuentaInput = new Cuenta(null, null, null, null, null, null, null, null, "1234567890", "AHORRO", new BigDecimal("1000.00"), "ACTIVA");
        Cliente clienteInput = new Cliente(null, "1723456789", "Juan", "Carlos", "Pérez", "Gómez", "Juan Carlos Pérez Gómez");

        when(cuentaRepository.buscarPorNumeroCuenta("1234567890")).thenReturn(Optional.empty());
        when(clienteRepository.buscarPorIdentificacion("1723456789")).thenReturn(Optional.empty());
        when(clienteRepository.guardar(any())).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setIdCliente(1L);
            return c;
        });
        when(cuentaRepository.guardar(any())).thenAnswer(inv -> {
            Cuenta cu = inv.getArgument(0);
            cu.setIdCuenta(10L);
            return cu;
        });

        Cuenta creada = cuentaUseCase.crearCuenta(cuentaInput, clienteInput);

        assertNotNull(creada);
        assertEquals(10L, creada.getIdCuenta());
        assertEquals("1234567890", creada.getNumeroCuenta());
        verify(cuentaRepository, times(1)).guardar(any());
    }

    @Test
    void crearCuentaLanzaExcepcionPorNumeroCuentaDuplicado() {
        Cuenta cuentaInput = new Cuenta(null, null, null, null, null, null, null, null, "1234567890", "AHORRO", new BigDecimal("1000.00"), "ACTIVA");
        Cliente clienteInput = new Cliente(null, "1723456789", "Juan", "Carlos", "Pérez", "Gómez", "Juan Carlos Pérez Gómez");

        Cuenta cuentaExistente = new Cuenta(1L, 1L, "1723456789", "Juan", "Carlos", "Pérez", "Gómez", "Juan Carlos Pérez Gómez", "1234567890", "AHORRO", new BigDecimal("500.00"), "ACTIVA");
        when(cuentaRepository.buscarPorNumeroCuenta("1234567890")).thenReturn(Optional.of(cuentaExistente));

        BusinessException ex = assertThrows(BusinessException.class, () -> cuentaUseCase.crearCuenta(cuentaInput, clienteInput));
        assertTrue(ex.getMessage().contains("Ya existe una cuenta registrada"));
    }
}
