package com.appResP.residuosPatologicos.DTO.response;


import lombok.*;


@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class TipoResiduoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private boolean estadoActividad;
    private Long transportistaId;
}
