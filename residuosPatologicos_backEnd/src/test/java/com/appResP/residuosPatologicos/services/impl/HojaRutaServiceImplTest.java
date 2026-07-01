package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.mappers.HojaRutaMapper;
import com.appResP.residuosPatologicos.models.HojaRuta;
import com.appResP.residuosPatologicos.models.Transportista;
import com.appResP.residuosPatologicos.repository.ICertificadoRepository;
import com.appResP.residuosPatologicos.repository.IHojaRutaRepository;
import com.appResP.residuosPatologicos.repository.ITransportistaRepository;
import com.appResP.residuosPatologicos.repository.ITicketRepository;
import com.appResP.residuosPatologicos.services.TransportistaProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HojaRutaServiceImplTest {

    private final IHojaRutaRepository repo = mock(IHojaRutaRepository.class);
    private final ICertificadoRepository certificadoRepository = mock(ICertificadoRepository.class);
    private final ITransportistaRepository transportistaRepository = mock(ITransportistaRepository.class);
    private final ITicketRepository ticketRepository = mock(ITicketRepository.class);
    private final TransportistaProfileService transportistaProfileService = mock(TransportistaProfileService.class);
    private final HojaRutaMapper mapper = mock(HojaRutaMapper.class);

    @Test
    void calcularFinCortaLaHojaEnElUltimoDiaDelMesSiLaSemanaCruzaDeMes() {
        HojaRutaServiceImpl service = serviceWithClock(LocalDate.of(2026, 4, 27));

        LocalDate fin = service.calcularFin(LocalDate.of(2026, 4, 27));

        assertThat(fin).isEqualTo(LocalDate.of(2026, 4, 30));
    }

    @Test
    void calcularFinContinuaHastaElDomingoCuandoLaHojaIniciaElPrimerDiaDelMes() {
        HojaRutaServiceImpl service = serviceWithClock(LocalDate.of(2026, 5, 1));

        LocalDate fin = service.calcularFin(LocalDate.of(2026, 5, 1));

        assertThat(fin).isEqualTo(LocalDate.of(2026, 5, 3));
    }

    @Test
    void creaHojaDesdeElPrimerDiaDelMesHastaElCierreEstandarCuandoLaAnteriorCerroPorMes() {
        HojaRutaServiceImpl service = serviceWithClock(LocalDate.of(2026, 5, 1));
        Transportista transportista = Transportista.builder()
                .idTransportista(7L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .estado(true)
                .build();
        HojaRuta hojaAbril = HojaRuta.builder()
                .fechaInicio(LocalDate.of(2026, 4, 27))
                .fechaFin(LocalDate.of(2026, 4, 30))
                .transportista(transportista)
                .build();

        when(transportistaRepository.findByEstadoTrue()).thenReturn(List.of(transportista));
        when(repo.findTopByTransportista_IdTransportistaOrderByFechaFinDesc(7L)).thenReturn(Optional.of(hojaAbril));
        when(repo.existsByTransportista_IdTransportistaAndFechaInicio(7L, LocalDate.of(2026, 5, 1))).thenReturn(false);

        service.verificarYCrearHojaRutaSiEsNecesario();

        ArgumentCaptor<HojaRuta> hojaCaptor = ArgumentCaptor.forClass(HojaRuta.class);
        verify(repo).save(hojaCaptor.capture());
        assertThat(hojaCaptor.getValue().getFechaInicio()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(hojaCaptor.getValue().getFechaFin()).isEqualTo(LocalDate.of(2026, 5, 3));
        assertThat(hojaCaptor.getValue().getTransportista()).isEqualTo(transportista);
    }

    private HojaRutaServiceImpl serviceWithClock(LocalDate fecha) {
        ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
        Clock clock = Clock.fixed(fecha.atStartOfDay(zone).toInstant(), zone);
        return new HojaRutaServiceImpl(
                repo,
                certificadoRepository,
                transportistaRepository,
                ticketRepository,
                transportistaProfileService,
                mapper,
                clock
        );
    }
}
