-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: temp
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `module` varchar(50) DEFAULT NULL COMMENT '操作模块',
  `description` varchar(200) DEFAULT NULL COMMENT '操作描述',
  `request_url` varchar(500) DEFAULT NULL COMMENT '请求URL',
  `request_method` varchar(10) DEFAULT NULL COMMENT '请求方法',
  `request_params` longtext COMMENT '请求参数',
  `response_body` longtext COMMENT '响应体',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '操作用户名',
  `ipv4` varchar(50) DEFAULT NULL COMMENT '操作IPv4地址',
  `ipv6` varchar(50) DEFAULT NULL COMMENT '操作IPv6地址',
  `operation_time` datetime DEFAULT NULL COMMENT '操作时间',
  `execution_time` bigint DEFAULT NULL COMMENT '执行耗时(毫秒)',
  `status` tinyint DEFAULT NULL COMMENT '1 成功 0 失败',
  `error_msg` text COMMENT '错误信息',
  `http_status` int DEFAULT NULL COMMENT 'HTTP状态码',
  PRIMARY KEY (`id`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (2,'未知模块','','http://localhost:9000/encrypt/public-key','GET',NULL,'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzHTz7pVWGvJ7e/q+FKfpGXI1f/jcI7HbUXmbCnoIIbjeU42KaQep1Mkyl0jJCOjMFxz3hERYSC3CYT2PN5h56LdAFlLesCZh/axgrp59zZBFI9sSyHY85SMs/i5IYHdXY64aqtRWpFiSSNj9aYcU/owPz9N+Nx3bcWOt3a+P2wIDAQAB',NULL,NULL,NULL,'0:0:0:0:0:0:0:1','2026-06-24 14:43:18',4,1,NULL,200),(3,'未知模块','','http://localhost:9000/encrypt/public-key','GET',NULL,'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzHTz7pVWGvJ7e/q+FKfpGXI1f/jcI7HbUXmbCnoIIbjeU42KaQep1Mkyl0jJCOjMFxz3hERYSC3CYT2PN5h56LdAFlLesCZh/axgrp59zZBFI9sSyHY85SMs/i5IYHdXY64aqtRWpFiSSNj9aYcU/owPz9N+Nx3bcWOt3a+P2wIDAQAB',NULL,NULL,NULL,'0:0:0:0:0:0:0:1','2026-06-24 14:43:52',3,1,NULL,200),(4,'未知模块','','http://localhost:9000/encrypt/public-key','GET','[]','MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzHTz7pVWGvJ7e/q+FKfpGXI1f/jcI7HbUXmbCnoIIbjeU42KaQep1Mkyl0jJCOjMFxz3hERYSC3CYT2PN5h56LdAFlLesCZh/axgrp59zZBFI9sSyHY85SMs/i5IYHdXY64aqtRWpFiSSNj9aYcU/owPz9N+Nx3bcWOt3a+P2wIDAQAB',NULL,NULL,NULL,'0:0:0:0:0:0:0:1','2026-06-24 15:05:02',8,1,NULL,200),(5,'未知模块','','/encrypt/public-key','GET','[]','MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzHTz7pVWGvJ7e/q+FKfpGXI1f/jcI7HbUXmbCnoIIbjeU42KaQep1Mkyl0jJCOjMFxz3hERYSC3CYT2PN5h56LdAFlLesCZh/axgrp59zZBFI9sSyHY85SMs/i5IYHdXY64aqtRWpFiSSNj9aYcU/owPz9N+Nx3bcWOt3a+P2wIDAQAB',NULL,NULL,NULL,'0:0:0:0:0:0:0:1','2026-06-25 14:07:32',3,1,NULL,200),(6,'未知模块','','/encrypt/public-key','GET','[]','MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzHTz7pVWGvJ7e/q+FKfpGXI1f/jcI7HbUXmbCnoIIbjeU42KaQep1Mkyl0jJCOjMFxz3hERYSC3CYT2PN5h56LdAFlLesCZh/axgrp59zZBFI9sSyHY85SMs/i5IYHdXY64aqtRWpFiSSNj9aYcU/owPz9N+Nx3bcWOt3a+P2wIDAQAB',NULL,NULL,NULL,'0:0:0:0:0:0:0:1','2026-06-25 14:07:40',0,1,NULL,200),(7,'未知模块','','/encrypt/public-key','GET','[]','MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzHTz7pVWGvJ7e/q+FKfpGXI1f/jcI7HbUXmbCnoIIbjeU42KaQep1Mkyl0jJCOjMFxz3hERYSC3CYT2PN5h56LdAFlLesCZh/axgrp59zZBFI9sSyHY85SMs/i5IYHdXY64aqtRWpFiSSNj9aYcU/owPz9N+Nx3bcWOt3a+P2wIDAQAB',NULL,NULL,NULL,'0:0:0:0:0:0:0:1','2026-06-25 14:08:44',1,1,NULL,200),(8,'未知模块','','/encrypt/public-key','GET','[]','MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMzHTz7pVWGvJ7e/q+FKfpGXI1f/jcI7HbUXmbCnoIIbjeU42KaQep1Mkyl0jJCOjMFxz3hERYSC3CYT2PN5h56LdAFlLesCZh/axgrp59zZBFI9sSyHY85SMs/i5IYHdXY64aqtRWpFiSSNj9aYcU/owPz9N+Nx3bcWOt3a+P2wIDAQAB',NULL,NULL,NULL,'0:0:0:0:0:0:0:1','2026-06-25 14:09:13',2,1,NULL,200);
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置KEY',
  `config_value` varchar(20) NOT NULL COMMENT '配置值',
  `config_type` varchar(50) COMMENT '配置类型',
  `config_name` varchar(200) DEFAULT NULL COMMENT '配置名称',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint DEFAULT '1' COMMENT '状态(1启用 0禁用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `sys_config_status_index` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'DATA_TRANSFER_ENCRYPT','true','BOOLEAN','数据传输加密','启用RSA+AES数据传输加密',1,'2026-06-16 19:18:38','2026-06-16 19:24:56'),(2,'USER_REGISTER','true','BOOLEAN','用户注册','是否允许新用户注册',1,'2026-06-16 19:18:38','2026-06-16 19:24:56'),(3,'OPERATION_LOG','true','BOOLEAN','操作日志','记录业务操作日志',1,'2026-06-16 19:18:38','2026-06-16 19:24:56'),(5,'SINGLE_LOGIN','true','BOOLEAN','单点登录','同一账号仅允许一个会话',1,'2026-06-16 19:18:38','2026-06-16 19:24:56'),(6,'TOKEN_EXPIRE_SECONDS','120','INTEGER','Token有效期','登录凭证有效时间',1,'2026-06-16 19:18:38','2026-06-24 18:05:23'),(8,'SYSTEM_MAINTENANCE','false','BOOLEAN','系统维护模式','开启后非管理员禁止访问',1,'2026-06-16 19:18:38','2026-06-29 11:36:18'),(10,'REPLAY_ATTACK_ENABLED','true','BOOLEAN','防重放攻击','是否启用请求防重放校验',1,'2026-06-23 09:44:45','2026-06-29 11:36:43'),(11,'REPLAY_ATTACK_TIMEOUT','300','INTEGER','防重放超时','请求有效时间窗口（秒）',1,'2026-06-23 09:44:45','2026-06-29 11:36:43'),(14,'NONCE_CACHE_EXPIRE','600','INTEGER','Nonce缓存过期','随机数缓存过期时间（秒）',1,'2026-06-23 14:48:35','2026-06-29 11:36:43');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_login_device`
--

DROP TABLE IF EXISTS `user_login_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_login_device` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `device_id` varchar(128) COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备唯一标识',
  `device_type` varchar(32) COLLATE utf8mb4_0900_ai_ci DEFAULT 'UNKNOWN' COMMENT '设备类型',
  `device_name` varchar(255) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备名称（用户自定义）',
  `access_token` varchar(512) COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '访问令牌',
  `refresh_token` varchar(512) COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '刷新令牌',
  `login_ip` varchar(64) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '登录IP地址',
  `login_location` varchar(255) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '登录地理位置',
  `user_agent` varchar(512) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '浏览器User-Agent',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `expire_time` datetime NOT NULL COMMENT 'Token过期时间',
  `last_active_time` datetime DEFAULT NULL COMMENT '最后活跃时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '登录状态: 1-有效 0-已失效/被踢',
  `kick_reason` varchar(255) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '被踢原因',
  `kick_time` datetime DEFAULT NULL COMMENT '被踢时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_device` (`user_id`,`device_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_access_token` (`access_token`(255)),
  KEY `idx_refresh_token` (`refresh_token`(255)),
  KEY `idx_is_active` (`status`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户登录设备表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_login_device`
--

LOCK TABLES `user_login_device` WRITE;
/*!40000 ALTER TABLE `user_login_device` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_login_device` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-29 16:49:19
