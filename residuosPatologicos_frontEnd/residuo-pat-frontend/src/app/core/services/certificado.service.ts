import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import {
  Certificado,
  HojaRuta,
  HojaRutaConTickets,
  PageResponse,
  TicketControl
} from '../models/operations.models';

@Injectable({ providedIn: 'root' })
export class CertificadoService {
  constructor(private readonly http: HttpClient) {}

  downloadManifiestoTicket(ticketId: number): Observable<Blob> {
    return this.http.get(`${API_BASE_URL}/api/ticket-control/${ticketId}/manifiesto/pdf`, {
      responseType: 'blob',
    });
  }

  downloadCertificado(certificadoId: number): Observable<Blob> {
    return this.http.get(`${API_BASE_URL}/api/certificados/${certificadoId}/pdf`, {
      responseType: 'blob',
    });
  }

  procesarHojasVencidas(): Observable<number> {
    return this.http
      .post<number>(`${API_BASE_URL}/api/ticket-control/procesar-hojas-vencidas`, null)
      .pipe(catchError(() => of(0)));
  }

  getByTransportista(transportistaId: number): Observable<Certificado[]> {
    return this.http
      .get<PageResponse<Certificado>>(
        `${API_BASE_URL}/api/certificados/transportista/${transportistaId}?size=100&sort=anio,desc&sort=mes,desc`
      )
      .pipe(
        map((page) => (page.content ?? []).sort((a, b) => {
          if (a.anio !== b.anio) {
            return b.anio - a.anio;
          }

          return this.mesOrden(b.mes) - this.mesOrden(a.mes);
        }))
      );
  }

  getHojasPendientesCertificado(transportistaId: number): Observable<HojaRuta[]> {
    return this.http.get<HojaRuta[]>(
      `${API_BASE_URL}/api/hojas-ruta/pendientes-certificado/transportista/${transportistaId}`
    );
  }

  getHojasConTickets(certificado: Certificado): Observable<HojaRutaConTickets[]> {
    return this.http
      .get<HojaRutaConTickets[]>(`${API_BASE_URL}/api/certificados/${certificado.id}/hojas-con-tickets`)
      .pipe(
        map((hojas) => hojas.sort((a, b) => a.fechaInicio.localeCompare(b.fechaInicio)))
      );
  }

  getTicketsDeHoja(hojaRutaId: number): Observable<TicketControl[]> {
    return this.http
      .get<PageResponse<TicketControl>>(`${API_BASE_URL}/api/hojas-ruta/${hojaRutaId}/tickets?size=200`)
      .pipe(map((page) => page.content ?? []));
  }

  private mesOrden(mes: string): number {
    return [
      'ENERO',
      'FEBRERO',
      'MARZO',
      'ABRIL',
      'MAYO',
      'JUNIO',
      'JULIO',
      'AGOSTO',
      'SEPTIEMBRE',
      'OCTUBRE',
      'NOVIEMBRE',
      'DICIEMBRE',
    ].indexOf(mes);
  }
}
