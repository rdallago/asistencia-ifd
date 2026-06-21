import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Turno } from '../pages/turnos/turnos';

@Injectable({
  providedIn: 'root'
})
export class TurnoService {

  private api = 'http://localhost:8080/turnos';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Turno[]> {
    return this.http.get<Turno[]>(this.api);
  }

  create(turno: Turno): Observable<Turno> {
    return this.http.post<Turno>(this.api, turno);
  }

  update(turno: Turno): Observable<Turno> {
    return this.http.put<Turno>(
      `${this.api}/${turno.id}`,
      turno
    );
  }

  desactivar(id: number): Observable<any> {
    return this.http.put(
      `${this.api}/${id}/desactivar`,
      {}
    );
  }

  activar(id: number): Observable<any> {
    return this.http.put(
      `${this.api}/${id}/activar`,
      {}
    );
  }
}