# Desarrollo local con IntelliJ y VS Code

Esta guia levanta el proyecto sin Docker: backend desde IntelliJ, frontend desde VS Code y MySQL local.

## 1. Base de datos local

Inicia MySQL local en el puerto `3306`.

Por defecto el backend usa:

- Base: `admin_residuosDB`
- Usuario: `root`
- Password: vacio

Si tu MySQL tiene password, configura en IntelliJ:

```text
SPRING_DATASOURCE_PASSWORD=tu_password
```

El perfil local crea la base si no existe.

## 2. Backend en IntelliJ

1. Abre la carpeta `residuosPatologicos_backEnd`.
2. Usa JDK 17.
3. Ejecuta la aplicacion Spring Boot.
4. En la configuracion de ejecucion, agrega:

```text
SPRING_PROFILES_ACTIVE=local
```

La API queda en:

```text
http://localhost:8080
```

Health check:

```text
http://localhost:8080/actuator/health
```

## 3. Frontend en VS Code

1. Abre la carpeta `residuosPatologicos_frontEnd/residuo-pat-frontend`.
2. Instala dependencias si hace falta:

```bash
npm install
```

3. Levanta Angular con proxy local:

```bash
npm run start:local
```

El frontend queda en:

```text
http://localhost:4200
```

El proxy redirige `/api` y `/actuator` hacia `http://localhost:8080`, por lo que el frontend puede usar rutas relativas sin CORS.

## 4. Orden recomendado

1. MySQL local.
2. Backend en IntelliJ.
3. Frontend en VS Code con `npm run start:local`.

## 5. Docker sigue disponible

Los archivos Docker y Compose quedan como alternativa para despliegue o pruebas aisladas.
