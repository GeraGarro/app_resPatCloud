package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.request.HojaRutaRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaDetalleDTO;
import com.appResP.residuosPatologicos.models.HojaRuta;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HojaRutaMapper {

    private final TicketMapper ticketMapper;


    public HojaRutaDTO toDTO(HojaRuta h) {
        if (h == null) return null;

        return HojaRutaDTO.builder()
                .id(h.getId())
                .numeroHojaRuta(h.getNumeroHojaRuta())
                .fechaInicio(h.getFechaInicio())
                .fechaFin(h.getFechaFin())
                .transportistaId(h.getTransportista() != null ? h.getTransportista().getIdTransportista() : null)
                .cantidadTickets(
                        Hibernate.isInitialized(h.getListaTickets())
                        ? h.getListaTickets().size()
                                :0
                )
                .build();
    }

    //para detalle
    public HojaRutaDetalleDTO toDetalleDTO (HojaRuta h)
    {if( h==null) return  null;

        return HojaRutaDetalleDTO.builder()
                .id(h.getId())
                .numeroHojaRuta(h.getNumeroHojaRuta())
                .fechaInicio(h.getFechaInicio())
                .fechaFin(h.getFechaFin())
                .transportistaId(h.getTransportista() != null ? h.getTransportista().getIdTransportista() : null)
                .tickets(
                        Hibernate.isInitialized(h.getListaTickets())
                        ? h.getListaTickets().stream()
                                .map(ticketMapper::toDTO)
                                .toList()
                        :List.of()
                        )
                        .build();

    }
}
