package com.appResP.residuosPatologicos.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehiculoDTO {
    private Long idVehiculo;
    private String marca;
    private String modelo;
    private String dominio;
    private String chasis;
    private Integer anio;
    private boolean activo;
}
