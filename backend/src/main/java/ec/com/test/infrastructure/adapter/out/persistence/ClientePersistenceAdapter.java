package ec.com.test.infrastructure.adapter.out.persistence;

import ec.com.test.domain.model.Cliente;
import ec.com.test.domain.port.ClienteRepositoryPort;
import ec.com.test.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import ec.com.test.infrastructure.adapter.out.persistence.repository.ClientePanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClientePanacheRepository repository;

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = toEntity(cliente);
        repository.persist(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findByIdOptional(id).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorIdentificacion(String identificacion) {
        return repository.find("identificacion", identificacion).firstResultOptional().map(this::toDomain);
    }

    private Cliente toDomain(ClienteEntity entity) {
        return new Cliente(
                entity.getIdCliente(),
                entity.getIdentificacion(),
                entity.getPrimerNombre(),
                entity.getSegundoNombre(),
                entity.getApellidoPaterno(),
                entity.getApellidoMaterno(),
                entity.getNombreLegal()
        );
    }

    private ClienteEntity toEntity(Cliente domain) {
        ClienteEntity entity = new ClienteEntity();
        entity.setIdCliente(domain.getIdCliente());
        entity.setIdentificacion(domain.getIdentificacion());
        entity.setPrimerNombre(domain.getPrimerNombre());
        entity.setSegundoNombre(domain.getSegundoNombre());
        entity.setApellidoPaterno(domain.getApellidoPaterno());
        entity.setApellidoMaterno(domain.getApellidoMaterno());
        entity.setNombreLegal(domain.getNombreLegal());
        return entity;
    }
}
