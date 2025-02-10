import {
  ChangeDetectorRef,
  Component,
  inject,
  OnInit,
  
  
} from '@angular/core';
import { Certificado } from '../../services/models/certificado.model';
import { ApiCertificadoService } from '../../services/api/api-certificado/api-certificado.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of, switchMap } from 'rxjs';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Meses } from 'src/app/services/models/Meses';
import { TablaTicketsForCertificateComponent } from "./tabla-tickets-for-certificate/tabla-tickets-for-certificate.component";
import {MatExpansionModule} from '@angular/material/expansion'
import {  Hoja} from 'src/app/services/models/hoja.model';
import { ApiHojaService } from 'src/app/services/api/api-hoja_ruta/api-hoja.service';
import { ITicket } from 'src/app/services/models/ticket.model';
@Component({
  selector: 'app-certificado',
  standalone: true,
  templateUrl: './certificado.component.html',
  styleUrls: ['./certificado.component.css'],
  imports: [
    CommonModule,
    FormsModule,
    MatInputModule,
    MatSelectModule,
    MatFormFieldModule,
    TablaTicketsForCertificateComponent,
    MatExpansionModule
],
})
export class CertificadoComponent implements OnInit {
  listaCertificados: Certificado[] = [];
  listaHojas:Hoja[]=[];
  
  selectTransportistaId?: number | null = 1;

  selectedCertificadoId?: number | undefined;

  private apiCertificado=inject(ApiCertificadoService);
  private apiHoja= inject(ApiHojaService);

  constructor(    
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
 
    this.cargarCertificadosbyTransportista(this.selectTransportistaId);
    this.cargarHojasByCertificado(this.selectedCertificadoId);
   
  }




  onSelectCertificado(certificadoId: number): void {
    this.selectedCertificadoId = certificadoId;
    this.cargarHojasByCertificado(certificadoId);
  }

  getTicketsDeHoja(hoja: Hoja): ITicket[] {
    if (!hoja ) {
      console.warn('Hoja de ruta o data es undefined:', hoja);
      return []; // Retorna un array vacío si no existe la hoja o la data
    }
    return hoja.listaTickets || []; // Accede a `ListaTickets` desde `data` o retorna un array vacío
  }


  onPanelOpened(hoja: Hoja): void {
    if (hoja ) {
      console.log('Panel abierto para la hoja de ruta:', hoja.id);
    } else {
      console.warn('Hoja de ruta o data no definida al abrir el panel:', hoja);
    }
  }
  cargarHojasByCertificado(certificadoId: number | undefined): void {
    if (!certificadoId) {
      console.warn('ID de certificado no proporcionado.');
      this.listaHojas = [];
      return;
    }
  
    this.apiHoja.getListHojasByCertificado(certificadoId).subscribe(
      (data:Hoja[] ) => {
        if (!data || data.length === 0) {
          console.warn('No se encontraron hojas de ruta para el certificado proporcionado.');
          this.listaHojas = [];
          return;
        }
  
        // Validar y filtrar hojas con datos válidos
        this.listaHojas = data
          .filter((hoja) => hoja?.fechaInicio)
          .sort((a, b) => {
            const fechaInicioA = new Date(a.fechaInicio).getTime();
            const fechaInicioB = new Date(b.fechaInicio).getTime();
            return fechaInicioA - fechaInicioB;
          });
  
        console.log('Hojas ordenadas:', this.listaHojas);
      },
      (error) => {
        console.error('Error al cargar las hojas de ruta:', error);
      }
    );
  }

  cargarCertificadosbyTransportista(transportistaId: number | null | undefined): void {
    if (!transportistaId) {
      this.listaCertificados = [];
      return;
    }

    this.apiCertificado.getFindCertificadosByTransportista(transportistaId).pipe(
      switchMap((data) => {
        console.log('Datos antes del ordenamiento:', data);
        const requests = data.map((numero) =>
          this.apiCertificado.getfindCertificado(numero.valueOf())
        );
        return forkJoin(requests);
      })
    ).subscribe(
      (certificados: Certificado[]) => {
        this.listaCertificados = certificados;
    
    // Función para convertir el nombre del mes a su valor numérico
    const obtenerValorMes = (mes: string): number => {
      return Meses[mes as keyof typeof Meses];
    };

        // Ordenar por año descendente, y mes descendente dentro del mismo año
         this.listaCertificados.sort((a, b) => {
          if (a.anio !== b.anio) {
            return b.anio - a.anio; // Año descendente
          }
          return obtenerValorMes(b.mes) - obtenerValorMes(a.mes); // Mes descendente
        }); 
    
        console.log('Certificados ordenados:', this.listaCertificados);
    
        this.cd.detectChanges();
      },
      (error) => {
        console.error('Error carga Certificados', error);
      }
    );
  }

  descargarReporte(id: number | undefined)
  {
    if(id!=undefined){
      this.apiCertificado.getReporteTicketById(id).subscribe(
        (response)=>{
          const nombreArchivo= `manifiesto_${id}.pdf`;
          this.apiCertificado.guardarArchivo(response,nombreArchivo);
        },
        (error)=>{
          console.error('Error al descargar el manifiesto:',error)
        }
      )
    }
  }
}