import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
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

toggleMenu(): void {
  this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  
}


abrirCambioClave(): void {
  // Podés abrir un modal o navegar a pantalla de perfil
  console.log('Abrir cambio de contraseña');
}
}