package com.appResP.residuosPatologicos.DTO.response;

import com.appResP.residuosPatologicos.DTO.embeddables.DomicilioDTO;
import com.appResP.residuosPatologicos.models.enums.TipoGenerador;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@SuperBuilder
public class GeneradorAutonomoDTO extends GeneradorDTO{

    private String nombre;
    private String apellido;
    private String cuil;
}
