package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.response.GeneradorDTO;
import com.appResP.residuosPatologicos.DTO.request.GeneradorRequestDTO;
import com.appResP.residuosPatologicos.models.enums.TipoGenerador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGeneradorService {

    Page<GeneradorDTO> findAllByTypo(Pageable pageable, TipoGenerador tipo);
    GeneradorDTO findById(Long id);
//    List<GeneradorDTO> findAll();
    GeneradorDTO crear(GeneradorRequestDTO dto);
    GeneradorDTO update(Long id, GeneradorRequestDTO dto);
    void delete(Long id);
    Page<GeneradorDTO> findByEstado(boolean estado, Pageable pageable);
    Page<GeneradorDTO> findAll(Pageable pageable);
     List<GeneradorDTO> findAllList();
    GeneradorDTO cambiarEstado(Long id);
}
