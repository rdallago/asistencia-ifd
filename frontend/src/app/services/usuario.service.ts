import { Injectable } from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import { Observable } from 'rxjs';

import { Usuario }
  from '../pages/usuarios/usuarios';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private api =
    'http://localhost:8080/api/usuarios';

  constructor(
    private http: HttpClient
  ) {}

  getAll(): Observable<Usuario[]> {

    return this.http.get<Usuario[]>(
      this.api
    );
  }

  crear(
    usuario: any
  ): Observable<Usuario> {

    return this.http.post<Usuario>(
      this.api,
      usuario
    );
  }

  actualizar(
    id: number,
    usuario: any
  ): Observable<Usuario> {

    return this.http.put<Usuario>(
      `${this.api}/${id}`,
      usuario
    );
  }

 desactivar(
  id: number
): Observable<any> {

  return this.http.delete(
    `${this.api}/${id}`,
    {
      responseType: 'text'
    }
  );
}
activar(
  id: number
): Observable<any> {

  return this.http.put(
    `${this.api}/${id}/activar`,
    {},
    {
      responseType: 'text'
    }
  );
}
}