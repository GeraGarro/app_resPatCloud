package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.request.ResiduoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaDTO;
import com.appResP.residuosPatologicos.DTO.response.ResiduoDTO;
import com.appResP.residuosPatologicos.DTO.response.TicketDTO;
import com.appResP.residuosPatologicos.DTO.request.TicketRequestDTO;
import com.appResP.residuosPatologicos.models.Residuo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITicketService {
    TicketDTO findById(Long Id);

    Page<TicketDTO> findAll(Pageable pageable);

    TicketDTO create(TicketRequestDTO ticketRequestDTO);

    TicketDTO update(long id, TicketRequestDTO ticketRequestDTO);

    void deletebyId(Long id);

    List<TicketDTO> findTicketsByPeriodo(int anio, int mes, Long idTransportista);

    Page<TicketDTO> findByHojaRutaId(Long hojaRutaId, Pageable pageable);

    byte[] generarManifiestoPdf(Long id);

    TicketDTO actualizarEstado(Long id);

    int procesarTicketsDeHojasVencidas();

    Page<TicketDTO> findTicketsDeHojaActual(Pageable pageable);

    ResiduoDTO agregarResiduo(Long TicketId, ResiduoRequestDTO dto);

    void eliminarResiduo (Long ticketId, Long residuoId);

    List<ResiduoDTO> obtenerResiduos(Long ticketId);

}
