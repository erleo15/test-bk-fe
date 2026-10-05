package ec.com.test.infrastructure.config;

import ec.com.test.application.port.in.MovimientoUseCase;
import ec.com.test.domain.port.CuentaRepositoryPort;
import ec.com.test.domain.port.MovimientoRepositoryPort;
import ec.com.test.domain.service.MovimientoDomainService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
@RegisterForReflection(targets = {
        ec.com.test.domain.model.Cliente.class,
        ec.com.test.domain.model.Cuenta.class,
        ec.com.test.domain.model.Movimiento.class,
        ec.com.test.domain.model.ParametroSistema.class,
        ec.com.test.domain.model.ResumenCuenta.class
})
public class ApplicationDomainConfig {

    @Produces
    @ApplicationScoped
    public MovimientoUseCase movimientoUseCase(MovimientoRepositoryPort movimientoRepositoryPort,
                                               CuentaRepositoryPort cuentaRepositoryPort) {
        return new MovimientoDomainService(movimientoRepositoryPort, cuentaRepositoryPort);
    }
}
