import { ITicket } from "./ticket.model";
export interface Hoja_ruta {
  data:    Hoja;
  success: boolean;
}

export interface Hoja {
  id:          number;
  fechaInicio: Date;
  fechaFin:    Date;
  listaTickets: ITicket[];
}