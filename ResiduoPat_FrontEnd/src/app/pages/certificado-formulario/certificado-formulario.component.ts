import { CommonModule, KeyValue } from '@angular/common';
import { Component, inject, Inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, } from '@angular/forms';
import { Transportista } from '../../services/models/transportista.model';
import { ApiTransportistaService } from '../../services/api/api-transportista/api-transportista.service';
/* import { desplegableCustom } from '../../directivas/desplegable'; */
import { DesplegableCustomDirective } from '../../directivas/desplegable';
import { Meses } from '../../services/models/certificado.model';
import { ApiCertificadoService } from '../../services/api/api-certificado/api-certificado.service';
import { ApiError } from '../../services/models/ApiError';
import { ResponseData } from '../../services/models/ResponseData';
import { ModalComponent } from '../../modal/modal.component';
import { Router } from '@angular/router';
@Component({
  selector: 'app-certificado-formulario',
  standalone: true,
  templateUrl: './certificado-formulario.component.html',
  styleUrls: ['./certificado-formulario.component.css'],
  imports: [CommonModule, ReactiveFormsModule, DesplegableCustomDirective,ModalComponent],
})
export class CertificadoFormularioComponent {
  listaTransportistas: Transportista[] = [];
  mesesLista: Map<string, number> = new Map<string, number>();
  fechaActual = new Date();
  selectTransportistaId?: number | null;
  formularioCertificado: FormGroup;
  private apiTransportista = Inject(ApiTransportistaService);
  listaAnios?: Number[] = [];
  meses = Meses;
  modal=false;
  private readonly router=inject(Router);
  mensajeModal:string='';

  /* private apiCertificado = Inject(ApiCertificadoService) */
  //
  constructor(
    private form: FormBuilder,
    private apiTrans: ApiTransportistaService,
    private apiCertificado: ApiCertificadoService
  ) {
    this.formularioCertificado = this.form.group({
      transportista: [null, Validators.required],
      mes: [null, Validators.required],
      anio: [null, Validators.required],
    });

  
  }
  //

  ngOnInit(): void {
    this.apiTrans.getTransportistas().subscribe(
      (dataTransportistas) => {
        this.listaTransportistas = dataTransportistas;
        console.log(this.listaTransportistas)
      },
      (error) => {
        console.error('Error carga Transportista', error);
      }
    );

    for (const mesNombre of Object.keys(Meses)) {
      if (isNaN(Number(mesNombre))) {
        // Filtra solo las claves que no son números
        const mesValor = Meses[mesNombre as keyof typeof Meses];
        this.mesesLista.set(mesNombre, mesValor); // Agregar al Map (clave, valor)
      }
    }
    const anioActual = this.fechaActual.getFullYear();
    for (let i = anioActual; i >= 2010; i--) {
      this.listaAnios?.push(i);
    }

    const mesActual = this.fechaActual.getMonth() + 1;
  const nombreMesActual = this.obtenerNombreMes(mesActual);



  // Inicializar el formulario con el valor numérico del mes actual
  this.formularioCertificado.patchValue({
    mes: nombreMesActual,
    anio: anioActual,
  });


  }

  private obtenerNombreMes(valorMes: number): Meses{
    for (const [nombre, valor] of this.mesesLista) {
      if (valor === valorMes) {
        return Meses[nombre as keyof typeof Meses];
      }
    }
    // Si el mes no se encuentra en el enum, podrías devolver un valor predeterminado o manejar el caso según lo necesites.
    return Meses.ENERO; // Por ejemplo, devuelves ENERO si no se encuentra ningún mes.
  }

  getNombreMes(valor: number): string {
    return Meses[valor];
  }


   compareFn(
    a: { key: string; value: number },
    b: { key: string; value: number }
  ): number {
    return a.value - b.value;
  }

  seleccionarTransportista(transportista: Transportista) {
    // Actualizar el valor del campo 'transportista' en el formulario
    this.formularioCertificado.patchValue({
      transportista: transportista
    });
  }

  seleccionarMes(item: KeyValue<string, number>): void {
    this.formularioCertificado.patchValue({ mes: item.value });
  }


}
