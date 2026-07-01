import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-confirm-email',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './confirm-email.component.html',
  styleUrls: ['./confirm-email.component.scss']
})
export class ConfirmEmailComponent implements OnInit {
  isLoading = true;
  isConfirmed = false;
  message = 'Estamos verificando tu correo.';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.isLoading = false;
      this.message = 'El link de confirmacion no tiene token.';
      return;
    }

    this.authService.confirmEmail(token)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.isConfirmed = true;
          this.message = 'Correo confirmado. Tu cuenta ya esta activa.';
        },
        error: (error: HttpErrorResponse) => {
          this.message = this.resolveError(error);
        },
      });
  }

  goHome(): void {
    void this.router.navigate(['/home']);
  }

  private resolveError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el backend local. Verifica que Spring Boot este iniciado.';
    }

    return error.error?.message || 'No se pudo confirmar el correo. Solicita un nuevo link.';
  }
}
