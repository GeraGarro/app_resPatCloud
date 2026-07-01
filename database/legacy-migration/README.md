# Migracion de base legacy Donweb

Extraccion realizada desde el servidor Donweb.

Archivos generados en el servidor:

- `/root/app_resPatCloud/backups/residuos_Patologicos_20260701_195546.sql`
- `/root/app_resPatCloud/backups/residuos_Patologicos_20260701_195546.sql.gz`
- `/root/app_resPatCloud/backups/residuos_Patologicos_schema_20260701_195546.sql`

Copias locales no versionadas:

- `server_exports/residuos_Patologicos_20260701_195546.sql`
- `server_exports/residuos_Patologicos_schema_20260701_195546.sql`

`server_exports/` queda ignorado por Git porque contiene datos reales.

## Estado legacy

Base legacy: `residuos_Patologicos`.

Tablas y conteos observados:

- `certificado`: 12
- `generador`: 88
- `hoja_ruta`: 50
- `residuo`: 46
- `ticket_control`: 61
- `tipo_residuo`: 5
- `transportista`: 1

Inconsistencias detectadas:

- `generador.cuit` tiene duplicados: `20273764381`, `20264143978`, `27324913624`.
- `generador.cuit` tiene un valor `NULL`.
- La direccion legacy es texto libre; el modelo nuevo usa `Domicilio` normalizado. El script inicial conserva esa direccion en `domicilio.calle`.
- El modelo legacy no tiene `usuarios`; el modelo nuevo exige usuario asociado al transportista. El script crea usuarios tecnicos para transportistas.
- El modelo legacy no tiene `horario`, `numero_ticket`, `numero_hoja_ruta`, `numero_certificado` ni `peso_total`. El script usa IDs historicos como numeracion inicial, `00:00:00` como horario y recalcula `peso_total`.
- Los usuarios tecnicos se crean con un hash temporal no usable para login real. Antes de operar productivamente hay que resetear la clave desde el flujo de la aplicacion o actualizar el hash.

## Flujo recomendado

1. Levantar el nuevo sistema y dejar que Hibernate cree `admin_residuosDB`.
2. Importar el dump legacy como base separada `residuos_Patologicos`.
3. Revisar `legacy_to_current_mysql.sql`.
4. Ejecutar el script contra MySQL.
5. Validar conteos, tickets, pesos y reportes desde la aplicacion.

El script esta pensado para una base destino limpia. Si la base nueva ya tiene datos reales, no ejecutarlo sin adaptar la estrategia de IDs.
