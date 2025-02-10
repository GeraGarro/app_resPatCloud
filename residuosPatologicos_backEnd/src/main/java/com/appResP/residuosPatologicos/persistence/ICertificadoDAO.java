package com.appResP.residuosPatologicos.persistence;

import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.enums.Meses;

import java.util.List;
import java.util.Optional;

public interface ICertificadoDAO {
    Optional<Certificado> findByID(Long Id);
    List<Certificado> findAll();

    void save(Certificado certificado);
    void deletebyID(Long id);
    public boolean existsByTransportistaAndMesAndAnio (Transportista transportista, Meses mes, int anio);

}
