export interface ResiduoDTO {
    id?:   number;
    tipoResiduo: TipoResiduo;
    peso:         number;
    id_TicketControl: number;
}

export interface TipoResiduo {
    id?:     number;
    nombre: string;
    estado:       boolean;
}

 
