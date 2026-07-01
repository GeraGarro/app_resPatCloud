package com.appResP.residuosPatologicos.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketResiduoReport {
    private String codigo;
    private String nombre;
    private BigDecimal peso;
}
