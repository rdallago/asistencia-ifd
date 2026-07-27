import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';

import { EstadisticasTurno } from '../../modelos/estadisticas-turno';

import { ResumenEjecutivo } from '../../modelos/resumen-ejecutivo';

import { EstadoGeneralJornada } from '../../modelos/estado-general-jornada';

import { ChangeDetectorRef, NgZone } from '@angular/core';

import { FormsModule } from '@angular/forms';

import { EvolucionAsistencia } from '../../modelos/evolucion-asistencia';

import { AsistenciaService } from '../../services/asistencia.service';

import { Estadisticas } from '../../modelos/estadisticas';

import { IndicadoresInstitucionales } from '../../modelos/indicadores-institucionales';

import { ComparacionHistorica } from '../../modelos/comparacion-historica';

import {AfterViewInit,ViewChild,ElementRef} from '@angular/core';

import {Chart,registerables} from 'chart.js';


@Component({
  selector: 'app-indicadores',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './indicadores.component.html',
  styleUrls: ['./indicadores.component.css']
})
export class IndicadoresComponent implements OnInit, AfterViewInit {

turnoSeleccionado = 2;

turnos = [
  { id: 1, nombre: 'Turno Tarde' },
  { id: 2, nombre: 'Turno Noche' },
  { id: 3, nombre: 'Turno Mañana' }
];

evolucion: EvolucionAsistencia[] = [];
comparacionHistorica: ComparacionHistorica = {

  promedioHistorico: 0,

  valorActual: 0,

  diferencia: 0,

  tendencia: ''

};
estadoGeneral: EstadoGeneralJornada = {

  puntaje: 0,

  estado: '',

  descripcion: '',

  registrosCompletos: 0,

  registrosIniciados: 0

};

resumenEjecutivo: ResumenEjecutivo = {

  estadoGeneral: '',

  tendencias: [],

  observaciones: [],

  recomendaciones: [],

  // Se mantiene temporalmente
  conclusiones: []

};
maxPresentes = 0;
fechaMaxPresentes = '';

maxSinEgreso = 0;
fechaMaxSinEgreso = '';

maxSinFirma = 0;
fechaMaxSinFirma = '';
resumenEvolucion = {

  maxPresentes: 0,
  fechaPresentes: '',

  maxSinEgreso: 0,
  fechaSinEgreso: '',

  maxSinFirma: 0,
  fechaSinFirma: ''

};

fecha='';
  estadisticas: Estadisticas = {
    presentes: 0,
    sinEgreso: 0,
    sinFirmaTotal: 0,
    actividadRegistrada: 0,
    totalDocentes: 0,
    porcentajeAsistencia: 0,
    docentesRegistrados: 0,
    docentesActivos: 0,
    jornadasAbiertas: 0,
    turnosConfigurados: 0,
    asignacionesVigentes: 0
  };
  
  estadisticasTurno: EstadisticasTurno = {

    turno: '',

    docentesAsignados: 0,

    presentes: 0,

    sinEgreso: 0,

    sinFirmaTotal: 0,

    porcentajeAsistencia: 0

};

  indicadores: IndicadoresInstitucionales = {
    docentesRegistrados: 0,
    docentesActivos: 0,
    turnosConfigurados: 0,
    asignacionesVigentes: 0,
    jornadasAbiertas: 0
  };
  
  @ViewChild('graficoAsistencia')
graficoCanvas!: ElementRef<HTMLCanvasElement>;

private grafico!: Chart;

@ViewChild('graficoInstitucional')
graficoInstitucionalCanvas!: ElementRef<HTMLCanvasElement>;

private graficoInstitucional!: Chart;

  constructor(
    private asistenciaService: AsistenciaService,
      private cdr: ChangeDetectorRef,
  private zone: NgZone
  ) {
     
      }

  ngOnInit(): void {

    const hoy = new Date();

    hoy.setMinutes(
        hoy.getMinutes() - hoy.getTimezoneOffset()
    );

    this.fecha = hoy.toISOString().split('T')[0];

}

  cargarIndicadores(): void {

  //====================================
  // Indicadores institucionales (actual)
  //====================================

  this.asistenciaService
    .obtenerEstadisticas(this.fecha)
    .subscribe(data => {

      this.zone.run(() => {

        this.estadisticas = data;

        if (this.grafico) {

          this.grafico.data.datasets[0].data = [

            data.presentes,

            data.sinEgreso,

            data.sinFirmaTotal

          ];

          this.grafico.update();

        }

        this.cdr.detectChanges();

      });

    });

  //====================================
  // Indicadores por turno (nuevo)
  //====================================

  this.asistenciaService
    .obtenerEstadisticasTurno(
        this.fecha,
        this.turnoSeleccionado
    )
    .subscribe(res => {

      this.estadisticasTurno = res;

    });

}

  cargarIndicadoresInstitucionales(): void {

    this.asistenciaService
  .obtenerIndicadoresInstitucionales()
  .subscribe(data => {

    this.zone.run(() => {

      this.indicadores = data;
      
      if (this.graficoInstitucional) {

    this.graficoInstitucional.data.datasets[0].data = [

        data.docentesRegistrados,

        data.docentesActivos,

        data.turnosConfigurados,

        data.jornadasAbiertas,

        data.asignacionesVigentes

    ];

    this.graficoInstitucional.update();

}

      this.cdr.detectChanges();

 
    });

  });

  }

actualizarDashboard(): void {

  this.cargarIndicadores();

  this.cargarIndicadoresInstitucionales();

  this.cargarEvolucion();

  this.cargarResumenEjecutivo();

  this.cargarEstadoGeneral();
  
  this.cargarComparacionHistorica();

}

ngAfterViewInit(): void {

    Chart.register(...registerables);

    this.crearGrafico();

    this.crearGraficoInstitucional();

    // Una vez creados los gráficos, cargar los datos
    this.actualizarDashboard();

}

private crearGrafico(): void {

  this.grafico = new Chart(
    this.graficoCanvas.nativeElement,
    {
      type: 'doughnut',

      data: {

        labels: [

          'Presentes',

          'Sin egreso',

          'Sin firma total'

        ],

        datasets: [

          {

            data: [1, 1, 1],

            backgroundColor: [

              '#198754',

              '#ffc107',

              '#dc3545'

            ],

            borderWidth: 1

          }

        ]

      },

      options: {

    responsive: true,

    maintainAspectRatio: true,

        plugins: {

          legend: {

            position: 'bottom'

          }

        }

      }

    }

  );

}
private crearGraficoInstitucional(): void {

  this.graficoInstitucional = new Chart(
    this.graficoInstitucionalCanvas.nativeElement,
    {

      type: 'line',

      data: {

  labels: [],

  datasets: [

    {

      label: '% Asistencia',

      data: [],

      borderColor: '#0d6efd',

      backgroundColor: 'rgba(13,110,253,0.15)',

      fill: true,

      tension: 0.3

    }

  ]

},

      options: {

        responsive: true,

        maintainAspectRatio: false,

        plugins: {

    title: {

        display: true,

        text: 'Evolución diaria de incidencias de asistencia',

        font: {

            size: 18

        }

    },

    subtitle: {

        display: true,

        text: 'Últimos 30 días registrados'

    },

    legend: {

        position: 'bottom'

    }

},

 scales: {

    x: {

        title: {

            display: true,

            text: 'Fecha'

        }

    },

    y: {

        beginAtZero: true,

        title: {

            display: true,

            text: 'Cantidad de docentes'

        },

        ticks: {

            precision: 0

        }

    }

}

      }

    }

  );

}
cargarEvolucion(): void {

  this.asistenciaService
      .obtenerEvolucion()
      .subscribe({

          next: data => {

              // ==========================================
// Resumen automático
// ==========================================

this.resumenEvolucion = {

  maxPresentes: 0,
  fechaPresentes: '',

  maxSinEgreso: 0,
  fechaSinEgreso: '',

  maxSinFirma: 0,
  fechaSinFirma: ''

};

for (const dia of data) {

  if (dia.presentes > this.resumenEvolucion.maxPresentes) {

    this.resumenEvolucion.maxPresentes = dia.presentes;

    this.resumenEvolucion.fechaPresentes = dia.fecha;

  }

  if (dia.sinEgreso > this.resumenEvolucion.maxSinEgreso) {

    this.resumenEvolucion.maxSinEgreso = dia.sinEgreso;

    this.resumenEvolucion.fechaSinEgreso = dia.fecha;

  }

  if (dia.sinFirmaTotal > this.resumenEvolucion.maxSinFirma) {

    this.resumenEvolucion.maxSinFirma = dia.sinFirmaTotal;

    this.resumenEvolucion.fechaSinFirma = dia.fecha;

  }

}

              this.evolucion = data;
              
              const mayorPresente = data.reduce((a, b) =>
    a.presentes >= b.presentes ? a : b
);

this.maxPresentes = mayorPresente.presentes;
this.fechaMaxPresentes = mayorPresente.fecha;

const mayorSinEgreso = data.reduce((a, b) =>
    a.sinEgreso >= b.sinEgreso ? a : b
);

this.maxSinEgreso = mayorSinEgreso.sinEgreso;
this.fechaMaxSinEgreso = mayorSinEgreso.fecha;

const mayorSinFirma = data.reduce((a, b) =>
    a.sinFirmaTotal >= b.sinFirmaTotal ? a : b
);

this.maxSinFirma = mayorSinFirma.sinFirmaTotal;
this.fechaMaxSinFirma = mayorSinFirma.fecha;

              if (!this.graficoInstitucional) {
                  return;
              }

              this.graficoInstitucional.data.labels =
                  data.map(d => d.fecha.substring(5));

              this.graficoInstitucional.data.datasets = [

                  {
                      label: 'Presentes',
                      data: data.map(d => d.presentes),
                      borderColor: '#198754',
                      backgroundColor: 'rgba(25,135,84,0.15)',
                      tension: 0.3
                  },

                  {
                      label: 'Sin egreso',
                      data: data.map(d => d.sinEgreso),
                      borderColor: '#ffc107',
                      backgroundColor: 'rgba(255,193,7,0.15)',
                      tension: 0.3
                  },

                  {
                      label: 'Sin firma total',
                      data: data.map(d => d.sinFirmaTotal),
                      borderColor: '#dc3545',
                      backgroundColor: 'rgba(220,53,69,0.15)',
                      tension: 0.3
                  }

              ];

              this.graficoInstitucional.update();

          },

          error: err => console.error(err)

      });

}
cargarResumenEjecutivo(): void {

 this.asistenciaService
    .obtenerResumenEjecutivo(this.fecha)
      .subscribe({

          next: data => {

              console.log("RESUMEN EJECUTIVO", data);

              this.resumenEjecutivo = data;

          },

          error: err => {

              console.error(err);

          }

      });

}
cargarEstadoGeneral(): void {

  this.asistenciaService
      .obtenerEstadoGeneral(this.fecha)
      .subscribe({

        next: res => {

          this.estadoGeneral = {
            ...res
          };

          this.cdr.detectChanges();

        },

        error: err => console.error(err)

      });

}

obtenerClaseEstado(): string {

  switch (this.estadoGeneral.estado) {

    case 'EXCELENTE':
      return 'estado-excelente';

    case 'BUENO':
      return 'estado-bueno';

    case 'REGULAR':
      return 'estado-regular';

    case 'CRÍTICO':
      return 'estado-critico';

    default:
      return '';

  }

}
cargarComparacionHistorica(): void {

  this.asistenciaService
    .obtenerComparacionHistorica(this.fecha)
    .subscribe({

      next: res => {

        this.comparacionHistorica = {
          ...res
        };

      },

      error: err => console.error(err)

    });

}
exportarReportePdf(): void {

  this.asistenciaService
      .exportarReporteEjecutivo(this.fecha)
      .subscribe({

        next: (pdf: Blob) => {

          const url = window.URL.createObjectURL(pdf);

          const a = document.createElement('a');

          a.href = url;

          a.download =
              `Reporte_Ejecutivo_${this.fecha}.pdf`;

          a.click();

          window.URL.revokeObjectURL(url);

        },

        error: err => console.error(err)

      });

}
}