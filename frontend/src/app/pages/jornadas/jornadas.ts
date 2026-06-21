import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Turno } from '../turnos/turnos';
import { JornadaService } from '../../services/jornada.service';
import { TurnoService } from '../../services/turno.service';

export interface Jornada {

  id: number;

  fecha: string;
  turno: {
    id: number;
    nombre: string;
  };

  habilitada: boolean;

  cerrada: boolean;

  fechaApertura?: string;

  fechaCierre?: string;

  observaciones?: string;

  cierreAutomatico?: boolean;

}

@Component({
  selector: 'app-jornadas',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './jornadas.html',
  styleUrls: ['./jornadas.css']
})



export class JornadasComponent
implements OnInit {

  jornadas: Jornada[] = [];
  jornadasFiltradas: Jornada[] = [];

  turnos: Turno[] = [];

  filtroBusqueda = '';
  filtroTurno = 0;

  mostrarFormulario = false;

  nuevaJornada = {

    turnoId: 0,

    observaciones: ''
  };

  usuarioId = 1;

  constructor(
    private jornadaService: JornadaService,
    private turnoService: TurnoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.listar();

    this.turnoService.getAll()
      .subscribe(data => {

        this.turnos = data;
      });
  }

  listar(): void {

    this.jornadaService.getAll()
      .subscribe({

        next: data => {

          this.jornadas = data;

          this.filtrar();

          this.cdr.detectChanges();
        },

        error: () => {

          alert('Error al cargar jornadas');
        }
      });
  }
obtenerEstadoVisual(j: Jornada): string {

  if (!j.cerrada) {
    return 'abierta';
  }

  if (j.cierreAutomatico) {
    return 'cerrada-automatica';
  }

  const turno = this.turnos.find(
    t => t.id === j.turno.id
  );

  if (!turno || !j.fechaCierre) {
    return 'cerrada';
  }

  const [anio, mes, dia] = j.fecha
    .split('-')
    .map(Number);

  const fechaJornada = new Date(
    anio,
    mes - 1,
    dia
  );

  const fechaCierre = new Date(
    j.fechaCierre
  );

  const [h, m] = turno.horaFin
    .split(':')
    .map(Number);

  const horaFin = new Date(
    fechaJornada
  );

  horaFin.setHours(h, m, 0, 0);

  if (fechaCierre < horaFin) {
    return 'cerrada-anticipada';
  }

  const limite = new Date(horaFin);

  limite.setMinutes(
    limite.getMinutes() + 30
  );

  return fechaCierre <= limite
    ? 'cerrada-ok'
    : 'cerrada-demora';
}

obtenerTextoEstado(j: Jornada): string {

  switch (this.obtenerEstadoVisual(j)) {

    case 'abierta':
      return '🟢 Abierta';

    case 'cerrada-anticipada':
      return '🟠 Cerrada anticipadamente';

    case 'cerrada-ok':
      return '✅ Cerrada en horario';

    case 'cerrada-demora':
      return '🟡 Cerrada con demora';

    case 'cerrada-automatica':
      return '🔴 Cerrada automáticamente';

    default:
      return '⚫ Cerrada';
  }
}

  abrirFormulario(): void {

    this.nuevaJornada = {

      turnoId: 0,

      observaciones: ''
    };

    this.mostrarFormulario = true;
  }

  abrirJornada(): void {

    this.jornadaService
      .abrir(
        this.usuarioId,
        this.nuevaJornada.turnoId,
        this.nuevaJornada.observaciones
      )
      .subscribe({

        next: () => {

          this.mostrarFormulario = false;

          this.listar();
        },

        error: err => {

          alert(
            err.error ??
            'Error al abrir jornada'
          );
        }
      });
  }

  cerrarJornada(
  jornada: Jornada
): void {

  if (!confirm(
    `¿Cerrar la jornada del turno ${jornada.turno.nombre}?`
  )) {

    return;
  }

  this.jornadaService
    .cerrar(
      this.usuarioId,
      jornada.turno.id,
      ''
    )
    .subscribe({

      next: () => {

        this.listar();
      },

      error: err => {

        if (err.error === 'CIERRE_ANTICIPADO') {

          const confirmar = confirm(
            'El turno aún no finalizó. ¿Desea cerrar la jornada igualmente?'
          );

          if (confirmar) {

            this.jornadaService
              .cerrar(
                this.usuarioId,
                jornada.turno.id,
                '',
                true
              )
              .subscribe({

                next: () => {

                  this.listar();
                },

                error: err2 => {

                  alert(
                    err2.error ??
                    'Error al cerrar jornada'
                  );
                }
              });
          }

          return;
        }

        alert(
          err.error ??
          'Error al cerrar jornada'
        );
      }
    });
}


  filtrar(): void {

    const texto =
      this.filtroBusqueda.toLowerCase();

    this.jornadasFiltradas =
      this.jornadas.filter(j => {

        const coincideTexto =

          j.turno.nombre
            .toLowerCase()
            .includes(texto);

        const coincideTurno =

          this.filtroTurno === 0 ||

          j.turno.id === this.filtroTurno;

        return coincideTexto
          && coincideTurno;
      });
  }
esAperturaAnticipada(j: Jornada): boolean {

  if (!j.fechaApertura) {
    return false;
  }

  const turno = this.turnos.find(
    t => t.id === j.turno.id
  );

  if (!turno) {
    return false;
  }

  const fechaApertura = new Date(
    j.fechaApertura
  );

  const [anio, mes, dia] = j.fecha
    .split('-')
    .map(Number);

  const fechaJornada = new Date(
    anio,
    mes - 1,
    dia
  );

  const [h, m] = turno.horaInicio
    .split(':')
    .map(Number);

  const horaInicio = new Date(
    fechaJornada
  );

  horaInicio.setHours(h, m, 0, 0);

  return fechaApertura < horaInicio;
}

}