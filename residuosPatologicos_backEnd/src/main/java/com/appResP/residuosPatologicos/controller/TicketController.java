package com.appResP.residuosPatologicos.controller;
import com.appResP.residuosPatologicos.DTO.request.ResiduoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.ResiduoDTO;
import com.appResP.residuosPatologicos.DTO.response.TicketDTO;
import com.appResP.residuosPatologicos.DTO.request.TicketRequestDTO;
import com.appResP.residuosPatologicos.models.Residuo;
import com.appResP.residuosPatologicos.services.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/ticket-control")
@RequiredArgsConstructor
public class TicketController {

    private final ITicketService ticketService;

    // GET /api/ticket-control/{id} → detalle con residuos
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    // GET /api/ticket-control/{id}/manifiesto/pdf
    @GetMapping("/{id}/manifiesto/pdf")
    public ResponseEntity<byte[]> imprimirManifiesto(@PathVariable Long id) {
        byte[] pdf = ticketService.generarManifiestoPdf(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=manifiesto-ticket-" + id + ".pdf")
                .body(pdf);
    }

    // LISTADO LIVIANO PAGINADO
    // GET /api/ticket-control?page=0&size=20&sort=fechaEmision,desc
    @GetMapping
    public ResponseEntity<Page<TicketDTO>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ticketService.findAll(pageable));
    }

    // GET /api/ticket-control/por-hoja-ruta/{hojaRutaId}
    @GetMapping("/por-hoja-ruta/{hojaRutaId}")
    public ResponseEntity<Page<TicketDTO>> listByHojaRuta(
            @PathVariable Long hojaRutaId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ticketService.findByHojaRutaId(hojaRutaId, pageable));
    }


    // GET /api/ticket-control/por-periodo?anio=2026&mes=2&idTransportista=1
    @GetMapping("/por-periodo")
    public ResponseEntity<List<TicketDTO>> getByPeriodo(@RequestParam int anio,
                                                        @RequestParam int mes,
                                                        @RequestParam Long idTransportista) {
        return ResponseEntity.ok(ticketService.findTicketsByPeriodo(anio, mes, idTransportista));
    }


    // POST /api/ticket-control
    @PostMapping
    public ResponseEntity<TicketDTO> create(
            @Valid @RequestBody TicketRequestDTO request) {
        TicketDTO created = ticketService.create(request);
        return ResponseEntity
                .created(URI.create("/api/tickets/" + created.getIdTicket()))
                .body(created);
    }


    // PUT /api/ticket-control/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> update(@PathVariable Long id,
                                            @Valid @RequestBody TicketRequestDTO request) {
        return ResponseEntity.ok(ticketService.update(id, request));
    }

    // PATCH /api/ticket-control/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TicketDTO> actualizarEstado(
            @PathVariable Long id) {
        return ResponseEntity.ok(ticketService.actualizarEstado(id));
    }

    // POST /api/ticket-control/procesar-hojas-vencidas
    @PostMapping("/procesar-hojas-vencidas")
    public ResponseEntity<Integer> procesarHojasVencidas() {
        return ResponseEntity.ok(ticketService.procesarTicketsDeHojasVencidas());
    }

    // DELETE /api/ticket-control/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.deletebyId(id);
        return ResponseEntity.noContent().build();
    }

    // ========== SUB-RECURSOS: residuos  ==========

    // GET /api/ticket-control/{ticketId}/residuos
    @GetMapping("/{ticketId}/residuos")
    public ResponseEntity<List<ResiduoDTO>> listarResiduos(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.obtenerResiduos(ticketId));
    }

    // POST /api/ticket-control/{ticketId}/residuos
    @PostMapping("/{ticketId}/residuo")
    public ResponseEntity<ResiduoDTO> agregarResiduo(@PathVariable Long ticketId,
                                                  @Valid @RequestBody ResiduoRequestDTO requestDTO){
        ResiduoDTO creado = ticketService.agregarResiduo(ticketId, requestDTO);
        return ResponseEntity
                .created(URI.create("/api/ticket-control/" + ticketId + "/residuos"))
                .body(creado);
    }

    // DELETE /api/ticket-control/{ticketId}/residuos/{residuoId}
    @DeleteMapping("/{ticketId}/residuos/{residuoId}")
    public ResponseEntity<Void> eliminarResiduo(
            @PathVariable Long ticketId,
            @PathVariable Long residuoId) {
        ticketService.eliminarResiduo(ticketId, residuoId);
        return ResponseEntity.noContent().build();
    }
    }
