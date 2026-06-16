import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  username = '';

  password = '';

  mensaje = '';

constructor(
  private authService: AuthService,
  private router: Router
) {}

  ingresar() {

    this.authService.login(

      this.username,

      this.password

    ).subscribe({

      next: (response) => {

        localStorage.setItem(
          'token',
          response.token
        );
        this.router.navigate(
  ['/']
);

        localStorage.setItem(
          'usuario',
          response.username
        );

        localStorage.setItem(
          'rol',
          response.rol
        );

        this.mensaje =
            'Login correcto';

        console.log(response);
      },

      error: (error) => {

  console.log('STATUS:', error.status);

  console.log('BODY:', error.error);

  this.mensaje = error.error;

  alert(error.error);

  console.error(error);
}

    });
  }
}