import { TestBed } from '@angular/core/testing';

import { ApiCertificadoService } from './api-certificado.service';

describe('ApiCertificadoService', () => {
  let service: ApiCertificadoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiCertificadoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
