import { TestBed } from '@angular/core/testing';

import { ApiHojaService } from './api-hoja.service';

describe('ApiHojaService', () => {
  let service: ApiHojaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiHojaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
