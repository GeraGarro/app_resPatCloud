package com.appResP.residuosPatologicos.DTO.response;

import com.appResP.residuosPatologicos.models.enums.Meses;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CertificadoDetalleDTO {
    private Long id;
    private Long numeroCertificado;
    private Long transportistaId;
    private Meses mes;
    private int anio;
    private List<HojaRutaDTO> hojasRuta;
}
