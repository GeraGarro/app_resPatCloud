package com.appResP.residuosPatologicos.DTO.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HojaRutaDTO {

    private Long id;
    private Long numeroHojaRuta;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long transportistaId;
    private int cantidadTickets;
}
