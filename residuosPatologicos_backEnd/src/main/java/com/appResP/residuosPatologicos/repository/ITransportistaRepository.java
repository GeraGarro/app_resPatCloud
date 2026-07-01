package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ITransportistaRepository extends JpaRepository<Transportista,Long> {
    Optional<Transportista> findByEmail(String email);

    Optional<Transportista> findByUsuario_Email(String email);

    Optional<Transportista> findByUsuario_Id(Long usuarioId);

    List<Transportista> findByEstadoTrue();
}
