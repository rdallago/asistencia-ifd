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
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `asistencias`
--

/*!40000 ALTER TABLE `asistencias` DISABLE KEYS */;
INSERT INTO `asistencias` (`id`,`docente_id`,`turno_id`,`jornada_id`,`fecha`,`hora_ingreso`,`hora_egreso`,`estado`,`observaciones`,`fecha_creacion`,`fecha_modificacion`) VALUES 
 (1,1,2,1,'2026-06-19','18:25:00','23:35:00','PRESENTE','Asistencia completa','2026-06-19 12:35:20',NULL),
 (2,2,2,1,'2026-06-19','18:30:00',NULL,'SIN_EGRESO','No registro egreso','2026-06-19 12:35:20',NULL),
 (3,3,2,1,'2026-06-19',NULL,NULL,'SIN_FIRMA_TOTAL','Ausente','2026-06-19 12:35:20',NULL),
 (4,1,2,5,'2026-06-23','19:05:48','20:01:06','PRESENTE',NULL,'2026-06-23 19:05:48','2026-06-23 20:01:06'),
 (5,1,1,6,'2026-06-24','10:27:47',NULL,'PRESENTE',NULL,'2026-06-24 10:27:47',NULL),
 (6,1,3,8,'2026-06-26','08:33:39',NULL,'SIN_EGRESO',NULL,'2026-06-26 08:33:39','2026-06-26 18:51:33'),
 (7,2,3,8,'2026-06-26','08:33:49',NULL,'SIN_EGRESO',NULL,'2026-06-26 08:33:49','2026-06-26 18:51:34'),
 (8,3,3,8,'2026-06-26','08:34:00',NULL,'SIN_EGRESO',NULL,'2026-06-26 08:34:00','2026-06-26 18:51:34'),
 (9,4,3,8,'2026-06-26','08:34:08',NULL,'SIN_EGRESO',NULL,'2026-06-26 08:34:08','2026-06-26 18:51:34'),
 (11,2,2,9,'2026-07-04','23:11:31','23:12:24','PRESENTE',NULL,'2026-07-04 23:11:31','2026-07-04 23:12:36'),
 (12,1,3,10,'2026-07-07','11:13:08','11:18:07','PRESENTE',NULL,'2026-07-07 11:13:08','2026-07-07 11:23:36'),
 (13,2,3,10,'2026-07-07','11:13:22',NULL,'SIN_EGRESO',NULL,'2026-07-07 11:13:22','2026-07-07 11:23:36'),
 (14,3,3,10,'2026-07-07','11:13:30','11:14:03','PRESENTE',NULL,'2026-07-07 11:13:30','2026-07-07 11:23:36'),
 (15,4,3,10,'2026-07-07',NULL,NULL,'SIN_FIRMA_TOTAL',NULL,'2026-07-07 11:23:36',NULL),
 (16,1,1,11,'2026-07-07',NULL,NULL,'SIN_FIRMA_TOTAL',NULL,'2026-07-07 19:15:44',NULL),
 (17,3,1,11,'2026-07-07',NULL,NULL,'SIN_FIRMA_TOTAL',NULL,'2026-07-07 19:15:44',NULL),
 (18,1,2,12,'2026-07-07','20:12:07','20:24:58','PRESENTE',NULL,'2026-07-07 20:12:07','2026-07-07 20:24:58'),
 (19,1,2,13,'2026-07-21','23:04:13','23:04:52','PRESENTE',NULL,'2026-07-21 23:04:13','2026-07-21 23:27:01'),
 (20,2,2,13,'2026-07-21','23:04:35','23:04:45','PRESENTE',NULL,'2026-07-21 23:04:35','2026-07-21 23:27:01'),
 (21,1,2,15,'2026-07-24','21:58:11','21:58:43','PRESENTE',NULL,'2026-07-24 21:58:11','2026-07-24 22:00:44'),
 (22,2,2,15,'2026-07-24','21:59:27','21:59:51','PRESENTE',NULL,'2026-07-24 21:59:27','2026-07-24 22:00:44'),
 (23,3,2,15,'2026-07-24',NULL,NULL,'SIN_FIRMA_TOTAL',NULL,'2026-07-24 22:00:44',NULL);
/*!40000 ALTER TABLE `asistencias` ENABLE KEYS */;


--
-- Definition of table `auditoria_respaldos`
--

DROP TABLE IF EXISTS `auditoria_respaldos`;
CREATE TABLE `auditoria_respaldos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `usuario` varchar(100) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `operacion` varchar(30) NOT NULL,
  `resultado` varchar(20) NOT NULL,
  `archivo` varchar(255) DEFAULT NULL,
  `detalle` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `auditoria_respaldos`
--

/*!40000 ALTER TABLE `auditoria_respaldos` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditoria_respaldos` ENABLE KEYS */;


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
-- Dumping data for table `docente_turnos`
--

/*!40000 ALTER TABLE `docente_turnos` DISABLE KEYS */;
INSERT INTO `docente_turnos` (`id`,`docente_id`,`turno_id`,`activo`,`fecha_desde`,`fecha_hasta`) VALUES 
 (1,1,1,1,'2026-06-19',NULL),
 (2,2,2,1,'2026-06-19',NULL),
 (3,3,1,1,'2026-06-19',NULL),
 (4,1,3,0,'2026-06-26','2026-07-23'),
 (5,2,3,1,'2026-06-26',NULL),
 (6,3,3,1,'2026-06-26',NULL),
 (7,4,3,1,'2026-06-26',NULL),
 (8,1,2,1,'2026-07-21',NULL),
 (9,3,2,1,'2026-07-22',NULL),
 (10,1,3,1,'2026-07-27',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `docentes`
--

/*!40000 ALTER TABLE `docentes` DISABLE KEYS */;
INSERT INTO `docentes` (`id`,`dni`,`apellido`,`nombre`,`cargo`,`telefono`,`correo`,`direccion`,`fecha_nacimiento`,`activo`,`fecha_alta`) VALUES 
 (1,'29029583','Dal Lago','Ricardo','Profesor','3777000001','ricardo@ifd.edu.ar','Esquina','1980-05-10',1,'2026-06-19'),
 (2,'20123456','Gomez','Ana','Secretaria Academica','3777000002','ana@ifd.edu.ar','Esquina','1985-02-15',1,'2026-06-19'),
 (3,'18123456','Perez','Juan','Profesor','3777000003','juan@ifd.edu.ar','Esquina','1978-11-20',1,'2026-06-19'),
 (4,'30111222','Fernandez','Maria','Profesora','3777000005','maria@ifd.edu.ar','Esquina','1987-03-10',1,'2026-06-19'),
 (5,'32487124','Cáceres','Luis','Docente','3777282904','lcaceres@gmail.com','Los Aromos S/N','1988-11-12',1,'2026-07-23');
/*!40000 ALTER TABLE `docentes` ENABLE KEYS */;


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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `jornadas`
--

/*!40000 ALTER TABLE `jornadas` DISABLE KEYS */;
INSERT INTO `jornadas` (`id`,`fecha`,`turno_id`,`habilitada`,`cerrada`,`fecha_apertura`,`fecha_cierre`,`usuario_apertura_id`,`usuario_cierre_id`,`observaciones`,`cierre_automatico`) VALUES 
 (1,'2026-06-19',1,0,1,'2026-06-19 12:35:20','2026-06-20 22:34:36',NULL,NULL,'Jornada abierta turno noche',0),
 (2,'2026-06-18',1,1,1,'2026-06-18 12:35:20','2026-06-18 12:35:20',2,2,'Jornada cerrada de prueba',0),
 (3,'2026-06-21',1,0,1,'2026-06-21 07:52:47','2026-06-23 17:48:28',1,NULL,'',1),
 (4,'2026-06-21',2,0,1,'2026-06-21 07:52:59','2026-06-23 17:48:28',1,NULL,'',1),
 (5,'2026-06-23',2,0,1,'2026-06-23 17:48:58','2026-06-24 10:00:17',1,NULL,'',1),
 (6,'2026-06-24',1,0,1,'2026-06-24 10:20:22','2026-06-25 11:40:14',1,NULL,'',1),
 (7,'2026-06-24',2,0,1,'2026-06-24 12:27:08','2026-06-25 11:40:14',1,NULL,'',1),
 (8,'2026-06-26',3,0,1,'2026-06-26 08:33:10','2026-06-26 18:51:34',1,1,'',0),
 (9,'2026-07-04',2,0,1,'2026-07-04 22:18:08','2026-07-04 23:12:36',1,1,'',0),
 (10,'2026-07-07',3,0,1,'2026-07-07 11:12:57','2026-07-07 11:23:36',1,1,'',0),
 (11,'2026-07-07',1,0,1,'2026-07-07 12:48:50','2026-07-07 19:15:44',1,1,'',0),
 (12,'2026-07-07',2,0,1,'2026-07-07 19:15:58','2026-07-08 10:31:21',1,NULL,'abrí tarde',1),
 (13,'2026-07-21',2,0,1,'2026-07-21 23:03:03','2026-07-21 23:27:01',1,1,'',0),
 (14,'2026-07-22',3,0,1,'2026-07-22 09:48:01','2026-07-23 09:04:44',1,NULL,'',1),
 (15,'2026-07-24',2,0,1,'2026-07-24 21:56:04','2026-07-24 22:00:44',1,1,'',0);
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `turnos`
--

/*!40000 ALTER TABLE `turnos` DISABLE KEYS */;
INSERT INTO `turnos` (`id`,`institucion_id`,`nombre`,`hora_inicio`,`hora_fin`,`activo`,`cierre_automatico`) VALUES 
 (1,1,'Turno Tarde','13:30:00','18:10:00',1,NULL),
 (2,1,'Turno Noche','18:20:00','23:40:00',1,NULL),
 (3,1,'Turno Mañana','08:30:00','13:00:00',1,0x01);
/*!40000 ALTER TABLE `turnos` ENABLE KEYS */;


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

--
-- Dumping data for table `usuarios`
--

/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` (`id`,`username`,`password`,`rol`,`activo`,`docente_id`,`telefono`,`correo`,`fecha_nacimiento`,`fecha_alta`,`apellido`,`email`,`nombre`) VALUES 
 (1,'admin','$2a$10$H7.HHT/Lhzjulq3ZMYqzie1jVyXJ9s44R6zgtdM2lO8F5ioxZGhxa','ADMIN',1,1,'3777000001','admin@ifd.edu.ar','1980-05-10','2026-06-19','Dal Lago','ricardodallago@gmail.com','Ricardo'),
 (2,'secretaria','$2a$10$H7.HHT/Lhzjulq3ZMYqzie1jVyXJ9s44R6zgtdM2lO8F5ioxZGhxa','SECRETARIO',1,2,'3777000002','secretaria@ifd.edu.ar','1985-02-15','2026-06-19','Ojeda','estudioojeda@gmail.com','Mariana');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
