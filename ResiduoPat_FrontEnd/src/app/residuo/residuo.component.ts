
import { Component, inject, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { ListaResiduo, TipoResiduo } from '../services/models/ticket.model';
import { CommonModule } from '@angular/common';
import { ApiServicesTipoResiduosService } from '../services/api/api-tipoResiduos/api.services-tipo-residuos.service';
import { FormsModule } from '@angular/forms';
import { ApiResiduoService } from '../services/api/api-residuo/api-residuo.service';
import { ModalInfoComponent } from '../modal-info/modal-info.component';
import { ValidadorPesoDirective } from '../directivas/validador-peso.directive';
import { ApiTicketService } from '../services/api/api-ticket/api-ticket.service';

@Component({
  selector: 'app-residuo',
  templateUrl: './residuo.component.html',
  styleUrls: ['./residuo.component.css'],
 standalone:true,
 imports: [CommonModule,FormsModule,ModalInfoComponent,ValidadorPesoDirective]

})

export class ResiduoComponent  implements OnInit {

  @Input() residuosLista: ListaResiduo[]=[];
 
  @Input() id_TicketControl?: number;



  listaTipoResiduos: TipoResiduo[]=[];
  originalResiduos: ListaResiduo[] = [];
  listaResAEliminar: number[] =[];

apiTipoResiduo=inject(ApiServicesTipoResiduosService)
apiResiduo= inject(ApiResiduoService)
apiTicket= inject(ApiTicketService)

totalPeso: number = 0; // Peso total

 // Acceso al Modal usando ViewChild
 @ViewChild(ModalInfoComponent) modalInfo!: ModalInfoComponent;

ngOnInit(): void {
this.cargarResiduosDelTicket();

}

cargarResiduosDelTicket()
{
  this.apiTipoResiduo.getTipoResiduos().subscribe(
    (data)=>{
      this.listaTipoResiduos=data;
  
  },
  error=>{
    console.error("ERROR")
  })
}

ngOnChanges(changes: SimpleChanges): void {
  if (changes['residuosLista']) {
    // Almacenar la lista original cada vez que cambie la lista de residuos
    this.originalResiduos = JSON.parse(JSON.stringify(this.residuosLista));
    
    if (this.residuosLista.length > 0) {
      this.recalcularTotal(); // Recalcula el total cuando cambia la lista
    }
  }
}

agregarResiduo(){
 const tipoDisponible= this.listaTipoResiduos.find(
  tipo => !this.residuosLista.some(residuo => residuo.tipoResiduo.id !== undefined && tipo.id != undefined && 
    residuo.tipoResiduo.id == tipo.id
  )
 );

 if(tipoDisponible) {
  const nuevoResiduo: ListaResiduo ={
    id:0,
    id_TicketControl:this.id_TicketControl ?? 0,
    tipoResiduo:tipoDisponible,
    peso:0
  };
  this.residuosLista.push(nuevoResiduo);
  this.recalcularTotal();
 } else {
  alert('No hay tipos de residuos disponibles.')
 }
}
 
deleteResiduo(index: number ,id:number): void {
  // Elimina el residuo de la lista en la posición indicada
  this.residuosLista.splice(index, 1);
  this.recalcularTotal(); // Recalcula el total después de eliminar un residuo

  this.listaResAEliminar.push(id)
}

recalcularTotal() {
  this.totalPeso = this.residuosLista
  .map(item => Number(item.peso)) // Convertir a número, si es posible
  .filter(peso => !isNaN(peso)) // Filtrar valores no numéricos
  .reduce((total, peso) => total + peso, 0);



}
    //controla cuando cambias un  tipo de residuo
onTipoChange(event: any, item: ListaResiduo) {
  const selectedTipoId = +event.target.value;
  const selectedTipo = this.listaTipoResiduos.find(tipo => tipo.id === selectedTipoId);

  if (selectedTipo) {
    item.tipoResiduo = selectedTipo;
  }
}

getFilteredTipos(item: ListaResiduo): TipoResiduo[] {
  return this.listaTipoResiduos.filter(
    tipo => !this.residuosLista.some(i => i.tipoResiduo.id === tipo.id && i !== item)
  );
}

enviarListaResiduos():void{

//metodos HTTP para realizar los diferentes procesos al servidor

const nuevosResiduos= this.residuosLista.filter(residuo => residuo.id === 0);

const residuosModificados = this.residuosLista.filter(residuoNuevo => {
  const original = this.originalResiduos.find(
    orig => orig.tipoResiduo.id === residuoNuevo.tipoResiduo.id
  );
  if (original) {
    console.log(
      `Comparando peso: Original(${+original.peso}) vs Nuevo(${+residuoNuevo.peso})`
    );
  }
  return original && +original.peso !== +residuoNuevo.peso;
});


console.log("Residuos Modificados:", residuosModificados.length);
residuosModificados.forEach(ele => console.log(ele));
console.log("Lista Original:", this.originalResiduos);
console.log("Lista Modificada:", this.residuosLista);

console.log("Residuos Modificados:"+residuosModificados.length)
residuosModificados.forEach(ele=> console.log(ele))

//metodo revisar
const residuosEliminados= this.originalResiduos.filter(
  original=> !this.residuosLista.some(residuoNuevo => residuoNuevo.tipoResiduo.id=== original.tipoResiduo.id)
)


console.log("Nuevos Residuos");

//Crear nuevo residuos(POST)
nuevosResiduos.forEach(residuo =>{
  this.apiResiduo.crearResiduo(residuo).subscribe({
    next: ()=> console.log(`Residuo creado; ${residuo.tipoResiduo.nombre}`),
    error: err=> console.error(`Error creando residuo:`, err)
   }); 
});


//Actualizar residuos Modificados por tipoResiduo(PUT)
  residuosModificados.forEach(residuo=>{
    const original=this.originalResiduos.find(
      orig => orig.tipoResiduo.id=== residuo.tipoResiduo.id
    );

   
    if(original){
      //Asignar el nuevo peso al residuo existente
      const payload = {
        tipoResiduo: { id: original.tipoResiduo.id },
        peso: residuo.peso,
        id_TicketControl: original.id_TicketControl,
      };

        this.apiResiduo.updateResiduo(original.id, payload).subscribe(
          {
            next:()=> console.log(`Residuyo actualizado: ${original.tipoResiduo.nombre}`),
            error: err => console.error('Error actualizar residuo', err)
          });
    }
  });


  
  //Eliminar residuos no presentes(DELETE)
  residuosEliminados.forEach(  residuo => {
    this.apiResiduo.eliminarResiduo(residuo.id).subscribe({
    
      next: ()=> console.log(`Residuo eliminado con ID: ${residuo.id}`),
      
      error: err=> console.error('Error eliminar residuo:', err)
    })
 
  })
  

 
   const nuevoEstado = this.residuosLista.length > 0; 
   if (this.id_TicketControl != null) {
     this.apiTicket.actualizarEstado(this.id_TicketControl, nuevoEstado).subscribe({
       next: () => console.log(`Estado del ticket actualizado a ${nuevoEstado}`),
       error: err => console.error('Error al actualizar el estado del ticket:', err)
     }); 
   }

  this.modalInfo.nuevosResiduos = nuevosResiduos;
  this.modalInfo.residuosModificados = residuosModificados;
  this.modalInfo.residuosEliminados = residuosEliminados;
  this.modalInfo.mostrarModal();


}


}


  
  

 
 



