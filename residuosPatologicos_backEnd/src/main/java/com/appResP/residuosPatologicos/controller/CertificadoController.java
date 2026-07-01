package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.response.CertificadoDTO;
import com.appResP.residuosPatologicos.DTO.request.CertificadoRequestDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.exceptions.UniqueConstraintViolationException;
import com.appResP.residuosPatologicos.models.enums.Meses;
import com.appResP.residuosPatologicos.services.ICertificadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;


@CrossOrigin
@RestController
@RequestMapping("/api/certificados")
@RequiredArgsConstructor

public class CertificadoController {

    private final ICertificadoService certificadoService;

    /**
     * Obtener certificado por ID
     */
    // GET /api/certificados/{id} → detalle con hojas de ruta

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(certificadoService.findById(id));
    }

    // GET /api/certificados/{id}/pdf
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> imprimirCertificado(@PathVariable Long id) {
        byte[] pdf = certificadoService.generarCertificadoPdf(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=certificado-" + id + ".pdf")
                .body(pdf);
    }

    // GET /api/certificados/{id}/hojas-con-tickets
    @GetMapping("/{id}/hojas-con-tickets")
    public ResponseEntity<?> hojasConTickets(@PathVariable Long id) {
        return ResponseEntity.ok(certificadoService.findHojasConTickets(id));
    }

    /**
     * Listar todos los certificados
     */
    // /api/certificados?page=0&size=10&sort=anio,desc
    @GetMapping
    public ResponseEntity<Page<CertificadoDTO>> findAll(
            @PageableDefault(size = 0) Pageable pageable) {
        return ResponseEntity.ok(certificadoService.findAll(pageable));
    }


    /**
     * Listar certificados por transportista
     */
    // /api/certificados/transportista/5?page=0&size=10&sort=anio,desc
    @GetMapping("/transportista/{idTransportista}")
    public ResponseEntity<?> findByTransportista(@PathVariable Long idTransportista,
                                                 @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                certificadoService.findByTransportista(idTransportista, pageable));
    }

    // GET /api/certificados/mes?mes=ENERO&anio=2026&page=0&size=10
    @GetMapping("/mes")
    public ResponseEntity<Page<CertificadoDTO>> findByMesAndAnio(
            @RequestParam Meses mes,
            @RequestParam int anio,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                certificadoService.findByMesAndAnio(mes, anio, pageable));
    }



    /**
     * Crear certificado
     */
// POST /api/certificados
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CertificadoRequestDTO dto) {
        CertificadoDTO creado = certificadoService.create(dto);
        return ResponseEntity
                .created(URI.create("/api/certificados/" + creado.getId()))
                .body(creado);
    }


    /**
     * Eliminar certificado
     */
    // DELETE /api/certificados/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        certificadoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/certificados/generar-si-falta — útil para testing
    @PostMapping("/generar-si-falta")
    public ResponseEntity<?> generarSiFalta() {
        certificadoService.verificarYCrearCertificadosSiEsNecesario();
        return ResponseEntity.ok("Verificación y generación de certificados ejecutada.");
    }
}
