import { Routes } from '@angular/router';
import { CuentasComponent } from './cuentas/dashboard/pages/cuentas/cuentas.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('./home/pages/menu/menu.component').then(m => m.MenuComponent)
  },
  {
    path: 'cuentas',
    loadComponent: () =>
      import('./cuentas/dashboard/pages/cuentas/cuentas.component').then(m => m.CuentasComponent)
  },
  {
    path: 'transferencias',
    loadComponent: () => import('./transferencias/transferencia/pages/transferencia-form/transferencia-form.component').then(m => m.TransferenciaFormComponent)
  },
  {
    path: 'historial_transferencias',
    loadComponent: () => import('./transferencias/historial/pages/historial/historial.component').then(m => m.HistorialComponent)
  }
];
