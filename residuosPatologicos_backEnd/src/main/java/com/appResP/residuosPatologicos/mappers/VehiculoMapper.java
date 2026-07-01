package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.request.VehiculoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.TransportistaDTO;
import com.appResP.residuosPatologicos.DTO.response.VehiculoDTO;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.Vehiculo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehiculoMapper {

    public  VehiculoDTO toDTO(Vehiculo v)
    { return VehiculoDTO.builder()
            .idVehiculo(v.getId())
            .marca(v.getMarca())
            .modelo(v.getModelo())
            .dominio(v.getDominio())
            .chasis(v.getChasis())
            .anio(v.getAnio())
            .activo(v.isActivo())
            .build();
    }

    /**
     * Crear entidad desde DTO.
     * Nota: No seteamos idVehiculo porque lo genera la base de datos.
     */
    public Vehiculo toEntity(VehiculoRequestDTO dto, Transportista t) {
        if (dto == null) return null;

        return Vehiculo.builder()
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .dominio(dto.getDominio())
                .chasis(dto.getChasis())
                .anio(dto.getAnio())
                .activo(dto.isActivo())
                .transportista(t)
                .build();
    }

    public void updateVehiculo(Vehiculo v, VehiculoRequestDTO dto){
      v.setMarca(dto.getMarca());
      v.setModelo(dto.getModelo());
      v.setDominio(dto.getDominio());
      v.setChasis(dto.getChasis());
      v.setAnio(dto.getAnio());
    }
}
