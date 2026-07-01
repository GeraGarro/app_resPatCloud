package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.response.GeneradorDTO;
import com.appResP.residuosPatologicos.DTO.request.GeneradorRequestDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.DuplicateResourceException;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.GeneradorMapper;
import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.models.GeneradorAutonomo;
import com.appResP.residuosPatologicos.models.GeneradorEmpresa;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.enums.Rol;
import com.appResP.residuosPatologicos.models.enums.TipoGenerador;
import com.appResP.residuosPatologicos.repository.IGeneradorAutonomoRepository;
import com.appResP.residuosPatologicos.repository.IGeneradorEmpresaRepository;
import com.appResP.residuosPatologicos.repository.IGeneradorRepository;
import com.appResP.residuosPatologicos.repository.ITransportistaRepository;
import com.appResP.residuosPatologicos.services.IGeneradorService;
import com.appResP.residuosPatologicos.services.TransportistaProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GeneradorServiceImpl implements IGeneradorService {

    private final IGeneradorRepository generadorRepository; // JpaRepository<Generador, Long>
    private final IGeneradorEmpresaRepository generadorEmpresaRepository; // para existsByCuit
    private final IGeneradorAutonomoRepository generadorAutonomoRepository; // para existsByCuil
    private final ITransportistaRepository transportistaRepository;

    private final GeneradorMapper generadorMapper; // bean (no static)
    private final TransportistaProfileService transportistaProfileService;

    @Override
    @Transactional(readOnly = true)
    public Page<GeneradorDTO> findAllByTypo(Pageable pageable, TipoGenerador tipo) {
       Transportista transportista = obtenerTransportistaAutenticadoSiCorresponde();

       if(tipo == null){
           if (transportista != null) {
               return generadorRepository
                       .findByTransportista_IdTransportista(transportista.getIdTransportista(), pageable)
                       .map(generadorMapper::toDTO);
           }
           return generadorRepository.findAll(pageable).map(generadorMapper::toDTO);
       }

       if (transportista != null) {
           return switch (tipo){
               case EMPRESA -> generadorEmpresaRepository
                       .findByTransportista_IdTransportista(transportista.getIdTransportista(), pageable)
                       .map(generadorMapper::toDTO);
               case AUTONOMO -> generadorAutonomoRepository
                       .findByTransportista_IdTransportista(transportista.getIdTransportista(), pageable)
                       .map(generadorMapper::toDTO);

           };
       }

        return switch (tipo){
            case EMPRESA -> generadorEmpresaRepository.findAll(pageable).map(generadorMapper::toDTO);
            case AUTONOMO -> generadorAutonomoRepository.findAll(pageable).map(generadorMapper::toDTO);

        };
    }

    @Override
    @Transactional(readOnly = true)
    public GeneradorDTO findById(Long id) {
        return generadorRepository.findById(id)
                .map(generadorMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Generador no encontrado: " + id));
    }


    @Override
    public GeneradorDTO crear(GeneradorRequestDTO dto) {
        validarTipo(dto);
        validarCamposObligatorios(dto);
        validarUnicosCreate(dto);

        Transportista transportista = transportistaProfileService.requireCompleteTransportistaProfile();
        Generador entity = generadorMapper.toEntity(dto);
        entity.setTransportista(transportista);

        try {
            return generadorMapper.toDTO(generadorRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Ya existe un generador con CUIT/CUIL repetido.");
        }
    }

    @Override
    public GeneradorDTO update(Long id, GeneradorRequestDTO dto) {
        Generador existing = generadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Generador no encontrado: " + id));


        // 1) Validaciones base
        validarCamposObligatorios(dto);


        //2) Regla: NO permitir cambiar el subtipo en update (evita líos con herencia)
        if (dto.getTipo() != null) {
            TipoGenerador actual = (existing instanceof GeneradorEmpresa)
                    ? TipoGenerador.EMPRESA
                    : TipoGenerador.AUTONOMO;

            if (dto.getTipo() != actual) {
                throw new IllegalArgumentException(
                        "No se permite cambiar el tipo de generador en una actualización.");
            }
        }

        // 3) Validación de unicidad (update real)
        validarUnicosUpdate(existing, dto);

        // 4) Aplicar cambios
        generadorMapper.updateEntity(existing, dto);

        // 5) Guardar
        try {
            Generador saved = generadorRepository.save(existing);
            return generadorMapper.toDTO(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Ya existe un generador con CUIT/CUIL repetido.");
        }
    }

    @Override
    public void delete(Long id) {
        if (!generadorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Generador no encontrado: " + id);
        }
        generadorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GeneradorDTO> findByEstado(boolean estado, Pageable pageable) {
        Transportista transportista = obtenerTransportistaAutenticadoSiCorresponde();

        if (transportista != null) {
            return generadorRepository
                    .findByEstadoAndTransportista_IdTransportista(estado, transportista.getIdTransportista(), pageable)
                    .map(generadorMapper::toDTO);
        }

        return generadorRepository.findByEstado(estado, pageable).map(generadorMapper::toDTO);

    }

    @Override
    public Page<GeneradorDTO> findAll(Pageable pageable) {
        Transportista transportista = obtenerTransportistaAutenticadoSiCorresponde();

        if (transportista != null) {
            return generadorRepository
                    .findByTransportista_IdTransportista(transportista.getIdTransportista(), pageable)
                    .map(generadorMapper::toDTO);
        }

        return generadorRepository.findAll(pageable).map(generadorMapper::toDTO);

    }


    // findAllList — solo si el frontend lo necesita para combos
    @Override
    public List<GeneradorDTO> findAllList() {
        return generadorMapper.toDTOList(generadorRepository.findAll());
    }
    @Override
    public GeneradorDTO cambiarEstado(Long id) {
        Generador generador = generadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Generador no encontrado: " + id));
        generador.setEstado(!generador.isEstado());
        return generadorMapper.toDTO(generadorRepository.save(generador)); // save explícito
    }

    private void validarTipo(GeneradorRequestDTO dto) {
        if (dto == null || dto.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de generador es obligatorio.");
        }
    }

    private Transportista obtenerTransportistaAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Debe iniciar sesion como transportista para registrar generadores.");
        }

        return transportistaRepository.findByUsuario_Email(authentication.getName())
                .or(() -> transportistaRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un transportista asociado al usuario autenticado."));
    }

    private Transportista obtenerTransportistaAutenticadoSiCorresponde() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        boolean esTransportista = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + Rol.TRANSPORTISTA.name()));

        return esTransportista ? obtenerTransportistaAutenticado() : null;
    }

    private void validarUnicosCreate(GeneradorRequestDTO dto) {
        if (dto.getTipo() == TipoGenerador.EMPRESA) {
            if (generadorEmpresaRepository.existsByCuit(dto.getCuit()))
                throw new DuplicateResourceException(
                        "Ya existe un generador empresa con ese CUIT.");
        } else {
            if (generadorAutonomoRepository.existsByCuil(dto.getCuil()))
                throw new DuplicateResourceException(
                        "Ya existe un generador autónomo con ese CUIL.");
        }
        if (hasText(dto.getEmail()) && generadorRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException(
                    "Ya existe un generador con ese email.");

    }

    private void validarUnicosUpdate(Generador existing, GeneradorRequestDTO dto) {

        if (existing instanceof GeneradorEmpresa empresa) {
            if (!dto.getCuit().equals(empresa.getCuit())
                    && generadorEmpresaRepository.existsByCuitAndIdNot(dto.getCuit(), empresa.getId()))
                throw new DuplicateResourceException("Ya existe un generador empresa con ese CUIT.");

        } else if (existing instanceof GeneradorAutonomo autonomo) {
            if (!dto.getCuil().equals(autonomo.getCuil())
                    && generadorAutonomoRepository.existsByCuilAndIdNot(dto.getCuil(), autonomo.getId()))
                throw new DuplicateResourceException("Ya existe un generador autónomo con ese CUIL.");
        }

        if (hasText(dto.getEmail())
                && !dto.getEmail().equals(existing.getEmail())
                && generadorRepository.existsByEmailAndIdNot(dto.getEmail(), existing.getId()))
            throw new DuplicateResourceException("Ya existe un generador con ese email.");

    }
    private void validarCamposObligatorios(GeneradorRequestDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("Datos del generador obligatorios.");

        if (dto.getDomicilio() == null)
            throw new IllegalArgumentException("El domicilio es obligatorio.");

        if (dto.getTipo() == TipoGenerador.EMPRESA) {
            if (dto.getCuit() == null || dto.getCuit().isBlank())
                throw new IllegalArgumentException("El CUIT es obligatorio para generador empresa.");
            if (dto.getRazonSocial() == null || dto.getRazonSocial().isBlank())
                throw new IllegalArgumentException("La razón social es obligatoria para generador empresa.");

        } else if (dto.getTipo() == TipoGenerador.AUTONOMO) {
            if (dto.getNombre() == null || dto.getNombre().isBlank())
                throw new IllegalArgumentException("El nombre es obligatorio para generador autónomo.");
            if (dto.getApellido() == null || dto.getApellido().isBlank())
                throw new IllegalArgumentException("El apellido es obligatorio para generador autónomo.");
            if (dto.getCuil() == null || dto.getCuil().isBlank())
                throw new IllegalArgumentException("El CUIL es obligatorio para generador autónomo.");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
    }

