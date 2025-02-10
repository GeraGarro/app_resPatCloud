package com.appResP.residuosPatologicos.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hoja_RutaDTO {

    private Long id;
    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    List<Ticket_controlDTO> listaTickets;
}
