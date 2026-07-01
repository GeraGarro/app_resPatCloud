-- Seed para probar Certificados usando los datos reales ya existentes.
-- Base tomada del dump: C:/Users/Rocio/Downloads/admin_residuosdb.sql
--
-- Reutiliza:
-- - El unico usuario con rol TRANSPORTISTA.
-- - El transportista asociado a ese usuario.
-- - Los generadores ya asociados a ese transportista.
-- - Los tipos de residuo ya cargados.
--
-- Genera datos historicos para ABRIL y MAYO de 2026:
-- - Certificados mensuales.
-- - Hojas de ruta semanales.
-- - Tickets procesados.
-- - Residuos vinculados a cada ticket.
--
-- Ejecutar desde phpMyAdmin/XAMPP.
-- Si tu base se llama distinto, cambia solo esta linea:
USE admin_residuosDB;

START TRANSACTION;

-- ---------------------------------------------------------------------------
-- Resolver datos existentes de la base
-- ---------------------------------------------------------------------------
SET @usuario_transportista_id := (
  SELECT id
  FROM usuarios
  WHERE rol = 'TRANSPORTISTA'
  ORDER BY id
  LIMIT 1
);

SET @transportista_id := (
  SELECT id_transportista
  FROM transportista
  WHERE usuario_id = @usuario_transportista_id
  ORDER BY id_transportista
  LIMIT 1
);

SET @generador_1_id := (
  SELECT id
  FROM generador
  WHERE transportista_id = @transportista_id
    AND estado = b'1'
  ORDER BY id
  LIMIT 1 OFFSET 0
);

SET @generador_2_id := (
  SELECT id
  FROM generador
  WHERE transportista_id = @transportista_id
    AND estado = b'1'
  ORDER BY id
  LIMIT 1 OFFSET 1
);

SET @generador_3_id := (
  SELECT id
  FROM generador
  WHERE transportista_id = @transportista_id
    AND estado = b'1'
  ORDER BY id
  LIMIT 1 OFFSET 2
);

SET @generador_4_id := (
  SELECT id
  FROM generador
  WHERE transportista_id = @transportista_id
    AND estado = b'1'
  ORDER BY id
  LIMIT 1 OFFSET 3
);

SET @generador_2_id := COALESCE(@generador_2_id, @generador_1_id);
SET @generador_3_id := COALESCE(@generador_3_id, @generador_1_id);
SET @generador_4_id := COALESCE(@generador_4_id, @generador_1_id);

SET @tipo_residuo_1_id := (
  SELECT id
  FROM tipo_residuo
  WHERE estado_actividad = b'1'
  ORDER BY id
  LIMIT 1 OFFSET 0
);

SET @tipo_residuo_2_id := (
  SELECT id
  FROM tipo_residuo
  WHERE estado_actividad = b'1'
  ORDER BY id
  LIMIT 1 OFFSET 1
);

-- Validacion visual: debe devolver transportista, generadores y dos tipos.
SELECT
  @usuario_transportista_id AS usuario_transportista_id,
  @transportista_id AS transportista_id,
  @generador_1_id AS generador_1_id,
  @generador_2_id AS generador_2_id,
  @generador_3_id AS generador_3_id,
  @generador_4_id AS generador_4_id,
  @tipo_residuo_1_id AS tipo_residuo_1_id,
  @tipo_residuo_2_id AS tipo_residuo_2_id;

-- ---------------------------------------------------------------------------
-- Certificados de abril y mayo 2026
-- ---------------------------------------------------------------------------
INSERT INTO certificado (anio, mes, id_transportista)
SELECT 2026, 'ABRIL', @transportista_id
WHERE @transportista_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM certificado
    WHERE id_transportista = @transportista_id
      AND anio = 2026
      AND mes = 'ABRIL'
  );

SET @certificado_abril_id := (
  SELECT id
  FROM certificado
  WHERE id_transportista = @transportista_id
    AND anio = 2026
    AND mes = 'ABRIL'
  LIMIT 1
);

INSERT INTO certificado (anio, mes, id_transportista)
SELECT 2026, 'MAYO', @transportista_id
WHERE @transportista_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM certificado
    WHERE id_transportista = @transportista_id
      AND anio = 2026
      AND mes = 'MAYO'
  );

SET @certificado_mayo_id := (
  SELECT id
  FROM certificado
  WHERE id_transportista = @transportista_id
    AND anio = 2026
    AND mes = 'MAYO'
  LIMIT 1
);

-- ---------------------------------------------------------------------------
-- Hojas de ruta semanales de los dos meses anteriores
-- ---------------------------------------------------------------------------
CREATE TEMPORARY TABLE seed_hojas_certificados (
  fecha_inicio DATE PRIMARY KEY,
  fecha_fin DATE NOT NULL,
  certificado_id BIGINT NOT NULL
);

INSERT INTO seed_hojas_certificados (fecha_inicio, fecha_fin, certificado_id)
VALUES
  ('2026-04-01', '2026-04-05', @certificado_abril_id),
  ('2026-04-06', '2026-04-12', @certificado_abril_id),
  ('2026-04-13', '2026-04-19', @certificado_abril_id),
  ('2026-04-20', '2026-04-26', @certificado_abril_id),
  ('2026-04-27', '2026-05-03', @certificado_abril_id),
  ('2026-05-04', '2026-05-10', @certificado_mayo_id),
  ('2026-05-11', '2026-05-17', @certificado_mayo_id),
  ('2026-05-18', '2026-05-24', @certificado_mayo_id),
  ('2026-05-25', '2026-05-31', @certificado_mayo_id);

INSERT INTO hoja_ruta (fecha_fin, fecha_inicio, id_certificado)
SELECT fecha_fin, fecha_inicio, certificado_id
FROM seed_hojas_certificados
WHERE certificado_id IS NOT NULL
ON DUPLICATE KEY UPDATE
  fecha_fin = VALUES(fecha_fin),
  id_certificado = VALUES(id_certificado);

-- ---------------------------------------------------------------------------
-- Tickets procesados por semana
-- ---------------------------------------------------------------------------
CREATE TEMPORARY TABLE seed_tickets_certificados (
  fecha_inicio DATE NOT NULL,
  fecha_emision DATE NOT NULL,
  horario TIME(6) NOT NULL,
  generador_id BIGINT NOT NULL,
  peso_tipo_1 DECIMAL(12, 3) NOT NULL,
  peso_tipo_2 DECIMAL(12, 3) NOT NULL
);

INSERT INTO seed_tickets_certificados (
  fecha_inicio,
  fecha_emision,
  horario,
  generador_id,
  peso_tipo_1,
  peso_tipo_2
)
VALUES
  ('2026-04-01', '2026-04-01', '09:00:00.000000', @generador_1_id, 18.500, 2.250),
  ('2026-04-01', '2026-04-03', '11:30:00.000000', @generador_2_id, 24.750, 3.100),
  ('2026-04-01', '2026-04-04', '15:15:00.000000', @generador_3_id, 12.200, 1.500),

  ('2026-04-06', '2026-04-06', '09:20:00.000000', @generador_1_id, 20.300, 2.800),
  ('2026-04-06', '2026-04-08', '12:10:00.000000', @generador_2_id, 27.600, 3.450),
  ('2026-04-06', '2026-04-10', '16:00:00.000000', @generador_4_id, 14.900, 1.850),

  ('2026-04-13', '2026-04-13', '08:45:00.000000', @generador_1_id, 21.250, 2.600),
  ('2026-04-13', '2026-04-15', '10:50:00.000000', @generador_2_id, 25.400, 3.900),
  ('2026-04-13', '2026-04-17', '14:35:00.000000', @generador_3_id, 13.750, 1.700),

  ('2026-04-20', '2026-04-20', '09:05:00.000000', @generador_1_id, 19.800, 2.950),
  ('2026-04-20', '2026-04-22', '11:45:00.000000', @generador_2_id, 29.100, 4.200),
  ('2026-04-20', '2026-04-24', '15:40:00.000000', @generador_4_id, 15.350, 2.050),

  ('2026-04-27', '2026-04-27', '08:55:00.000000', @generador_1_id, 22.000, 3.150),
  ('2026-04-27', '2026-04-29', '12:25:00.000000', @generador_2_id, 26.850, 3.700),
  ('2026-04-27', '2026-04-30', '16:20:00.000000', @generador_3_id, 16.100, 2.250),

  ('2026-05-04', '2026-05-04', '09:10:00.000000', @generador_1_id, 20.900, 2.700),
  ('2026-05-04', '2026-05-06', '11:20:00.000000', @generador_2_id, 28.350, 3.950),
  ('2026-05-04', '2026-05-08', '15:10:00.000000', @generador_4_id, 14.600, 1.950),

  ('2026-05-11', '2026-05-11', '09:30:00.000000', @generador_1_id, 23.400, 3.050),
  ('2026-05-11', '2026-05-13', '12:05:00.000000', @generador_2_id, 30.200, 4.100),
  ('2026-05-11', '2026-05-15', '16:15:00.000000', @generador_3_id, 15.900, 2.300),

  ('2026-05-18', '2026-05-18', '08:50:00.000000', @generador_1_id, 22.750, 3.200),
  ('2026-05-18', '2026-05-20', '11:55:00.000000', @generador_2_id, 27.950, 3.850),
  ('2026-05-18', '2026-05-22', '14:45:00.000000', @generador_4_id, 17.250, 2.450),

  ('2026-05-25', '2026-05-25', '09:15:00.000000', @generador_1_id, 24.100, 3.300),
  ('2026-05-25', '2026-05-27', '12:35:00.000000', @generador_2_id, 31.500, 4.350),
  ('2026-05-25', '2026-05-29', '15:25:00.000000', @generador_3_id, 18.000, 2.600);

INSERT INTO ticket_control (
  estado,
  fecha_emision,
  horario,
  peso_total,
  id_generador,
  id_hoja_ruta,
  id_transportista
)
SELECT
  b'1',
  s.fecha_emision,
  s.horario,
  s.peso_tipo_1 + s.peso_tipo_2,
  s.generador_id,
  h.id,
  @transportista_id
FROM seed_tickets_certificados s
JOIN hoja_ruta h
  ON h.fecha_inicio = s.fecha_inicio
WHERE @transportista_id IS NOT NULL
  AND s.generador_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM ticket_control t
    WHERE t.id_transportista = @transportista_id
      AND t.id_hoja_ruta = h.id
      AND t.fecha_emision = s.fecha_emision
      AND t.horario = s.horario
      AND t.id_generador = s.generador_id
  );

INSERT IGNORE INTO residuo (peso, ticket_id, tipo_residuo_id)
SELECT
  s.peso_tipo_1,
  t.id_ticket,
  @tipo_residuo_1_id
FROM seed_tickets_certificados s
JOIN hoja_ruta h
  ON h.fecha_inicio = s.fecha_inicio
JOIN ticket_control t
  ON t.id_transportista = @transportista_id
  AND t.id_hoja_ruta = h.id
  AND t.fecha_emision = s.fecha_emision
  AND t.horario = s.horario
  AND t.id_generador = s.generador_id
WHERE @tipo_residuo_1_id IS NOT NULL
  AND s.peso_tipo_1 > 0
UNION ALL
SELECT
  s.peso_tipo_2,
  t.id_ticket,
  @tipo_residuo_2_id
FROM seed_tickets_certificados s
JOIN hoja_ruta h
  ON h.fecha_inicio = s.fecha_inicio
JOIN ticket_control t
  ON t.id_transportista = @transportista_id
  AND t.id_hoja_ruta = h.id
  AND t.fecha_emision = s.fecha_emision
  AND t.horario = s.horario
  AND t.id_generador = s.generador_id
WHERE @tipo_residuo_2_id IS NOT NULL
  AND s.peso_tipo_2 > 0;

UPDATE ticket_control t
JOIN (
  SELECT ticket_id, SUM(peso) AS total
  FROM residuo
  GROUP BY ticket_id
) r
  ON r.ticket_id = t.id_ticket
SET t.peso_total = r.total
WHERE t.id_transportista = @transportista_id
  AND t.fecha_emision BETWEEN '2026-04-01' AND '2026-05-31';

DROP TEMPORARY TABLE IF EXISTS seed_tickets_certificados;
DROP TEMPORARY TABLE IF EXISTS seed_hojas_certificados;

COMMIT;

-- ---------------------------------------------------------------------------
-- Verificacion rapida
-- ---------------------------------------------------------------------------
SELECT
  u.id AS usuario_id,
  u.email AS usuario_email,
  tr.id_transportista,
  CONCAT(tr.nombre, ' ', tr.apellido) AS transportista
FROM usuarios u
JOIN transportista tr
  ON tr.usuario_id = u.id
WHERE tr.id_transportista = @transportista_id;

SELECT
  c.id AS certificado_id,
  c.mes,
  c.anio,
  COUNT(DISTINCT h.id) AS hojas_ruta,
  COUNT(t.id_ticket) AS tickets_procesados,
  COALESCE(SUM(t.peso_total), 0) AS peso_total
FROM certificado c
LEFT JOIN hoja_ruta h
  ON h.id_certificado = c.id
LEFT JOIN ticket_control t
  ON t.id_hoja_ruta = h.id
  AND t.estado = b'1'
WHERE c.id_transportista = @transportista_id
  AND c.anio = 2026
  AND c.mes IN ('ABRIL', 'MAYO')
GROUP BY c.id, c.mes, c.anio
ORDER BY c.anio, FIELD(c.mes, 'ENERO', 'FEBRERO', 'MARZO', 'ABRIL', 'MAYO', 'JUNIO', 'JULIO', 'AGOSTO', 'SEPTIEMBRE', 'OCTUBRE', 'NOVIEMBRE', 'DICIEMBRE');
