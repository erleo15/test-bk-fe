package ec.com.test.application.port.in;

import ec.com.test.domain.model.Cliente;
import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.model.ResumenCuenta;

import java.util.List;

public interface CuentaUseCase {
    Cuenta crearCuenta(Cuenta cuenta, Cliente cliente);

    List<Cuenta> listarCuentas(Integer page, Integer pageSize, String sortBySaldo);

    long contarCuentas();

    ResumenCuenta obtenerResumenCuenta(String identificadorCuenta);
}
