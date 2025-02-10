import { ChangeDetectorRef, Component, inject, Input, OnInit, ViewChild } from '@angular/core';
import { TipoResiduo } from '../../services/models/tipo_Residuos';
import { ApiServicesTipoResiduosService } from '../../services/api/api-tipoResiduos/api.services-tipo-residuos.service';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ReactiveFormsModule } from '@angular/forms';
import { MatRadioModule } from '@angular/material/radio';
import { RouterModule } from '@angular/router';
import { RotacionCircularDirective } from 'src/app/directivas/rotacion-circular.directive';
import { TipoResiduoFormularioComponent } from './tipo-residuo-formulario/tipo-residuo-formulario.component';
import { FormsModule } from '@angular/forms';
import { ModalComponent } from 'src/app/modal/modal.component';

@Component({
  selector: 'app-tipo-residuo',
  templateUrl: './tipo-residuo.component.html',
  standalone:true,
  imports: [
    RouterModule,
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatRadioModule,
    FormsModule,
    TipoResiduoFormularioComponent,
    ModalComponent
],
  styleUrls: ['./tipo-residuo.component.css']
})
export class TipoResiduoComponent implements OnInit{
ListaTipoResiduos: TipoResiduo[]=[];
ListaTiposActivos: TipoResiduo[]=[];
ListaTiposInactivos: TipoResiduo[]=[];

mensajeDeFormulario:string="Mensaje recibido de formulario";
activarModal:boolean=false;
estadoModal:boolean=false;

recepcionDatos(event: any): void {
  const modal = event as { mensajeEnviar: string; activarModal: boolean ,estadoAprobado:boolean};
  this.mensajeDeFormulario = modal.mensajeEnviar;
  this.activarModal = modal.activarModal;
this.estadoModal=modal.estadoAprobado;
console.log("estadoModal: "+this.estadoModal )
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

idSeleccionado: number | undefined;
mostrarFormulario: boolean = false;

isActivo:boolean=false;

selectedIndex: number | null = null;
@Input()nuevo: boolean = false;


private currentOffset = 0; // Mantiene la posición actual del desplazamiento
private itemHeight = 200; // Altura de cada item (en píxeles, ajusta según tu diseño)
private visibleItemsCount = 4; // Número de ítems visibles a la vez (ajusta según el contenedor)
private cdr= inject(ChangeDetectorRef)
items: string[] = Array.from({ length: 30 }, (_, i) => `Elemento ${i + 1}`);

constructor(private _apiTipoResiduo:ApiServicesTipoResiduosService ){
 
  }


ngOnInit(): void {
  this._apiTipoResiduo.getTipoResiduos().subscribe(
    data=>{
      this.ListaTipoResiduos=data;
      this.ListaTipoResiduos=data.sort((a,b)=>{
        return a.nombre.localeCompare(b.nombre)
      });
   this.cdr.detectChanges();
    },
    error=>{
      console.error('error Listar Tipos', error)
    }
  );
}




seleccionarElemento(index: number, id:number|undefined) {
 
  this.selectedIndex = index; 

  this.activarFormulario() // Mostrar el formulario al seleccionar un elemento
  this.nuevo = false; // Desactivar el modo "nuevo"

  this.idSeleccionado=id;
  console.log("id seleccionado: "+this.idSeleccionado)

 this.isActivo=true;

}



/* Actualizar estado de Actividad Generadores mediante solicitud Update a estado */
cambiarEstado(id: number | undefined, estado: boolean): void {
  if (!id) {
    console.warn('ID no disponible.');
    return;
  }


  
  // Realiza el cambio de estado
  this._apiTipoResiduo.cambioEstadoTipo(id, estado).subscribe({
    next: (respuesta) => {
      console.log('Estado actualizado:', respuesta);
      const generadorActualizado = this.ListaTipoResiduos.find((gen) => gen.id === id);
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
  console.log(`estado mostrar formulario: ${this.mostrarFormulario}`)
  // Forzar la actualización del tamaño del DOM
  this.cdr.detectChanges();
}

activarNuevo(): void {
  this.idSeleccionado = undefined; // No hay un ID seleccionado

  this.nuevo = true; // Activa el modo "nuevo"

  this.activarFormulario() // Muestra el formulario

}

controlarVisibilidadFormulario(event: { estadoEdicion: boolean }): void {
  // Recibe el estado del componente hijo y controla la visibilidad del formulario
  this.mostrarFormulario = event.estadoEdicion;
}


scrollUp() {
  // Límite superior
  const maxOffset = 0;
  this.currentOffset = Math.min(this.currentOffset + this.itemHeight, maxOffset);
  this.updateTransform();
}

scrollDown() {
  // Límite inferior
  const maxOffset = -this.itemHeight * (this.ListaTipoResiduos.length - this.visibleItemsCount);
  this.currentOffset = Math.max(this.currentOffset - this.itemHeight, maxOffset);
  this.updateTransform();
}
private updateTransform() {
  const listaElement = document.querySelector('.contenedor-lista') as HTMLElement;
  if (listaElement) {
    listaElement.style.transform = `translateY(${this.currentOffset}px)`;
  }
}
}