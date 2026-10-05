package ec.com.test.infrastructure.adapter.in.rest.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.*;
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
@Schema(name = "CrearCuentaRequest", description = "DTO para la creación de cuentas bancarias")
public class CrearCuentaRequest {

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Pattern(regexp = "\\d{10}", message = "El número de cuenta debe tener exactamente 10 dígitos numéricos")
    @Schema(description = "Número único de cuenta de 10 dígitos", example = "1234567890", required = true)
    private String numeroCuenta;

    @Schema(description = "Primer nombre del titular", example = "Juan")
    private String primerNombre;

    @Schema(description = "Segundo nombre del titular", example = "Carlos")
    private String segundoNombre;

    @Schema(description = "Apellido paterno del titular", example = "Pérez")
    private String apellidoPaterno;

    @Schema(description = "Apellido materno del titular", example = "Gómez")
    private String apellidoMaterno;

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 20, message = "La identificación no puede superar 20 caracteres")
    @Schema(description = "Documento de identidad del titular", example = "1723456789", required = true)
    private String identificacionTitular;

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Pattern(regexp = "^(AHORRO|CORRIENTE)$", message = "El tipo de cuenta debe ser AHORRO o CORRIENTE")
    @Schema(description = "Tipo de cuenta (AHORRO o CORRIENTE)", example = "AHORRO", required = true)
    private String tipoCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.00", message = "El saldo inicial debe ser mayor o igual a 0")
    @Schema(description = "Saldo inicial de apertura", example = "1000.00", required = true)
    private BigDecimal saldoInicial;

    @Pattern(regexp = "^(ACTIVA|INACTIVA)$", message = "El estado debe ser ACTIVA o INACTIVA")
    @Schema(description = "Estado operativo de la cuenta", example = "ACTIVA", defaultValue = "ACTIVA")
    private String estado;
}
