import {
  Directive,
  ElementRef,
  HostBinding,
  HostListener,
  Inject,
  OnDestroy,
  PLATFORM_ID,
  Renderer2,
} from '@angular/core';
import { DOCUMENT, isPlatformBrowser } from '@angular/common';

@Directive({
  selector: 'select'
})
export class MobileSelectModalDirective implements OnDestroy {
  @HostBinding('class.mobile-select-source') readonly mobileSelectSource = true;

  private overlay: HTMLElement | null = null;
  private removeKeyListener: (() => void) | null = null;

  constructor(
    private readonly elementRef: ElementRef<HTMLSelectElement>,
    private readonly renderer: Renderer2,
    @Inject(DOCUMENT) private readonly document: Document,
    @Inject(PLATFORM_ID) private readonly platformId: object
  ) {}

  @HostListener('pointerdown', ['$event'])
  onPointerDown(event: PointerEvent): void {
    if (!this.shouldUseModal()) {
      return;
    }

    event.preventDefault();
    event.stopPropagation();
    this.openModal();
  }

  ngOnDestroy(): void {
    this.closeModal();
  }

  private shouldUseModal(): boolean {
    if (!isPlatformBrowser(this.platformId)) {
      return false;
    }

    const select = this.elementRef.nativeElement;
    return window.matchMedia('(max-width: 720px)').matches
      && !select.multiple
      && !select.matches(':disabled')
      && select.options.length > 0;
  }

  private openModal(): void {
    if (this.overlay) {
      return;
    }

    const select = this.elementRef.nativeElement;
    const overlay = this.renderer.createElement('div') as HTMLElement;
    const sheet = this.renderer.createElement('section') as HTMLElement;
    const header = this.renderer.createElement('header') as HTMLElement;
    const titleGroup = this.renderer.createElement('div') as HTMLElement;
    const eyebrow = this.renderer.createElement('p') as HTMLElement;
    const title = this.renderer.createElement('h3') as HTMLElement;
    const closeButton = this.renderer.createElement('button') as HTMLButtonElement;
    const options = this.renderer.createElement('div') as HTMLElement;
    const hasScrollableOptions = select.options.length > 10;

    this.renderer.addClass(overlay, 'mobile-select-backdrop');
    this.renderer.addClass(sheet, 'mobile-select-sheet');
    this.renderer.addClass(header, 'mobile-select-header');
    this.renderer.addClass(eyebrow, 'mobile-select-eyebrow');
    this.renderer.addClass(options, 'mobile-select-options');

    if (hasScrollableOptions) {
      this.renderer.addClass(sheet, 'has-scroll');
    }

    this.renderer.setAttribute(sheet, 'role', 'dialog');
    this.renderer.setAttribute(sheet, 'aria-modal', 'true');
    this.renderer.setAttribute(closeButton, 'type', 'button');

    eyebrow.textContent = 'Seleccionar opcion';
    title.textContent = this.getLabel();
    closeButton.textContent = 'Cerrar';

    this.renderer.appendChild(titleGroup, eyebrow);
    this.renderer.appendChild(titleGroup, title);
    this.renderer.appendChild(header, titleGroup);
    this.renderer.appendChild(header, closeButton);
    this.renderer.appendChild(sheet, header);

    Array.from(select.options).forEach((option, index) => {
      const optionButton = this.renderer.createElement('button') as HTMLButtonElement;
      this.renderer.setAttribute(optionButton, 'type', 'button');
      this.renderer.addClass(optionButton, 'mobile-select-option');
      optionButton.textContent = option.text.trim() || option.label || option.value;

      if (index === select.selectedIndex) {
        this.renderer.addClass(optionButton, 'selected');
        this.renderer.setAttribute(optionButton, 'aria-current', 'true');
      }

      if (option.disabled) {
        optionButton.disabled = true;
      }

      optionButton.addEventListener('click', () => this.chooseOption(index));
      this.renderer.appendChild(options, optionButton);
    });

    this.renderer.appendChild(sheet, options);

    if (hasScrollableOptions) {
      const hint = this.renderer.createElement('div') as HTMLElement;
      this.renderer.addClass(hint, 'mobile-select-hint');
      hint.textContent = 'Desliza para ver mas opciones';
      this.renderer.appendChild(sheet, hint);
    }

    this.renderer.appendChild(overlay, sheet);
    this.renderer.appendChild(this.document.body, overlay);
    this.document.body.classList.add('mobile-select-open');

    overlay.addEventListener('click', (event) => {
      if (event.target === overlay) {
        this.closeModal();
      }
    });
    closeButton.addEventListener('click', () => this.closeModal());
    this.removeKeyListener = this.renderer.listen(this.document, 'keydown', (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        this.closeModal();
      }
    });

    this.overlay = overlay;
    setTimeout(() => {
      options.querySelector('.mobile-select-option.selected')?.scrollIntoView({
        block: 'center',
      });
    });
  }

  private chooseOption(index: number): void {
    const select = this.elementRef.nativeElement;
    const option = select.options.item(index);

    if (!option || option.disabled) {
      return;
    }

    select.selectedIndex = index;
    select.dispatchEvent(new Event('input', { bubbles: true }));
    select.dispatchEvent(new Event('change', { bubbles: true }));
    this.closeModal();
    select.focus({ preventScroll: true });
  }

  private closeModal(): void {
    if (this.overlay) {
      this.overlay.remove();
      this.overlay = null;
    }

    this.document.body.classList.remove('mobile-select-open');
    this.removeKeyListener?.();
    this.removeKeyListener = null;
  }

  private getLabel(): string {
    const select = this.elementRef.nativeElement;
    const labelledBy = select.getAttribute('aria-labelledby');

    if (labelledBy) {
      const label = this.document.getElementById(labelledBy)?.textContent?.trim();
      if (label) {
        return label;
      }
    }

    return select.closest('label')?.querySelector('span')?.textContent?.trim()
      || select.getAttribute('aria-label')
      || 'Opciones disponibles';
  }
}
