export interface Residuo {
    id: number;
    TipoResiduoTDO:  TipoResiduo;
   /*  peso:       number; */
}

export interface TipoResiduo {
    id:     number;
    nombre: string;
    estado:       boolean;
}
