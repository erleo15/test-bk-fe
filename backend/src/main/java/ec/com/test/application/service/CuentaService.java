package ec.com.test.application.service;

import ec.com.test.application.port.in.CuentaUseCase;
import ec.com.test.domain.constant.DomainConstants;
import ec.com.test.domain.exception.BusinessException;
import ec.com.test.domain.exception.ResourceNotFoundException;
import ec.com.test.domain.model.Cliente;
import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.model.ResumenCuenta;
import ec.com.test.domain.port.ClienteRepositoryPort;
import ec.com.test.domain.port.CuentaRepositoryPort;
import ec.com.test.domain.port.MovimientoRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class CuentaService implements CuentaUseCase {

    private final CuentaRepositoryPort cuentaRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final MovimientoRepositoryPort movimientoRepository;

    public CuentaService(CuentaRepositoryPort cuentaRepository, ClienteRepositoryPort clienteRepository,
                         MovimientoRepositoryPort movimientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    @Transactional
    public Cuenta crearCuenta(Cuenta cuenta, Cliente clienteInfo) {
        log.info("Iniciando creación de cuenta bancaria para identificación: {}", clienteInfo.getIdentificacion());
        validarDatosCuenta(cuenta);
        inicializarNombreLegal(clienteInfo);
        Cliente cliente = resolverCliente(clienteInfo);

        cuenta.setClienteId(cliente.getIdCliente());
        if (cuenta.getEstado() == null) {
            cuenta.setEstado(DomainConstants.ESTADO_CUENTA_ACTIVA);
        }

        Cuenta creada = cuentaRepository.guardar(cuenta);
        log.info("Cuenta creada exitosamente con ID: {} para cliente ID: {}", creada.getIdCuenta(),
                cliente.getIdCliente());
        return creada;
    }

    private void validarDatosCuenta(Cuenta cuenta) {
        if (cuenta.getNumeroCuenta() == null
                || !cuenta.getNumeroCuenta().matches(DomainConstants.NUMERO_CUENTA_REGEX)) {
            log.warn("Número de cuenta inválido: {}", cuenta.getNumeroCuenta());
            throw new BusinessException("El número de cuenta debe tener exactamente 10 dígitos numéricos",
                    DomainConstants.HTTP_BAD_REQUEST);
        }
        if (cuenta.getSaldo() == null || cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Saldo inicial no válido: {}", cuenta.getSaldo());
            throw new BusinessException("El saldo inicial no puede ser negativo", DomainConstants.HTTP_BAD_REQUEST);
        }
        if (cuentaRepository.buscarPorNumeroCuenta(cuenta.getNumeroCuenta()).isPresent()) {
            log.warn("Cuenta duplicada intentada: {}", cuenta.getNumeroCuenta());
            throw new BusinessException("Ya existe una cuenta registrada con el número: " + cuenta.getNumeroCuenta(),
                    DomainConstants.HTTP_BAD_REQUEST);
        }
    }

    private void inicializarNombreLegal(Cliente clienteInfo) {
        if (clienteInfo.getNombreLegal() != null && !clienteInfo.getNombreLegal().isBlank()) {
            return;
        }
        StringBuilder nombreBuilder = new StringBuilder();
        if (clienteInfo.getPrimerNombre() != null)
            nombreBuilder.append(clienteInfo.getPrimerNombre()).append(" ");
        if (clienteInfo.getSegundoNombre() != null)
            nombreBuilder.append(clienteInfo.getSegundoNombre()).append(" ");
        if (clienteInfo.getApellidoPaterno() != null)
            nombreBuilder.append(clienteInfo.getApellidoPaterno()).append(" ");
        if (clienteInfo.getApellidoMaterno() != null)
            nombreBuilder.append(clienteInfo.getApellidoMaterno()).append(" ");
        String construido = nombreBuilder.toString().trim();
        clienteInfo.setNombreLegal(!construido.isEmpty() ? construido : DomainConstants.NOMBRE_LEGAL_DEFAULT);
        log.debug("Nombre legal autogenerado: {}", clienteInfo.getNombreLegal());
    }

    private Cliente resolverCliente(Cliente clienteInfo) {
        log.debug("Buscando o registrando cliente con cédula: {}", clienteInfo.getIdentificacion());
        return clienteRepository.buscarPorIdentificacion(clienteInfo.getIdentificacion())
                .orElseGet(() -> clienteRepository.guardar(clienteInfo));
    }

    @Override
    public List<Cuenta> listarCuentas(Integer page, Integer pageSize, String sortBySaldo) {
        int targetPage = (page != null && page >= 0) ? page : 0;
        int targetPageSize = (pageSize != null && pageSize > 0) ? pageSize : 0;
        return cuentaRepository.listarTodas(targetPage, targetPageSize, sortBySaldo);
    }

    @Override
    public long contarCuentas() {
        return cuentaRepository.contarTodas();
    }

    @Override
    public ResumenCuenta obtenerResumenCuenta(String identificadorCuenta) {
        Cuenta cuenta = resolverCuenta(identificadorCuenta);
        Long idCuenta = cuenta.getIdCuenta();

        LocalDateTime inicioDia = LocalDateTime.of(DomainConstants.todayEcuador(), LocalTime.MIN);
        LocalDateTime finDia = LocalDateTime.of(DomainConstants.todayEcuador(), LocalTime.MAX);

        BigDecimal totalTransferidoHoy = movimientoRepository.calcularTotalTransferidoHoy(idCuenta, inicioDia, finDia);
        long cantidadTransferencias = movimientoRepository.contarTransferenciasDia(idCuenta, inicioDia, finDia);
        long cantidadSospechosas = movimientoRepository.contarSospechosasDia(idCuenta, inicioDia, finDia);

        return new ResumenCuenta(cuenta.getSaldo(), totalTransferidoHoy, cantidadTransferencias, cantidadSospechosas);
    }

    private Cuenta resolverCuenta(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            throw new ResourceNotFoundException("El identificador de la cuenta no puede estar vacío");
        }
        return cuentaRepository.buscarPorNumeroCuenta(identificador)
                .or(() -> {
                    try {
                        Long numericId = Long.parseLong(identificador);
                        return cuentaRepository.buscarPorId(numericId);
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la cuenta: " + identificador));
    }
}
