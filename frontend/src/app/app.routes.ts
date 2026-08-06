import { Routes } from '@angular/router';

import { Login } from './auth/login/login';

import { Dashboard } from './dashboard/dashboard';

import { MainLayout } from './layout/main-layout/main-layout';

import { AsistenciaComponent } from './pages/asistencia/asistencia';

import { IndicadoresComponent } from './components/indicadores/indicadores.component';

import { MarcadorAsistenciaComponent } from './pages/marcador-asistencia/marcador-asistencia';

import {TurnosComponent} from './pages/turnos/turnos';

import { AsignacionesComponent } from './pages/asignaciones/asignaciones';

import { JornadasComponent} from './pages/jornadas/jornadas';

import { UsuariosComponent} from './pages/usuarios/usuarios';

import { RespaldosComponent} from './pages/respaldos/respaldos';

import { AuditoriaComponent }from './pages/auditoria/auditoria';

import { Ayuda } from './pages/ayuda/ayuda';

export const routes: Routes = [

  {
    path: 'login',
    component: Login
  },

{
path: 'marcador/:jornadaId',
  component: MarcadorAsistenciaComponent
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
  component: AsistenciaComponent
},

     {
 

  path: 'asistencia/:jornadaId',
  component: AsistenciaComponent
},

  {
    path: 'indicadores',
    component: IndicadoresComponent
},
      
      {
        path: 'usuarios',
        component: UsuariosComponent
      },

      {
        path: 'respaldos',
        component: RespaldosComponent
      },
      
      {
    path: 'auditoria',
    component: AuditoriaComponent
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