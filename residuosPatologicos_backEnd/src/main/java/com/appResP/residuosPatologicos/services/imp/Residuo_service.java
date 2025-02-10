package com.appResP.residuosPatologicos.services.imp;

import com.appResP.residuosPatologicos.DTO.ResiduoDTO;
import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.persistence.IResiduoDAO;
import com.appResP.residuosPatologicos.services.IResiduo_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class Residuo_service implements IResiduo_service {
    @Autowired
    IResiduoDAO residuoDAO;
    @Override
    public Optional<Residuo> findByID(Long id) {
        return residuoDAO.findByID(id);
    }

    @Override
    public List<Residuo> findAll() {
        return residuoDAO.findAll();
    }

    @Override
    public void save(Residuo residuo) {
        residuoDAO.save(residuo);
    }

    @Override
    public void deletebyID(Long id) {
        residuoDAO.deletebyID(id);
    }

    @Override
    public void validateAndSave(ResiduoDTO dto) {
        if(dto.getPeso()<0){
            dto.setPeso(0);
        }

    }
}
