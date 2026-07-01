package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IVehiculoRepository extends JpaRepository<Vehiculo, Long> {
    List<Vehiculo> findByTransportista_IdTransportista(Long idTransportista);
    boolean existsByTransportista_IdTransportista(Long idTransportista);
    boolean existsByDominio(String dominio);
    boolean existsByChasis(String chasis);
}
