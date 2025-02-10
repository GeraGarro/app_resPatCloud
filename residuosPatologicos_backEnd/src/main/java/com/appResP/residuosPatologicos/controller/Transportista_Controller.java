package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.TransportistaDTO;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.services.imp.Transportista_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;


@RestController

@RequestMapping("/api/transportista")
public class Transportista_Controller {

    @Autowired
    private Transportista_service transportistaService;

    //Ver un Determinado  Transportista
@GetMapping("/transportista/{id}")
public ResponseEntity<?> findTransportistaById(@PathVariable Long id){
    Optional<Transportista> transportistaOptional= transportistaService.findByID(id);

    if(transportistaOptional.isPresent()){
        Transportista transportista=transportistaOptional.get();

        TransportistaDTO transportistaDTO= TransportistaDTO.builder()
                .id_transportista(transportista.getId_transportista())
                .nombre(transportista.getNombre())
                .apellido(transportista.getApellido())
                .cuit(transportista.getCuit())
                .telefono(transportista.getTelefono())
                .domicilio(transportista.getDomicilio())
                .estado(transportista.isEstado())
                .build();

        return ResponseEntity.ok(transportistaDTO);

    }
    return ResponseEntity.notFound().build();
}
//Mostrar Todos los Registros
@GetMapping("/Todos")
    public ResponseEntity<?> findTransportistaAll(){
     List <TransportistaDTO> listaTransportista= transportistaService.findAll().stream()
             .map(transportista -> TransportistaDTO.builder()
                     .id_transportista(transportista.getId_transportista())
                     .nombre(transportista.getNombre())
                     .apellido(transportista.getApellido())
                     .cuit(transportista.getCuit())
                     .telefono(transportista.getTelefono())
                     .domicilio(transportista.getDomicilio())
                     .estado(transportista.isEstado())
                     .build()).toList();

     return ResponseEntity.ok(listaTransportista);
}
//Creación De nuevo Transportista
@PostMapping("/crear")
    public ResponseEntity <?> saveTransportista(@RequestBody TransportistaDTO transportistaDTO) throws Exception {
   try {
       if(transportistaDTO.getNombre().isBlank()||
               transportistaDTO.getApellido().isBlank()||
               transportistaDTO.getCuit().isBlank()){
           return ResponseEntity.badRequest().build();
       }
       transportistaService.save(Transportista.builder()
               .nombre(transportistaDTO.getNombre())
               .apellido(transportistaDTO.getApellido())
               .cuit(transportistaDTO.getCuit())
               .telefono(transportistaDTO.getTelefono())
               .domicilio(transportistaDTO.getDomicilio())
               .estado(transportistaDTO.isEstado())
               .build());

       return ResponseEntity.created(new URI("/api/transportista/crear")).body("Transportista Ha sido Creado");
   }catch (IllegalArgumentException e){
       return ResponseEntity.badRequest().body("No ha sido posible crear el Transportista");
   }catch (Exception e) {
       // Error 500 - Internal Server Error
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ha ocurrido un error interno al intentar crear el Transportista");
   }


}

    //Eliminación de Transportista
    @DeleteMapping("eliminar/{id}")
    public ResponseEntity <?> deleteTransportista(@PathVariable Long id){

        if(id!=null){
            try {
                transportistaService.deletebyID(id);
                return  ResponseEntity.ok("El transportista ha sido Eliminado");
            }catch (DataIntegrityViolationException e){
                String mensajeError = "No se puede eliminar el Transportista con ID " + id + " ya que tiene restriccion por estar en Ticket";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(mensajeError);

            }

        }
        return ResponseEntity.notFound().build();
    }

    //Actualizacion de Registro Transportista
    @PutMapping("update/{id}")
    public ResponseEntity <?> updateTransportista(@PathVariable Long id,@RequestBody TransportistaDTO transportistaDTO){
    Optional<Transportista> transportistaOptional=transportistaService.findByID(id);

    if(transportistaOptional.isPresent()){
        Transportista transportista=transportistaOptional.get();
        transportista.setNombre(transportistaDTO.getNombre());
        transportista.setApellido(transportistaDTO.getApellido());
        transportista.setCuit(transportistaDTO.getCuit());
        transportista.setDomicilio(transportistaDTO.getDomicilio());
        transportista.setTelefono(transportistaDTO.getTelefono());
        transportista.setEstado(transportistaDTO.isEstado());

        transportistaService.save(transportista);

        return ResponseEntity.ok("Transportista con id: " + id + " ha sido modificado");
    }

    return ResponseEntity.notFound().build();


    }
}

