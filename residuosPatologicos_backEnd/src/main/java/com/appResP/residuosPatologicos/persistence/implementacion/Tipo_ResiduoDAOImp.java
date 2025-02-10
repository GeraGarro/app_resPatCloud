package com.appResP.residuosPatologicos.persistence.implementacion;

import com.appResP.residuosPatologicos.models.Tipo_residuo;
import com.appResP.residuosPatologicos.persistence.ITipo_ResiduoDAO;
import com.appResP.residuosPatologicos.persistence.repositories.ITipoResiduo_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class Tipo_ResiduoDAOImp implements ITipo_ResiduoDAO {
    @Autowired
    ITipoResiduo_Repository tipoResiduo_Repositorio;
    @Override
    public Optional<Tipo_residuo> findByID(Long id) {
        return tipoResiduo_Repositorio.findById(id);
    }

    @Override
    public List<Tipo_residuo> findAll() {
        return (List<Tipo_residuo>) tipoResiduo_Repositorio.findAll();
    }

    @Override
    public void save(Tipo_residuo tipoResiduo) {
    tipoResiduo_Repositorio.save(tipoResiduo);
    }

    @Override
    public void deletebyID(Long id) {
    tipoResiduo_Repositorio.deleteById(id);
    }
}
