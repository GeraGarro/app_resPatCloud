package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.request.TransportistaRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.TransportistaDTO;
import com.appResP.residuosPatologicos.DTO.response.TransportistaProfileStatusDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.DomicilioMapper;
import com.appResP.residuosPatologicos.mappers.TransportistaMapper;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.Usuario;
import com.appResP.residuosPatologicos.models.enums.Rol;
import com.appResP.residuosPatologicos.repository.ITransportistaRepository;
import com.appResP.residuosPatologicos.repository.IUsuarioRepository;
import com.appResP.residuosPatologicos.services.IHojaRutaService;
import com.appResP.residuosPatologicos.services.ITransportistaService;
import com.appResP.residuosPatologicos.services.TransportistaProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransportistaServiceImpl implements ITransportistaService {

    private final ITransportistaRepository ITransportistaRepository;

    private final TransportistaMapper transportistaMapper;

    private final IUsuarioRepository usuarioRepository;

    private final DomicilioMapper domicilioMapper;

    private final TransportistaProfileService transportistaProfileService;

    private final IHojaRutaService hojaRutaService;


    @Override
    public TransportistaDTO findByID(Long id) {
        return ITransportistaRepository.findById(id)
                .map(transportistaMapper::toDTO)
                .orElseThrow(()-> new RuntimeException("Transportista no encontrado con ID: "+ id));
    }


    @Override
    public List<TransportistaDTO> findAll() {
        return ITransportistaRepository.findAll()
                .stream()
                .map(transportistaMapper::toDTO)
                .toList();
    }

    @Override
    public TransportistaDTO save(TransportistaRequestDTO transportistaDto) {
        validarDocumento(transportistaDto);

        Usuario usuario = usuarioRepository.findByEmail(transportistaDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + transportistaDto.getEmail()));

        if (usuario.getRol() != Rol.TRANSPORTISTA) {
            throw new IllegalArgumentException("El usuario no tiene rol TRANSPORTISTA");
        }

        var existente = ITransportistaRepository.findByUsuario_Email(usuario.getEmail())
                .or(() -> ITransportistaRepository.findByEmail(usuario.getEmail()));

        if (existente.isPresent()) {
            hojaRutaService.verificarYCrearHojaRutaSiEsNecesario(existente.get());
            return transportistaMapper.toDTO(existente.get());
        }

        Transportista entityTransportista =transportistaMapper.toEntityTransportista(transportistaDto);
        entityTransportista.setUsuario(usuario);
        Transportista saved = ITransportistaRepository.save(entityTransportista);
        hojaRutaService.verificarYCrearHojaRutaSiEsNecesario(saved);
        return transportistaMapper.toDTO (saved);

    }

    @Override
    public void deletebyId(Long id) {
        if(!ITransportistaRepository.existsById(id)){
            throw new RuntimeException(("No existe Transportista con ID:" + id));
        }
        ITransportistaRepository.deleteById(id);
    }

    @Override
    public TransportistaDTO update(Long id, TransportistaRequestDTO dto) {
        Transportista existing= ITransportistaRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Transportista no encontrado con id "+ id));
        validarDocumento(dto);

        existing.setNombre(dto.getNombre());
        existing.setApellido(dto.getApellido());
        existing.setNombreFantasia(dto.getNombreFantasia());
        existing.setCuit(null);
        existing.setCuil(dto.getCuil());
        existing.setTelefono(dto.getTelefono());
        existing.setEmail(dto.getEmail());
        existing.setEstado(dto.isEstado());
        existing.setDomicilio(domicilioMapper.toEntity(dto.getDomicilio()));

        return transportistaMapper.toDTO( ITransportistaRepository.save(existing));
    }

    private void validarDocumento(TransportistaRequestDTO dto) {
        boolean sinCuil = dto.getCuil() == null || dto.getCuil().isBlank();

        if (sinCuil) {
            throw new IllegalArgumentException("Debe registrar el CUIL del transportista.");
        }

        if (!dto.getCuil().matches("\\d{11}")) {
            throw new IllegalArgumentException("El CUIL del transportista debe tener exactamente 11 digitos.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TransportistaDTO findAuthenticated() {
        return transportistaMapper.toDTO(transportistaProfileService.getAuthenticatedTransportista());
    }

    @Override
    @Transactional(readOnly = true)
    public TransportistaProfileStatusDTO getAuthenticatedProfileStatus() {
        return transportistaProfileService.getStatus();
    }

}
