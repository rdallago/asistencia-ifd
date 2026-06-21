import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Asignacion,
  AsignacionRequest
} from '../pages/asignaciones/asignaciones';

@Injectable({
  providedIn: 'root'
})
export class AsignacionService {

  private api = 'http://localhost:8080/asignaciones';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Asignacion[]> {
    return this.http.get<Asignacion[]>(this.api);
  }

  create(
    request: AsignacionRequest
  ): Observable<Asignacion> {

    return this.http.post<Asignacion>(
      this.api,
      request
    );
  }

desasignar(id: number) {

  return this.http.put(
    `${this.api}/${id}/desasignar`,
    {}
  );
}
getActivas(): Observable<Asignacion[]> {

  return this.http.get<Asignacion[]>(
    `${this.api}/activas`
  );
}
reactivar(id: number) {

  return this.http.put(
    `${this.api}/${id}/reactivar`,
    {}
  );
}
}