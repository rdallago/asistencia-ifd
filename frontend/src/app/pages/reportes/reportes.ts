import { finalize } from 'rxjs';
import { Component,ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AsistenciaService } from '../../services/asistencia.service';
import { RankingAusencias } from '../../modelos/ranking-ausencias';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './reportes.html',
  styleUrls: ['./reportes.css']
})
export class ReportesComponent {

  pestania: string = 'diario';
  rankingAusencias: RankingAusencias[] = [];

desdeRanking = '';
hastaRanking = '';
periodoRanking: string = 'personalizado';
cargandoRanking = false;
  
  fecha = '';

turnoId: number | null = null;

docenteId: number | null = null;

estado = '';

constructor(
  private asistenciaService: AsistenciaService,private cdr: ChangeDetectorRef
) { }

ngOnInit(): void {

  const hoy = new Date();

  hoy.setMinutes(
      hoy.getMinutes() - hoy.getTimezoneOffset()
  );

  this.fecha =
      hoy.toISOString().split('T')[0];

}

generarReporteDiario(): void {

  this.asistenciaService
      .exportarPdf(
          this.fecha,
          this.turnoId,
          this.docenteId,
          this.estado
      )
      .subscribe({

        next: (pdf: Blob) => {

          const url =
              window.URL.createObjectURL(pdf);

          const a =
              document.createElement('a');

          a.href = url;

          a.download =
              `Reporte_Diario_${this.fecha}.pdf`;

          a.click();

          window.URL.revokeObjectURL(url);

        },

        error: err => {

          console.error(err);

          alert(
            'No fue posible generar el reporte diario.'
          );

        }

      });

}

generarReporteEjecutivo(): void {

  this.asistenciaService
      .exportarReporteEjecutivo(this.fecha)
      .subscribe({

        next: (pdf: Blob) => {

          const url =
              window.URL.createObjectURL(pdf);

          const a =
              document.createElement('a');

          a.href = url;

          a.download =
              `Reporte_Ejecutivo_${this.fecha}.pdf`;

          a.click();

          window.URL.revokeObjectURL(url);

        },

        error: err => {

          console.error(err);

          alert(
            'No fue posible generar el reporte ejecutivo.'
          );

        }

      });

}
probarReporteDocentes(): void {

  this.asistenciaService
    .obtenerDocentesParaReporte()
    .subscribe({

      next: docentes => {

        console.log(
          'DOCENTES RECIBIDOS PARA REPORTE:',
          docentes
        );

      },

      error: err => {

        console.error(
          'ERROR REPORTE DOCENTES:',
          err
        );

      }

    });

}
generarReporteDocentes(): void {

  this.asistenciaService
    .exportarReporteDocentes()
    .subscribe({

      next: (pdf: Blob) => {

        const url =
          window.URL.createObjectURL(pdf);

        const a =
          document.createElement('a');

        a.href = url;

        a.download =
          'Reporte_Docentes.pdf';

        a.click();

        window.URL.revokeObjectURL(url);

      },

      error: err => {

        console.error(err);

        alert(
          'No fue posible generar el reporte de docentes.'
        );

      }

    });

}
generarReporteTurnos(): void {

  this.asistenciaService
    .exportarReporteTurnos()
    .subscribe({

      next: (pdf: Blob) => {

        const url =
          window.URL.createObjectURL(pdf);

        const a =
          document.createElement('a');

        a.href = url;

        a.download =
          'Reporte_Turnos.pdf';

        a.click();

        window.URL.revokeObjectURL(url);
      },

      error: err => {

        console.error(err);

        alert(
          'No fue posible generar el reporte de turnos.'
        );
      }

    });
}
obtenerRankingAusencias(): void {

  if (!this.desdeRanking || !this.hastaRanking) {

    alert('Seleccione el período del ranking.');

    return;
  }

  if (this.desdeRanking > this.hastaRanking) {

    alert(
      'La fecha desde no puede ser posterior a la fecha hasta.'
    );

    return;
  }

  this.cargandoRanking = true;

  this.rankingAusencias = [];

  this.cdr.detectChanges();

  this.asistenciaService
    .obtenerRankingAusencias(
      this.desdeRanking,
      this.hastaRanking
    )
    .subscribe({

      next: ranking => {

        console.log(
          'RANKING RECIBIDO:',
          ranking
        );

        this.rankingAusencias = ranking;

        this.cargandoRanking = false;

        console.log(
          'FINALIZÓ petición ranking - cargandoRanking:',
          this.cargandoRanking
        );

        console.log(
          'RANKING FINAL:',
          this.rankingAusencias
        );

        this.cdr.detectChanges();

      },

      error: err => {

        console.error(
          'Error obteniendo ranking de ausencias:',
          err
        );

        this.cargandoRanking = false;

        this.cdr.detectChanges();

        alert(
          'No fue posible obtener el ranking de ausencias.'
        );

      }

    });

}
cambiarPeriodoRanking(): void {

  const hoy = new Date();

  hoy.setMinutes(
    hoy.getMinutes() - hoy.getTimezoneOffset()
  );

  const fechaActual =
    hoy.toISOString().split('T')[0];

  // ==========================================
  // PERSONALIZADO
  // ==========================================

  if (this.periodoRanking === 'personalizado') {

    this.desdeRanking = '';
    this.hastaRanking = '';

    return;
  }


  // ==========================================
  // ESTE MES
  // ==========================================

  if (this.periodoRanking === 'mes') {

    const anio = hoy.getFullYear();

    const mes = hoy.getMonth();

    const primerDia =
      new Date(anio, mes, 1);

    const ultimoDia =
      new Date(anio, mes + 1, 0);


    primerDia.setMinutes(
      primerDia.getMinutes() -
      primerDia.getTimezoneOffset()
    );

    ultimoDia.setMinutes(
      ultimoDia.getMinutes() -
      ultimoDia.getTimezoneOffset()
    );


    this.desdeRanking =
      primerDia.toISOString().split('T')[0];

    this.hastaRanking =
      ultimoDia.toISOString().split('T')[0];

    return;
  }


  // ==========================================
  // ESTE AÑO
  // ==========================================

  if (this.periodoRanking === 'anio') {

    const anio = hoy.getFullYear();

    this.desdeRanking =
      `${anio}-01-01`;

    this.hastaRanking =
      `${anio}-12-31`;

    return;
  }

}
}