import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { TicketControl, TicketRequest } from '../models/operations.models';

@Injectable({ providedIn: 'root' })
export class TicketService {
  constructor(private readonly http: HttpClient) {}

  getById(id: number): Observable<TicketControl> {
    return this.http.get<TicketControl>(`${API_BASE_URL}/api/ticket-control/${id}`);
  }

  create(request: TicketRequest): Observable<TicketControl> {
    return this.http.post<TicketControl>(`${API_BASE_URL}/api/ticket-control`, request);
  }

  update(id: number, request: TicketRequest): Observable<TicketControl> {
    return this.http.put<TicketControl>(`${API_BASE_URL}/api/ticket-control/${id}`, request);
  }
}
