package com.appResP.residuosPatologicos.controller;
import com.appResP.residuosPatologicos.DTO.response.TipoResiduoDTO;
import com.appResP.residuosPatologicos.DTO.request.TipoResiduoRequestDTO;
import com.appResP.residuosPatologicos.mappers.TipoResiduoMapper;
import com.appResP.residuosPatologicos.models.TipoResiduo;
import com.appResP.residuosPatologicos.services.ITipoResiduoService;
import com.appResP.residuosPatologicos.services.TransportistaProfileService;
import com.appResP.residuosPatologicos.services.impl.TipoResiduoServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
@CrossOrigin
@RestController
@RequestMapping("/api/tipos-residuo")
@RequiredArgsConstructor

public class TipoResiduoController {

   private final ITipoResiduoService tipoResiduoService;

   private final TipoResiduoMapper tipoResiduoMapper;
   private final TransportistaProfileService transportistaProfileService;


    // GET: Obtener un tipo de residuo por ID
@GetMapping("/{id}")
    public ResponseEntity <TipoResiduoDTO> findTipoResiduobyId(@PathVariable Long id){
    TipoResiduoDTO dto = tipoResiduoService.findById(id);
    return ResponseEntity.ok(dto);
}
    // GET: Obtener todos los tipos de residuos
    @GetMapping
    public ResponseEntity<Page<TipoResiduoDTO>> findAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(tipoResiduoService.findAll(pageable));
    }

    // POST: Crear un nuevo tipo de residuo
    @PostMapping
    public ResponseEntity <?> saveTipoResiduo(@Valid  @RequestBody TipoResiduoRequestDTO dto) throws URISyntaxException {
        transportistaProfileService.requireCompleteTransportistaProfile();

        // Convertir DTO a entidad
        TipoResiduo tipoResiduo= tipoResiduoMapper.toEntity(dto);

        // Guardar
        TipoResiduo saved = tipoResiduoService.save(tipoResiduo);

        // Convertir a DTO y devolver
        TipoResiduoDTO responseDTO = tipoResiduoMapper.toDTO(saved);

        // Crear URI del recurso creado
        URI location = new URI("/api/tipos-residuo/" + saved.getId());

        return ResponseEntity.created(location).body(responseDTO);
    }


    //Eliminacion de Un Tipo de Residuo
    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteTipoResiduo(@PathVariable Long id){
        transportistaProfileService.requireCompleteTransportistaProfile();
        tipoResiduoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
//Actualizacion de un Elemento de Tipo Residuo
  @PutMapping("/{id}")
    public ResponseEntity<?> updateTipoResiduo(@PathVariable Long id,
                                               @Valid @RequestBody TipoResiduoRequestDTO tipoResiduoRequestDTO){
    transportistaProfileService.requireCompleteTransportistaProfile();
    TipoResiduoDTO actualizado= tipoResiduoService.update(id,tipoResiduoRequestDTO);

    return ResponseEntity.ok(actualizado);
    }

  @PatchMapping("/cambio-estado/{id}")
    public ResponseEntity <?> cambioEstadoTipo(@PathVariable Long id){
      transportistaProfileService.requireCompleteTransportistaProfile();
      TipoResiduoDTO actualizado = tipoResiduoService.cambiarEstado(id);
      return ResponseEntity.ok(actualizado);
}
}
