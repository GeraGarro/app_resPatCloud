package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.Residuo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IResiduoRepository extends JpaRepository<Residuo,Long> {
    List<Residuo> findByTipoResiduo_Id(Long tipoResiduoId);

    List<Residuo> findByTicketControl_Id(Long ticketId);
}
