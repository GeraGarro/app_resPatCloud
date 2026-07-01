package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.response.*;
import com.appResP.residuosPatologicos.mappers.HojaRutaMapper;
import com.appResP.residuosPatologicos.services.IHojaRutaService;
import com.appResP.residuosPatologicos.services.ITicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/hojas-ruta")
@RequiredArgsConstructor
public class HojaRutaController {

    private final IHojaRutaService hojaService;
    private final ITicketService ticketService;

//
// LISTADO PAGINADO
// api/hojas-ruta?page=0&size=10&sort=fechaInicio,desc
//


    // GET /api/hojas-ruta → listado liviano con cantidadTickets
    @GetMapping
    public ResponseEntity<Page<HojaRutaDTO>> obtenerTodasRutas(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(hojaService.findAll(pageable));
    }


    //OBTENER POR ID
    // GET /api/hojas-ruta/{id}
    @GetMapping("/{id}")
    public ResponseEntity<HojaRutaDetalleDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(hojaService.findDetalleById(id));
    }

    // GET /api/hojas-ruta/{id}/informe/pdf
    @GetMapping("/{id}/informe/pdf")
    public ResponseEntity<byte[]> imprimirInforme(@PathVariable Long id) {
        byte[] pdf = hojaService.generarInformePdf(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=informe-hoja-ruta-" + id + ".pdf")
                .body(pdf);
    }


        //OBTENER POR FECHA (la hoja que contiene esa fecha)
     // Ej: /api/hojas-ruta/por-fecha?fecha=2026-02-07
        @GetMapping("/por-fecha")
        public ResponseEntity<HojaRutaDTO> obtenerPorFecha(
                @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
        ) {
            return ResponseEntity.ok(hojaService.findDTOByFecha(fecha));  }

 // OBTENER HOJA ACTUAL (contiene hoy)
// GET /api/hojas-ruta/actual
    @GetMapping("/actual")
    public ResponseEntity<HojaRutaDTO> obtenerHojaRutaActual() {
        return ResponseEntity.ok(hojaService.findDTOForCurrentDate());

    }

    // GET /api/hojas-ruta/ultima
    @GetMapping("/ultima")
    public ResponseEntity<HojaRutaDTO> obtenerUltima() {
        return ResponseEntity.ok(hojaService.findUltima());

    }

     // FORZAR GENERACIÓN (útil para testing / si el server estuvo apagado)
     // POST /api/hojas-ruta/generar-si-falta
    @PostMapping("/generar-si-falta")
    public ResponseEntity<?> generarSiFalta() {
        hojaService.verificarYCrearHojaRutaSiEsNecesario();
        return ResponseEntity.ok("Verificación y generación ejecutada.");
    }


//      HOJAS POR CERTIFICADO
//      (Normalmente: obtener tickets por certificado y extraer hojas.
//       Mejor: resolver con query en TicketControlService o repo.)

    // GET /api/hojas-ruta/por-certificado/{certificadoId}
    @GetMapping("/por-certificado/{certificadoId}")
    public ResponseEntity<?> hojasByCertificado(@PathVariable Long certificadoId) {
        return ResponseEntity.ok(hojaService.findByCertificadoId(certificadoId));

    }

    // GET /api/hojas-ruta/pendientes-certificado/transportista/{idTransportista}
    @GetMapping("/pendientes-certificado/transportista/{idTransportista}")
    public ResponseEntity<List<HojaRutaDTO>> hojasPendientesCertificado(
            @PathVariable Long idTransportista) {
        return ResponseEntity.ok(hojaService.findPendientesCertificadoByTransportista(idTransportista));
    }

//* TICKETS DE UNA HOJA
// GET /api/hojas-ruta/{id}/tickets
    @GetMapping("/{id}/tickets")
    public ResponseEntity<Page<TicketDTO>> ticketsDeHoja(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.findByHojaRutaId(id, pageable));
    }



    // NUEVO: TICKETS DE LA HOJA ACTUAL (sin pasar id)
    // GET /api/hojas-ruta/actual/tickets?page=0&size=10
    @GetMapping("/actual/tickets")
    public ResponseEntity<?> ticketsDeHojaActual(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        HojaRutaDTO hojaActual = hojaService.findDTOForCurrentDate();
        return ResponseEntity.ok(ticketService.findByHojaRutaId(hojaActual.getId(), pageable));
    }
}
