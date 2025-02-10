import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, SimpleChanges, inject } from "@angular/core";
import {
  FormBuilder,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { ApiGeneradorService } from "../../../services/api/api-generador/api-generador.service";
import { Generador } from "../../../services/models/generador.model";
import { CommonModule } from "@angular/common";




@Component({
  selector: "app-generador-formulario",
  standalone: true,
  templateUrl: "./generador-formulario.component.html",
  styleUrls: ["./generador-formulario.component.css"],
  imports: [CommonModule, ReactiveFormsModule],
})
export class GeneradorFormularioComponent implements OnInit {


  estadoModificacion: boolean = true;

 @Input() nuevo: boolean = true;
@Output() estadoFormulario = new EventEmitter<{ estadoEdicion: boolean }>();

textoBotonSubmit: string= '';

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
  private apiGenerador=inject(ApiGeneradorService);
private cdr= inject(ChangeDetectorRef);

  formularioGenerador = this._formBuilder.nonNullable.group({
    nombre: ["", Validators.required],
    cuit:   [
              "",[
                  Validators.required,
                  Validators.min(9999999999),
                  Validators.max(99999999999),
                ],
            ],
    direccion:["", Validators.required],
    telefono: ["", Validators.required],
    estadoActividad: ["Activo", Validators.required],
    legajo: [""]
  });


  ngOnInit(): void {

    console.log(`el id recibido del componente padre es ${this.idRecibido}`)
    if (this.nuevo) {
      this.formularioGenerador.enable();
      this.estadoModificacion=false // Si es nuevo, habilita el formulario
    } else {
      this.formularioGenerador.disable(); // Deshabilita el formulario si no es nuevo
    }
  }



  ngOnChanges(changes: SimpleChanges): void {

 
    if (changes['idRecibido'] && changes['idRecibido'].currentValue !== undefined) {
      // Si se recibe un ID, activa el modo edición
      this.cargarDatosGenerador(changes['idRecibido'].currentValue);
      this.estadoFormulario.emit({ estadoEdicion: true });
      this.textoBotonSubmit=' Aceptar Cambios';
    } else if (changes['nuevo'] && changes['nuevo'].currentValue === true) {
      // Si se activa el modo "nuevo", limpia el formulario y activa la edición

      this.formularioGenerador.reset();
      this.formularioGenerador.enable();
      this.estadoModificacion=false;
      this.textoBotonSubmit='Crear Nuevo';
      this.estadoFormulario.emit({ estadoEdicion: true });
    }

    
  }

  cargarDatosGenerador(id: number): void {
    this.apiGenerador.getInfoGenerador(id).subscribe({
      next: (data) => {
        this.formularioGenerador.patchValue({
          nombre: data.nombre,
          cuit: data.cuit,
          direccion: data.direccion,
         legajo: data.legajo? data.legajo : 'No tiene',
        telefono: data.telefono? data.telefono : 'No registra telefono',
        
        });
        this.estadoModificacion = true; // Activa el modo de edición si hay datos cargados
      },
      error: (err) => {
        console.error('Error al cargar los datos del generador:', err);
 
      }
    });
  }
  

  /* Metodo para enviar solicitud get e ingresar nuevo Generador a  BD */
  onSubmit() { 
    const nombre= this.formularioGenerador.controls.nombre.value;
    const cuit= this.formularioGenerador.controls.cuit.value;
    const direccion= this.formularioGenerador.controls.direccion.value;
    const telefono=this.formularioGenerador.controls.telefono.value;
    const legajo= this.formularioGenerador.controls.legajo.value;
    let estado=true;
  
    let generadorSubmit :Generador=
    {
      nombre:nombre,
      cuit:cuit,
      direccion:direccion,
      telefono: telefono==='No registra telefono'? undefined:telefono,
    
      legajo: legajo==='No tiene'? undefined:legajo,
      estado:estado
    }

    if(this.idRecibido!=null){
  
      this.apiGenerador.updateGenerador(generadorSubmit,this.idRecibido).subscribe(
        response => {

          this.enviarDatos(response['message'],true);
          
         
          setTimeout(()=>{
            location.reload();
          },4000)
        },
        error => {
          const errorMessage = error?.error?.message || 'Error desconocido';

          this.enviarDatos('Error al crear el generador '+ errorMessage,false)

          console.log(error)
        }
    );
     
    }
    else
    {
     

      this.apiGenerador.crearGenerador(generadorSubmit).subscribe(
        response => {
          console.log(response);
         
          this.enviarDatos(response['message'],true) ;
        
          setTimeout(()=>{
            location.reload();
          },4000)
         
      },
        error => {
       
          const errorMessage = error?.error?.message || 'Error desconocido';

          this.enviarDatos('Error al crear el generador : '+ errorMessage,false)

          console.log(error)
      }

      )  
    }
   
  }


  hablitarEdicion() {
    this.formularioGenerador.enable();
    this.estadoModificacion=false;
    this.cdr.markForCheck();

  }

  hasErrors(controlNombre: string, errorType: string) {
    return (
      this.formularioGenerador.get(controlNombre)?.hasError(errorType) &&
      this.formularioGenerador.get(controlNombre)?.touched
    );
  }

}
