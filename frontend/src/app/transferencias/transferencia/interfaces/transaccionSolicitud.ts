export interface TransaccionSolicitud {
  userapp:   string;
  process:   string;
  channel:   string;
  operation: string;
  ip:        string;
  ctl:       CTL;
}

export interface CTL {
  numeroCuentaOrigen:  string;
  numeroCuentaDestino: string;
  monto:               number;
  observacion:         string;
}
