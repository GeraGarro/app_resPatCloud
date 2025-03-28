import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';


@Component({
  selector: 'app-confirmacion-dialogo',
 
  
  template: `
  
    <h2 mat-dialog-title>Confirmación</h2>
    <div mat-dialog-content>¿Estás seguro de que deseas eliminar este registro?</div>
    
    <div mat-dialog-actions>
      <button mat-button (click)="cancelar()">Cancelar</button>
      <button mat-raised-button color="warn" (click)="confirmar()">Eliminar</button>
    </div>

  `,
  styles: [
  
  ], 

})
export class ConfirmacionDialogoComponent {

  constructor(private dialogRef: MatDialogRef<ConfirmacionDialogoComponent>) {}


  confirmar() {
    this.dialogRef.close(true);
  }

  cancelar() {
    this.dialogRef.close(false);
  }
}
