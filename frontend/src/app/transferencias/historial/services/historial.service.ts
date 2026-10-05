import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment.development';
import { HistorialRespuesta } from '../interfaces/historialRespuesta';

export interface FiltrosHistorial {
  cuentaOrigen?: string;
  cuentaDestino?: string;
  estado?: string;
  fechaInicio?: string | Date;
  fechaFin?: string | Date;
}

@Injectable({
  providedIn: 'root'
})
export class HistorialService {

  private http = inject(HttpClient);
  private apiUrl = `${environment.baseUrl}/transferencias`;

  obtenerHistorial(page?: number | null, size?: number | null, filtros?: FiltrosHistorial): Observable<HistorialRespuesta> {
    let params = new HttpParams();

    if (page !== undefined && page !== null) {
      params = params.set('page', page.toString());
    }
    if (size !== undefined && size !== null) {
      params = params.set('pageSize', size.toString());
    }

    if (filtros) {
      const formatFechaISO = (f?: any, esFin: boolean = false) => {
        if (!f) return undefined;
        let strDate = '';
        if (f instanceof Date) {
          const yyyy = f.getFullYear();
          const mm = String(f.getMonth() + 1).padStart(2, '0');
          const dd = String(f.getDate()).padStart(2, '0');
          const hh = String(f.getHours()).padStart(2, '0');
          const min = String(f.getMinutes()).padStart(2, '0');
          const ss = String(f.getSeconds()).padStart(2, '0');
          return `${yyyy}-${mm}-${dd}T${hh}:${min}:${ss}`;
        } else {
          strDate = String(f).trim();
        }
        if (!strDate) return undefined;

        // Si viene del input datetime-local (ej. 2026-10-01T14:30)
        if (strDate.includes('T')) {
          const partes = strDate.split('T')[1].split(':');
          if (partes.length === 2) {
            // Se añaden segundos :00 o :59
            return strDate + (esFin ? ':59' : ':00');
          }
          return strDate;
        }

        return esFin ? `${strDate}T23:59:59` : `${strDate}T00:00:00`;
      };

      const fInicio = formatFechaISO(filtros.fechaInicio, false);
      const fFin = formatFechaISO(filtros.fechaFin, true);

      if (filtros.cuentaOrigen) params = params.set('cuentaOrigen', filtros.cuentaOrigen);
      if (filtros.cuentaDestino) params = params.set('cuentaDestino', filtros.cuentaDestino);
      if (filtros.estado) params = params.set('estado', filtros.estado);
      if (fInicio) params = params.set('fechaInicio', fInicio);
      if (fFin) params = params.set('fechaFin', fFin);
    }

    const headers = new HttpHeaders({
      'Accept': 'application/json',
      'userapp': 'WEB_PORTAL',
      'channel': 'WEB',
      'process': crypto.randomUUID()
    });

    return this.http.get<HistorialRespuesta>(this.apiUrl, { params, headers });
  }
}
