package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.response.AdminUsuarioDTO;
import com.appResP.residuosPatologicos.services.AdminUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private final AdminUsuarioService adminUsuarioService;

    @GetMapping("/transportistas/pendientes")
    public ResponseEntity<List<AdminUsuarioDTO>> findTransportistasPendientes() {
        return ResponseEntity.ok(adminUsuarioService.findTransportistasPendientes());
    }

    @PatchMapping("/{usuarioId}/aprobar")
    public ResponseEntity<AdminUsuarioDTO> aprobarTransportista(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(adminUsuarioService.aprobarTransportista(usuarioId));
    }

    @PatchMapping("/{usuarioId}/rechazar")
    public ResponseEntity<AdminUsuarioDTO> rechazarTransportista(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(adminUsuarioService.rechazarTransportista(usuarioId));
    }

    @PatchMapping("/{usuarioId}/suspender")
    public ResponseEntity<AdminUsuarioDTO> suspenderTransportista(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(adminUsuarioService.suspenderTransportista(usuarioId));
    }
}
