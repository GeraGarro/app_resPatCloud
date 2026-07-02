-- Migra datos desde la base legacy residuos_Patologicos hacia admin_residuosDB.
-- Requisitos:
-- 1. Importar el dump legacy en el schema residuos_Patologicos.
-- 2. Levantar el backend nuevo para que Hibernate cree admin_residuosDB.
-- 3. Ejecutar este script sobre el mismo servidor MySQL.
--
-- Nota: el modelo nuevo exige identificadores fiscales unicos en generador_empresa.
-- Para CUIT legacy duplicado, vacio o invalido se genera un CUIT tecnico 9 + id legacy.
-- Los generadores quedan vinculados al transportista id 1. No se asigna
-- generador.usuario_id=1 porque esa columna es UNIQUE y representa una relacion 1:1.
-- La vinculacion con el usuario 1 queda en transportista.usuario_id.

SET NAMES utf8mb4;
START TRANSACTION;

SET @legacy_schema := 'residuos_Patologicos';
SET @target_schema := 'admin_residuosDB';
SET @temporary_password_hash := '$2a$10$invalidMigrationHashReplaceBeforeLogin';

-- ---------------------------------------------------------------------------
-- Usuarios y transportistas
-- ---------------------------------------------------------------------------
INSERT INTO admin_residuosDB.usuarios (
    id,
    email,
    password,
    rol,
    email_verificado,
    estado_cuenta,
    fecha_registro
)
SELECT
    t.id_transportista,
    COALESCE(NULLIF(TRIM(t.email), ''), CONCAT('transportista-', t.id_transportista, '@legacy.local')),
    @temporary_password_hash,
    'TRANSPORTISTA',
    true,
    'APROBADO',
    NOW()
FROM residuos_Patologicos.transportista t
ON DUPLICATE KEY UPDATE
    rol = VALUES(rol),
    email_verificado = VALUES(email_verificado),
    estado_cuenta = VALUES(estado_cuenta);

SET @next_usuario_id := (
    SELECT COALESCE(MAX(id), 0) + 1
    FROM admin_residuosDB.usuarios
);

SET @usuario_sequence_table := (
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = @target_schema
      AND table_name IN ('usuarios_SEQ', 'usuarios_seq')
    LIMIT 1
);

SET @usuario_sequence_sql := IF(
    @usuario_sequence_table IS NULL,
    'SELECT 1',
    CONCAT(
        'UPDATE ', @target_schema, '.', @usuario_sequence_table,
        ' SET next_val = GREATEST(next_val, ', @next_usuario_id, ')'
    )
);

PREPARE usuario_sequence_stmt FROM @usuario_sequence_sql;
EXECUTE usuario_sequence_stmt;
DEALLOCATE PREPARE usuario_sequence_stmt;

INSERT INTO admin_residuosDB.transportista (
    id_transportista,
    nombre,
    apellido,
    nombre_fantasia,
    cuit,
    cuil,
    telefono,
    email,
    usuario_id,
    barrio,
    calle,
    altura,
    departamento,
    codigo_postal,
    localidad,
    provincia,
    estado
)
SELECT
    t.id_transportista,
    COALESCE(NULLIF(TRIM(t.nombre), ''), 'Transportista'),
    COALESCE(NULLIF(TRIM(t.apellido), ''), 'Legacy'),
    NULL,
    CASE
        WHEN CHAR_LENGTH(REGEXP_REPLACE(COALESCE(t.cuit, ''), '[^0-9]', '')) = 11
            THEN REGEXP_REPLACE(COALESCE(t.cuit, ''), '[^0-9]', '')
        ELSE NULL
    END,
    NULL,
    NULLIF(TRIM(t.telefono), ''),
    COALESCE(NULLIF(TRIM(t.email), ''), CONCAT('transportista-', t.id_transportista, '@legacy.local')),
    u.id,
    NULL,
    NULLIF(TRIM(t.domicilio), ''),
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    CAST(t.estado AS UNSIGNED)
FROM residuos_Patologicos.transportista t
JOIN admin_residuosDB.usuarios u
    ON u.email = COALESCE(NULLIF(TRIM(t.email), ''), CONCAT('transportista-', t.id_transportista, '@legacy.local'))
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    apellido = VALUES(apellido),
    telefono = VALUES(telefono),
    usuario_id = VALUES(usuario_id),
    calle = VALUES(calle),
    estado = VALUES(estado);

SET @default_transportista_id := (
    SELECT id_transportista
    FROM admin_residuosDB.transportista
    WHERE id_transportista = 1
    LIMIT 1
);

-- ---------------------------------------------------------------------------
-- Tipos de residuo
-- ---------------------------------------------------------------------------
INSERT INTO admin_residuosDB.tipo_residuo (
    id,
    codigo,
    nombre_tipo,
    estado_actividad,
    id_transportista
)
SELECT
    tr.id,
    LEFT(COALESCE(NULLIF(TRIM(tr.codigo), ''), CONCAT('LEGACY-', tr.id)), 20),
    COALESCE(NULLIF(TRIM(tr.nombre), ''), CONCAT('Tipo legacy ', tr.id)),
    CAST(tr.estado AS UNSIGNED),
    @default_transportista_id
FROM residuos_Patologicos.tipo_residuo tr
ON DUPLICATE KEY UPDATE
    codigo = VALUES(codigo),
    nombre_tipo = VALUES(nombre_tipo),
    estado_actividad = VALUES(estado_actividad),
    id_transportista = VALUES(id_transportista);

-- ---------------------------------------------------------------------------
-- Generadores: se migran como EMPRESA para conservar razon social/nombre libre.
-- La direccion legacy queda en domicilio.calle; telefonos pasan a tabla telefono.
-- ---------------------------------------------------------------------------
INSERT INTO admin_residuosDB.generador (
    id,
    tipo_generador,
    email,
    usuario_id,
    transportista_id,
    legajo,
    estado,
    barrio,
    calle,
    altura,
    departamento,
    codigo_postal,
    localidad,
    provincia
)
SELECT
    g.id,
    'EMPRESA',
    NULL,
    NULL,
    @default_transportista_id,
    NULLIF(TRIM(g.legajo), ''),
    CAST(g.estado AS UNSIGNED),
    NULL,
    NULLIF(TRIM(g.direccion), ''),
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
FROM residuos_Patologicos.generador g
ON DUPLICATE KEY UPDATE
    transportista_id = VALUES(transportista_id),
    legajo = VALUES(legajo),
    estado = VALUES(estado),
    calle = VALUES(calle);

INSERT INTO admin_residuosDB.generador_empresa (
    id,
    cuit,
    razon_social,
    nombre_fantasia
)
WITH normalizados AS (
    SELECT
        g.id,
        g.nombre,
        REGEXP_REPLACE(COALESCE(g.cuit, ''), '[^0-9]', '') AS cuit_digits,
        ROW_NUMBER() OVER (
            PARTITION BY REGEXP_REPLACE(COALESCE(g.cuit, ''), '[^0-9]', '')
            ORDER BY g.id
        ) AS cuit_ordinal
    FROM residuos_Patologicos.generador g
)
SELECT
    n.id,
    CASE
        WHEN CHAR_LENGTH(n.cuit_digits) = 11 AND n.cuit_ordinal = 1
            THEN n.cuit_digits
        ELSE CONCAT('9', LPAD(n.id, 10, '0'))
    END,
    COALESCE(NULLIF(TRIM(n.nombre), ''), CONCAT('Generador legacy ', n.id)),
    COALESCE(NULLIF(TRIM(n.nombre), ''), CONCAT('Generador legacy ', n.id))
FROM normalizados n
ON DUPLICATE KEY UPDATE
    razon_social = VALUES(razon_social),
    nombre_fantasia = VALUES(nombre_fantasia);

INSERT INTO admin_residuosDB.telefono (
    numero,
    tipo,
    generador_id,
    estado
)
SELECT
    TRIM(g.telefono),
    'CELULAR',
    g.id,
    true
FROM residuos_Patologicos.generador g
WHERE g.telefono IS NOT NULL
  AND TRIM(g.telefono) <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM admin_residuosDB.telefono existente
      WHERE existente.generador_id = g.id
        AND existente.numero = TRIM(g.telefono)
  );

-- ---------------------------------------------------------------------------
-- Hojas de ruta, certificados, tickets y residuos
-- ---------------------------------------------------------------------------
INSERT INTO admin_residuosDB.hoja_ruta (
    id,
    fecha_inicio,
    fecha_fin,
    numero_hoja_ruta,
    id_transportista
)
SELECT
    h.id,
    h.fecha_inicio,
    h.fecha_fin,
    h.id,
    @default_transportista_id
FROM residuos_Patologicos.hoja_ruta h
ON DUPLICATE KEY UPDATE
    fecha_inicio = VALUES(fecha_inicio),
    fecha_fin = VALUES(fecha_fin),
    numero_hoja_ruta = VALUES(numero_hoja_ruta),
    id_transportista = VALUES(id_transportista);

INSERT INTO admin_residuosDB.certificado (
    id,
    numero_certificado,
    id_transportista,
    mes,
    anio
)
SELECT
    c.id,
    c.id,
    COALESCE(c.id_transportista, @default_transportista_id),
    c.mes,
    c.`año`
FROM residuos_Patologicos.certificado c
ON DUPLICATE KEY UPDATE
    numero_certificado = VALUES(numero_certificado),
    id_transportista = VALUES(id_transportista),
    mes = VALUES(mes),
    anio = VALUES(anio);

INSERT INTO admin_residuosDB.ticket_control (
    id_ticket,
    numero_ticket,
    id_transportista,
    id_hoja_ruta,
    fecha_emision,
    horario,
    estado,
    id_generador,
    peso_total
)
SELECT
    t.id_ticket,
    t.id_ticket,
    COALESCE(t.id_transportista, @default_transportista_id),
    t.hoja_ruta_id,
    COALESCE(t.fecha_emision, CURDATE()),
    '00:00:00',
    CAST(t.estado AS UNSIGNED),
    t.generador_id,
    0
FROM residuos_Patologicos.ticket_control t
ON DUPLICATE KEY UPDATE
    numero_ticket = VALUES(numero_ticket),
    id_transportista = VALUES(id_transportista),
    id_hoja_ruta = VALUES(id_hoja_ruta),
    fecha_emision = VALUES(fecha_emision),
    horario = VALUES(horario),
    estado = VALUES(estado),
    id_generador = VALUES(id_generador);

INSERT INTO admin_residuosDB.residuo (
    id,
    tipo_residuo_id,
    peso,
    ticket_id
)
SELECT
    r.id,
    r.tipo_residuo,
    CAST(r.peso AS DECIMAL(12,3)),
    r.ticket_control
FROM residuos_Patologicos.residuo r
ON DUPLICATE KEY UPDATE
    tipo_residuo_id = VALUES(tipo_residuo_id),
    peso = VALUES(peso),
    ticket_id = VALUES(ticket_id);

UPDATE admin_residuosDB.ticket_control t
LEFT JOIN (
    SELECT ticket_id, COALESCE(SUM(peso), 0) AS peso_total
    FROM admin_residuosDB.residuo
    GROUP BY ticket_id
) pesos ON pesos.ticket_id = t.id_ticket
SET t.peso_total = COALESCE(pesos.peso_total, 0);

COMMIT;
