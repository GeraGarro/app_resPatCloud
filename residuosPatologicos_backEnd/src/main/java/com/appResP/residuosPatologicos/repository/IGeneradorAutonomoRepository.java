package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.GeneradorAutonomo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IGeneradorAutonomoRepository extends JpaRepository<GeneradorAutonomo,Long> {

    // Para listados filtrados
    Page<GeneradorAutonomo> findByEstado(boolean estado, Pageable pageable);

    Page<GeneradorAutonomo> findByTransportista_IdTransportista(Long idTransportista, Pageable pageable);

    List<GeneradorAutonomo> findByEstado(boolean estado);
    boolean existsByCuil(String cuil);

    boolean existsByCuilAndIdNot(String cuil, Long id);

    // Búsqueda por nombre/apellido útil para el frontend
    Page<GeneradorAutonomo> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido, Pageable pageable);


}
