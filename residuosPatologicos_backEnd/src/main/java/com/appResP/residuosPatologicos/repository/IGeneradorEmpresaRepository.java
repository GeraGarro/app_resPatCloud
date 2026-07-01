package com.appResP.residuosPatologicos.repository;

import com.appResP.residuosPatologicos.models.GeneradorEmpresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IGeneradorEmpresaRepository extends JpaRepository< GeneradorEmpresa, Long> {
    // Para listados filtrados
    Page<GeneradorEmpresa> findByEstado(boolean estado, Pageable pageable);

    Page<GeneradorEmpresa> findByTransportista_IdTransportista(Long idTransportista, Pageable pageable);

    boolean existsByCuit(String cuit);

    boolean existsByCuitAndIdNot(String cuit, Long id);

    // Búsqueda por razón social útil para el frontend
    Page<GeneradorEmpresa> findByRazonSocialContainingIgnoreCase(
            String razonSocial, Pageable pageable);
}
