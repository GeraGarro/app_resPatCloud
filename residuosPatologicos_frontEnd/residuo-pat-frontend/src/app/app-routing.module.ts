import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { authGuard } from './core/auth/auth.guard';
import { transportistaProfileGuard } from './core/auth/transportista-profile.guard';
import { TransportistaGeneradoresComponent } from './pages/transportista-generadores/transportista-generadores.component';
import { TransportistaVehiculosComponent } from './pages/transportista-vehiculos/transportista-vehiculos.component';
import { TransportistaPerfilComponent } from './pages/transportista-perfil/transportista-perfil.component';
import { RegisterComponent } from './pages/register/register.component';
import { ConfirmEmailComponent } from './pages/confirm-email/confirm-email.component';
import { TransportistaTiposResiduoComponent } from './pages/transportista-tipos-residuo/transportista-tipos-residuo.component';
import { TransportistaTicketFormComponent } from './pages/transportista-ticket-form/transportista-ticket-form.component';
import { TransportistaCertificadosComponent } from './pages/transportista-certificados/transportista-certificados.component';
import { AdminTransportistasPendientesComponent } from './pages/admin-transportistas-pendientes/admin-transportistas-pendientes.component';
const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegisterComponent },
  { path: 'confirmar-email', component: ConfirmEmailComponent },
  {
    path: 'admin/transportistas-pendientes',
    component: AdminTransportistasPendientesComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard, transportistaProfileGuard],
    data: { roles: ['TRANSPORTISTA'] }
  },
  {
    path: 'transportista/perfil',
    component: TransportistaPerfilComponent,
    canActivate: [authGuard],
    data: { roles: ['TRANSPORTISTA'] }
  },
  {
    path: 'transportista/generadores',
    component: TransportistaGeneradoresComponent,
    canActivate: [authGuard, transportistaProfileGuard],
    data: { roles: ['TRANSPORTISTA'] }
  },
  {
    path: 'transportista/vehiculos',
    component: TransportistaVehiculosComponent,
    canActivate: [authGuard, transportistaProfileGuard],
    data: { roles: ['TRANSPORTISTA'] }
  },
  {
    path: 'transportista/tipos-residuo',
    component: TransportistaTiposResiduoComponent,
    canActivate: [authGuard, transportistaProfileGuard],
    data: { roles: ['TRANSPORTISTA'] }
  },
  {
    path: 'transportista/certificados',
    component: TransportistaCertificadosComponent,
    canActivate: [authGuard, transportistaProfileGuard],
    data: { roles: ['TRANSPORTISTA'] }
  },
  {
    path: 'transportista/tickets/nuevo',
    component: TransportistaTicketFormComponent,
    canActivate: [authGuard, transportistaProfileGuard],
    data: { roles: ['TRANSPORTISTA'] }
  },
  {
    path: 'transportista/tickets/:id',
    component: TransportistaTicketFormComponent,
    canActivate: [authGuard, transportistaProfileGuard],
    data: { roles: ['TRANSPORTISTA'] }
  },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
