import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule} from '@angular/router';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,FormsModule
  ],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css'
})
export class MainLayout {

  usuario = localStorage.getItem('usuario');

  rol = localStorage.getItem('rol');

  menuAbierto = true;

  constructor(
    private router: Router
  ) {}

  esAdmin(): boolean {
    return this.rol === 'ADMIN';
  }

  esSecretario(): boolean {
    return this.rol === 'SECRETARIO';
  }



  cerrarSesion(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    localStorage.removeItem('rol');

    this.router.navigate(['/login']);
  }
  mostrarMenuUsuario = false;
  
  mostrarCambioClave = false;

  passwordActual = '';

  passwordNueva = '';

  confirmarPassword = '';

toggleMenu(): void {
  this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  
}

abrirCambioClave(): void {

  this.mostrarMenuUsuario = false;

  this.mostrarCambioClave = true;

}
cancelarCambioClave(): void {

  this.mostrarCambioClave = false;

  this.passwordActual = '';
  this.passwordNueva = '';
  this.confirmarPassword = '';

}
cambiarPassword(): void {

  if (this.passwordNueva !== this.confirmarPassword) {

    alert('Las contraseñas no coinciden');

    return;

  }

  alert('Luego conectaremos este formulario con el backend.');

  this.cancelarCambioClave();

}
}