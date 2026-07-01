package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.request.TicketRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.TicketDTO;
import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.models.HojaRuta;
import com.appResP.residuosPatologicos.models.TicketControl;
import com.appResP.residuosPatologicos.models.Transportista;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final ResiduoMapper residuoMapper;

public TicketDTO toDTO(TicketControl t){
    if( t== null) return null;

    return TicketDTO.builder()
            .idTicket(t.getId())
            .numeroTicket(t.getNumeroTicket())
            .transportistaId(t.getTransportista() !=null? t.getTransportista().getIdTransportista() : null)
            .transportistaNombre(t.getTransportista().getNombre()+" "+t.getTransportista().getApellido())
            .hojaRutaId(t.getHojaRuta() != null ? t.getHojaRuta().getId() : null)
            .generadorId(t.getGenerador() != null ? t.getGenerador().getId() : null)
            .fechaEmision(t.getFechaEmision())
            .horario(t.getHorario())

            .estado(t.isEstado())
            .pesoTotal(t.getPesoTotal())
            .residuos(Hibernate.isInitialized(t.getListaResiduos())
                    ? t.getListaResiduos().stream().map(residuoMapper::toDTO).toList()
                    : List.of())
            .build();

}

public TicketControl toEntity(TicketRequestDTO dto,Transportista t,HojaRuta hr,Generador g){

    return TicketControl.builder()
        .transportista(t)
        .hojaRuta(hr)
        .generador(g)
        .fechaEmision(dto.getFechaEmision()!= null ? dto.getFechaEmision(): LocalDate.now())
        .horario(dto.getHorario() != null ? dto.getHorario() : LocalTime.now())
        .estado(false)
        .build();

}

    public TicketDTO toListDTO(TicketControl t) {
        if (t == null) return null;

        return TicketDTO.builder()
                .idTicket(t.getId())
                .numeroTicket(t.getNumeroTicket())
                .transportistaId(t.getTransportista() != null ? t.getTransportista().getIdTransportista() : null)
                .transportistaNombre(t.getTransportista().getNombre()+" "+t.getTransportista().getApellido())
                .hojaRutaId(t.getHojaRuta() != null ? t.getHojaRuta().getId() : null)
                .generadorId(t.getGenerador() != null ? t.getGenerador().getId() : null)
                .fechaEmision(t.getFechaEmision())
                .horario(t.getHorario())
                .estado(t.isEstado())
                .pesoTotal(t.getPesoTotal())
                .residuos(Hibernate.isInitialized(t.getListaResiduos())
                        ? t.getListaResiduos().stream().map(residuoMapper::toDTO).toList()
                        : List.of())
                .build();
    }
}
