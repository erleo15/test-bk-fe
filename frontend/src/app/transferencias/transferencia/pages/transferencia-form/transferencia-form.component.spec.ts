import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';

import { TransferenciaFormComponent } from './transferencia-form.component';

describe('TransferenciaFormComponent', () => {
  let component: TransferenciaFormComponent;
  let fixture: ComponentFixture<TransferenciaFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferenciaFormComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNoopAnimations(),
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransferenciaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse el componente TransferenciaFormComponent', () => {
    expect(component).toBeTruthy();
  });

  it('debe iniciar el formulario con validadores requeridos', () => {
    expect(component.transferenciaForm).toBeDefined();
    expect(component.transferenciaForm.get('cuentaOrigen')?.valid).toBeFalse();
    expect(component.transferenciaForm.get('cuentaDestino')?.valid).toBeFalse();
    expect(component.transferenciaForm.get('monto')?.valid).toBeFalse();
  });
});
