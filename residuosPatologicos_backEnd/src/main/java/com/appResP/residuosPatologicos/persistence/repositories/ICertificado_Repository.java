package com.appResP.residuosPatologicos.persistence.repositories;

import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.enums.Meses;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICertificado_Repository extends CrudRepository<Certificado,Long> {
/*
    Optional<Certificado> findByTransportistaIdAndAñoAndMesId(Long idTransportista, int año, int mesId);
*/

    boolean existsByTransportistaAndMesAndAño(Transportista transportista, Meses mes, int año);

}
