package com.appResP.residuosPatologicos.DTO.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TicketDTO {

    private Long idTicket;
    private Long numeroTicket;

    private Long transportistaId;
    private String transportistaNombre;
    private Long hojaRutaId;
    private Long generadorId;
    private LocalDate fechaEmision;
    private LocalTime horario;
    private boolean estado;

    @Builder.Default
    private List<ResiduoDTO> residuos = new ArrayList<>();

    private BigDecimal pesoTotal;

}
