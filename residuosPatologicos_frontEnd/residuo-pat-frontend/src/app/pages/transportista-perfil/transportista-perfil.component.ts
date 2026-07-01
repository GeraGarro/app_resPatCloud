import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Transportista, TransportistaProfileStatus } from '../../core/models/operations.models';
import { TransportistaService } from '../../core/services/transportista.service';

@Component({
  selector: 'app-transportista-perfil',
  templateUrl: './transportista-perfil.component.html',
  styleUrl: './transportista-perfil.component.scss'
})
export class TransportistaPerfilComponent implements OnInit {
  profileForm!: FormGroup;
  status: TransportistaProfileStatus | null = null;
  transportista: Transportista | null = null;
  isEditingProfile = false;
  isLoading = true;
  isSavingProfile = false;
  emailEditable = false;
  message = '';
  userEmail = '';
  userRole = '';

  get profileInitials(): string {
    if (this.transportista) {
      return `${this.transportista.nombre?.[0] ?? ''}${this.transportista.apellido?.[0] ?? ''}`.toUpperCase() || 'TP';
    }

    return (this.userEmail?.[0] ?? 'U').toUpperCase();
  }

  get domicilioTipo(): 'BARRIO' | 'CALLE' {
    return this.profileForm.get('domicilioTipo')?.value as 'BARRIO' | 'CALLE';
  }

  get cuilDigitsCount(): number {
    return this.digitsOnly(this.profileForm.get('cuil')?.value).length;
  }

  get showCuilWarning(): boolean {
    const control = this.profileForm.get('cuil');

    return Boolean((control?.touched || control?.dirty) && control.invalid);
  }

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly transportistaService: TransportistaService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    this.userEmail = currentUser?.email ?? '';
    this.userRole = currentUser?.rol ?? '';

    this.profileForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      nombreFantasia: [''],
      cuil: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      telefono: [''],
      email: [{ value: this.userEmail, disabled: true }, [Validators.required, Validators.email]],
      domicilioTipo: ['CALLE', Validators.required],
      barrio: [''],
      calle: [''],
      altura: [null],
      codigoPostal: [null],
      localidad: ['', Validators.required],
      provincia: ['', Validators.required],
    });

    this.profileForm.get('domicilioTipo')?.valueChanges.subscribe((tipoDomicilio) => {
      this.configureDomicilioValidators(tipoDomicilio);
    });
    this.profileForm.get('cuil')?.valueChanges.subscribe((value) => {
      const normalized = this.digitsOnly(value).slice(0, 11);

      if (value !== normalized) {
        this.profileForm.get('cuil')?.setValue(normalized, { emitEvent: false });
      }
    });
    this.configureDomicilioValidators(this.profileForm.get('domicilioTipo')?.value);
    this.loadStatus();
  }

  loadStatus(): void {
    this.isLoading = true;
    this.message = '';

    this.transportistaService.getProfileStatus().subscribe({
      next: (status) => {
        this.status = status;
        this.transportista = status.transportista ?? null;
        this.isEditingProfile = !this.transportista;
        this.patchProfileForm();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.message = 'No se pudo cargar el estado del perfil.';
      },
    });
  }

  editProfile(): void {
    this.isEditingProfile = true;
    this.patchProfileForm();
  }

  cancelProfileEdit(): void {
    this.isEditingProfile = false;
    this.emailEditable = false;
    this.patchProfileForm();
  }

  toggleEmailEdit(): void {
    this.emailEditable = !this.emailEditable;
    const emailControl = this.profileForm.get('email');

    if (this.emailEditable) {
      emailControl?.enable();
      emailControl?.markAsTouched();
      return;
    }

    emailControl?.disable();
  }

  saveProfile(): void {
    this.message = '';

    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      this.message = this.buildValidationMessage();
      return;
    }

    this.isSavingProfile = true;
    const request = this.buildTransportistaRequest();
    const isCreatingProfile = !this.transportista;
    const action = this.transportista
      ? this.transportistaService.update(this.transportista.idTransportista, request)
      : this.transportistaService.create(request);

    action.subscribe({
      next: (transportista) => {
        this.transportista = transportista;
        this.isSavingProfile = false;
        this.isEditingProfile = false;
        this.message = 'Datos del transportista guardados.';

        if (isCreatingProfile) {
          void this.router.navigate(['/transportista/vehiculos']);
          return;
        }

        this.loadStatus();
      },
      error: (error) => {
        this.isSavingProfile = false;
        this.message = this.getErrorMessage(error);
      },
    });
  }

  private patchProfileForm(): void {
    if (!this.transportista) {
      this.profileForm.reset({
        nombre: '',
        apellido: '',
        nombreFantasia: '',
        cuil: '',
        telefono: '',
        email: this.userEmail,
        domicilioTipo: 'CALLE',
        barrio: '',
        calle: '',
        altura: null,
        codigoPostal: null,
        localidad: '',
        provincia: '',
      });
      this.syncEmailControlState();
      return;
    }

    const domicilioTipo = this.transportista.domicilio?.barrio ? 'BARRIO' : 'CALLE';
    this.profileForm.patchValue({
      nombre: this.transportista.nombre,
      apellido: this.transportista.apellido,
      nombreFantasia: this.transportista.nombreFantasia,
      cuil: this.transportista.cuil,
      telefono: this.transportista.telefono,
      email: this.transportista.email,
      domicilioTipo,
      barrio: this.transportista.domicilio?.barrio,
      calle: this.transportista.domicilio?.calle,
      altura: this.transportista.domicilio?.altura,
      codigoPostal: this.transportista.domicilio?.codigoPostal,
      localidad: this.transportista.domicilio?.localidad,
      provincia: this.transportista.domicilio?.provincia,
    });
    this.configureDomicilioValidators(domicilioTipo);
    this.syncEmailControlState();
  }

  private buildTransportistaRequest() {
    const value = this.profileForm.getRawValue();

    return {
      nombre: this.normalizeText(value.nombre) ?? '',
      apellido: this.normalizeText(value.apellido) ?? '',
      nombreFantasia: this.normalizeText(value.nombreFantasia),
      cuil: this.digitsOnly(value.cuil),
      telefono: this.normalizeText(value.telefono),
      email: this.normalizeText(value.email) ?? '',
      estado: true,
      domicilio: {
        barrio: value.domicilioTipo === 'BARRIO' ? this.normalizeText(value.barrio) : undefined,
        calle: value.domicilioTipo === 'CALLE' ? this.normalizeText(value.calle) : undefined,
        altura: value.domicilioTipo === 'CALLE' ? value.altura ?? undefined : undefined,
        codigoPostal: value.codigoPostal ?? undefined,
        localidad: this.normalizeText(value.localidad),
        provincia: this.normalizeText(value.provincia),
      },
    };
  }

  private configureDomicilioValidators(tipoDomicilio: 'BARRIO' | 'CALLE'): void {
    this.setValidators('barrio', tipoDomicilio === 'BARRIO' ? [Validators.required] : []);
    this.setValidators('calle', tipoDomicilio === 'CALLE' ? [Validators.required] : []);
    this.setValidators('altura', tipoDomicilio === 'CALLE' ? [Validators.required, Validators.min(1)] : []);

    if (tipoDomicilio === 'BARRIO') {
      this.profileForm.patchValue({ calle: '', altura: null }, { emitEvent: false });
      return;
    }

    this.profileForm.patchValue({ barrio: '' }, { emitEvent: false });
  }

  private setValidators(controlName: string, validators: ValidatorFn[]): void {
    const control = this.profileForm.get(controlName);
    control?.setValidators(validators);
    control?.updateValueAndValidity({ emitEvent: false });
  }

  private normalizeText(value: unknown): string | undefined {
    if (typeof value !== 'string') {
      return undefined;
    }

    const normalized = value.trim();
    return normalized || undefined;
  }

  private digitsOnly(value: unknown): string {
    return typeof value === 'string' ? value.replace(/\D/g, '') : '';
  }

  private buildValidationMessage(): string {
    if (this.profileForm.get('cuil')?.invalid) {
      return `El CUIL debe tener exactamente 11 digitos. Ingresaste ${this.cuilDigitsCount}.`;
    }

    if (this.domicilioTipo === 'BARRIO' && this.profileForm.get('barrio')?.invalid) {
      return 'Completa el barrio del domicilio.';
    }

    if (this.domicilioTipo === 'CALLE') {
      if (this.profileForm.get('calle')?.invalid) {
        return 'Completa la calle del domicilio.';
      }

      if (this.profileForm.get('altura')?.invalid) {
        return 'Completa la numeracion del domicilio.';
      }
    }

    if (this.profileForm.get('localidad')?.invalid || this.profileForm.get('provincia')?.invalid) {
      return 'Completa localidad y provincia.';
    }

    return 'Completa los campos obligatorios del transportista.';
  }

  private syncEmailControlState(): void {
    const emailControl = this.profileForm.get('email');

    if (this.emailEditable) {
      emailControl?.enable({ emitEvent: false });
      return;
    }

    emailControl?.disable({ emitEvent: false });
  }

  private getErrorMessage(error: unknown): string {
    return (error as { error?: { message?: string } })?.error?.message
      ?? 'No se pudo completar la operacion.';
  }
}
