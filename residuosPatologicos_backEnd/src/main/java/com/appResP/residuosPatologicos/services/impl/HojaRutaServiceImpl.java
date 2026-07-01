package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.response.HojaRutaDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaDetalleDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaReportRow;
import com.appResP.residuosPatologicos.DTO.response.TicketsReport;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.HojaRutaMapper;
import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.models.GeneradorAutonomo;
import com.appResP.residuosPatologicos.models.GeneradorEmpresa;
import com.appResP.residuosPatologicos.models.HojaRuta;
import com.appResP.residuosPatologicos.models.TicketControl;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.Vehiculo;
import com.appResP.residuosPatologicos.models.embeddables.Domicilio;
import com.appResP.residuosPatologicos.repository.ICertificadoRepository;
import com.appResP.residuosPatologicos.repository.IHojaRutaRepository;
import com.appResP.residuosPatologicos.repository.ITransportistaRepository;
import com.appResP.residuosPatologicos.repository.ITicketRepository;
import com.appResP.residuosPatologicos.services.IHojaRutaService;
import com.appResP.residuosPatologicos.services.TransportistaProfileService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JREmptyDataSource;
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
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional

public class HojaRutaServiceImpl implements IHojaRutaService {

    private static final DateTimeFormatter REPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final IHojaRutaRepository repo;
    private final ICertificadoRepository certificadoRepository;
    private final ITransportistaRepository transportistaRepository;
    private final ITicketRepository ticketRepository;
    private final TransportistaProfileService transportistaProfileService;
    private final HojaRutaMapper mapper;
    private final Clock clock;


    @Scheduled(cron = "0 0 0 * * MON")   // cada lunes 00:00
    @Scheduled(cron = "0 0 0 1 * *")     // cada 1° de mes 00:00
    @Override

    public void crearHojaRutaSemanal() {
        verificarYCrearHojaRutaSiEsNecesario();
    }

    @Override
    public void verificarYCrearHojaRutaSiEsNecesario() {
        transportistaRepository.findByEstadoTrue()
                .forEach(this::verificarYCrearHojaRutaSiEsNecesario);
    }

    @Override
    public void verificarYCrearHojaRutaSiEsNecesario(Transportista transportista) {
        if (transportista == null || transportista.getIdTransportista() == null) {
            return;
        }

        LocalDate hoy= LocalDate.now(clock);

        Long transportistaId = transportista.getIdTransportista();
        long siguienteNumero = repo.findMaxNumeroHojaRutaByTransportista(transportistaId) + 1;
        HojaRuta ultima = repo.findTopByTransportista_IdTransportistaOrderByFechaFinDesc(transportistaId)
                .orElse(null);

        //1) Si existe una hoja que todavia cubre "hoy" , no crea nada.

        if(ultima != null && !hoy.isAfter(ultima.getFechaFin())){
            return;
        }

        // Inicio = si no hay hojas, arrancamos desde el lunes actual (para tener un patrón limpio inicial)
        //         si hay hojas, arrancamos al día siguiente del fin

        LocalDate inicio = (ultima== null)
                ? hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : ultima.getFechaFin().plusDays(1);

        // Por si el server estuvo apagado, creamos las que falten hasta cubrir "hoy"

        while (true){
            LocalDate fin = calcularFin(inicio);

            //Solo creamos si no existe ya esa hoja
            if(!repo.existsByTransportista_IdTransportistaAndFechaInicio(transportistaId, inicio)){
                repo.save(HojaRuta.builder()
                        .fechaInicio(inicio)
                        .fechaFin(fin)
                        .numeroHojaRuta(siguienteNumero++)
                        .transportista(transportista)
                        .build()
                );
            }

            //si ya cubrimos hoy, terminamos
            if(!hoy.isAfter(fin)){
                return;
            }
            inicio=fin.plusDays(1);
        }

    }


    /**
     * Regla de negocio: una hoja nunca cruza de mes.
     * Si la semana estandar termina en otro mes, la hoja cierra el ultimo dia
     * del mes de inicio. La siguiente hoja comienza el dia 1 y llega hasta el
     * cierre estandar de esa semana.
     */

    LocalDate calcularFin(LocalDate inicio){
        LocalDate finSemana= inicio.with( TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        if(inicio.getMonth() != finSemana.getMonth()){
            return inicio.with(TemporalAdjusters.lastDayOfMonth());
        }
        return finSemana;
    }

    private void validarFechaInicio(LocalDate fechaInicio){
        if(fechaInicio.getDayOfWeek() != DayOfWeek.MONDAY
                && fechaInicio.getDayOfMonth() != 1){
            throw new IllegalArgumentException(
                    "fechaInicio debe ser lunes o el primer día del mes, fue: " + fechaInicio);

        }
    }
    @Override
    @Transactional(readOnly = true)
    public HojaRutaDTO findById(Long id) {
        HojaRuta hoja = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HojaRuta no encontrada: " + id));
        validarAccesoHoja(hoja);
        return mapper.toDTO(hoja);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HojaRutaDTO> findAll(Pageable pageable) {
        java.util.Optional<Transportista> transportista = authenticatedTransportistaIfPresent();

        if (transportista.isPresent()) {
            return repo.findByTransportista_IdTransportista(
                    transportista.get().getIdTransportista(),
                    pageable
            ).map(mapper::toDTO);
        }

        return repo.findAll(pageable).map(mapper::toDTO);
    }

    @Override
    @Transactional
    public HojaRutaDTO  findDTOByFecha(LocalDate fecha) {
        java.util.Optional<Transportista> transportista = authenticatedTransportistaIfPresent();

        if (transportista.isPresent()) {
            verificarYCrearHojaRutaSiEsNecesario(transportista.get());
            return repo.findByTransportista_IdTransportistaAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                            transportista.get().getIdTransportista(),
                            fecha,
                            fecha
                    )
                    .map(mapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No existe HojaRuta para el transportista autenticado en la fecha: " + fecha));
        }

        return repo.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(fecha, fecha)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe HojaRuta que contenga la fecha: " + fecha));
    }

    @Override
    @Transactional
    public HojaRutaDTO  findDTOForCurrentDate() {
        return findDTOByFecha(LocalDate.now(clock));
    }

    @Override
    @Transactional
    public HojaRutaDTO  findUltima() {
        java.util.Optional<Transportista> transportista = authenticatedTransportistaIfPresent();

        if (transportista.isPresent()) {
            verificarYCrearHojaRutaSiEsNecesario(transportista.get());
            return repo.findTopByTransportista_IdTransportistaOrderByFechaFinDesc(transportista.get().getIdTransportista())
                    .map(mapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No hay hojas de ruta registradas para el transportista autenticado."));
        }

        return repo.findTopByOrderByFechaFinDesc()
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay hojas de ruta registradas."));
    }

    @Override
    @Transactional(readOnly = true)
    public HojaRutaDetalleDTO findDetalleById(Long id) {
        HojaRuta hoja = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HojaRuta no encontrada: " + id));
        validarAccesoHoja(hoja);
        return mapper.toDetalleDTO(hoja);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HojaRutaDTO> findByCertificadoId(Long certificadoId) {
        Certificado certificado = certificadoRepository.findById(certificadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado: " + certificadoId));

        YearMonth periodo = YearMonth.of(certificado.getAnio(), certificado.getMes().getId());

        return repo.findDistinctByTransportistaAndFechaEmisionBetween(
                        certificado.getTransportista().getIdTransportista(),
                        periodo.atDay(1),
                        periodo.atEndOfMonth()
                )
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HojaRutaDTO> findPendientesCertificadoByTransportista(Long idTransportista) {
        Set<String> periodosCertificados = new HashSet<>();

        for (Certificado certificado : certificadoRepository.findByTransportista_IdTransportista(idTransportista)) {
            periodosCertificados.add(periodoKey(YearMonth.of(certificado.getAnio(), certificado.getMes().getId())));
        }

        return repo.findByTransportista_IdTransportistaOrderByFechaInicioDesc(idTransportista)
                .stream()
                .filter(hoja -> periodosDeHoja(hoja).stream()
                        .anyMatch(periodo -> !periodosCertificados.contains(periodoKey(periodo))))
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarInformePdf(Long id) {
        HojaRuta hoja = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HojaRuta no encontrada: " + id));
        validarAccesoHoja(hoja);
        List<TicketControl> tickets = ticketRepository.findByHojaRuta_Id(id, Pageable.unpaged()).getContent()
                .stream()
                .sorted(Comparator
                        .comparing(TicketControl::getFechaEmision)
                        .thenComparing(TicketControl::getHorario)
                        .thenComparing(TicketControl::getId))
                .toList();
        List<TicketControl> ticketsParaImprimir = tickets;
        Transportista transportista = hoja.getTransportista() != null
                ? hoja.getTransportista()
                : tickets.stream()
                .map(TicketControl::getTransportista)
                .findFirst()
                .orElse(null);

        List<HojaRutaReportRow> reportRows = toHojaRutaReportRows(ticketsParaImprimir);
        if (reportRows.isEmpty()) {
            reportRows = List.of(HojaRutaReportRow.builder().build());
        }

        Vehiculo vehiculo = vehiculoPrincipal(transportista);

        Map<String, Object> params = new HashMap<>();
        params.put("empresa_nombre", nombreEmpresaTransportista(transportista));
        params.put("transportista_nombre", nombreTransportista(transportista));
        params.put("transportista_documento", documentoTransportista(transportista));
        params.put("transportista_direccion", domicilioTexto(transportista != null ? transportista.getDomicilio() : null));
        params.put("hoja_ruta_id", numeroHojaRutaValor(hoja));
        params.put("fecha_emision", fechaTexto(hoja.getFechaInicio()));
        params.put("periodo_visita", fechaTexto(hoja.getFechaInicio()) + " / " + fechaTexto(hoja.getFechaFin()));
        params.put("cantidad_visitas", String.valueOf(ticketsParaImprimir.size()));
        params.put("vehiculo_marca", vehiculo != null ? vehiculo.getMarca() : "-");
        params.put("vehiculo_modelo", vehiculo != null ? vehiculo.getModelo() : "-");
        params.put("vehiculo_patente", vehiculo != null ? vehiculo.getDominio() : "-");
        params.put("firma_transportista", nombreTransportista(transportista));

        try (InputStream template = new ClassPathResource("templates/hojaRutaReport.jrxml").getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(report, params, new JRBeanCollectionDataSource(reportRows));
            return JasperExportManager.exportReportToPdf(print);
        } catch (IOException | JRException e) {
            throw new IllegalStateException("No se pudo generar el informe PDF de la hoja de ruta.", e);
        }
    }

    //Consultas
    @Transactional(readOnly = true)
    public java.util.Optional <HojaRuta> findHojaRutaPorFecha(LocalDate fecha){
        java.util.Optional<Transportista> transportista = authenticatedTransportistaIfPresent();

        if (transportista.isPresent()) {
            return repo.findByTransportista_IdTransportistaAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                    transportista.get().getIdTransportista(),
                    fecha,
                    fecha
            );
        }

        return  repo.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(fecha, fecha);

    }
    @Transactional(readOnly = true)
    public java.util.Optional<HojaRuta> findHojaRutaForCurrentDate() {
        return findHojaRutaPorFecha(LocalDate.now(clock));
    }

    private List<YearMonth> periodosDeHoja(HojaRuta hoja) {
        List<YearMonth> periodos = new ArrayList<>();
        YearMonth actual = YearMonth.from(hoja.getFechaInicio());
        YearMonth fin = YearMonth.from(hoja.getFechaFin());

        while (!actual.isAfter(fin)) {
            periodos.add(actual);
            actual = actual.plusMonths(1);
        }

        return periodos;
    }

    private String periodoKey(YearMonth periodo) {
        return periodo.getYear() + "-" + periodo.getMonthValue();
    }

    private TicketsReport toTicketsReport(TicketControl ticket) {
        return TicketsReport.builder()
                .id_ticket(String.valueOf(ticket.getId()))
                .generador_nombre(nombreGenerador(ticket.getGenerador()))
                .fechaEmision(String.valueOf(ticket.getFechaEmision()))
                .peso(ticket.getPesoTotal())
                .build();
    }

    private List<HojaRutaReportRow> toHojaRutaReportRows(List<TicketControl> tickets) {
        List<HojaRutaReportRow> rows = new ArrayList<>();

        for (int i = 0; i < tickets.size(); i++) {
            TicketControl ticket = tickets.get(i);
            Generador generador = ticket.getGenerador();
            Domicilio domicilio = generador != null ? generador.getDomicilio() : null;

            rows.add(HojaRutaReportRow.builder()
                    .nro(String.valueOf(i + 1))
                    .generadorNombre(nombreGenerador(generador))
                    .generadorDomicilio(domicilioCalleAltura(domicilio))
                    .generadorLocalidad(localidadTexto(domicilio))
                    .fechaVisita(fechaTexto(ticket.getFechaEmision()))
                    .comprobante(numeroTicketTexto(ticket))
                    .build());
        }

        return rows;
    }

    private Vehiculo vehiculoPrincipal(Transportista transportista) {
        if (transportista == null || transportista.getVehiculos() == null) {
            return null;
        }

        return transportista.getVehiculos().stream()
                .filter(Vehiculo::isActivo)
                .min(Comparator.comparing(Vehiculo::getId))
                .orElseGet(() -> transportista.getVehiculos().stream()
                        .min(Comparator.comparing(Vehiculo::getId))
                        .orElse(null));
    }

    private String nombreTransportista(Transportista transportista) {
        if (transportista == null) {
            return "-";
        }

        return (transportista.getNombre() + " " + transportista.getApellido()).trim();
    }

    private String nombreEmpresaTransportista(Transportista transportista) {
        if (transportista == null) {
            return "-";
        }

        if (transportista.getNombreFantasia() != null && !transportista.getNombreFantasia().isBlank()) {
            return transportista.getNombreFantasia();
        }

        return nombreTransportista(transportista);
    }

    private String documentoTransportista(Transportista transportista) {
        if (transportista == null) {
            return "-";
        }

        if (transportista.getCuit() != null && !transportista.getCuit().isBlank()) {
            return transportista.getCuit();
        }

        return transportista.getCuil() != null && !transportista.getCuil().isBlank()
                ? transportista.getCuil()
                : "-";
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

    private String domicilioTexto(Domicilio domicilio) {
        if (domicilio == null) {
            return "-";
        }

        return Arrays.asList(
                        domicilio.getCalle(),
                        domicilio.getAltura() != null ? String.valueOf(domicilio.getAltura()) : null,
                        domicilio.getLocalidad(),
                        domicilio.getProvincia()
                )
                .stream()
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse("-");
    }

    private String domicilioCalleAltura(Domicilio domicilio) {
        if (domicilio == null) {
            return "-";
        }

        String calleAltura = Arrays.asList(
                        domicilio.getCalle(),
                        domicilio.getAltura() != null ? String.valueOf(domicilio.getAltura()) : null,
                        domicilio.getDepartamento() != null ? "Dto. " + domicilio.getDepartamento() : null
                )
                .stream()
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + " " + right)
                .orElse(null);

        if (calleAltura != null && !calleAltura.isBlank()) {
            return calleAltura;
        }

        return domicilio.getBarrio() != null && !domicilio.getBarrio().isBlank()
                ? domicilio.getBarrio()
                : "-";
    }

    private String localidadTexto(Domicilio domicilio) {
        if (domicilio == null || domicilio.getLocalidad() == null || domicilio.getLocalidad().isBlank()) {
            return "-";
        }

        return domicilio.getLocalidad();
    }

    private String fechaTexto(LocalDate fecha) {
        return fecha != null ? fecha.format(REPORT_DATE_FORMAT) : "-";
    }

    private String numeroHojaRutaTexto(HojaRuta hoja) {
        if (hoja == null) {
            return "-";
        }

        return hoja.getNumeroHojaRuta() != null
                ? String.valueOf(hoja.getNumeroHojaRuta())
                : String.valueOf(hoja.getId());
    }

    private Long numeroHojaRutaValor(HojaRuta hoja) {
        if (hoja == null) {
            return null;
        }

        return hoja.getNumeroHojaRuta() != null
                ? hoja.getNumeroHojaRuta()
                : hoja.getId();
    }

    private String numeroTicketTexto(TicketControl ticket) {
        if (ticket == null) {
            return "-";
        }

        return ticket.getNumeroTicket() != null
                ? String.valueOf(ticket.getNumeroTicket())
                : String.valueOf(ticket.getId());
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

    private void validarAccesoHoja(HojaRuta hoja) {
        java.util.Optional<Transportista> transportista = authenticatedTransportistaIfPresent();

        if (transportista.isEmpty()) {
            return;
        }

        Long hojaTransportistaId = hoja.getTransportista() != null
                ? hoja.getTransportista().getIdTransportista()
                : null;

        if (!transportista.get().getIdTransportista().equals(hojaTransportistaId)) {
            throw new ResourceNotFoundException("HojaRuta no encontrada: " + hoja.getId());
        }
    }
}
