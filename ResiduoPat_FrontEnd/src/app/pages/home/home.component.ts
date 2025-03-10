import { Component, HostListener, inject, Input, OnInit, ViewChild } from '@angular/core';
import { HojaRutaTicketsComponent } from 'src/app/pages/home/hoja-ruta-tickets/hoja-ruta-tickets.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TicketControlFormularioComponent } from './ticket-control-formulario/ticket-control-formulario.component';
import { ApiTransportistaService } from 'src/app/services/api/api-transportista/api-transportista.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterModule,
    HojaRutaTicketsComponent,
    DashboardComponent,
    CommonModule,
RouterModule,
TicketControlFormularioComponent
  ],
  
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {

 private apiTransportistaService=inject(ApiTransportistaService);
 private router=inject(Router);
 idTransportista:number | undefined;

  ngOnInit(): void {
    this.apiTransportistaService.getTransportistaById(1).subscribe({
      next: (transportista) => {
        this.idTransportista = transportista.id_transportista;
        console.log('idTransportista:', this.idTransportista);
      },
      error: (error) => {
        if (error.status === 404) {
          console.log('Transportista no encontrado, redirigiendo...');
          this.router.navigate(['/transportista']);
        } else {
          console.error('Error desconocido:', error);
        }
      }
    });
  

  }
  estiloDashboardGenerador = {
    background: '#AF1EDD',
  };

  estiloDashboardTipo = {
    background: '#F19D00',
  };

  urlReport: string = '../../assets/icons/report.png';
  urlGenerador: string = '../../assets/icons/hospital.png';
  urlReportWarning: string = '../../assets/icons/ticketWarning.png';

  _contador?: number;
  _contadorTicket?:number;
  _contadorTicketWarning?:number;
  @Input()
  recepcionDato(valor: number) {
    this._contador = valor;
    console.log(this._contador);
  }

  @Input()
  receptorContadorTicket(valor: number){
    this._contadorTicket=valor;
  }

  @Input()
  receptorContadorTicketWarning(valor: number){
    this._contadorTicketWarning=valor
  }

  @ViewChild('hojaRutaTickets') hojaRutaTickets!: HojaRutaTicketsComponent;

  onTicketCreado(): void {
    this.hojaRutaTickets.recargarTickets(); 
  }

  formularioVisible = false; // Controla la visibilidad del formulario

  // Método para alternar la visibilidad del formulario
  toggleFormulario(): void {
    this.formularioVisible = !this.formularioVisible;
  }

  // Detecta si la pantalla es pequeña (por ejemplo, móvil o tablet)
  isSmallScreen(): boolean {
    return window.innerWidth <= 768;
  }

  // Detecta el cambio de tamaño de la pantalla
  @HostListener('window:resize', ['$event'])
  onResize(): void {
    if (!this.isSmallScreen() && this.formularioVisible) {
      this.formularioVisible = false; // Oculta el formulario en pantallas grandes
    }
  }

  
}
