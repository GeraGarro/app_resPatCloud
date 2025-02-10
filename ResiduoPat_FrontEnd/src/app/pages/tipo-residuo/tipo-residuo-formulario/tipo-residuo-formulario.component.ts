import { Component, Input,SimpleChanges, Output, EventEmitter,ChangeDetectorRef, inject, OnInit,    } from '@angular/core';
import { TipoResiduo } from '../../../services/models/tipo_Residuos';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiServicesTipoResiduosService } from '../../../services/api/api-tipoResiduos/api.services-tipo-residuos.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-tipo-residuo-formulario',
  standalone:true,
    imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './tipo-residuo-formulario.component.html',
  styleUrls: ['./tipo-residuo-formulario.component.css']
})
export class TipoResiduoFormularioComponent implements OnInit{



  estadoModificacion: boolean = true;

  @Input() nuevo: boolean = true;
  @Output() estadoFormulario = new EventEmitter<{ estadoEdicion: boolean }>();
  textoBotonSubmit: string= '';
  nombreTituloForm: string='';

  @Input() idRecibido!: number | undefined;

  @Output() enviarMensaje=new EventEmitter<{mensajeEnviar:string, activarModal: boolean,estadoAprobado:boolean}>();

  enviarDatos(mensaje: string, estadoAprobado:boolean): void {
    const datos = {
      mensajeEnviar: mensaje,
      activarModal: true,
      estadoAprobado: estadoAprobado
    };
    this.enviarMensaje.emit(datos);
  }

  private readonly _formBuilder = inject(FormBuilder);
  private apiTipoResiduo=inject(ApiServicesTipoResiduosService);
private cdr= inject(ChangeDetectorRef);
  

 

    formularioTipoResiduo=this._formBuilder.nonNullable.group(
      {
        nombre_tipo:['',Validators.required],
        codigo_tipo:['']
      });
   
ngOnInit(): void {
  console.log(`el id recibido de componente padre es ${this.idRecibido}`)
  if (this.nuevo) {
    this.formularioTipoResiduo.enable();
    this.estadoModificacion=false // Si es nuevo, habilita el formulario
  } else {
    this.estadoModificacion=true
    this.formularioTipoResiduo.disable(); // Deshabilita el formulario si no es nuevo
  }
  
}
    
ngOnChanges(changes:SimpleChanges):void{
  if (changes['idRecibido'] && changes['idRecibido'].currentValue !== undefined) {
    // Si se recibe un ID, activa el modo edición

    this.cargaDatosUnTipo(changes['idRecibido'].currentValue);
    this.estadoFormulario.emit({ estadoEdicion: true });
    this.textoBotonSubmit=' Aceptar Cambios';
    this.nombreTituloForm= 'Editar Tipo Residuo'
  } else if (changes['nuevo'] && changes['nuevo'].currentValue === true) {
    // Si se activa el modo "nuevo", limpia el formulario y activa la edición

    this.formularioTipoResiduo.reset();
    this.formularioTipoResiduo.enable();
    this.nombreTituloForm='Ingresar Nueva Clasificación Residuo';
    this.estadoModificacion=false;
    this.textoBotonSubmit='Crear Nuevo';
    this.estadoFormulario.emit({ estadoEdicion: true });
  }
}
//carga de datos de un residuo TipoResiduo
cargaDatosUnTipo(id: number):void{
  this.apiTipoResiduo.getInfoTipoResiduo(id).subscribe({
    next: (data)=> {
   
      this.formularioTipoResiduo.patchValue({
        nombre_tipo: data.nombre,
        codigo_tipo: data.codigo,
      
      });
      
      this.estadoModificacion=true;
      this.cdr.markForCheck();
    },
    error:(err)=>{
    console.error('Error al cargar los datos de Clasificación de Residuos '+ err);
    }
  });
}

onSubmit(){
  const codigo_tipo= this.formularioTipoResiduo.controls.codigo_tipo.value;
  const nombre_tipo= this.formularioTipoResiduo.controls.nombre_tipo.value;
  let estado=true;

  let tipo_ResiduoSubmit: TipoResiduo=
  {
   nombre:nombre_tipo,
   codigo:codigo_tipo,
   estado:estado

  }
   if(this.idRecibido!=null){
    this.apiTipoResiduo.updateTipoResiduo(tipo_ResiduoSubmit,this.idRecibido).subscribe(
      response =>{
     
        this.enviarDatos(response['message'],true);
      
      setTimeout(()=>{
        location.reload();
      },4000)
      },
      error=>{
        const errorMessage = error?.error?.message || 'Error desconocido';

        this.enviarDatos('Error al crear Nueva Clasificación '+ errorMessage,false)

        console.log(error)
      }
    )
   }else{
    this.apiTipoResiduo.agregarTipoResiduo(tipo_ResiduoSubmit).subscribe(
      response =>{
        this.enviarDatos(response['message'],true) ;
        
        setTimeout(()=>{
          location.reload();
        },4000)
      
      },
      error=>{
        const errorMessage = error?.error?.message || 'Error desconocido';

        this.enviarDatos('Error al crear el Clasificacion Residuo : '+ errorMessage,false)

        console.log(error)
      }
    )
   }
}

hablitarEdicion() {
  this.formularioTipoResiduo.enable();
  this.estadoModificacion=false;
  this.cdr.markForCheck();

}

hasErrors(controlNombre: string, errorType: string) {
  return (
    this.formularioTipoResiduo.get(controlNombre)?.hasError(errorType) &&
    this.formularioTipoResiduo.get(controlNombre)?.touched
  );
}

}
