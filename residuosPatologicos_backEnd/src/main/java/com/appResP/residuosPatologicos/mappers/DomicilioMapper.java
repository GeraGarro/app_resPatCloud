package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.embeddables.DomicilioDTO;
import com.appResP.residuosPatologicos.models.embeddables.Domicilio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomicilioMapper {


    public DomicilioDTO toDto(Domicilio d){
        if(d== null)
            return null;
        return DomicilioDTO.builder()
                .barrio(d.getBarrio())
                .calle(d.getCalle())
                .altura(d.getAltura())
                .departamento(d.getDepartamento())
                .codigoPostal(d.getCodigoPostal())
                .localidad(d.getLocalidad())
                .provincia(d.getProvincia())
                .build();
    }

    public  Domicilio toEntity(DomicilioDTO dto){
        if(dto==null) return  null;

        return Domicilio.builder()
                .barrio(dto.getBarrio())
                .calle(dto.getCalle())
                .altura(dto.getAltura())
                .departamento(dto.getDepartamento())
                .codigoPostal(dto.getCodigoPostal())
                .provincia(dto.getProvincia())
                .localidad(dto.getLocalidad())
                .build();
    }

    /**
     * Actualiza un Domicilio existente (entidad administrada por JPA)
     */
    public void updateEntity(Domicilio entity, DomicilioDTO dto) {
        if (entity == null || dto == null) return;

        entity.setBarrio(dto.getBarrio());
        entity.setCalle(dto.getCalle());
        entity.setAltura(dto.getAltura());
        entity.setDepartamento(dto.getDepartamento());
        entity.setCodigoPostal(dto.getCodigoPostal());
        entity.setLocalidad(dto.getLocalidad());
        entity.setProvincia(dto.getProvincia());
    }
}
