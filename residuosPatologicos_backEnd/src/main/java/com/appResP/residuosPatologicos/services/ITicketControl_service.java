package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.models.Ticket_control;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ITicketControl_service {

    Optional<Ticket_control> findByID(Long Id);
    List<Ticket_control> findAll();

    void save(Ticket_control ticketControl);
    void deletebyID(Long id);

    BigDecimal pesoResiduosByTicket(Long id);

    List<Ticket_control> findTicketsByPeriodo(int anio,int mes, Long id_transportista);

    String codificacionIdTicket(Long id);

    List<Ticket_control> ListaTicketsbyHoja (Long id);

    List <Ticket_control> ListaTicketsbyCertificado (long id);

    void actualizarEstado(Long id, boolean nuevoEstado);


}
