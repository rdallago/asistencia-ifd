import { Component, OnInit,ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  Auditoria,
  AuditoriaService
} from '../../services/auditoria.service';

@Component({
  selector: 'app-auditoria',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './auditoria.html',
  styleUrls: ['./auditoria.css']
})
export class AuditoriaComponent
implements OnInit {

  auditorias: Auditoria[] = [];
  auditoriasFiltradas: Auditoria[] = [];

  filtro = '';

  constructor(
    private auditoriaService: AuditoriaService,
    private cdr: ChangeDetectorRef
  ) {}

ngOnInit(): void {

 

  this.cargar();

}

  cargar(): void {

    this.auditoriaService
        .listar()
        .subscribe({

 next: datos => {


  this.auditorias = [...datos];
  this.auditoriasFiltradas = [...datos];
  this.cdr.detectChanges();
}

    });

  }

  filtrar(): void {

    const texto =
      this.filtro.toLowerCase();

    this.auditoriasFiltradas =
      this.auditorias.filter(a =>

        a.usuario.toLowerCase().includes(texto) ||

        a.modulo.toLowerCase().includes(texto) ||

        a.accion.toLowerCase().includes(texto) ||

        a.resultado.toLowerCase().includes(texto) ||

        (a.detalle ?? '')
            .toLowerCase()
            .includes(texto)

      );

  }

}