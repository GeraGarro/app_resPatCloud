import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Generador, GeneradorRequest, Telefono, TipoTelefono, TransportistaProfileStatus } from '../../core/models/operations.models';
import { GeneradorService } from '../../core/services/generador.service';
import { TransportistaService } from '../../core/services/transportista.service';

@Component({
  selector: 'app-transportista-generadores',
  templateUrl: './transportista-generadores.component.html',
  styleUrl: './transportista-generadores.component.scss'
})
export class TransportistaGeneradoresComponent implements OnInit {
  form!: FormGroup;
  generadores: Generador[] = [];
  profileStatus: TransportistaProfileStatus | null = null;
  selectedGenerador: Generador | null = null;
  isEditingGenerador = true;
  isLoading = true;
  isLoadingProfile = true;
  isSaving = false;
  message = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly generadorService: GeneradorService,
    private readonly transportistaService: TransportistaService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      tipo: ['EMPRESA', Validators.required],
      razonSocial: [''],
      nombreFantasia: [''],
      cuit: [''],
      nombre: [''],
      apellido: [''],
      cuil: [''],
      legajo: [''],
      email: ['', [Validators.email]],
      telefonos: this.fb.array([]),
      domicilioTipo: ['CALLE', Validators.required],
      barrio: [''],
      calle: [''],
      altura: [null],
      localidad: ['', Validators.required],
      provincia: ['', Validators.required],
      codigoPostal: [null],
    });

    this.configureTipoValidators(this.tipo);
    this.form.get('tipo')?.valueChanges.subscribe((tipo) => {
      this.configureTipoValidators(tipo);
    });
    this.form.get('domicilioTipo')?.valueChanges.subscribe((tipoDomicilio) => {
      this.configureDomicilioValidators(tipoDomicilio);
    });
    this.configureDomicilioValidators(this.form.get('domicilioTipo')?.value);
    this.addTelefono();

    this.load();
    this.loadTransportista();
  }

  get tipo(): 'EMPRESA' | 'AUTONOMO' {
    return this.form.get('tipo')?.value as 'EMPRESA' | 'AUTONOMO';
  }

  get telefonos(): FormArray {
    return this.form.get('telefonos') as FormArray;
  }

  get isReadOnlySelected(): boolean {
    return Boolean(this.selectedGenerador && !this.isEditingGenerador);
  }

  get domicilioTipo(): 'BARRIO' | 'CALLE' {
    return this.form.get('domicilioTipo')?.value as 'BARRIO' | 'CALLE';
  }

  get canAddTelefono(): boolean {
    const ultimoTelefono = this.telefonos.at(this.telefonos.length - 1);

    if (!ultimoTelefono) {
      return true;
    }

    return Boolean(this.normalizeText(ultimoTelefono.get('numero')?.value));
  }

  load(): void {
    this.isLoading = true;
    this.generadorService.listActivos().subscribe({
      next: (generadores) => {
        this.generadores = generadores;
        this.isLoading = false;
      },
      error: () => {
        this.generadores = [];
        this.isLoading = false;
        this.message = 'No se pudieron cargar los generadores.';
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

    if (this.isReadOnlySelected) {
      this.message = 'Habilita la edicion antes de modificar este generador.';
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.message = 'Completa los campos obligatorios del generador.';
      return;
    }

    if (!this.profileStatus?.completo) {
      this.message = 'Primero completa tu perfil transportista y registra al menos un vehiculo.';
      return;
    }

    this.isSaving = true;
    const request = this.buildRequest();
    const action = this.selectedGenerador
      ? this.generadorService.update(this.selectedGenerador.id, request)
      : this.generadorService.create(request);

    action.subscribe({
      next: () => {
        this.isSaving = false;
        this.message = this.selectedGenerador
          ? 'Generador actualizado correctamente.'
          : 'Generador registrado correctamente.';
        this.resetForm();
        this.load();
      },
      error: (error) => {
        this.isSaving = false;
        this.message = this.getErrorMessage(error);
      },
    });
  }

  selectGenerador(generador: Generador): void {
    this.selectedGenerador = generador;
    this.isEditingGenerador = false;
    const domicilioTipo = generador.domicilio?.barrio ? 'BARRIO' : 'CALLE';
    this.form.patchValue({
      tipo: generador.tipo,
      razonSocial: generador.razonSocial ?? '',
      nombreFantasia: generador.nombreFantasia ?? '',
      cuit: generador.cuit ?? '',
      nombre: generador.nombre ?? '',
      apellido: generador.apellido ?? '',
      cuil: generador.cuil ?? '',
      legajo: generador.legajo ?? '',
      email: generador.email ?? '',
      domicilioTipo,
      barrio: generador.domicilio?.barrio ?? '',
      calle: generador.domicilio?.calle ?? '',
      altura: generador.domicilio?.altura ?? null,
      localidad: generador.domicilio?.localidad ?? '',
      provincia: generador.domicilio?.provincia ?? '',
      codigoPostal: generador.domicilio?.codigoPostal ?? null,
    });

    this.telefonos.clear();
    const telefonos = generador.telefonos?.length ? generador.telefonos : [];
    telefonos.forEach((telefono) => this.addTelefono(telefono));

    if (!this.telefonos.length) {
      this.addTelefono();
    }

    this.configureTipoValidators(generador.tipo);
    this.configureDomicilioValidators(domicilioTipo);
    this.message = 'Generador seleccionado en modo consulta.';
  }

  editSelectedGenerador(): void {
    if (!this.selectedGenerador) {
      return;
    }

    this.isEditingGenerador = true;
    this.message = 'Edicion habilitada para el generador seleccionado.';
  }

  cancelEdit(): void {
    this.resetForm();
    this.message = '';
  }

  addTelefono(telefono?: Partial<Telefono>): void {
    if (!telefono && !this.canAddTelefono) {
      this.message = 'Completa el ultimo telefono antes de agregar otro.';
      return;
    }

    this.telefonos.push(this.fb.group({
      numero: [telefono?.numero ?? ''],
      tipo: [telefono?.tipo ?? 'WHATSAPP'],
      estado: [telefono?.estado ?? true],
    }));
  }

  removeTelefono(index: number): void {
    this.telefonos.removeAt(index);

    if (!this.telefonos.length) {
      this.addTelefono();
    }
  }

  nombreGenerador(generador: Generador): string {
    return generador.razonSocial
      || generador.nombreFantasia
      || [generador.nombre, generador.apellido].filter(Boolean).join(' ')
      || 'Generador sin nombre';
  }

  ubicacion(generador: Generador): string {
    return [generador.domicilio?.localidad, generador.domicilio?.provincia].filter(Boolean).join(', ')
      || 'Sin ubicacion';
  }

  telefonosVisibles(generador: Generador): Telefono[] {
    return generador.telefonos?.filter((telefono) => telefono.numero) ?? [];
  }

  telefonoLabel(telefono: Telefono): string {
    return `${telefono.tipo}: ${telefono.numero}`;
  }

  showDocumentWarning(controlName: 'cuit' | 'cuil'): boolean {
    const control = this.form.get(controlName);
    const value = String(control?.value ?? '').trim();

    return Boolean((control?.touched || control?.dirty) && value && !/^\d{11}$/.test(value));
  }

  private buildRequest(): GeneradorRequest {
    const value = this.form.value;
    const tipo = value.tipo as 'EMPRESA' | 'AUTONOMO';
    const request: GeneradorRequest = {
      tipo,
      estado: true,
      domicilio: {
        barrio: value.domicilioTipo === 'BARRIO' ? this.normalizeText(value.barrio) : undefined,
        calle: value.domicilioTipo === 'CALLE' ? this.normalizeText(value.calle) : undefined,
        altura: value.domicilioTipo === 'CALLE' ? value.altura ?? undefined : undefined,
        localidad: this.normalizeText(value.localidad),
        provincia: this.normalizeText(value.provincia),
        codigoPostal: value.codigoPostal ?? undefined,
      },
      telefonos: this.buildTelefonos(),
      legajo: this.normalizeText(value.legajo),
      email: this.normalizeText(value.email),
    };

    if (tipo === 'EMPRESA') {
      request.razonSocial = this.normalizeText(value.razonSocial);
      request.nombreFantasia = this.normalizeText(value.nombreFantasia);
      request.cuit = this.normalizeText(value.cuit);
    } else {
      request.nombre = this.normalizeText(value.nombre);
      request.apellido = this.normalizeText(value.apellido);
      request.cuil = this.normalizeText(value.cuil);
    }

    return request;
  }

  private buildTelefonos(): Telefono[] {
    return this.telefonos.controls
      .map((control) => control.value as { numero?: string; tipo?: TipoTelefono; estado?: boolean })
      .map((telefono) => ({
        numero: this.normalizeText(telefono.numero) ?? '',
        tipo: telefono.tipo ?? 'WHATSAPP',
        estado: telefono.estado ?? true,
      }))
      .filter((telefono) => Boolean(telefono.numero));
  }

  private configureTipoValidators(tipo: 'EMPRESA' | 'AUTONOMO'): void {
    this.setValidators('razonSocial', tipo === 'EMPRESA' ? [Validators.required] : []);
    this.setValidators('cuit', tipo === 'EMPRESA'
      ? [Validators.required, Validators.pattern(/^\d{11}$/)]
      : []
    );
    this.setValidators('nombre', tipo === 'AUTONOMO' ? [Validators.required] : []);
    this.setValidators('apellido', tipo === 'AUTONOMO' ? [Validators.required] : []);
    this.setValidators('cuil', tipo === 'AUTONOMO'
      ? [Validators.required, Validators.pattern(/^\d{11}$/)]
      : []
    );
  }

  private configureDomicilioValidators(tipoDomicilio: 'BARRIO' | 'CALLE'): void {
    this.setValidators('barrio', tipoDomicilio === 'BARRIO' ? [Validators.required] : []);
    this.setValidators('calle', tipoDomicilio === 'CALLE' ? [Validators.required] : []);
    this.setValidators('altura', tipoDomicilio === 'CALLE' ? [Validators.required, Validators.min(1)] : []);

    if (tipoDomicilio === 'BARRIO') {
      this.form.patchValue({ calle: '', altura: null }, { emitEvent: false });
      return;
    }

    this.form.patchValue({ barrio: '' }, { emitEvent: false });
  }

  private setValidators(controlName: string, validators: ValidatorFn[]): void {
    const control = this.form.get(controlName);
    control?.setValidators(validators);
    control?.updateValueAndValidity({ emitEvent: false });
  }

  private resetForm(): void {
    this.selectedGenerador = null;
    this.isEditingGenerador = true;
    this.form.reset({ tipo: 'EMPRESA', domicilioTipo: 'CALLE' });
    this.telefonos.clear();
    this.addTelefono();
    this.configureTipoValidators('EMPRESA');
    this.configureDomicilioValidators('CALLE');
  }

  private normalizeText(value: unknown): string | undefined {
    if (typeof value !== 'string') {
      return undefined;
    }

    const normalized = value.trim();
    return normalized || undefined;
  }

  private getErrorMessage(error: unknown): string {
    const apiError = (error as {
      error?: {
        message?: string;
        details?: { fieldErrors?: Record<string, string> };
      };
    })?.error;
    const fieldErrors = apiError?.details?.fieldErrors;

    if (fieldErrors && Object.keys(fieldErrors).length) {
      return Object.values(fieldErrors)[0];
    }

    return apiError?.message ?? 'No se pudo registrar el generador.';
  }
}
