package ec.com.test.infrastructure.adapter.in.rest.mapper;

import ec.com.test.domain.model.Cuenta;
import ec.com.test.domain.model.Movimiento;
import ec.com.test.domain.model.ResumenCuenta;
import ec.com.test.infrastructure.adapter.in.rest.dto.CrearCuentaRequest;
import ec.com.test.infrastructure.adapter.in.rest.dto.CuentaResponse;
import ec.com.test.infrastructure.adapter.in.rest.dto.MovimientoResponse;
import ec.com.test.infrastructure.adapter.in.rest.dto.ResumenCuentaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface DtoMapper {

    @Mapping(target = "idCuenta", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "identificacionTitular", ignore = true)
    @Mapping(target = "primerNombreTitular", ignore = true)
    @Mapping(target = "segundoNombreTitular", ignore = true)
    @Mapping(target = "apellidoPaternoTitular", ignore = true)
    @Mapping(target = "apellidoMaternoTitular", ignore = true)
    @Mapping(target = "nombreLegalTitular", ignore = true)
    @Mapping(target = "saldo", source = "saldoInicial")
    Cuenta toDomain(CrearCuentaRequest request);

    @Mapping(target = "idCliente", ignore = true)
    @Mapping(target = "nombreLegal", ignore = true)
    @Mapping(target = "identificacion", source = "identificacionTitular")
    ec.com.test.domain.model.Cliente toClienteDomain(CrearCuentaRequest request);

    @Mapping(target = "identificacion", source = "identificacionTitular")
    @Mapping(target = "primerNombre", source = "primerNombreTitular")
    @Mapping(target = "segundoNombre", source = "segundoNombreTitular")
    @Mapping(target = "apellidoPaterno", source = "apellidoPaternoTitular")
    @Mapping(target = "apellidoMaterno", source = "apellidoMaternoTitular")
    @Mapping(target = "nombreLegal", source = "nombreLegalTitular")
    CuentaResponse toCuentaResponse(Cuenta cuenta);

    List<CuentaResponse> toCuentaResponseList(List<Cuenta> cuentas);

    MovimientoResponse toMovimientoResponse(Movimiento movimiento);

    List<MovimientoResponse> toMovimientoResponseList(List<Movimiento> movimientos);

    ResumenCuentaResponse toResumenCuentaResponse(ResumenCuenta resumen);
}
