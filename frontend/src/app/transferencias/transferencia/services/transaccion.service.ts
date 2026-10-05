import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../../environments/environment.development';
import { CuentasTodasRespuesta } from '../interfaces/cuentasTodasRespuesta';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { TransaccionSolicitud } from '../interfaces/transaccionSolicitud';
import { TransaccionRespuesta } from '../interfaces/transaccionRespuesta';

@Injectable({
  providedIn: 'root'
})
export class TransaccionService {

  private http = inject(HttpClient);
  private apiUrl = `${environment.baseUrl}/transferencias`;

  crearTransferencia(request: TransaccionSolicitud): Observable<TransaccionRespuesta> {
    return this.http.post<TransaccionRespuesta>(
      this.apiUrl,
      request
    );
  }
}
