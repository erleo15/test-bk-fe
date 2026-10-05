export interface HistorialRespuesta {
  process:   string;
  code:      number;
  message:   string;
  timestamp: Date;
  page:      number | null;
  size:      number | null;
  total:     number;
  ctl:       CTL[];
}

export interface CTL {
  numeroMovimiento: string;
  numeroCuentaOrigen:   number;
  numeroCuentaDestino:  number;
  monto:            string;
  observacion:      null | string;
  estado:           string;
  fechaCreacion:    Date;
  motivoEstado:     string;
}
