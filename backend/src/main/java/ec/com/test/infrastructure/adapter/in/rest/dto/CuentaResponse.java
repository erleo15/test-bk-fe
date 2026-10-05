package ec.com.test.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@RegisterForReflection
@Schema(name = "CuentaResponse", description = "Respuesta con los detalles de la cuenta bancaria")
public class CuentaResponse {

    @Schema(description = "ID único de la cuenta", example = "1")
    private Long idCuenta;

    @Schema(description = "Identificación/Cédula del cliente titular", example = "1723456789")
    private String identificacion;

    @Schema(description = "Primer nombre del titular", example = "Juan")
    private String primerNombre;

    @Schema(description = "Segundo nombre del titular", example = "Carlos")
    private String segundoNombre;

    @Schema(description = "Apellido paterno del titular", example = "Pérez")
    private String apellidoPaterno;

    @Schema(description = "Apellido materno del titular", example = "Gómez")
    private String apellidoMaterno;

    @Schema(description = "Nombre legal completo del titular", example = "Juan Carlos Pérez Gómez")
    private String nombreLegal;

    @Schema(description = "Número único de cuenta bancaria", example = "1234567890")
    private String numeroCuenta;

    @Schema(description = "Tipo de cuenta bancaria", example = "AHORRO")
    private String tipoCuenta;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Saldo disponible actual", example = "1000.00")
    private BigDecimal saldo;

    @Schema(description = "Estado operativo actual", example = "ACTIVA")
    private String estado;
}
