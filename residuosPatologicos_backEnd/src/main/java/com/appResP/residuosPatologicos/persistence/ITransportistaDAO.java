package com.appResP.residuosPatologicos.persistence;

import com.appResP.residuosPatologicos.models.Transportista;

import java.util.List;
import java.util.Optional;

public interface ITransportistaDAO {

    Optional<Transportista> findByID(Long Id);
    List <Transportista> findAll();

    void save(Transportista transportista);
    void deletebyID(Long id);
}
