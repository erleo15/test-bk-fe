package ec.com.test.domain.port;

import ec.com.test.domain.model.Cliente;

import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    Optional<Cliente> buscarPorIdentificacion(String identificacion);
}
