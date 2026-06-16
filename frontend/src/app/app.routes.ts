import { Routes } from '@angular/router';

import { Login } from './auth/login/login';

import { Dashboard } from './dashboard/dashboard';

import { MainLayout } from './layout/main-layout/main-layout';

import { Asistencia } from './pages/asistencia/asistencia';

import { Turnos} from './pages/turnos/turnos';

import { Usuarios} from './pages/usuarios/usuarios';

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
        path: 'docentes',
        component: Dashboard
      },

      {
        path: 'asistencia',
        component: Asistencia
      },

      {
        path: 'turnos',
        component: Turnos
      },

      {
        path: 'usuarios',
        component: Usuarios
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