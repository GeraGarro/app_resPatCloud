package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.response.AdminUsuarioDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.Usuario;
import com.appResP.residuosPatologicos.models.enums.EstadoCuenta;
import com.appResP.residuosPatologicos.models.enums.Rol;
import com.appResP.residuosPatologicos.repository.ITransportistaRepository;
import com.appResP.residuosPatologicos.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final ITransportistaRepository transportistaRepository;

    @Transactional(readOnly = true)
    public List<AdminUsuarioDTO> findTransportistasPendientes() {
        return usuarioRepository
                .findByRolAndEstadoCuentaOrderByIdDesc(Rol.TRANSPORTISTA, EstadoCuenta.PENDIENTE_APROBACION)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public AdminUsuarioDTO aprobarTransportista(Long usuarioId) {
        Usuario usuario = findTransportistaUser(usuarioId);
        usuario.setEstadoCuenta(EstadoCuenta.APROBADO);
        Usuario saved = usuarioRepository.save(usuario);
        setTransportistaEstado(saved, true);
        return toDTO(saved);
    }

    @Transactional
    public AdminUsuarioDTO rechazarTransportista(Long usuarioId) {
        Usuario usuario = findTransportistaUser(usuarioId);
        usuario.setEstadoCuenta(EstadoCuenta.RECHAZADO);
        Usuario saved = usuarioRepository.save(usuario);
        setTransportistaEstado(saved, false);
        return toDTO(saved);
    }

    @Transactional
    public AdminUsuarioDTO suspenderTransportista(Long usuarioId) {
        Usuario usuario = findTransportistaUser(usuarioId);
        usuario.setEstadoCuenta(EstadoCuenta.SUSPENDIDO);
        Usuario saved = usuarioRepository.save(usuario);
        setTransportistaEstado(saved, false);
        return toDTO(saved);
    }

    private Usuario findTransportistaUser(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        if (usuario.getRol() != Rol.TRANSPORTISTA) {
            throw new IllegalArgumentException("Solo se pueden administrar aprobaciones de transportistas.");
        }

        return usuario;
    }

    private void setTransportistaEstado(Usuario usuario, boolean activo) {
        transportistaRepository.findByUsuario_Id(usuario.getId())
                .or(() -> transportistaRepository.findByEmail(usuario.getEmail()))
                .ifPresent(transportista -> {
                    transportista.setEstado(activo);
                    transportistaRepository.save(transportista);
                });
    }

    private AdminUsuarioDTO toDTO(Usuario usuario) {
        Optional<Transportista> transportista = transportistaRepository.findByUsuario_Id(usuario.getId())
                .or(() -> transportistaRepository.findByEmail(usuario.getEmail()));

        return AdminUsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .estadoCuenta(usuario.getEstadoCuenta())
                .emailVerificado(usuario.getEmailVerificado())
                .fechaRegistro(usuario.getFechaRegistro())
                .transportistaId(transportista.map(Transportista::getIdTransportista).orElse(null))
                .nombre(transportista.map(Transportista::getNombre).orElse(null))
                .apellido(transportista.map(Transportista::getApellido).orElse(null))
                .nombreFantasia(transportista.map(Transportista::getNombreFantasia).orElse(null))
                .build();
    }
}
