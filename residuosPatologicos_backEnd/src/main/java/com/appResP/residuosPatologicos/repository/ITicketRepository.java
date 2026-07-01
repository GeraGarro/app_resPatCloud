package com.appResP.residuosPatologicos.repository;


import com.appResP.residuosPatologicos.models.HojaRuta;
import com.appResP.residuosPatologicos.models.TicketControl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ITicketRepository extends JpaRepository<TicketControl,Long> {

    // LISTADO LIVIANO — sin residuos
    @EntityGraph(attributePaths = {"transportista", "hojaRuta", "generador"})
    Page<TicketControl> findAll(Pageable pageable);


    // DETALLE — con residuos y tipoResiduo
    @EntityGraph(attributePaths = {
            "transportista", "hojaRuta", "generador",
            "listaResiduos", "listaResiduos.tipoResiduo"
    })
    Optional<TicketControl> findById(Long id);

    // POR HOJA DE RUTA — listado liviano paginado
    @EntityGraph(attributePaths = {
            "transportista", "hojaRuta", "generador",
            "listaResiduos", "listaResiduos.tipoResiduo"
    })
    Page<TicketControl> findByHojaRuta_Id(Long hojaRutaId, Pageable pageable);

    // POR PERIODO — con residuos para cálculos de peso
    @EntityGraph(attributePaths = {
            "transportista", "hojaRuta", "generador",
            "listaResiduos", "listaResiduos.tipoResiduo"
    })
    @Query("""
    SELECT t FROM TicketControl t
     WHERE YEAR(t.fechaEmision) = :anio
     AND MONTH(t.fechaEmision) = :mes
     AND t.transportista.idTransportista = :idTransportista
    """)
    List<TicketControl> findTicketsByPeriodo(
            @Param("anio") int anio,
            @Param("mes") int mes,
            @Param("idTransportista") Long idTransportista
    );

    @EntityGraph(attributePaths = {
            "transportista", "hojaRuta", "generador",
            "listaResiduos", "listaResiduos.tipoResiduo"
    })
    List<TicketControl> findByTransportistaIdTransportistaAndFechaEmisionBetween(
            Long transportistaId,
            LocalDate desde,
            LocalDate hasta
    );

    @EntityGraph(attributePaths = {"transportista", "hojaRuta", "generador"})
    List<TicketControl> findByEstadoFalseAndHojaRuta_FechaFinBefore(LocalDate fecha);

    @Query("""
        select coalesce(max(t.numeroTicket), 0)
        from TicketControl t
        where t.transportista.idTransportista = :transportistaId
    """)
    Long findMaxNumeroTicketByTransportista(@Param("transportistaId") Long transportistaId);


}
