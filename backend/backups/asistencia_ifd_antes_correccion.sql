-- MySQL dump 10.13  Distrib 5.6.25, for Win32 (x86)
--
-- Host: localhost    Database: asistencia_ifd
-- ------------------------------------------------------
-- Server version	5.6.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `asistencia_ifd`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `asistencia_ifd` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `asistencia_ifd`;

--
-- Table structure for table `asistencias`
--

DROP TABLE IF EXISTS `asistencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencias`
--

LOCK TABLES `asistencias` WRITE;
/*!40000 ALTER TABLE `asistencias` DISABLE KEYS */;
INSERT INTO `asistencias` VALUES (1,1,2,1,'2026-06-19','18:25:00','23:35:00','PRESENTE','Asistencia completa','2026-06-19 12:35:20',NULL),(2,2,2,1,'2026-06-19','18:30:00',NULL,'SIN_EGRESO','No registro egreso','2026-06-19 12:35:20',NULL),(3,3,2,1,'2026-06-19',NULL,NULL,'SIN_FIRMA_TOTAL','Ausente','2026-06-19 12:35:20',NULL),(4,1,2,5,'2026-06-23','19:05:48','20:01:06','PRESENTE',NULL,'2026-06-23 19:05:48','2026-06-23 20:01:06'),(5,1,1,6,'2026-06-24','10:27:47',NULL,'PRESENTE',NULL,'2026-06-24 10:27:47',NULL),(6,1,3,8,'2026-06-26','08:33:39',NULL,'SIN_EGRESO',NULL,'2026-06-26 08:33:39','2026-06-26 18:51:33'),(7,2,3,8,'2026-06-26','08:33:49',NULL,'SIN_EGRESO',NULL,'2026-06-26 08:33:49','2026-06-26 18:51:34'),(8,3,3,8,'2026-06-26','08:34:00',NULL,'SIN_EGRESO',NULL,'2026-06-26 08:34:00','2026-06-26 18:51:34'),(9,4,3,8,'2026-06-26','08:34:08',NULL,'SIN_EGRESO',NULL,'2026-06-26 08:34:08','2026-06-26 18:51:34'),(11,2,2,9,'2026-07-04','23:11:31','23:12:24','PRESENTE',NULL,'2026-07-04 23:11:31','2026-07-04 23:12:36'),(12,1,3,10,'2026-07-07','11:13:08','11:18:07','PRESENTE',NULL,'2026-07-07 11:13:08','2026-07-07 11:23:36'),(13,2,3,10,'2026-07-07','11:13:22',NULL,'SIN_EGRESO',NULL,'2026-07-07 11:13:22','2026-07-07 11:23:36'),(14,3,3,10,'2026-07-07','11:13:30','11:14:03','PRESENTE',NULL,'2026-07-07 11:13:30','2026-07-07 11:23:36'),(15,4,3,10,'2026-07-07',NULL,NULL,'SIN_FIRMA_TOTAL',NULL,'2026-07-07 11:23:36',NULL),(16,1,1,11,'2026-07-07',NULL,NULL,'SIN_FIRMA_TOTAL',NULL,'2026-07-07 19:15:44',NULL),(17,3,1,11,'2026-07-07',NULL,NULL,'SIN_FIRMA_TOTAL',NULL,'2026-07-07 19:15:44',NULL),(18,1,2,12,'2026-07-07','20:12:07','20:24:58','PRESENTE',NULL,'2026-07-07 20:12:07','2026-07-07 20:24:58'),(19,1,2,13,'2026-07-21','23:04:13','23:04:52','PRESENTE',NULL,'2026-07-21 23:04:13','2026-07-21 23:27:01'),(20,2,2,13,'2026-07-21','23:04:35','23:04:45','PRESENTE',NULL,'2026-07-21 23:04:35','2026-07-21 23:27:01'),(21,1,2,15,'2026-07-24','21:58:11','21:58:43','PRESENTE',NULL,'2026-07-24 21:58:11','2026-07-24 22:00:44'),(22,2,2,15,'2026-07-24','21:59:27','21:59:51','PRESENTE',NULL,'2026-07-24 21:59:27','2026-07-24 22:00:44'),(23,3,2,15,'2026-07-24',NULL,NULL,'SIN_FIRMA_TOTAL',NULL,'2026-07-24 22:00:44',NULL),(24,1,3,16,'2026-10-13','08:48:26',NULL,'PRESENTE',NULL,'2026-10-13 08:48:26',NULL);
/*!40000 ALTER TABLE `asistencias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditoria_respaldos`
--

DROP TABLE IF EXISTS `auditoria_respaldos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auditoria_respaldos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `usuario` varchar(100) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `operacion` varchar(30) NOT NULL,
  `resultado` varchar(20) NOT NULL,
  `archivo` varchar(255) DEFAULT NULL,
  `detalle` text,
  `accion` varchar(50) DEFAULT NULL,
  `modulo` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria_respaldos`
--

LOCK TABLES `auditoria_respaldos` WRITE;
/*!40000 ALTER TABLE `auditoria_respaldos` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditoria_respaldos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `docente_turnos`
--

DROP TABLE IF EXISTS `docente_turnos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `docente_turnos`
--

LOCK TABLES `docente_turnos` WRITE;
/*!40000 ALTER TABLE `docente_turnos` DISABLE KEYS */;
INSERT INTO `docente_turnos` VALUES (1,1,1,1,'2026-06-19',NULL),(2,2,2,1,'2026-06-19',NULL),(3,3,1,1,'2026-06-19',NULL),(4,1,3,0,'2026-06-26','2026-07-23'),(5,2,3,1,'2026-06-26',NULL),(6,3,3,1,'2026-06-26',NULL),(7,4,3,1,'2026-06-26',NULL),(8,1,2,1,'2026-07-21',NULL),(9,3,2,1,'2026-07-22',NULL),(10,1,3,1,'2026-07-27',NULL);
/*!40000 ALTER TABLE `docente_turnos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `docentes`
--

DROP TABLE IF EXISTS `docentes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `docentes`
--

LOCK TABLES `docentes` WRITE;
/*!40000 ALTER TABLE `docentes` DISABLE KEYS */;
INSERT INTO `docentes` VALUES (1,'29029583','Dal Lago','Ricardo','Profesor','3777000001','ricardo@ifd.edu.ar','Esquina','1980-05-10',1,'2026-06-19'),(2,'20123456','Gomez','Ana','Secretaria Academica','3777000002','ana@ifd.edu.ar','Esquina','1985-02-15',1,'2026-06-19'),(3,'18123456','Perez','Juan','Profesor','3777000003','juan@ifd.edu.ar','Esquina','1978-11-20',1,'2026-06-19'),(4,'30111222','Fernandez','Maria','Profesora','3777000005','maria@ifd.edu.ar','Esquina','1987-03-10',1,'2026-06-19'),(5,'32487124','Cáceres','Luis','Docente','3777282904','lcaceres@gmail.com','Los Aromos S/N','1988-11-12',1,'2026-07-23');
/*!40000 ALTER TABLE `docentes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historia_clinica`
--

DROP TABLE IF EXISTS `historia_clinica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `historia_clinica` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `paciente_id` bigint(20) DEFAULT NULL,
  `fecha_creacion` date NOT NULL,
  `resumen_clinico` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `paciente_id` (`paciente_id`),
  CONSTRAINT `fk_historia_paciente` FOREIGN KEY (`paciente_id`) REFERENCES `pacientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historia_clinica`
--

LOCK TABLES `historia_clinica` WRITE;
/*!40000 ALTER TABLE `historia_clinica` DISABLE KEYS */;
INSERT INTO `historia_clinica` VALUES (1,3,'2026-05-05','<p class=\"ql-align-justify\">Historia&nbsp;Clínica&nbsp;Psicológica&nbsp;Identificación:&nbsp;Paciente&nbsp;adulto,&nbsp;género&nbsp;no&nbsp;especificado,&nbsp;consulta&nbsp;por&nbsp;sintomatología&nbsp;ansiosa.&nbsp;Motivo&nbsp;de&nbsp;Consulta:&nbsp;Síndrome&nbsp;de&nbsp;ansiedad&nbsp;generalizada,&nbsp;caracterizado&nbsp;por&nbsp;tensión&nbsp;persistente&nbsp;e&nbsp;impaciencia&nbsp;exacerbada&nbsp;ante&nbsp;situaciones&nbsp;de&nbsp;incertidumbre&nbsp;o&nbsp;variabilidad&nbsp;en&nbsp;eventos&nbsp;cotidianos.&nbsp;Refiere&nbsp;estrés&nbsp;elevado&nbsp;al&nbsp;momento&nbsp;de&nbsp;la&nbsp;consulta.&nbsp;Evaluación&nbsp;Inicial:&nbsp;-&nbsp;Síntomas&nbsp;Emocionales:&nbsp;Predomina&nbsp;la&nbsp;hipervigilancia&nbsp;y&nbsp;anticipación&nbsp;catastrófica,&nbsp;con&nbsp;dificultad&nbsp;para&nbsp;modular&nbsp;respuestas&nbsp;ante&nbsp;estímulos&nbsp;ambiguos.&nbsp;-&nbsp;Síntomas&nbsp;Conductuales:&nbsp;Impaciencia&nbsp;marcada,&nbsp;posible&nbsp;evitación&nbsp;sutil&nbsp;de&nbsp;contextos&nbsp;impredecibles.&nbsp;-&nbsp;Patrones&nbsp;Detectados:&nbsp;Reactividad&nbsp;emocional&nbsp;desproporcionada&nbsp;frente&nbsp;a&nbsp;cambios&nbsp;mínimos&nbsp;en&nbsp;el&nbsp;entorno,&nbsp;sugiriendo&nbsp;rigidez&nbsp;cognitiva&nbsp;y&nbsp;baja&nbsp;tolerancia&nbsp;a&nbsp;la&nbsp;incertidumbre.&nbsp;Evolución:&nbsp;El&nbsp;cuadro&nbsp;se&nbsp;mantiene&nbsp;estable&nbsp;en&nbsp;intensidad,&nbsp;sin&nbsp;exacerbaciones&nbsp;agudas&nbsp;pero&nbsp;con&nbsp;cronificación&nbsp;de&nbsp;la&nbsp;sintomatología.&nbsp;No&nbsp;se&nbsp;observan&nbsp;mecanismos&nbsp;adaptativos&nbsp;consolidados;&nbsp;la&nbsp;tensión&nbsp;persiste&nbsp;como&nbsp;estado&nbsp;basal.&nbsp;Observaciones&nbsp;Clínicas:&nbsp;La&nbsp;ansiedad&nbsp;parece&nbsp;vinculada&nbsp;a&nbsp;expectativas&nbsp;rígidas&nbsp;sobre&nbsp;el&nbsp;control&nbsp;ambiental.&nbsp;La&nbsp;ausencia&nbsp;de&nbsp;variabilidad&nbsp;sintomá<strong>tica.&nbsp;Además&nbsp;le&nbsp;molesta&nbsp;la&nbsp;impuntualidad&nbsp;de&nbsp;la&nbsp;gente.&nbsp;Además&nbsp;refiere&nbsp;a&nbsp;situaciones&nbsp;complicadas&nbsp;socialmente.</strong></p><p class=\"ql-align-justify\"></p><p class=\"ql-align-justify\"></p><p class=\"ql-align-justify\"></p><p class=\"ql-align-justify\"></p>'),(6,2,'2026-05-07','<p>Historia&nbsp;Clínica&nbsp;Psicológica&nbsp;<strong><em>Identificación:</em></strong>&nbsp;Consulta&nbsp;por&nbsp;primera&nbsp;vez&nbsp;por&nbsp;sintomatología&nbsp;ansiosa.&nbsp;Motivo&nbsp;de&nbsp;Consulta:&nbsp;El&nbsp;paciente&nbsp;refiere&nbsp;&quot;sentirme&nbsp;constantemente&nbsp;nerviosa,&nbsp;con&nbsp;preocupaciones&nbsp;excesivas&nbsp;y&nbsp;dificultad&nbsp;para&nbsp;controlarlas&quot;.&nbsp;Describe&nbsp;tensión&nbsp;muscular,&nbsp;insomnio&nbsp;de&nbsp;conciliación&nbsp;y&nbsp;fatiga&nbsp;persistente.&nbsp;Los&nbsp;síntomas&nbsp;se&nbsp;exacerban&nbsp;en&nbsp;contextos&nbsp;laborales&nbsp;y&nbsp;sociales.&nbsp;Evaluación&nbsp;Inicial:&nbsp;-&nbsp;Cognitivo:&nbsp;Patrón&nbsp;de&nbsp;pensamiento&nbsp;rumiativo,&nbsp;focalizado&nbsp;en&nbsp;errores&nbsp;pasados&nbsp;y&nbsp;anticipación&nbsp;catastrófica&nbsp;del&nbsp;futuro.&nbsp;-&nbsp;Emocional:&nbsp;Labilidad&nbsp;afectiva,&nbsp;con&nbsp;episodios&nbsp;de&nbsp;irritabilidad&nbsp;y&nbsp;llanto&nbsp;espontáneo.&nbsp;Menciona&nbsp;sentimientos&nbsp;de&nbsp;impotencia.&nbsp;-&nbsp;Conductual:&nbsp;Evitación&nbsp;progresiva&nbsp;de&nbsp;reuniones&nbsp;sociales&nbsp;y&nbsp;sobrecompensación&nbsp;mediante&nbsp;perfeccionismo&nbsp;laboral.&nbsp;-&nbsp;Fisiológico:&nbsp;Taquicardia&nbsp;episódica,&nbsp;sudoración&nbsp;palmar&nbsp;y&nbsp;cefaleas&nbsp;tensionales.&nbsp;Evolución:&nbsp;En&nbsp;seguimiento&nbsp;a&nbsp;3&nbsp;meses,&nbsp;persiste&nbsp;la&nbsp;sintomatología&nbsp;ansiosa,&nbsp;aunque&nbsp;con&nbsp;mayor&nbsp;conciencia&nbsp;de&nbsp;sus&nbsp;desencadenantes.&nbsp;Se&nbsp;observa&nbsp;leve&nbsp;mejoría&nbsp;en&nbsp;la&nbsp;regulación&nbsp;emocional&nbsp;mediante&nbsp;técnicas&nbsp;de&nbsp;grounding,&nbsp;pero&nbsp;dificultad&nbsp;para&nbsp;implementar&nbsp;estrategias&nbsp;a.</p>'),(7,4,'2026-05-12','<p><strong><em><u>Historia&nbsp;Clínica&nbsp;</u></em></strong>Psicológica&nbsp;Identificación:&nbsp;Paciente&nbsp;femenina,&nbsp;edad&nbsp;no&nbsp;especificada,&nbsp;acude&nbsp;a&nbsp;consulta&nbsp;por&nbsp;primera&nbsp;vez.&nbsp;Motivo&nbsp;de&nbsp;Consulta:&nbsp;La&nbsp;paciente&nbsp;refiere&nbsp;sintomatología&nbsp;asociada&nbsp;a&nbsp;estrés&nbsp;permanente,&nbsp;caracterizado&nbsp;por&nbsp;insomnio&nbsp;y&nbsp;cansancio&nbsp;crónico.&nbsp;No&nbsp;se&nbsp;describen&nbsp;antecedentes&nbsp;médicos&nbsp;o&nbsp;psiquiátricos&nbsp;previos.&nbsp;Evaluación&nbsp;Inicial:&nbsp;Se&nbsp;observa&nbsp;un&nbsp;patrón&nbsp;de&nbsp;activación&nbsp;psicofisiológica&nbsp;persistente,&nbsp;compatible&nbsp;con&nbsp;estrés&nbsp;crónico.&nbsp;Los&nbsp;indicadores&nbsp;de&nbsp;insomnio&nbsp;sugieren&nbsp;alteraciones&nbsp;en&nbsp;el&nbsp;ritmo&nbsp;sueño-vigilia,&nbsp;posiblemente&nbsp;vinculadas&nbsp;a&nbsp;hiperactivación&nbsp;cognitiva&nbsp;o&nbsp;emocional.&nbsp;El&nbsp;cansancio&nbsp;crónico&nbsp;puede&nbsp;ser&nbsp;secundario&nbsp;a&nbsp;la&nbsp;falta&nbsp;de&nbsp;descanso&nbsp;reparador&nbsp;y/o&nbsp;a&nbsp;la&nbsp;sobrecarga&nbsp;psíquica.&nbsp;No&nbsp;se&nbsp;reportan&nbsp;síntomas&nbsp;depresivos&nbsp;o&nbsp;ansiosos&nbsp;adicionales&nbsp;en&nbsp;esta&nbsp;fase.&nbsp;Evolución&nbsp;Emocional&nbsp;y&nbsp;Conductual:&nbsp;La&nbsp;paciente&nbsp;presenta&nbsp;dificultad&nbsp;en&nbsp;la&nbsp;regulación&nbsp;emocional,&nbsp;con&nbsp;predominio&nbsp;de&nbsp;tensión&nbsp;sostenida.&nbsp;La&nbsp;ausencia&nbsp;de&nbsp;estrategias&nbsp;adaptativas&nbsp;eficaces&nbsp;podría&nbsp;perpetuar&nbsp;el&nbsp;ciclo&nbsp;estrés-insomnio-fatiga.&nbsp;No&nbsp;se&nbsp;identifican&nbsp;patrones&nbsp;de&nbsp;evitación&nbsp;o&nbsp;conductas&nbsp;compulsivas,&nbsp;pero&nbsp;la&nbsp;sintomatología&nbsp;actual&nbsp;sugiere&nbsp;un&nbsp;deterioro&nbsp;funcional&nbsp;significativo.&nbsp;Conclusiones&nbsp;C<strong>línicas.&nbsp;kdkkdkd</strong></p>'),(8,6,'2026-06-07','Historia Clínica Psicológica Nombre: [Nombre omitido por confidencialidad] Edad: [Edad] Ocupación: [Actividad laboral] Motivo de consulta: Ansiedad laboral, con manifestaciones de tensión persistente, preocupación excesiva por desempeño y dificultad para desconectar del ámbito profesional. Historia actual: El paciente refiere sintomatología ansiosa focalizada en su entorno laboral, con pensamientos recurrentes sobre demandas laborales, autoexigencia elevada y percepción de falta de control. Presenta inquietud psicomotriz, fatiga cognitiva y alteraciones del sueño (dificultad para conciliarlo). No describe crisis de angustia, pero sí un estado de hipervigilancia sostenido. Antecedentes personales: Sin historial psiquiátrico previo. Niega consumo de sustancias. Refiere patrones perfeccionistas desde la adolescencia. Antecedentes familiares: Sin datos relevantes de trastornos mentales en familiares directos. Área laboral/vinculaciones: Desempeña funciones con alta responsabilidad, en un clima organizacional percibido como demandante. Relaciones laborales superficiales, sin conflictos explícitos. Fuera del trabajo, red de.'),(9,7,'2026-10-05',NULL);
/*!40000 ALTER TABLE `historia_clinica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instituciones`
--

DROP TABLE IF EXISTS `instituciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instituciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instituciones`
--

LOCK TABLES `instituciones` WRITE;
/*!40000 ALTER TABLE `instituciones` DISABLE KEYS */;
INSERT INTO `instituciones` VALUES (1,'Instituto Superior de Formacion Docente y Tecnica Dr. J. A. Ferreira','Esquina - Corrientes','3777-000000','info@ifdferreira.edu.ar');
/*!40000 ALTER TABLE `instituciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jornadas`
--

DROP TABLE IF EXISTS `jornadas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jornadas`
--

LOCK TABLES `jornadas` WRITE;
/*!40000 ALTER TABLE `jornadas` DISABLE KEYS */;
INSERT INTO `jornadas` VALUES (1,'2026-06-19',1,0,1,'2026-06-19 12:35:20','2026-06-20 22:34:36',NULL,NULL,'Jornada abierta turno noche',0),(2,'2026-06-18',1,1,1,'2026-06-18 12:35:20','2026-06-18 12:35:20',2,2,'Jornada cerrada de prueba',0),(3,'2026-06-21',1,0,1,'2026-06-21 07:52:47','2026-06-23 17:48:28',1,NULL,'',1),(4,'2026-06-21',2,0,1,'2026-06-21 07:52:59','2026-06-23 17:48:28',1,NULL,'',1),(5,'2026-06-23',2,0,1,'2026-06-23 17:48:58','2026-06-24 10:00:17',1,NULL,'',1),(6,'2026-06-24',1,0,1,'2026-06-24 10:20:22','2026-06-25 11:40:14',1,NULL,'',1),(7,'2026-06-24',2,0,1,'2026-06-24 12:27:08','2026-06-25 11:40:14',1,NULL,'',1),(8,'2026-06-26',3,0,1,'2026-06-26 08:33:10','2026-06-26 18:51:34',1,1,'',0),(9,'2026-07-04',2,0,1,'2026-07-04 22:18:08','2026-07-04 23:12:36',1,1,'',0),(10,'2026-07-07',3,0,1,'2026-07-07 11:12:57','2026-07-07 11:23:36',1,1,'',0),(11,'2026-07-07',1,0,1,'2026-07-07 12:48:50','2026-07-07 19:15:44',1,1,'',0),(12,'2026-07-07',2,0,1,'2026-07-07 19:15:58','2026-07-08 10:31:21',1,NULL,'abrí tarde',1),(13,'2026-07-21',2,0,1,'2026-07-21 23:03:03','2026-07-21 23:27:01',1,1,'',0),(14,'2026-07-22',3,0,1,'2026-07-22 09:48:01','2026-07-23 09:04:44',1,NULL,'',1),(15,'2026-07-24',2,0,1,'2026-07-24 21:56:04','2026-07-24 22:00:44',1,1,'',0),(16,'2026-10-13',3,1,0,'2026-10-13 07:09:58',NULL,1,NULL,'',0);
/*!40000 ALTER TABLE `jornadas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pacientes`
--

DROP TABLE IF EXISTS `pacientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pacientes`
--

LOCK TABLES `pacientes` WRITE;
/*!40000 ALTER TABLE `pacientes` DISABLE KEYS */;
INSERT INTO `pacientes` VALUES (2,'Adolfo','Perez','45678210',NULL,NULL,NULL,NULL),(3,'Juanjo','Conte','29292821','juanjo@mail.com','23892822',NULL,NULL),(4,'Carla','Andrade','34598366','sdfsdfsdf@jkl.lkj','332244',NULL,NULL),(5,'Luis','Zena','123456578','zzena@gmail.com','3777543891','Velazco 111','1956-05-12'),(6,'Calos','Herman','54928271','cgerman@live.com','3794392810','Salta 456','1942-09-12'),(7,'Enrique','Lopez','21901912','enri@mail.com','3777893939','Las Lomas S/N','1978-12-12');
/*!40000 ALTER TABLE `pacientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `respaldos`
--

DROP TABLE IF EXISTS `respaldos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `respaldos`
--

LOCK TABLES `respaldos` WRITE;
/*!40000 ALTER TABLE `respaldos` DISABLE KEYS */;
/*!40000 ALTER TABLE `respaldos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turnos`
--

DROP TABLE IF EXISTS `turnos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turnos`
--

LOCK TABLES `turnos` WRITE;
/*!40000 ALTER TABLE `turnos` DISABLE KEYS */;
INSERT INTO `turnos` VALUES (1,1,'Turno Tarde','13:30:00','18:10:00',1,NULL),(2,1,'Turno Noche','18:20:00','23:40:00',1,NULL),(3,1,'Turno Mañana','08:30:00','13:00:00',1,'');
/*!40000 ALTER TABLE `turnos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'admin','$2a$10$H7.HHT/Lhzjulq3ZMYqzie1jVyXJ9s44R6zgtdM2lO8F5ioxZGhxa','ADMIN',1,1,'3777000001','admin@ifd.edu.ar','1980-05-10','2026-06-19','Dal Lago','ricardodallago@gmail.com','Ricardo'),(2,'secretaria','$2a$10$H7.HHT/Lhzjulq3ZMYqzie1jVyXJ9s44R6zgtdM2lO8F5ioxZGhxa','SECRETARIO',1,2,'3777000002','secretaria@ifd.edu.ar','1985-02-15','2026-06-19','Ojeda','estudioojeda@gmail.com','Mariana');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visitas`
--

DROP TABLE IF EXISTS `visitas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visitas`
--

LOCK TABLES `visitas` WRITE;
/*!40000 ALTER TABLE `visitas` DISABLE KEYS */;
INSERT INTO `visitas` VALUES (35,7,2,'2026-04-30','<p>se&nbsp;presenta&nbsp;con&nbsp;indicadores&nbsp;de&nbsp;insomnio&nbsp;y&nbsp;cansancio&nbsp;crónico</p>','Insomnio y fatiga persistente.','OK','SEGUIMIENTO'),(49,1,12,'2026-05-04','<p>llega&nbsp;con&nbsp;alto&nbsp;nivel&nbsp;de&nbsp;estrés</p>','Estrés elevado manifiesto.','OK','SEGUIMIENTO'),(61,1,7,'2026-05-05','<p>sindrome&nbsp;de&nbsp;ansiedad&nbsp;generalizada,&nbsp;se&nbsp;manifiesta&nbsp;en&nbsp;estado&nbsp;de&nbsp;tensión&nbsp;y&nbsp;mucha&nbsp;impaciencia&nbsp;ante&nbsp;eventos&nbsp;que&nbsp;no&nbsp;siempre&nbsp;se&nbsp;presentan&nbsp;iguales.</p>','Trastorno de ansiedad con tensión persistente.','OK','SEGUIMIENTO'),(62,7,11,'2026-05-03','Esta es la primera visita de la paciente en la cual manifiesta una sintomatología de estrés permanente ','Síntomas de estrés persistente.','OK','SEGUIMIENTO'),(63,8,18,'2026-06-05','\n\n<h2>REGISTRO DE PRIMERA ENTREVISTA</h2>\n\n<br>\n\n<h3>1. Datos filiatorios</h3>\n\n<p><strong>Nombre:</strong></p>\n\n<p><strong>Preferencia:</strong></p>\n\n<p><strong>Edad:</strong></p>\n\n<p><strong>DNI:</strong></p>\n\n<p><strong>Ocupación:</strong></p>\n\n<p><strong>Tel:</strong></p>\n\n<p><strong>Emergencia:</strong></p>\n\n<br>\n\n<h3>2. Motivo de consulta</h3>\n\n<p>-</p>\n\n<br>\n\n<h3>3. Historia actual</h3>\n\n<p>-</p>\n\n<br>\n\n<h3>4. Antecedentes personales</h3>\n\n<p>-</p>\n\n<br>\n\n<h3>5. Antecedentes familiares</h3>\n\n<p>-</p>\n\n<br>\n\n<h3>6. Escolaridad / Trabajo</h3>\n\n<p>-</p>\n\n<br>\n\n<h3>7. Relaciones vinculares</h3>\n\n<p>-</p>\n\n<br>\n\n<h3>8. Observaciones clínicas</h3>\n\n<p>-</p>\n\n<br>\n\n<h3>9. Impresión diagnóstica inicial</h3>\n\n<p>-</p>\n\n<br>\n\n<h3>10. Objetivos terapéuticos</h3>\n\n<p>-</p>\n\n','Plantilla de registro clínico inicial psicoterapéutico.','OK','PRIMERA_ENTREVISTA'),(64,8,19,'2026-06-06','el paciente se presenta en el consultorio con un alto nivel de ansiedad provocado en su vida laboral y en el ámbito de trabajo en el cual lleva adelante todas sus actividades ','Ansiedad laboral elevada.','OK','SEGUIMIENTO'),(65,8,23,'2026-06-08','el paciente vino asistió con mucha angustia y tenía indicadores de haber sido víctima de bullying por ejemplo ','Angustia con indicadores de bullying.','OK','SEGUIMIENTO');
/*!40000 ALTER TABLE `visitas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'asistencia_ifd'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-13  9:45:56
