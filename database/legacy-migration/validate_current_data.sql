-- Validaciones posteriores a la migracion legacy -> admin_residuosDB.
-- Ejecutar despues de legacy_to_current_mysql.sql.

USE admin_residuosDB;

SELECT
    u.id AS usuario_id,
    u.email AS usuario_email,
    u.rol AS usuario_rol,
    t.id_transportista,
    t.email AS transportista_email
FROM usuarios u
JOIN transportista t ON t.usuario_id = u.id
WHERE u.id = 1
  AND t.id_transportista = 1;

SELECT
    COUNT(*) AS generadores_total,
    SUM(CASE WHEN transportista_id = 1 THEN 1 ELSE 0 END) AS generadores_transportista_1,
    SUM(CASE WHEN transportista_id <> 1 OR transportista_id IS NULL THEN 1 ELSE 0 END) AS generadores_fuera_transportista_1,
    SUM(CASE WHEN usuario_id IS NOT NULL THEN 1 ELSE 0 END) AS generadores_con_usuario_directo
FROM generador;

SELECT
    g.id,
    ge.razon_social,
    ge.cuit,
    g.transportista_id,
    g.usuario_id,
    g.legajo,
    g.estado
FROM generador g
JOIN generador_empresa ge ON ge.id = g.id
WHERE g.transportista_id <> 1
   OR g.transportista_id IS NULL
   OR g.usuario_id IS NOT NULL
ORDER BY g.id;

SELECT
    COUNT(*) AS telefonos_generadores
FROM telefono
WHERE generador_id IN (SELECT id FROM generador WHERE transportista_id = 1);
