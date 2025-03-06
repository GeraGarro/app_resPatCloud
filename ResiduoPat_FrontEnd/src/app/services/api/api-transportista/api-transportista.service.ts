import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Transportista } from 'src/app/services/models/transportista.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiTransportistaService {

  private baseURL=environment.apiUrl;

constructor(private _httpCliente: HttpClient) {

}

public getTransportistas():Observable<Transportista[]>{
  const urlPeticion=`${this.baseURL}/transportista/Todos`;
return this._httpCliente.get<Transportista[]>(urlPeticion);
}

public getTransportistaById(id:number):Observable<Transportista>{
  const urlPeticion=`${this.baseURL}/transportista/${id}`;
  return this._httpCliente.get<Transportista>(urlPeticion);
}

public createTransportista(transportista:Transportista):Observable<any>{
  const urlPeticion=`${this.baseURL}/transportista/crear`;
  return this._httpCliente.post<Transportista>(urlPeticion,transportista);
}
}
