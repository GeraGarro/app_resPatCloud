package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.response.TelefonoDTO;
import com.appResP.residuosPatologicos.DTO.request.TelefonoRequestDTO;
import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.models.Telefono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelefonoMapper {



    /* ============================
       ENTITY → DTO
       ============================ */
    public  TelefonoDTO toDTO(Telefono t) {
        if (t == null) return null;

        return TelefonoDTO.builder()
                .id(t.getId())
                .numero(t.getNumero())
                .tipo(t.getTipo())
                .estado(t.isEstado())
                .build();
    }

    public  Telefono toEntity(TelefonoRequestDTO dto, Generador generador) {
        if (dto == null) return null;

        return Telefono.builder()
                .numero(dto.getNumero())
                .tipo(dto.getTipo())
                .estado(dto.isEstado())
                .generador(generador) // relación padre
                .build();
    }
    /* ============================
    UPDATE ENTITY
    ============================ */
    public static void updateEntity(Telefono telefono, TelefonoRequestDTO dto) {
        telefono.setNumero(dto.getNumero());
        telefono.setTipo(dto.getTipo());
        telefono.setEstado(dto.isEstado());
    }
}
