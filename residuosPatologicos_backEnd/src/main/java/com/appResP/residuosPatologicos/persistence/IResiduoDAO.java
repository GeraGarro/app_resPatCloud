package com.appResP.residuosPatologicos.persistence;

import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.models.Transportista;

import java.util.List;
import java.util.Optional;

public interface IResiduoDAO {
    Optional<Residuo> findByID(Long Id);
    List<Residuo> findAll();

    void save(Residuo residuo);
    void deletebyID(Long id);
}
