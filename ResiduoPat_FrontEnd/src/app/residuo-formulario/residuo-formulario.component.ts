import { Component, OnInit } from '@angular/core';
import { FormGroup, Validators,FormBuilder,ReactiveFormsModule} from '@angular/forms';
import { ApiServicesTipoResiduosService } from '../services/api/api-tipoResiduos/api.services-tipo-residuos.service';
import { TipoResiduo } from '../services/models/tipo_Residuos';


@Component({
  selector: 'app-residuo-formulario',
  templateUrl: './residuo-formulario.component.html',
  styleUrls: ['./residuo-formulario.component.css'],
  standalone:true
})
export class ResiduoFormularioComponent implements OnInit{
  modoEdicion: boolean = false;
  formularioResiduo:FormGroup;
  tipo_residuo: TipoResiduo[] = [];
constructor(private form: FormBuilder,private api_tipo: ApiServicesTipoResiduosService){
this.formularioResiduo=this.form.group({
  tipo_residuo:['',Validators.required],
 peso:['',[Validators.required,Validators.min(0.01)]]
})

}


ngOnInit(): void {
    this.cargarTipoResiduo();
}
  hasErrors(controlNombre : string, errorType: string){
    return this.formularioResiduo.get(controlNombre)?.hasError(errorType) &&
        this.formularioResiduo.get(controlNombre)?.touched
    }

    enviar(){

    }

    cargarTipoResiduo(): void{
      this.api_tipo.getTipoResiduos().subscribe(
        (data:TipoResiduo[])=>{
          this.tipo_residuo=data;
        },
          error => {
            console.error('Error al obtener Tipo Residuos', error);
          }
        );
      }
      
}
