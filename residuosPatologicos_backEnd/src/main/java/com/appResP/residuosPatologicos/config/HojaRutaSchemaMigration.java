package com.appResP.residuosPatologicos.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HojaRutaSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            dropUniqueFechaInicioLegacyIndex();
            backfillLegacyTransportista();
        } catch (Exception e) {
            log.warn("No se pudo verificar o actualizar el indice legacy de hoja_ruta.fecha_inicio.", e);
        }
    }

    private void dropUniqueFechaInicioLegacyIndex() {
        List<String> legacyIndexes = jdbcTemplate.queryForList("""
                select s.index_name
                from information_schema.statistics s
                where s.table_schema = database()
                  and s.table_name = 'hoja_ruta'
                  and s.non_unique = 0
                group by s.index_name
                having group_concat(s.column_name order by s.seq_in_index) = 'fecha_inicio'
                """, String.class);

        for (String indexName : legacyIndexes) {
            if (!isSafeIndexName(indexName)) {
                log.warn("Indice legacy omitido por nombre inesperado: {}", indexName);
                continue;
            }

            jdbcTemplate.execute("alter table hoja_ruta drop index `" + indexName + "`");
            log.info("Indice legacy de hoja_ruta removido: {}", indexName);
        }
    }

    private void backfillLegacyTransportista() {
        int updated = jdbcTemplate.update("""
                update hoja_ruta h
                join (
                    select id_hoja_ruta, min(id_transportista) as id_transportista
                    from ticket_control
                    group by id_hoja_ruta
                    having count(distinct id_transportista) = 1
                ) owner on owner.id_hoja_ruta = h.id
                left join hoja_ruta existing
                    on existing.id_transportista = owner.id_transportista
                    and existing.fecha_inicio = h.fecha_inicio
                    and existing.id <> h.id
                set h.id_transportista = owner.id_transportista
                where h.id_transportista is null
                  and existing.id is null
                """);

        if (updated > 0) {
            log.info("Hojas de ruta legacy asociadas a transportista: {}", updated);
        }
    }

    private boolean isSafeIndexName(String indexName) {
        return indexName != null && indexName.matches("[A-Za-z0-9_$]+");
    }
}
