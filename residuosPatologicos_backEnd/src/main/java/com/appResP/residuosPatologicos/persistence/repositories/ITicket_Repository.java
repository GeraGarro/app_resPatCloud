package com.appResP.residuosPatologicos.persistence.repositories;


import com.appResP.residuosPatologicos.models.Hoja_ruta;
import com.appResP.residuosPatologicos.models.Ticket_control;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository

public interface ITicket_Repository extends CrudRepository<Ticket_control,Long> {
    List<Ticket_control> findByHojaRuta(Hoja_ruta hojaRuta);
    @Query("SELECT t FROM Ticket_control t WHERE YEAR(t.fechaEmision) = ?1 AND MONTH(t.fechaEmision) = ?2 AND t.transportista.id = ?3")
    List<Ticket_control> findTicketsByPeriodo(int anio, int mes, Long idTransportista);

    @Query ("SELECT t FROM Ticket_control t WHERE t.hojaRuta.id = :hojaRutaId")
    List <Ticket_control> findByHojaRutaId(@Param( "hojaRutaId" )Long hojaRutaId);

    List <Ticket_control> findByCertificadoId(Long certificadoId);
}


