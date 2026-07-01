import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { SessionUser } from '../../core/models/auth.models';
import { TransportistaService } from '../../core/services/transportista.service';

interface NavItem {
  label: string;
  route: string;
  shortLabel: string;
  description: string;
  marker: string;
  home?: boolean;
}

@Component({
  selector: 'app-transportista-sidebar',
  templateUrl: './transportista-sidebar.component.html',
  styleUrl: './transportista-sidebar.component.scss'
})
export class TransportistaSidebarComponent implements OnInit {
  @Input() displayName = '';

  readonly user: SessionUser | null;
  private resolvedDisplayName = '';

  readonly navItems: NavItem[] = [
    {
      label: 'Inicio',
      shortLabel: 'Inicio',
      route: '/home',
      description: 'Volver al panel principal',
      marker: 'IN',
      home: true,
    },
    {
      label: 'Perfil',
      shortLabel: 'Perfil',
      route: '/transportista/perfil',
      description: 'Datos del usuario',
      marker: 'PF',
    },
    {
      label: 'Generadores',
      shortLabel: 'Generadores',
      route: '/transportista/generadores',
      description: 'Clientes y telefonos',
      marker: 'GE',
    },
    {
      label: 'Vehiculos',
      shortLabel: 'Vehiculos',
      route: '/transportista/vehiculos',
      description: 'Unidades disponibles',
      marker: 'VH',
    },
    {
      label: 'Tipos de residuos',
      shortLabel: 'Residuos',
      route: '/transportista/tipos-residuo',
      description: 'Categorias registradas',
      marker: 'TR',
    },
    {
      label: 'Certificados',
      shortLabel: 'Certificados',
      route: '/transportista/certificados',
      description: 'Periodos y documentos',
      marker: 'CE',
    },
  ];

  constructor(
    private readonly authService: AuthService,
    private readonly transportistaService: TransportistaService,
    private readonly router: Router
  ) {
    this.user = this.authService.getCurrentUser();
  }

  ngOnInit(): void {
    this.transportistaService.getProfileStatus().subscribe({
      next: (status) => {
        const transportista = status.transportista;
        this.resolvedDisplayName = transportista
          ? `${transportista.nombre} ${transportista.apellido}`
          : '';
      },
      error: () => {
        this.resolvedDisplayName = '';
      },
    });
  }

  get userName(): string {
    return this.displayName || this.resolvedDisplayName || 'Transportista';
  }

  get initials(): string {
    return this.userName.slice(0, 2).toUpperCase();
  }

  get sectionNavItems(): NavItem[] {
    return this.navItems.filter((item) => !item.home);
  }

  isActive(route: string): boolean {
    return this.router.isActive(route, {
      paths: 'exact',
      queryParams: 'ignored',
      fragment: 'ignored',
      matrixParams: 'ignored',
    });
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }
}
