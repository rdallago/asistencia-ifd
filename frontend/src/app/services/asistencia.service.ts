import { Injectable } from '@angular/core';
import { IndicadoresInstitucionales } from '../modelos/indicadores-institucionales';
import { EvolucionAsistencia } from '../modelos/evolucion-asistencia';
import { ResumenEjecutivo } from '../modelos/resumen-ejecutivo';
import { ComparacionHistorica } from '../modelos/comparacion-historica';
import { EstadoGeneralJornada } from '../modelos/estado-general-jornada';
import { EstadisticasTurno } from '../modelos/estadisticas-turno';
import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

import { Estadisticas } from '../modelos/estadisticas';


@Injectable({
  providedIn: 'root'
})
export class AsistenciaService {

  private api =
    'http://localhost:8080/api/asistencia';

  constructor(
    private http: HttpClient
  ) {}

 registrarEntrada(
  dni: string,
  jornadaId: number
): Observable<any> {

  return this.http.post(
    `${this.api}/entrada`,
    {
      dni,
      jornadaId
    },
    {
      responseType: 'text'
    }
  );
}

registrarSalida(
  dni: string,
  jornadaId: number
): Observable<any> {

  return this.http.post(
    `${this.api}/salida`,
    {
      dni,
      jornadaId
    },
    {
      responseType: 'text'
    }
  );
} 
 
 
  reporteHoy(): Observable<any[]> {

    const hoy =
      new Date()
        .toISOString()
        .split('T')[0];

    return this.http.get<any[]>(
      `${this.api}/reporte?fecha=${hoy}`
    );
  }
  obtenerJornadasAbiertas() {

  return this.http.get<any[]>(
    'http://localhost:8080/api/jornadas/abiertas'
  );
}
reporte(
  fecha: string,
  turnoId: number | null,
  docenteId: number | null,
  estado: string
): Observable<any[]> {

  let url =
    `${this.api}/reporte?fecha=${fecha}`;

if (turnoId != null) {

    url += `&turnoId=${turnoId}`;

}

if (docenteId != null) {

    url += `&docenteId=${docenteId}`;

}

  if (estado && estado.trim() !== '') {

    url += `&estado=${estado}`;
  }

  return this.http.get<any[]>(url);
}
exportarExcel(

    fecha: string,

    turnoId: number | null,

    docenteId: number | null,

    estado: string

){

    let url =
      `${this.api}/excel?fecha=${fecha}`;

    if(turnoId){

        url += `&turnoId=${turnoId}`;

    }

    if(docenteId){

        url += `&docenteId=${docenteId}`;

    }

    if(estado){

        url += `&estado=${estado}`;

    }

    return this.http.get(url,{

        responseType:'blob'

    });

}
exportarPdf(

  fecha: string,

  turnoId: number | null,

  docenteId: number | null,

  estado: string

): Observable<Blob> {

  let url =
    `${this.api}/pdf?fecha=${fecha}`;

  if (turnoId != null) {

    url += `&turnoId=${turnoId}`;

  }

  if (docenteId != null) {

    url += `&docenteId=${docenteId}`;

  }

  if (estado) {

    url += `&estado=${estado}`;

  }

  return this.http.get(

    url,

    {

      responseType: 'blob'

    }

  );

}
obtenerEstadisticas(

  fecha: string

): Observable<Estadisticas> {

  return this.http.get<Estadisticas>(

    `${this.api}/estadisticas?fecha=${fecha}`

  );

}
obtenerIndicadoresInstitucionales()
: Observable<IndicadoresInstitucionales> {

    return this.http.get<IndicadoresInstitucionales>(

        `${this.api}/indicadores`

    );

}
obtenerEvolucion(): Observable<EvolucionAsistencia[]> {

  return this.http.get<EvolucionAsistencia[]>(

    `${this.api}/evolucion`

  );

}
obtenerResumenEjecutivo(
  fecha: string
): Observable<ResumenEjecutivo> {

  return this.http.get<ResumenEjecutivo>(

      `${this.api}/resumen-ejecutivo?fecha=${fecha}`

  );

}
obtenerEstadoGeneral(fecha: string): Observable<EstadoGeneralJornada> {

  return this.http.get<EstadoGeneralJornada>(

    `${this.api}/estado-general?fecha=${fecha}`

  );

}

obtenerComparacionHistorica(
  fecha: string
): Observable<ComparacionHistorica> {

  return this.http.get<ComparacionHistorica>(

    `${this.api}/comparacion-historica?fecha=${fecha}`

  );

}
exportarReporteEjecutivo(fecha: string): Observable<Blob> {

  return this.http.get(

    `http://localhost:8080/api/reportes/ejecutivo?fecha=${fecha}`,

    {

      responseType: 'blob'

    }

  );

}
obtenerEstadisticasTurno(
    fecha: string,
    turnoId: number) {

  return this.http.get<EstadisticasTurno>(

      `${this.api}/estadisticas-turno`,

      {
        params: {
          fecha,
          turnoId
        }
      });

}

}