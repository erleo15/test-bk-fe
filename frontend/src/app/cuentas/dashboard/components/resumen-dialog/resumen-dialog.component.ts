import { Component, inject } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ResumenCuentaRespuesta } from '../../interfaces/resumen-cuenta';

@Component({
  selector: 'app-resumen-dialog',
  imports: [
    CommonModule,
    CurrencyPipe,
    MatDialogModule,
    MatButtonModule,
  ],
  template: `
    <h2 mat-dialog-title>
      Resumen de Cuenta: {{ data.numeroCuenta }}
    </h2>

    <mat-dialog-content class="resumen-content">
      <div class="resumen-grid">
        <div class="stat-card">
          <div class="stat-label">Saldo Actual</div>
          <div class="stat-value saldo">{{ saldoActual | currency:'USD':'symbol':'1.2-2' }}</div>
        </div>

        <div class="stat-card">
          <div class="stat-label">Total Transferido Hoy</div>
          <div class="stat-value transferido">{{ totalTransferidoHoy | currency:'USD':'symbol':'1.2-2' }}</div>
        </div>

        <div class="stat-card">
          <div class="stat-label">Transferencias del Día</div>
          <div class="stat-value">{{ cantidadTransferenciasDia }}</div>
        </div>

        <div class="stat-card" [class.warning]="cantidadSospechosas > 0">
          <div class="stat-label">Operaciones Sospechosas</div>
          <div class="stat-value sospechosas">{{ cantidadSospechosas }}</div>
        </div>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-raised-button color="primary" mat-dialog-close>Cerrar</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .resumen-content {
      padding: 16px 0;
    }
    .resumen-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
      min-width: 340px;
    }
    .stat-card {
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 16px;
      text-align: center;
    }
    .stat-card.warning {
      background: #fef2f2;
      border-color: #fca5a5;
    }
    .stat-label {
      font-size: 13px;
      color: #64748b;
      margin-bottom: 6px;
      font-weight: 500;
    }
    .stat-value {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
    }
    .stat-value.saldo {
      color: #16a34a;
    }
    .stat-value.transferido {
      color: #2563eb;
    }
    .stat-value.sospechosas {
      color: #dc2626;
    }
  `]
})
export class ResumenDialogComponent {
  data = inject<{ numeroCuenta: string; resumen: ResumenCuentaRespuesta }>(MAT_DIALOG_DATA);

  get saldoActual(): number {
    return Number(this.data.resumen.ctl?.saldoActual ?? this.data.resumen.saldoActual ?? 0);
  }

  get totalTransferidoHoy(): number {
    return Number(this.data.resumen.ctl?.totalTransferidoHoy ?? this.data.resumen.totalTransferidoHoy ?? 0);
  }

  get cantidadTransferenciasDia(): number {
    return Number(this.data.resumen.ctl?.cantidadTransferenciasDia ?? this.data.resumen.cantidadTransferenciasDia ?? 0);
  }

  get cantidadSospechosas(): number {
    return Number(this.data.resumen.ctl?.cantidadSospechosas ?? this.data.resumen.cantidadSospechosas ?? 0);
  }
}
