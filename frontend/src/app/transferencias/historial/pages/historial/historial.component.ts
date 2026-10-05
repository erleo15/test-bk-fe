import { Component, inject, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Subject, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';

import { HistorialService, FiltrosHistorial } from '../../services/historial.service';
import { CTL } from '../../interfaces/historialRespuesta';

@Component({
  selector: 'app-historial',
  providers: [provideNativeDateAdapter()],
  imports: [
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    CurrencyPipe,
    DatePipe,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
  ],
  templateUrl: './historial.component.html',
  styleUrl: './historial.component.css'
})
export class HistorialComponent implements OnInit, OnDestroy {

  displayedColumns: string[] = [
    'Nro.',
    'numeroMovimiento',
    'monto',
    'numeroCuentaOrigen',
    'numeroCuentaDestino',
    'observacion',
    'estado',
    'fechaCreacion',
  ];

  page = 0;
  size = 10;
  totalElements = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50, 100, 200, 500, 1000];

  filtros: FiltrosHistorial = {
    cuentaOrigen: '',
    cuentaDestino: '',
    estado: '',
    fechaInicio: '',
    fechaFin: ''
  };

  private historialService = inject(HistorialService);
  private paginatorIntl = inject(MatPaginatorIntl);
  private busquedaSubject = new Subject<void>();
  private busquedaSub?: Subscription;

  dataSource = new MatTableDataSource<CTL>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngOnInit(): void {
    this.paginatorIntl.itemsPerPageLabel = 'Registros por página:';
    this.paginatorIntl.nextPageLabel = 'Siguiente';
    this.paginatorIntl.previousPageLabel = 'Anterior';
    this.paginatorIntl.firstPageLabel = 'Primera página';
    this.paginatorIntl.lastPageLabel = 'Última página';

    this.dataSource.filterPredicate = (data: CTL, filter: string) => {
      if (!filter) return true;
      try {
        const f = JSON.parse(filter);
        const matchOrigen = !f.cuentaOrigen || String(data.numeroCuentaOrigen).includes(f.cuentaOrigen.trim());
        const matchDestino = !f.cuentaDestino || String(data.numeroCuentaDestino).includes(f.cuentaDestino.trim());
        const matchEstado = !f.estado || data.estado === f.estado;
        return matchOrigen && matchDestino && matchEstado;
      } catch (e) {
        return true;
      }
    };

    this.busquedaSub = this.busquedaSubject.pipe(
      debounceTime(200)
    ).subscribe(() => {
      this.aplicarFiltros();
    });

    this.obtenerHistorial(this.page, this.size);
  }

  ngOnDestroy(): void {
    this.busquedaSub?.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.paginator.page.subscribe(event => {
      this.page = event.pageIndex;
      this.size = event.pageSize;
      if (this.esPaginacionServidor) {
        this.obtenerHistorial(this.page, this.size);
      }
    });
  }

  esPaginacionServidor = false;

  onInputFiltro(): void {
    this.dataSource.filter = JSON.stringify(this.filtros);
    this.busquedaSubject.next();
  }

  calcularPageSizeOptions(total: number, sizeActual?: number | null): number[] {
    const baseOptions = [5, 10, 20, 50, 100, 200, 500, 1000];
    const s = sizeActual || this.size || 10;
    let filtradas = baseOptions.filter(opt => opt <= total);
    if (!filtradas.includes(s)) {
      filtradas.push(s);
    }
    if (total > 0 && !filtradas.includes(total)) {
      filtradas.push(total);
    }
    filtradas.sort((a, b) => a - b);
    return filtradas.length > 0 ? filtradas : [5, 10, 20];
  }

  obtenerHistorial(page?: number | null, size?: number | null): void {
    this.historialService.obtenerHistorial(page, size, this.filtros).subscribe({
      next: (respuesta) => {
        console.log('Respuesta API:', respuesta);
        this.dataSource.data = respuesta.ctl;

        // Actualizamos total, página y tamaño devueltos por la API
        this.totalElements = respuesta.total;
        if (respuesta.page !== null && respuesta.page !== undefined) {
          this.page = respuesta.page;
        }
        if (respuesta.size !== null && respuesta.size !== undefined) {
          this.size = respuesta.size;
        }

        // Si el backend devuelve todas (page === null), la paginación es cliente
        if (respuesta.page === null || respuesta.size === null) {
          this.esPaginacionServidor = false;
          this.dataSource.paginator = this.paginator;
        } else {
          // Si el backend devuelve respuesta paginada (page y size del servidor)
          this.esPaginacionServidor = true;
          this.dataSource.paginator = null;
        }

        // Asegurar que el combo tenga la lista correcta incluyendo la opción seleccionada
        this.pageSizeOptions = [...this.calcularPageSizeOptions(respuesta.total, this.size)];

        // Forzar la selección del valor en el paginador de la vista
        if (this.paginator) {
          this.paginator.length = respuesta.total;
          this.paginator.pageSize = this.size;
          this.paginator.pageIndex = this.page;
        }

        this.dataSource.filter = JSON.stringify(this.filtros);
      },
      error: (error) => {
        console.error('Error al obtener historial:', error);
      }
    });
  }

  aplicarFiltros(): void {
    this.page = 0;
    if (this.paginator) {
      this.paginator.pageIndex = 0;
    }
    this.obtenerHistorial(this.page, this.size);
  }

  limpiarFiltros(): void {
    this.filtros = {
      cuentaOrigen: '',
      cuentaDestino: '',
      estado: '',
      fechaInicio: '',
      fechaFin: ''
    };
    this.aplicarFiltros();
  }
}
