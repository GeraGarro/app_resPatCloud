package com.appResP.residuosPatologicos.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HojaRutaReportRow {
    private String nro;
    private String generadorNombre;
    private String generadorDomicilio;
    private String generadorLocalidad;
    private String fechaVisita;
    private String comprobante;
}
