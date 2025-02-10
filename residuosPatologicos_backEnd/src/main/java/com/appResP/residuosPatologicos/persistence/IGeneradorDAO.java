package com.appResP.residuosPatologicos.persistence;

import com.appResP.residuosPatologicos.models.Generador;

import java.util.List;
import java.util.Optional;

public interface IGeneradorDAO {
    Optional<Generador> findByID(Long Id);
    List<Generador> findAll();

    void save(Generador generador);
    void deletebyID(Long id);

    List<Generador>  obtenerGeneradoresActivos();
    List<Generador>  obtenerGeneradoresInactivos();
}
