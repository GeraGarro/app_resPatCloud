package com.appResP.residuosPatologicos.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class residuosReportDTO {
    private String id;
    private String tipoDeResiduo;
    private float peso;
}
