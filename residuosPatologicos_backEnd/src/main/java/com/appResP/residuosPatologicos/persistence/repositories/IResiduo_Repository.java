package com.appResP.residuosPatologicos.persistence.repositories;

import com.appResP.residuosPatologicos.models.Residuo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IResiduo_Repository extends CrudRepository<Residuo,Long> {

/*@Query("SELECT ticket_control, SUM(peso) AS total_pesos\n" +
        "FROM residuo\n" +
        "WHERE ticket_control = ?\n" +
        "GROUP BY ticket_control;")

    BigDecimal findPesoByTicket(Long id_Ticket);*/
}
