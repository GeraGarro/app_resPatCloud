import { NgModule } from '@angular/core';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi
} from '@angular/common/http';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { AuthInterceptor } from './core/auth/auth.interceptor';
import { ReactiveFormsModule } from '@angular/forms';
import { TransportistaGeneradoresComponent } from './pages/transportista-generadores/transportista-generadores.component';
import { TransportistaVehiculosComponent } from './pages/transportista-vehiculos/transportista-vehiculos.component';
import { TransportistaPerfilComponent } from './pages/transportista-perfil/transportista-perfil.component';
import { TransportistaTiposResiduoComponent } from './pages/transportista-tipos-residuo/transportista-tipos-residuo.component';
import { TransportistaTicketFormComponent } from './pages/transportista-ticket-form/transportista-ticket-form.component';
import { TransportistaCertificadosComponent } from './pages/transportista-certificados/transportista-certificados.component';
import { TransportistaSidebarComponent } from './shared/transportista-sidebar/transportista-sidebar.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    TransportistaGeneradoresComponent,
    TransportistaVehiculosComponent,
    TransportistaPerfilComponent,
    TransportistaTiposResiduoComponent,
    TransportistaTicketFormComponent,
    TransportistaCertificadosComponent,
    TransportistaSidebarComponent

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    LoginComponent
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
