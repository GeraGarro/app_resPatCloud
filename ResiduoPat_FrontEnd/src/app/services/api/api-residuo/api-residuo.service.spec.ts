import { TestBed } from '@angular/core/testing';

import { ApiResiduoService } from './api-residuo.service';

describe('ApiResiduoService', () => {
  let service: ApiResiduoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiResiduoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
