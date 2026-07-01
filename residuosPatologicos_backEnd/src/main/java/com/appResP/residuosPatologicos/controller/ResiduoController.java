package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.request.ResiduoUpdateDTO;
import com.appResP.residuosPatologicos.DTO.response.ResiduoDTO;
import com.appResP.residuosPatologicos.services.IResiduoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController

@RequestMapping("api/residuo")
@RequiredArgsConstructor
public class ResiduoController {

    private final IResiduoService residuoService;

    @GetMapping("/{id}")
    public ResponseEntity<ResiduoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(residuoService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResiduoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResiduoUpdateDTO request) {
        return ResponseEntity.ok(residuoService.actualizar(id, request));
    }

    @GetMapping("/buscar-por-tipo/{tipoId}")
    public ResponseEntity<List<ResiduoDTO>> buscarPorTipo(@PathVariable Long tipoId) {
        return ResponseEntity.ok(residuoService.buscarPorTipo(tipoId));
    }
}



