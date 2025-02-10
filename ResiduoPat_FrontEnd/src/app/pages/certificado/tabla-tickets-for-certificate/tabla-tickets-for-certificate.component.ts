import { CommonModule } from '@angular/common';
import { Component, inject, Inject, Input ,OnChanges, SimpleChange, SimpleChanges} from '@angular/core';
import { Router } from '@angular/router';

import { ApiCertificadoService } from 'src/app/services/api/api-certificado/api-certificado.service';
import { ITicket } from 'src/app/services/models/ticket.model';
import { ServicioCompartidoService } from 'src/app/services/servicio-compartido/servicio-compartido.service';

@Component({
  selector: 'app-tabla-tickets-for-certificate',
  standalone:true,
  imports:[CommonModule],
  templateUrl: './tabla-tickets-for-certificate.component.html',
  styleUrls: ['./tabla-tickets-for-certificate.component.css']
})
export class TablaTicketsForCertificateComponent implements OnChanges {
 @Input() listaTickets:any []=[];

 

 private router=inject(Router);

 private apiCertificado=inject(ApiCertificadoService);

ngOnChanges(changes: SimpleChanges): void {
  if (changes['idCertificadoRecibido']?.currentValue !== undefined) {
    const id = changes['idCertificadoRecibido'].currentValue as number;
    this.cargarTicketsByCertificado(id);
  }
}
ngOnInit():void{
console.log(this.listaTickets)
}

cargarTicketsByCertificado(id:number ):void{
 
    this.apiCertificado.getfindCertificado(id).subscribe(
      (data)=>{
        if(data.listaTicketsDTO){
            // Ordenar la lista de tickets por fecha de emisión
            this.listaTickets =  data.listaTicketsDTO.sort((a: any, b: any) => {
              const fechaA = a.fechaEmisionTk ? new Date(a.fechaEmisionTk).getTime() : 0;
              const fechaB = b.fechaEmisionTk ? new Date(b.fechaEmisionTk).getTime() : 0;
              return fechaA - fechaB; // Orden ascendente (más antiguo primero)
            }); }
      



        }
  
  
        
    )
  
  
}


  onRowClicked(row: any): void {
    this.router.navigate(['/ticket-info'], { queryParams: { id: row.id_Ticket } });
  }


}
