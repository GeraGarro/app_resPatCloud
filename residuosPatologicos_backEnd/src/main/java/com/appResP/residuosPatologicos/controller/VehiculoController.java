package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.request.VehiculoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.VehiculoDTO;
import com.appResP.residuosPatologicos.services.impl.VehiculoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {
    private final VehiculoServiceImpl vehiculoService;

    @PostMapping("/{idTransportista}")
    public ResponseEntity<?> crear(@PathVariable Long idTransportista,  @RequestBody VehiculoRequestDTO req){
        return ResponseEntity.ok(vehiculoService.crear(idTransportista,req));
    }

    @GetMapping("/transportista/{id}")
    public ResponseEntity<?> listarPorTransportista(@PathVariable Long id){
        return ResponseEntity.ok(vehiculoService.listarPorTransportista(id));
    }

    @PatchMapping("/cambio-estado/{idVehiculo}")
    public ResponseEntity<?> cambioEstadoVehiculo (@PathVariable Long idVehiculo){
       VehiculoDTO vehiculoDTO= vehiculoService.cambiarEstadoActividad(idVehiculo);
       String response=vehiculoDTO.isActivo()? "activo" : "Inactivo";

        return ResponseEntity.ok("El estado nuevo de Vehiculo con ID N° "+vehiculoDTO.getIdVehiculo()+" "+response);
    }

    @PutMapping("{idVehiculo}")
    public ResponseEntity<?> updateVehiculo (@PathVariable Long idVehiculo,@RequestBody VehiculoRequestDTO dto){
       VehiculoDTO vehiculoDTO= vehiculoService.updateVehiculo(idVehiculo, dto);

        return ResponseEntity.ok(vehiculoDTO);
    }
}
