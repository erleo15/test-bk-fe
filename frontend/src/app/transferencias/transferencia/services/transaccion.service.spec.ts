import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { TransaccionService } from './transaccion.service';
import { TransaccionSolicitud } from '../interfaces/transaccionSolicitud';
import { TransaccionRespuesta } from '../interfaces/transaccionRespuesta';

describe('TransaccionService', () => {
  let service: TransaccionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TransaccionService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(TransaccionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('debe enviar la solicitud de transferencia via POST', () => {
    const solicitud: TransaccionSolicitud = {
      userapp: 'TEST_WEB',
      process: 'PROC-123',
      channel: 'WEB',
      operation: 'REALIZAR_TRANSFERENCIA',
      ip: '127.0.0.1',
      ctl: {
        numeroCuentaOrigen: '9983352001',
        numeroCuentaDestino: '9983352002',
        monto: 50.00,
        observacion: 'Prueba unitaria'
      }
    };

    const mockRespuesta: TransaccionRespuesta = {
      process: 'PROC-123',
      code: 200,
      message: 'Transferencia realizada con éxito',
      timestamp: new Date(),
      ctl: {
        idTransaccion: 1,
        numeroMovimiento: 'MOV-123',
        numeroCuentaOrigen: '9983352001',
        numeroCuentaDestino: '9983352002',
        monto: '50.00',
        estado: 'COMPLETADA'
      }
    };

    service.crearTransferencia(solicitud).subscribe(res => {
      expect(res.code).toBe(200);
      expect(res.message).toContain('éxito');
    });

    const req = httpMock.expectOne(req => req.url.includes('/api/transferencias'));
    expect(req.request.method).toBe('POST');
    req.flush(mockRespuesta);
  });
});
