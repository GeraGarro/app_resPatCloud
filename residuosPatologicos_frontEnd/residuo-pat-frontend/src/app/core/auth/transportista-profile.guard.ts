import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { TransportistaService } from '../services/transportista.service';
import { AuthService } from './auth.service';

export const transportistaProfileGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const transportistaService = inject(TransportistaService);
  const router = inject(Router);
  const user = authService.getCurrentUser();

  if (user?.rol !== 'TRANSPORTISTA') {
    return true;
  }

  return transportistaService.getProfileStatus().pipe(
    map((status) => {
      if (status.perfilRegistrado) {
        return true;
      }

      return router.createUrlTree(['/transportista/perfil']);
    }),
    catchError(() => of(router.createUrlTree(['/transportista/perfil'])))
  );
};
