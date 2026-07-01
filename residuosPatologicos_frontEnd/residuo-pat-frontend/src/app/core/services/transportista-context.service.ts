import { Injectable } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { Transportista } from '../models/operations.models';
import { TransportistaService } from './transportista.service';

@Injectable({ providedIn: 'root' })
export class TransportistaContextService {
  constructor(
    private readonly transportistaService: TransportistaService
  ) {}

  getCurrentTransportista(): Observable<Transportista | null> {
    return this.transportistaService.getCurrent().pipe(catchError(() => of(null)));
  }
}
