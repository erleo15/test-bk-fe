package ec.com.test.infrastructure.adapter.out.persistence;

import ec.com.test.domain.model.ParametroSistema;
import ec.com.test.domain.port.ParametroSistemaRepositoryPort;
import ec.com.test.infrastructure.adapter.out.persistence.entity.ParametroSistemaEntity;
import ec.com.test.infrastructure.adapter.out.persistence.repository.ParametroSistemaPanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ParametroSistemaPersistenceAdapter implements ParametroSistemaRepositoryPort {

    private final ParametroSistemaPanacheRepository repository;

    @Override
    public Optional<ParametroSistema> buscarPorCodigo(String codigoParametro, String canal, String codigoTransaccion) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("codigo", codigoParametro);
        parameters.put("canal", canal);
        parameters.put("tx", codigoTransaccion);
        Optional<ParametroSistemaEntity> entity = repository.find(
                "codigoParametro = :codigo AND canal = :canal AND codigoTransaccion = :tx AND estado = true",
                parameters
        ).firstResultOptional();

        if (entity.isPresent()) {
            return entity.map(this::toDomain);
        }

        Map<String, Object> fallbackParams = new HashMap<>();
        fallbackParams.put("codigo", codigoParametro);
        fallbackParams.put("tx", codigoTransaccion);

        return repository.find(
                "codigoParametro = :codigo AND canal = '*' AND codigoTransaccion = :tx AND estado = true",
                fallbackParams
        ).firstResultOptional().map(this::toDomain);
    }

    private ParametroSistema toDomain(ParametroSistemaEntity entity) {
        return new ParametroSistema(
                entity.getIdParametro(),
                entity.getCodigoParametro(),
                entity.getCanal(),
                entity.getCodigoTransaccion(),
                entity.getValor(),
                entity.getEstado(),
                entity.getFechaCreacion()
        );
    }
}
