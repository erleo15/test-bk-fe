import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { HistorialService } from './historial.service';
import { HistorialRespuesta } from '../interfaces/historialRespuesta';

describe('HistorialService', () => {
  let service: HistorialService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        HistorialService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(HistorialService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('debe solicitar el historial con las cabeceras requeridas', () => {
    const mockHistorial: HistorialRespuesta = {
      process: 'PROC-001',
      code: 200,
      message: 'OK',
      timestamp: new Date(),
      page: 0,
      size: 10,
      total: 1,
      ctl: [
        {
          numeroMovimiento: 'MOV-1',
          numeroCuentaOrigen: 9983352001,
          numeroCuentaDestino: 9983352002,
          monto: '100.00',
          observacion: 'Prueba',
          estado: 'COMPLETADA',
          fechaCreacion: new Date(),
          motivoEstado: 'OK'
        }
      ]
    };

    service.obtenerHistorial(0, 10).subscribe(res => {
      expect(res.ctl.length).toBe(1);
      expect(res.ctl[0].numeroMovimiento).toBe('MOV-1');
    });

    const req = httpMock.expectOne(req => req.url.includes('/api/transferencias'));
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('userapp')).toBe('WEB_PORTAL');
    expect(req.request.headers.get('channel')).toBe('WEB');
    req.flush(mockHistorial);
  });
});
