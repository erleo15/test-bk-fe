package ec.com.test.infrastructure.adapter.in.rest;

import ec.com.test.application.port.in.CuentaUseCase;
import ec.com.test.application.port.in.TransferenciaUseCase;
import ec.com.test.domain.model.Cliente;
import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.model.Movimiento;
import ec.com.test.infrastructure.adapter.in.rest.dto.CrearCuentaRequest;
import ec.com.test.infrastructure.adapter.in.rest.dto.RequestFrame;
import ec.com.test.infrastructure.adapter.in.rest.dto.TransferenciaRequest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class RestIntegrationTest {

    @InjectMock
    CuentaUseCase cuentaUseCase;

    @InjectMock
    TransferenciaUseCase transferenciaUseCase;

    @Test
    void testCrearCuentaEndpoint() {
        CrearCuentaRequest ctl = new CrearCuentaRequest();
        ctl.setNumeroCuenta("1234567890");
        ctl.setPrimerNombre("Juan");
        ctl.setSegundoNombre("Carlos");
        ctl.setApellidoPaterno("Pérez");
        ctl.setApellidoMaterno("Gómez");
        ctl.setIdentificacionTitular("1723456789");
        ctl.setTipoCuenta("AHORRO");
        ctl.setSaldoInicial(new BigDecimal("1000.00"));
        ctl.setEstado("ACTIVA");

        RequestFrame<CrearCuentaRequest> frame = new RequestFrame<>();
        frame.setUserapp("APP_MOBILE");
        frame.setProcess("PROC-101");
        frame.setChannel("MOBILE");
        frame.setOperation("CREAR_CUENTA");
        frame.setIp("192.168.1.1");
        frame.setCtl(ctl);

        Cuenta creada = new Cuenta(1L, 1L, "1723456789", "Juan", "Carlos", "Pérez", "Gómez", "Juan Carlos Pérez Gómez", "1234567890", "AHORRO", new BigDecimal("1000.00"), "ACTIVA");
        when(cuentaUseCase.crearCuenta(any(Cuenta.class), any(Cliente.class))).thenReturn(creada);

        given()
                .contentType(ContentType.JSON)
                .body(frame)
                .when()
                .post("/api/cuentas")
                .then()
                .statusCode(201)
                .body("code", equalTo(201))
                .body("ctl.numeroCuenta", equalTo("1234567890"))
                .body("ctl.identificacion", equalTo("1723456789"));
    }

    @Test
    void testEjecutarTransferenciaEndpoint() {
        TransferenciaRequest ctl = new TransferenciaRequest();
        ctl.setNumeroCuentaOrigen("1234567890");
        ctl.setNumeroCuentaDestino("0987654321");
        ctl.setMonto(new BigDecimal("250.00"));
        ctl.setObservacion("Pago de servicios");

        RequestFrame<TransferenciaRequest> frame = new RequestFrame<>();
        frame.setUserapp("APP_WEB");
        frame.setProcess("PROC-102");
        frame.setChannel("WEB");
        frame.setOperation("TRANSFERENCIA");
        frame.setIp("10.0.0.1");
        frame.setCtl(ctl);

        Movimiento movimiento = new Movimiento(100L, "TRF-A3F2B1C4", "1234567890", "0987654321", new BigDecimal("250.00"), "Pago de servicios", "COMPLETADA", LocalDateTime.now(), "Transferencia ejecutada con éxito");
        when(transferenciaUseCase.ejecutarTransferencia("1234567890", "0987654321", new BigDecimal("250.00"), "Pago de servicios")).thenReturn(movimiento);

        given()
                .contentType(ContentType.JSON)
                .body(frame)
                .when()
                .post("/api/transferencias")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("ctl.numeroMovimiento", equalTo("TRF-A3F2B1C4"))
                .body("ctl.numeroCuentaOrigen", equalTo("1234567890"))
                .body("ctl.numeroCuentaDestino", equalTo("0987654321"))
                .body("ctl.estado", equalTo("COMPLETADA"));
    }
}
