import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {  Hoja_ruta } from 'src/app/services/models/hoja_ruta.model';
import { Hoja } from '../../models/hoja.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiHojaService {

  private baseURL= environment.apiUrl
  
  constructor(private _httpClient: HttpClient) { }

public getHojaActual(): Observable<Hoja_ruta >{
  const url= `${this.baseURL}/HojaRuta/actual`;

  return this._httpClient.get<Hoja_ruta>(url);
 }

 public getListHojasByCertificado(id:number):Observable<Hoja[]>{
  const url= `${this.baseURL}/HojaRuta/hojasPorCertificado/${id}`;
  return this._httpClient.get<Hoja[]>(url);
 }
}
