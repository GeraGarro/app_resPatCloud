package com.appResP.residuosPatologicos.persistence.implementacion;

import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.persistence.ITransportistaDAO;
import com.appResP.residuosPatologicos.persistence.repositories.ITransportista_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class TransportistaDAOImp implements ITransportistaDAO {
    @Autowired
    ITransportista_Repository transportistaRepositorio;
    @Override
    public Optional<Transportista> findByID(Long Id) {
       return transportistaRepositorio.findById(Id);
    }

    @Override
    public List<Transportista> findAll() {
        return (List<Transportista>) transportistaRepositorio.findAll();
    }

    @Override
    public void save(Transportista transportista) {
        transportistaRepositorio.save(transportista);
    }

    @Override
    public void deletebyID(Long id) {
        transportistaRepositorio.deleteById(id);
    }
}
