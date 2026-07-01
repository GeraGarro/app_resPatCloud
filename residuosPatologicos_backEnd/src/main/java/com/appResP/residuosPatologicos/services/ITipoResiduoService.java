package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.response.TipoResiduoDTO;
import com.appResP.residuosPatologicos.DTO.request.TipoResiduoRequestDTO;
import com.appResP.residuosPatologicos.models.TipoResiduo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITipoResiduoService {
    TipoResiduoDTO findById(Long id);
    Page<TipoResiduoDTO> findAll(Pageable pageable);
    TipoResiduo save(TipoResiduo tipoResiduo);
    void deleteById(Long id);
    TipoResiduoDTO update(Long id, TipoResiduoRequestDTO dto);
    TipoResiduoDTO cambiarEstado(Long id);
}
