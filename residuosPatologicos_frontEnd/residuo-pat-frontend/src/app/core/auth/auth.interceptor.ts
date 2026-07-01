import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (this.isAuthRequest(request.url)) {
      return next.handle(request);
    }

    const token = this.authService.getToken();

    if (!token) {
      return next.handle(request);
    }

    if (!this.isJwtLike(token)) {
      this.authService.logout();
      return next.handle(request);
    }

    return next.handle(
      request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    ).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.logout();
        }

        return throwError(() => error);
      })
    );
  }

  private isJwtLike(token: string): boolean {
    return token.split('.').length === 3;
  }

  private isAuthRequest(url: string): boolean {
    return url.startsWith(`${API_BASE_URL}/api/auth/`);
  }
}
