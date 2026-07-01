package com.appResP.residuosPatologicos.DTO.response;

import com.appResP.residuosPatologicos.models.enums.TipoTelefono;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelefonoDTO {
    private Long id;
    private String numero;
    private TipoTelefono tipo;
    private boolean estado;
}
