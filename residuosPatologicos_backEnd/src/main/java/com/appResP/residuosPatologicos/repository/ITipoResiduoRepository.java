package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.TipoResiduo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITipoResiduoRepository extends JpaRepository<TipoResiduo,Long> {
    Optional<TipoResiduo> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    Optional<TipoResiduo> findByTransportista_IdTransportistaAndCodigo(Long transportistaId, String codigo);

    boolean existsByTransportista_IdTransportistaAndCodigo(Long transportistaId, String codigo);

    Page<TipoResiduo> findByTransportista_IdTransportista(Long transportistaId, Pageable pageable);
}
