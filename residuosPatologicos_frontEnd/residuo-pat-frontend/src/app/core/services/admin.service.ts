import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { AdminUser } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(private readonly http: HttpClient) {}

  getTransportistasPendientes(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${API_BASE_URL}/api/admin/usuarios/transportistas/pendientes`);
  }

  aprobarUsuario(usuarioId: number): Observable<AdminUser> {
    return this.http.patch<AdminUser>(`${API_BASE_URL}/api/admin/usuarios/${usuarioId}/aprobar`, {});
  }

  rechazarUsuario(usuarioId: number): Observable<AdminUser> {
    return this.http.patch<AdminUser>(`${API_BASE_URL}/api/admin/usuarios/${usuarioId}/rechazar`, {});
  }

  suspenderUsuario(usuarioId: number): Observable<AdminUser> {
    return this.http.patch<AdminUser>(`${API_BASE_URL}/api/admin/usuarios/${usuarioId}/suspender`, {});
  }
}
