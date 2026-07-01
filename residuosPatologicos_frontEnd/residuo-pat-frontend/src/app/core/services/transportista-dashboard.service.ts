import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import {
  Certificado,
  Generador,
  HojaRuta,
  PageResponse,
  TicketControl,
  TipoResiduo,
  Transportista,
  TransportistaDashboardData
} from '../models/operations.models';

@Injectable({ providedIn: 'root' })
export class TransportistaDashboardService {
  constructor(private readonly http: HttpClient) {}

  downloadInformeHojaRuta(hojaRutaId: number): Observable<Blob> {
    return this.http.get(`${API_BASE_URL}/api/hojas-ruta/${hojaRutaId}/informe/pdf`, {
      responseType: 'blob',
    });
  }

  load(): Observable<TransportistaDashboardData> {
    return this.safe<number>(
      this.http.post<number>(`${API_BASE_URL}/api/ticket-control/procesar-hojas-vencidas`, null),
      0
    ).pipe(
      switchMap(() => this.safe<Transportista | null>(
        this.http.get<Transportista>(`${API_BASE_URL}/api/transportistas/me`),
        null
      )),
      switchMap((transportistaActual) => forkJoin({
        hojaActual: this.safe<HojaRuta | null>(
          this.http.get<HojaRuta>(`${API_BASE_URL}/api/hojas-ruta/actual`),
          null
        ),
        ticketsActuales: this.safePage<TicketControl>(
          this.http.get<PageResponse<TicketControl>>(`${API_BASE_URL}/api/hojas-ruta/actual/tickets?size=100`)
        ),
        generadoresActivos: this.safePage<Generador>(
          this.http.get<PageResponse<Generador>>(`${API_BASE_URL}/api/generadores/activos?size=20`)
        ),
        tiposResiduo: this.safePage<TipoResiduo>(
          this.http.get<PageResponse<TipoResiduo>>(`${API_BASE_URL}/api/tipos-residuo?size=20`)
        ),
        certificadosPage: this.loadCertificadosPage(transportistaActual),
        transportistas: transportistaActual
          ? of([transportistaActual])
          : this.safe<Transportista[]>(this.http.get<Transportista[]>(`${API_BASE_URL}/api/transportistas`), []),
      })),
      switchMap(({ certificadosPage, ...data }) => {
        const transportista = data.transportistas[0] ?? null;

        return forkJoin({
          hojasPendientesCertificado: transportista
            ? this.safe<HojaRuta[]>(
                this.http.get<HojaRuta[]>(
                  `${API_BASE_URL}/api/hojas-ruta/pendientes-certificado/transportista/${transportista.idTransportista}`
                ),
                []
              )
            : of([]),
          ticketsPeriodoMensual: transportista && data.hojaActual
            ? this.safe<TicketControl[]>(
                this.http.get<TicketControl[]>(
                  `${API_BASE_URL}/api/ticket-control/por-periodo?anio=${this.anio(data.hojaActual.fechaInicio)}&mes=${this.mes(data.hojaActual.fechaInicio)}&idTransportista=${transportista.idTransportista}`
                ),
                []
              )
            : of([]),
        }).pipe(
          map((extras) => ({
            ...data,
            ...extras,
            certificados: certificadosPage.content ?? [],
            certificadosTotal: certificadosPage.totalElements ?? certificadosPage.content?.length ?? 0,
          }))
        );
      })
    );
  }

  private loadCertificadosPage(transportista: Transportista | null): Observable<PageResponse<Certificado>> {
    const url = transportista
      ? `${API_BASE_URL}/api/certificados/transportista/${transportista.idTransportista}?size=100&sort=anio,desc&sort=mes,desc`
      : `${API_BASE_URL}/api/certificados?size=100&sort=anio,desc&sort=mes,desc`;

    return this.safe<PageResponse<Certificado>>(
      this.http.get<PageResponse<Certificado>>(url),
      this.emptyPage<Certificado>()
    );
  }

  private emptyPage<T>(): PageResponse<T> {
    return {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 0,
      number: 0,
    };
  }

  private safe<T>(source: Observable<T>, fallback: T): Observable<T> {
    return source.pipe(catchError(() => of(fallback)));
  }

  private safePage<T>(source: Observable<PageResponse<T>>): Observable<T[]> {
    return source.pipe(
      map((page) => page.content ?? []),
      catchError(() => of([]))
    );
  }

  private anio(fecha: string): number {
    return Number(fecha.split('-')[0]);
  }

  private mes(fecha: string): number {
    return Number(fecha.split('-')[1]);
  }
}
