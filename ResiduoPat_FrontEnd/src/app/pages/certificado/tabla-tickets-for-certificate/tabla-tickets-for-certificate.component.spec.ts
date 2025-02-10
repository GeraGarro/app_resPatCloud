import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TablaTicketsForCertificateComponent } from './tabla-tickets-for-certificate.component';

describe('TablaTicketsForCertificateComponent', () => {
  let component: TablaTicketsForCertificateComponent;
  let fixture: ComponentFixture<TablaTicketsForCertificateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TablaTicketsForCertificateComponent]
    });
    fixture = TestBed.createComponent(TablaTicketsForCertificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
