# Sistema de Gestión de Asistencia Docente

**Instituto Superior de Formación Docente y Técnica «Dr. J. A. Ferreira» — Esquina, Corrientes, Argentina**

Aplicación web para organizar turnos y jornadas institucionales, registrar el ingreso y egreso de docentes, consultar novedades y generar reportes de asistencia e indicadores para la gestión.

> **Estado (14/10/2026):** backend compilado con éxito; 189 pruebas Maven aprobadas (0 fallos, 0 errores). La validación de despliegue en un servidor público o de operación multiinstitucional todavía no está realizada.

## Funcionalidades

- **Acceso y usuarios:** autenticación mediante JWT, gestión de usuarios y controles por roles.
- **Docentes:** alta, consulta, actualización, baja lógica y reactivación; validación de DNI.
- **Turnos:** definición de horarios, activación y desactivación.
- **Asignaciones:** vinculación de docentes a turnos con fechas de vigencia.
- **Jornadas:** apertura y cierre por turno; cierre anticipado autorizado y cierre automático de pendientes.
- **Asistencias:** registro de ingreso y egreso; validaciones de horario, turno asignado y jornada habilitada.
- **Estados:** presente (ingreso y egreso), sin egreso, sin firma total; estado abierto durante el proceso.
- **Consultas y reportes:** filtros por fecha, docente, turno y estado; PDF, Excel, indicadores, evolución histórica y Reporte Ejecutivo Institucional.
- **Respaldos:** generación y restauración de copias SQL con auditoría de operaciones.

La disponibilidad de cada opción depende del rol del usuario y de la interfaz instalada.

## Tecnologías

| Capa | Tecnología |
| --- | --- |
| Frontend | Angular, TypeScript, HTML, CSS |
| Backend | Java 17, Spring Boot 3.5.14, API REST |
| Persistencia | Spring Data JPA / Hibernate, MySQL |
| Seguridad | JWT, autorización por roles |
| Pruebas | JUnit 5, Mockito, Spring Boot Test, Maven |
| Construcción | Maven Wrapper (`mvnw.cmd`) y Angular CLI |
| Reportes | PDF y Excel (verificar bibliotecas exactas en `pom.xml`) |
| Entorno local utilizado | Windows, XAMPP / MySQL |

## Organización lógica

```text
Angular (interfaz)
      │ HTTP / JSON
      ▼
Spring Boot (controladores REST)
      ▼
Servicios (reglas de negocio)
      ▼
Repositorios JPA / Hibernate
      ▼
MySQL
```

Paquete base del backend: `com.instituto.asistencia`.

## Bases de datos y entornos

| Base | Finalidad |
| --- | --- |
| `asistencia_ifd` | Producción institucional |
| `asistencia_ifd_pruebas` | Copia de verificación histórica; no utilizar como base de tests destructivos |
| `asistencia_ifd_test` | Pruebas de integración automatizadas |

- `src/main/resources/application.properties`: configuración normal.
- `src/test/resources/application-test.properties`: sobrescribe la conexión cuando se activa el perfil `test`.
- Las pruebas de integración deben declarar `@ActiveProfiles("test")` y comprobar su aislamiento.
- Las pruebas con `@Transactional` revierten los cambios dentro de la transacción de prueba; **no** garantizan limpieza de llamadas HTTP reales, procesos asíncronos ni transacciones independientes.
- `StartupVerifier` y `CierreAutomaticoTask` se excluyen mediante `@Profile("!test")`.
- `spring.jpa.hibernate.ddl-auto=validate` verifica el esquema, **no impide escrituras de la aplicación**.

**Nunca publicar contraseñas, claves JWT, respaldos SQL con datos reales ni configuraciones sensibles en GitHub.** Configurar secretos mediante variables de entorno o un gestor de secretos en despliegues reales.

## Compilación y pruebas

Desde la carpeta `backend` en Windows:

```powershell
# Ejecutar las pruebas
.\mvnw.cmd test

# Generar JAR actualizado
.\mvnw.cmd -DskipTests clean package

# Iniciar el backend (solo con la base correcta y respaldo actualizado)
java -jar target\asistencia-0.0.1-SNAPSHOT.jar
```

Para Angular, desde la carpeta del frontend:

```powershell
npm install
npx ng build
```

**Integración Angular + JAR:** si el frontend se distribuye dentro del JAR, debe copiarse la última compilación Angular a los recursos estáticos del backend **antes** de empaquetarlo. La ubicación y el script exactos deben comprobarse en el repositorio.

## Reglas de negocio destacadas

1. Una jornada corresponde a una fecha y un turno y debe estar habilitada para firmar.
2. El docente debe existir y estar asignado al turno correspondiente.
3. No se permite duplicar el ingreso ni el egreso de una misma asistencia.
4. Una vez cerrada la jornada, no se registra un egreso ordinario posterior.
5. Los reportes distinguen **asistencia** (registros completos sobre asignaciones del día) y **calidad del registro** (registros completos sobre registros iniciados).
6. Las asignaciones vigentes actuales de toda la institución pueden diferir de las asignaciones históricas de una fecha.

## Seguridad y operación

Se recomienda usar HTTPS, políticas de contraseñas, roles mínimos necesarios, copias verificadas, procedimientos de restauración, registros de auditoría y control de acceso a los datos personales. El inicio normal ejecuta la verificación de jornadas pendientes, por lo que puede modificar registros existentes.

## Escalabilidad

El sistema fue desarrollado y aplicado al ISFDyT «Dr. J. A. Ferreira» de Esquina. La arquitectura web puede servir como base para una implementación en otros institutos superiores de Corrientes. **No debe considerarse multiinstitucional listo para producción sin validar aislamiento por institución, permisos, migración de datos, infraestructura, seguridad, concurrencia y soporte.**

## Documentación adicional

Consultar `DOCUMENTACION_TECNICA.md` para el modelo relacional, diagramas Mermaid (DER, clases, casos de uso, estados y actividades), arquitectura y fundamentación institucional; y `ayuda.html` para la guía de usuario.

## Estado de validación

- 14/10/2026: **189 pruebas Maven aprobadas**; JAR Spring Boot 3.5.14 compilado correctamente.
- Conexión del perfil `test` verificada contra `asistencia_ifd_test`.
- Rollback de prueba transaccional comprobado: registro ficticio no persistió.
- Reporte Ejecutivo Institucional verificado para el 13/10/2026.

## Licencia y responsables

**Licencia:** pendiente de definición por los titulares del proyecto. No se presupone una licencia de código abierto por alojarlo en GitHub.

**Responsabilidad institucional y técnica:** completar con las autorizaciones, créditos y datos de contacto correspondientes antes de publicar.
