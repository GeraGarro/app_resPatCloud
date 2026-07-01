package com.appResP.residuosPatologicos.DTO.request;

import com.appResP.residuosPatologicos.models.enums.Meses;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CertificadoRequestDTO {

    @NotNull
    private Long transportistaId;  // era "transportista", nombre confuso

    @NotNull
    private Meses mes;

    @NotNull
    private Integer anio;

}
