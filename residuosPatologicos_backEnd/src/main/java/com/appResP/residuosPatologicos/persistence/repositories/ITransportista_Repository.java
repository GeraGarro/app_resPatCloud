package com.appResP.residuosPatologicos.persistence.repositories;

import com.appResP.residuosPatologicos.models.Transportista;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransportista_Repository extends CrudRepository<Transportista,Long> {
}
