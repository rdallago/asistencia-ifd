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
  AsistenciaService
} from '../../services/asistencia.service';


import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-asistencias',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './marcador-asistencia.html',
  styleUrls: ['./marcador-asistencia.css']
})

export class MarcadorAsistenciaComponent
implements OnInit {

  dni = '';
 jornadaId!: number;
 asistencias: any[] = [];
 
constructor(
  private asistenciaService: AsistenciaService,
  private route: ActivatedRoute,
  private cdr: ChangeDetectorRef
) {}

ngOnInit(): void {

  this.jornadaId = Number(
    this.route.snapshot.paramMap.get(
      'jornadaId'
    )
  );
    if (!this.jornadaId) {

    alert(
      'No se recibió una jornada válida'
    );
  }
}

 
 
  registrarIngreso(): void {

  if (!this.dni) {

    alert(
      'Debe ingresar un DNI'
    );

    return;
  }

this.asistenciaService
  .registrarEntrada(
    this.dni,
    this.jornadaId
  )
    .subscribe({

      next: mensaje => {

        alert(mensaje);

        this.dni = '';
         this.cdr.detectChanges();
      },

      error: err => {

        alert(err.error);
      }
    });
}
 registrarEgreso(): void {

  if (!this.dni) {

    alert(
      'Debe ingresar un DNI'
    );

    return;
  }

 this.asistenciaService
  .registrarSalida(
    this.dni,
    this.jornadaId
  )
    .subscribe({

      next: mensaje => {

        alert(mensaje);

        this.dni = '';
         this.cdr.detectChanges();
      },

      error: err => {

        alert(err.error);
      }
    });
}
soloNumeros(): void {

  this.dni = this.dni.replace(
    /[^0-9]/g,
    ''
  );
}
}