import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TipoResiduo, TipoResiduoRequest, TransportistaProfileStatus } from '../../core/models/operations.models';
import { TipoResiduoService } from '../../core/services/tipo-residuo.service';
import { TransportistaService } from '../../core/services/transportista.service';

@Component({
  selector: 'app-transportista-tipos-residuo',
  templateUrl: './transportista-tipos-residuo.component.html',
  styleUrl: './transportista-tipos-residuo.component.scss'
})
export class TransportistaTiposResiduoComponent implements OnInit {
  form!: FormGroup;
  tiposResiduo: TipoResiduo[] = [];
  profileStatus: TransportistaProfileStatus | null = null;
  selectedTipo: TipoResiduo | null = null;
  isLoading = true;
  isLoadingProfile = true;
  isSaving = false;
  message = '';
  isEditingTipo = true;
  isFormModalOpen = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly tipoResiduoService: TipoResiduoService,
    private readonly transportistaService: TransportistaService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      codigo: ['', Validators.required],
      nombre: ['', Validators.required],
      estadoActividad: [true],
    });

    this.load();
    this.loadTransportista();
  }

  get activosCount(): number {
    return this.tiposResiduo.filter((tipo) => tipo.estadoActividad).length;
  }

  load(): void {
    this.isLoading = true;
    this.tipoResiduoService.listAll().subscribe({
      next: (tipos) => {
        this.tiposResiduo = tipos;
        this.isLoading = false;
      },
      error: () => {
        this.tiposResiduo = [];
        this.isLoading = false;
        this.message = 'No se pudieron cargar los tipos de residuo.';
      },
    });
  }

  loadTransportista(): void {
    this.isLoadingProfile = true;
    this.transportistaService.getProfileStatus().subscribe({
      next: (status) => {
        this.profileStatus = status;
        this.isLoadingProfile = false;
      },
      error: () => {
        this.profileStatus = null;
        this.isLoadingProfile = false;
        this.message = 'No se pudo verificar el perfil operativo del transportista.';
      },
    });
  }

  submit(): void {
    this.message = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.message = 'Completa codigo y nombre del tipo de residuo.';
      return;
    }

    if (!this.profileStatus?.completo) {
      this.message = 'Primero completa tu perfil transportista y registra al menos un vehiculo.';
      return;
    }

    this.isSaving = true;
    const request = this.buildRequest();
    const action = this.selectedTipo
      ? this.tipoResiduoService.update(this.selectedTipo.id, request)
      : this.tipoResiduoService.create(request);

    action.subscribe({
      next: () => {
        this.isSaving = false;
        this.message = this.selectedTipo
          ? 'Tipo de residuo actualizado correctamente.'
          : 'Tipo de residuo registrado correctamente.';
        this.resetForm();
        this.closeFormModal();
        this.load();
      },
      error: (error) => {
        this.isSaving = false;
        this.message = this.getErrorMessage(error);
      },
    });
  }

  editTipo(tipo: TipoResiduo): void {
    this.selectedTipo = tipo;
    this.isEditingTipo = false;
    this.form.patchValue({
      codigo: tipo.codigo,
      nombre: tipo.nombre,
      estadoActividad: tipo.estadoActividad,
    });
    this.message = 'Tipo de residuo seleccionado en modo consulta.';
    this.openFormModal();
  }

  enableTipoEdit(): void {
    if (!this.selectedTipo) {
      return;
    }

    this.isEditingTipo = true;
    this.message = 'Edicion habilitada para el tipo de residuo seleccionado.';
    this.openFormModal();
  }

  cancelEdit(): void {
    this.resetForm();
    this.message = '';
  }

  openNewTipo(): void {
    this.resetForm();
    this.message = '';
    this.openFormModal();
  }

  showExistingTipos(): void {
    this.closeFormModal();
  }

  get isReadOnlySelected(): boolean {
    return Boolean(this.selectedTipo && !this.isEditingTipo);
  }

  get submitButtonLabel(): string {
    if (this.isSaving) {
      return 'Guardando...';
    }

    return this.selectedTipo ? 'Guardar cambios del tipo' : 'Guardar nuevo tipo';
  }

  openFormModal(): void {
    this.isFormModalOpen = true;
  }

  closeFormModal(): void {
    this.isFormModalOpen = false;
  }

  toggleEstado(tipo: TipoResiduo): void {
    if (!this.profileStatus?.completo) {
      this.message = 'Primero completa tu perfil transportista y registra al menos un vehiculo.';
      return;
    }

    this.tipoResiduoService.toggleEstado(tipo.id).subscribe({
      next: () => {
        this.message = 'Estado del tipo de residuo actualizado.';
        this.load();
      },
      error: () => {
        this.message = 'No se pudo cambiar el estado del tipo de residuo.';
      },
    });
  }

  private buildRequest(): TipoResiduoRequest {
    const value = this.form.value;

    return {
      codigo: (this.normalizeText(value.codigo) ?? '').toUpperCase(),
      nombre: this.normalizeText(value.nombre) ?? '',
      estadoActividad: Boolean(value.estadoActividad),
    };
  }

  private resetForm(): void {
    this.selectedTipo = null;
    this.isEditingTipo = true;
    this.form.reset({ estadoActividad: true });
  }

  private normalizeText(value: unknown): string | undefined {
    if (typeof value !== 'string') {
      return undefined;
    }

    const normalized = value.trim();
    return normalized || undefined;
  }

  private getErrorMessage(error: unknown): string {
    return (error as { error?: { message?: string } })?.error?.message
      ?? 'No se pudo guardar el tipo de residuo.';
  }
}
