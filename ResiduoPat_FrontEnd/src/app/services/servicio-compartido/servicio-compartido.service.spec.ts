import { TestBed } from '@angular/core/testing';

import { ServicioCompartidoService } from './servicio-compartido.service';

describe('ServicioCompartidoService', () => {
  let service: ServicioCompartidoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServicioCompartidoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
