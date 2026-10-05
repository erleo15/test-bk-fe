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
@Schema(name = "TransferenciaRequest", description = "DTO para la ejecución de transferencias interbancarias")
public class TransferenciaRequest {

    @NotBlank(message = "La cuenta de origen es obligatoria")
    @Pattern(regexp = "\\d{10}", message = "La cuenta de origen debe tener exactamente 10 dígitos numéricos")
    @Schema(description = "Número de cuenta de origen (debito)", example = "1234567890", required = true)
    private String numeroCuentaOrigen;

    @NotBlank(message = "La cuenta de destino es obligatoria")
    @Pattern(regexp = "\\d{10}", message = "La cuenta de destino debe tener exactamente 10 dígitos numéricos")
    @Schema(description = "Número de cuenta de destino (credito)", example = "0987654321", required = true)
    private String numeroCuentaDestino;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto de la transferencia debe ser mayor a cero")
    @Schema(description = "Monto de dinero a transferir", example = "250.00", required = true)
    private BigDecimal monto;

    @Size(max = 255, message = "La observación no puede superar 255 caracteres")
    @Schema(description = "Concepto u observación opcional del pago/transferencia", example = "Pago de cena")
    private String observacion;
}
