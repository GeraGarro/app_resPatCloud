package com.appResP.residuosPatologicos.mappers;

import com.appResP.residuosPatologicos.DTO.response.GeneradorAutonomoDTO;
import com.appResP.residuosPatologicos.DTO.response.GeneradorDTO;
import com.appResP.residuosPatologicos.DTO.request.GeneradorRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.GeneradorEmpresaDTO;
import com.appResP.residuosPatologicos.DTO.response.TelefonoDTO;
import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.models.GeneradorAutonomo;
import com.appResP.residuosPatologicos.models.GeneradorEmpresa;
import com.appResP.residuosPatologicos.models.Telefono;
import com.appResP.residuosPatologicos.models.enums.TipoGenerador;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@RequiredArgsConstructor

public class GeneradorMapper {

    private final DomicilioMapper domicilioMapper;
    private final TelefonoMapper telefonoMapper;
    /* ============================
       ENTITY → DTO
       ============================ */
    public GeneradorDTO toDTO(Generador g) {
        if (g == null) return null;

        //comunes

        // Solo cargamos teléfonos si la colección ya está inicializada — evita N+1
        List<TelefonoDTO> telefonos = Hibernate.isInitialized(g.getTelefonos())
                ? g.getTelefonos().stream().map(telefonoMapper::toDTO).toList()
                : List.of();

        var domicilio= domicilioMapper.toDto(g.getDomicilio());

        if(g instanceof  GeneradorEmpresa empresa) {
            return GeneradorEmpresaDTO.builder()
                    .id(empresa.getId())
                    .tipo(TipoGenerador.EMPRESA)
                    .estado(empresa.isEstado())
                    .legajo(g.getLegajo())
                    .domicilio(domicilio)
                    .telefonos(telefonos)
                    .email(g.getEmail())
                    .transportistaId(empresa.getTransportista() != null ? empresa.getTransportista().getIdTransportista() : null)
                    .usuarioId(empresa.getUsuario() != null ? empresa.getUsuario().getId() : null) // ✅
                    //especificos
                    .cuit(empresa.getCuit())
                    .razonSocial(empresa.getRazonSocial())
                    .nombreFantasia(empresa.getNombreFantasia())
                    .build();
        }
            if(g instanceof GeneradorAutonomo autonomo){
                return GeneradorAutonomoDTO.builder()
                        .id(autonomo.getId())
                        .tipo(TipoGenerador.AUTONOMO)
                        .estado(autonomo.isEstado())
                        .legajo(g.getLegajo())
                        .domicilio(domicilio)
                        .telefonos(telefonos)
                        .email(g.getEmail())
                        .transportistaId(autonomo.getTransportista() != null ? autonomo.getTransportista().getIdTransportista() : null)
                        .usuarioId(autonomo.getUsuario() != null ? autonomo.getUsuario().getId() : null) // ✅

                                // específicos
                        .nombre(autonomo.getNombre())
                        .apellido(autonomo.getApellido())
                        .cuil(autonomo.getCuil())
                        .build();
            }
            // por si aparece otro subtipo futuro
            throw new IllegalStateException("Tipo de Generador no soportado: " + g.getClass().getName());

        }



    /* ============================
       REQUEST DTO → ENTITY (CREATE)
       ============================ */

    public  Generador toEntity(GeneradorRequestDTO dto) {
        if (dto == null) return null;

        Generador generador = switch (dto.getTipo()) {

            case EMPRESA -> GeneradorEmpresa.builder()
                    .cuit(dto.getCuit())
                    .razonSocial(dto.getRazonSocial())
                    .nombreFantasia(dto.getNombreFantasia())
                    .build();

            case AUTONOMO -> GeneradorAutonomo.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .cuil(dto.getCuil())
                    .build();
        };

        if (dto.getTelefonos() != null) {
            List<Telefono> tels = dto.getTelefonos().stream()
                    .map(t -> telefonoMapper.toEntity(t, generador))
                    .toList();
            generador.setTelefonos(tels);
        }
        generador.setLegajo(dto.getLegajo());
        generador.setDomicilio(domicilioMapper.toEntity(dto.getDomicilio()));
        generador.setEstado(dto.isEstado());
        generador.setEmail(normalizeOptional(dto.getEmail()));
        return generador;
    }

/* ============================
       UPDATE ENTITY
       ============================ */

    public  void updateEntity(Generador generador, GeneradorRequestDTO dto){
        generador.setEstado(dto.isEstado());
        generador.setDomicilio(domicilioMapper.toEntity(dto.getDomicilio()));
        generador.setLegajo(dto.getLegajo());
        generador.setEmail(normalizeOptional(dto.getEmail()));
        if(generador instanceof GeneradorEmpresa empresa){
            empresa.setCuit(dto.getCuit());
            empresa.setRazonSocial(dto.getRazonSocial());
            empresa.setNombreFantasia(dto.getNombreFantasia());
        }
        else if (generador instanceof GeneradorAutonomo autonomo) {
            autonomo.setNombre(dto.getNombre());
            autonomo.setApellido(dto.getApellido());
            autonomo.setCuil(dto.getCuil());
        }

        // Teléfonos (versión simple): reemplaza toda la lista
        // (Con orphanRemoval=true en Generador, los viejos se eliminan)
        if (dto.getTelefonos() != null) {
            List<Telefono> nuevos = dto.getTelefonos().stream()
                    .map(t -> telefonoMapper.toEntity(t, generador))
                    .toList();

            if (generador.getTelefonos() == null) {
                generador.setTelefonos(new ArrayList<>());
            } else {
                generador.getTelefonos().clear();
            }
            generador.getTelefonos().addAll(nuevos);
        }

    }
    // toDTOPage eliminado — el service usa .map(this::toDTO) directamente
    public List<GeneradorDTO> toDTOList(List<Generador> lista) {
        if (lista == null) return List.of();
        return lista.stream().map(this::toDTO).toList();
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

}

