import {
  Component,
  OnInit
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  Router
} from '@angular/router';

import {
  AsistenciaService
} from '../../services/asistencia.service';

import {
  JornadaService
} from '../../services/jornada.service';

import {
  TurnoService
} from '../../services/turno.service';

import { DocenteService } from '../../services/docente.service';
@Component({
  selector: 'app-asistencia',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './asistencia.html',
  styleUrls: ['./asistencia.css']
})
export class AsistenciaComponent
implements OnInit {

  fecha = '';

  turnoId: number | null = null;

  turnos: any[] = [];

  jornadasAbiertas: any[] = [];

  reporte: any[] = [];
  estadisticas = {

  presentes: 0,

  sinEgreso: 0,

  sinFirmaTotal: 0,

  total: 0

};
  
  docentes:any[] = [];

  docenteId:number | null = null;
  estado = '';

  constructor(
    private asistenciaService: AsistenciaService,
    private jornadaService: JornadaService,
    private turnoService: TurnoService,
    private router: Router,
    private docenteService: DocenteService
  ) {}

  ngOnInit(): void {

    this.fecha =
      new Date()
        .toISOString()
        .split('T')[0];

    this.cargarTurnos();

    this.cargarJornadasAbiertas();
    
    this.docenteService
  .getAll()
  .subscribe(data=>{
      this.docentes = data;
  });
  }

  cargarTurnos(): void {

    this.turnoService
      .getAll()
      .subscribe(data => {

        this.turnos = data;
      });
  }

  cargarJornadasAbiertas(): void {

    this.jornadaService
      .getAbiertas()
      .subscribe(data => {

        this.jornadasAbiertas = data;
      });
  }

  buscarReporte(){
       console.log("ANTES",
        this.fecha,
        this.turnoId,
        this.docenteId,
        this.estado
    );

    this.asistenciaService
        .reporte(

            this.fecha,

            this.turnoId,

            this.docenteId,

            this.estado

        )
        .subscribe({

            next:data=>{

                this.reporte=data;

            },

            error:()=>{

                alert("Error al consultar reporte");

            }

        });

}

  abrirMarcador(
    jornadaId: number
  ): void {

    window.open(
      `/marcador/${jornadaId}`,
      '_blank'
    );
  }
 buscar(){

    this.asistenciaService
        .reporte(

            this.fecha,

            this.turnoId,

            this.docenteId,

            this.estado

        )
        .subscribe({

            next:data=>{

                this.reporte=data;

            },

            error:()=>{

                alert("Error al consultar reporte");

            }

        });

}
  limpiarFiltros(){

    this.fecha =
        new Date()
        .toISOString()
        .split('T')[0];

    this.turnoId=null;

    this.docenteId=null;

    this.estado="";

    this.buscarReporte();

}
exportarExcel(){

    this.asistenciaService

        .exportarExcel(

            this.fecha,

            this.turnoId,

            this.docenteId,

            this.estado

        )

        .subscribe(blob=>{

            const archivo = window.URL.createObjectURL(blob);

            const a = document.createElement('a');

            a.href = archivo;

            a.download = 'Reporte_Asistencias.xlsx';

            a.click();

            window.URL.revokeObjectURL(archivo);

        });

}
exportarPdf() {

  this.asistenciaService

      .exportarPdf(

          this.fecha,

          this.turnoId,

          this.docenteId,

          this.estado

      )

      .subscribe({

          next: (blob) => {

              const url =
                  window.URL.createObjectURL(blob);

              window.open(url, '_blank');

          },

          error: (err) => {

              console.error(err);

              alert("No fue posible generar el PDF.");

          }

      });

}
}