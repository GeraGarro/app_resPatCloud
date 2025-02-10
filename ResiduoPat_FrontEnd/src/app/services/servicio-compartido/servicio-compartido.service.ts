import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServicioCompartidoService {

  private idHojaSource = new BehaviorSubject<number | null>(null);
  idHoja$ = this.idHojaSource.asObservable();

  private reloadSource = new BehaviorSubject<void>(undefined);
  reload$ = this.reloadSource.asObservable();
  
  setIdHoja(idHoja: number) {
    this.idHojaSource.next(idHoja);
  }

  triggerReload() {
    this.reloadSource.next();
  }
}
