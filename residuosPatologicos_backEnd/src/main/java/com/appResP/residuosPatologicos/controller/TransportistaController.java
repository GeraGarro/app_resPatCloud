package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.request.TransportistaRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.TransportistaDTO;
import com.appResP.residuosPatologicos.services.ITransportistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
public class TransportistaController {


    private final ITransportistaService transportistaService;

@GetMapping("/me")
public ResponseEntity<TransportistaDTO> me(){
return ResponseEntity.ok(transportistaService.findAuthenticated());
}

@GetMapping("/me/estado-perfil")
public ResponseEntity<?> estadoPerfil(){
return ResponseEntity.ok(transportistaService.getAuthenticatedProfileStatus());
}

    //Ver un Determinado  Transportista
@GetMapping("/{idTransportista}")
public ResponseEntity<TransportistaDTO> findById(@PathVariable Long idTransportista){
return ResponseEntity.ok(transportistaService.findByID(idTransportista));
}
//Mostrar Todos los Registros
@GetMapping
    public ResponseEntity<List<TransportistaDTO>> findAll(){
return ResponseEntity.ok(transportistaService.findAll());

}


    @PostMapping
public ResponseEntity<TransportistaDTO> save(@RequestBody TransportistaRequestDTO dtoRequest){
    return ResponseEntity.ok(transportistaService.save(dtoRequest));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transportistaService.deletebyId(id);
        return ResponseEntity.noContent().build();
    }


    //Actualizacion de Registro Transportista
    @PutMapping("/update/{id}")
    public ResponseEntity <TransportistaDTO> updateTransportista(@PathVariable Long id,
                                                                 @RequestBody TransportistaRequestDTO transportistaDTO){
TransportistaDTO updated= transportistaService.update(id, transportistaDTO);
return ResponseEntity.ok(updated);
}
    }
