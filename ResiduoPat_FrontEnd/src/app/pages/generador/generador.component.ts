import { Component, OnInit,ChangeDetectorRef, Output, EventEmitter, inject, SimpleChanges} from '@angular/core';
import { Generador } from '../../services/models/generador.model';
import { MatTableModule } from '@angular/material/table';
import { ApiGeneradorService } from '../../services/api/api-generador/api-generador.service';
import {  MatPaginatorModule } from '@angular/material/paginator';
import { Router, } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';

import { MatDialog } from '@angular/material/dialog';
import { GeneradorFormularioComponent } from './generador-formulario/generador-formulario.component';
import { ModalComponent } from "../../modal/modal.component";
@Component({
  selector: 'app-generador',
  templateUrl: './generador.component.html',
  standalone:true,
  imports: [
    MatTableModule,
    CommonModule,
    MatPaginatorModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSlideToggleModule,
    FormsModule,
    GeneradorFormularioComponent,
    ModalComponent
],
  styleUrls: ['./generador.component.scss']
})
export class GeneradorComponent  {
   listaGeneradores: Generador[]=[]; 
 

mensajeDeFormulario:string="Mensaje recibido de formulario Mensaje recibido de formulario ";
activarModal:boolean=false;
estadoModal:boolean=false;

recepcionDatos(event: any): void {
  const modal = event as { mensajeEnviar: string; activarModal: boolean ,estadoAprobado:boolean};
  this.mensajeDeFormulario = modal.mensajeEnviar;
  this.activarModal = modal.activarModal;
this.estadoModal=modal.estadoAprobado;
  if (this.activarModal) {
    this.cerrarModalConDelay(); // Llama al método para desactivar el modal automáticamente
  }
}

cerrarModalConDelay(): void {
  const delay = 3000; // Tiempo en milisegundos (3 segundos)

  setTimeout(() => {
    this.activarModal = false; // Cambia el estado del modal a false
    console.log('Modal desactivado automáticamente.');
  }, delay);
}



@Output()contadorEmitido=new EventEmitter<{contador:number}>();

idSeleccionado: number | undefined=undefined;

isActivo:boolean=false;

mostrarFormulario: boolean = false; // Controla la visibilidad del formulario
nuevo: boolean = false; // Indica si el formulario está en modo "nuevo"

private cdr= inject(ChangeDetectorRef)
selectedIndex: number | null = null;


constructor(private _apiGeneradorService:ApiGeneradorService , private router: Router, private dialog: MatDialog){
}

items: string[] = Array.from({ length: 30 }, (_, i) => `Elemento ${i + 1}`);

ngOnInit(): void {
  this.cargaGeneradores();

}


cargaGeneradores():void{
  this._apiGeneradorService.getGeneradores().subscribe(
    data=>{
      console.log(data);
    this.listaGeneradores=data.sort((a, b) => {
      return a.nombre.localeCompare(b.nombre); // Compara los nombres de forma alfabética
    });;
     this.contadorEmitido.emit({ contador: this.listaGeneradores.length }); 
    this.cdr.detectChanges();
    },
    error=>{
      console.error('Error fetching Generadores:', error);
    }
  );
}
/* Seleccionar un elemento para mandar la información a Formulario */
seleccionarElemento(index: number, id:number|undefined) {
 
  this.selectedIndex = index; 

  this.activarFormulario() // Mostrar el formulario al seleccionar un elemento
  this.nuevo = false; // Desactivar el modo "nuevo"

  this.idSeleccionado=id;
  console.log("id seleccionado: "+this.idSeleccionado)

 this.isActivo=true;
 this.cdr.detectChanges();
}
/* Actualizar estado de Actividad Generadores mediante solicitud Update a estado */
cambiarEstado(id: number | undefined, estado: boolean): void {
  if (!id) {
    console.warn('ID no disponible.');
    return;
  }

  // Realiza el cambio de estado
  this._apiGeneradorService.cambioEstadoGenerador(id, estado).subscribe({
    next: (respuesta) => {
      console.log('Estado actualizado:', respuesta);
      const generadorActualizado = this.listaGeneradores.find((gen) => gen.id === id);
      if (generadorActualizado) {
        generadorActualizado.estado = estado; // Actualiza el estado en la lista local
      }
    },
    error: (error) => {
      console.error('Error al cambiar el estado:', error);
    }
  });
}

activarFormulario() {
  this.mostrarFormulario = true;
  // Forzar la actualización del tamaño del DOM
  this.cdr.detectChanges();
}
activarNuevo(): void {
  this.idSeleccionado = undefined; // No hay un ID seleccionado
 
 this.nuevo = true; // Activa el modo "nuevo"
  this.activarFormulario() // Muestra el formulario
}




}
