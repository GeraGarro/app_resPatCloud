import { ITicket } from "./ticket.model";

export interface Hoja {
  id:          number;
  fechaInicio: Date;
  fechaFin:    Date;
  listaTickets: ITicket[];
}