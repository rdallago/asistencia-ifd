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

CREATE DATABASE IF NOT EXISTS asistencia_ifd;
USE asistencia_ifd;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `asistencias`
--

/*!40000 ALTER TABLE `asistencias` DISABLE KEYS */;
INSERT INTO `asistencias` (`id`,`docente_id`,`turno_id`,`jornada_id`,`fecha`,`hora_ingreso`,`hora_egreso`,`estado`,`observaciones`,`fecha_creacion`,`fecha_modificacion`) VALUES 
 (1,1,2,1,'2026-06-19','18:25:00','23:35:00','PRESENTE','Asistencia completa','2026-06-19 12:35:20',NULL),
 (2,2,2,1,'2026-06-19','18:30:00',NULL,'SIN_EGRESO','No registro egreso','2026-06-19 12:35:20',NULL),
 (3,3,2,1,'2026-06-19',NULL,NULL,'SIN_FIRMA_TOTAL','Ausente','2026-06-19 12:35:20',NULL);
/*!40000 ALTER TABLE `asistencias` ENABLE KEYS */;


--
-- Definition of table `docente`
--

DROP TABLE IF EXISTS `docente`;
CREATE TABLE `docente` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cargo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dni` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_alta` date DEFAULT NULL,
  `fecha_nacimiento` date NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKxsbc345m2vlo2cms5rqv4g9v` (`dni`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `docente`
--

/*!40000 ALTER TABLE `docente` DISABLE KEYS */;
/*!40000 ALTER TABLE `docente` ENABLE KEYS */;


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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `docente_turnos`
--

/*!40000 ALTER TABLE `docente_turnos` DISABLE KEYS */;
INSERT INTO `docente_turnos` (`id`,`docente_id`,`turno_id`,`activo`,`fecha_desde`,`fecha_hasta`) VALUES 
 (1,1,1,1,'2026-06-19',NULL),
 (2,2,2,1,'2026-06-19',NULL),
 (3,3,1,1,'2026-06-19',NULL);
/*!40000 ALTER TABLE `docente_turnos` ENABLE KEYS */;


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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `docentes`
--

/*!40000 ALTER TABLE `docentes` DISABLE KEYS */;
INSERT INTO `docentes` (`id`,`dni`,`apellido`,`nombre`,`cargo`,`telefono`,`correo`,`direccion`,`fecha_nacimiento`,`activo`,`fecha_alta`) VALUES 
 (1,'22111222','Dal Lago','Ricardo','Profesor','3777000001','ricardo@ifd.edu.ar','Esquina','1980-05-10',1,'2026-06-19'),
 (2,'20123456','Gomez','Ana','Secretaria Academica','3777000002','ana@ifd.edu.ar','Esquina','1985-02-15',1,'2026-06-19'),
 (3,'18123456','Perez','Juan','Profesor','3777000003','juan@ifd.edu.ar','Esquina','1978-11-20',1,'2026-06-19'),
 (4,'30111222','Fernandez','Maria','Profesora','3777000005','maria@ifd.edu.ar','Esquina','1987-03-10',1,'2026-06-19');
/*!40000 ALTER TABLE `docentes` ENABLE KEYS */;


--
-- Definition of table `docentes_turnos`
--

DROP TABLE IF EXISTS `docentes_turnos`;
CREATE TABLE `docentes_turnos` (
  `docente_id` bigint(20) NOT NULL,
  `turno_id` bigint(20) NOT NULL,
  KEY `FKn2bj6q2po046tn9hwcn5kjo6l` (`turno_id`),
  KEY `FKc2kioksg7hfmmm98r257mvgxm` (`docente_id`),
  CONSTRAINT `FKc2kioksg7hfmmm98r257mvgxm` FOREIGN KEY (`docente_id`) REFERENCES `docente` (`id`),
  CONSTRAINT `FKn2bj6q2po046tn9hwcn5kjo6l` FOREIGN KEY (`turno_id`) REFERENCES `turnos` (`id`),
  CONSTRAINT `FKqr3ho3lolikqid6g46fckowt8` FOREIGN KEY (`docente_id`) REFERENCES `docentes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `docentes_turnos`
--

/*!40000 ALTER TABLE `docentes_turnos` DISABLE KEYS */;
/*!40000 ALTER TABLE `docentes_turnos` ENABLE KEYS */;


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
-- Dumping data for table `instituciones`
--

/*!40000 ALTER TABLE `instituciones` DISABLE KEYS */;
INSERT INTO `instituciones` (`id`,`nombre`,`direccion`,`telefono`,`correo`) VALUES 
 (1,'Instituto Superior de Formacion Docente y Tecnica Dr. J. A. Ferreira','Esquina - Corrientes','3777-000000','info@ifdferreira.edu.ar');
/*!40000 ALTER TABLE `instituciones` ENABLE KEYS */;


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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `jornadas`
--

/*!40000 ALTER TABLE `jornadas` DISABLE KEYS */;
INSERT INTO `jornadas` (`id`,`fecha`,`turno_id`,`habilitada`,`cerrada`,`fecha_apertura`,`fecha_cierre`,`usuario_apertura_id`,`usuario_cierre_id`,`observaciones`,`cierre_automatico`) VALUES 
 (1,'2026-06-19',1,0,1,'2026-06-19 12:35:20','2026-06-20 22:34:36',NULL,NULL,'Jornada abierta turno noche',0),
 (2,'2026-06-18',1,1,1,'2026-06-18 12:35:20','2026-06-18 12:35:20',2,2,'Jornada cerrada de prueba',0),
 (3,'2026-06-21',1,1,0,'2026-06-21 07:52:47',NULL,1,NULL,'',0),
 (4,'2026-06-21',2,1,0,'2026-06-21 07:52:59',NULL,1,NULL,'',0);
/*!40000 ALTER TABLE `jornadas` ENABLE KEYS */;


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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `turnos`
--

/*!40000 ALTER TABLE `turnos` DISABLE KEYS */;
INSERT INTO `turnos` (`id`,`institucion_id`,`nombre`,`hora_inicio`,`hora_fin`,`activo`,`cierre_automatico`) VALUES 
 (1,1,'Turno Tarde','13:30:00','18:10:00',1,NULL),
 (2,1,'Turno Noche','18:20:00','23:40:00',1,NULL);
/*!40000 ALTER TABLE `turnos` ENABLE KEYS */;


--
-- Definition of table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rol` enum('ADMIN','SECRETARIO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `docente_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK60dpth2sap0h6jy1vabk8o2xr` (`docente_id`),
  CONSTRAINT `FK60dpth2sap0h6jy1vabk8o2xr` FOREIGN KEY (`docente_id`) REFERENCES `docente` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `usuario`
--

/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;


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
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_usuario_docente` (`docente_id`),
  CONSTRAINT `fk_usuario_docente` FOREIGN KEY (`docente_id`) REFERENCES `docentes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `usuarios`
--

/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` (`id`,`username`,`password`,`rol`,`activo`,`docente_id`,`telefono`,`correo`,`fecha_nacimiento`,`fecha_alta`) VALUES 
 (1,'admin','$2a$10$H7.HHT/Lhzjulq3ZMYqzie1jVyXJ9s44R6zgtdM2lO8F5ioxZGhxa','ADMIN',1,1,'3777000001','admin@ifd.edu.ar','1980-05-10','2026-06-19'),
 (2,'secretaria','$2a$10$H7.HHT/Lhzjulq3ZMYqzie1jVyXJ9s44R6zgtdM2lO8F5ioxZGhxa','SECRETARIO',1,2,'3777000002','secretaria@ifd.edu.ar','1985-02-15','2026-06-19');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
