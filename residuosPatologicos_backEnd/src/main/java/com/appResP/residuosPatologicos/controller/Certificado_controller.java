package com.appResP.residuosPatologicos.controller;

import com.appResP.residuosPatologicos.DTO.CertificadoDTO;
import com.appResP.residuosPatologicos.DTO.TicketsReport;
import com.appResP.residuosPatologicos.DTO.TransportistaDTO;
import com.appResP.residuosPatologicos.exceptions.UniqueConstraintViolationException;
import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.models.Ticket_control;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.enums.Meses;
import com.appResP.residuosPatologicos.services.imp.Certificado_service;
import com.appResP.residuosPatologicos.services.imp.Generador_service;

import com.appResP.residuosPatologicos.services.imp.TicketControl_service;
import com.appResP.residuosPatologicos.services.imp.Transportista_service;
import jakarta.persistence.EntityNotFoundException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.RoundingMode;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/api/Certificado")

public class Certificado_controller {

    @Autowired
    Certificado_service certificadoService;
    @Autowired
    Transportista_service transportistaService;
    @Autowired
    Generador_service generadorService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    TicketControl_service ticketControlService;

    @GetMapping("/infocertificado/{id}")
    public ResponseEntity<?> findByCertificadoId(@PathVariable Long id) {
        Optional<Certificado> certificadoOptional = certificadoService.findByID(id);

        if (certificadoOptional.isPresent()) {
            Certificado certificado = certificadoOptional.get();

            Optional<Transportista> transportistaOptional = transportistaService.findByID(certificado.getTransportista().getId_transportista());


            if (transportistaOptional.isPresent()) {
                Transportista transportista = transportistaOptional.get();

                TransportistaDTO transportistaDTO = TransportistaDTO.builder()
                        .id_transportista(transportista.getId_transportista())
                        .nombre(transportista.getNombre())
                        .apellido(transportista.getApellido())
                        .cuit(transportista.getCuit())
                        .telefono(transportista.getTelefono())
                        .domicilio(transportista.getDomicilio())
                        .estado(transportista.isEstado())
                        .build();


                CertificadoDTO certificadoDTO = CertificadoDTO.builder()
                        .id(certificado.getId())
                        .mes(certificado.getMes())
                        .anio(certificado.getAño())
                        .peso(0)
                        .transportista(transportistaDTO)
                        .listaTicketsDTO(certificado.getListaTickets().stream().
                                map(ticketControl -> ticketControl.getId_Ticket())
                                .collect(Collectors.toList())
                        )
                        .build();

                return ResponseEntity.ok(certificadoDTO);
            }

        }

        return ResponseEntity.badRequest().body("No se ha podido encontrar el Certificado solicitado");
    }

    @GetMapping("/verTodos")
    public ResponseEntity<?> findAllCertificados() {
        List<CertificadoDTO> listaCertificadoDTO = certificadoService.findAll().stream().map(
                        certificado -> CertificadoDTO.builder()
                                .id(certificado.getId())
                                .transportista(TransportistaDTO.builder()
                                        .id_transportista(certificado.getTransportista().getId_transportista())
                                        .nombre(certificado.getTransportista().getNombre())
                                        .apellido(certificado.getTransportista().getApellido())
                                        .domicilio(certificado.getTransportista().getDomicilio())
                                        .cuit(certificado.getTransportista().getCuit())
                                        .telefono(certificado.getTransportista().getTelefono())
                                        .estado(certificado.getTransportista().isEstado())
                                        .build())
                                .mes(certificado.getMes())
                                .anio(certificado.getAño())
                                .listaTicketsDTO(certificado.getListaTickets().stream()
                                        .map(ticketControl -> ticketControl.getId_Ticket())
                                        .collect(Collectors.toList())
                                )
                                .build())
                .toList();

        return ResponseEntity.ok(listaCertificadoDTO);

    }
    @GetMapping("/FiltroTransportista/{id_transportista}")
    public ResponseEntity <?> findCertificadosByTransportista(@PathVariable Long id_transportista){
        Optional<Transportista> transportistaOptional=transportistaService.findByID(id_transportista);
        if(transportistaOptional.isPresent()) {
            Transportista transportista = transportistaOptional.get();
            List<Long> listaCertificados = certificadoService.findListByTransportista(transportista.getId_transportista())
        .stream().map(certificado -> certificado.getId()).collect(Collectors.toList());
          return   ResponseEntity.ok(listaCertificados);
        }
return ResponseEntity.badRequest().body("No se Encontraron Certificados Para el Transportista ");
    }

    @PostMapping("/crear")
    public ResponseEntity<?> saveCertificado(@RequestBody CertificadoDTO certificadoDTO) {
        Optional<Transportista> transportistaOptional = transportistaService.findByID(certificadoDTO.getTransportista().getId_transportista());
        Transportista transportista = new Transportista();

        try {
            if (transportistaOptional.isPresent()) {
                transportista = transportistaOptional.get();
            } else {
                throw new NullPointerException("Transportista no encontrado");

            }
            if (certificadoDTO.getAnio() < 2010 || certificadoDTO.getAnio() > 2100) {
                throw new IllegalArgumentException("Año no válidos");

            }
            if (certificadoDTO.getMes() == null) {
                throw new NullPointerException("Mes no puede ser nulo");
            }

            //

          /*  Optional<Certificado> existingCertificado = certificadoService.findByTransportistaAndPeriodo(
                    transportista.getId_transportista(),
                    certificadoDTO.getAnio(),
                    certificadoDTO.getMes().getId()
            );

            if (existingCertificado.isPresent()) {
                throw new IllegalStateException("Mes y año ya registrados para el transportista");
            }
*/
            //
            Certificado certificado = Certificado.builder()
                    .mes(certificadoDTO.getMes())
                    .año(certificadoDTO.getAnio())
                    .transportista(transportista)
                    .build();
            certificadoService.save(certificado);

            List<Ticket_control> listaTicketsPeriodo = ticketControlService.findTicketsByPeriodo(certificadoDTO.getAnio(), certificadoDTO.getMes().getId(), transportista.getId_transportista());
            for (Ticket_control ticket : listaTicketsPeriodo) {

                ticket.setCertificado(certificado);
                ticketControlService.save(ticket);
            }


            URI location = URI.create("/api/Certificado/crear");

            //Respuesta JSON
            Map<String, String> response= new HashMap<>();
            response.put("message","Certificado creado");

            return ResponseEntity.created(location).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error",e.getMessage()));
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error",e.getMessage()));
        }


    }


        @DeleteMapping("/eliminar/{id}")
    public ResponseEntity <?> deleteCertificado(@PathVariable Long id){
            try {
                if(id==null){
                    return ResponseEntity.badRequest().body("Es necesario indicar el Certificado a eliminar");

                }


                Optional<Certificado> certificadoOptional=certificadoService.findByID(id);

                if(certificadoOptional.isPresent()){
                    Certificado certificado=certificadoOptional.get();

                    for(Ticket_control ticketControl: certificado.getListaTickets()){
                        ticketControl.setCertificado(null);
                        ticketControlService.save(ticketControl);
                    }

                    certificadoService.deletebyID(id);

                    return  ResponseEntity.ok("Ha sido ELiminado El certificado");
                }else {
                    return ResponseEntity.badRequest().body("El certificado con el ID proporcionado no existe");
                }

            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @PutMapping("/update/{id}")
    public ResponseEntity <?>  updateCertificado(@PathVariable Long id,@RequestBody CertificadoDTO certificadoDTO) {
            Optional<Transportista> transportistaOptional = transportistaService.findByID(certificadoDTO.getTransportista().getId_transportista());
            Optional<Certificado> certificadoOptional = certificadoService.findByID(id);
            try {
                if (certificadoOptional.isPresent()) {
                    Certificado certificado = certificadoOptional.get();

                    if (wouldViolateConstraint(certificadoDTO.getAnio(), certificadoDTO.getMes(), certificado.getTransportista().getId_transportista(), id)) {
                        throw new UniqueConstraintViolationException("Cambiar el año y el mes a un periodo aun no creado al Transportista.");
                    }
                    // Comprobar la violación de restricciones antes de la modificación:


                   if(certificado.getAño()!=certificadoDTO.getAnio() || certificado.getMes()!=certificadoDTO.getMes()|| certificado.getTransportista().getId_transportista()!=certificadoDTO.getTransportista().getId_transportista()){
                       certificado.setAño(certificadoDTO.getAnio());
                       certificado.setMes(certificadoDTO.getMes());

                       for(Ticket_control ticket: certificado.getListaTickets()){
                          ticket.setCertificado(null);
                       }
                   }
                    Transportista transportista = new Transportista();
                    if (transportistaOptional.isPresent()) {
                        transportista = transportistaOptional.get();
                        certificado.setTransportista(transportista);
                    }
                    certificadoService.save(certificado);

                    List<Ticket_control> listaTicketsPeriodo=ticketControlService.findTicketsByPeriodo(certificado.getAño(),certificado.getMes().getId(),transportista.getId_transportista());
                    for (Ticket_control ticket : listaTicketsPeriodo) {

                        ticket.setCertificado(certificado);
                        ticketControlService.save(ticket);
                    }

                    return ResponseEntity.ok("El Certificado con id: " + id + " ha sido modificado");
                } else {
                    throw new EntityNotFoundException("No se encontró el Certificado con id: " + id);
                }
            } catch (UniqueConstraintViolationException e) {
                return ResponseEntity.badRequest().body("La actualización del certificado no se pudo realizar debido a una restricción de la base de datos-\n"+e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Ocurrió un error al actualizar el certificado: " + e.getMessage());
            }
        }
    private boolean wouldViolateConstraint(int año, Meses mesId, Long transportistaId, Long currentCertificadoId) {
        try {
            String sql = "SELECT COUNT(*) FROM certificado WHERE año = ? AND mes = ? AND id_transportista = ? AND id != ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, año, mesId.name(), transportistaId, currentCertificadoId) > 0;
        } catch (DataAccessException e) {
            throw new UniqueConstraintViolationException("Error al verificar la violación de restricciones únicas.", e);
        }


    }


    @GetMapping("/generadorPDF/{id}")
    public ResponseEntity<?> generatePdfCertificado(@PathVariable Long id) {
        try {
            Optional<Certificado> certificadoOptional = certificadoService.findByID(id);

            if (certificadoOptional.isPresent()) {
                String filePatch = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "templates" + File.separator + "certificadoReport.jrxml";

                Certificado certificado = certificadoOptional.get();

                Map<String, Object> parameters = new HashMap<>();

                String nombreCompletoTransportista = certificado.getTransportista().getNombre() + " " + certificado.getTransportista().getApellido();

                String periodo = certificado.getMes() + "/" + certificado.getAño();

                parameters.put("transportista_nombre", nombreCompletoTransportista);
                parameters.put("transportista_cuit", certificado.getTransportista().getCuit());
                parameters.put("transportista_direccion", certificado.getTransportista().getDomicilio());
                parameters.put("certificado_id", certificado.getId());
                parameters.put("certificado_periodo", periodo);
                parameters.put("firma_transportista", nombreCompletoTransportista);


                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                List<TicketsReport> listaTicketsReporte = certificado.getListaTickets().stream().map(
                        ticketControl -> TicketsReport.builder()
                                .id_ticket(ticketControlService.codificacionIdTicket(ticketControl.getId_Ticket()))
                                .generador_nombre(ticketControl.getGenerador().getNombre())
                                .peso(ticketControlService.pesoResiduosByTicket(ticketControl.getId_Ticket()).setScale(2, RoundingMode.HALF_UP))
                                .fechaEmision(dateFormat.format(java.sql.Date.valueOf(ticketControl.getFechaEmision()))
                                ).build()).toList();


                JRBeanCollectionDataSource residuosDataSource = new JRBeanCollectionDataSource(listaTicketsReporte);

                //JRBeanCollectionDataSource residuosDataSource=new JRBeanCollectionDataSource(ticketControl.getListaResiduos());
                parameters.put("ticketDateSet", residuosDataSource);

                JasperReport report = JasperCompileManager.compileReport(filePatch);
                JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

                // Exportar el PDF a un arreglo de bytes
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                JasperExportManager.exportReportToPdfStream(print, outputStream);
                byte[] pdfBytes = outputStream.toByteArray();

                // Configurar las cabeceras para la descarga del archivo
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Manifiesto-" + certificado.getId() + ".pdf");
                headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

            }

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
        return ResponseEntity.notFound().build();
}


    }

