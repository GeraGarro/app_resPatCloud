import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Transportista, Vehiculo, VehiculoRequest } from '../../core/models/operations.models';
import { TransportistaContextService } from '../../core/services/transportista-context.service';
import { VehiculoService } from '../../core/services/vehiculo.service';

@Component({
  selector: 'app-transportista-vehiculos',
  templateUrl: './transportista-vehiculos.component.html',
  styleUrl: './transportista-vehiculos.component.scss'
})
export class TransportistaVehiculosComponent implements OnInit {
  form!: FormGroup;
  transportista: Transportista | null = null;
  vehiculos: Vehiculo[] = [];
  selectedVehiculo: Vehiculo | null = null;
  isLoading = true;
  isSaving = false;
  message = '';
  isEditingVehiculo = true;
  isFormModalOpen = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly contextService: TransportistaContextService,
    private readonly vehiculoService: VehiculoService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      marca: ['', Validators.required],
      modelo: ['', Validators.required],
      dominio: ['', Validators.required],
      chasis: [''],
      anio: [null],
      activo: [true],
    });

    this.contextService.getCurrentTransportista().subscribe((transportista) => {
      this.transportista = transportista;

      if (!transportista) {
        this.isLoading = false;
        this.message = 'No se encontro un transportista asociado a la sesion.';
        return;
      }

      this.loadVehiculos();
    });
  }

  loadVehiculos(): void {
    if (!this.transportista) {
      return;
    }

    this.isLoading = true;
    this.vehiculoService.listByTransportista(this.transportista.idTransportista).subscribe({
      next: (vehiculos) => {
        this.vehiculos = vehiculos;
        this.isLoading = false;
      },
      error: () => {
        this.vehiculos = [];
        this.isLoading = false;
        this.message = 'No se pudieron cargar los vehiculos del transportista.';
      },
    });
  }

  submit(): void {
    this.message = '';

    if (!this.transportista) {
      this.message = 'Primero debe existir un transportista asociado.';
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.message = 'Completa los campos obligatorios del vehiculo.';
      return;
    }

    this.isSaving = true;
    const request = this.buildRequest();
    const action = this.selectedVehiculo
      ? this.vehiculoService.update(this.selectedVehiculo.idVehiculo, request)
      : this.vehiculoService.create(this.transportista.idTransportista, request);

    action.subscribe({
      next: () => {
        this.isSaving = false;
        this.message = this.selectedVehiculo
          ? 'Vehiculo actualizado correctamente.'
          : 'Vehiculo registrado correctamente.';
        this.newVehicle();
        this.closeFormModal();
        this.loadVehiculos();
      },
      error: () => {
        this.isSaving = false;
        this.message = this.selectedVehiculo
          ? 'No se pudo actualizar el vehiculo.'
          : 'No se pudo registrar el vehiculo.';
      },
    });
  }

  newVehicle(): void {
    this.selectedVehiculo = null;
    this.isEditingVehiculo = true;
    this.form.reset({ activo: true });
  }

  openNewVehicle(): void {
    this.newVehicle();
    this.message = '';
    this.openFormModal();
  }

  showExistingVehicles(): void {
    this.closeFormModal();
  }

  editVehicle(vehiculo: Vehiculo): void {
    this.selectedVehiculo = vehiculo;
    this.isEditingVehiculo = false;
    this.form.patchValue({
      marca: vehiculo.marca,
      modelo: vehiculo.modelo,
      dominio: vehiculo.dominio,
      chasis: vehiculo.chasis,
      anio: vehiculo.anio,
      activo: vehiculo.activo,
    });
    this.message = 'Vehiculo seleccionado en modo consulta.';
    this.openFormModal();
  }

  enableVehicleEdit(): void {
    if (!this.selectedVehiculo) {
      return;
    }

    this.isEditingVehiculo = true;
    this.message = 'Edicion habilitada para el vehiculo seleccionado.';
    this.openFormModal();
  }

  get isReadOnlySelected(): boolean {
    return Boolean(this.selectedVehiculo && !this.isEditingVehiculo);
  }

  get submitButtonLabel(): string {
    if (this.isSaving) {
      return 'Guardando...';
    }

    return this.selectedVehiculo ? 'Guardar cambios del vehiculo' : 'Guardar nuevo vehiculo';
  }

  openFormModal(): void {
    this.isFormModalOpen = true;
  }

  closeFormModal(): void {
    this.isFormModalOpen = false;
  }

  toggleEstado(vehiculo: Vehiculo): void {
    this.vehiculoService.toggleEstado(vehiculo.idVehiculo).subscribe({
      next: () => this.loadVehiculos(),
      error: () => {
        this.message = 'No se pudo cambiar el estado del vehiculo.';
      },
    });
  }

  private buildRequest(): VehiculoRequest {
    const value = this.form.value;

    return {
      marca: value.marca,
      modelo: value.modelo,
      dominio: String(value.dominio).toUpperCase(),
      chasis: value.chasis,
      anio: value.anio,
      activo: Boolean(value.activo),
      idTransportista: this.transportista?.idTransportista,
    };
  }
}
