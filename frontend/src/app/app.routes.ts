import { Routes } from '@angular/router';

import { Login } from './auth/login/login';

import { Dashboard } from './dashboard/dashboard';

import { MainLayout } from './layout/main-layout/main-layout';

import { Asistencia } from './pages/asistencia/asistencia';

import {TurnosComponent} from './pages/turnos/turnos';

import { AsignacionesComponent } from './pages/asignaciones/asignaciones';

import { JornadasComponent} from './pages/jornadas/jornadas';

import { UsuariosComponent} from './pages/usuarios/usuarios';

import { Respaldos} from './pages/respaldos/respaldos';

import { Ayuda } from './pages/ayuda/ayuda';

export const routes: Routes = [

  {
    path: 'login',
    component: Login
  },

  {
    path: '',
    component: MainLayout,

    children: [

      {
        path: '',
        redirectTo: 'docentes',
        pathMatch: 'full'
      },
{
        path: 'turnos',
        component: TurnosComponent
      },

      {
        path: 'docentes',
        component: Dashboard
      },
{
        path: 'asignaciones',
        component: AsignacionesComponent
      },
{
        path: 'jornadas',
        component: JornadasComponent
      },

      {
        path: 'asistencia',
        component: Asistencia
      },

      
      {
        path: 'usuarios',
        component: UsuariosComponent
      },

      {
        path: 'respaldos',
        component: Respaldos
      },

      {
        path: 'ayuda',
        component: Ayuda
      }

    ]
  },

  {
    path: '**',
    redirectTo: ''
  }
];