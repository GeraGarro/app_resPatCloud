package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.response.TransportistaDTO;
import com.appResP.residuosPatologicos.DTO.response.TransportistaProfileStatusDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.TransportistaMapper;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.repository.ITransportistaRepository;
import com.appResP.residuosPatologicos.repository.IVehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportistaProfileService {
    private final ITransportistaRepository transportistaRepository;
    private final IVehiculoRepository vehiculoRepository;
    private final TransportistaMapper transportistaMapper;

    @Transactional(readOnly = true)
    public Transportista getAuthenticatedTransportista() {
        String email = getAuthenticatedEmail();

        return transportistaRepository.findByUsuario_Email(email)
                .or(() -> transportistaRepository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un transportista asociado al usuario autenticado."));
    }

    @Transactional(readOnly = true)
    public Transportista requireCompleteTransportistaProfile() {
        Transportista transportista = getAuthenticatedTransportista();

        if (!vehiculoRepository.existsByTransportista_IdTransportista(transportista.getIdTransportista())) {
            throw new IllegalArgumentException(
                    "Debe registrar al menos un vehiculo antes de operar con generadores o tipos de residuo.");
        }

        return transportista;
    }

    @Transactional(readOnly = true)
    public TransportistaProfileStatusDTO getStatus() {
        String email = getAuthenticatedEmail();
        var transportista = transportistaRepository.findByUsuario_Email(email)
                .or(() -> transportistaRepository.findByEmail(email));
        List<String> pendientes = new ArrayList<>();

        if (transportista.isEmpty()) {
            pendientes.add("Registrar datos personales del transportista.");
            pendientes.add("Registrar al menos un vehiculo.");

            return TransportistaProfileStatusDTO.builder()
                    .perfilRegistrado(false)
                    .vehiculoRegistrado(false)
                    .completo(false)
                    .pendientes(pendientes)
                    .build();
        }

        boolean tieneVehiculo = vehiculoRepository
                .existsByTransportista_IdTransportista(transportista.get().getIdTransportista());

        if (!tieneVehiculo) {
            pendientes.add("Registrar al menos un vehiculo.");
        }

        TransportistaDTO dto = transportistaMapper.toDTO(transportista.get());
        return TransportistaProfileStatusDTO.builder()
                .perfilRegistrado(true)
                .vehiculoRegistrado(tieneVehiculo)
                .completo(tieneVehiculo)
                .transportista(dto)
                .pendientes(pendientes)
                .build();
    }

    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Debe iniciar sesion como transportista.");
        }

        return authentication.getName();
    }
}
