package com.appResP.residuosPatologicos.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportistaProfileStatusDTO {
    private boolean perfilRegistrado;
    private boolean vehiculoRegistrado;
    private boolean completo;
    private TransportistaDTO transportista;
    private List<String> pendientes;
}
