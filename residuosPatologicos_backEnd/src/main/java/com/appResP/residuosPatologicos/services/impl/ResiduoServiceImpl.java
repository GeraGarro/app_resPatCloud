package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.request.ResiduoRequestDTO;
import com.appResP.residuosPatologicos.DTO.request.ResiduoUpdateDTO;
import com.appResP.residuosPatologicos.DTO.response.ResiduoDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.ResiduoMapper;
import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.models.TipoResiduo;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.repository.IResiduoRepository;
import com.appResP.residuosPatologicos.repository.ITipoResiduoRepository;
import com.appResP.residuosPatologicos.services.IResiduoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ResiduoServiceImpl implements IResiduoService {
    private final IResiduoRepository residuoRepository;
    private final ITipoResiduoRepository tipoResiduoRepository;

    private final ResiduoMapper residuoMapper;

    @Override
    public ResiduoDTO findById(Long id) {
        Residuo residuo = residuoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Residuo no encontrado: " + id));
        return residuoMapper.toDTO(residuo);
    }

    @Override
    public ResiduoDTO actualizar(Long id, ResiduoUpdateDTO dto) {
        Residuo residuo = residuoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Residuo no encontrado: " + id));

        // Actualizar peso
        if (dto.getPeso() != null) {
            residuo.setPeso(dto.getPeso());
        }

        // Actualizar tipo (opcional, si lo permites)
        if (dto.getTipoResiduoId() != null && !residuo.getTipoResiduo().getId().equals(dto.getTipoResiduoId())) {
            TipoResiduo nuevoTipo = tipoResiduoRepository.findById(dto.getTipoResiduoId())
                    .orElseThrow(() -> new ResourceNotFoundException("TipoResiduo no encontrado: " + dto.getTipoResiduoId()));
            validarTipoResiduoPerteneceAlTransportista(
                    nuevoTipo,
                    residuo.getTicketControl() != null ? residuo.getTicketControl().getTransportista() : null
            );
            residuo.setTipoResiduo(nuevoTipo);
        }

        // El listener automáticamente recalculará el pesoTotal del ticket
        Residuo actualizado = residuoRepository.save(residuo);

        return residuoMapper.toDTO(actualizado);
    }


    @Override
    public List<ResiduoDTO> buscarPorTipo(Long tipoResiduoId) {
        return residuoRepository.findByTipoResiduo_Id(tipoResiduoId).stream()
                .map(residuoMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void validarTipoResiduoPerteneceAlTransportista(TipoResiduo tipoResiduo, Transportista transportista) {
        Long tipoTransportistaId = tipoResiduo != null && tipoResiduo.getTransportista() != null
                ? tipoResiduo.getTransportista().getIdTransportista()
                : null;
        Long transportistaId = transportista != null ? transportista.getIdTransportista() : null;

        if (tipoTransportistaId == null || !tipoTransportistaId.equals(transportistaId)) {
            throw new IllegalArgumentException("El tipo de residuo seleccionado no pertenece al transportista.");
        }
    }
}
