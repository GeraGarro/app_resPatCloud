package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.request.VehiculoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.VehiculoDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.VehiculoMapper;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.Vehiculo;
import com.appResP.residuosPatologicos.repository.ITransportistaRepository;
import com.appResP.residuosPatologicos.repository.IVehiculoRepository;

import com.appResP.residuosPatologicos.services.IVehiculoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculoServiceImpl implements IVehiculoService {
    private final IVehiculoRepository vehiculoRepository;
    private final ITransportistaRepository transportistaRepository;
    private final VehiculoMapper vehiculoMapper;


    public VehiculoDTO crear(Long transportistaId, VehiculoRequestDTO req){
        if (vehiculoRepository.existsByDominio(req.getDominio()))
        throw new IllegalArgumentException("Ya existe un vehículo con ese dominio.");

        if (vehiculoRepository.existsByChasis(req.getChasis()))
            throw new IllegalArgumentException("Ya existe un vehículo con ese chasis.");

        Transportista transportista = transportistaRepository.findById(transportistaId)
                .orElseThrow(() -> new IllegalArgumentException("Transportista no encontrado."));

        Vehiculo vehiculo = Vehiculo.builder()
                .marca(req.getMarca())
                .modelo(req.getModelo())
                .dominio(req.getDominio())
                .chasis(req.getChasis())
                .anio(req.getAnio())
                .activo(req.isActivo())
                .transportista(transportista)
                .build();

        Vehiculo guardado = vehiculoRepository.save(vehiculo);
        return vehiculoMapper.toDTO(guardado);
    }

    @Transactional (readOnly = true)
    public List<VehiculoDTO> listarPorTransportista(Long transportistaId){
        return vehiculoRepository.findByTransportista_IdTransportista(transportistaId)
                .stream()
                .map(vehiculoMapper::toDTO)
                .toList();


    }

    @Transactional
    public VehiculoDTO cambiarEstadoActividad(Long idVehiculo){
        Vehiculo vehiculo = vehiculoRepository.findById( idVehiculo)
                .orElseThrow(()-> new ResourceNotFoundException( "Vehiculo no encontrado"));

        vehiculo.setActivo(!vehiculo.isActivo());
        return vehiculoMapper.toDTO(vehiculoRepository.save(vehiculo));
    }

    @Override
    public VehiculoDTO updateVehiculo(Long idVehiculo ,VehiculoRequestDTO dto) {

        Transportista t= transportistaRepository.findById(dto.getIdTransportista())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transportista no encontrado con id: " + dto.getIdTransportista()));

        Vehiculo vehiculo = vehiculoRepository.findById( idVehiculo)
                .orElseThrow(()-> new ResourceNotFoundException( "Vehiculo no encontrado"));

        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setDominio(dto.getDominio());
        vehiculo.setChasis(dto.getChasis());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setActivo(dto.isActivo());
        vehiculo.setTransportista(t);


        return vehiculoMapper.toDTO( vehiculoRepository.save(vehiculo));
    }
}
