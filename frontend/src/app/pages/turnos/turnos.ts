import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TurnoService } from '../../services/turno.service';

export interface Turno {
  id?: number;
  institucionId: number;
  nombre: string;
  horaInicio: string;
  horaFin: string;
  activo: boolean;
}

@Component({
  selector: 'app-turnos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './turnos.html',
  styleUrls: ['./turnos.css'],
})
export class TurnosComponent {

  turnos: Turno[] = [];
  turnosFiltrados: Turno[] = [];

  turno: Turno = {
    institucionId: 1,
    nombre: '',
    horaInicio: '',
    horaFin: '',
    activo: true
  };

  mostrarFormulario = false;
  modoEdicion = false;
  filtroBusqueda = '';

  constructor(private turnoService: TurnoService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
       console.log('ngOnInit Turnos');
    this.listar();
  }

listar(): void {

  console.log('Ejecutando listar()');

  this.turnoService.getAll().subscribe({

    next: data => {

      console.log('Datos recibidos:', data);

      this.turnos = [...data];
      this.turnosFiltrados = [...data];
        this.cdr.detectChanges();
    },

    error: err => {

      console.error('Error al obtener turnos:', err);
    }

  });
}

  filtrar(): void {
    this.turnosFiltrados = this.turnos.filter(t =>
      t.nombre.toLowerCase().includes(this.filtroBusqueda.toLowerCase())
    );
  }

  abrirFormulario(): void {
    this.modoEdicion = false;

    this.turno = {
      institucionId: 1,
      nombre: '',
      horaInicio: '',
      horaFin: '',
      activo: true
    };

    this.mostrarFormulario = true;
  }

  editar(turno: Turno): void {
    this.modoEdicion = true;
    this.turno = { ...turno };
    this.mostrarFormulario = true;
  }

guardar(): void {

  const operacion = this.modoEdicion
    ? this.turnoService.update(this.turno)
    : this.turnoService.create(this.turno);

  operacion.subscribe({

    next: () => {
      this.listar();
      this.mostrarFormulario = false;
    },

    error: (err) => {

      const mensaje =
        err?.error?.message ||
        err?.error ||
        'Error al guardar el turno, no se puede desactivar un turno con jornadas asociadas';

      alert(mensaje);
    }
  });
}

 
desactivar(turno: Turno): void {
  this.turnoService.desactivar(turno.id!).subscribe({
    next: () => this.listar(),
    error: (err) => {

  const mensaje =
    err?.error?.message ||
    err?.message ||
    'No fue posible desactivar el turno porque posee jornadas asociadas';

  alert(mensaje);
}
  });
}
activar(turno: Turno): void {

  this.turnoService.activar(turno.id!).subscribe({
    next: () => this.listar(),
    error: (err) => alert(err.error.message)
  });

}
}