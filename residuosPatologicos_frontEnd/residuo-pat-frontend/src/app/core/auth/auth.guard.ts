import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserRole } from '../models/auth.models';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  const allowedRoles = route.data?.['roles'] as UserRole[] | undefined;

  if (!allowedRoles?.length) {
    return true;
  }

  const user = authService.getCurrentUser();

  if (user && allowedRoles.includes(user.rol)) {
    return true;
  }

  return router.createUrlTree([user?.rol === 'ADMIN' ? '/admin/transportistas-pendientes' : '/home']);
};
