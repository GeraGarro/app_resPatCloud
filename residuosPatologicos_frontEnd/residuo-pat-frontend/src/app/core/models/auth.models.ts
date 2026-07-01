export type UserRole = 'TRANSPORTISTA' | 'GENERADOR' | 'ADMIN';
export type AccountStatus = 'PENDIENTE_APROBACION' | 'APROBADO' | 'RECHAZADO' | 'SUSPENDIDO';

export interface LoginRequest {
  email: string;
  password: string;
  rol: UserRole;
}

export interface RegisterRequest {
  email: string;
  password: string;
  rol: UserRole;
}

export interface RegisterResponse {
  email: string;
  rol: UserRole;
  message: string;
  confirmationUrl?: string | null;
}

export interface AuthResponse {
  token: string;
  email: string;
  rol: UserRole;
}

export interface SessionUser {
  email: string;
  rol: UserRole;
}

export interface AdminUser {
  id: number;
  email: string;
  rol: UserRole;
  estadoCuenta: AccountStatus;
  emailVerificado: boolean;
  fechaRegistro?: string | null;
  transportistaId?: number | null;
  nombre?: string | null;
  apellido?: string | null;
  nombreFantasia?: string | null;
}
