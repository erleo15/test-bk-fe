export interface ResumenCuentaData {
  saldoActual: number;
  totalTransferidoHoy: number;
  cantidadTransferenciasDia: number;
  cantidadSospechosas: number;
}

export interface ResumenCuentaRespuesta {
  process?: string;
  code?: number;
  message?: string;
  timestamp?: Date;
  saldoActual?: number;
  totalTransferidoHoy?: number;
  cantidadTransferenciasDia?: number;
  cantidadSospechosas?: number;
  ctl?: ResumenCuentaData;
  data?: ResumenCuentaData;
}
