package com.appResP.residuosPatologicos.persistence.implementacion;

import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.enums.Meses;
import com.appResP.residuosPatologicos.persistence.ICertificadoDAO;
import com.appResP.residuosPatologicos.persistence.repositories.ICertificado_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class CertificadoDAOImp implements ICertificadoDAO {
    @Autowired
    ICertificado_Repository certificado_repositorio;
    @Override
    public Optional<Certificado> findByID(Long id) {
        return certificado_repositorio.findById(id);
    }

    @Override
    public List<Certificado> findAll() {
        return (List<Certificado>) certificado_repositorio.findAll();
    }

    @Override
    public void save(Certificado certificado) {
        certificado_repositorio.save(certificado);
    }

    @Override
    public void deletebyID(Long id) {
        certificado_repositorio.deleteById(id);
    }

    @Override
    public boolean existsByTransportistaAndMesAndAnio(Transportista transportista, Meses mes, int anio) {
        return certificado_repositorio.existsByTransportistaAndMesAndAño(transportista,mes,anio);
    }


}
