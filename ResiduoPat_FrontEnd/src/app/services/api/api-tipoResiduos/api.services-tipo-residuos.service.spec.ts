import { TestBed } from '@angular/core/testing';

import { ApiServicesTipoResiduosService } from './api.services-tipo-residuos.service';

describe('ApiServicesTipoResiduosService', () => {
  let service: ApiServicesTipoResiduosService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiServicesTipoResiduosService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
