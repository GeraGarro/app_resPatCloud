package com.appResP.residuosPatologicos.DTO.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class HojaRutaDetalleDTO {
    private Long id;
    private Long numeroHojaRuta;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long transportistaId;
    private List<TicketDTO> tickets;
}
