import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';

import {
  RespaldoService,
  AuditoriaRespaldo
} from '../../services/respaldo.service';

@Component({
  selector: 'app-respaldos',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './respaldos.html',
  styleUrls: ['./respaldos.css']
})
export class RespaldosComponent implements OnInit {

  auditorias: AuditoriaRespaldo[] = [];
  auditoriasFiltradas: AuditoriaRespaldo[] = [];
  restaurando = false;
  filtro = '';

  archivoSeleccionado?: File;



  constructor(
    private respaldoService: RespaldoService,
    private cd: ChangeDetectorRef
) {}

get puedeRestaurar(): boolean {

    return !!this.archivoSeleccionado && !this.restaurando;

}

  ngOnInit(): void {
   
    this.cargarAuditoria();

  }


  cargarAuditoria(): void {

  this.respaldoService.obtenerAuditoria()
    .subscribe({

      next: data => {

        this.auditorias = [...data];

        this.auditoriasFiltradas = [...data];

        this.cd.detectChanges();

      },

      error: err => {

        console.error(
          "Error cargando auditoría:",
          err
        );

      }

    });

}

 filtrar(): void {

    const texto = this.filtro
        .toLowerCase()
        .trim();


    if (!texto) {

        this.auditoriasFiltradas =
            [...this.auditorias];

        return;
    }


    this.auditoriasFiltradas =
        this.auditorias.filter(a =>

            (a.usuario ?? '')
            .toLowerCase()
            .includes(texto)

        );

}
generar(): void {

  this.respaldoService
    .generarBackup()
    .subscribe({

      next: response => {
          console.log("HEADERS:");
    console.log(response.headers.keys());
    console.log(response.headers.get('Content-Disposition'));
    console.log(response.headers);

        const blob = response.body!;

        let nombreArchivo = 'backup.sql';

        const disposition =
          response.headers.get('Content-Disposition');

        if (disposition) {

          const resultado =
            /filename\*?=(?:UTF-8''|")?([^"]+)/.exec(disposition);

          if (resultado && resultado[1]) {

            nombreArchivo =
              decodeURIComponent(resultado[1].replace(/"/g, ''));

          }

        }

        const url =
          window.URL.createObjectURL(blob);

        const a =
          document.createElement('a');

        a.href = url;

        a.download = nombreArchivo;

        a.click();

        window.URL.revokeObjectURL(url);

        setTimeout(() => {

          this.cargarAuditoria();

        }, 500);

      },

      error: (err) => {

        console.error(err);

        alert('No fue posible generar el respaldo.');

      }

    });

}
  seleccionarArchivo(event: any): void {

    this.archivoSeleccionado =
      event.target.files[0];

  }

  restaurar(): void {

    if (!this.archivoSeleccionado) {

      alert('Seleccione un archivo.');

      return;

    }

    if (!confirm(
        'La restauración reemplazará la base de datos actual.\n\n¿Desea continuar?'
    )) {
      return;
    }



    this.respaldoService
  .restaurarBackup(this.archivoSeleccionado)
 .pipe(
  finalize(() => {

    this.archivoSeleccionado = undefined;

    setTimeout(() => {

      this.cargarAuditoria();

    }, 1000);

  })
)
  .subscribe({

    next: () => {

      alert('Respaldo restaurado correctamente.');

    },

    error: (err) => {

      console.error(err);

      alert('No fue posible restaurar el respaldo.');

    }

  });

  }
  trackById(
  index:number,
  item:AuditoriaRespaldo
){
  return item.id;
}


onArchivoSeleccionado(event: Event): void {

  const input = event.target as HTMLInputElement;

  if (input.files && input.files.length > 0) {

    const archivo = input.files[0];

    if (!archivo.name.toLowerCase().endsWith('.sql')) {

      alert('Solo se permiten archivos SQL.');

      this.archivoSeleccionado = undefined;

      input.value = '';

      return;
    }

    this.archivoSeleccionado = archivo;

  } else {

    this.archivoSeleccionado = undefined;

  }

}
}