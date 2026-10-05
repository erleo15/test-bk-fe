package ec.com.test.infrastructure.audit.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@RegisterForReflection
@Entity
@Table(name = "LOG_AUDITORIA")
public class LogAuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long idLog;

    @Column(name = "process_id", nullable = false, length = 50)
    private String processId;

    @Column(name = "operacion", nullable = false, length = 100)
    private String operacion;

    @Column(name = "payload_entrada", columnDefinition = "TEXT")
    private String payloadEntrada;

    @Column(name = "payload_salida", columnDefinition = "TEXT")
    private String payloadSalida;

    @Column(name = "codigo_http", nullable = false)
    private Integer codigoHttp;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;

    @Column(name = "tiempo_ejecucion_ms", nullable = false)
    private Integer tiempoEjecucionMs;

    @Column(name = "headers", columnDefinition = "TEXT")
    private String headers;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
