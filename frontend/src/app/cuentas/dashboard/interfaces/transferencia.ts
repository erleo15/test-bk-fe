export interface transferencia {
  numeroCuenta: string;
  titular: string;
  tipoCuenta: 'AHORRO' | 'CORRIENTE';
  saldoActual: number;
  estado: 'ACTIVA' | 'INACTIVA';
}
