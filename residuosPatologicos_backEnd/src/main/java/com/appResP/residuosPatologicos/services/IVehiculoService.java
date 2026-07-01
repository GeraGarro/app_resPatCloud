package com.appResP.residuosPatologicos.services;

import com.appResP.residuosPatologicos.DTO.request.VehiculoRequestDTO;
import com.appResP.residuosPatologicos.DTO.request.VehiculoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.VehiculoDTO;
import com.appResP.residuosPatologicos.models.Vehiculo;

import java.util.List;

public interface IVehiculoService {

    public VehiculoDTO crear(Long transportistaId, VehiculoRequestDTO req);

    public List<VehiculoDTO> listarPorTransportista(Long transportistaId);

    public VehiculoDTO cambiarEstadoActividad(Long idVehiculo);

    public VehiculoDTO updateVehiculo(Long idVehiculo, VehiculoRequestDTO dto);
}
