import { Component, inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NotificationsService } from '../../../../utils/notifications.service';
import { Router, RouterLink } from '@angular/router';
import { CuentasTodasService } from '../../services/cuentas_todas.service';
import { CTL } from '../../interfaces/cuentasTodasRespuesta';
import { TransaccionSolicitud } from '../../interfaces/transaccionSolicitud';
import { TransaccionService } from '../../services/transaccion.service';
import { TransaccionError } from '../../interfaces/transaccionRespuesta';

@Component({
  selector: 'app-transferencia-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
  ],
  templateUrl: './transferencia-form.component.html',
  styleUrl: './transferencia-form.component.css'
})
export class TransferenciaFormComponent implements OnInit {

  cuentas: CTL[] = [];

  transferenciaForm: FormGroup;
  private cuentasTodasService = inject(CuentasTodasService);
  private transaccionService = inject(TransaccionService);

  constructor(
    private fb: FormBuilder,
    private notificationService: NotificationsService,
    private router: Router,
  ) {
    this.transferenciaForm = this.fb.group({
      cuentaOrigen: ['', Validators.required],
      cuentaDestino: ['', Validators.required],
      monto: [
        '',
        [
          Validators.required,
          Validators.min(0.01)
        ]
      ],
      observacion: ['']
    });

    // Cuando cambia la cuenta origen
    this.transferenciaForm
      .get('cuentaOrigen')
      ?.valueChanges
      .subscribe(numeroCuenta => {
        const cuenta = this.cuentas.find(
          c => c.numeroCuenta === numeroCuenta
        )
        const montoControl = this.transferenciaForm.get('monto');
        if (cuenta) {
          montoControl?.setValidators([
            Validators.required,
            Validators.min(0.01),
            Validators.max(Number(cuenta.saldo))
          ]);
        } else {
          montoControl?.setValidators([
            Validators.required,
            Validators.min(0.01)
          ]);
        }
        montoControl?.updateValueAndValidity();
      });
  }

  ngOnInit(): void {
    this.obtenerTodasCuentas();
  }

  obtenerSaldoCuentaOrigen(): number {
    const numeroCuenta =
      this.transferenciaForm.get('cuentaOrigen')?.value;

    const cuenta = this.cuentas.find(
      c => c.numeroCuenta === numeroCuenta
    );

    return Number(cuenta?.saldo ?? 0);
  }

  cuentasDiferentesValidator(form: AbstractControl): ValidationErrors | null {
    const origen = form.get('cuentaOrigen')?.value;
    const destino = form.get('cuentaDestino')?.value;
    if (origen && destino && origen === destino) {
      return {
        cuentasIguales: true
      };
    }
    return null;
  }

  obtenerTodasCuentas(): void {
    this.cuentasTodasService.obtenerTodasCuentas().subscribe({
      next: (respuesta) => {
        console.log('Respuesta API:', respuesta);
        this.cuentas = respuesta.ctl;
      },
      error: (error) => {
        console.error('Error al obtener cuentas:', error);
      }
    });
  }

  enviarTransferencia(): void {
    if (this.transferenciaForm.invalid) {
      this.transferenciaForm.markAllAsTouched();
      return;
    }
    const form = this.transferenciaForm.getRawValue();
    const request: TransaccionSolicitud = {
      userapp: 'TEST_WEB',
      process: crypto.randomUUID(),
      channel: 'WEB',
      operation: 'REALIZAR_TRANSFERENCIA',
      ip: '10.1.107.50',
      ctl: {
        numeroCuentaOrigen: form.cuentaOrigen!,
        numeroCuentaDestino: form.cuentaDestino!,
        monto: Number(form.monto),
        observacion: form.observacion == "" ? "Transferencia web pruebas" : form.observacion
      }
    };
    console.log("Prt: ", request)
    this.transaccionService.crearTransferencia(request).subscribe({
      next: (respuesta) => {
        console.log('Transferencia realizada:', respuesta);
        this.notificationService.success(respuesta.message);
        //this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Error en transferencia:', error);
        const respuestaError = error.error as TransaccionError;
        this.notificationService.error(respuestaError.mensaje);
      }
    });
  }
}
