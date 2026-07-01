package com.appResP.residuosPatologicos.DTO.response;

import com.appResP.residuosPatologicos.DTO.embeddables.DomicilioDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransportistaDTO {

    private Long idTransportista;
    private String nombre;
    private String apellido;
    private String nombreFantasia;
    private String cuit;
    private String cuil;
    private boolean estado;
    private String email;
    private String telefono;
    private DomicilioDTO domicilio;
    private Long usuarioId;

    @Builder.Default
    private List<VehiculoDTO> vehiculos= new ArrayList<>();
}
