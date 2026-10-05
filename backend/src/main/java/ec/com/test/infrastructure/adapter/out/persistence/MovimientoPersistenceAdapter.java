package ec.com.test.infrastructure.adapter.out.persistence;

import ec.com.test.domain.model.Movimiento;
import ec.com.test.domain.port.MovimientoRepositoryPort;
import ec.com.test.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import ec.com.test.infrastructure.adapter.out.persistence.entity.MovimientoEntity;
import ec.com.test.infrastructure.adapter.out.persistence.repository.CuentaRepository;
import ec.com.test.infrastructure.adapter.out.persistence.repository.MovimientoPanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class MovimientoPersistenceAdapter implements MovimientoRepositoryPort {

    private final MovimientoPanacheRepository repository;
    private final CuentaRepository cuentaRepository;

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        MovimientoEntity entity = toEntity(movimiento);
        repository.persist(entity);
        return toDomain(entity);
    }

    @Override
    public Movimiento guardarEnNuevaTransaccion(Movimiento movimiento) {
        MovimientoEntity entity = toEntity(movimiento);
        io.quarkus.narayana.jta.QuarkusTransaction.requiringNew().run(() -> repository.persist(entity));
        return toDomain(entity);
    }

    @Override
    public List<Movimiento> buscarConFiltros(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado, int page, int pageSize) {
        Long cuentaOrigenId = resolverCuentaId(cuentaOrigen);
        Long cuentaDestinoId = resolverCuentaId(cuentaDestino);

        if (cuentaOrigen != null && !cuentaOrigen.isBlank() && cuentaOrigenId == null) {
            return List.of();
        }
        if (cuentaDestino != null && !cuentaDestino.isBlank() && cuentaDestinoId == null) {
            return List.of();
        }

        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (cuentaOrigenId != null) {
            query.append(" AND cuentaOrigenId = :cuentaOrigenId");
            params.put("cuentaOrigenId", cuentaOrigenId);
        }
        if (cuentaDestinoId != null) {
            query.append(" AND cuentaDestinoId = :cuentaDestinoId");
            params.put("cuentaDestinoId", cuentaDestinoId);
        }
        if (fechaInicio != null) {
            query.append(" AND fechaCreacion >= :fechaInicio");
            params.put("fechaInicio", fechaInicio);
        }
        if (fechaFin != null) {
            query.append(" AND fechaCreacion <= :fechaFin");
            params.put("fechaFin", fechaFin);
        }
        if (estado != null && !estado.isBlank()) {
            query.append(" AND estado = :estado");
            params.put("estado", estado);
        }

        if (page == 0 && pageSize == 0) {
            return repository.find(query.toString(), Sort.descending("fechaCreacion"), params)
                    .list()
                    .stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        }

        return repository.find(query.toString(), Sort.descending("fechaCreacion"), params)
                .page(Page.of(page, pageSize))
                .list()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long contarConFiltros(String cuentaOrigen, String cuentaDestino, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado) {
        Long cuentaOrigenId = resolverCuentaId(cuentaOrigen);
        Long cuentaDestinoId = resolverCuentaId(cuentaDestino);

        if (cuentaOrigen != null && !cuentaOrigen.isBlank() && cuentaOrigenId == null) {
            return 0;
        }
        if (cuentaDestino != null && !cuentaDestino.isBlank() && cuentaDestinoId == null) {
            return 0;
        }

        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (cuentaOrigenId != null) {
            query.append(" AND cuentaOrigenId = :cuentaOrigenId");
            params.put("cuentaOrigenId", cuentaOrigenId);
        }
        if (cuentaDestinoId != null) {
            query.append(" AND cuentaDestinoId = :cuentaDestinoId");
            params.put("cuentaDestinoId", cuentaDestinoId);
        }
        if (fechaInicio != null) {
            query.append(" AND fechaCreacion >= :fechaInicio");
            params.put("fechaInicio", fechaInicio);
        }
        if (fechaFin != null) {
            query.append(" AND fechaCreacion <= :fechaFin");
            params.put("fechaFin", fechaFin);
        }
        if (estado != null && !estado.isBlank()) {
            query.append(" AND estado = :estado");
            params.put("estado", estado);
        }

        return repository.find(query.toString(), params).count();
    }

    private Long resolverCuentaId(String cuentaIdentificador) {
        if (cuentaIdentificador == null || cuentaIdentificador.isBlank()) {
            return null;
        }
        CuentaEntity encontradaPorNumero = cuentaRepository.find("numeroCuenta", cuentaIdentificador).firstResult();
        if (encontradaPorNumero != null) {
            return encontradaPorNumero.getIdCuenta();
        }
        try {
            Long numericId = Long.parseLong(cuentaIdentificador);
            CuentaEntity encontradaPorId = cuentaRepository.findById(numericId);
            return encontradaPorId != null ? encontradaPorId.getIdCuenta() : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    public BigDecimal calcularTotalTransferidoHoy(Long cuentaOrigenId, LocalDateTime inicioDia, LocalDateTime finDia) {
        BigDecimal total = repository.getEntityManager()
                .createQuery("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoEntity m WHERE m.cuentaOrigenId = :cuentaOrigenId AND m.estado = 'COMPLETADA' AND m.fechaCreacion BETWEEN :inicioDia AND :finDia", BigDecimal.class)
                .setParameter("cuentaOrigenId", cuentaOrigenId)
                .setParameter("inicioDia", inicioDia)
                .setParameter("finDia", finDia)
                .getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public long contarTransferenciasUltimaHora(Long cuentaOrigenId, LocalDateTime haceUnaHora) {
        Map<String, Object> params = new HashMap<>();
        params.put("cuentaOrigenId", cuentaOrigenId);
        params.put("haceUnaHora", haceUnaHora);
        return repository.count("cuentaOrigenId = :cuentaOrigenId AND fechaCreacion >= :haceUnaHora", params);
    }

    @Override
    public long contarTransferenciasDia(Long cuentaOrigenId, LocalDateTime inicioDia, LocalDateTime finDia) {
        Map<String, Object> params = new HashMap<>();
        params.put("cuentaOrigenId", cuentaOrigenId);
        params.put("inicioDia", inicioDia);
        params.put("finDia", finDia);
        return repository.count("cuentaOrigenId = :cuentaOrigenId AND fechaCreacion BETWEEN :inicioDia AND :finDia", params);
    }

    @Override
    public long contarSospechosasDia(Long cuentaOrigenId, LocalDateTime inicioDia, LocalDateTime finDia) {
        Map<String, Object> params = new HashMap<>();
        params.put("cuentaOrigenId", cuentaOrigenId);
        params.put("inicioDia", inicioDia);
        params.put("finDia", finDia);
        return repository.count("cuentaOrigenId = :cuentaOrigenId AND estado = 'REQUIERE_REVISION' AND fechaCreacion BETWEEN :inicioDia AND :finDia", params);
    }

    private Movimiento toDomain(MovimientoEntity entity) {
        Movimiento movimiento = new Movimiento();
        movimiento.setIdMovimiento(entity.getIdMovimiento());
        movimiento.setNumeroMovimiento(entity.getNumeroMovimiento());
        CuentaEntity origen = cuentaRepository.findById(entity.getCuentaOrigenId());
        if (origen != null) {
            movimiento.setNumeroCuentaOrigen(origen.getNumeroCuenta());
        }
        CuentaEntity destino = cuentaRepository.findById(entity.getCuentaDestinoId());
        if (destino != null) {
            movimiento.setNumeroCuentaDestino(destino.getNumeroCuenta());
        }
        movimiento.setMonto(entity.getMonto());
        movimiento.setObservacion(entity.getObservacion());
        movimiento.setEstado(entity.getEstado());
        movimiento.setFechaCreacion(entity.getFechaCreacion());
        movimiento.setMotivoEstado(entity.getMotivoEstado());
        return movimiento;
    }

    private MovimientoEntity toEntity(Movimiento domain) {
        MovimientoEntity entity = new MovimientoEntity();
        entity.setIdMovimiento(domain.getIdMovimiento());
        String numeroMovimiento = "TRF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        entity.setNumeroMovimiento(numeroMovimiento);
        if (domain.getNumeroCuentaOrigen() != null) {
            CuentaEntity origen = cuentaRepository.find("numeroCuenta", domain.getNumeroCuentaOrigen()).firstResult();
            if (origen != null) {
                entity.setCuentaOrigenId(origen.getIdCuenta());
            }
        }
        if (domain.getNumeroCuentaDestino() != null) {
            CuentaEntity destino = cuentaRepository.find("numeroCuenta", domain.getNumeroCuentaDestino()).firstResult();
            if (destino != null) {
                entity.setCuentaDestinoId(destino.getIdCuenta());
            }
        }
        entity.setMonto(domain.getMonto());
        entity.setObservacion(domain.getObservacion());
        entity.setEstado(domain.getEstado());
        entity.setFechaCreacion(domain.getFechaCreacion());
        entity.setMotivoEstado(domain.getMotivoEstado());
        return entity;
    }
}
