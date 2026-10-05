package ec.com.test.infrastructure.audit.repository;

import ec.com.test.infrastructure.audit.entity.LogAuditoriaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogAuditoriaPanacheRepository implements PanacheRepository<LogAuditoriaEntity> {
}
