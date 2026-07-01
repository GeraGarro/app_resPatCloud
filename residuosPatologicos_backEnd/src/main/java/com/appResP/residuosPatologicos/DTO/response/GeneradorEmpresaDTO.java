package com.appResP.residuosPatologicos.DTO.response;

import com.appResP.residuosPatologicos.DTO.embeddables.DomicilioDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@SuperBuilder
public class GeneradorEmpresaDTO extends GeneradorDTO{


    private String cuit;
    private String razonSocial;
    private String nombreFantasia;

}
