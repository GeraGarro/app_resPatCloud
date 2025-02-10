import { Component, Input } from '@angular/core';
import { ListaResiduo } from '../services/models/ticket.model';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-modal-info',
  standalone: true,
  templateUrl: './modal-info.component.html',
  styleUrls: ['./modal-info.component.css'],
  imports:[CommonModule,RouterLink]
})
export class ModalInfoComponent {
  @Input() nuevosResiduos: ListaResiduo[] = [];
  @Input() residuosModificados: ListaResiduo[] = [];
  @Input() residuosEliminados: ListaResiduo[] = [];
  
  isVisible = false;

  mostrarModal() {
    this.isVisible = true;
  }

  cerrarModal() {
    this.isVisible = false;
  }
}
