import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { PageResponse, TipoResiduo, TipoResiduoRequest } from '../models/operations.models';

@Injectable({ providedIn: 'root' })
export class TipoResiduoService {
  constructor(private readonly http: HttpClient) {}

  listAll(size = 200): Observable<TipoResiduo[]> {
    return this.fetchPage(0, size).pipe(
      switchMap((firstPage) => {
        const firstContent = firstPage.content ?? [];
        const totalPages = firstPage.totalPages ?? 1;

        if (totalPages <= 1) {
          return of(firstContent);
        }

        const rest = Array.from({ length: totalPages - 1 }, (_, index) => {
          return this.fetchPage(index + 1, size);
        });

        return forkJoin(rest).pipe(
          map((pages) => [
            ...firstContent,
            ...pages.flatMap((page) => page.content ?? []),
          ])
        );
      })
    );
  }

  listActivos(size = 200): Observable<TipoResiduo[]> {
    return this.listAll(size).pipe(
      map((tipos) => tipos.filter((tipo) => tipo.estadoActividad))
    );
  }

  create(request: TipoResiduoRequest): Observable<TipoResiduo> {
    return this.http.post<TipoResiduo>(`${API_BASE_URL}/api/tipos-residuo`, request);
  }

  update(id: number, request: TipoResiduoRequest): Observable<TipoResiduo> {
    return this.http.put<TipoResiduo>(`${API_BASE_URL}/api/tipos-residuo/${id}`, request);
  }

  toggleEstado(id: number): Observable<TipoResiduo> {
    return this.http.patch<TipoResiduo>(`${API_BASE_URL}/api/tipos-residuo/cambio-estado/${id}`, null);
  }

  private fetchPage(page: number, size: number): Observable<PageResponse<TipoResiduo>> {
    return this.http.get<PageResponse<TipoResiduo>>(
      `${API_BASE_URL}/api/tipos-residuo?page=${page}&size=${size}&sort=nombre,asc`
    );
  }
}
