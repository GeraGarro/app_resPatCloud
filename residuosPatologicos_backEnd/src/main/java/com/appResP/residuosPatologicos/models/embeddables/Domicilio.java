package com.appResP.residuosPatologicos.models.embeddables;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Domicilio {
private String barrio;
private String calle;
private Integer altura;
private String departamento;
private Integer codigoPostal;
private String localidad;
private String provincia;
}
