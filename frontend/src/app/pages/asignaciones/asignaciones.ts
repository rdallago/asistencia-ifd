import {
  Component,ChangeDetectorRef,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AsignacionService } from '../../services/asignacion.service';
import { DocenteService } from '../../services/docente.service';
import { TurnoService } from '../../services/turno.service';

export interface Asignacion {

  id?: number;

  docente: {
    id: number;
    nombre: string;
    apellido: string;
    dni: string;
  };

  turno: {
    id: number;
    nombre: string;
  };

  fechaDesde: string;
  fechaHasta?: string;
  activo: boolean;
}

export interface AsignacionRequest {

  docenteId: number;
  turnoId: number;
  fechaDesde: string;
  
}


@Component({
  selector: 'app-asignaciones',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './asignaciones.html',
  styleUrls: ['./asignaciones.css']
})
export class AsignacionesComponent implements OnInit {

  asignaciones: Asignacion[] = [];
  asignacionesFiltradas: Asignacion[] = [];

  docentes: any[] = [];
  turnos: any[] = [];

  filtroBusqueda = '';
  filtroTurno = 0;

  mostrarFormulario = false;
  mostrarHistorial = false;
  mostrarSoloActivas = true;

  asignacion: AsignacionRequest = {
    docenteId: 0,
    turnoId: 0,
    fechaDesde: new Date()
      .toISOString()
      .split('T')[0]
  };

  constructor(
    private asignacionService: AsignacionService,
    private docenteService: DocenteService,
    private turnoService: TurnoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.listar();

    this.docenteService.getAll()
      .subscribe(data => this.docentes = data);

    this.turnoService.getAll()
      .subscribe(data => this.turnos = data);
  }

  listar(): void {

  this.asignacionService
    .getAll()
    .subscribe(data => {

      this.asignaciones = data;

this.filtrar();

this.cdr.detectChanges();

      this.filtrar();

      this.cdr.detectChanges();
    });
}

  abrirFormulario(): void {

    this.asignacion = {
      docenteId: 0,
      turnoId: 0,
      fechaDesde: new Date()
        .toISOString()
        .split('T')[0]
    };

    this.mostrarFormulario = true;
  }

  guardar(): void {

  this.asignacionService
    .create(this.asignacion)
    .subscribe({

      next: () => {

        this.mostrarFormulario = false;
        this.listar();
      },

      error: err => {

        alert(
          err.error?.message ??
          'Error al crear la asignación'
        );
      }
    });
}

  desasignar(asignacion: Asignacion): void {

  if (!confirm('¿Desea desasignar al docente de este turno?')) {
    return;
  }

  this.asignacionService
    .desasignar(asignacion.id!)
    .subscribe({

      next: () => this.listar(),

      error: err => {

        alert(
          err.error?.message ??
          'Error al desasignar al docente'
        );
      }
    });
}

  filtrar(): void {

  const texto = this.filtroBusqueda.toLowerCase();

  this.asignacionesFiltradas =
    this.asignaciones.filter(a => {

      const coincideTexto =

        a.docente.nombre
          .toLowerCase()
          .includes(texto)

        ||

        a.docente.apellido
          .toLowerCase()
          .includes(texto)

        ||

        a.turno.nombre
          .toLowerCase()
          .includes(texto);

      const coincideTurno =

        this.filtroTurno === 0 ||

        a.turno.id === this.filtroTurno;

      const coincideEstado =

        !this.mostrarSoloActivas ||

        a.activo;

      return coincideTexto
        && coincideTurno
        && coincideEstado;
    });
}

  cambiarVista(): void {

  this.mostrarHistorial =
    !this.mostrarHistorial;

  this.listar();
}
cambiarFiltro(): void {

  this.mostrarSoloActivas =
    !this.mostrarSoloActivas;

  this.filtrar();
}
reactivar(asignacion: Asignacion): void {

  this.asignacionService
    .reactivar(asignacion.id!)
    .subscribe({

      next: () => {

        this.listar();
      },

      error: err => {

        alert(
          err.error?.message ??
          'Error al reactivar la asignación'
        );
      }
    });
}
}