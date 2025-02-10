import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Transportista } from 'src/app/services/models/transportista.model';

@Injectable({
  providedIn: 'root'
})
export class ApiTransportistaService {

private baseURL= 'http://localhost:8080/api/transportista';

constructor(private _httpCliente: HttpClient) {

}

public getTransportistas():Observable<Transportista[]>{
  const urlPeticion=`${this.baseURL}/Todos`;
return this._httpCliente.get<Transportista[]>(urlPeticion);
}
}
