package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.request.ResiduoRequestDTO;
import com.appResP.residuosPatologicos.DTO.request.ResiduoUpdateDTO;
import com.appResP.residuosPatologicos.DTO.response.ResiduoDTO;

;import java.util.List;
import java.math.BigDecimal;

public interface IResiduoService {

    ResiduoDTO findById(Long id);


    ResiduoDTO actualizar(Long id, ResiduoUpdateDTO dto);

    List<ResiduoDTO> buscarPorTipo(Long tipoResiduoId);


}
