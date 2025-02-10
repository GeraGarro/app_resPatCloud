package com.appResP.residuosPatologicos.persistence.implementacion;

import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.persistence.IResiduoDAO;
import com.appResP.residuosPatologicos.persistence.repositories.IResiduo_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class ResiduoDAOImp implements IResiduoDAO {

    @Autowired
    IResiduo_Repository residuo_Respositorio;
    @Override
    public Optional<Residuo> findByID(Long id) {
        return residuo_Respositorio.findById(id);
    }

    @Override
    public List<Residuo> findAll() {
        return (List<Residuo>) residuo_Respositorio.findAll();
    }

    @Override
    public void save(Residuo residuo) {
        residuo_Respositorio.save(residuo);
    }

    @Override
    public void deletebyID(Long id) {
        residuo_Respositorio.deleteById(id);
    }
}
