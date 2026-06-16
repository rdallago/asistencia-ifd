import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private http = inject(HttpClient);

  login(
    username: string,
    password: string
  ): Observable<any> {

    return this.http.post(
      `${environment.apiUrl}/auth/login`,
      {
        username,
        password
      }
    );
  }

  changePassword(data: any) {
  return this.http.put(
    'http://localhost:8080/api/auth/cambiar-password',
    data
  );
}
}