import { Injectable, inject } from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Observable
} from 'rxjs';

import {
  environment
} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DocenteService {

  private http =
      inject(HttpClient);

  listar(): Observable<any> {

    return this.http.get(
      `${environment.apiUrl}/docentes`
    );
  }
  guardar(docente: any): Observable<any> {

  return this.http.post(
    `${environment.apiUrl}/docentes`,
    docente
  );
}
  actualizar(docente: any): Observable<any> {

    return this.http.put(
      `${environment.apiUrl}/docentes/${docente.id}`,
      docente
    );
  }
  eliminar(id: number): Observable<any> {

  return this.http.delete(
    `${environment.apiUrl}/docentes/${id}`
  );
}
}