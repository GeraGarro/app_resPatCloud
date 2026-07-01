package com.appResP.residuosPatologicos.DTO.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder

public class ResiduoDTO {

  private Long id;

  private Long tipoResiduoId;

  private BigDecimal peso;

}
