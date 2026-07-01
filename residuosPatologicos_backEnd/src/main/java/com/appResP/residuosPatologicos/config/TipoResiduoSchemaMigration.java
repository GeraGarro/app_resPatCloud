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
public class TipoResiduoSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            dropUniqueCodigoLegacyIndex();
            backfillLegacyTiposResiduo();
        } catch (Exception e) {
            log.warn("No se pudo verificar o actualizar los tipos de residuo legacy.", e);
        }
    }

    private void dropUniqueCodigoLegacyIndex() {
        List<String> legacyIndexes = jdbcTemplate.queryForList("""
                select s.index_name
                from information_schema.statistics s
                where s.table_schema = database()
                  and s.table_name = 'tipo_residuo'
                  and s.non_unique = 0
                group by s.index_name
                having group_concat(s.column_name order by s.seq_in_index) = 'codigo'
                """, String.class);

        for (String indexName : legacyIndexes) {
            if (!isSafeIndexName(indexName)) {
                log.warn("Indice legacy omitido por nombre inesperado: {}", indexName);
                continue;
            }

            jdbcTemplate.execute("alter table tipo_residuo drop index `" + indexName + "`");
            log.info("Indice legacy de tipo_residuo removido: {}", indexName);
        }
    }

    private void backfillLegacyTiposResiduo() {
        int singleOwnerUpdates = jdbcTemplate.update("""
                update tipo_residuo tr
                join (
                    select r.tipo_residuo_id, min(tc.id_transportista) as id_transportista
                    from residuo r
                    join ticket_control tc on tc.id_ticket = r.ticket_id
                    group by r.tipo_residuo_id
                    having count(distinct tc.id_transportista) = 1
                ) owner on owner.tipo_residuo_id = tr.id
                left join tipo_residuo existing
                    on existing.codigo = tr.codigo
                    and existing.id_transportista = owner.id_transportista
                    and existing.id <> tr.id
                set tr.id_transportista = owner.id_transportista
                where tr.id_transportista is null
                  and existing.id is null
                """);

        int clonedTipos = jdbcTemplate.update("""
                insert into tipo_residuo (codigo, nombre_tipo, estado_actividad, id_transportista)
                select distinct tr.codigo, tr.nombre_tipo, tr.estado_actividad, tc.id_transportista
                from tipo_residuo tr
                join residuo r on r.tipo_residuo_id = tr.id
                join ticket_control tc on tc.id_ticket = r.ticket_id
                left join tipo_residuo existing
                    on existing.codigo = tr.codigo
                    and existing.id_transportista = tc.id_transportista
                where tr.id_transportista is null
                  and existing.id is null
                """);

        int reassignedResiduos = jdbcTemplate.update("""
                update residuo r
                join ticket_control tc on tc.id_ticket = r.ticket_id
                join tipo_residuo original
                    on original.id = r.tipo_residuo_id
                    and original.id_transportista is null
                join tipo_residuo replacement
                    on replacement.codigo = original.codigo
                    and replacement.id_transportista = tc.id_transportista
                set r.tipo_residuo_id = replacement.id
                where replacement.id <> original.id
                """);

        if (singleOwnerUpdates > 0 || clonedTipos > 0 || reassignedResiduos > 0) {
            log.info(
                    "Tipos de residuo legacy migrados. Asociados: {}, clonados: {}, residuos reasignados: {}",
                    singleOwnerUpdates,
                    clonedTipos,
                    reassignedResiduos
            );
        }
    }

    private boolean isSafeIndexName(String indexName) {
        return indexName != null && indexName.matches("[A-Za-z0-9_$]+");
    }
}
