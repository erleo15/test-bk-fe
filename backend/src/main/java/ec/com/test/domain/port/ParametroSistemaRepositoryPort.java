package ec.com.test.domain.port;

import ec.com.test.domain.model.ParametroSistema;

import java.util.Optional;

public interface ParametroSistemaRepositoryPort {
    Optional<ParametroSistema> buscarPorCodigo(String codigoParametro, String canal, String codigoTransaccion);
}
