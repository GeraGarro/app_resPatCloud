package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.request.TransportistaRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.TransportistaDTO;
import com.appResP.residuosPatologicos.models.Transportista;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransportistaMapper {

    private final DomicilioMapper domicilioMapper;
    private final VehiculoMapper vehiculoMapper;

    public  TransportistaDTO toDTO(Transportista t){
        if(t== null) return null;

        return TransportistaDTO.builder()
                .idTransportista(t.getIdTransportista())
                .nombre(t.getNombre())
                .apellido(t.getApellido())
                .nombreFantasia(t.getNombreFantasia())
                .cuit(t.getCuit())
                .cuil(t.getCuil())
                .email(t.getEmail())
                .telefono(t.getTelefono())
                .domicilio(domicilioMapper.toDto(t.getDomicilio()))
                .estado(t.isEstado())
                .usuarioId(t.getUsuario() != null ? t.getUsuario().getId() : null)
                .vehiculos(t.getVehiculos() == null
                    ? List.of()
                    : t.getVehiculos().stream().map(vehiculoMapper::toDTO).toList()
                )
                .build();
    }

    public Transportista toEntityTransportista (TransportistaRequestDTO dtoTransportista){
        if(dtoTransportista == null) return null;

        return Transportista.builder()

                .nombre(dtoTransportista.getNombre())
                .apellido(dtoTransportista.getApellido())
                .nombreFantasia(dtoTransportista.getNombreFantasia())
                .cuit(null)
                .cuil(dtoTransportista.getCuil())
                .telefono(dtoTransportista.getTelefono())
                .email(dtoTransportista.getEmail())
                .domicilio(domicilioMapper.toEntity(dtoTransportista.getDomicilio()))
                .estado(dtoTransportista.isEstado())
                .build();
    }
    public void updateTransportista(Transportista t, TransportistaDTO dto){
        t.setNombre(dto.getNombre());
        t.setApellido(dto.getApellido());
        t.setNombreFantasia(dto.getNombreFantasia());
        t.setCuit(dto.getCuit());
        t.setCuil(dto.getCuil());
        t.setTelefono(dto.getTelefono());
        t.setEmail(dto.getEmail());
        t.setEstado(dto.isEstado());
        t.setDomicilio(domicilioMapper.toEntity(dto.getDomicilio()));
    }
}
