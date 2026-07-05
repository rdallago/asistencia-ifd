import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';

import { ChangeDetectorRef, NgZone } from '@angular/core';

import { FormsModule } from '@angular/forms';

import { AsistenciaService } from '../../services/asistencia.service';

import { Estadisticas } from '../../modelos/estadisticas';

import { IndicadoresInstitucionales } from '../../modelos/indicadores-institucionales';

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

  //fecha = new Date().toISOString().split('T')[0];
fecha='';
  estadisticas: Estadisticas = {
    presentes: 0,
    sinEgreso: 0,
    sinFirmaTotal: 0,
    totalDocentes: 0,
    porcentajeAsistencia: 0,
    docentesRegistrados: 0,
    docentesActivos: 0,
    jornadasAbiertas: 0,
    turnosConfigurados: 0,
    asignacionesVigentes: 0
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

    setTimeout(() => {

        this.actualizarDashboard();

    }, 0);

}

  cargarIndicadores(): void {

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

    setTimeout(() => {

        this.cargarIndicadores();

        this.cargarIndicadoresInstitucionales();

    }, 100);

}

ngAfterViewInit(): void {

    Chart.register(...registerables);

    this.crearGrafico();
    this.crearGraficoInstitucional();

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

        maintainAspectRatio: false,

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

      type: 'bar',

      data: {

        labels: [

          'Registrados',

          'Activos',

          'Turnos',

          'Jornadas',

          'Asignaciones'

        ],

        datasets: [

          {

            label: 'Cantidad',

            data: [0, 0, 0, 0, 0],

            backgroundColor: [

              '#0d6efd',

              '#198754',

              '#ffc107',

              '#6f42c1',

              '#fd7e14'

            ]

          }

        ]

      },

      options: {

        responsive: true,

        maintainAspectRatio: false,

        plugins: {

          legend: {

            display: false

          }

        },

        scales: {

          y: {

            beginAtZero: true

          }

        }

      }

    }

  );

}
}