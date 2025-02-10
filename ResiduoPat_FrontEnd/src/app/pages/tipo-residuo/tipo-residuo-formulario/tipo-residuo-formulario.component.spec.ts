import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TipoResiduoFormularioComponent } from './tipo-residuo-formulario.component';

describe('TipoResiduoFormularioComponent', () => {
  let component: TipoResiduoFormularioComponent;
  let fixture: ComponentFixture<TipoResiduoFormularioComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TipoResiduoFormularioComponent]
    });
    fixture = TestBed.createComponent(TipoResiduoFormularioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
