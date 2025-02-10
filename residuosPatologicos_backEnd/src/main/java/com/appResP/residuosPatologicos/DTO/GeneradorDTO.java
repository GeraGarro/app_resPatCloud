package com.appResP.residuosPatologicos.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneradorDTO {
    private Long id;

    private String nombre;
    private String cuit;
    private String direccion;
    private String legajo;
    private boolean estado;
    private String telefono;

}
