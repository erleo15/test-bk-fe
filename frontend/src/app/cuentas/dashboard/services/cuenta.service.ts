import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RespuestaCuentas } from '../interfaces/respuesta-cuentas';
import { ResumenCuentaRespuesta } from '../interfaces/resumen-cuenta';
import { environment } from '../../../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class CuentaService {

  private http = inject(HttpClient);
  private apiUrl = `${environment.baseUrl}/cuentas`;

  obtenerCuentas(page: number, size: number): Observable<RespuestaCuentas> {
    return this.http.get<RespuestaCuentas>(
      `${this.apiUrl}?page=${page}&pageSize=${size}`
    );
  }

  obtenerResumenCuenta(numeroCuenta: string): Observable<ResumenCuentaRespuesta> {
    return this.http.get<ResumenCuentaRespuesta>(
      `${this.apiUrl}/${numeroCuenta}/resumen`
    );
  }
}
