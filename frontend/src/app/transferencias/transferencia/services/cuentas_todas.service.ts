import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment.development';
import { CuentasTodasRespuesta } from '../interfaces/cuentasTodasRespuesta';

@Injectable({
  providedIn: 'root'
})
export class CuentasTodasService {

  private http = inject(HttpClient);

  private apiUrl = `${environment.baseUrl}/cuentas`;

  obtenerTodasCuentas(): Observable<CuentasTodasRespuesta> {
    return this.http.get<CuentasTodasRespuesta>(
      `${this.apiUrl}`
    );
  }
}
