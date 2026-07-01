package com.appResP.residuosPatologicos.reports;

import com.appResP.residuosPatologicos.DTO.response.HojaRutaReportRow;
import com.appResP.residuosPatologicos.DTO.response.TicketResiduoReport;
import com.appResP.residuosPatologicos.DTO.response.TicketsReport;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JasperTemplateCompileSmokeTest {

    @Test
    void hojaRutaReportTemplateCompiles() throws Exception {
        try (InputStream template = getClass().getResourceAsStream("/templates/hojaRutaReport.jrxml")) {
            assertThat(template).isNotNull();
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    sampleParams(),
                    new JRBeanCollectionDataSource(sampleRows())
            );

            assertThat(JasperExportManager.exportReportToPdf(print)).isNotEmpty();
        }
    }

    @Test
    void ticketResiduoTemplateCompiles() throws Exception {
        try (InputStream template = getClass().getResourceAsStream("/templates/ticketResiduo.jrxml")) {
            assertThat(template).isNotNull();
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    sampleTicketParams(),
                    new JRBeanCollectionDataSource(sampleTicketRows())
            );

            assertThat(JasperExportManager.exportReportToPdf(print)).isNotEmpty();
        }
    }

    @Test
    void ticketResiduoTemplateCompilesWithoutResiduos() throws Exception {
        try (InputStream template = getClass().getResourceAsStream("/templates/ticketResiduo.jrxml")) {
            assertThat(template).isNotNull();
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    sampleTicketParams(),
                    new JRBeanCollectionDataSource(List.of())
            );

            assertThat(JasperExportManager.exportReportToPdf(print)).isNotEmpty();
        }
    }

    @Test
    void certificadoReportTemplateCompiles() throws Exception {
        try (InputStream template = getClass().getResourceAsStream("/templates/certificadoReport.jrxml")) {
            assertThat(template).isNotNull();
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    sampleCertificadoParams(),
                    new JRBeanCollectionDataSource(sampleCertificadoRows())
            );

            assertThat(JasperExportManager.exportReportToPdf(print)).isNotEmpty();
        }
    }

    private Map<String, Object> sampleParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("empresa_nombre", "EcoGroup S.R.L.");
        params.put("transportista_nombre", "Juan Perez");
        params.put("transportista_documento", "30000000000");
        params.put("transportista_direccion", "Ruta 3 km 4,5");
        params.put("hoja_ruta_id", 33L);
        params.put("fecha_emision", "30/06/2026");
        params.put("periodo_visita", "30/06/2026 / 05/07/2026");
        params.put("cantidad_visitas", "1");
        params.put("vehiculo_marca", "Iveco");
        params.put("vehiculo_modelo", "Daily");
        params.put("vehiculo_patente", "AD561VA");
        params.put("firma_transportista", "Juan Perez");
        return params;
    }

    private List<HojaRutaReportRow> sampleRows() {
        return List.of(HojaRutaReportRow.builder()
                .nro("1")
                .generadorNombre("Hospital de La Toma")
                .generadorDomicilio("La Toma 1250")
                .generadorLocalidad("La Toma")
                .fechaVisita("30/06/2026")
                .comprobante("642")
                .build());
    }

    private Map<String, Object> sampleTicketParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("comprobante_numero", "48");
        params.put("marca_transportista", "EcoRetiro Sanitario");
        params.put("imgTransportista", null);
        params.put("transportista_nombre", "Juan Perez");
        params.put("transportista_nombre_fantasia", "EcoRetiro Sanitario");
        params.put("transportista_documento", "20300000001");
        params.put("transportista_domicilio", "Barrio Centro, Belgrano 120");
        params.put("transportista_telefono", "2664000000");
        params.put("transportista_localidad_provincia", "San Luis, San Luis");
        params.put("generador_tipo", "Empresa");
        params.put("generador_nombre", "Clinica Modelo S.A.");
        params.put("generador_nombre_fantasia", "Clinica Modelo");
        params.put("generador_documento_label", "CUIT");
        params.put("generador_documento", "30700000002");
        params.put("generador_domicilio", "Mitre 455");
        params.put("generador_localidad_provincia", "Villa Mercedes, San Luis");
        params.put("fecha_emision", "2026-06-30");
        params.put("pesoTotal", new BigDecimal("14.50"));
        params.put("firma_generador", "Clinica Modelo S.A.");
        params.put("firma_transportista", "Juan Perez");
        return params;
    }

    private List<TicketResiduoReport> sampleTicketRows() {
        return List.of(
                TicketResiduoReport.builder()
                        .codigo("Y1")
                        .nombre("Residuos patologicos")
                        .peso(new BigDecimal("10.25"))
                        .build(),
                TicketResiduoReport.builder()
                        .codigo("Y3")
                        .nombre("Cortopunzantes")
                        .peso(new BigDecimal("4.25"))
                        .build()
        );
    }

    private Map<String, Object> sampleCertificadoParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("transportista_nombre", "Juan Perez");
        params.put("transportista_cuil", "20300000001");
        params.put("transportista_nombre_fantasia", "EcoRetiro Sanitario");
        params.put("transportista_direccion", "Barrio Centro, Belgrano 120, San Luis");
        params.put("certificado_id", 12L);
        params.put("certificado_periodo", "JUNIO 2026");
        params.put("firma_transportista", "Juan Perez");
        return params;
    }

    private List<TicketsReport> sampleCertificadoRows() {
        return List.of(
                TicketsReport.builder()
                        .hoja_ruta_id("31")
                        .hoja_ruta_periodo("2026-06-01 / 2026-06-07")
                        .id_ticket("46")
                        .generador_nombre("Clinica Modelo S.A.")
                        .fechaEmision("2026-06-03")
                        .peso(new BigDecimal("12.50"))
                        .build(),
                TicketsReport.builder()
                        .hoja_ruta_id("31")
                        .hoja_ruta_periodo("2026-06-01 / 2026-06-07")
                        .id_ticket("47")
                        .generador_nombre("Consultorio Norte")
                        .fechaEmision("2026-06-05")
                        .peso(new BigDecimal("8.20"))
                        .build(),
                TicketsReport.builder()
                        .hoja_ruta_id("32")
                        .hoja_ruta_periodo("2026-06-08 / 2026-06-14")
                        .id_ticket("48")
                        .generador_nombre("Sanatorio Central")
                        .fechaEmision("2026-06-10")
                        .peso(new BigDecimal("15.75"))
                        .build()
        );
    }
}
