package com.appResP.residuosPatologicos.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContabilidadSecuenciaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            backfillHojasRuta();
            backfillTickets();
            backfillCertificados();
        } catch (Exception e) {
            log.warn("No se pudieron completar las secuencias contables por transportista.", e);
        }
    }

    private void backfillHojasRuta() {
        int updated = jdbcTemplate.update("""
                update hoja_ruta h
                join (
                    select id,
                           row_number() over (
                               partition by id_transportista
                               order by fecha_inicio, id
                           ) as numero
                    from hoja_ruta
                    where id_transportista is not null
                ) sec on sec.id = h.id
                set h.numero_hoja_ruta = sec.numero
                where h.numero_hoja_ruta is null
                  and h.id_transportista is not null
                """);

        if (updated > 0) {
            log.info("Secuencias de hojas de ruta completadas: {}", updated);
        }
    }

    private void backfillTickets() {
        int updated = jdbcTemplate.update("""
                update ticket_control t
                join (
                    select id_ticket,
                           row_number() over (
                               partition by id_transportista
                               order by id_ticket
                           ) as numero
                    from ticket_control
                    where id_transportista is not null
                ) sec on sec.id_ticket = t.id_ticket
                set t.numero_ticket = sec.numero
                where t.numero_ticket is null
                  and t.id_transportista is not null
                """);

        if (updated > 0) {
            log.info("Secuencias de tickets completadas: {}", updated);
        }
    }

    private void backfillCertificados() {
        int updated = jdbcTemplate.update("""
                update certificado c
                join (
                    select id,
                           row_number() over (
                               partition by id_transportista
                               order by anio,
                                        field(
                                            mes,
                                            'ENERO', 'FEBRERO', 'MARZO', 'ABRIL', 'MAYO', 'JUNIO',
                                            'JULIO', 'AGOSTO', 'SEPTIEMBRE', 'OCTUBRE', 'NOVIEMBRE', 'DICIEMBRE'
                                        ),
                                        id
                           ) as numero
                    from certificado
                    where id_transportista is not null
                ) sec on sec.id = c.id
                set c.numero_certificado = sec.numero
                where c.numero_certificado is null
                  and c.id_transportista is not null
                """);

        if (updated > 0) {
            log.info("Secuencias de certificados completadas: {}", updated);
        }
    }
}
