package com.appResP.residuosPatologicos.services.imp;

import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.persistence.IGeneradorDAO;
import com.appResP.residuosPatologicos.services.IGenerador_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class Generador_service implements IGenerador_service {
    @Autowired
    IGeneradorDAO generadorDAO;


    @Override
    public Optional<Generador> findByID(Long id) {
        return generadorDAO.findByID(id);
    }

    @Override
    public List<Generador> findAll() {
        return generadorDAO.findAll();
    }

    @Override
    public void save(Generador generador) {
    generadorDAO.save(generador);
    }

    @Override
    public void deletebyID(Long id) {
    generadorDAO.deletebyID(id);
    }

    @Override
    public List<Generador> obtenerGeneradoresActivos() {
        return generadorDAO.obtenerGeneradoresActivos();
    }

    @Override
    public List<Generador> obtenerGeneradoresInactivos() {
        return generadorDAO.obtenerGeneradoresInactivos();
    }
}
