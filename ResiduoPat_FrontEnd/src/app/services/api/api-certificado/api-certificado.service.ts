import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Certificado } from 'src/app/services/models/certificado.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiCertificadoService {

  private baseURL=environment.apiUrl;

  constructor(private httpSOlicitud: HttpClient) { }

  public getCertificados():Observable<Certificado[]>
  {const urlPeticion=`${this.baseURL}/Certificado/verTodos`;
    return this.httpSOlicitud.get<Certificado[]>(urlPeticion)
  }

  public getfindCertificado(id:number):Observable<Certificado>
  {
const urlPeticion=`${this.baseURL}/Certificado/infocertificado/${id}`;
return this.httpSOlicitud.get<Certificado>(urlPeticion)
  }

  public getFindCertificadosByTransportista(id:number):Observable<Number[]>
  {
    const urlPeticion=`${this.baseURL}/Certificado/FiltroTransportista/${id}`;
    return this.httpSOlicitud.get<Number[]>(urlPeticion);
  }

  public saveNewCertificado(certificado:Certificado):Observable<any>{
    const urlAdd=`${this.baseURL}/Certificado/crear`;
    return this.httpSOlicitud.post<any>(urlAdd,certificado)
  }

  public getReporteTicketById(id:number){
    const urlReport=`${this.baseURL}/Certificado/generadorPDF/${id}`;
    return this.httpSOlicitud.get(urlReport,{
      responseType: 'blob',
      observe: 'response'
    })
  }

  guardarArchivo(response: any, nombreArchivo:string){
    const blob= new Blob ([response.body],{type: 'application/pdf'});
    const url= window.URL.createObjectURL(blob);

    const link= document.createElement('a');
    link.href=url;
    link.download=nombreArchivo;
    link.click();

    //Limpieza
    window.URL.revokeObjectURL(url);
  }

}
