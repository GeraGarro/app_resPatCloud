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
@DiscriminatorValue("AUTONOMO")
public class GeneradorAutonomo extends Generador{

    private String nombre;
    private String apellido;

    @Column (unique = true, nullable = false, length = 11)
    private String cuil;
}
