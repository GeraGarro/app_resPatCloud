package com.appResP.residuosPatologicos.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HojaRutaConTicketsDTO {
    private Long id;
    private Long numeroHojaRuta;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long transportistaId;
    private int cantidadTickets;
    private BigDecimal pesoTotal;
    private List<TicketDTO> tickets;
}
