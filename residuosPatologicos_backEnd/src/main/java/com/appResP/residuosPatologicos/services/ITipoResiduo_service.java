package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.models.Tipo_residuo;

import java.util.List;
import java.util.Optional;

public interface ITipoResiduo_service {

    Optional<Tipo_residuo> findByID(Long Id);
    List<Tipo_residuo> findAll();

    void save(Tipo_residuo tipoResiduo);
    void deletebyID(Long id);
}
