package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.*;
import com.appResP.residuosPatologicos.models.Hoja_ruta;
import com.appResP.residuosPatologicos.services.imp.HojaRuta_service;
import com.appResP.residuosPatologicos.services.imp.TicketControl_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController

@RequestMapping("/api/HojaRuta")

public class HojaRuta_controller {

    @Autowired
    HojaRuta_service hoja_service;

    @Autowired
    TicketControl_service ticketControlService;

    @GetMapping ("/verTodos")
    public Hoja_ruta obtenerTodasLasHojasRuta(LocalDate fecha) {
        return hoja_service.obtenerHojaRutaPorFecha(fecha);
    }

@GetMapping("/actual")
    public ResponseEntity<Map<String, Object>> obtenerHojaRutaActual() {
        Optional<Hoja_ruta> hojaRutaActual = hoja_service.obtenerHojaRutaActual();

        if (hojaRutaActual.isPresent()) {
            Hoja_ruta hojaRuta = hojaRutaActual.get();
            Hoja_RutaDTO dto = Hoja_RutaDTO.builder()
                    .id(hojaRuta.getId())
                    .fechaInicio(hojaRuta.getFechaInicio())
                    .fechaFin(hojaRuta.getFechaFin())
                    .build();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", dto
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", "No se encontró una hoja de ruta para la fecha actual"
            ));
        }
    }

@GetMapping("hojasPorCertificado/{id}")
    ResponseEntity<?> hojasbyCertificado(@PathVariable Long id){
    List<Hoja_RutaDTO> listaHojas=hoja_service.getHojasRutaByCertificado(id).stream()
            .map(hojaRuta -> Hoja_RutaDTO.builder()
                    .id(hojaRuta.getId())
                    .fechaInicio(hojaRuta.getFechaInicio())
                    .fechaFin(hojaRuta.getFechaFin())
                    .listaTickets
                            (hojaRuta.getListaTickets().stream()
                                    .map(ticketControl -> Ticket_controlDTO.builder()
                                        .id_Ticket(ticketControl.getId_Ticket())
                                            .codigo(ticketControlService.codificacionIdTicket(ticketControl.getId_Ticket()))
                                            .generador(GeneradorDTO.builder()
                                                            .id(ticketControl.getGenerador().getId())
                                                            .nombre(ticketControl.getGenerador().getNombre())
                                                            .direccion(ticketControl.getGenerador().getDireccion())
                                                            .cuit(ticketControl.getGenerador().getCuit())
                                                            .legajo(ticketControl.getGenerador().getLegajo())
                                                            .telefono(ticketControl.getGenerador().getTelefono())
                                                            .estado(ticketControl.getGenerador().isEstado())
                                                    .build())
                                            .transportista(TransportistaDTO.builder()
                                                    .id_transportista(ticketControl.getTransportista().getId_transportista())
                                                    .nombre(ticketControl.getTransportista().getNombre())
                                                    .apellido(ticketControl.getTransportista().getApellido())
                                                    .cuit(ticketControl.getTransportista().getCuit())
                                                    .telefono(ticketControl.getTransportista().getTelefono())
                                                    .domicilio(ticketControl.getTransportista().getDomicilio())
                                                    .estado(ticketControl.getTransportista().isEstado()).build())
                                            .fechaEmisionTk(ticketControl.getFechaEmision())
                                            .estado(ticketControl.isEstado())
                                            .listaResiduos
                                                    (ticketControl.getListaResiduos().stream().map(residuo -> ResiduoDTO.builder()
                                                            .id(residuo.getId())
                                                            .peso(residuo.getPeso())
                                                            .tipoResiduo(TipoResiduoDTO.builder()
                                                                    .id(residuo.getTipoResiduo().getId())
                                                                    .nombre(residuo.getTipoResiduo().getNombre())
                                                                    .codigo(residuo.getTipoResiduo().getCodigo())
                                                                    .estado(residuo.getTipoResiduo().isEstado())
                                                                    .build())
                                                            .id_TicketControl(residuo.getTicketControl().getId_Ticket())
                                                            .build()).toList())
                                            .pesoTotal(ticketControlService.pesoResiduosByTicket(ticketControl.getId_Ticket()).setScale(2, RoundingMode.HALF_UP))
                                            .build())
                                    .toList()).build()).toList();
    return ResponseEntity.ok(listaHojas);
}

    }
