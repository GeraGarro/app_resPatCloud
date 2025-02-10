package com.appResP.residuosPatologicos.persistence.repositories;

import com.appResP.residuosPatologicos.models.Tipo_residuo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITipoResiduo_Repository extends CrudRepository<Tipo_residuo,Long> {
}
