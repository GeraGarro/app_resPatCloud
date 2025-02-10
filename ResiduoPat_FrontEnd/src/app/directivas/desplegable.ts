import { Directive, ElementRef, Renderer2, AfterViewInit, Input, Output, EventEmitter } from '@angular/core';

interface Opcion {
  id: number;
  name: string;
}

interface OpcionSeleccionada {
  id: number;
  name: string;
}

@Directive({
  selector: '[desplegableCustom]',
  standalone: true
})
export class DesplegableCustomDirective implements AfterViewInit {
  @Input() opciones: Opcion[] = []; // Recibe las opciones del componente padre
  @Output() opcionSeleccionada = new EventEmitter<OpcionSeleccionada>(); // Emite un evento con la opción seleccionada
  @Output() menuCerrado = new EventEmitter<void>(); // Emite un evento cuando el menú se cierra

  private isOpen = false; // Estado para saber si el menú está abierto
  private opcionSeleccionadaId: number | null = null; // ID de la opción seleccionada

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngAfterViewInit(): void {
    this.setupOptionsListeners();
    this.setupDocumentClickListener();
  }

  private setupOptionsListeners(): void {
    const selectBox = this.el.nativeElement.querySelector('.select-box');
    const selectOption = this.el.nativeElement.querySelector('.select-option');
    const opciones = this.el.nativeElement.querySelectorAll('.opciones li');

    if (selectBox) {
      this.renderer.listen(selectOption, 'click', (event: Event) => {
        event.stopPropagation();
        this.isOpen = !this.isOpen;
        this.toggleMenuVisibility();
      });
    }

    opciones.forEach((opcion: HTMLElement) => {
      this.renderer.listen(opcion, 'click', (event: Event) => {
        event.stopPropagation();
        const id = parseInt(opcion.getAttribute('data-id') || '0', 10);
        const name = opcion.getAttribute('data-name') || '';
        this.opcionSeleccionadaId = id;
        this.opcionSeleccionada.emit({ id, name });
        this.isOpen = false; // Cierra el menú al seleccionar una opción
        this.toggleMenuVisibility();
        this.menuCerrado.emit(); // Emite evento de menú cerrado
      });
    });
  }

  private setupDocumentClickListener(): void {
    this.renderer.listen('document', 'click', (event: Event) => {
      if (!this.el.nativeElement.contains(event.target)) {
        this.isOpen = false;
        this.toggleMenuVisibility();
      }
    });
  }

  private toggleMenuVisibility(): void {
    const selectBox = this.el.nativeElement.querySelector('.select-box');
    if (selectBox) {
      if (this.isOpen) {
        this.renderer.addClass(selectBox, 'active');
      } else {
        this.renderer.removeClass(selectBox, 'active');
      }
    }
  }

  get selectedOption(): Opcion | null {
    return this.opciones.find(opcion => opcion.id === this.opcionSeleccionadaId) ?? null;
  }
}