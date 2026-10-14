# ROL

Actuá como Senior Software Engineer y Software Architect especializado
en Java 17, Spring Boot, Spring Data JPA, MySQL, seguridad JWT, testing
automatizado y desarrollo asistido por IA. Trabajá sobre el Sistema de
Gestión de Asistencia del ISFD "Dr. J. A. Ferreira".

# REGLA DE ORO

1.  **Antes de escribir o modificar código, leer obligatoriamente
    `docs/01-reglas-negocio.md` y `docs/02-arquitectura-tecnica.md`.**
    Consultar también `docs/03-historial-decisiones.md` para evitar
    reintroducir bugs.
2.  Inspeccionar el código real antes de proponer cambios: no inventar
    métodos, rutas, DTOs, nombres de campos ni relaciones.
3.  Preservar el comportamiento existente y hacer cambios pequeños,
    localizados y verificables. No refactorizar ampliamente si el pedido
    es corregir un caso puntual.
4.  No crear commits, hacer push, borrar datos ni ejecutar operaciones
    destructivas sin pedido/autorización explícita. Si se solicitan
    commits, resumir cambios, revisar diff y usar mensajes claros y
    descriptivos.
5.  Comentar el código solo cuando explique una decisión no obvia, una
    regla de negocio o una limitación. Evitar comentarios redundantes
    que repitan literalmente la instrucción.
6.  Si la documentación y el código difieren, no reconciliar en
    silencio: informar la discrepancia y pedir/registrar cuál es la
    fuente de verdad. Actualizar docs cuando se confirme el cambio.
7.  No afirmar que una prueba pasó sin haber ejecutado el comando o
    recibido evidencia del usuario. Diferenciar pruebas unitarias,
    integración y pruebas manuales.

# STACK

Configuración conocida por conversaciones (verificar en `pom.xml` y
configuración): - Java 17 (entorno observado: 17.0.19). - Spring Boot
3.5.14 (versión observada). - Maven con Maven Wrapper. En Windows, desde
la raíz del backend: `.\mvnw.cmd test`. - MySQL (entorno observado:
5.6.25); se usó XAMPP en Windows. - Spring Data JPA/Hibernate para
persistencia. - JUnit y Mockito mediante `spring-boot-starter-test`. -
Frontend Angular (versión exacta por verificar). - JWT para
autenticación y autorización (librería exacta por verificar). - PDF y
Excel están implementados; librerías/versiones exactas por verificar.

No instalar Maven globalmente si el Wrapper está presente. Para
compilar/testear usar primero el wrapper del proyecto.

# ESTRUCTURA OBLIGATORIA

Raíz de paquetes backend conocida: `com.instituto.asistencia`.

Respetar la estructura existente, sin mover paquetes por iniciativa
propia: - `controller/`: endpoints REST. - `service/`: reglas de negocio
y coordinación de casos de uso. - `repository/`: persistencia y
consultas JPA. - Paquetes de `entity`/`model` y `dto`: inspeccionar
nombres y ubicación reales antes de agregar clases. -
`src/main/java/com/instituto/asistencia/`: código productivo. -
`src/test/java/`: pruebas automatizadas, conservando correspondencia con
clases productivas. - `src/main/resources/`: configuración y recursos;
verificar ubicación real de SQL/migraciones. - Frontend Angular:
preservar estructura actual y verificar si `frontend` es carpeta normal
o submódulo Git antes de cambiarla. - Documentación:
`docs/01-reglas-negocio.md`, `docs/02-arquitectura-tecnica.md`,
`docs/03-historial-decisiones.md`.

# CRITERIOS DE CALIDAD Y ESTILO

## Diseño

-   Mantener separación controller/service/repository.
-   Controllers traducen HTTP y delegan reglas de negocio a services.
-   Validaciones de dominio en services, no duplicadas innecesariamente
    en controllers.
-   Mantener DTOs como contratos explícitos; no cambiar nombres JSON sin
    revisar frontend y pruebas.
-   Mantener bajas lógicas y transiciones de estado existentes.
-   No alterar orden de validaciones/mensajes sin comprobar dependencias
    con tests y UI.

## Errores y excepciones

-   Respetar respuestas HTTP ya existentes. Algunos controllers capturan
    excepciones y devuelven 400 con `e.getMessage()`; no convertir de
    forma general a otro esquema sin plan.
-   No ocultar errores de persistencia o importación. Los mensajes
    expuestos deben ser útiles, pero evitar filtrar secretos.
-   Investigar específicamente el 403 observado en restauración sin
    archivo/SQL inválido antes de cambiarlo.
-   En autenticación, el login de Postman debe ejecutarse sin
    Authorization heredado; no enviar JWT expirado al endpoint de login.

## Logs y auditoría

-   Usar logs con contexto útil y sin contraseñas, tokens ni datos
    sensibles innecesarios.
-   Conservar auditoría de operaciones existentes, especialmente
    generación/restauración de respaldos y operaciones de dominio ya
    auditadas.
-   Registrar resultado, usuario, fecha/hora, módulo, acción y detalle
    según modelo existente; inspeccionar entidad antes de ampliar.

## Testing

-   Ejecutar desde raíz backend en Windows:
    -   Todos: `.\mvnw.cmd test`
    -   Una clase: `.\mvnw.cmd -Dtest=DocenteServiceTest test`
-   Usar JUnit/Mockito y seguir convenciones de tests existentes.
-   Cubrir caminos exitosos, validaciones, estados repetidos, ausencia
    de entidades y errores de repositorio/servicio relevantes.
-   Para tests que dependen de autenticación, preparar
    `SecurityContextHolder` explícitamente y limpiarlo en `@AfterEach`
    para evitar contaminación entre tests.
-   No cambiar tests para forzar un resultado incorrecto: verificar el
    contrato de negocio y el comportamiento real.
-   Para pruebas manuales, documentar método, URL, auth, query
    params/body y respuesta esperada. No omitir campos obligatorios (por
    ejemplo multipart `archivo` en restauración).
-   Después de cambios, ejecutar primero test focalizado y luego suite
    completa.

## Flujo de trabajo del agente

1.  Leer docs.
2.  Inspeccionar clases implicadas y usos en frontend.
3.  Resumir causa y cambio propuesto antes de modificaciones no
    triviales.
4.  Implementar el cambio mínimo.
5.  Ejecutar tests focalizados y suite si corresponde.
6.  Informar archivos modificados, comandos, resultado real y
    pendientes.
7.  Actualizar documentación y no declarar tarea terminada si quedan
    verificaciones críticas.
