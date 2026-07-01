package com.appResP.residuosPatologicos.DTO.response;

import com.appResP.residuosPatologicos.DTO.embeddables.DomicilioDTO;
import com.appResP.residuosPatologicos.models.enums.TipoGenerador;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GeneradorEmpresaDTO.class, name = "EMPRESA"),
        @JsonSubTypes.Type(value = GeneradorAutonomoDTO.class, name = "AUTONOMO")
})
public abstract class GeneradorDTO {

    private Long id;
    private TipoGenerador tipo;

    // comunes
    private boolean estado;
    private DomicilioDTO domicilio;
    @Builder.Default
    private List<TelefonoDTO> telefonos=new ArrayList<>();
    private String legajo;
    private String email;

    private Long usuarioId;
    private Long transportistaId;
}
