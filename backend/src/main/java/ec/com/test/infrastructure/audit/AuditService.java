package ec.com.test.infrastructure.audit;

import ec.com.test.infrastructure.audit.entity.LogAuditoriaEntity;
import ec.com.test.infrastructure.audit.repository.LogAuditoriaPanacheRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AuditService {

    private final LogAuditoriaPanacheRepository logAuditoriaRepository;

    public void saveAuditLog(LogAuditoriaEntity logInstance) {
        try {
            QuarkusTransaction.requiringNew().run(() -> logAuditoriaRepository.persist(logInstance));
            log.info("Log de auditoría guardado exitosamente. ProcessId: {} | Operación: {} | HTTP: {} | Tiempo: {} ms",
                    logInstance.getProcessId(), logInstance.getOperacion(), logInstance.getCodigoHttp(), logInstance.getTiempoEjecucionMs());
        } catch (Exception e) {
            log.error("Error guardando el registro de auditoría: {}", e.getMessage(), e);
        }
    }
}
