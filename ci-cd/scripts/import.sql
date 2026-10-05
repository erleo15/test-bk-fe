-- COMENTARIOS NIVEL TABLA
ALTER TABLE PARAMETRO_SISTEMA COMMENT = 'Parametros configurables de las reglas de negocio';
ALTER TABLE CLIENTE COMMENT = 'Informacion de los clientes titulares de cuentas';
ALTER TABLE CUENTA COMMENT = 'Cuentas bancarias y sus saldos';
ALTER TABLE MOVIMIENTO COMMENT = 'Movimientos financieros entre cuentas';
ALTER TABLE LOG_AUDITORIA COMMENT = 'Registro de auditoria y trazabilidad de operaciones';

-- COMENTARIOS NIVEL COLUMNA 
-- PARAMETRO_SISTEMA
ALTER TABLE PARAMETRO_SISTEMA MODIFY COLUMN id_parametro BIGINT AUTO_INCREMENT COMMENT 'Identificador unico del parametro';
ALTER TABLE PARAMETRO_SISTEMA MODIFY COLUMN codigo_parametro VARCHAR(50) NOT NULL COMMENT 'Codigo de la regla de negocio';
ALTER TABLE PARAMETRO_SISTEMA MODIFY COLUMN canal VARCHAR(30) NOT NULL COMMENT 'Canal de aplicacion de la regla';
ALTER TABLE PARAMETRO_SISTEMA MODIFY COLUMN codigo_transaccion VARCHAR(30) NOT NULL COMMENT 'Codigo de la transaccion a la que aplica la regla';
ALTER TABLE PARAMETRO_SISTEMA MODIFY COLUMN valor DECIMAL(15,2) NOT NULL COMMENT 'Valor numerico de la regla';
ALTER TABLE PARAMETRO_SISTEMA MODIFY COLUMN estado BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el parametro se encuentra activo';
ALTER TABLE PARAMETRO_SISTEMA MODIFY COLUMN fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creacion del parametro';

-- CLIENTE
ALTER TABLE CLIENTE MODIFY COLUMN id_cliente BIGINT AUTO_INCREMENT COMMENT 'Identificador unico del cliente';
ALTER TABLE CLIENTE MODIFY COLUMN identificacion VARCHAR(20) NOT NULL COMMENT 'Documento de identidad o cedula del cliente';
ALTER TABLE CLIENTE MODIFY COLUMN primer_nombre VARCHAR(50) COMMENT 'Primer nombre del cliente';
ALTER TABLE CLIENTE MODIFY COLUMN segundo_nombre VARCHAR(50) COMMENT 'Segundo nombre del cliente';
ALTER TABLE CLIENTE MODIFY COLUMN apellido_paterno VARCHAR(50) COMMENT 'Apellido paterno del cliente';
ALTER TABLE CLIENTE MODIFY COLUMN apellido_materno VARCHAR(50) COMMENT 'Apellido materno del cliente';
ALTER TABLE CLIENTE MODIFY COLUMN nombre_legal VARCHAR(200) NOT NULL COMMENT 'Nombre legal completo del cliente';

-- CUENTA
ALTER TABLE CUENTA MODIFY COLUMN id_cuenta BIGINT AUTO_INCREMENT COMMENT 'Identificador unico de la cuenta';
ALTER TABLE CUENTA MODIFY COLUMN cliente_id BIGINT NOT NULL COMMENT 'Cliente titular de la cuenta';
ALTER TABLE CUENTA MODIFY COLUMN numero_cuenta VARCHAR(10) NOT NULL COMMENT 'Numero de cuenta de exactamente 10 digitos';
ALTER TABLE CUENTA MODIFY COLUMN tipo_cuenta VARCHAR(20) NOT NULL COMMENT 'Tipo de cuenta: AHORRO o CORRIENTE';
ALTER TABLE CUENTA MODIFY COLUMN saldo DECIMAL(15,2) NOT NULL COMMENT 'Saldo disponible de la cuenta';
ALTER TABLE CUENTA MODIFY COLUMN estado VARCHAR(20) NOT NULL COMMENT 'Estado operativo de la cuenta: ACTIVA o INACTIVA';

-- MOVIMIENTO
ALTER TABLE MOVIMIENTO MODIFY COLUMN id_movimiento BIGINT AUTO_INCREMENT COMMENT 'Identificador unico del movimiento';
ALTER TABLE MOVIMIENTO MODIFY COLUMN numero_movimiento VARCHAR(20) NOT NULL COMMENT 'Numero de movimiento publico para trazabilidad (TRF-XXXXXXXX)';
ALTER TABLE MOVIMIENTO MODIFY COLUMN cuenta_origen_id BIGINT NOT NULL COMMENT 'Cuenta desde la cual se debitan los fondos';
ALTER TABLE MOVIMIENTO MODIFY COLUMN cuenta_destino_id BIGINT NOT NULL COMMENT 'Cuenta que recibe los fondos';
ALTER TABLE MOVIMIENTO MODIFY COLUMN monto DECIMAL(15,2) NOT NULL COMMENT 'Monto del movimiento';
ALTER TABLE MOVIMIENTO MODIFY COLUMN observacion VARCHAR(255) COMMENT 'Concepto u observacion de la transferencia realizada por el cliente';
ALTER TABLE MOVIMIENTO MODIFY COLUMN estado VARCHAR(20) NOT NULL COMMENT 'Estado del movimiento: COMPLETADA, RECHAZADA o REQUIERE_REVISION';
ALTER TABLE MOVIMIENTO MODIFY COLUMN fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creacion del movimiento';
ALTER TABLE MOVIMIENTO MODIFY COLUMN motivo_estado VARCHAR(255) COMMENT 'Motivo asociado al estado del movimiento';

-- LOG_AUDITORIA
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN id_log BIGINT AUTO_INCREMENT COMMENT 'Identificador unico del registro de auditoria';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN process_id VARCHAR(50) NOT NULL COMMENT 'Identificador de trazabilidad de la peticion';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN operacion VARCHAR(100) NOT NULL COMMENT 'Endpoint o metodo ejecutado';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN payload_entrada TEXT COMMENT 'Payload de entrada de la peticion';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN payload_salida TEXT COMMENT 'Payload de salida de la peticion';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN codigo_http INT NOT NULL COMMENT 'Codigo HTTP retornado al cliente';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN mensaje_error TEXT COMMENT 'Detalle del error tecnico o de negocio';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN tiempo_ejecucion_ms INT NOT NULL COMMENT 'Tiempo total de procesamiento en milisegundos';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN headers TEXT COMMENT 'Headers HTTP de la peticion en formato JSON';
ALTER TABLE LOG_AUDITORIA MODIFY COLUMN fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora del registro de auditoria';

INSERT IGNORE INTO PARAMETRO_SISTEMA (codigo_parametro, canal, codigo_transaccion, valor, estado)
VALUES ('MAX_MONTO_POR_TRANSFERENCIA', '*', 'TRF_INT', 5000.00, true);

INSERT IGNORE INTO PARAMETRO_SISTEMA (codigo_parametro, canal, codigo_transaccion, valor, estado)
VALUES ('MAX_TRANSFERENCIAS_POR_HORA', '*', 'TRF_INT', 5.00, true);

INSERT IGNORE INTO PARAMETRO_SISTEMA (codigo_parametro, canal, codigo_transaccion, valor, estado)
VALUES ('LIMITE_DIARIO_ACUMULADO', '*', 'TRF_INT', 10000.00, true);

INSERT IGNORE INTO CLIENTE (id_cliente, identificacion, primer_nombre, segundo_nombre, apellido_paterno, apellido_materno, nombre_legal) VALUES
(1, '1723456789', 'Juan', 'Carlos', 'Perez', 'Gomez', 'Juan Carlos Perez Gomez'),
(2, '0987654321', 'Maria', 'Fernanda', 'Lopez', 'Martinez', 'Maria Fernanda Lopez Martinez'),
(3, '1102938475', 'Carlos', 'Alberto', 'Gomez', 'Torres', 'Carlos Alberto Gomez Torres'),
(4, '1711223344', 'Luis', 'Enrique', 'Morales', 'Suarez', 'Luis Enrique Morales Suarez'),
(5, '0922334455', 'Ana', 'Beatriz', 'Castillo', 'Vargas', 'Ana Beatriz Castillo Vargas'),
(6, '1133445566', 'Diego', 'Alejandro', 'Rios', 'Mendoza', 'Diego Alejandro Rios Mendoza'),
(7, '1744556677', 'Sofia', 'Elizabeth', 'Espinoza', 'Ramos', 'Sofia Elizabeth Espinoza Ramos'),
(8, '0955667788', 'Jorge', 'Humberto', 'Cardenas', 'Benitez', 'Jorge Humberto Cardenas Benitez'),
(9, '1166778899', 'Valeria', 'Cristina', 'Paredes', 'Silva', 'Valeria Cristina Paredes Silva'),
(10, '1777889900', 'Gabriel', 'Mateo', 'Salazar', 'Ortega', 'Gabriel Mateo Salazar Ortega');

INSERT IGNORE INTO CUENTA (id_cuenta, cliente_id, numero_cuenta, tipo_cuenta, saldo, estado) VALUES
(1, 1, '1234567890', 'AHORRO', 51000.00, 'ACTIVA'),
(2, 2, '0987654321', 'CORRIENTE', 23500.31, 'ACTIVA'),
(3, 3, '1122334455', 'AHORRO', 1021000.22, 'INACTIVA'),
(4, 4, '1000000004', 'CORRIENTE', 4200.50, 'ACTIVA'),
(5, 5, '1000000005', 'AHORRO', 184350.75, 'ACTIVA'),
(6, 6, '1000000006', 'AHORRO', 136300.00, 'INACTIVA'),
(7, 7, '1000000007', 'CORRIENTE', 19100.20, 'ACTIVA'),
(8, 8, '1000000008', 'AHORRO', 3450.00, 'ACTIVA'),
(9, 9, '1000000009', 'CORRIENTE', 7800.82, 'INACTIVA'),
(10, 10, '1000000010', 'AHORRO', 12500.67, 'ACTIVA');
