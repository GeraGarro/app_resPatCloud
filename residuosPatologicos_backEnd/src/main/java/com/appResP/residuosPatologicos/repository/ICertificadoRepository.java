package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.enums.Meses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICertificadoRepository extends JpaRepository<Certificado,Long> {

    boolean existsByTransportistaAndMesAndAnio(Transportista transportista, Meses mes, int anio);

    boolean existsByTransportistaAndMesAndAnioAndIdNot(
            Transportista transportista, Meses mes, int anio, Long id
    );

    Page<Certificado> findByMesAndAnio(Meses mes, int anio, Pageable pageable);

    // ✅ paginado por transportista
    Page<Certificado> findByTransportista_IdTransportista(Long idTransportista, Pageable pageable);

    List<Certificado> findByTransportista_IdTransportista(Long idTransportista);

    Page<Certificado> findByTransportista_IdTransportistaAndMesAndAnio(
            Long idTransportista, Meses mes, int anio, Pageable pageable
    );

    // Útil para buscar certificado por transportista y periodo sin cargar el objeto Transportista
    Optional<Certificado> findByTransportista_IdTransportistaAndMesAndAnio(
            Long idTransportista, Meses mes, int anio
    );

    @Query("""
        select coalesce(max(c.numeroCertificado), 0)
        from Certificado c
        where c.transportista.idTransportista = :transportistaId
    """)
    Long findMaxNumeroCertificadoByTransportista(@Param("transportistaId") Long transportistaId);
}
