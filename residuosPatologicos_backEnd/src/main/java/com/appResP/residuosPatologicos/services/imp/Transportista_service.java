package com.appResP.residuosPatologicos.services.imp;

import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.persistence.ITransportistaDAO;
import com.appResP.residuosPatologicos.services.ITransporista_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Transportista_service implements ITransporista_service {

@Autowired
ITransportistaDAO transportistaDAO;
    @Override
    public Optional<Transportista> findByID(Long id) {
        return transportistaDAO.findByID(id);
    }

    @Override
    public List<Transportista> findAll() {
        return transportistaDAO.findAll();
    }

    @Override
    public void save(Transportista transportista) {
transportistaDAO.save(transportista);
    }

    @Override
    public void deletebyID(Long id) {
    transportistaDAO.deletebyID(id);
    }
}
