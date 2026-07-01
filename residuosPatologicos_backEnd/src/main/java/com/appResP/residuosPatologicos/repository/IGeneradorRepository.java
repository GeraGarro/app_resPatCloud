package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.Generador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGeneradorRepository extends JpaRepository<Generador,Long> {

    @Override
    @EntityGraph(attributePaths = {"telefonos"})
    Optional<Generador> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"telefonos"})
    Page<Generador> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"telefonos"})
    Page<Generador> findByEstado(boolean estado, Pageable pageable);

    @EntityGraph(attributePaths = {"telefonos"})
    Page<Generador> findByEstadoAndTransportista_IdTransportista(boolean estado, Long idTransportista, Pageable pageable);

    @EntityGraph(attributePaths = {"telefonos"})
    Page<Generador> findByTransportista_IdTransportista(Long idTransportista, Pageable pageable);

    // Útil para validar email único entre todos los generadores (autónomo y empresa)
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
