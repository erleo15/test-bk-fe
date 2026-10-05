package ec.com.test.domain.constant;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@UtilityClass
public class DomainConstants {

    public static final String ZONE_ID_ECUADOR_STR = "America/Guayaquil";
    public static final ZoneId ZONE_ECUADOR = ZoneId.of(ZONE_ID_ECUADOR_STR);
    public static final String ESTADO_CUENTA_ACTIVA = "ACTIVA";
    public static final String ESTADO_CUENTA_INACTIVA = "INACTIVA";
    public static final String ESTADO_MOVIMIENTO_COMPLETADA = "COMPLETADA";
    public static final String ESTADO_MOVIMIENTO_REQUIERE_REVISION = "REQUIERE_REVISION";
    public static final String ESTADO_MOVIMIENTO_RECHAZADA = "RECHAZADA";
    public static final String PARAM_LIMITE_DIARIO_ACUMULADO = "LIMITE_DIARIO_ACUMULADO";
    public static final String PARAM_MAX_MONTO_POR_TRANSFERENCIA = "MAX_MONTO_POR_TRANSFERENCIA";
    public static final String PARAM_MAX_TRANSFERENCIAS_POR_HORA = "MAX_TRANSFERENCIAS_POR_HORA";
    public static final String CANAL_API = "API";
    public static final String TRANSACCION_TRF_INT = "TRF_INT";
    public static final BigDecimal DEFAULT_LIMITE_DIARIO = new BigDecimal("10000.00");
    public static final BigDecimal DEFAULT_MAX_MONTO_TRANSFERENCIA = new BigDecimal("5000.00");
    public static final BigDecimal DEFAULT_MAX_TRANSFERENCIAS_HORA = new BigDecimal("5.00");
    public static final String MOTIVO_TRANSACCION_EXITOSA = "Transferencia ejecutada con éxito";
    public static final String MOTIVO_TRANSACCION_SOSPECHOSA = "Operación detectada como sospechosa por reglas de monto o frecuencia";
    public static final String MOTIVO_SOSPECHOSA_MONTO = "Operación marcada para revisión: el monto supera el umbral permitido por transacción";
    public static final String MOTIVO_SOSPECHOSA_FRECUENCIA = "Operación marcada para revisión: excede el límite de frecuencia de transacciones por hora";
    public static final String MOTIVO_SOSPECHOSA_AMBOS = "Operación marcada para revisión: excede el umbral de monto y la frecuencia de transacciones por hora";
    public static final String NOMBRE_LEGAL_DEFAULT = "SIN NOMBRE";
    public static final String NUMERO_CUENTA_REGEX = "\\d{10}";
    public static final int NUMERO_CUENTA_LONGITUD = 10;
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_UNPROCESSABLE_ENTITY = 422;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String SORT_DESC = "desc";
    public static final String SORT_ASC = "asc";
    public static final String SORT_FIELD_SALDO = "saldo";
    public static final String SORT_FIELD_ID_CUENTA = "idCuenta";
    public static final String SORT_FIELD_FECHA_CREACION = "fechaCreacion";
    public static final String HEADER_PROCESS_ID = "X-Process-Id";
    public static final String HEADER_START_TIME = "X-Start-Time";
    public static final String HEADER_REQUEST_BODY = "X-Request-Body";
    public static final String HEADER_HEADERS_JSON = "X-Headers-Json";
    public static final int MAX_AUDIT_PAYLOAD_LENGTH = 3000;

    public static LocalDateTime nowEcuador() {
        return LocalDateTime.now(ZONE_ECUADOR);
    }

    public static LocalDate todayEcuador() {
        return LocalDate.now(ZONE_ECUADOR);
    }
}
