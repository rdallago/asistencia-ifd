-- =====================================================
-- SISTEMA DE ASISTENCIA DOCENTE
-- ISFDyT "Dr. J. A. Ferreira"
-- =====================================================

DROP DATABASE IF EXISTS asistencia_ifd;

CREATE DATABASE asistencia_ifd
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE asistencia_ifd;

-- =====================================================
-- INSTITUCIONES
-- =====================================================

CREATE TABLE instituciones (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(200) NOT NULL,

    direccion VARCHAR(250),

    telefono VARCHAR(50),

    correo VARCHAR(100)

);

-- =====================================================
-- TURNOS
-- =====================================================

CREATE TABLE turnos (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    institucion_id BIGINT NOT NULL,

    nombre VARCHAR(50) NOT NULL,

    hora_inicio TIME NOT NULL,

    hora_fin TIME NOT NULL,

    activo BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_turno_institucion
        FOREIGN KEY (institucion_id)
        REFERENCES instituciones(id)

);

-- =====================================================
-- DOCENTES
-- =====================================================

CREATE TABLE docentes (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    dni VARCHAR(20) NOT NULL UNIQUE,

    apellido VARCHAR(100) NOT NULL,

    nombre VARCHAR(100) NOT NULL,

    cargo VARCHAR(150),

    telefono VARCHAR(50),

    correo VARCHAR(100),

    direccion VARCHAR(250),

    fecha_nacimiento DATE,

    activo BOOLEAN NOT NULL DEFAULT TRUE,

    fecha_alta DATE NOT NULL

);

-- =====================================================
-- USUARIOS
-- =====================================================

CREATE TABLE usuarios (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(50) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    rol ENUM(
        'ADMIN',
        'SECRETARIO'
    ) NOT NULL,

    activo BOOLEAN NOT NULL DEFAULT TRUE,

    docente_id BIGINT NULL,

    telefono VARCHAR(50),

    correo VARCHAR(100),

    fecha_nacimiento DATE,

    fecha_alta DATE,

    CONSTRAINT fk_usuario_docente
        FOREIGN KEY (docente_id)
        REFERENCES docentes(id)

);

-- =====================================================
-- JORNADAS
-- =====================================================
CREATE TABLE jornadas (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    fecha DATE NOT NULL,

    turno_id BIGINT NOT NULL,

    habilitada BOOLEAN NOT NULL DEFAULT FALSE,

    cerrada BOOLEAN NOT NULL DEFAULT FALSE,

    fecha_apertura DATETIME NULL,

    fecha_cierre DATETIME NULL,

    usuario_apertura_id BIGINT NULL,

    usuario_cierre_id BIGINT NULL,

    observaciones VARCHAR(500),

    CONSTRAINT fk_jornada_turno
        FOREIGN KEY (turno_id)
        REFERENCES turnos(id),

    CONSTRAINT fk_jornada_usuario_apertura
        FOREIGN KEY (usuario_apertura_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_jornada_usuario_cierre
        FOREIGN KEY (usuario_cierre_id)
        REFERENCES usuarios(id),

    UNIQUE(fecha, turno_id)

);

-- =====================================================
-- ASISTENCIAS
-- =====================================================

CREATE TABLE asistencias (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    docente_id BIGINT NOT NULL,

    turno_id BIGINT NOT NULL,

    jornada_id BIGINT NOT NULL,

    fecha DATE NOT NULL,

    hora_ingreso TIME NULL,

    hora_egreso TIME NULL,

    estado ENUM(
        'ABIERTO',
        'PRESENTE',
        'SIN_EGRESO',
        'SIN_FIRMA_TOTAL'
    ) NOT NULL DEFAULT 'ABIERTO',

    observaciones VARCHAR(500),

    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    fecha_modificacion DATETIME NULL,

    CONSTRAINT fk_asistencia_docente
        FOREIGN KEY (docente_id)
        REFERENCES docentes(id),

    CONSTRAINT fk_asistencia_turno
        FOREIGN KEY (turno_id)
        REFERENCES turnos(id),

    CONSTRAINT fk_asistencia_jornada
        FOREIGN KEY (jornada_id)
        REFERENCES jornadas(id),

    UNIQUE (docente_id, turno_id, fecha)

);

-- =====================================================
-- DATOS DE PRUEBA
-- =====================================================

-- =====================================================
-- INSTITUCION
-- =====================================================

INSERT INTO instituciones
(
    nombre,
    direccion,
    telefono,
    correo
)
VALUES
(
    'Instituto Superior de Formacion Docente y Tecnica Dr. J. A. Ferreira',
    'Esquina - Corrientes',
    '3777-000000',
    'info@ifdferreira.edu.ar'
);

-- =====================================================
-- TURNOS
-- =====================================================

INSERT INTO turnos
(
    institucion_id,
    nombre,
    hora_inicio,
    hora_fin,
    activo
)
VALUES
(
    1,
    'Turno Tarde',
    '13:30:00',
    '18:10:00',
    TRUE
);

INSERT INTO turnos
(
    institucion_id,
    nombre,
    hora_inicio,
    hora_fin,
    activo
)
VALUES
(
    1,
    'Turno Noche',
    '18:20:00',
    '23:40:00',
    TRUE
);

-- =====================================================
-- DOCENTES
-- =====================================================

INSERT INTO docentes
(
    dni,
    apellido,
    nombre,
    cargo,
    telefono,
    correo,
    direccion,
    fecha_nacimiento,
    activo,
    fecha_alta
)
VALUES
(
    '22111222',
    'Dal Lago',
    'Ricardo',
    'Profesor',
    '3777000001',
    'ricardo@ifd.edu.ar',
    'Esquina',
    '1980-05-10',
    TRUE,
    CURDATE()
);

INSERT INTO docentes
(
    dni,
    apellido,
    nombre,
    cargo,
    telefono,
    correo,
    direccion,
    fecha_nacimiento,
    activo,
    fecha_alta
)
VALUES
(
    '20123456',
    'Gomez',
    'Ana',
    'Secretaria Academica',
    '3777000002',
    'ana@ifd.edu.ar',
    'Esquina',
    '1985-02-15',
    TRUE,
    CURDATE()
);

INSERT INTO docentes
(
    dni,
    apellido,
    nombre,
    cargo,
    telefono,
    correo,
    direccion,
    fecha_nacimiento,
    activo,
    fecha_alta
)
VALUES
(
    '18123456',
    'Perez',
    'Juan',
    'Profesor',
    '3777000003',
    'juan@ifd.edu.ar',
    'Esquina',
    '1978-11-20',
    TRUE,
    CURDATE()
);

INSERT INTO docentes
(
    dni,
    apellido,
    nombre,
    cargo,
    telefono,
    correo,
    direccion,
    fecha_nacimiento,
    activo,
    fecha_alta
)
VALUES
(
    '30111222',
    'Fernandez',
    'Maria',
    'Profesora',
    '3777000005',
    'maria@ifd.edu.ar',
    'Esquina',
    '1987-03-10',
    TRUE,
    CURDATE()
);

-- =====================================================
-- USUARIOS
-- =====================================================

INSERT INTO usuarios
(
    username,
    password,
    rol,
    activo,
    docente_id,
    telefono,
    correo,
    fecha_nacimiento,
    fecha_alta
)
VALUES
(
    'admin',
    '$2a$10$H7.HHT/Lhzjulq3ZMYqzie1jVyXJ9s44R6zgtdM2lO8F5ioxZGhxa',
    'ADMIN',
    TRUE,
    1,
    '3777000001',
    'admin@ifd.edu.ar',
    '1980-05-10',
    CURDATE()
);

INSERT INTO usuarios
(
    username,
    password,
    rol,
    activo,
    docente_id,
    telefono,
    correo,
    fecha_nacimiento,
    fecha_alta
)
VALUES
(
    'secretaria',
    '$2a$10$H7.HHT/Lhzjulq3ZMYqzie1jVyXJ9s44R6zgtdM2lO8F5ioxZGhxa',
    'SECRETARIO',
    TRUE,
    2,
    '3777000002',
    'secretaria@ifd.edu.ar',
    '1985-02-15',
    CURDATE()
);


-- =====================================================
-- JORNADAS DE PRUEBA
-- =====================================================

INSERT INTO jornadas
(
    fecha,
    turno_id,
    habilitada,
    cerrada,
    fecha_apertura,
    fecha_cierre,
    usuario_apertura_id,
    usuario_cierre_id,
    observaciones
)
VALUES
(
    CURDATE(),
    1,
    TRUE,
    FALSE,
    NOW(),
    NULL,
    2,
    NULL,
    'Jornada abierta turno noche'
);

INSERT INTO jornadas
(
    fecha,
    turno_id,
    habilitada,
    cerrada,
    fecha_apertura,
    fecha_cierre,
    usuario_apertura_id,
    usuario_cierre_id,
    observaciones
)
VALUES
(
    DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    1,
    TRUE,
    TRUE,
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    2,
    2,
    'Jornada cerrada de prueba'
);





-- =====================================================
-- --Tabla de Docente - Turno
-- =====================================================

CREATE TABLE docente_turnos (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    docente_id BIGINT NOT NULL,

    turno_id BIGINT NOT NULL,

    activo BOOLEAN NOT NULL DEFAULT TRUE,

    fecha_desde DATE NOT NULL,

    fecha_hasta DATE NULL,

    CONSTRAINT fk_docente_turno_docente
        FOREIGN KEY (docente_id)
        REFERENCES docentes(id),

    CONSTRAINT fk_docente_turno_turno
        FOREIGN KEY (turno_id)
        REFERENCES turnos(id)

);




-- =====================================================
-- DOCENTE-TURNO DE PRUEBA
-- =====================================================


INSERT INTO docente_turnos
(
    docente_id,
    turno_id,
    activo,
    fecha_desde
)
VALUES
(
    1,
    1,
    TRUE,
    CURDATE()
);

INSERT INTO docente_turnos
(
    docente_id,
    turno_id,
    activo,
    fecha_desde
)
VALUES
(
    2,
    2,
    TRUE,
    CURDATE()
);

INSERT INTO docente_turnos
(
    docente_id,
    turno_id,
    activo,
    fecha_desde
)
VALUES
(
    3,
    1,
    TRUE,
    CURDATE()
);


-- =====================================================
-- ASISTENCIAS DE PRUEBA
-- =====================================================

-- PRESENTE

INSERT INTO asistencias
(
    docente_id,
    turno_id,
    jornada_id,
    fecha,
    hora_ingreso,
    hora_egreso,
    estado,
    observaciones
)
VALUES
(
    1,
    2,
    1,
    CURDATE(),
    '18:25:00',
    '23:35:00',
    'PRESENTE',
    'Asistencia completa'
);

-- SIN EGRESO

INSERT INTO asistencias
(
    docente_id,
    turno_id,
    jornada_id,
    fecha,
    hora_ingreso,
    estado,
    observaciones
)
VALUES
(
    2,
    2,
    1,
    CURDATE(),
    '18:30:00',
    'SIN_EGRESO',
    'No registro egreso'
);

-- SIN FIRMA

INSERT INTO asistencias
(
    docente_id,
    turno_id,
    jornada_id,
    fecha,
    estado,
    observaciones
)
VALUES
(
    3,
    2,
    1,
    CURDATE(),
    'SIN_FIRMA_TOTAL',
    'Ausente'
);

-- =====================================================
-- CONSULTAS
-- =====================================================

SELECT * FROM instituciones;
SELECT * FROM turnos;
SELECT * FROM docentes;
SELECT * FROM usuarios;
SELECT * FROM jornadas;
SELECT * FROM asistencias;