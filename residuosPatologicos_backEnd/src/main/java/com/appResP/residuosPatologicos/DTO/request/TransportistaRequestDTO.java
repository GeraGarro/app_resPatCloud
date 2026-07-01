package com.appResP.residuosPatologicos.DTO.request;

import com.appResP.residuosPatologicos.DTO.embeddables.DomicilioDTO;
import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class TransportistaRequestDTO {

    private String nombre;

    private String apellido;

    private String nombreFantasia;

    private String cuit;

    private  String cuil;

    private String telefono;

    private  String email;

    private  DomicilioDTO domicilio;

    boolean estado;

}
