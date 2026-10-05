export interface RespuestaCuentas {
  process:   string;
  code:      number;
  message:   string;
  timestamp: Date;
  page: number,
  size: number,
  total: number;
  ctl: CTL[];
}

export interface CTL {
  idCuenta:        number;
  identificacion:  string;
  primerNombre:    string;
  segundoNombre:   string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  nombreLegal:     string;
  numeroCuenta:    string;
  tipoCuenta:      string;
  saldo:           number;
  estado:          string;
}




