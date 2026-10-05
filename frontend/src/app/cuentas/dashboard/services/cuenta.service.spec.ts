import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { CuentaService } from './cuenta.service';
import { ResumenCuentaRespuesta } from '../interfaces/resumen-cuenta';

describe('CuentaService', () => {
  let service: CuentaService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CuentaService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CuentaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('debe obtener el resumen de una cuenta', () => {
    const mockResumen: ResumenCuentaRespuesta = {
      saldoActual: 1500.00,
      totalTransferidoHoy: 300.00,
      cantidadTransferenciasDia: 2,
      cantidadSospechosas: 0
    };

    service.obtenerResumenCuenta('9983352005').subscribe(res => {
      expect(res.saldoActual).toBe(1500.00);
      expect(res.totalTransferidoHoy).toBe(300.00);
      expect(res.cantidadTransferenciasDia).toBe(2);
    });

    const req = httpMock.expectOne(req => req.url.includes('/api/cuentas/9983352005/resumen'));
    expect(req.request.method).toBe('GET');
    req.flush(mockResumen);
  });
});
