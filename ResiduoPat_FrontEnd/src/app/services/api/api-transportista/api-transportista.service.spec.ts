import { TestBed } from '@angular/core/testing';

import { ApiTransportistaService } from './api-transportista.service';

describe('ApiTransportistaService', () => {
  let service: ApiTransportistaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiTransportistaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
