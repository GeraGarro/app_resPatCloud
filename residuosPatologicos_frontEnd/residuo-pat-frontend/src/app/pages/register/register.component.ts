import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { UserRole } from '../../core/models/auth.models';

interface RegisterRoleOption {
  value: UserRole;
  label: string;
  description: string;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  readonly roles: RegisterRoleOption[] = [
    {
      value: 'TRANSPORTISTA',
      label: 'Transportista',
      description: 'Gestion de rutas, vehiculos y recolecciones.',
    }
  ];

  form!: FormGroup;
  showPassword = false;
  showConfirmPassword = false;
  isLoading = false;
  isResending = false;
  errorMessage = '';
  successMessage = '';
  registeredEmail = '';
  confirmationUrl: string | null = null;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  get email() {
    return this.form.get('email')!;
  }

  get password() {
    return this.form.get('password')!;
  }

  get confirmPassword() {
    return this.form.get('confirmPassword')!;
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      accessRole: ['TRANSPORTISTA' satisfies UserRole, Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
    }, { validators: this.passwordsMatch });

    if (this.authService.isAuthenticated()) {
      const user = this.authService.getCurrentUser();
      void this.router.navigate([user?.rol === 'ADMIN' ? '/admin/transportistas-pendientes' : '/home']);
    }
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  selectRole(role: UserRole): void {
    this.form.patchValue({ accessRole: role });
  }

  isSelectedRole(role: UserRole): boolean {
    return this.form.get('accessRole')?.value === role;
  }

  submit(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.confirmationUrl = null;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMessage = 'Completa el formulario con un email valido y contrasenas coincidentes.';
      return;
    }

    this.isLoading = true;

    this.authService.register({
      email: this.normalizeEmail(this.email.value),
      password: this.password.value as string,
      rol: this.form.get('accessRole')?.value as UserRole,
    })
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (response) => {
          this.registeredEmail = response.email;
          this.successMessage = response.message || 'Registro creado. Revisa tu correo para confirmar la cuenta.';
          this.confirmationUrl = response.confirmationUrl ?? null;
          this.form.disable();
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage = this.resolveError(error);
        },
      });
  }

  resendConfirmation(): void {
    if (!this.registeredEmail) {
      return;
    }

    this.errorMessage = '';
    this.isResending = true;

    this.authService.resendConfirmation(this.registeredEmail)
      .pipe(finalize(() => (this.isResending = false)))
      .subscribe({
        next: (response) => {
          this.successMessage = response.message || 'Se envio un nuevo correo de confirmacion.';
          this.confirmationUrl = response.confirmationUrl ?? null;
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage = this.resolveError(error);
        },
      });
  }

  private passwordsMatch(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    if (!password || !confirmPassword) {
      return null;
    }

    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  private resolveError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el backend local. Verifica que Spring Boot este iniciado.';
    }

    if (error.status === 400 || error.status === 409 || error.status === 500) {
      const backendMessage = typeof error.error === 'string'
        ? error.error
        : error.error?.message;

      return backendMessage || 'Ese email ya esta registrado o los datos no son validos.';
    }

    return 'No se pudo crear la cuenta. Intenta nuevamente.';
  }

  private normalizeEmail(value: unknown): string {
    return typeof value === 'string' ? value.trim().toLowerCase() : '';
  }
}
