package com.appResP.residuosPatologicos.DTO.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ResiduoRequestDTO {

    @NotNull(message = "El tipo de residuo es obligatorio")
    private Long tipoResiduoId;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.001", message = "El peso debe ser mayor a 0")
    private BigDecimal peso;
}
