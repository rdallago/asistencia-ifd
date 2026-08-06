import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Auditoria {

  id: number;

  usuario: string;

  fechaHora: string;

  modulo: string;

  accion: string;

  resultado: string;

  archivo: string;

  detalle: string;

}

@Injectable({
  providedIn: 'root'
})
export class AuditoriaService {

  private api = 'http://localhost:8080/api/auditoria';

  constructor(private http: HttpClient) {}

  listar(): Observable<Auditoria[]> {

    return this.http.get<Auditoria[]>(this.api);

  }

  listarPorModulo(
      modulo: string): Observable<Auditoria[]> {

    return this.http.get<Auditoria[]>(
      `${this.api}/modulo/${modulo}`
    );

  }
listarAuditoriaRespaldos() {

  return this.http.get<any[]>(
    'http://localhost:8080/api/auditoria/respaldos'
  );

}
}