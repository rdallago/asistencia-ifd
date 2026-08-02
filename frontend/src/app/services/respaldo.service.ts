import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

export interface AuditoriaRespaldo {

  id: number;

  usuario: string;

  fechaHora: string;

  operacion: string;

  resultado: string;

  archivo: string;

  detalle: string;

}

@Injectable({
  providedIn: 'root'
})
export class RespaldoService {

  private api = 'http://localhost:8080/api/respaldos';

  constructor(private http: HttpClient) {}

  generarBackup(): Observable<HttpResponse<Blob>> {

    return this.http.post(
        `${this.api}/generar`,
        {},
        {
            responseType: 'blob',
            observe: 'response'
        }
    );

}

  restaurarBackup(file: File): Observable<string> {

    const formData = new FormData();

    formData.append('archivo', file);

    return this.http.post(
        `${this.api}/restaurar`,
        formData,
        {
            responseType: 'text'
        }
    );

}

obtenerAuditoria(): Observable<AuditoriaRespaldo[]> {

    return this.http.get<AuditoriaRespaldo[]>(
      `${this.api}/auditoria`
    );

}
}