import { Component, OnInit } from '@angular/core';

import { CommonModule } from '@angular/common';

import { ChangeDetectorRef, NgZone } from '@angular/core';

import { FormsModule } from '@angular/forms';

import { AsistenciaService } from '../../services/asistencia.service';

import { Estadisticas } from '../../modelos/estadisticas';

import { IndicadoresInstitucionales } from '../../modelos/indicadores-institucionales';

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
export class IndicadoresComponent implements OnInit {

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

    console.log("FECHA:", this.fecha);

    setTimeout(() => {

        this.actualizarDashboard();

    }, 0);

}

  cargarIndicadores(): void {

 console.log("CONSULTANDO FECHA:", this.fecha);

  this.asistenciaService
  .obtenerEstadisticas(this.fecha)
  .subscribe(data => {

    this.zone.run(() => {

      this.estadisticas = data;

      this.cdr.detectChanges();

      console.log("ESTADISTICAS DESPUÉS", this.estadisticas);

    });

  });
}
  cargarIndicadoresInstitucionales(): void {

    this.asistenciaService
  .obtenerIndicadoresInstitucionales()
  .subscribe(data => {

    this.zone.run(() => {

      this.indicadores = data;

      this.cdr.detectChanges();

      console.log("INDICADORES DESPUÉS", this.indicadores);

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

}