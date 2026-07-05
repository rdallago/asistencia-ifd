import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class JornadaService {

  private api = 'http://localhost:8080/api/jornadas';

  constructor(
    private http: HttpClient
  ) {}

  getAll(): Observable<any[]> {

    return this.http.get<any[]>(this.api);
  }

  abrir(
    usuarioId: number,
    turnoId: number,
    observaciones: string = ''
  ): Observable<any> {

    return this.http.post(
      `${this.api}/abrir`,

      null,

      {
        params: {
          usuarioId,
          turnoId,
          observaciones
        }
      }
    );
  }

 cerrar(
  usuarioId: number,
  turnoId: number,
  observaciones: string = '',
  forzarCierre: boolean = false
): Observable<any> {

  return this.http.post(

    `${this.api}/cerrar`,

    null,

    {
      params: {
        usuarioId,
        turnoId,
        observaciones,
        forzarCierre
      }
    }
  );
}
abiertas() {

  return this.http.get<any[]>(
    `${this.api}/abiertas`
  );
}
 getAbiertas(): Observable<any[]> {

  return this.http.get<any[]>(
    `${this.api}/abiertas`
  );
}

}