import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { Vehiculo, VehiculoRequest } from '../models/operations.models';

@Injectable({ providedIn: 'root' })
export class VehiculoService {
  constructor(private readonly http: HttpClient) {}

  listByTransportista(idTransportista: number): Observable<Vehiculo[]> {
    return this.http.get<Vehiculo[]>(`${API_BASE_URL}/api/vehiculos/transportista/${idTransportista}`);
  }

  create(idTransportista: number, request: VehiculoRequest): Observable<Vehiculo> {
    return this.http.post<Vehiculo>(`${API_BASE_URL}/api/vehiculos/${idTransportista}`, {
      ...request,
      idTransportista,
    });
  }

  update(idVehiculo: number, request: VehiculoRequest): Observable<Vehiculo> {
    return this.http.put<Vehiculo>(`${API_BASE_URL}/api/vehiculos/${idVehiculo}`, request);
  }

  toggleEstado(idVehiculo: number): Observable<string> {
    return this.http.patch(`${API_BASE_URL}/api/vehiculos/cambio-estado/${idVehiculo}`, null, {
      responseType: 'text',
    });
  }
}
