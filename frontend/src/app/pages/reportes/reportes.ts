import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AsistenciaService } from '../../services/asistencia.service';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './reportes.html',
  styleUrls: ['./reportes.css']
})
export class ReportesComponent {

  pestania: string = 'diario';
 
  
  fecha = '';

turnoId: number | null = null;

docenteId: number | null = null;

estado = '';

constructor(
  private asistenciaService: AsistenciaService
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
}