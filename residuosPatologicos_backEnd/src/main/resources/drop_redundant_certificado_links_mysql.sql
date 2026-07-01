-- Limpieza opcional de columnas redundantes luego de pasar a la regla:
-- Certificado = transportista + mes/anio; tickets y hojas se derivan por fecha.
--
-- Ejecutar sobre la base de datos de la aplicacion. El script es idempotente
-- para MySQL 8: si la FK o columna no existe, no falla.

SET @schema_name := DATABASE();

SET @fk_ticket_cert := NULL;
SELECT constraint_name
INTO @fk_ticket_cert
FROM information_schema.key_column_usage
WHERE table_schema = @schema_name
  AND table_name = 'ticket_control'
  AND column_name = 'id_certificado'
  AND referenced_table_name = 'certificado'
LIMIT 1;

SET @sql := IF(
  @fk_ticket_cert IS NULL,
  'SELECT ''ticket_control.id_certificado sin FK redundante''',
  CONCAT('ALTER TABLE ticket_control DROP FOREIGN KEY `', @fk_ticket_cert, '`')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ticket_column_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'ticket_control'
    AND column_name = 'id_certificado'
);

SET @sql := IF(
  @ticket_column_exists = 0,
  'SELECT ''ticket_control.id_certificado ya fue eliminada''',
  'ALTER TABLE ticket_control DROP COLUMN id_certificado'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_hoja_cert := NULL;
SELECT constraint_name
INTO @fk_hoja_cert
FROM information_schema.key_column_usage
WHERE table_schema = @schema_name
  AND table_name = 'hoja_ruta'
  AND column_name = 'id_certificado'
  AND referenced_table_name = 'certificado'
LIMIT 1;

SET @sql := IF(
  @fk_hoja_cert IS NULL,
  'SELECT ''hoja_ruta.id_certificado sin FK redundante''',
  CONCAT('ALTER TABLE hoja_ruta DROP FOREIGN KEY `', @fk_hoja_cert, '`')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @hoja_column_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'hoja_ruta'
    AND column_name = 'id_certificado'
);

SET @sql := IF(
  @hoja_column_exists = 0,
  'SELECT ''hoja_ruta.id_certificado ya fue eliminada''',
  'ALTER TABLE hoja_ruta DROP COLUMN id_certificado'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
