package com.appResP.residuosPatologicos.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ResiduoDTO {

  private Long id;

  private TipoResiduoDTO tipoResiduo;

  private float peso;

  private Long id_TicketControl;

}
