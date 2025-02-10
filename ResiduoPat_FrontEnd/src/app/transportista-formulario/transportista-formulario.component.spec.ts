import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransportistaFormularioComponent } from './transportista-formulario.component';

describe('TransportistaFormularioComponent', () => {
  let component: TransportistaFormularioComponent;
  let fixture: ComponentFixture<TransportistaFormularioComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TransportistaFormularioComponent]
    });
    fixture = TestBed.createComponent(TransportistaFormularioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
