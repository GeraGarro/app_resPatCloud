package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.models.Generador;

import java.util.List;
import java.util.Optional;

public interface IGenerador_service {
    Optional<Generador> findByID(Long id);
    List<Generador> findAll();

    void save(Generador generador);
    void deletebyID(Long id);

    List<Generador>  obtenerGeneradoresActivos();
    List<Generador>  obtenerGeneradoresInactivos();
}
