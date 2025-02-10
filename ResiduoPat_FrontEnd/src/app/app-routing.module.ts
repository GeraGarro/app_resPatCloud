import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TicketControlComponent } from './ticket-control/ticket-control.component';
import { ResiduoComponent } from './residuo/residuo.component';
import { TipoResiduoComponent } from './pages/tipo-residuo/tipo-residuo.component';
import { GeneradorComponent } from './pages/generador/generador.component';
import { TransportistaComponent } from './transportista/transportista.component';
import { TicketControlFormularioComponent } from './pages/home/ticket-control-formulario/ticket-control-formulario.component';
import { ResiduoFormularioComponent } from './residuo-formulario/residuo-formulario.component';
import { TipoResiduoFormularioComponent } from '../app/pages/tipo-residuo/tipo-residuo-formulario/tipo-residuo-formulario.component';
import { CertificadoComponent } from './pages/certificado/certificado.component';
import { TransportistaFormularioComponent } from './transportista-formulario/transportista-formulario.component';
import { GeneradorFormularioComponent } from './pages/generador/generador-formulario/generador-formulario.component';
import { CertificadoFormularioComponent } from './pages/certificado-formulario/certificado-formulario.component';
import { HomeComponent } from './pages/home/home.component';
import { Error404Component } from './error404/error404.component';
import { LoginComponent } from './pages/login/login.component';
import { TicketInfoComponent } from './pages/ticket-info/ticket-info.component';

const routes: Routes = [
{path:'ticket',component:TicketControlComponent},

{path: 'formulario-ticket/:id', component: TicketControlFormularioComponent },
{path:'residuo',component: ResiduoComponent},
{path:'residuo_Formulario',component: ResiduoFormularioComponent},
{path:'tipos-residuos',component: TipoResiduoComponent},
{path:'generador',component: GeneradorComponent},
{path:'certificado',component: CertificadoComponent},
{path: 'certificado_formulario',component:CertificadoFormularioComponent},
{path: 'certificado_formulario/:id',component:CertificadoFormularioComponent},
{path:'transportista',component: TransportistaComponent},
{path:'transportista_Formulario',component: TransportistaFormularioComponent},
{path:'home',component: HomeComponent},
{path:'', redirectTo:'/home', pathMatch:'full'},
{path:'ticket-control-Formulario',component: TicketControlFormularioComponent},
{ path: 'generador-Formulario', component: GeneradorFormularioComponent },
{ path: 'tipo-residuo-Formulario', component: TipoResiduoFormularioComponent },
{path: 'error-404', component: Error404Component,},
{path: 'login', component:LoginComponent},
{path: 'ticket-info', component:TicketInfoComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
