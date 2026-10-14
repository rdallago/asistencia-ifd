# Arquitectura técnica

> **Fuente y alcance:** datos concretos recuperados del contexto de
> conversación disponible. Confirmar versiones y configuración
> directamente en `pom.xml`, `application.properties`/`application.yml`,
> entidades y controladores antes de usar como especificación
> contractual.

## 1. Stack conocido

-   Java: 17.0.19 (versión observada en entorno de pruebas).
-   Spring Boot: 3.5.14 (versión observada en el proyecto).
-   Build: Maven, con Maven Wrapper (`mvnw`, `mvnw.cmd`,
    `.mvn/wrapper/`).
-   Persistencia: MySQL; entorno de prueba reportó MySQL 5.6.25.
-   ORM: Hibernate/JPA (se observaron sentencias `Hibernate:`).
-   Testing: JUnit y Mockito mediante `spring-boot-starter-test`.
-   Frontend: Angular.
-   Entorno local mencionado: Windows y XAMPP (binario MySQL bajo
    `C:/xampp/mysql/bin/mysql.exe`).

**No confirmado aquí:** versiones exactas de Angular, JWT library,
POI/Excel, librería PDF, drivers, estrategia de migraciones
(Flyway/Liquibase/SQL manual), ni configuración de producción.

## 2. Paquetes y capas

Raíz de paquetes observada: `com.instituto.asistencia`.

Capas/módulos identificados por clases: - `controller`: endpoints REST
(por ejemplo `DocenteController`, `TurnoController`,
`JornadaController`, `AsistenciaController`, `RespaldoController`,
`AuditoriaRespaldoController`, `UsuarioController`,
`AsignacionController`). - `service`: reglas de negocio (por ejemplo
`DocenteService`, `TurnoService`, `JornadaService`, `AsistenciaService`,
`RespaldoService`, `AuditoriaRespaldoService`, `ReportePdfService`). -
`repository`: acceso JPA (por ejemplo `DocenteRepository`,
`JornadaRepository`, `AsistenciaRepository`, `DocenteTurnoRepository`,
`UsuarioRepository`). - Entidades/modelos JPA y DTOs existen; sus
subpaquetes exactos no están confirmados en el contexto recuperado.

## 3. Endpoints confirmados por controladores

### Docentes

Base `/api/docentes`: - `GET /api/docentes` - `GET /api/docentes/{id}` -
`POST /api/docentes` - `PUT /api/docentes/{id}` -
`DELETE /api/docentes/{id}`

### Turnos

Base `/turnos` (no `/api/turnos`): - `GET /turnos` - `POST /turnos` -
`PUT /turnos/{id}` - `PUT /turnos/{id}/desactivar` -
`PUT /turnos/{id}/activar`

### Jornadas

Base `/api/jornadas`: - `POST /api/jornadas/abrir` --- query params
`usuarioId`, `turnoId`, opcional `observaciones` (default vacío). -
`POST /api/jornadas/cerrar` --- query params `usuarioId`, `turnoId`,
opcional `observaciones`, opcional `forzarCierre` (default false). -
`GET /api/jornadas/abierta` --- `fecha`, `turnoId`. -
`GET /api/jornadas/buscar` --- `fecha`, `turnoId`. -
`GET /api/jornadas` - `GET /api/jornadas/turno/{turnoId}` -
`GET /api/jornadas/abiertas`

### Usuarios

Base `/api/usuarios`: - `GET /api/usuarios` - `GET /api/usuarios/{id}` -
`GET /api/usuarios/buscar?texto=...` - `POST /api/usuarios` -
`PUT /api/usuarios/{id}` - `DELETE /api/usuarios/{id}` (desactivar) -
`PUT /api/usuarios/{id}/activar`

DTO `UsuarioRequest`: `username`, `password`, `nombre`, `apellido`,
`email`, `rol`, `docenteId`.

### Asignaciones

Base `/asignaciones`: - `GET /asignaciones` -
`GET /asignaciones/activas` - `POST /asignaciones` con
`AsignacionRequest` (campos exactos por verificar en clase). -
`PUT /asignaciones/{id}/desasignar` - `PUT /asignaciones/{id}/reactivar`

### Asistencia

Base `/api/asistencia`: - `POST /api/asistencia/entrada` --- JSON
`AsistenciaRequest` con `dni`, `jornadaId`. -
`POST /api/asistencia/salida` --- JSON `AsistenciaRequest` con `dni`,
`jornadaId`. - `POST /api/asistencia/cierre` --- parámetro `turnoId`. -
`GET /api/asistencia/pdf` --- `fecha`, opcionales `turnoId`,
`docenteId`, `estado`. - `GET /api/asistencia/reporte` --- mismos
filtros. - `GET /api/asistencia/excel` --- mismos filtros. -
`GET /api/asistencia/estadisticas?fecha=...` -
`GET /api/asistencia/estado-general?fecha=...` -
`GET /api/asistencia/indicadores` -
`GET /api/asistencia/evolucion?fecha=...` (ventana de 30 días). -
`GET /api/asistencia/resumen-ejecutivo?fecha=...` -
`GET /api/asistencia/comparacion-historica?fecha=...` -
`GET /api/asistencia/estadisticas-turno?fecha=...&turnoId=...` -
`GET /api/asistencia/ranking-ausencias?desde=...&hasta=...`

DTO `AsistenciaRequest`: `dni`, `jornadaId`. DTO de reporte:
`ReporteAsistenciaDTO` (campos exactos por verificar).

### Respaldos y auditoría

Base `/api/respaldos`: - `GET /api/respaldos/prueba` -
`POST /api/respaldos/generar` --- sin body; usuario proviene del
`SecurityContext`. - `POST /api/respaldos/restaurar` ---
multipart/form-data, campo obligatorio `archivo` (File).

Base `/api/respaldos/auditoria`: - `GET /api/respaldos/auditoria` ---
`@PreAuthorize("hasRole('ADMIN')")`.

### Institución

Existe `InstitucionController` bajo `/api/instituciones`, pero rutas y
DTOs exactos no quedaron confirmados en el contexto disponible. Se
indicó que solo hay un registro base y no una gestión completa en UI.

## 4. Entidades y relaciones conocidas

-   `Docente`: incluye DNI, fecha de alta y estado activo/inactivo;
    otros campos por verificar.
-   `Turno`: `id`, `institucion` (ManyToOne), `nombre`, `horaInicio`,
    `horaFin`, `activo=true`, `cierreAutomatico=true`.
-   `Jornada`: fecha, turno, habilitada/cerrada, datos de
    apertura/cierre, usuario de apertura/cierre y observaciones (nombres
    exactos completos por verificar).
-   `Asistencia`: docente, turno, jornada, hora de ingreso, hora de
    egreso, estado y campos de auditoría.
-   `DocenteTurno`: relación/asignación docente-turno, estado/vigencia;
    más campos por verificar.
-   `Usuario`: cuenta con username/password, datos personales/email, rol
    y posible relación a docente.
-   `AuditoriaRespaldo`: `id`, `usuario`, `fechaHora`, `resultado`,
    `archivo`, `detalle`, `modulo`, `accion`.

## 5. Persistencia, transacciones y migraciones

-   Persistencia mediante repositorios Spring Data JPA/Hibernate y
    MySQL.
-   `DocenteRepository` ofrece búsquedas por DNI, existencia por DNI,
    docentes activos, conteo de activos y orden por apellido/nombre.
-   `JornadaRepository` y `AsistenciaRepository` soportan búsquedas por
    fecha/turno y listados usados por estadísticas y reportes.
-   Se observaron sentencias SQL generadas por Hibernate en consola.
-   No hay evidencia suficiente en el contexto recuperado para afirmar
    que se haya acordado Flyway, Liquibase o un esquema de migración
    formal. Verificar `pom.xml`, `application.properties` y carpeta de
    recursos SQL.
-   No modificar esquema o relaciones sin inspeccionar entidades y datos
    existentes; preferir cambios pequeños y validar contra MySQL local.
