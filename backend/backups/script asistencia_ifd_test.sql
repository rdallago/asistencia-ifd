CREATE DATABASE IF NOT EXISTS asistencia_ifd_test;
USE asistencia_ifd_test;

-- 1. Tablas independientes (sin claves foráneas)
DROP TABLE IF EXISTS `auditoria_respaldos`;
CREATE TABLE `auditoria_respaldos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `usuario` varchar(100) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `operacion` varchar(30) DEFAULT NULL,
  `resultado` varchar(20) NOT NULL,
  `archivo` varchar(255) DEFAULT NULL,
  `detalle` text,
  `accion` varchar(50) DEFAULT NULL,
  `modulo` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `docentes`;
CREATE TABLE `docentes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dni` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cargo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_alta` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `instituciones`;
CREATE TABLE `instituciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `pacientes`;
CREATE TABLE `pacientes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `dni` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `telefono` varchar(50) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;


-- 2. Tablas que dependen de docentes o instituciones
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` enum('ADMIN','SECRETARIO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `docente_id` bigint(20) DEFAULT NULL,
  `telefono` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `fecha_alta` date DEFAULT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_usuario_docente` (`docente_id`),
  CONSTRAINT `fk_usuario_docente` FOREIGN KEY (`docente_id`) REFERENCES `docentes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `turnos`;
CREATE TABLE `turnos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `institucion_id` bigint(20) NOT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `cierre_automatico` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_turno_institucion` (`institucion_id`),
  CONSTRAINT `fk_turno_institucion` FOREIGN KEY (`institucion_id`) REFERENCES `instituciones` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `historia_clinica`;
CREATE TABLE `historia_clinica` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `paciente_id` bigint(20) DEFAULT NULL,
  `fecha_creacion` date NOT NULL,
  `resumen_clinico` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `paciente_id` (`paciente_id`),
  CONSTRAINT `fk_historia_paciente` FOREIGN KEY (`paciente_id`) REFERENCES `pacientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;


-- 3. Tablas con dependencias múltiples (Turnos, Usuarios, Docentes)
DROP TABLE IF EXISTS `docente_turnos`;
CREATE TABLE `docente_turnos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `docente_id` bigint(20) NOT NULL,
  `turno_id` bigint(20) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_desde` date NOT NULL,
  `fecha_hasta` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_docente_turno_docente` (`docente_id`),
  KEY `fk_docente_turno_turno` (`turno_id`),
  CONSTRAINT `fk_docente_turno_docente` FOREIGN KEY (`docente_id`) REFERENCES `docentes` (`id`),
  CONSTRAINT `fk_docente_turno_turno` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `jornadas`;
CREATE TABLE `jornadas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `turno_id` bigint(20) NOT NULL,
  `habilitada` tinyint(1) NOT NULL DEFAULT '0',
  `cerrada` tinyint(1) NOT NULL DEFAULT '0',
  `fecha_apertura` datetime DEFAULT NULL,
  `fecha_cierre` datetime DEFAULT NULL,
  `usuario_apertura_id` bigint(20) DEFAULT NULL,
  `usuario_cierre_id` bigint(20) DEFAULT NULL,
  `observaciones` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cierre_automatico` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `fecha` (`fecha`,`turno_id`),
  KEY `fk_jornada_turno` (`turno_id`),
  KEY `fk_jornada_usuario_apertura` (`usuario_apertura_id`),
  KEY `fk_jornada_usuario_cierre` (`usuario_cierre_id`),
  CONSTRAINT `fk_jornada_turno` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`),
  CONSTRAINT `fk_jornada_usuario_apertura` FOREIGN KEY (`usuario_apertura_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `fk_jornada_usuario_cierre` FOREIGN KEY (`usuario_cierre_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `respaldos`;
CREATE TABLE `respaldos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre_archivo` varchar(255) NOT NULL,
  `ruta_archivo` varchar(500) DEFAULT NULL,
  `tamano_archivo` bigint(20) DEFAULT NULL,
  `fecha_backup` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `usuario_id` bigint(20) DEFAULT NULL,
  `tipo_operacion` enum('BACKUP','RESTORE') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_respaldo_usuario` (`usuario_id`),
  CONSTRAINT `fk_respaldo_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- 4. Tablas finales que dependen de jornadas, turnos, docentes e historia clínica
DROP TABLE IF EXISTS `asistencias`;
CREATE TABLE `asistencias` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `docente_id` bigint(20) NOT NULL,
  `turno_id` bigint(20) NOT NULL,
  `jornada_id` bigint(20) NOT NULL,
  `fecha` date NOT NULL,
  `hora_ingreso` time DEFAULT NULL,
  `hora_egreso` time DEFAULT NULL,
  `estado` enum('ABIERTO','PRESENTE','SIN_EGRESO','SIN_FIRMA_TOTAL') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ABIERTO',
  `observaciones` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `docente_id` (`docente_id`,`turno_id`,`fecha`),
  KEY `fk_asistencia_turno` (`turno_id`),
  KEY `fk_asistencia_jornada` (`jornada_id`),
  CONSTRAINT `fk_asistencia_docente` FOREIGN KEY (`docente_id`) REFERENCES `docentes` (`id`),
  CONSTRAINT `fk_asistencia_jornada` FOREIGN KEY (`jornada_id`) REFERENCES `jornadas` (`id`),
  CONSTRAINT `fk_asistencia_turno` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `visitas`;
CREATE TABLE `visitas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `historia_id` bigint(20) NOT NULL,
  `turno_id` bigint(20) DEFAULT NULL,
  `fecha` date NOT NULL,
  `observacion` longtext COLLATE utf8mb4_unicode_ci,
  `resumen_ia` longtext COLLATE utf8mb4_unicode_ci,
  `estado_ia` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_visita` enum('PRIMERA_ENTREVISTA','SEGUIMIENTO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SEGUIMIENTO',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_visita_turno` (`turno_id`),
  KEY `fk_visita_historia` (`historia_id`),
  CONSTRAINT `fk_visita_historia` FOREIGN KEY (`historia_id`) REFERENCES `historia_clinica` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;