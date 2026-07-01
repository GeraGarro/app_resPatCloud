package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.response.TipoResiduoDTO;
import com.appResP.residuosPatologicos.DTO.request.TipoResiduoRequestDTO;
import com.appResP.residuosPatologicos.models.TipoResiduo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoResiduoMapper {

   public  TipoResiduoDTO toDTO(TipoResiduo e){
       if(e== null) return null;
       return TipoResiduoDTO.builder()
               .id(e.getId())
               .codigo(e.getCodigo())
               .nombre(e.getNombre())
               .estadoActividad(e.isEstadoActividad())
               .transportistaId(e.getTransportista() != null ? e.getTransportista().getIdTransportista() : null)
               .build();
   }

   public   TipoResiduo toEntity(TipoResiduoRequestDTO dto){
       if(dto== null) return null;
       return TipoResiduo.builder()
               .codigo(dto.getCodigo())
               .nombre(dto.getNombre())
               .estadoActividad(dto.isEstadoActividad())
               .build();
   }

    public    void updateEntity(TipoResiduo e, TipoResiduoRequestDTO dto) {
        e.setCodigo(dto.getCodigo());
        e.setNombre(dto.getNombre());
        e.setEstadoActividad(dto.isEstadoActividad());
    }
}
