import { Directive, ElementRef, Renderer2, AfterViewInit, OnDestroy, HostListener } from '@angular/core';

@Directive({
  selector: '[appToggleVisibility]',
  standalone: true
})
export class ToggleVisibilityDirective implements AfterViewInit, OnDestroy {
  private isVisible: boolean = true;
  private static currentlyVisible: HTMLElement | null = null;
  private mutationObserver: MutationObserver | null = null;

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngAfterViewInit() {
    const parentElement = this.el.nativeElement.parentNode as HTMLElement;
    this.mutationObserver = new MutationObserver(() => this.checkAndToggleParentClass());
    this.mutationObserver.observe(parentElement, { attributes: true, subtree: true });


    if (window.innerWidth < 700) {
      const principalContainer = this.el.nativeElement.closest('.contenedor-principal') as HTMLElement;
      const childContainers = principalContainer.querySelectorAll('.contenedor');
      childContainers.forEach((child: Element) => {
        this.toggleVisibility(child as HTMLElement, false);
      });
    }
  }

  ngOnDestroy() {
    if (this.mutationObserver) {
      this.mutationObserver.disconnect();
    }
  }

  @HostListener('click')
  toggle() {
    const contenedor = this.el.nativeElement.closest('.contenedor') as HTMLElement;

    if (!contenedor) return;

    // Minimiza el contenedor actualmente visible si existe y no es el mismo que se está intentando desplegar
    if (ToggleVisibilityDirective.currentlyVisible && ToggleVisibilityDirective.currentlyVisible !== contenedor) {
      this.toggleVisibility(ToggleVisibilityDirective.currentlyVisible, false);
    }

    // Cambia la visibilidad del contenedor actual
    this.isVisible = !this.isVisible;
    this.toggleVisibility(contenedor, this.isVisible);

    // Actualiza el contenedor actualmente visible
    if (this.isVisible) {
      ToggleVisibilityDirective.currentlyVisible = contenedor;
    } else {
      ToggleVisibilityDirective.currentlyVisible = null;
    }

    this.checkAndToggleParentClass();
  }

  private toggleVisibility(contenedor: HTMLElement, show: boolean) {
    const components = contenedor.querySelectorAll('app-hoja-ruta-tickets, app-generador, app-tipo-residuo');
  
    components.forEach((component: Element) => {
      if (show) {
        this.renderer.removeClass(component, 'hidden');
        this.renderer.removeClass(this.el.nativeElement, 'rotated');
      } else {
        this.renderer.addClass(component, 'hidden');
        this.renderer.addClass(this.el.nativeElement, 'rotated');
      }
    });

    if (show) {
      this.renderer.removeClass(contenedor, 'hidden');
    } else {
      this.renderer.addClass(contenedor, 'hidden');
    }
  }

  private checkAndToggleParentClass() {
    const principalContainer = this.el.nativeElement.closest('.contenedor-principal') as HTMLElement;
    if (!principalContainer) return;

    const childContainers = principalContainer.querySelectorAll('.contenedor');
    let anyHidden = false;

    childContainers.forEach((child: Element) => {
      const hiddenComponents = child.querySelectorAll('app-hoja-ruta-tickets.hidden, app-generador.hidden, app-tipo-residuo.hidden');
      if (hiddenComponents.length > 0) {
        anyHidden = true;
      }
    });

    if (anyHidden) {
      this.renderer.addClass(principalContainer, 'contenedor-principal-flex');
      this.renderer.removeClass(principalContainer, 'contenedor-principal');
    } else {
      this.renderer.removeClass(principalContainer, 'contenedor-principal-flex');
      this.renderer.addClass(principalContainer, 'contenedor-principal');
    }
  }
}