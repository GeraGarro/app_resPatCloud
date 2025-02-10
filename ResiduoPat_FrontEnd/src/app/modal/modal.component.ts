import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal',
  standalone:true,
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css'],
  imports:[CommonModule]
})
export class ModalComponent {

@Input() _mensaje: string='';
@Input()  estadoAprobado: boolean = true;

set resultadoAModal(isSuccessful:boolean){
  this.estadoAprobado=isSuccessful;
}

get resultadoAModal():boolean{
  return this.estadoAprobado
}

set mensaje(value: string | undefined) {
  this._mensaje = value || ''; // Asignar un valor predeterminado si es undefined
}

get mensaje(): string {
  return this._mensaje;
}
  constructor(){
    
  }

  openModal:boolean=true;

  @Output() accionModal=new EventEmitter<boolean>()
  
   accionBotonModal(){
    this.openModal = !this.openModal;
    this.accionModal.emit(this.openModal)
  }
 
}
