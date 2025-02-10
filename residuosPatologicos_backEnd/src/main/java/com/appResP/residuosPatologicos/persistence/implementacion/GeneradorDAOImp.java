package com.appResP.residuosPatologicos.persistence.implementacion;

import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.persistence.IGeneradorDAO;
import com.appResP.residuosPatologicos.persistence.repositories.IGenerador_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class GeneradorDAOImp implements IGeneradorDAO {
    @Autowired
    IGenerador_Repository generador_repositorio;
    @Override
    public Optional<Generador> findByID(Long id) {
        return generador_repositorio.findById(id);
    }

    @Override
    public List<Generador> findAll() {
        return (List<Generador>)generador_repositorio.findAll();
    }

    @Override
    public void save(Generador generador) {
        generador_repositorio.save(generador);
    }

    @Override
    public void deletebyID(Long id) {
        generador_repositorio.deleteById(id);
    }

    @Override
    public List<Generador> obtenerGeneradoresActivos() {
        return generador_repositorio.findByEstadoTrue();
    }

    @Override
    public List<Generador> obtenerGeneradoresInactivos() {
        return generador_repositorio.findByEstadoFalse();
    }
}
