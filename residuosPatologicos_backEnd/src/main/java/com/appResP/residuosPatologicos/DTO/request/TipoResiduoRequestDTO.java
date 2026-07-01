package com.appResP.residuosPatologicos.DTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class TipoResiduoRequestDTO {

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;

    private boolean estadoActividad;
}