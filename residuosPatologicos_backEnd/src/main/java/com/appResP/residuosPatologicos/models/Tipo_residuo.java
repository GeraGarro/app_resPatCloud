package com.appResP.residuosPatologicos.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class Tipo_residuo  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String nombre;
    private boolean estado;

    @OneToMany(targetEntity = Residuo.class, fetch = FetchType.LAZY,mappedBy = "tipoResiduo")
    @JsonIgnore
    private List<Residuo> listaResiduo;
}
