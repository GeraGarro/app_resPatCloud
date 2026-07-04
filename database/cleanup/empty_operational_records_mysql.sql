-- Vacia registros operativos para iniciar certificados, manifiestos y hojas de ruta desde cero.
-- No borra usuarios, transportistas, generadores, telefonos, vehiculos ni tipos de residuo.
--
-- Orden de borrado:
-- 1. residuo: detalle asociado a manifiestos/tickets.
-- 2. ticket_control: manifiestos/tickets.
-- 3. hoja_ruta: hojas de ruta.
-- 4. certificado: certificados.

START TRANSACTION;

DELETE FROM residuo;
DELETE FROM ticket_control;
DELETE FROM hoja_ruta;
DELETE FROM certificado;

ALTER TABLE residuo AUTO_INCREMENT = 1;
ALTER TABLE ticket_control AUTO_INCREMENT = 1;
ALTER TABLE hoja_ruta AUTO_INCREMENT = 1;
ALTER TABLE certificado AUTO_INCREMENT = 1;

COMMIT;

SELECT 'residuo' AS tabla, COUNT(*) AS registros FROM residuo
UNION ALL
SELECT 'ticket_control' AS tabla, COUNT(*) AS registros FROM ticket_control
UNION ALL
SELECT 'hoja_ruta' AS tabla, COUNT(*) AS registros FROM hoja_ruta
UNION ALL
SELECT 'certificado' AS tabla, COUNT(*) AS registros FROM certificado;
