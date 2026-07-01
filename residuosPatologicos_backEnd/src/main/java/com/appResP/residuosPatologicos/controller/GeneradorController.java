package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.response.GeneradorDTO;
import com.appResP.residuosPatologicos.DTO.request.GeneradorRequestDTO;
import com.appResP.residuosPatologicos.models.enums.TipoGenerador;
import com.appResP.residuosPatologicos.services.IGeneradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/generadores")

public class GeneradorController{

   private final IGeneradorService generadorService;


    // GET /api/generadores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<GeneradorDTO> findById(@PathVariable Long id){

        return ResponseEntity.ok(generadorService.findById(id));
    }


    // GET /api/generadores?page=0&size=10&sort=id,desc
    @GetMapping
    public ResponseEntity<Page<GeneradorDTO>> findAll(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(generadorService.findAll(pageable));
    }

    // GET /api/generadores/tipo?page=0&size=10&sort=id,desc
    // GET /api/generadores/tipo?tipo=EMPRESA
    // GET /api/generadores/tipo?tipo=AUTONOMO
    @GetMapping("/tipo")
    public ResponseEntity<Page<GeneradorDTO>> findAllTypo(
            @RequestParam(required = false) TipoGenerador tipo,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(generadorService.findAllByTypo(pageable, tipo));
    }

    // GET /api/generadores/activos?page=0&size=10
    @GetMapping("/activos")
    public ResponseEntity<Page<GeneradorDTO>> activos(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(generadorService.findByEstado(true, pageable));
    }

    // GET /api/generadores/inactivos?page=0&size=10
    @GetMapping("/inactivos")
    public ResponseEntity<Page<GeneradorDTO>> inactivos(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(generadorService.findByEstado(false, pageable));
    }


    // POST /api/generadores
@PostMapping
    public ResponseEntity<GeneradorDTO> crear(@Valid @RequestBody GeneradorRequestDTO dto){
        GeneradorDTO creado= generadorService.crear(dto);
    return ResponseEntity
            .created(URI.create("/api/generadores/" + creado.getId()))
            .body(creado);
    }

    // PUT /api/generadores/{id}
@PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                  @Valid  @RequestBody GeneradorRequestDTO dto) {
return ResponseEntity.ok(generadorService.update(id, dto));
}

    // PATCH /api/generadores/{id}/estado?nuevoEstado=true
    @PatchMapping("/{id}/estado")
    public ResponseEntity<GeneradorDTO> cambiarEstado(
            @PathVariable Long id) {

        return ResponseEntity.ok(generadorService.cambiarEstado(id));
    }

    // DELETE /api/generadores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        generadorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}



