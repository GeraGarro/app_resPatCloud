package com.appResP.residuosPatologicos.DTO;

import com.appResP.residuosPatologicos.models.enums.Meses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificadoDTO {

    private Long id;
    private TransportistaDTO transportista;
    private Meses mes;
    private Integer anio;
    private float peso;

    private List <Long> listaTicketsDTO;

    
}
