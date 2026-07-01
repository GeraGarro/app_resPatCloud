import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { Transportista, TransportistaProfileStatus, TransportistaRequest } from '../models/operations.models';

@Injectable({ providedIn: 'root' })
export class TransportistaService {
  constructor(private readonly http: HttpClient) {}

  create(request: TransportistaRequest): Observable<Transportista> {
    return this.http.post<Transportista>(`${API_BASE_URL}/api/transportistas`, request);
  }

  update(idTransportista: number, request: TransportistaRequest): Observable<Transportista> {
    return this.http.put<Transportista>(`${API_BASE_URL}/api/transportistas/update/${idTransportista}`, request);
  }

  getCurrent(): Observable<Transportista> {
    return this.http.get<Transportista>(`${API_BASE_URL}/api/transportistas/me`);
  }

  getProfileStatus(): Observable<TransportistaProfileStatus> {
    return this.http.get<TransportistaProfileStatus>(`${API_BASE_URL}/api/transportistas/me/estado-perfil`);
  }
}
