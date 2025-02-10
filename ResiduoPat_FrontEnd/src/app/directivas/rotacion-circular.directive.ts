import { Directive, ElementRef, AfterViewInit, Input, OnChanges, SimpleChanges, OnDestroy, Renderer2 } from '@angular/core';

@Directive({
  selector: '[appRotacionCircular]',
  standalone: true
})
export class RotacionCircularDirective implements AfterViewInit, OnChanges, OnDestroy {
    @Input('appRotacionCircular') elementos: any[] = [];
  private animationId: number | null = null;
  private velocidad = 1; // Velocidad del carrusel
  private posicionActual = 0;
  private itemWidth!: number;

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.configurarCarrusel();
      this.iniciarDesplazamientoAutomatico();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['elementos']) {
      this.configurarCarrusel();
    }
  }

  ngOnDestroy(): void {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
    }
  }

  configurarCarrusel(): void {
    const container = this.el.nativeElement;
    const items: HTMLElement[] = Array.from(container.children) as HTMLElement[];

    if (items.length === 0) return;

    this.itemWidth = items[0].clientWidth; // Ajustar según el primer elemento
    container.style.display = 'flex'; // Asegúrate de que el contenedor es flex
    container.style.transition = 'transform 0.5s ease-in-out';

    // Ajustar la posición inicial
    this.posicionActual = 0;
    container.style.transform = `translateX(${this.posicionActual}px)`;
  }

  iniciarDesplazamientoAutomatico(): void {
    const container = this.el.nativeElement;
  
    // Duplicar los elementos para el efecto de carrusel
    const elementosOriginales = Array.from(container.children) as HTMLElement[];
    elementosOriginales.forEach(item => {
      const clone = item.cloneNode(true) as HTMLElement; // Clonamos cada elemento
      container.appendChild(clone); // Añadimos el clon al contenedor
    });
  
    const totalItems = container.children.length; // Total de elementos ahora (originales + duplicados)
  
    const desplazar = () => {
      this.posicionActual -= this.velocidad;
  
      // Aplicar transformación
      container.style.transform = `translateX(${this.posicionActual}px)`;
  
      if (this.posicionActual <= -this.itemWidth * totalItems / 2) {
        // Reiniciar la posición para el efecto infinito
        this.posicionActual = 0; // Reiniciar a la posición inicial
      }
  
      this.animationId = requestAnimationFrame(desplazar);
    };
  
    this.animationId = requestAnimationFrame(desplazar);
  }
  scrollLeft(): void {
    this.posicionActual += this.itemWidth;
    this.el.nativeElement.style.transform = `translateX(${this.posicionActual}px)`;
  }

  scrollRight(): void {
    this.posicionActual -= this.itemWidth;
    this.el.nativeElement.style.transform = `translateX(${this.posicionActual}px)`;
  }
}