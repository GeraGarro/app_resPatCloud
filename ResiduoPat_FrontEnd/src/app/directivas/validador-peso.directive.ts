import { Directive, ElementRef, HostListener, Optional } from '@angular/core';
import { NgModel } from '@angular/forms';

@Directive({
  selector: '[appValidadorPeso]',
  standalone:true
})
export class ValidadorPesoDirective {

  constructor(private el: ElementRef, @Optional() private ngModel: NgModel) { }

  @HostListener('input', ['$event']) onInput(event: any): void {
    let valor = event.target.value;

    // Reemplazar coma por punto para normalizar el separador decimal
    valor = valor.replace(',', '.');

    // Permitir solo números y un único punto decimal
    const partes = valor.split('.');
    if (partes.length > 2) {
      // Si hay más de un punto decimal, conservar la primera parte y el primer decimal
      valor = partes[0] + '.' + partes.slice(1).join('');
    }

    // Actualizar el valor en el input para reflejar los cambios
    event.target.value = valor;

    // Si hay un modelo enlazado, actualizarlo como cadena (para permitir edición intermedia)
    if (this.ngModel) {
      this.ngModel.control.setValue(valor, { emitEvent: false });
    }
  }

  @HostListener('blur', ['$event']) onBlur(event: any): void {
    let valor = event.target.value;

    // Normalizar el separador decimal y eliminar caracteres no válidos
    valor = valor.replace(',', '.');
    valor = valor.replace(/[^0-9.]/g, '');

    // Convertir a número y validar al salir del campo
    let valorNumerico = parseFloat(valor);

    // Si el valor es inválido o negativo, establecer 0
    if (isNaN(valorNumerico) || valorNumerico < 0) {
      valorNumerico = 0;
    }

    // Actualizar el valor final en el input y el modelo
    event.target.value = valorNumerico.toString();
    if (this.ngModel) {
      this.ngModel.control.setValue(valorNumerico, { emitEvent: true });
    }
  }
}