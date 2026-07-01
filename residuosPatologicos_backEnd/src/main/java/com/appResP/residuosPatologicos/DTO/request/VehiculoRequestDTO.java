package com.appResP.residuosPatologicos.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoRequestDTO {
    private String marca;
    private String modelo;
    private String dominio;
    private String chasis;
    private Integer anio;
    private boolean activo;
    private Long idTransportista;
}