package com.appResP.residuosPatologicos.persistence;

import com.appResP.residuosPatologicos.models.Ticket_control;


import java.util.List;
import java.util.Optional;

public interface ITicketControlDAO {
    Optional<Ticket_control> findByID(Long Id);
    List<Ticket_control> findAll();

    void save(Ticket_control ticketControl);
    void deletebyID(Long id);

   List<Ticket_control> findTicketsByPeriodo(int anio,int mes, Long id_transportista);

   List <Ticket_control> getTicketsByHojaRutaId(Long hojaRutaId);

   List <Ticket_control> getTicketsByCertificado(Long certificadoId);
    void actualizarEstado(Long id, boolean nuevoEstado);
}
