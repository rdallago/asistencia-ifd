import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
@Injectable({
  providedIn: 'root'
})
export class AuthService {
    
    constructor(
    private router: Router
  ) {}

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
getUsername(): string {

  const token = localStorage.getItem('token');

  if (!token) {
    return '';
  }

  const payload = JSON.parse(
    atob(token.split('.')[1])
  );

  return payload.sub;
}
  logout(): void {

    localStorage.removeItem('token');

    this.router.navigate(['/login']);
  }
}