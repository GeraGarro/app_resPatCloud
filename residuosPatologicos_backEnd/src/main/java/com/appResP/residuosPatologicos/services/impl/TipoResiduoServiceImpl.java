package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.request.TipoResiduoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.TipoResiduoDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.DuplicateResourceException;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.TipoResiduoMapper;
import com.appResP.residuosPatologicos.models.TipoResiduo;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.repository.ITipoResiduoRepository;
import com.appResP.residuosPatologicos.services.ITipoResiduoService;
import com.appResP.residuosPatologicos.services.TransportistaProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TipoResiduoServiceImpl implements ITipoResiduoService {

    private final ITipoResiduoRepository repo;
    private final TipoResiduoMapper tipoResiduoMapper;
    private final TransportistaProfileService transportistaProfileService;

    @Override
    @Transactional(readOnly = true)
    public TipoResiduoDTO findById(Long id) {
        TipoResiduo tipoResiduo = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoResiduo no encontrado: " + id));
        validarAccesoTipoResiduo(tipoResiduo);
        return tipoResiduoMapper.toDTO(tipoResiduo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TipoResiduoDTO> findAll(Pageable pageable) {
        Optional<Transportista> transportista = authenticatedTransportistaIfPresent();

        if (transportista.isPresent()) {
            return repo.findByTransportista_IdTransportista(
                            transportista.get().getIdTransportista(),
                            pageable
                    )
                    .map(tipoResiduoMapper::toDTO);
        }

        return repo.findAll(pageable).map(tipoResiduoMapper::toDTO);
    }

    @Override
    public TipoResiduo save(TipoResiduo tipoResiduo) {
        Transportista transportista = transportistaProfileService.requireCompleteTransportistaProfile();
        String codigoNormalizado = normalizeCodigo(tipoResiduo.getCodigo());
        tipoResiduo.setCodigo(codigoNormalizado);
        tipoResiduo.setTransportista(transportista);

        if (tipoResiduo.getId() == null
                && repo.existsByTransportista_IdTransportistaAndCodigo(
                        transportista.getIdTransportista(),
                        codigoNormalizado
                )) {
            throw new DuplicateResourceException("Ya existe un Tipo Residuo con codigo: " + codigoNormalizado);
        }

        return repo.save(tipoResiduo);
    }

    @Override
    public void deleteById(Long id) {
        TipoResiduo tipoResiduo = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoResiduo no encontrado con id: " + id));
        validarAccesoTipoResiduo(tipoResiduo);
        repo.delete(tipoResiduo);
    }

    @Override
    public TipoResiduoDTO update(Long id, TipoResiduoRequestDTO dto) {
        TipoResiduo entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoResiduo no encontrado con id: " + id));
        Transportista transportista = transportistaProfileService.requireCompleteTransportistaProfile();
        validarAccesoTipoResiduo(entity, transportista);

        String codigoNormalizado = normalizeCodigo(dto.getCodigo());

        repo.findByTransportista_IdTransportistaAndCodigo(
                transportista.getIdTransportista(),
                codigoNormalizado
        ).ifPresent(otro -> {
            if (!otro.getId().equals(id)) {
                throw new DuplicateResourceException(
                        "El codigo " + codigoNormalizado + " ya pertenece a otro Tipo Residuo"
                );
            }
        });

        tipoResiduoMapper.updateEntity(entity, dto);
        entity.setCodigo(codigoNormalizado);
        entity.setTransportista(transportista);

        return tipoResiduoMapper.toDTO(repo.save(entity));
    }

    @Override
    public TipoResiduoDTO cambiarEstado(Long id) {
        TipoResiduo tipoResiduo = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoResiduo no encontrado: " + id));
        validarAccesoTipoResiduo(tipoResiduo);

        tipoResiduo.setEstadoActividad(!tipoResiduo.isEstadoActividad());

        return tipoResiduoMapper.toDTO(repo.save(tipoResiduo));
    }

    private String normalizeCodigo(String codigo) {
        return codigo != null ? codigo.trim().toUpperCase() : null;
    }

    private void validarAccesoTipoResiduo(TipoResiduo tipoResiduo) {
        Optional<Transportista> transportista = authenticatedTransportistaIfPresent();

        if (transportista.isEmpty()) {
            return;
        }

        validarAccesoTipoResiduo(tipoResiduo, transportista.get());
    }

    private void validarAccesoTipoResiduo(TipoResiduo tipoResiduo, Transportista transportista) {
        Long tipoTransportistaId = tipoResiduo != null && tipoResiduo.getTransportista() != null
                ? tipoResiduo.getTransportista().getIdTransportista()
                : null;
        Long transportistaId = transportista != null ? transportista.getIdTransportista() : null;

        if (tipoTransportistaId == null || !tipoTransportistaId.equals(transportistaId)) {
            throw new ResourceNotFoundException("TipoResiduo no encontrado: " + tipoResiduo.getId());
        }
    }

    private Optional<Transportista> authenticatedTransportistaIfPresent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        boolean esTransportista = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_TRANSPORTISTA".equals(authority.getAuthority()));

        if (!esTransportista) {
            return Optional.empty();
        }

        return Optional.of(transportistaProfileService.getAuthenticatedTransportista());
    }
}
