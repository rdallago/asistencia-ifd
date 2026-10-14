# Reglas de negocio y dominio

> **Fuente y alcance:** consolidación del contexto disponible de las
> conversaciones del proyecto entre junio y septiembre de 2026. No se
> dispone aquí del historial íntegro ni de todos los archivos fuente.
> Los puntos marcados **Por verificar** no deben tratarse como
> requisitos confirmados.

## 1. Resumen ejecutivo

Sistema de Gestión de Asistencia para el ISFD "Dr. J. A. Ferreira", en
Esquina. Backend Java con Spring Boot y MySQL, con frontend Angular.
Permite administrar docentes, usuarios, turnos, jornadas y asignaciones;
registrar entradas/salidas; producir reportes (PDF/Excel), indicadores y
respaldos con auditoría.

## 2. Módulos identificados

-   Autenticación y autorización mediante JWT y roles.
-   Docentes: alta, consulta, modificación y baja lógica/reactivación.
-   Usuarios: administración de cuentas y roles.
-   Turnos: configuración horaria y estado activo.
-   Jornadas: apertura/cierre por usuario y turno, habilitación y cierre
    forzado.
-   Asignaciones: relación docente-turno con vigencia/estado.
-   Asistencia: entradas, salidas, cierres y estados de registro.
-   Reportes: consultas filtradas, PDF, Excel, estadísticas y análisis.
-   Respaldos: generación/restauración SQL.
-   Auditoría: registro de operaciones relevantes.
-   Institución: entidad institucional; se indicó que solo existe un
    registro base y que no hay gestión completa implementada.

## 3. Reglas y casos de uso

### 3.1 Docentes

-   El DNI identifica al docente para las operaciones de asistencia.
-   No se permite crear otro docente activo con el mismo DNI. Mensaje
    observado: `Ya existe un docente activo con ese DNI`.
-   Si existe un docente inactivo con ese DNI, el alta puede reactivarlo
    en lugar de crear otro registro.
-   La baja es lógica; no se elimina físicamente el docente.
-   No se puede volver a desactivar un docente ya eliminado/inactivo; se
    corrigió para informar el estado repetido.
-   `guardar()` asigna `fechaAlta` con `LocalDate.now()` y ejecuta
    validaciones antes de persistir.
-   Se registran operaciones de auditoría. Los detalles exactos de todos
    los campos de validación deben cotejarse con `DocenteService`.

### 3.2 Usuarios y seguridad

-   El inicio de sesión requiere nombre de usuario y contraseña.
-   No se admite username nulo/vacío:
    `El nombre de usuario es obligatorio`.
-   No se admite password nulo/vacío: `La contraseña es obligatoria`.
-   No se admite username duplicado:
    `Ya existe un usuario con ese nombre`.
-   La actualización exige username y, si cambia, verifica que no
    pertenezca a otro usuario.
-   Activar/desactivar tiene control de estado: repetir la acción
    devuelve `El usuario ya está activo` o
    `El usuario ya está desactivado`.
-   Las rutas protegidas requieren JWT válido.
-   La auditoría de respaldos requiere rol ADMIN
    (`@PreAuthorize("hasRole('ADMIN')")`).
-   Los intentos de acceso sin autenticación o con rol insuficiente a la
    auditoría devolvieron 403 en Postman.
-   Roles admitidos deben confirmarse en el enum real; `DIRECTOR` fue
    rechazado como rol inválido en una prueba.
-   Al probar login, usar **No Auth** y quitar cualquier header
    Authorization heredado: un JWT vencido enviado al login provocó 403
    antes de llegar al controlador.

### 3.3 Turnos

-   Un turno tiene nombre, hora de inicio, hora de fin, estado activo y
    configuración de cierre automático.
-   Nombre y horarios son obligatorios en la entidad.
-   La hora de inicio no puede ser posterior a la hora de fin.
-   No se admite duplicar el nombre de un turno activo.
-   Desactivar un turno puede estar impedido si existen jornadas
    asociadas (regla implementada en el servicio; confirmar mensaje
    exacto).
-   No se permite desactivar dos veces: `El turno ya está desactivado`.
-   No se permite activar dos veces: `El turno ya está activo`.

### 3.4 Jornadas

-   La jornada se abre asociada a un usuario y un turno, con
    observaciones opcionales.
-   Se consulta por fecha y turno, y se puede listar por turno o listar
    jornadas abiertas.
-   El cierre recibe usuario, turno, observaciones opcionales y
    `forzarCierre`.
-   Si se intenta cerrar una jornada ya cerrada, se informa
    `La jornada ya fue cerrada`.
-   Si se intenta cerrar antes de la hora de fin y no se fuerza, el
    servicio señala `CIERRE_ANTICIPADO`; el cierre forzado permite
    continuar.
-   Al cerrar: `cerrada=true`, `habilitada=false`, se registra
    fecha/hora de cierre, usuario de cierre y observaciones, y
    `cierreAutomatico=false`.
-   Para estadísticas históricas, una jornada cuenta si está habilitada
    **o** cerrada; esto evita excluir jornadas cerradas por tener
    `habilitada=false`.

### 3.5 Asistencia

#### Entrada

Validaciones observadas en este orden: 1. La jornada debe existir
(`Jornada no encontrada`). 2. Debe estar habilitada
(`La jornada no está habilitada`). 3. No debe estar cerrada
(`La jornada ya fue cerrada`). 4. El docente debe existir
(`Docente no encontrado`). 5. El docente debe estar asignado al turno
(`El docente no está asignado al turno <nombre>`). 6. Debe haber
comenzado el turno (`Aún no comenzó el turno`). 7. No debe existir ya
una entrada (`La entrada ya fue registrada`).

Al registrar entrada, se guarda hora de ingreso y estado `PRESENTE`;
respuesta observada: `Entrada registrada correctamente`.

#### Salida

-   Requiere jornada existente, habilitada y no cerrada.
-   El docente debe existir y estar asignado al turno.
-   Debe existir una asistencia/entrada previa
    (`No existe ingreso registrado`).
-   No puede repetirse la salida (`El egreso ya fue registrado`).
-   Respuesta observada: `Salida registrada correctamente`.

#### Cierre y estados

Al procesar cierre de jornada para los docentes asignados: - Sin
asistencia/entrada: `SIN_FIRMA_TOTAL`. - Con entrada y sin salida:
`SIN_EGRESO`. - Con entrada y salida: `PRESENTE`.

### 3.6 Reportes e indicadores

-   Reportes por fecha, con filtros opcionales por turno, docente y
    estado.
-   Parámetro correcto del filtro de docente: `docenteId` (no
    `docente_id`).
-   PDF y Excel deben respetar los filtros.
-   Estadística por turno: docentes asignados, presentes, sin egreso,
    sin firma total y porcentaje.
-   Estadística general clasifica docentes asignados contra asistencias
    de las jornadas de la fecha, considerando jornadas habilitadas o
    cerradas.
-   Estado general se calcula sobre registros iniciados: presentes /
    registros iniciados. Umbrales observados: \>=90 EXCELENTE; \>=75
    BUENO; \>=60 REGULAR; inferior CRÍTICO. Si no hay actividad:
    `SIN ACTIVIDAD`.
-   Existen consultas de indicadores institucionales, evolución de 30
    días, resumen ejecutivo, comparación histórica y ranking de
    ausencias.
-   La definición completa de cada DTO, las fórmulas de otros
    indicadores y el tratamiento de casos límite deben verificarse en
    código.

### 3.7 Respaldos y auditoría

-   Generación de respaldo SQL y restauración desde archivo multipart.
-   Restauración usa obligatoriamente el campo de formulario `archivo`
    de tipo File.
-   Auditoría registra, al menos, usuario, fecha/hora, resultado,
    archivo, detalle, módulo y acción.
-   Acciones observadas: `GENERAR`, `RESTAURAR`; módulo `RESPALDOS`;
    resultados `EXITOSO` y `ERROR`.
-   Un SQL inválido es rechazado por MySQL; el servicio registra
    resultado `ERROR` con el diagnóstico.
-   La restauración exitosa devuelve
    `{"mensaje":"Respaldo restaurado correctamente."}`.
-   **Por verificar:** el endpoint de restauración sin archivo devolvió
    403 en Postman, aunque por contrato de parámetro requerido cabría
    esperar un error de petición; investigar antes de cambiar código.
-   Tras un intento de restauración inválida, se verificó que
    `GET /api/docentes` seguía respondiendo 200.

## 4. Datos de prueba conocidos (no usar como valores permanentes)

En una prueba de fecha 2026-09-15 se observaron 13 docentes registrados,
11 activos, 6 turnos, 10 asignaciones vigentes y 0 jornadas abiertas.
Para Turno Noche (id 2): 4 asignados, 1 presente, 0 sin egreso, 3 sin
firma y 25%. Son datos de esa instancia, no reglas ni valores fijos.
