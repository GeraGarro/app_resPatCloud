package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.HojaRuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IHojaRutaRepository extends JpaRepository<HojaRuta, Long> {

    Optional<HojaRuta> findTopByOrderByFechaFinDesc();

    Optional<HojaRuta> findTopByTransportista_IdTransportistaOrderByFechaFinDesc(Long transportistaId);

    boolean existsByFechaInicio(LocalDate fechaInicio);

    boolean existsByTransportista_IdTransportistaAndFechaInicio(Long transportistaId, LocalDate fechaInicio);

    Optional<HojaRuta> findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            LocalDate fecha,
            LocalDate fecha2
    );

    Optional<HojaRuta> findByTransportista_IdTransportistaAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long transportistaId,
            LocalDate fecha,
            LocalDate fecha2
    );

    Page<HojaRuta> findByTransportista_IdTransportista(Long transportistaId, Pageable pageable);

    List<HojaRuta> findByTransportista_IdTransportistaOrderByFechaInicioDesc(Long transportistaId);

    @Query("""
        select coalesce(max(h.numeroHojaRuta), 0)
        from HojaRuta h
        where h.transportista.idTransportista = :transportistaId
    """)
    Long findMaxNumeroHojaRutaByTransportista(@Param("transportistaId") Long transportistaId);

    boolean existsByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            LocalDate fechaFinNueva,
            LocalDate fechaInicioNueva
    );

    @Query("""
        select distinct h from HojaRuta h
        join h.listaTickets t
        where t.transportista.idTransportista = :transportistaId
          and t.fechaEmision between :desde and :hasta
        order by h.fechaInicio asc
    """)
    List<HojaRuta> findDistinctByTransportistaAndFechaEmisionBetween(
            @Param("transportistaId") Long transportistaId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
        select count(distinct h.id) from HojaRuta h
        join h.listaTickets t
        where t.transportista.idTransportista = :transportistaId
          and t.fechaEmision between :desde and :hasta
    """)
    int countDistinctByTransportistaAndFechaEmisionBetween(
            @Param("transportistaId") Long transportistaId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );
}
