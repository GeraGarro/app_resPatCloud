import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { Generador, GeneradorRequest, PageResponse } from '../models/operations.models';

@Injectable({ providedIn: 'root' })
export class GeneradorService {
  constructor(private readonly http: HttpClient) {}

  listActivos(size = 500): Observable<Generador[]> {
    return this.http
      .get<PageResponse<Generador>>(`${API_BASE_URL}/api/generadores/activos?size=${size}`)
      .pipe(map((page) => page.content ?? []));
  }

  listAll(): Observable<Generador[]> {
    return this.http
      .get<PageResponse<Generador>>(`${API_BASE_URL}/api/generadores?size=500`)
      .pipe(map((page) => page.content ?? []));
  }

  create(request: GeneradorRequest): Observable<Generador> {
    return this.http.post<Generador>(`${API_BASE_URL}/api/generadores`, request);
  }

  update(id: number, request: GeneradorRequest): Observable<Generador> {
    return this.http.put<Generador>(`${API_BASE_URL}/api/generadores/${id}`, request);
  }
}
