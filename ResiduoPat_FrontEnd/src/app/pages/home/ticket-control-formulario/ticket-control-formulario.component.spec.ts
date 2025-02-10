import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketControlFormularioComponent } from './ticket-control-formulario.component';

describe('TicketControlFormularioComponent', () => {
  let component: TicketControlFormularioComponent;
  let fixture: ComponentFixture<TicketControlFormularioComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TicketControlFormularioComponent]
    });
    fixture = TestBed.createComponent(TicketControlFormularioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
