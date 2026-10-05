package ec.com.test.domain.port;

import ec.com.test.domain.model.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaRepositoryPort {
    Cuenta guardar(Cuenta cuenta);

    Optional<Cuenta> buscarPorId(Long id);

    Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta);

    Optional<Cuenta> buscarPorNumeroCuentaConBloqueo(String numeroCuenta);

    List<Cuenta> listarTodas(int page, int pageSize, String sortBySaldo);

    long contarTodas();

    void actualizar(Cuenta cuenta);
}
