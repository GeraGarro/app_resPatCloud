import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResiduoModalComponent } from './residuo-modal.component';

describe('ResiduoModalComponent', () => {
  let component: ResiduoModalComponent;
  let fixture: ComponentFixture<ResiduoModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResiduoModalComponent]
    });
    fixture = TestBed.createComponent(ResiduoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
