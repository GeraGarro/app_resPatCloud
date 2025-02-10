import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import { ITicket} from 'src/app/services/models/ticket.model';
import { environment } from 'src/environments/environment';
@Injectable({
  providedIn: 'root'
})
export class ApiTicketService {

  private baseURL = environment.apiUrl;  

  constructor(private _httpClient: HttpClient) { }

  public getTickets(): Observable<ITicket[]> {
    const getTodosTicket=`${this.baseURL}/TicketControl/verTodos`;
    return this._httpClient.get<ITicket[]>(getTodosTicket);
  }
 
  public getTicketsByHoja(id: number): Observable<ITicket[]>{
    const url= `${this.baseURL}/TicketControl/hoja-ruta/${id}`;
    return this._httpClient.get<ITicket[]>(url);
  }
  public eliminarTicket(id:number):Observable<any>{
    const urlDelete=`${this.baseURL}/TicketControl/eliminar/${id}`;
    return this._httpClient.delete<any>(urlDelete);
  }

  public addTicket(ticket:ITicket):Observable<any>{
const urlAdd= `${this.baseURL}/TicketControl/crear`;
    return this._httpClient.post<any>(urlAdd,ticket);
  }

  public updateTicket(id_Ticket:number, ticket: ITicket): Observable<any>{
    const urlUpdate=`${this.baseURL}/TicketControl/editar/${id_Ticket}`;
    return this._httpClient.put<any>(urlUpdate,ticket);
  }

  public getTicketById(id: number): Observable<ITicket> {
    const url = `${this.baseURL}/TicketControl/UnTicket/${id}`;
    return this._httpClient.get<ITicket>(url);
  }

  public getReporteTicketById(id:number){
    const urlReport=`${this.baseURL}/TicketControl/generadorPDFNav/${id}`;
    return this._httpClient.get(urlReport,{
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

  actualizarEstado(id: number, estado: boolean): Observable<void> {
    const url = `${this.baseURL}/TicketControl/${id}/estado`; // Construir la URL dinámica
    return this._httpClient.patch<void>(url, { estado });
  }
}
