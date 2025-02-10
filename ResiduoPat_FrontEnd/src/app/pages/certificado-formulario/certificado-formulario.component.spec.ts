import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificadoFormularioComponent } from './certificado-formulario.component';

describe('CertificadoFormularioComponent', () => {
  let component: CertificadoFormularioComponent;
  let fixture: ComponentFixture<CertificadoFormularioComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CertificadoFormularioComponent]
    });
    fixture = TestBed.createComponent(CertificadoFormularioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
