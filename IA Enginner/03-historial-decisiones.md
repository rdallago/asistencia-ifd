# Historial de decisiones, bugs y pendientes

> **Nota:** cronología parcial basada en el contexto recuperado; las
> fechas exactas de muchas decisiones no están disponibles. No atribuir
> fecha precisa sin revisar el historial íntegro.

## 1. Cronología disponible

### Junio de 2026

-   Se trabajó en errores SQL y correcciones del proyecto de asistencia.
-   Se revisó un error HTTP 403 en Spring Boot al intentar cargar un
    docente.
-   Se corrigió `DocenteService.guardar()` para asignar
    `fechaAlta = LocalDate.now()`, validar datos y controlar DNI
    duplicado.

### Agosto de 2026

-   Se consolidaron pruebas unitarias Maven con JUnit/Mockito.
-   Entorno observado: Java 17.0.19, Spring Boot 3.5.14, MySQL 5.6.25.
-   `DocenteServiceTest` ya existía y JUnit/Mockito funcionaban; se
    detectó inicialmente que el contexto de seguridad era null en el
    test de `guardar()`. La solución específica final debe cotejarse con
    el test actual.

### Septiembre de 2026

-   Se completaron pruebas unitarias de servicios principales y se
    avanzó con pruebas manuales en Postman.
-   Se ajustaron estados repetidos de docentes, turnos y usuarios para
    evitar desactivaciones/activaciones duplicadas.
-   Se corrigió la estadística general para considerar jornadas
    habilitadas o cerradas y así no perder registros históricos
    cerrados.
-   Se verificaron reportes PDF/Excel, estadísticas, evolución, resumen
    ejecutivo, comparación histórica, ranking de ausencias y
    estadísticas por turno.
-   Se probaron generación/restauración de backups y auditoría.
-   Se verificó que una restauración SQL inválida genera registro de
    auditoría ERROR y que el backend seguía respondiendo tras el fallo.

## 2. Bugs complejos y soluciones

### 2.1 Login bloqueado por JWT vencido

**Síntoma:** login devolvía 403 aunque las credenciales podían ser
correctas.\
**Causa observada:** Postman enviaba un Authorization Bearer heredado
con JWT expirado. `JwtFilter` intentaba extraer claims y fallaba antes
de llegar al login.\
**Solución operativa:** poner login en `No Auth` y quitar el header
Authorization heredado; revisar auth de colección/carpeta.

### 2.2 Duplicación de DNI en docentes

**Comportamiento requerido:** no duplicar docentes activos por DNI;
permitir reactivación del inactivo existente.\
**Solución:** `DocenteService.guardar()` valida y reactiva el registro
inactivo con el mismo DNI, y rechaza si ya hay uno activo.

### 2.3 Estados repetidos

-   Docente ya dado de baja: rechazar segundo borrado lógico.
-   Turno ya desactivado: `El turno ya está desactivado`.
-   Turno ya activo: `El turno ya está activo`.
-   Usuario ya desactivado/activo: mensajes específicos para cada caso.
    Estas validaciones se añadieron para hacer explícitas las
    transiciones de estado.

### 2.4 Estadísticas históricas inconsistentes

**Síntoma:** estadísticas generales podían excluir jornadas cerradas,
porque su flag `habilitada` era false al cerrarse.\
**Solución:** incluir jornadas donde `habilitada=true` **o**
`cerrada=true` al construir el conjunto de asignaciones de la fecha.

### 2.5 Restauración de SQL inválido

**Síntoma:** MySQL devolvió error de sintaxis 1064 al intentar importar
texto que no era un backup válido.\
**Resultado:** `RespaldoService` lanzó excepción y registró auditoría
`ERROR` con el detalle de MySQL. Luego `GET /api/docentes` devolvió 200,
confirmando que la aplicación seguía operativa. **Pendiente:** revisar
por qué Postman mostró 403 para restauración inválida y restauración sin
archivo, aunque la lógica del servicio llegó a ejecutarse en el caso
inválido.

### 2.6 Error Git de submódulo

Se observó:
`fatal: no submodule mapping found in .gitmodules for path 'frontend'`.
No hay constancia suficiente aquí de la solución final. Revisar
`.gitmodules`, índice Git y estructura real antes de intervenir.

## 3. Estado de pruebas conocido

### Unitarias

Se informó que pasan: - `DocenteServiceTest`: 30 - `TurnoServiceTest`:
25 - `JornadaServiceTest`: 25 - `AsistenciaServiceTest`: 28 -
`ReportePdfServiceTest`: 15 - `RespaldoServiceTest`: 11 - También se
indicó que pasan `AsignacionServiceTest`, `UsuarioServiceTest`,
`InstitucionServiceTest`, `DocenteTurnoServiceTest` y
`AuditoriaRespaldoServiceTest` (conteos no recuperados).

### Integración/manuales

-   Docentes: 18/18 confirmadas.
-   Turnos: bloque confirmado como funcionando.
-   Usuarios: 16/16 confirmadas.
-   Asignaciones: 9/9 confirmadas.
-   Asistencia: bloque de 27 pruebas considerado completo.
-   Jornadas: confirmadas pruebas 1--18; consultas adicionales deben
    revisarse.
-   Respaldos/auditoría: generación, restauración exitosa, auditoría
    admin y restricciones de acceso verificadas; anomalía 403 con
    archivo ausente/SQL inválido pendiente de aclaración.

## 4. Deuda técnica y pendientes

1.  Investigar respuestas 403 de `/api/respaldos/restaurar` cuando falta
    archivo o se envía SQL inválido; contrastar Postman, filtros,
    excepciones y status real devuelto.
2.  Confirmar las consultas restantes de Jornada: `GET /api/jornadas`,
    `/api/jornadas/turno/{turnoId}`, `/api/jornadas/abiertas` y
    `/api/jornadas/abierta`.
3.  Confirmar si se probó PDF filtrado por turno/docente/estado; no
    repetir si ya consta en Postman.
4.  Revisar endpoints exactos de institución y decidir si el alcance
    implementado requiere pruebas manuales.
5.  Completar pruebas end-to-end de Angular (login, formularios,
    asistencia, jornada, reportes, backups si están expuestos).
6.  Revisar el problema Git de `frontend` como submódulo si sigue
    vigente.
7.  Confirmar stack real en archivos del repositorio, especialmente
    versiones, dependencias, configuración de MySQL y migraciones.
8.  Documentar contrato completo de DTOs y entidades leyendo el código
    actual; los resúmenes disponibles no contienen todos los campos.
