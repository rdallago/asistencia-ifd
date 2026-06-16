import { Component, OnInit,AfterViewInit,ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

import { DocenteService } from '../services/docente.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit,AfterViewInit {
  mostrarMenuUsuario: boolean = false;
  mostrarCambioClave = false;
  passwordActual = '';
  passwordNueva = '';
  confirmarPassword = '';
  
  errores: any = {};
  modoEdicion: boolean = false;
  docenteEditandoId: number | null = null; 
  docentes: any[] = [];
  usuario: string | null =
  localStorage.getItem('usuario');
  filtroBusqueda: string = '';
  docentesFiltrados: any[] = [];

    rol: string | null =
    localStorage.getItem('rol');
mostrarFormulario: boolean = false;
  constructor(
  private docenteService: DocenteService,
  private cd: ChangeDetectorRef,
  private router: Router,
 private authService: AuthService
) {}
  ngOnInit(): void {
    this.mostrarFormulario=false;
    console.log('SE CREÓ DASHBOARD');
    this.resetFormulario();
    this.cargarDocentes();
  }
  
  get botonNuevoHabilitado(): boolean{
      return !this.mostrarFormulario;
  }
  cargarDocentes(): void {

  this.docenteService
    .listar()
    .subscribe({

      next: (datos: any) => {

        console.log('DOCENTES RECIBIDOS:', datos);

        this.docentes = [...datos];

        this.docentesFiltrados = [...datos];

        (window as any).debugDocentes = this.docentes;

        this.cd.detectChanges();

        console.log(
          'TOTAL DOCENTES:',
          this.docentes.length
        );
      },

      error: (error: any) => {

        console.error(
          'ERROR DOCENTES:',
          error
        );
      }
    });
}

  ngAfterViewInit() {

  console.log(
    'HTML VE:',
    this.docentes.length
  );

  setTimeout(() => {

    console.log(
      '1 SEGUNDO DESPUÉS:',
      this.docentes.length
    );

  }, 1000);
}
nuevoDocente = {
  nombre: '',
  apellido: '',
  dni: '',
  telefono: '',
  correo: '',
  fechaNacimiento: '',
  direccion: '',
  cargo: '',
  activo: true
};

abrirFormulario() {
this.errores = {};
  this.mostrarFormulario = true;
}

cancelarCambioClave(): void {

  this.cerrarModalCambioClave();
}


cancelarFormulario() {
     this.errores = {};

  this.mostrarFormulario = false;
    this.modoEdicion = false;
  this.docenteEditandoId = null;
  this.resetFormulario();
}


resetFormulario():void

 { 
 this.errores = {};
 this.nuevoDocente =
 { nombre: '',
 apellido: '',
 dni: '',
 telefono: '',
 correo: '',
 fechaNacimiento: '',
 direccion: '',
 cargo: '',
 activo: true };
 }
 
 
validarFormulario(): boolean {
console.log(this.errores);
  this.errores = {};

  if (!this.nuevoDocente.nombre?.trim()) {

    this.errores.nombre =
      'Ingrese el nombre del docente.';
  }
  else if (this.nuevoDocente.nombre.trim().length < 2) {

    this.errores.nombre =
      'El nombre debe tener al menos 2 caracteres.';
  }

  if (!this.nuevoDocente.apellido?.trim()) {

    this.errores.apellido =
      'Ingrese el apellido del docente.';
  }
  else if (this.nuevoDocente.apellido.trim().length < 2) {

    this.errores.apellido =
      'El apellido debe tener al menos 2 caracteres.';
  }
  if (!this.nuevoDocente.dni?.trim()) {

    this.errores.dni =
      'Ingrese el DNI.';
  }
  else if (!/^\d{7,8}$/.test(this.nuevoDocente.dni)) {

    this.errores.dni =
      'El DNI debe contener entre 7 y 8 números, sin puntos ni espacios.';
  }

if (!this.nuevoDocente.direccion?.trim()) {

  this.errores.direccion =
    'Ingrese la dirección del docente.';
}
else if (
  this.nuevoDocente.direccion.trim().length < 5
) {

  this.errores.direccion =
    'La dirección debe tener al menos 5 caracteres.';
}
else if (
  this.nuevoDocente.direccion.trim().length > 250
) {

  this.errores.direccion =
    'La dirección no puede superar los 250 caracteres.';
}

if (!this.nuevoDocente.cargo?.trim()) {

  this.errores.cargo =
    'Ingrese el cargo del docente.';
}
else if (
  this.nuevoDocente.cargo.trim().length < 3
) {

  this.errores.cargo =
    'El cargo debe tener al menos 3 caracteres.';
}
else if (
  this.nuevoDocente.cargo.trim().length > 150
) {

  this.errores.cargo =
    'El cargo no puede superar los 150 caracteres.';
}
  if (!this.nuevoDocente.fechaNacimiento) {

    this.errores.fechaNacimiento =
      'Seleccione la fecha de nacimiento.';
  }
  else {

    const nacimiento = new Date(
      this.nuevoDocente.fechaNacimiento
    );

    const hoy = new Date();

    let edad =
      hoy.getFullYear() -
      nacimiento.getFullYear();

    const mes =
      hoy.getMonth() -
      nacimiento.getMonth();

    if (
      mes < 0 ||
      (mes === 0 &&
       hoy.getDate() < nacimiento.getDate())
    ) {
      edad--;
    }

    if (edad < 18) {

      this.errores.fechaNacimiento =
        'El docente debe ser mayor de 18 años.';
    }
  }

  if (
    this.nuevoDocente.correo &&
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
      this.nuevoDocente.correo
    )
  ) {

    this.errores.correo =
      'Ingrese una dirección de correo electrónico válida.';
  }
console.log(this.errores);

  return Object.keys(this.errores).length === 0;
}
 guardarDocente(): void {

 if (!this.validarFormulario()) {
    return;
  }
  if (this.modoEdicion && this.docenteEditandoId !== null) {

    const docenteActualizado = {
      ...this.nuevoDocente,
      id: this.docenteEditandoId
    };

    this.docenteService
      .actualizar(docenteActualizado)
      .subscribe({

        next: () => {

          alert('Docente actualizado correctamente');

          this.mostrarFormulario = false;
          this.resetFormulario();

          this.modoEdicion = false;
          this.docenteEditandoId = null;

          this.cargarDocentes();
        },

        error: (error) => {

          console.error(error);

          alert('Error al actualizar docente');

          this.mostrarFormulario = true;
        }
      });

  } else {

    this.docenteService
      .guardar(this.nuevoDocente)
      .subscribe({

       next: (respuesta: any) => {
 this.errores = {};
  alert(respuesta.mensaje);

  this.mostrarFormulario = false;
  this.resetFormulario();
  this.cargarDocentes();
},

        error: (error: any) => {

  console.error(error);

  const mensaje =
    error.error || 'Error al guardar docente';

  alert(mensaje);

  this.mostrarFormulario = true;
}
      });
  }
}
 editarDocente(docente: any): void {
this.errores = {};
  this.modoEdicion = true;

  this.docenteEditandoId = docente.id;

  this.nuevoDocente = {

    nombre: docente.nombre,
    apellido: docente.apellido,
    dni: docente.dni,
    telefono: docente.telefono,
    correo: docente.correo,
    fechaNacimiento: docente.fechaNacimiento,
    direccion: docente.direccion,
    cargo: docente.cargo,
    activo: docente.activo
  };

  this.mostrarFormulario = true;
}
eliminarDocente(docente: any): void {

  const confirmar = confirm(
    `¿Desea eliminar al docente ${docente.apellido}, ${docente.nombre}?`
  );

  if (!confirmar) {
    return;
  }

  this.docenteService
    .eliminar(docente.id)
    .subscribe({

      next: () => {

        alert('Docente eliminado correctamente');

        this.cargarDocentes();
      },

      error: (error: any) => {

  console.error(error);

  alert(error.error || 'Error al eliminar docente');
}
    });
} 
filtrarDocentes(): void {

  const texto = this.filtroBusqueda
    .toLowerCase()
    .trim();

  if (!texto) {

    this.docentesFiltrados = [...this.docentes];
    return;
  }

  this.docentesFiltrados = this.docentes.filter(docente =>

    docente.dni?.toLowerCase().includes(texto) ||

    docente.nombre?.toLowerCase().includes(texto) ||

    docente.apellido?.toLowerCase().includes(texto) ||

    `${docente.apellido} ${docente.nombre}`
      .toLowerCase()
      .includes(texto)
  );
}
 
cerrarSesion(): void {

  localStorage.removeItem('token');
  localStorage.removeItem('usuario');
  localStorage.removeItem('rol');

  this.router.navigate(['/login']);
}




toggleMenu(): void {
  this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
}

abrirCambioClave(): void {
  this.mostrarMenuUsuario = false;
  this.mostrarCambioClave = true;
  this.passwordActual = '';
  this.passwordNueva = '';
  this.confirmarPassword = '';
  
  
}

cerrarModalCambioClave(): void {

  console.log('ANTES:', this.mostrarCambioClave);

  this.mostrarCambioClave = false;

  this.passwordActual = '';
  this.passwordNueva = '';
  this.confirmarPassword = '';

  this.mostrarMenuUsuario = false;

  this.cd.detectChanges();

  console.log('DESPUÉS:', this.mostrarCambioClave);
}

cambiarPassword(): void {

  if (!this.passwordActual ||
      !this.passwordNueva ||
      !this.confirmarPassword) {

    alert('Complete todos los campos.');
    return;
  }

  if (this.passwordNueva !== this.confirmarPassword) {

    alert('Las contraseñas nuevas no coinciden.');
    return;
  }

  this.authService.changePassword({
    username: this.usuario,
    passwordActual: this.passwordActual,
    passwordNueva: this.passwordNueva
  }).subscribe({

    next: (respuesta: any) => {

  alert(respuesta.mensaje);

  console.log('ANTES:', this.mostrarCambioClave);

  this.cerrarModalCambioClave();

  console.log('DESPUÉS:', this.mostrarCambioClave);
},

    error: (error: any) => {

  console.log('STATUS:', error.status);

  console.log('ERROR:', error.error);

  console.log('COMPLETO:', error);

  alert(
    JSON.stringify(error.error) ||
    'No se pudo cambiar la contraseña.'
  );
}
  });
}


}