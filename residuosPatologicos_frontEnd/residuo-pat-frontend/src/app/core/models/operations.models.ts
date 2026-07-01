export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface Domicilio {
  barrio?: string;
  calle?: string;
  altura?: number;
  departamento?: string;
  codigoPostal?: number;
  localidad?: string;
  provincia?: string;
}

export interface Vehiculo {
  idVehiculo: number;
  marca: string;
  modelo: string;
  dominio: string;
  chasis?: string;
  anio?: number;
  activo: boolean;
}

export interface VehiculoRequest {
  marca: string;
  modelo: string;
  dominio: string;
  chasis?: string;
  anio?: number;
  activo: boolean;
  idTransportista?: number;
}

export interface Transportista {
  idTransportista: number;
  nombre: string;
  apellido: string;
  nombreFantasia?: string;
  cuit?: string;
  cuil?: string;
  estado: boolean;
  email: string;
  telefono?: string;
  domicilio?: Domicilio;
  usuarioId?: number;
  vehiculos: Vehiculo[];
}

export interface TransportistaRequest {
  nombre: string;
  apellido: string;
  nombreFantasia?: string;
  cuit?: string;
  cuil?: string;
  estado: boolean;
  email: string;
  telefono?: string;
  domicilio?: Domicilio;
}

export interface TransportistaProfileStatus {
  perfilRegistrado: boolean;
  vehiculoRegistrado: boolean;
  completo: boolean;
  transportista?: Transportista | null;
  pendientes: string[];
}

export type TipoTelefono = 'CELULAR' | 'FIJO' | 'WHATSAPP';

export interface Telefono {
  id?: number;
  numero: string;
  tipo: TipoTelefono;
  estado: boolean;
}

export interface Generador {
  id: number;
  tipo: 'EMPRESA' | 'AUTONOMO';
  estado: boolean;
  domicilio?: Domicilio;
  telefonos?: Telefono[];
  legajo?: string;
  email?: string;
  usuarioId?: number;
  transportistaId?: number;
  cuit?: string;
  razonSocial?: string;
  nombreFantasia?: string;
  nombre?: string;
  apellido?: string;
  cuil?: string;
}

export interface GeneradorRequest {
  tipo: 'EMPRESA' | 'AUTONOMO';
  estado: boolean;
  domicilio: Domicilio;
  telefonos: Telefono[];
  legajo?: string;
  email?: string;
  nombre?: string;
  apellido?: string;
  cuil?: string;
  cuit?: string;
  razonSocial?: string;
  nombreFantasia?: string;
}

export interface TipoResiduo {
  id: number;
  codigo: string;
  nombre: string;
  estadoActividad: boolean;
  transportistaId?: number;
}

export interface TipoResiduoRequest {
  codigo: string;
  nombre: string;
  estadoActividad: boolean;
}

export interface Residuo {
  id: number;
  tipoResiduoId: number;
  peso: number;
}

export interface ResiduoRequest {
  tipoResiduoId: number;
  peso: number;
}

export interface TicketControl {
  idTicket: number;
  numeroTicket?: number;
  transportistaId: number;
  transportistaNombre?: string;
  hojaRutaId: number;
  generadorId: number;
  fechaEmision: string;
  horario: string;
  estado: boolean;
  residuos: Residuo[];
  pesoTotal: number;
}

export interface TicketRequest {
  transportistaId: number;
  hojaRutaId: number;
  generadorId: number;
  fechaEmision?: string;
  horario?: string;
  estado?: boolean;
  residuos: ResiduoRequest[];
}

export interface HojaRuta {
  id: number;
  numeroHojaRuta?: number;
  fechaInicio: string;
  fechaFin: string;
  transportistaId?: number;
  cantidadTickets: number;
}

export interface Certificado {
  id: number;
  numeroCertificado?: number;
  transportistaId: number;
  mes: string;
  anio: number;
  cantidadHojasRuta: number;
}

export interface HojaRutaConTickets extends HojaRuta {
  tickets: TicketControl[];
  pesoTotal: number;
}

export interface TransportistaDashboardData {
  hojaActual: HojaRuta | null;
  ticketsActuales: TicketControl[];
  generadoresActivos: Generador[];
  tiposResiduo: TipoResiduo[];
  certificados: Certificado[];
  certificadosTotal?: number;
  hojasPendientesCertificado?: HojaRuta[];
  ticketsPeriodoMensual?: TicketControl[];
  transportistas: Transportista[];
}
