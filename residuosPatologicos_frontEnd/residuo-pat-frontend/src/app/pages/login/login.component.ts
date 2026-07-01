import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { UserRole } from '../../core/models/auth.models';

interface AccessRoleOption {
  value: UserRole;
  label: string;
  description: string;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  readonly accessRoles: AccessRoleOption[] = [
    {
      value: 'TRANSPORTISTA',
      label: 'Transportista',
      description: 'Rutas, recolecciones, manifiestos e informes.',
    },
    {
      value: 'GENERADOR',
      label: 'Generador',
      description: 'Retiros, tickets y certificados propios.',
    },
    {
      value: 'ADMIN',
      label: 'Admin',
      description: 'Vista completa y configuracion del sistema.',
    },
  ];

  form!: FormGroup;
  showPassword = false;
  isLoading = false;
  errorMessage = '';

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

  ngOnInit(): void {
    this.form = this.fb.group({
      accessRole: ['TRANSPORTISTA' satisfies UserRole, Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      rememberMe: [true],
    });

    if (this.authService.isAuthenticated()) {
      void this.navigateByRole(this.authService.getCurrentUser()?.rol);
    }
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  selectRole(role: UserRole): void {
    this.form.patchValue({ accessRole: role });
  }

  isSelectedRole(role: UserRole): boolean {
    return this.form.get('accessRole')?.value === role;
  }

  submit(): void {
    this.errorMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMessage = 'Selecciona el rol correspondiente e ingresa email y contrasena.';
      return;
    }

    this.isLoading = true;

    this.authService.login({
      email: this.normalizeEmail(this.email.value),
      password: this.password.value as string,
      rol: this.form.get('accessRole')?.value as UserRole,
    })
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (response) => void this.navigateByRole(response.rol),
        error: (error: HttpErrorResponse) => {
          this.errorMessage = this.resolveError(error);
        },
      });
  }

  private resolveError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el backend local. Verifica que Spring Boot este iniciado.';
    }

    if (error.status === 401 || error.status === 403) {
      const backendMessage = typeof error.error === 'string'
        ? error.error
        : error.error?.message;

      return backendMessage || 'Email, contrasena o rol incorrecto.';
    }

    return 'No se pudo iniciar sesion. Intenta nuevamente.';
  }

  private navigateByRole(role?: UserRole): Promise<boolean> {
    if (role === 'ADMIN') {
      return this.router.navigate(['/admin/transportistas-pendientes']);
    }

    return this.router.navigate(['/home']);
  }

  private normalizeEmail(value: unknown): string {
    return typeof value === 'string' ? value.trim().toLowerCase() : '';
  }
}
