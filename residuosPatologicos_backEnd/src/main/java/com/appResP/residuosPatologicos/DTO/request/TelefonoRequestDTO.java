package com.appResP.residuosPatologicos.DTO.request;

import com.appResP.residuosPatologicos.models.enums.TipoTelefono;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelefonoRequestDTO {
    private String numero;
    private TipoTelefono tipo;
    private boolean estado;
}
