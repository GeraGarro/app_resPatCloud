import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResiduoComponent } from './residuo.component';

describe('ResiduoComponent', () => {
  let component: ResiduoComponent;
  let fixture: ComponentFixture<ResiduoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResiduoComponent]
    });
    fixture = TestBed.createComponent(ResiduoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
