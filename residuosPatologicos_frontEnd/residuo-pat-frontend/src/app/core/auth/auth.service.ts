import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';
import { AuthResponse, LoginRequest, RegisterRequest, RegisterResponse, SessionUser } from '../models/auth.models';

const TOKEN_KEY = 'respat.auth.token';
const USER_KEY = 'respat.auth.user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly isBrowser: boolean;
  private readonly currentUserSubject = new BehaviorSubject<SessionUser | null>(null);

  readonly currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private readonly http: HttpClient,
    @Inject(PLATFORM_ID) platformId: object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    this.currentUserSubject.next(this.readStoredUser());
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_BASE_URL}/api/auth/login`, credentials)
      .pipe(tap((response) => this.persistSession(response)));
  }

  register(data: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${API_BASE_URL}/api/auth/register`, data);
  }

  confirmEmail(token: string): Observable<AuthResponse> {
    return this.http
      .get<AuthResponse>(`${API_BASE_URL}/api/auth/confirm-email`, { params: { token } })
      .pipe(tap((response) => this.persistSession(response)));
  }

  resendConfirmation(email: string): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${API_BASE_URL}/api/auth/resend-confirmation`, { email });
  }

  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
    }

    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    if (!this.isBrowser) {
      return null;
    }

    const token = localStorage.getItem(TOKEN_KEY);

    if (token && !this.isUsableToken(token)) {
      this.logout();
      return null;
    }

    return token;
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken());
  }

  getCurrentUser(): SessionUser | null {
    return this.currentUserSubject.value;
  }

  private persistSession(response: AuthResponse): void {
    if (!this.isJwtLike(response.token)) {
      throw new Error('La respuesta de login no contiene un token valido.');
    }

    const user: SessionUser = {
      email: response.email,
      rol: response.rol,
    };

    if (this.isBrowser) {
      localStorage.setItem(TOKEN_KEY, response.token);
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    }

    this.currentUserSubject.next(user);
  }

  private readStoredUser(): SessionUser | null {
    if (!this.isBrowser) {
      return null;
    }

    const token = localStorage.getItem(TOKEN_KEY);
    if (!this.isUsableToken(token)) {
      localStorage.removeItem(USER_KEY);
      localStorage.removeItem(TOKEN_KEY);
      return null;
    }

    const stored = localStorage.getItem(USER_KEY);

    if (!stored) {
      return null;
    }

    try {
      return JSON.parse(stored) as SessionUser;
    } catch {
      localStorage.removeItem(USER_KEY);
      localStorage.removeItem(TOKEN_KEY);
      return null;
    }
  }

  private isJwtLike(token: string | null | undefined): boolean {
    return typeof token === 'string' && token.split('.').length === 3;
  }

  private isUsableToken(token: string | null | undefined): boolean {
    return this.isJwtLike(token) && !this.isTokenExpired(token as string);
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1])) as { exp?: number };

      if (!payload.exp) {
        return true;
      }

      return payload.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}
