import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { AdminUser } from '../../core/models/auth.models';
import { AdminService } from '../../core/services/admin.service';

type AdminAction = 'aprobar' | 'rechazar' | 'suspender';

@Component({
  selector: 'app-admin-transportistas-pendientes',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-transportistas-pendientes.component.html',
  styleUrls: ['./admin-transportistas-pendientes.component.scss']
})
export class AdminTransportistasPendientesComponent implements OnInit {
  usuarios: AdminUser[] = [];
  isLoading = true;
  actionUserId: number | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly adminService: AdminService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.loadPendientes();
  }

  loadPendientes(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.adminService.getTransportistasPendientes()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (usuarios) => {
          this.usuarios = usuarios;
        },
        error: (error: HttpErrorResponse) => {
          this.errorMessage = this.resolveError(error);
        },
      });
  }

  ejecutarAccion(usuario: AdminUser, accion: AdminAction): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.actionUserId = usuario.id;

    const request = accion === 'aprobar'
      ? this.adminService.aprobarUsuario(usuario.id)
      : accion === 'rechazar'
        ? this.adminService.rechazarUsuario(usuario.id)
        : this.adminService.suspenderUsuario(usuario.id);

    request.pipe(finalize(() => (this.actionUserId = null))).subscribe({
      next: () => {
        this.usuarios = this.usuarios.filter((item) => item.id !== usuario.id);
        this.successMessage = this.actionMessage(usuario.email, accion);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.resolveError(error);
      },
    });
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }

  nombreVisible(usuario: AdminUser): string {
    const nombre = [usuario.nombre, usuario.apellido].filter(Boolean).join(' ').trim();

    return nombre || usuario.nombreFantasia || 'Perfil pendiente';
  }

  fechaVisible(value?: string | null): string {
    if (!value) {
      return 'Sin fecha';
    }

    return new Date(value).toLocaleDateString('es-AR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  private actionMessage(email: string, accion: AdminAction): string {
    const labels: Record<AdminAction, string> = {
      aprobar: 'aprobado',
      rechazar: 'rechazado',
      suspender: 'suspendido',
    };

    return `Usuario ${email} ${labels[accion]} correctamente.`;
  }

  private resolveError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el backend local. Verifica que Spring Boot este iniciado.';
    }

    if (error.status === 401 || error.status === 403) {
      return error.error?.message || 'No tenes permisos para administrar usuarios.';
    }

    return error.error?.message || 'No se pudo completar la accion administrativa.';
  }
}
