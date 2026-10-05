package ec.com.test.infrastructure.adapter.out.persistence.repository;

import ec.com.test.infrastructure.adapter.out.persistence.entity.ParametroSistemaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParametroSistemaPanacheRepository implements PanacheRepository<ParametroSistemaEntity> {
}
