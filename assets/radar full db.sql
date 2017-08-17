CREATE DATABASE  IF NOT EXISTS `dbradar` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `dbradar`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: dbradar
-- ------------------------------------------------------
-- Server version	5.7.11-log

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
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dni` varchar(20) DEFAULT NULL,
  `username` varchar(16) NOT NULL,
  `password` varchar(50) NOT NULL,
  `name` varchar(20) NOT NULL,
  `lastname` varchar(20) NOT NULL,
  `create_date` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'20356841','yornel','81dc9bdb52d04dc20036dbd8313ed055','Yornel','Marval',1500571999336,1502821624067,1);
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `numeration` varchar(50) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (11,'Empresas Polar','RMVC','123456',1),(12,'Jprisoft','JPS','3245624',1);
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `control_position`
--

DROP TABLE IF EXISTS `control_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `control_position` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `place_name` varchar(50) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `control_position`
--

LOCK TABLES `control_position` WRITE;
/*!40000 ALTER TABLE `control_position` DISABLE KEYS */;
INSERT INTO `control_position` VALUES (25,10.267762308236982,-68.02440782291386,'Lugar de guardado',1),(26,10.267696642376384,-68.02448451519014,'Sala de conferencias',1),(27,10.267690476585305,-68.02435038359152,'esquina de afuera',1),(28,10.267778459279745,-68.02430346608162,'punto 343',1),(29,10.267801552756707,-68.02404932677746,'puerta este',1),(30,10.267817058376007,-68.02357155829668,'path de iconos 2',1),(31,10.267946711716137,-68.0225969105959,'Kiosko Yovanni',1),(32,10.267139759020198,-68.02297040820122,'punto de afuera',1);
/*!40000 ALTER TABLE `control_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group`
--

DROP TABLE IF EXISTS `group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `route_id` bigint(20) DEFAULT NULL,
  `create_date` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `group_fk0` (`route_id`),
  CONSTRAINT `group_fk0` FOREIGN KEY (`route_id`) REFERENCES `route` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group`
--

LOCK TABLES `group` WRITE;
/*!40000 ALTER TABLE `group` DISABLE KEYS */;
INSERT INTO `group` VALUES (1,'Nocturno',15,1500571999336,1502979289640,1),(2,'Sabatino',16,1500571999336,1500571999336,1),(3,'Ala Oeste',17,1500571999336,1500571999336,1),(5,'Solitario',16,1502808571222,1502808571222,1);
/*!40000 ALTER TABLE `group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `position`
--

DROP TABLE IF EXISTS `position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `position` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `time` bigint(20) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  `control_id` bigint(20) NOT NULL,
  `watch_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `position_fk0` (`control_id`),
  KEY `position_fk1` (`watch_id`),
  CONSTRAINT `position_fk0` FOREIGN KEY (`control_id`) REFERENCES `control_position` (`id`),
  CONSTRAINT `position_fk1` FOREIGN KEY (`watch_id`) REFERENCES `watch` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `position`
--

LOCK TABLES `position` WRITE;
/*!40000 ALTER TABLE `position` DISABLE KEYS */;
INSERT INTO `position` VALUES (29,10.267711089438617,-68.02432570727836,1500571941786,1500571941786,27,11),(30,10.267785013428298,-68.02428316687303,1500571992515,1500571992515,28,11),(31,10.267728628890978,-68.02450549293611,1500572452809,1500572452809,26,12),(32,10.267776837576358,-68.024445858272,1500572457002,1500572457002,25,12),(33,10.267765611921758,-68.0243434985462,1500572470739,1500572470739,28,12),(34,10.267717249192374,-68.0243210991907,1500572475538,1500572475538,27,12),(35,10.267811655073789,-68.02426627027383,1500869550563,1500869550563,28,13),(36,10.267680486347734,-68.02448168221154,1500869652457,1500869652457,26,13),(37,10.267684177250372,-68.02448592289686,1500869736236,1500869736236,26,13),(38,10.267772253147408,-68.02433007231582,1502846102136,1502846102136,28,51),(39,10.26774089011423,-68.02427034525101,1502847562569,1502847562569,28,52),(40,10.267732295326386,-68.02435990416474,1502847591819,1502847591819,27,52),(41,10.267738734635682,-68.02439929948979,1502847597403,1502847597403,25,52),(42,10.267717978111255,-68.02435648092977,1502847609717,1502847609717,27,52);
/*!40000 ALTER TABLE `position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `route`
--

DROP TABLE IF EXISTS `route`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `route` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `create_date` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `route`
--

LOCK TABLES `route` WRITE;
/*!40000 ALTER TABLE `route` DISABLE KEYS */;
INSERT INTO `route` VALUES (14,'Ruta Casa',1501786496446,1501786496468,1),(15,'Av Principal',1501786862640,1501786862640,1),(16,'Recorrido Completo',1501786931423,1501786931423,1),(17,'Aislado',1501786948878,1501786948878,1),(18,'Ruta Prueba 1',1501786986040,1501786986040,1),(19,'Ruta Prueba 2',1501786989739,1501786989739,1),(20,'Ruta Prueba 3',1501786993326,1501786993326,1);
/*!40000 ALTER TABLE `route` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `route_marker`
--

DROP TABLE IF EXISTS `route_marker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `route_marker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `control_id` bigint(20) NOT NULL,
  `create_date` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `route_marker_fk0` (`user_id`),
  KEY `route_marker_fk1` (`control_id`),
  CONSTRAINT `route_marker_fk0` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `route_marker_fk1` FOREIGN KEY (`control_id`) REFERENCES `control_position` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `route_marker`
--

LOCK TABLES `route_marker` WRITE;
/*!40000 ALTER TABLE `route_marker` DISABLE KEYS */;
/*!40000 ALTER TABLE `route_marker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `route_position`
--

DROP TABLE IF EXISTS `route_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `route_position` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `control_id` bigint(20) NOT NULL,
  `route_id` bigint(20) NOT NULL,
  `create_date` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `route_position_fk0` (`control_id`),
  KEY `route_position_fk1` (`route_id`),
  CONSTRAINT `route_position_fk0` FOREIGN KEY (`control_id`) REFERENCES `control_position` (`id`),
  CONSTRAINT `route_position_fk1` FOREIGN KEY (`route_id`) REFERENCES `route` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=252 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `route_position`
--

LOCK TABLES `route_position` WRITE;
/*!40000 ALTER TABLE `route_position` DISABLE KEYS */;
INSERT INTO `route_position` VALUES (150,25,16,1501786938080),(151,26,16,1501786938082),(152,27,16,1501786938084),(153,28,16,1501786938086),(154,29,16,1501786938088),(155,30,16,1501786938090),(156,31,16,1501786938092),(157,32,16,1501786938095),(158,26,17,1501786969074),(159,31,17,1501786969077),(160,32,17,1501786969079),(190,25,14,1501824076552),(191,26,14,1501824076556),(192,27,14,1501824076558),(193,30,14,1501824076560),(194,31,14,1501824076561),(240,25,20,1502808476859),(241,26,20,1502808476889),(242,27,20,1502808476891),(243,28,20,1502808476893),(244,29,20,1502808476895),(245,30,20,1502808476898),(246,31,20,1502808476900),(247,32,20,1502808476902),(248,27,15,1502808499970),(249,29,15,1502808499973),(250,30,15,1502808499975),(251,31,15,1502808499977);
/*!40000 ALTER TABLE `route_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dni` varchar(20) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `create_date` bigint(20) NOT NULL,
  `last_update` bigint(20) NOT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `company_id` bigint(20) DEFAULT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_fk0` (`group_id`),
  KEY `user_company_idx` (`company_id`),
  CONSTRAINT `user_fk0` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`),
  CONSTRAINT `user_fk2` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (5,'20356841','Yornel','Marval','81dc9bdb52d04dc20036dbd8313ed055',1500571999336,1502940153320,1,12,1),(6,'19356214','Antonio','Marval','81dc9bdb52d04dc20036dbd8313ed055',1500571999336,1501478791532,NULL,12,1),(7,'29728483','Melanie ','Marval','81dc9bdb52d04dc20036dbd8313ed055',1500571999336,1501467086861,NULL,12,1),(8,'19968325','Joshuan','Marval','81dc9bdb52d04dc20036dbd8313ed055',1500571999336,1501478010189,1,12,1),(9,'123456','fdgdf','1 gfdgfd','a29b68aede41e25179a66c5978b21437',1502807514426,1502989504649,NULL,11,1),(10,'1234567','gfhgf','2 hfghgf','30bea55b94dffa98f8439631ce37ec00',1502807521948,1502989509506,NULL,11,1),(11,'12345678','dsfds','3 dsfdsfds','4ff75da0d3b8234fb3edcd1d4ad17c85',1502807531381,1502989516330,NULL,11,1),(14,'52254','fggfd','4 gfdgfdgdf','2ed8ba416fb42b17fd291302c87972e2',1502853844371,1502989521522,NULL,11,1),(15,'545654654','fgdfg','5 gfdgfd','328d6fde080b9845fb7fa3a612bea12f',1502853854390,1502989526369,NULL,11,1),(17,'654321','556646','6 456546','81dc9bdb52d04dc20036dbd8313ed055',1502940169656,1502989531545,NULL,11,1),(18,'20968047','Anderson','Morillo','81dc9bdb52d04dc20036dbd8313ed055',1502990582165,1502990582188,NULL,12,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watch`
--

DROP TABLE IF EXISTS `watch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `watch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `start_time` bigint(20) NOT NULL,
  `end_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `watch_fk0` (`user_id`),
  CONSTRAINT `watch_fk0` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watch`
--

LOCK TABLES `watch` WRITE;
/*!40000 ALTER TABLE `watch` DISABLE KEYS */;
INSERT INTO `watch` VALUES (11,5,1500571815522,1500571999336),(12,5,1500869527147,1500869758398),(13,8,1500869527147,1500869758398),(14,6,1500571815522,1500571999336),(15,6,1500571815522,1500571999336),(16,6,1500571815522,1500571999336),(17,6,1500869527147,1500869758398),(18,6,1500869527147,1500869758398),(19,6,1500869527147,1500869758398),(20,6,1500869527147,1500869758398),(21,6,1500869527147,1500869758398),(22,6,1500869527147,1500869758398),(23,6,1500869527147,1500869758398),(24,6,1500869527147,1500869758398),(25,6,1500869527147,1500869758398),(26,6,1500869527147,1500869758398),(27,6,1500869527147,1500869758398),(28,6,1500869527147,1500869758398),(29,6,1500869527147,1500869758398),(30,6,1500869527147,1500869758398),(31,6,1500869527147,1500869758398),(32,6,1500869527147,1500869758398),(33,6,1500869527147,1500869758398),(34,6,1500869527147,1500869758398),(35,6,1500869527147,1500869758398),(36,6,1500869527147,1500869758398),(37,6,1500869527147,1500869758398),(38,6,1500869527147,1500869758398),(39,6,1500869527147,1500869758398),(40,6,1500869527147,1500869758398),(41,6,1500869527147,1500869758398),(42,6,1500869527147,1500869758398),(43,6,1500869527147,1500869758398),(44,6,1500869527147,1500869758398),(45,6,1500869527147,1500869758398),(46,6,1500869527147,1500869758398),(47,6,1500869527147,1500869758398),(48,6,1500869527147,1500869758398),(49,6,1500869527147,1500869758398),(50,6,1500869527147,1500869758398),(51,5,1502826839611,1502847251272),(52,5,1502847514751,1502847623785);
/*!40000 ALTER TABLE `watch` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-08-17 17:15:20
