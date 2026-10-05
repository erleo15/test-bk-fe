package ec.com.test.infrastructure.adapter.out.persistence;

import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.port.CuentaRepositoryPort;
import ec.com.test.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import ec.com.test.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import ec.com.test.infrastructure.adapter.out.persistence.repository.ClientePanacheRepository;
import ec.com.test.infrastructure.adapter.out.persistence.repository.CuentaRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CuentaPersistenceAdapter implements CuentaRepositoryPort {

    private final CuentaRepository repository;
    private final ClientePanacheRepository clienteRepository;

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        CuentaEntity entity = toEntity(cuenta);
        repository.persist(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Cuenta> buscarPorId(Long id) {
        return repository.findByIdOptional(id).map(this::toDomain);
    }

    @Override
    public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
        return repository.find("numeroCuenta", numeroCuenta).firstResultOptional().map(this::toDomain);
    }

    @Override
    public Optional<Cuenta> buscarPorNumeroCuentaConBloqueo(String numeroCuenta) {
        return repository.find("numeroCuenta", numeroCuenta)
                .withLock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
                .firstResultOptional()
                .map(this::toDomain);
    }

    @Override
    public List<Cuenta> listarTodas(int page, int pageSize, String sortBySaldo) {
        Sort sort = Sort.ascending("idCuenta");
        if ("desc".equalsIgnoreCase(sortBySaldo)) {
            sort = Sort.descending("saldo");
        } else if ("asc".equalsIgnoreCase(sortBySaldo)) {
            sort = Sort.ascending("saldo");
        }

        if (page == 0 && pageSize == 0) {
            return repository.findAll(sort)
                    .list()
                    .stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        }

        return repository.findAll(sort)
                .page(Page.of(page, pageSize))
                .list()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long contarTodas() {
        return repository.count();
    }

    @Override
    public void actualizar(Cuenta cuenta) {
        CuentaEntity entity = repository.findById(cuenta.getIdCuenta());
        if (entity != null) {
            entity.setSaldo(cuenta.getSaldo());
            entity.setEstado(cuenta.getEstado());
            entity.setTipoCuenta(cuenta.getTipoCuenta());
            repository.persist(entity);
        }
    }

    private Cuenta toDomain(CuentaEntity entity) {
        String identificacion = null;
        String primerNombre = null;
        String segundoNombre = null;
        String apellidoPaterno = null;
        String apellidoMaterno = null;
        String nombreLegal = null;

        if (entity.getClienteId() != null) {
            ClienteEntity cliente = clienteRepository.findById(entity.getClienteId());
            if (cliente != null) {
                identificacion = cliente.getIdentificacion();
                primerNombre = cliente.getPrimerNombre();
                segundoNombre = cliente.getSegundoNombre();
                apellidoPaterno = cliente.getApellidoPaterno();
                apellidoMaterno = cliente.getApellidoMaterno();
                nombreLegal = cliente.getNombreLegal();
            }
        }

        return new Cuenta(
                entity.getIdCuenta(),
                entity.getClienteId(),
                identificacion,
                primerNombre,
                segundoNombre,
                apellidoPaterno,
                apellidoMaterno,
                nombreLegal,
                entity.getNumeroCuenta(),
                entity.getTipoCuenta(),
                entity.getSaldo(),
                entity.getEstado()
        );
    }

    private CuentaEntity toEntity(Cuenta domain) {
        CuentaEntity entity = new CuentaEntity();
        entity.setIdCuenta(domain.getIdCuenta());
        entity.setClienteId(domain.getClienteId());
        entity.setNumeroCuenta(domain.getNumeroCuenta());
        entity.setTipoCuenta(domain.getTipoCuenta());
        entity.setSaldo(domain.getSaldo());
        entity.setEstado(domain.getEstado());
        return entity;
    }
}
