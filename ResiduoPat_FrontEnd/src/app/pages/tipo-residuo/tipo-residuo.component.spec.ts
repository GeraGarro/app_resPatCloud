import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TipoResiduoComponent } from './tipo-residuo.component';

describe('TipoResiduoComponent', () => {
  let component: TipoResiduoComponent;
  let fixture: ComponentFixture<TipoResiduoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TipoResiduoComponent]
    });
    fixture = TestBed.createComponent(TipoResiduoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
