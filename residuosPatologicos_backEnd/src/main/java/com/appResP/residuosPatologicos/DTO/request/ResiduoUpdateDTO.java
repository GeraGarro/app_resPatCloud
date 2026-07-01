package com.appResP.residuosPatologicos.DTO.request;


import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResiduoUpdateDTO {

    @DecimalMin(value = "0.001", message = "El peso debe ser mayor a 0")
    private BigDecimal peso;

    private Long tipoResiduoId; // Opcional: permitir cambiar el tipo
}
