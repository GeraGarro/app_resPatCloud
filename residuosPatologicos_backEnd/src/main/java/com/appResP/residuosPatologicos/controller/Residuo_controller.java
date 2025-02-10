package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.ResiduoDTO;
import com.appResP.residuosPatologicos.DTO.TipoResiduoDTO;
import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.models.Ticket_control;
import com.appResP.residuosPatologicos.models.Tipo_residuo;
import com.appResP.residuosPatologicos.services.imp.Residuo_service;
import com.appResP.residuosPatologicos.services.imp.TicketControl_service;
import com.appResP.residuosPatologicos.services.imp.TipoResiduo_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("api/Residuo")
public class Residuo_controller {

@Autowired
    Residuo_service residuoService;
@Autowired
    TicketControl_service ticketControlService;
@Autowired
    TipoResiduo_Service tipoResiduoService;
@GetMapping("/unResiduo/{id}")
  public ResponseEntity <?> findResiduoById (@PathVariable Long id){
    try{
        Optional<Residuo> residuoOptional= residuoService.findByID(id);
        if(residuoOptional.isPresent()){
            ResiduoDTO residuoDTO= mapToDTO(residuoOptional.get());
            return ResponseEntity.ok(residuoDTO);
        }else{
            return (ResponseEntity.status(404).body("Residuo no encontrado"));
        }
    }catch (Exception e){
        return ResponseEntity.status(500).body("Error al procesar");
    }
}

private ResiduoDTO mapToDTO(Residuo residuo){
    return ResiduoDTO.builder()
            .id(residuo.getId())
            .tipoResiduo(TipoResiduoDTO.builder()
                    .id(residuo.getTipoResiduo().getId())
                    .nombre(residuo.getTipoResiduo().getNombre())
                    .estado(residuo.getTipoResiduo().isEstado())
                    .codigo(residuo.getTipoResiduo().getCodigo())
                    .build())
            .peso(residuo.getPeso())
            .id_TicketControl(residuo.getTicketControl().getId_Ticket())
            .build();

}
@GetMapping("/verTodos")
    public ResponseEntity<?> findResiduoAll(){
    List <ResiduoDTO> listaResiduos=residuoService.findAll().stream()
            .map(residuo -> ResiduoDTO.builder()
                    .id(residuo.getId())
                    .tipoResiduo(TipoResiduoDTO.builder()
                            .id(residuo.getTipoResiduo().getId())
                            .nombre(residuo.getTipoResiduo().getNombre())
                            .estado(residuo.getTipoResiduo().isEstado())
                            .build())
                    .peso(residuo.getPeso())
                    .id_TicketControl(residuo.getTicketControl().getId_Ticket())
                    .build()).toList();

    return ResponseEntity.ok(listaResiduos);
}

@PostMapping("/crear")
    public ResponseEntity<?> saveResiduo(@RequestBody @Validated ResiduoDTO residuoDTO) throws Exception{
try {
    Optional<Ticket_control> ticketControlOptional=ticketControlService.findByID(residuoDTO.getId_TicketControl());

    Optional<Tipo_residuo> tipoResiduoOptional= tipoResiduoService.findByID(residuoDTO.getTipoResiduo().getId());

    if(ticketControlOptional.isPresent()){
        Ticket_control ticketControl=ticketControlOptional.get();
        Tipo_residuo tipoResiduo=tipoResiduoOptional.get();

        if(residuoDTO.getPeso()<0){
            residuoDTO.setPeso(0);
        }

        Residuo residuo=Residuo.builder()
                .tipoResiduo(tipoResiduo)
                .peso(residuoDTO.getPeso())
                .ticketControl(ticketControl)
                .build();
        residuoService.save(residuo);

        return ResponseEntity.ok("El residuo ha sido creado");
    }


    URI location = URI.create("/api/Residuo/crear"); // Construir la URL

    return ResponseEntity.created(location).body("Residuo Incorporado a Ticket "+residuoDTO.getId_TicketControl());
}catch (DataIntegrityViolationException e) {
    return ResponseEntity.badRequest().body("No se puede crear dos residuos con el mismo tipo en el mismo ticket.");
} catch (Exception e) {
    return ResponseEntity.badRequest().body("Error al procesar la solicitud.");
}
}


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> deleteResiduo(@PathVariable Long id){
    if (id!=null){
        try {
            residuoService.deletebyID(id);
            return ResponseEntity.ok("El Residuo con ID: "+id+ " ha sido Eliminado");

        }catch (Exception e){
            return ResponseEntity.badRequest().body("El Residuo con ID " + id + " no existe");


        }
    }
    return ResponseEntity.notFound().build();

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateResiduo(@PathVariable Long id,@RequestBody ResiduoDTO residuoDTO){
    Optional<Residuo> residuoOptional=residuoService.findByID(id);
    Optional<Tipo_residuo> tipoResiduoOptional=tipoResiduoService.findByID(residuoDTO.getTipoResiduo().getId());
    Optional<Ticket_control> ticketControlOptional=ticketControlService.findByID(residuoDTO.getId_TicketControl());
    try {
        if(residuoOptional.isPresent()){
            Residuo residuoEdit=residuoOptional.get();
            if(tipoResiduoOptional.isPresent()){
                Tipo_residuo tipoResiduo=tipoResiduoOptional.get();
                residuoEdit.setTipoResiduo(tipoResiduo);
            }
            if(residuoDTO.getPeso()>0){
                residuoEdit.setPeso(residuoDTO.getPeso());
            }
            Ticket_control ticketControl=ticketControlOptional.get();
            residuoEdit.setTicketControl(ticketControl);

            residuoService.save(residuoEdit);
            return ResponseEntity.ok("Residuo con id: " + id + " ha sido modificado");

        }  else {
            return ResponseEntity.badRequest().body("El Residuo con ID " + id + " no existe");
        }
    }catch (Exception e) {
        return ResponseEntity.badRequest().body("No ha sido posible realizar la modificación");
    }

    }
}



