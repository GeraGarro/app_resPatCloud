package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.response.CertificadoDTO;
import com.appResP.residuosPatologicos.DTO.request.CertificadoRequestDTO;
import com.appResP.residuosPatologicos.DTO.response.HojaRutaConTicketsDTO;
import com.appResP.residuosPatologicos.DTO.response.TicketsReport;
import com.appResP.residuosPatologicos.api.error.exceptions.DuplicateResourceException;
import com.appResP.residuosPatologicos.api.error.exceptions.ResourceNotFoundException;
import com.appResP.residuosPatologicos.mappers.CertificadoMapper;
import com.appResP.residuosPatologicos.mappers.TicketMapper;
import com.appResP.residuosPatologicos.models.Certificado;
import com.appResP.residuosPatologicos.models.Generador;
import com.appResP.residuosPatologicos.models.GeneradorAutonomo;
import com.appResP.residuosPatologicos.models.GeneradorEmpresa;
import com.appResP.residuosPatologicos.models.HojaRuta;
import com.appResP.residuosPatologicos.models.TicketControl;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.models.embeddables.Domicilio;
import com.appResP.residuosPatologicos.models.enums.Meses;
import com.appResP.residuosPatologicos.repository.ICertificadoRepository;
import com.appResP.residuosPatologicos.repository.ITicketRepository;
import com.appResP.residuosPatologicos.repository.ITransportistaRepository;
import com.appResP.residuosPatologicos.services.ICertificadoService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificadoServiceImpl implements ICertificadoService {

    private final ICertificadoRepository certificadoRepo;
    private final ITransportistaRepository transportistaRepo;
    private final CertificadoMapper certificadoMapper;
    private final ITicketRepository ticketRepository;
    private final TicketMapper ticketMapper;


    @Override
    @Transactional(readOnly = true)
    public CertificadoDTO findById(Long id) {
        Certificado c = certificadoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado: " + id));
        return certificadoMapper.toDTO(c);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CertificadoDTO> findAll(Pageable pageable) {
        return certificadoRepo.findAll(pageable).map(certificadoMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CertificadoDTO> findByTransportista(Long idTransportista, Pageable pageable) {
        return certificadoRepo.findByTransportista_IdTransportista(idTransportista, pageable)
                .map(certificadoMapper::toDTO);
    }


    @Override
    public CertificadoDTO update(Long id, CertificadoRequestDTO certificado) {

        Certificado actual = certificadoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado: " + id));

        Transportista t = transportistaRepo.findById(certificado.getTransportistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado " + id));

        int anio = certificado.getAnio();

        if (certificadoRepo.existsByTransportistaAndMesAndAnioAndIdNot(t, certificado.getMes(), anio, id)) {
            throw new DuplicateResourceException("Cambiar el año/ mes violria la restricción unica.");
        }

        actual.setTransportista(t);
        actual.setMes(certificado.getMes());
        actual.setAnio(anio);

        return certificadoMapper.toDTO(certificadoRepo.save(actual));
    }


    @Override
    public void deleteById(Long id) {
        Certificado c = certificadoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado: " + id));

        // Desvinculamos las hojas antes de eliminar para no dejar FK huérfanas
        certificadoRepo.delete(c);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarCertificadoPdf(Long id) {
        Certificado certificado = certificadoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado: " + id));
        Transportista transportista = certificado.getTransportista();
        YearMonth periodo = YearMonth.of(certificado.getAnio(), certificado.getMes().getId());
        List<TicketControl> tickets = ticketRepository.findByTransportistaIdTransportistaAndFechaEmisionBetween(
                transportista.getIdTransportista(),
                periodo.atDay(1),
                periodo.atEndOfMonth()
        );

        List<TicketsReport> reportRows = tickets.stream()
                .filter(TicketControl::isEstado)
                .sorted(Comparator
                        .comparing((TicketControl ticket) -> ticket.getHojaRuta() != null ? ticket.getHojaRuta().getId() : Long.MAX_VALUE)
                        .thenComparing(TicketControl::getFechaEmision)
                        .thenComparing(TicketControl::getId))
                .map(this::toTicketsReport)
                .toList();

        Map<String, Object> params = new HashMap<>();
        params.put("transportista_nombre", nombreTransportista(transportista));
        params.put("transportista_cuil", documentoTransportista(transportista));
        params.put("transportista_nombre_fantasia", nombreFantasiaTransportista(transportista));
        params.put("transportista_direccion", domicilioTexto(transportista != null ? transportista.getDomicilio() : null));
        params.put("certificado_id", numeroCertificadoValor(certificado));
        params.put("certificado_periodo", certificado.getMes().name() + " " + certificado.getAnio());
        params.put("firma_transportista", nombreTransportista(transportista));

        try (InputStream template = new ClassPathResource("templates/certificadoReport.jrxml").getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(report, params, new JRBeanCollectionDataSource(reportRows));
            return JasperExportManager.exportReportToPdf(print);
        } catch (IOException | JRException e) {
            throw new IllegalStateException("No se pudo generar el certificado PDF.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HojaRutaConTicketsDTO> findHojasConTickets(Long id) {
        Certificado certificado = certificadoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificado no encontrado: " + id));

        Transportista transportista = certificado.getTransportista();
        if (transportista == null || transportista.getIdTransportista() == null) {
            return List.of();
        }

        YearMonth periodo = YearMonth.of(certificado.getAnio(), certificado.getMes().getId());
        List<TicketControl> tickets = ticketRepository.findByTransportistaIdTransportistaAndFechaEmisionBetween(
                transportista.getIdTransportista(),
                periodo.atDay(1),
                periodo.atEndOfMonth()
        ).stream()
                .sorted(Comparator
                        .comparing((TicketControl ticket) -> {
                            HojaRuta hoja = ticket.getHojaRuta();
                            return hoja != null && hoja.getFechaInicio() != null
                                    ? hoja.getFechaInicio()
                                    : LocalDate.MAX;
                        })
                        .thenComparing(TicketControl::getFechaEmision)
                        .thenComparing(TicketControl::getId))
                .toList();

        Map<Long, HojaRutaConTicketsDTO> hojas = new LinkedHashMap<>();
        Map<Long, List<com.appResP.residuosPatologicos.DTO.response.TicketDTO>> ticketsPorHoja = new LinkedHashMap<>();

        for (TicketControl ticket : tickets) {
            HojaRuta hoja = ticket.getHojaRuta();
            if (hoja == null || hoja.getId() == null) {
                continue;
            }

            Long hojaId = hoja.getId();
            hojas.putIfAbsent(hojaId, HojaRutaConTicketsDTO.builder()
                    .id(hojaId)
                    .numeroHojaRuta(hoja.getNumeroHojaRuta())
                    .fechaInicio(hoja.getFechaInicio())
                    .fechaFin(hoja.getFechaFin())
                    .transportistaId(transportista.getIdTransportista())
                    .cantidadTickets(0)
                    .pesoTotal(BigDecimal.ZERO)
                    .tickets(List.of())
                    .build());

            ticketsPorHoja.computeIfAbsent(hojaId, ignored -> new ArrayList<>())
                    .add(ticketMapper.toDTO(ticket));
        }

        for (Map.Entry<Long, HojaRutaConTicketsDTO> entry : hojas.entrySet()) {
            List<com.appResP.residuosPatologicos.DTO.response.TicketDTO> ticketsDeHoja =
                    ticketsPorHoja.getOrDefault(entry.getKey(), List.of());
            BigDecimal pesoTotal = ticketsDeHoja.stream()
                    .map(ticket -> ticket.getPesoTotal() != null ? ticket.getPesoTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            entry.getValue().setTickets(ticketsDeHoja);
            entry.getValue().setCantidadTickets(ticketsDeHoja.size());
            entry.getValue().setPesoTotal(pesoTotal);
        }

        return List.copyOf(hojas.values());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CertificadoDTO> findByMesAndAnio(Meses mes, int anio, Pageable pageable) {
        return certificadoRepo.findByMesAndAnio(mes, anio, pageable)
                .map(certificadoMapper::toDTO);
    }


    @Override
    public CertificadoDTO create(CertificadoRequestDTO dto) {
        Transportista t = transportistaRepo.findById(dto.getTransportistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado: " + dto.getTransportistaId()));

        if (certificadoRepo.existsByTransportistaAndMesAndAnio(t, dto.getMes(), dto.getAnio())) {
            throw new DuplicateResourceException(
                    "Ya existe un certificado para ese transportista/mes/año.");

        }
        Certificado nuevo = Certificado.builder()
                .transportista(t)
                .numeroCertificado(nextNumeroCertificado(t))
                .mes(dto.getMes())
                .anio(dto.getAnio())
                .build();

        Certificado guardado = certificadoRepo.save(nuevo);
        procesarTicketsDelPeriodo(guardado);

        return certificadoMapper.toDTO(guardado);
    }

    /**
     * Verifica y crea certificados del mes anterior para todos los transportistas,
     * y asocia tickets del período al certificado creado.
     */
    @Override
    @Scheduled(cron = "0 1 0 1 * *")
    public void verificarYCrearCertificadosSiEsNecesario() {
        YearMonth mesAnterior = YearMonth.from(LocalDate.now()).minusMonths(1);
        int anio = mesAnterior.getYear();
        Meses mesEnum = Meses.fromId(mesAnterior.getMonthValue());
        List<Transportista> transportistas = transportistaRepo.findAll();

        for (Transportista t : transportistas) {
            certificadoRepo
                    .findByTransportista_IdTransportistaAndMesAndAnio(
                            t.getIdTransportista(),
                            mesEnum,
                            anio
                    )
                    .orElseGet(() -> certificadoRepo.save(
                            Certificado.builder()
                                    .transportista(t)
                                    .numeroCertificado(nextNumeroCertificado(t))
                                    .mes(mesEnum)
                                    .anio(anio)
                                    .build()
                    ));
        }

        certificadoRepo.findAll().forEach(this::procesarTicketsDelPeriodo);
    }

    private void procesarTicketsDelPeriodo(Certificado certificado) {
        YearMonth periodo = YearMonth.of(certificado.getAnio(), certificado.getMes().getId());
        List<TicketControl> tickets = ticketRepository
                .findByTransportistaIdTransportistaAndFechaEmisionBetween(
                        certificado.getTransportista().getIdTransportista(),
                        periodo.atDay(1),
                        periodo.atEndOfMonth()
                );

        boolean hayCambios = false;
        for (TicketControl ticket : tickets) {
            if (!ticket.isEstado()) {
                ticket.setEstado(true);
                hayCambios = true;
            }
        }

        if (hayCambios) {
            ticketRepository.saveAll(tickets);
        }
    }

    private TicketsReport toTicketsReport(TicketControl ticket) {
        HojaRuta hojaRuta = ticket.getHojaRuta();

        return TicketsReport.builder()
                .hoja_ruta_id(numeroHojaRutaTexto(hojaRuta))
                .hoja_ruta_periodo(periodoHojaRuta(hojaRuta))
                .id_ticket(numeroTicketTexto(ticket))
                .generador_nombre(nombreGenerador(ticket.getGenerador()))
                .fechaEmision(String.valueOf(ticket.getFechaEmision()))
                .peso(ticket.getPesoTotal())
                .build();
    }

    private String nombreTransportista(Transportista transportista) {
        if (transportista == null) {
            return "-";
        }

        return (transportista.getNombre() + " " + transportista.getApellido()).trim();
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

    private String nombreGenerador(Generador generador) {
        Generador generadorReal = generador != null ? (Generador) Hibernate.unproxy(generador) : null;

        if (generadorReal instanceof GeneradorEmpresa empresa) {
            if (empresa.getRazonSocial() != null && !empresa.getRazonSocial().isBlank()) {
                return empresa.getRazonSocial();
            }

            if (empresa.getNombreFantasia() != null && !empresa.getNombreFantasia().isBlank()) {
                return empresa.getNombreFantasia();
            }
        }

        if (generadorReal instanceof GeneradorAutonomo autonomo) {
            String nombre = ((autonomo.getNombre() != null ? autonomo.getNombre() : "") + " "
                    + (autonomo.getApellido() != null ? autonomo.getApellido() : "")).trim();

            if (!nombre.isBlank()) {
                return nombre;
            }
        }

        return generadorReal != null ? "Generador #" + generadorReal.getId() : "-";
    }

    private String domicilioTexto(Domicilio domicilio) {
        if (domicilio == null) {
            return "-";
        }

        return java.util.stream.Stream.of(
                        domicilio.getCalle(),
                        domicilio.getAltura() != null ? String.valueOf(domicilio.getAltura()) : null,
                        domicilio.getBarrio() != null && !domicilio.getBarrio().isBlank()
                                ? "Barrio " + domicilio.getBarrio()
                                : null,
                        domicilio.getLocalidad(),
                        domicilio.getProvincia()
                )
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse("-");
    }

    private String periodoHojaRuta(HojaRuta hojaRuta) {
        if (hojaRuta == null) {
            return "-";
        }

        return java.util.stream.Stream.of(hojaRuta.getFechaInicio(), hojaRuta.getFechaFin())
                .map(fecha -> fecha != null ? String.valueOf(fecha) : null)
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + " / " + right)
                .orElse("-");
    }

    private long nextNumeroCertificado(Transportista transportista) {
        Long transportistaId = transportista != null ? transportista.getIdTransportista() : null;

        if (transportistaId == null) {
            throw new IllegalArgumentException("El transportista es obligatorio para numerar el certificado.");
        }

        return certificadoRepo.findMaxNumeroCertificadoByTransportista(transportistaId) + 1;
    }

    private String numeroCertificadoTexto(Certificado certificado) {
        if (certificado == null) {
            return "-";
        }

        return certificado.getNumeroCertificado() != null
                ? String.valueOf(certificado.getNumeroCertificado())
                : String.valueOf(certificado.getId());
    }

    private Long numeroCertificadoValor(Certificado certificado) {
        if (certificado == null) {
            return null;
        }

        return certificado.getNumeroCertificado() != null
                ? certificado.getNumeroCertificado()
                : certificado.getId();
    }

    private String numeroHojaRutaTexto(HojaRuta hojaRuta) {
        if (hojaRuta == null) {
            return "-";
        }

        return hojaRuta.getNumeroHojaRuta() != null
                ? String.valueOf(hojaRuta.getNumeroHojaRuta())
                : String.valueOf(hojaRuta.getId());
    }

    private String numeroTicketTexto(TicketControl ticket) {
        if (ticket == null) {
            return "-";
        }

        return ticket.getNumeroTicket() != null
                ? String.valueOf(ticket.getNumeroTicket())
                : String.valueOf(ticket.getId());
    }
}
