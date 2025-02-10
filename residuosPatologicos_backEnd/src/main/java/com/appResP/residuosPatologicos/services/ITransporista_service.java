package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.models.Transportista;

import java.util.List;
import java.util.Optional;

public interface ITransporista_service
{
    Optional<Transportista> findByID(Long Id);
    List<Transportista> findAll();

    void save(Transportista transportista);
    void deletebyID(Long id);
}
