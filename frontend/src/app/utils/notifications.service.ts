import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {

  constructor(private snackBar: MatSnackBar) {}

  success(message: string): void {
    this.snackBar.open(
      `✓ ${message}`,
      'Cerrar',
      {
        duration: 4000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['toast-success']
      }
    );
  }

  error(message: string): void {
    this.snackBar.open(
      `✕ ${message}`,
      'Cerrar',
      {
        duration: 8000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['toast-error']
      }
    );
  }
}
