package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.response.ResiduoDTO;
import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.models.TipoResiduo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ResiduoMapper {


    public  ResiduoDTO toDTO(Residuo r) {
        if (r == null) return null;

        return ResiduoDTO.builder()
                .id(r.getId())
                .tipoResiduoId(r.getTipoResiduo() != null ? r.getTipoResiduo().getId() : null)
                .peso(r.getPeso())
                .build();
    }
    /** Crea entity SIN setear TicketControl (eso lo hace TicketService con addResiduo). */
    public Residuo toEntity(TipoResiduo tipo, BigDecimal peso) {
        validarPeso(peso);

        return Residuo.builder()
                .tipoResiduo(tipo)
                .peso(peso)
                .build();
    }


    /** Actualiza solo el peso (validando). */
    public void updatePeso(Residuo entity, BigDecimal nuevoPeso) {
        validarPeso(nuevoPeso);
        entity.setPeso(nuevoPeso);
    }

    private void validarPeso(BigDecimal peso) {
        if (peso == null || peso.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0");
        }
    }


}


