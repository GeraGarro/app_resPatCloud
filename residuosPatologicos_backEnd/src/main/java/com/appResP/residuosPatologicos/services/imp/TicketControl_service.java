package com.appResP.residuosPatologicos.services.imp;

import com.appResP.residuosPatologicos.exceptions.TicketNotFoundException;
import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.models.Ticket_control;
import com.appResP.residuosPatologicos.persistence.ITicketControlDAO;
import com.appResP.residuosPatologicos.services.ITicketControl_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
public class TicketControl_service implements ITicketControl_service {
    @Autowired
    ITicketControlDAO ticketControlDAO;

    @Override
    public Optional<Ticket_control> findByID(Long id) {
        return ticketControlDAO.findByID(id);
    }

    @Override
    public List<Ticket_control> findAll() {
        return ticketControlDAO.findAll();
    }

    @Override
    public void save(Ticket_control ticketControl) {
    ticketControlDAO.save(ticketControl);
    }

    @Override
    public void deletebyID(Long id) {
        ticketControlDAO.deletebyID(id);
    }

    @Override
    public BigDecimal pesoResiduosByTicket(Long id) {
        Optional<Ticket_control> ticketControlOptional = ticketControlDAO.findByID(id);

        if (ticketControlOptional.isPresent()) {
            Ticket_control ticketControl = ticketControlOptional.get();
            BigDecimal pesoTotal = BigDecimal.ZERO;

            for (Residuo residuo : ticketControl.getListaResiduos()) {
                BigDecimal pesoResiduo = BigDecimal.valueOf(residuo.getPeso());
                pesoTotal = pesoTotal.add(pesoResiduo); // Sumar el peso del residuo al total

            }
            return pesoTotal;
        } else {
            // Manejar el caso en que el ticket no se encuentra
            throw new TicketNotFoundException("Ticket con ID " + id + " no encontrado");
        }
    }

    @Override
    public List<Ticket_control> findTicketsByPeriodo(int anio, int mes, Long id_transportista) {
        return ticketControlDAO.findTicketsByPeriodo(anio,mes,id_transportista);
    }

    @Override
    public String codificacionIdTicket(Long id) {
        Optional<Ticket_control> ticketControlOptional=ticketControlDAO.findByID(id);
        String codigoId;
        if(ticketControlOptional.isPresent()){
            Ticket_control ticket=ticketControlOptional.get();

            codigoId="00000"+ticket.getId_Ticket();
            while(codigoId.length()>6){
                codigoId=codigoId.substring(1);
            }
            return "00"+ticket.getTransportista().getId_transportista()+"-"+codigoId ;
        }

          return null;

    }

    @Override
    public List<Ticket_control> ListaTicketsbyHoja(Long id) {
        return ticketControlDAO.getTicketsByHojaRutaId(id);
    }

    @Override
    public List<Ticket_control> ListaTicketsbyCertificado(long id) {
        return ticketControlDAO.getTicketsByCertificado(id);
    }

    @Override
    public void actualizarEstado(Long id, boolean nuevoEstado) {
        ticketControlDAO.actualizarEstado(id, nuevoEstado);
    }


}