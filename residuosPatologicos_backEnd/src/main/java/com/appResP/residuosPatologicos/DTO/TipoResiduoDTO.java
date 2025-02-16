package com.appResP.residuosPatologicos.DTO;


import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoResiduoDTO {

    private Long id;
    private String nombre;
    private String codigo;
    private boolean estado;

}