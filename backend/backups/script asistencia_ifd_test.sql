-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.6.25


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema asistencia_ifd
--

CREATE DATABASE IF NOT EXISTS asistencia_ifd_test;
USE asistencia_ifd_test;

--
-- Definition of table `asistencias`
--

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
  UNIQUE KEY `UKo46lwnpsi62khsm9ondcucq07` (`docente_id`,`turno_id`,`fecha`),
  KEY `fk_asistencia_turno` (`turno_id`),
  KEY `fk_asistencia_jornada` (`jornada_id`),
  CONSTRAINT `fk_asistencia_docente` FOREIGN KEY (`docente_id`) REFERENCES `docentes` (`id`),
  CONSTRAINT `fk_asistencia_jornada` FOREIGN KEY (`jornada_id`) REFERENCES `jornadas` (`id`),
  CONSTRAINT `fk_asistencia_turno` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--
-- Definition of table `auditoria_respaldos`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;


--
-- Definition of table `docente_turnos`
--

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



--
-- Definition of table `docentes`
--

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


--
-- Definition of table `instituciones`
--

DROP TABLE IF EXISTS `instituciones`;
CREATE TABLE `instituciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--
-- Definition of table `jornadas`
--

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
  UNIQUE KEY `UKp3x8xin938t8xrhyge41y0l1e` (`fecha`,`turno_id`),
  KEY `fk_jornada_turno` (`turno_id`),
  KEY `fk_jornada_usuario_apertura` (`usuario_apertura_id`),
  KEY `fk_jornada_usuario_cierre` (`usuario_cierre_id`),
  CONSTRAINT `fk_jornada_turno` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`),
  CONSTRAINT `fk_jornada_usuario_apertura` FOREIGN KEY (`usuario_apertura_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `fk_jornada_usuario_cierre` FOREIGN KEY (`usuario_cierre_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



--
-- Definition of table `respaldos`
--

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

--

--
-- Definition of table `turnos`
--

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


--
-- Definition of table `usuarios`
--

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






/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;