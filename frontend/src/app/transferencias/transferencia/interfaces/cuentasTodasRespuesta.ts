export interface CuentasTodasRespuesta {
  process:   string;
  code:      number;
  message:   string;
  timestamp: Date;
  total:     number;
  ctl:       CTL[];
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
  saldo:           string;
  estado:          string;
}
