package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.request.ResiduoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaDTO;
import com.appResP.residuosPatologicos.DTO.response.ResiduoDTO;
import com.appResP.residuosPatologicos.DTO.response.TicketResiduoReport;
import com.appResP.residuosPatologicos.DTO.response.TicketDTO;
import com.appResP.residuosPatologicos.DTO.request.TicketRequestDTO;
import com.appResP.residuosPatologicos.api.error.exceptions.DuplicateResourceException;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.ResiduoMapper;
import com.appResP.residuosPatologicos.mappers.TicketMapper;
import com.appResP.residuosPatologicos.models.*;
import com.appResP.residuosPatologicos.models.embeddables.Domicilio;
import com.appResP.residuosPatologicos.models.enums.Meses;
import com.appResP.residuosPatologicos.repository.*;
import com.appResP.residuosPatologicos.services.IHojaRutaService;
import com.appResP.residuosPatologicos.services.ITicketService;
import com.appResP.residuosPatologicos.services.TransportistaProfileService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {
    private final ITicketRepository ticketRepository;
    private final ITransportistaRepository transportistaRepository;
    private final IHojaRutaRepository hojaRutaRepository;
    private final IGeneradorRepository generadorRepository;
    private final ITipoResiduoRepository tipoResiduoRepository;
    private final ICertificadoRepository certificadoRepository;
    private final IHojaRutaService hojaRutaService;
    private final TransportistaProfileService transportistaProfileService;
    private final ResiduoMapper residuoMapper;
    private final TicketMapper ticketMapper;

    @Override
    public TicketDTO findById(Long id) {
        return ticketRepository.findById(id)
                .map(ticketMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado: " + id));

    }

    @Override
    public Page<TicketDTO> findAll(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(ticketMapper::toListDTO);

    }


    @Override
    public TicketDTO create(TicketRequestDTO dto) {

        Transportista t = transportistaRepository.findById(dto.getTransportistaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transportista no encontrado: " + dto.getTransportistaId()));

        HojaRuta hr = hojaRutaRepository.findById(dto.getHojaRutaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "HojaRuta no encontrada: " + dto.getHojaRutaId()));
        validarHojaPerteneceAlTransportista(hr, t);

        Generador g = generadorRepository.findById(dto.getGeneradorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Generador no encontrado: " + dto.getGeneradorId()));

        TicketControl ticket = ticketMapper.toEntity(dto, t, hr, g);
        ticket.setNumeroTicket(nextNumeroTicket(t));
        validarFechaEmisionEnHojaRuta(ticket.getFechaEmision(), hr);
        validarPeriodoNoCertificado(t, ticket.getFechaEmision());

        for (ResiduoRequestDTO rDto : dto.getResiduos()) {
            TipoResiduo tipo = tipoResiduoRepository.findById(rDto.getTipoResiduoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "TipoResiduo no encontrado: " + rDto.getTipoResiduoId()));
            validarTipoResiduoPerteneceAlTransportista(tipo, t);
            ticket.addResiduo(residuoMapper.toEntity(tipo, rDto.getPeso()));
        }

        ticket.recalcularPesoTotal();

        return ticketMapper.toDTO(ticketRepository.save(ticket));
    }

    @Override
    public TicketDTO update(long id, TicketRequestDTO request) {
        TicketControl ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado: " + id));
        validarTicketEditable(ticket);

        ticket.setGenerador(generadorRepository.findById(request.getGeneradorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Generador no encontrado: " + request.getGeneradorId())));

        if (request.getFechaEmision() != null) ticket.setFechaEmision(request.getFechaEmision());
        if (request.getEstado() != null) ticket.setEstado(request.getEstado());

        validarFechaEmisionEnHojaRuta(ticket.getFechaEmision(), ticket.getHojaRuta());
        validarPeriodoNoCertificado(ticket.getTransportista(), ticket.getFechaEmision());

        aplicarResiduosReemplazo(ticket, request.getResiduos());

        return ticketMapper.toDTO(ticketRepository.save(ticket));
    }


    @Override
    public void deletebyId(Long id) {
        TicketControl ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado: " + id));
        validarTicketEditable(ticket);
        ticketRepository.delete(ticket);
    }

    @Override
    public List<TicketDTO> findTicketsByPeriodo(int anio, int mes, Long idTransportista) {
        return ticketRepository.findTicketsByPeriodo(anio, mes, idTransportista).stream()
                .map(ticketMapper::toDTO)
                .toList();
    }

    @Override
    public Page<TicketDTO> findByHojaRutaId(Long hojaRutaId, Pageable pageable) {
        HojaRuta hojaRuta = hojaRutaRepository.findById(hojaRutaId)
                .orElseThrow(() -> new ResourceNotFoundException("HojaRuta no encontrada: " + hojaRutaId));
        validarAccesoHojaComoTransportista(hojaRuta);

        return ticketRepository.findByHojaRuta_Id(hojaRutaId, pageable)
                .map(ticketMapper::toListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarManifiestoPdf(Long id) {
        TicketControl ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado: " + id));
        Transportista transportista = ticket.getTransportista();
        Generador generador = ticket.getGenerador();

        Map<String, Object> params = new HashMap<>();
        List<TicketResiduoReport> residuosReport = ticket.getListaResiduos().stream()
                .map(this::toResiduoReport)
                .toList();

        params.put("comprobante_numero", numeroTicketTexto(ticket));
        params.put("marca_transportista", marcaTransportista(transportista));
        params.put("imgTransportista", null);
        params.put("transportista_nombre", nombreTransportista(transportista));
        params.put("transportista_nombre_fantasia", nombreFantasiaTransportista(transportista));
        params.put("transportista_documento", documentoTransportista(transportista));
        params.put("transportista_domicilio", domicilioLinea(transportista != null ? transportista.getDomicilio() : null));
        params.put("transportista_telefono", transportista != null && transportista.getTelefono() != null ? transportista.getTelefono() : "-");
        params.put("transportista_localidad_provincia", localidadProvinciaTexto(transportista != null ? transportista.getDomicilio() : null));
        params.put("generador_tipo", tipoGeneradorTexto(generador));
        params.put("generador_nombre", nombreGenerador(generador));
        params.put("generador_nombre_fantasia", nombreFantasiaGenerador(generador));
        params.put("generador_documento_label", documentoGeneradorLabel(generador));
        params.put("generador_documento", documentoGenerador(generador));
        params.put("generador_domicilio", domicilioLinea(generador != null ? generador.getDomicilio() : null));
        params.put("generador_localidad_provincia", localidadProvinciaTexto(generador != null ? generador.getDomicilio() : null));
        params.put("fecha_emision", String.valueOf(ticket.getFechaEmision()));
        params.put("pesoTotal", ticket.getPesoTotal() != null ? ticket.getPesoTotal() : BigDecimal.ZERO);
        params.put("firma_generador", nombreGenerador(generador));
        params.put("firma_transportista", nombreTransportista(transportista));

        try (InputStream template = new ClassPathResource("templates/ticketResiduo.jrxml").getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(report, params, new JRBeanCollectionDataSource(residuosReport));
            return JasperExportManager.exportReportToPdf(print);
        } catch (IOException | JRException e) {
            throw new IllegalStateException("No se pudo generar el manifiesto PDF.", e);
        }
    }

    @Override
    public TicketDTO actualizarEstado(Long id) {
        TicketControl ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado: " + id));
        validarTicketEditable(ticket);
        ticket.setEstado(true);
        return ticketMapper.toDTO(ticketRepository.save(ticket));
    }

    @Override
    @Scheduled(cron = "0 5 0 * * *")
    public int procesarTicketsDeHojasVencidas() {
        List<TicketControl> tickets = ticketRepository
                .findByEstadoFalseAndHojaRuta_FechaFinBefore(LocalDate.now());

        tickets.forEach(ticket -> ticket.setEstado(true));
        ticketRepository.saveAll(tickets);

        return tickets.size();
    }


    @Override
    public Page<TicketDTO> findTicketsDeHojaActual(Pageable pageable) {

        // findDTOForCurrentDate ya lanza ResourceNotFoundException si no hay hoja vigente
        HojaRutaDTO hojaActual = hojaRutaService.findDTOForCurrentDate();
        return findByHojaRutaId(hojaActual.getId(), pageable);

    }

    @Override
    public ResiduoDTO agregarResiduo(Long ticketId, ResiduoRequestDTO dto) {
        TicketControl ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()->new ResourceNotFoundException("Ticket no encontrado:" + ticketId));
        validarTicketEditable(ticket);

        //validar que no exista ya ese tipo de residuo (por UNIQUE constraint)
        boolean yaExiste= ticket.getListaResiduos().stream()
                .anyMatch(r -> r.getTipoResiduo().getId().equals(dto.getTipoResiduoId()));

        if( yaExiste){
            throw  new DuplicateResourceException("Ya existe un residuo de tipo "+ dto.getTipoResiduoId() + " en este ticket");
        }
        TipoResiduo tipo= tipoResiduoRepository.findById(dto.getTipoResiduoId())
                .orElseThrow(()-> new ResourceNotFoundException("TipoResiduo no encontrado: " + dto.getTipoResiduoId()));
        validarTipoResiduoPerteneceAlTransportista(tipo, ticket.getTransportista());


        Residuo residuo = residuoMapper.toEntity(tipo, dto.getPeso());

        ticket.addResiduo(residuo); // Mantiene bidireccionalidad
        ticket.recalcularPesoTotal(); // Recalcula peso

        ticketRepository.save(ticket);


        return residuoMapper.toDTO(residuo);
    }

    @Override
    public void eliminarResiduo(Long ticketId, Long residuoId) {
    TicketControl ticket = ticketRepository.findById(ticketId)
            .orElseThrow(()-> new ResourceNotFoundException("Ticket no encontrado: " + ticketId));
        validarTicketEditable(ticket);

        Residuo residuo = ticket.getListaResiduos().stream()
                .filter(r -> r.getId().equals(residuoId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Residuo " + residuoId + " no pertenece al ticket " + ticketId
                ));

        ticket.removeResiduo(residuo); // Mantiene bidireccionalidad
       ticket.recalcularPesoTotal();
        ticket.recalcularPesoTotal(); // Recalcula peso

        ticketRepository.save(ticket);
    }

    @Override
    public List<ResiduoDTO> obtenerResiduos(Long ticketId) {
        TicketControl ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket no encontrado: " + ticketId));
        return ticket.getListaResiduos().stream()
                .map(residuoMapper::toDTO)
                .toList();
    }


    /* =========================================================
   MERGE RESIDUOS (modo REEMPLAZO, ideal para PUT)
   - Consolida duplicados en request (suma pesos por tipo)
   - Upsert por tipoResiduoId
   - Elimina los que no vinieron
   ========================================================= */
    private void aplicarResiduosReemplazo(TicketControl ticket, List<ResiduoRequestDTO> requestResiduos){
        if(requestResiduos == null) requestResiduos= List.of();

        //1) Consolidar duplicados del request por tipoResiduoId (Suma)
        Map<Long, BigDecimal> pesoPorTipo= new HashMap<>();

        for(ResiduoRequestDTO dto: requestResiduos) {
            if (dto == null || dto.getTipoResiduoId() == null) {
                throw new IllegalArgumentException("tipoResiduoId es obligatorio en residuos");
            }
            if (dto.getPeso() == null || dto.getPeso().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("peso inválido para tipoResiduoId=" + dto.getTipoResiduoId());
            }
            pesoPorTipo.merge(dto.getTipoResiduoId(), dto.getPeso(), BigDecimal::add);
        }
            //2) indexar existentes por tipoResiduoId
            Map<Long, Residuo> existentes  = ticket.getListaResiduos().stream()
                    .filter(r -> r.getTipoResiduo() != null && r.getTipoResiduo().getId() != null)
                    .collect(Collectors.toMap(r-> r.getTipoResiduo().getId(), r -> r));

            //3) Insertar

            for(var entry : pesoPorTipo.entrySet()){
                Long tipoId= entry.getKey();
                BigDecimal nuevoPeso = entry.getValue();

                Residuo actual= existentes.get(tipoId);
                if(actual !=null){
                    actual.setPeso(nuevoPeso);
                } else{
                    TipoResiduo tipo= tipoResiduoRepository.findById(tipoId)
                            .orElseThrow(()-> new ResourceNotFoundException("Tipo Residuo no encontrado: " + tipoId));
                    validarTipoResiduoPerteneceAlTransportista(tipo, ticket.getTransportista());

                    ticket.addResiduo(Residuo.builder()
                            .tipoResiduo(tipo)
                            .peso(nuevoPeso)
                            .build());
                }
            }

        // 4) ⚠️ ELIMINAR los que NO vinieron en el request (ESTO FALTABA)
        List<Residuo> aEliminar = ticket.getListaResiduos().stream()
                .filter(r -> !pesoPorTipo.containsKey(r.getTipoResiduo().getId()))
                .toList();

        for (Residuo r : aEliminar) {
            ticket.removeResiduo(r); // orphanRemoval = true lo eliminará de BD
        }

        // 5) Recalcular peso total final
        ticket.recalcularPesoTotal();
        }

    private void validarFechaEmisionEnHojaRuta(LocalDate fechaEmision, HojaRuta hojaRuta) {
        if (fechaEmision == null || hojaRuta == null) {
            return;
        }

        boolean fueraDePeriodo = fechaEmision.isBefore(hojaRuta.getFechaInicio())
                || fechaEmision.isAfter(hojaRuta.getFechaFin());

        if (fueraDePeriodo) {
            throw new IllegalArgumentException(
                    "La fecha de emision del ticket debe estar dentro del periodo de la hoja de ruta."
            );
        }
    }

    private void validarTicketEditable(TicketControl ticket) {
        if (periodoCertificado(ticket.getTransportista(), ticket.getFechaEmision())) {
            throw new IllegalStateException(
                    "No se puede modificar un ticket de un periodo certificado."
            );
        }
    }

    private void validarPeriodoNoCertificado(Transportista transportista, LocalDate fechaEmision) {
        if (periodoCertificado(transportista, fechaEmision)) {
            throw new IllegalStateException(
                    "No se pueden crear o modificar tickets dentro de un periodo certificado."
            );
        }
    }

    private boolean periodoCertificado(Transportista transportista, LocalDate fechaEmision) {
        if (transportista == null || fechaEmision == null) {
            return false;
        }

        return certificadoRepository.existsByTransportistaAndMesAndAnio(
                transportista,
                Meses.fromId(fechaEmision.getMonthValue()),
                fechaEmision.getYear()
        );
    }

    private long nextNumeroTicket(Transportista transportista) {
        Long transportistaId = transportista != null ? transportista.getIdTransportista() : null;

        if (transportistaId == null) {
            throw new IllegalArgumentException("El transportista es obligatorio para numerar el ticket.");
        }

        return ticketRepository.findMaxNumeroTicketByTransportista(transportistaId) + 1;
    }

    private String numeroTicketTexto(TicketControl ticket) {
        if (ticket == null) {
            return "-";
        }

        return ticket.getNumeroTicket() != null
                ? String.valueOf(ticket.getNumeroTicket())
                : String.valueOf(ticket.getId());
    }

    private void validarHojaPerteneceAlTransportista(HojaRuta hojaRuta, Transportista transportista) {
        Long hojaTransportistaId = hojaRuta != null && hojaRuta.getTransportista() != null
                ? hojaRuta.getTransportista().getIdTransportista()
                : null;
        Long transportistaId = transportista != null ? transportista.getIdTransportista() : null;

        if (hojaTransportistaId == null || !hojaTransportistaId.equals(transportistaId)) {
            throw new IllegalArgumentException("La hoja de ruta seleccionada no pertenece al transportista.");
        }
    }

    private void validarTipoResiduoPerteneceAlTransportista(TipoResiduo tipoResiduo, Transportista transportista) {
        Long tipoTransportistaId = tipoResiduo != null && tipoResiduo.getTransportista() != null
                ? tipoResiduo.getTransportista().getIdTransportista()
                : null;
        Long transportistaId = transportista != null ? transportista.getIdTransportista() : null;

        if (tipoTransportistaId == null || !tipoTransportistaId.equals(transportistaId)) {
            throw new IllegalArgumentException("El tipo de residuo seleccionado no pertenece al transportista.");
        }
    }

    private void validarAccesoHojaComoTransportista(HojaRuta hojaRuta) {
        java.util.Optional<Transportista> transportista = authenticatedTransportistaIfPresent();

        if (transportista.isEmpty()) {
            return;
        }

        validarHojaPerteneceAlTransportista(hojaRuta, transportista.get());
    }

    private java.util.Optional<Transportista> authenticatedTransportistaIfPresent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return java.util.Optional.empty();
        }

        boolean esTransportista = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_TRANSPORTISTA".equals(authority.getAuthority()));

        if (!esTransportista) {
            return java.util.Optional.empty();
        }

        return java.util.Optional.of(transportistaProfileService.getAuthenticatedTransportista());
    }

    private TicketResiduoReport toResiduoReport(Residuo residuo) {
        TipoResiduo tipoResiduo = residuo.getTipoResiduo();

        return TicketResiduoReport.builder()
                .codigo(tipoResiduo != null ? tipoResiduo.getCodigo() : "-")
                .nombre(tipoResiduo != null ? tipoResiduo.getNombre() : "-")
                .peso(residuo.getPeso() != null ? residuo.getPeso() : BigDecimal.ZERO)
                .build();
    }

    private String marcaTransportista(Transportista transportista) {
        String nombreFantasia = nombreFantasiaTransportista(transportista);
        return !"-".equals(nombreFantasia) ? nombreFantasia : nombreTransportista(transportista);
    }

    private String nombreFantasiaTransportista(Transportista transportista) {
        if (transportista != null
                && transportista.getNombreFantasia() != null
                && !transportista.getNombreFantasia().isBlank()) {
            return transportista.getNombreFantasia();
        }

        return "-";
    }

    private String documentoTransportista(Transportista transportista) {
        if (transportista == null) {
            return "-";
        }

        if (transportista.getCuil() != null && !transportista.getCuil().isBlank()) {
            return transportista.getCuil();
        }

        return transportista.getCuit() != null && !transportista.getCuit().isBlank()
                ? transportista.getCuit()
                : "-";
    }

    private String nombreTransportista(Transportista transportista) {
        if (transportista == null) {
            return "-";
        }

        return (transportista.getNombre() + " " + transportista.getApellido()).trim();
    }

    private String nombreGenerador(Generador generador) {
        if (generador instanceof GeneradorEmpresa empresa) {
            if (empresa.getRazonSocial() != null && !empresa.getRazonSocial().isBlank()) {
                return empresa.getRazonSocial();
            }

            if (empresa.getNombreFantasia() != null && !empresa.getNombreFantasia().isBlank()) {
                return empresa.getNombreFantasia();
            }
        }

        if (generador instanceof GeneradorAutonomo autonomo) {
            String nombre = ((autonomo.getNombre() != null ? autonomo.getNombre() : "") + " "
                    + (autonomo.getApellido() != null ? autonomo.getApellido() : "")).trim();

            if (!nombre.isBlank()) {
                return nombre;
            }
        }

        return generador != null ? "Generador #" + generador.getId() : "-";
    }

    private String tipoGeneradorTexto(Generador generador) {
        if (generador instanceof GeneradorEmpresa) {
            return "Empresa";
        }

        if (generador instanceof GeneradorAutonomo) {
            return "Autonomo";
        }

        return "-";
    }

    private String nombreFantasiaGenerador(Generador generador) {
        if (generador instanceof GeneradorEmpresa empresa
                && empresa.getNombreFantasia() != null
                && !empresa.getNombreFantasia().isBlank()) {
            return empresa.getNombreFantasia();
        }

        return "-";
    }

    private String documentoGeneradorLabel(Generador generador) {
        if (generador instanceof GeneradorEmpresa) {
            return "CUIT";
        }

        if (generador instanceof GeneradorAutonomo) {
            return "CUIL";
        }

        return "CUIT/CUIL";
    }

    private String documentoGenerador(Generador generador) {
        if (generador instanceof GeneradorEmpresa empresa) {
            return empresa.getCuit() != null ? empresa.getCuit() : "-";
        }

        if (generador instanceof GeneradorAutonomo autonomo) {
            return autonomo.getCuil() != null ? autonomo.getCuil() : "-";
        }

        return "-";
    }

    private String domicilioLinea(Domicilio domicilio) {
        if (domicilio == null) {
            return "-";
        }

        String calleAltura = java.util.stream.Stream.of(
                        domicilio.getCalle(),
                        domicilio.getAltura() != null ? String.valueOf(domicilio.getAltura()) : null
                )
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + " " + right)
                .orElse(null);

        String linea = java.util.stream.Stream.of(
                        domicilio.getBarrio() != null && !domicilio.getBarrio().isBlank()
                                ? "Barrio " + domicilio.getBarrio()
                                : null,
                        calleAltura,
                        domicilio.getDepartamento() != null && !domicilio.getDepartamento().isBlank()
                                ? "Depto. " + domicilio.getDepartamento()
                                : null
                )
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse("-");

        return linea;
    }

    private String localidadProvinciaTexto(Domicilio domicilio) {
        if (domicilio == null) {
            return "-";
        }

        return java.util.stream.Stream.of(domicilio.getLocalidad(), domicilio.getProvincia())
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse("-");
    }

    }
