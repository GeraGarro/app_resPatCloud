import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, NgZone, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {  MatTableModule } from '@angular/material/table';
import { ITicket } from '../../../services/models/ticket.model';
import { ApiTicketService } from '../../../services/api/api-ticket/api-ticket.service';
import { ApiHojaService } from 'src/app/services/api/api-hoja_ruta/api-hoja.service';
import { ServicioCompartidoService } from 'src/app/services/servicio-compartido/servicio-compartido.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmacionDialogoComponent } from 'src/app/confirmacion-dialogo/confirmacion-dialogo.component';
import { format, parseISO } from 'date-fns';
import { es } from 'date-fns/locale';

@Component({
  selector: 'app-hoja-ruta-tickets',
  standalone:true,
  imports:[ MatTableModule,
    CommonModule,
    MatPaginatorModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSlideToggleModule,
    FormsModule,
    RouterModule,
],
  templateUrl: './hoja-ruta-tickets.component.html',
  styleUrls: ['./hoja-ruta-tickets.component.css']
})
export class HojaRutaTicketsComponent {
listaTickets:ITicket []=[
 ];

private readonly apiTicket= inject(ApiTicketService);
private readonly apiHoja = inject(ApiHojaService);
private servicioCompartido= inject(ServicioCompartidoService)
 idHoja:number=0;
 primerDiaSemana:String="";

 ultimoDiaSemana:String="";

 @Output() contadorTicket=new EventEmitter<{contador: number}>();
 @Output() contadorTicketWarning=new EventEmitter<{contador: number}>();
constructor(private router: Router,private cdr: ChangeDetectorRef,private dialog: MatDialog,private zone: NgZone){
  const hoy = new Date(); // Obtener la fecha actual



}

ngOnInit(): void {
  
  this.servicioCompartido.reload$.subscribe(() => {
    this.cargaTickets();
  });

   this.apiHoja.getHojaActual().subscribe(
    (response)=>{
      console.log(response)
      this.idHoja = response.data.id;
     
      this.servicioCompartido.setIdHoja(this.idHoja);

     // Convertir las cadenas ISO a objetos Date
     const fechaInicio = parseISO(response.data.fechaInicio as unknown as string);
     const fechaFin = parseISO(response.data.fechaFin as unknown as string);

       // Formatear las fechas correctamente
    this.primerDiaSemana = format(fechaInicio, "dd/MM/yyyy", { locale: es });
    this.ultimoDiaSemana = format(fechaFin, "dd/MM/yyyy", { locale: es });
  
      console.log("id fecha Inicio: "+ this.primerDiaSemana)
      console.log("id fecha Fin: "+ this.ultimoDiaSemana)

      this.servicioCompartido.reload$.subscribe(() => {
        this.cargaTickets();
      });
          // Cargar los tickets inicialmente
    this.cargaTickets();
    }
  )

}

cargaTickets(){
  this.apiTicket.getTicketsByHoja(this.idHoja).subscribe(
    (data)=>{
      console.log(data)
      this.listaTickets = data.sort((a: ITicket, b: ITicket) => {
        const fechaA = a.fechaEmisionTk ? new Date(a.fechaEmisionTk).getTime() : 0;
        const fechaB = b.fechaEmisionTk ? new Date(b.fechaEmisionTk).getTime() : 0;
        return fechaA - fechaB; // Orden ascendente (más antiguo primero)
      });

  
    this.contadorTicket.emit({contador: this.listaTickets.length})
    
    this.contadorTicketWarning.emit({contador: this.ticketsWarning(this.listaTickets).length})
     } )
}


recargarTickets(): void {
  this.cargaTickets(); // Reutilizamos la función para recargar datos.
}

ticketsWarning(lista:ITicket[]){
 const nuevaLista : ITicket[]=[]
lista.forEach(element => {
  if(!element.estado){
    nuevaLista.push(element)
  }
});
return nuevaLista;
}



onRowClicked(row: any): void {
  this.router.navigate(['/ticket-info'], { queryParams: { id: row.id_Ticket } });
}



async deleteTicket(id:number | undefined){

  if (!id) {
    alert('El ID del Ticket no está definido.');
    return;
  }

const dialogRef= this.dialog.open
(ConfirmacionDialogoComponent)

const confirmacion= await dialogRef.afterClosed().toPromise();

if(confirmacion){
  try{
    const response:any =await this.apiTicket.eliminarTicket(id).toPromise();
    console.log(response.mensaje)
    if(response.resultado ==='éxito'){
     
    
      this.zone.run(() => {
        this.listaTickets = this.listaTickets.filter(ticket => ticket.id_Ticket !== id);
      });
        // Emitir eventos de actualización
        this.contadorTicket.emit({ contador: this.listaTickets.length });
        this.contadorTicketWarning.emit({ contador: this.ticketsWarning(this.listaTickets).length });

        alert(response.mensaje);
    }else{
      alert(response.mensaje)
    }
  }catch(error){
    console.error('Error al eliminar', error);
    alert('Ocurrió un error al eliminar el ticket');
  
  }
}
  
}

cambiarestado(id:number | undefined, estado:boolean):void{
  if (id === undefined) {
    console.error('El ID del ticket no está definido');
    return;
  }

  // Si id está definido, realiza la actualización
  this.apiTicket.actualizarEstado(id, estado).subscribe({
    next: () =>    this.recargarTickets(),
        error: err => console.error('Error al actualizar el estado del ticket:', err)
  });
}

descargarReporte(id:number | undefined){
  if(id!=undefined){
    this.apiTicket.getReporteTicketById(id).subscribe(
      (response) =>{
        const nombreArchivo=`Ticket_${id}.pdf`;
        this.apiTicket.guardarArchivo(response,nombreArchivo);
      },
      (error)=>{
        console.error('Error al descargar el reporte:',error)
      }
    )
  }

}

}


