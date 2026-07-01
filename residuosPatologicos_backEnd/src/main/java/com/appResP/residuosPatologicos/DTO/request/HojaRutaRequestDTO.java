package com.appResP.residuosPatologicos.DTO.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class HojaRutaRequestDTO {

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;
}
