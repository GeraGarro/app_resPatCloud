export interface Certificado {
    id?:              number;
    transportista:   Transportista;
    mes:             string;
    anio:            number;
    peso?:            number;
    listaTicketsDTO?: number[];
}


export interface Transportista {
    id_transportista: number;
    nombre:    string;
    apellido:  string;
    cuit:      string;
    telefono:  string;
    domicilio: string;
    estado:    boolean;
}

export enum Meses {
    ENERO = 1,
    FEBRERO = 2,
    MARZO = 3,
    ABRIL = 4,
    MAYO = 5,
    JUNIO = 6,
    JULIO = 7,
    AGOSTO = 8,
    SEPTIEMBRE = 9,
    OCTUBRE = 10,
    NOVIEMBRE = 11,
    DICIEMBRE = 12
}