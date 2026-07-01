package com.appResP.residuosPatologicos.models;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("EMPRESA")
public class GeneradorEmpresa extends Generador{

    @Column(unique = true, nullable = false, length = 11)
    private String cuit;

    private String razonSocial;

    private String nombreFantasia;

}
