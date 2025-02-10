import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResiduoFormularioComponent } from './residuo-formulario.component';

describe('ResiduoFormularioComponent', () => {
  let component: ResiduoFormularioComponent;
  let fixture: ComponentFixture<ResiduoFormularioComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResiduoFormularioComponent]
    });
    fixture = TestBed.createComponent(ResiduoFormularioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
