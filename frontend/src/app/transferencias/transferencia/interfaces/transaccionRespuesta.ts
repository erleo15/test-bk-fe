export interface TransaccionRespuesta {
  process:   string;
  code:      number;
  message:   string;
  timestamp: Date;
  ctl:       CTL;
}

export interface CTL {
  idMovimiento:    number;
  cuentaOrigenId:  number;
  cuentaDestinoId: number;
  monto:           string;
  observacion:     string;
  estado:          string;
  fechaCreacion:   Date;
  motivoEstado:    string;
}

export interface TransaccionError {
  processId:  string;
  mensaje:    string;
  codigoHttp: number;
  timestamp:  Date;
}
