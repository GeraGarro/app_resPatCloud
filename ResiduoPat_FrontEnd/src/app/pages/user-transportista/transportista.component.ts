import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, Validators, FormBuilder } from '@angular/forms';
import { Transportista } from '../../services/models/transportista.model';
import { ApiTransportistaService } from '../../services/api/api-transportista/api-transportista.service';
import { ModalComponent } from '../../modal/modal.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-transportista',
  standalone:true,
  templateUrl: './transportista.component.html',
  styleUrls: ['./transportista.component.css'],
  imports: [CommonModule, ReactiveFormsModule,  ModalComponent],
})
export class TransportistaComponent {

  mensajeDeFormulario:string="Mensaje recibido de formulario Mensaje recibido de formulario ";
  activarModal:boolean=false;
  estadoModal:boolean=false;

  private readonly _formBuilder = inject(FormBuilder);
  private apiTransportista=inject(ApiTransportistaService);
private router=inject(Router);

  formularioTransportista = this._formBuilder.nonNullable.group({
    nombre: ["", Validators.required],
    apellido: ["", Validators.required],
    email: ["", Validators.required],
    cuit: ["", Validators.required],
    telefono: ["", Validators.required],
    domicilio: ["", Validators.required]
   
  });


  onSubmit() {
   const nombre= this.formularioTransportista.controls.nombre.value;
   const apellido= this.formularioTransportista.controls.apellido.value;
   const email= this.formularioTransportista.controls.email.value;
   const cuit= this.formularioTransportista.controls.cuit.value;
   const telefono= this.formularioTransportista.controls.telefono.value;
   const domicilio= this.formularioTransportista.controls.domicilio.value;
   let estado=true;

   let transportistaSubmit:Transportista={
    nombre:nombre,
    apellido:apellido,
    email:email,
    cuit:cuit,
    telefono:telefono,
    domicilio:domicilio,
    estado:estado
  }
  this.apiTransportista.createTransportista(transportistaSubmit).subscribe(
    response=>{
      console.log(response);

     this.mensajeDeFormulario = response['message'];
     this.activarModal=true;
     this.estadoModal=true;

      setTimeout(()=>{
        this.router.navigate(['/home']);
      },4000)
    },
    error=>{

      this.mensajeDeFormulario = 'Error al crear el transportista : '+ error.error.message;
      this.activarModal=true;
      this.estadoModal=false;
      setTimeout(()=>{
        this.activarModal=false;
      },3000)
    }
  )
  }


  
    hasErrors(controlNombre: string, errorType: string) {
    return (
      this.formularioTransportista.get(controlNombre)?.hasError(errorType) &&
      this.formularioTransportista.get(controlNombre)?.touched
    );
  }
}
