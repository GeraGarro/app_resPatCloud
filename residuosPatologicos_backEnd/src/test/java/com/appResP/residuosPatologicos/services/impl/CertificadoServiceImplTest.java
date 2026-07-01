package com.appResP.residuosPatologicos.services.impl;

import com.appResP.residuosPatologicos.DTO.response.TicketsReport;
import com.appResP.residuosPatologicos.models.GeneradorAutonomo;
import com.appResP.residuosPatologicos.models.GeneradorEmpresa;
import com.appResP.residuosPatologicos.models.HojaRuta;
import com.appResP.residuosPatologicos.models.TicketControl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class CertificadoServiceImplTest {

    private final CertificadoServiceImpl service = new CertificadoServiceImpl(null, null, null, null, null);

    @Test
    void ticketReportUsesTicketIdHojaRutaAndEmpresaRazonSocial() throws Exception {
        TicketsReport report = toTicketsReport(TicketControl.builder()
                .id(46L)
                .hojaRuta(HojaRuta.builder()
                        .id(7L)
                        .fechaInicio(LocalDate.of(2026, 6, 1))
                        .fechaFin(LocalDate.of(2026, 6, 7))
                        .build())
                .generador(GeneradorEmpresa.builder()
                        .razonSocial("Clinica Modelo S.A.")
                        .nombreFantasia("Clinica Modelo")
                        .build())
                .fechaEmision(LocalDate.of(2026, 6, 3))
                .horario(LocalTime.NOON)
                .pesoTotal(new BigDecimal("12.50"))
                .estado(true)
                .build());

        assertThat(report.getId_ticket()).isEqualTo("46");
        assertThat(report.getHoja_ruta_id()).isEqualTo("7");
        assertThat(report.getHoja_ruta_periodo()).isEqualTo("2026-06-01 / 2026-06-07");
        assertThat(report.getGenerador_nombre()).isEqualTo("Clinica Modelo S.A.");
        assertThat(report.getPeso()).isEqualByComparingTo("12.50");
    }

    @Test
    void ticketReportUsesAutonomoNombreYApellido() throws Exception {
        TicketsReport report = toTicketsReport(TicketControl.builder()
                .id(47L)
                .hojaRuta(HojaRuta.builder()
                        .id(8L)
                        .fechaInicio(LocalDate.of(2026, 6, 8))
                        .fechaFin(LocalDate.of(2026, 6, 14))
                        .build())
                .generador(GeneradorAutonomo.builder()
                        .nombre("Maria")
                        .apellido("Lopez")
                        .build())
                .fechaEmision(LocalDate.of(2026, 6, 10))
                .horario(LocalTime.NOON)
                .pesoTotal(new BigDecimal("8.25"))
                .estado(true)
                .build());

        assertThat(report.getId_ticket()).isEqualTo("47");
        assertThat(report.getHoja_ruta_id()).isEqualTo("8");
        assertThat(report.getGenerador_nombre()).isEqualTo("Maria Lopez");
    }

    private TicketsReport toTicketsReport(TicketControl ticket) throws Exception {
        Method method = CertificadoServiceImpl.class.getDeclaredMethod("toTicketsReport", TicketControl.class);
        method.setAccessible(true);
        return (TicketsReport) method.invoke(service, ticket);
    }
}
