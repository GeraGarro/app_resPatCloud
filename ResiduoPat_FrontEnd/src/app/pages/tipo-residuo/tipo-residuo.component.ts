import { ChangeDetectorRef, Component, HostListener, inject, Input, OnInit, ViewChild } from '@angular/core';
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
listaTipoResiduos: TipoResiduo[]=[];



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

private cdr= inject(ChangeDetectorRef)


constructor(private _apiTipoResiduo:ApiServicesTipoResiduosService ){
 
  }


ngOnInit(): void {
  this.cargaTipoResiduos();
}


cargaTipoResiduos():void{
  this._apiTipoResiduo.getTipoResiduos().subscribe(
    data=>{
      this.listaTipoResiduos=data;
      this.listaTipoResiduos=data.sort((a,b)=>{
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
      const generadorActualizado = this.listaTipoResiduos.find((gen) => gen.id === id);
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