import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HojaRutaTicketsComponent } from './hoja-ruta-tickets.component';

describe('HojaRutaTicketsComponent', () => {
  let component: HojaRutaTicketsComponent;
  let fixture: ComponentFixture<HojaRutaTicketsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HojaRutaTicketsComponent]
    });
    fixture = TestBed.createComponent(HojaRutaTicketsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
