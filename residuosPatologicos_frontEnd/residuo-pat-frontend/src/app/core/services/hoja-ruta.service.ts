import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { HojaRuta, PageResponse, TicketControl } from '../models/operations.models';

@Injectable({ providedIn: 'root' })
export class HojaRutaService {
  constructor(private readonly http: HttpClient) {}

  getById(id: number): Observable<HojaRuta> {
    return this.http.get<HojaRuta>(`${API_BASE_URL}/api/hojas-ruta/${id}`);
  }

  getActual(): Observable<HojaRuta> {
    return this.http.get<HojaRuta>(`${API_BASE_URL}/api/hojas-ruta/actual`);
  }

  getTickets(id: number, size = 200): Observable<PageResponse<TicketControl>> {
    return this.http.get<PageResponse<TicketControl>>(
      `${API_BASE_URL}/api/hojas-ruta/${id}/tickets?size=${size}`
    );
  }

  downloadPdf(id: number): Observable<Blob> {
    return this.http.get(`${API_BASE_URL}/api/hojas-ruta/${id}/informe/pdf`, {
      responseType: 'blob',
    });
  }
}
