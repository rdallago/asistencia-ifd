import {
  AuthService
} from '../../services/auth.service';

import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import {
  CommonModule
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  UsuarioService
} from '../../services/usuario.service';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './usuarios.html',
  styleUrls: ['./usuarios.css']
})
export class UsuariosComponent
implements OnInit {

  usuarios: Usuario[] = [];

  usuariosFiltrados: Usuario[] = [];

  filtro = '';

  mostrarFormulario = false;

  editando = false;

  usuarioActual: any = {};

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private cdr:ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.listar();
  }

  listar(): void {

    this.usuarioService
      .getAll()
      .subscribe({

        next: data => {

  this.usuarios = [...data];

  this.filtrar();

  this.cdr.detectChanges();
},

        error: () => {

          alert(
            'Error al cargar usuarios'
          );
        }
      });
  }

  filtrar(): void {

    const texto =
      this.filtro.toLowerCase();

    this.usuariosFiltrados =
      this.usuarios.filter(u =>

        u.username
          .toLowerCase()
          .includes(texto)

        ||

        u.nombre
          .toLowerCase()
          .includes(texto)

        ||

        u.apellido
          .toLowerCase()
          .includes(texto)
      );
  }

  nuevo(): void {

    this.editando = false;

    this.usuarioActual = {

      username: '',

      nombre: '',

      apellido: '',

      email: '',

      password: '',

      rol: 'SECRETARIO',

      docenteId: null
    };
     console.log(this.usuarioActual);

  this.mostrarFormulario = true;

    this.mostrarFormulario = true;
  }

  editar(
    usuario: Usuario
  ): void {

    this.editando = true;

    this.usuarioActual = {

      ...usuario,

      password: ''
    };

    this.mostrarFormulario = true;
  }

  guardar(): void {

  const esUsuarioActual =

    this.editando &&

    this.usuarioActual.username ===
    this.authService.getUsername();

  const accion = this.editando

    ? this.usuarioService.actualizar(
        this.usuarioActual.id,
        this.usuarioActual
      )

    : this.usuarioService.crear(
        this.usuarioActual
      );

  accion.subscribe({

    next: () => {

      this.mostrarFormulario = false;

      if (esUsuarioActual) {

        alert(
          'Los cambios realizados en su usuario se aplicarán al volver a iniciar sesión.'
        );

        this.authService.logout();

        return;
      }

      this.listar();
    },

    error: err => {

      alert(
        err.error ??
        'Error al guardar usuario'
      );
    }
  });
}

  desactivar(
    usuario: Usuario
  ): void {

     
    if (!confirm(
      `¿Desactivar al usuario ${usuario.username}?`
    )) {

      return;
    }

    this.usuarioService
      .desactivar(
        usuario.id!
      )
      .subscribe({

        next: () => {

  this.listar();

  this.cdr.detectChanges();
},

error: err => {

  console.log('ERROR COMPLETO:', err);

  console.log('err.error:', err.error);

  console.log('err.status:', err.status);

  alert(
    JSON.stringify(err.error) ||
    'Error al desactivar usuario'
  );
}
      });
  }

  cancelar(): void {

     
    this.mostrarFormulario = false;

    this.usuarioActual = {};
  }
  activar(
  usuario: Usuario
): void {

  if (!confirm(
    `¿Activar al usuario ${usuario.username}?`
  )) {
    return;
  }

  this.usuarioService
    .activar(usuario.id!)
    .subscribe({

      next: (mensaje) => {

        alert(mensaje);

        this.listar();

        this.cdr.detectChanges();
      },

      error: err => {

        console.log(err);

        alert(
          'Error al activar usuario'
        );
      }
    });
}
}

export interface Usuario {

  id?: number;

  username: string;

  nombre: string;

  apellido: string;

  email: string;

  rol: string;

  activo: boolean;

  docenteId?: number;

  docenteNombre?: string;
  
  
}