package com.appResP.residuosPatologicos.DTO.response;

import com.appResP.residuosPatologicos.models.enums.Meses;
import lombok.*;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class CertificadoDTO {
        private Long id;
        private Long numeroCertificado;
        private Long transportistaId;
        private Meses mes;
        private int anio;
        private int cantidadHojasRuta;
}
