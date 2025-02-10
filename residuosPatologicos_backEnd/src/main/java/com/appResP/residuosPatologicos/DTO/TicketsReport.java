package com.appResP.residuosPatologicos.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketsReport {
private String id_ticket;
private String generador_nombre;
private String fechaEmision;
private BigDecimal peso;
}
