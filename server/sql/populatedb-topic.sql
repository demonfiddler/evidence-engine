-- --------------------------------------------------------
-- Host:                         DT-ADRIAN
-- Server version:               10.10.2-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6924
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping data for table evidence_engine.topic: ~11 rows (approximately)
INSERT INTO "topic" ("id", "label", "description", "parent_id", "status", "created", "created_by_user_id", "updated", "updated_by_user_id") VALUES
	(1, 'Climate', 'Contrarian climate science', NULL, 'DRA', '2024-11-10 21:29:42', 0, NULL, NULL),
	(2, 'Extremes', 'Extremes of weather', 1, 'DRA', '2024-11-10 22:00:32', 0, NULL, NULL),
	(3, 'Precipitation', 'Extremes of rain, sleet, hail, snow', 2, 'DRA', '2024-11-10 22:19:52', 0, NULL, NULL),
	(4, 'Temperature', 'Extremes of temperature', 2, 'DRA', '2024-11-10 22:21:00', 0, NULL, NULL),
	(5, 'Wind', 'Extreme cyclones, hurricanes, storms, tornadoes', 2, 'DRA', '2024-11-10 22:22:53', 0, NULL, NULL),
	(6, 'Fire', 'Extreme wildfire events', 2, 'DRA', '2024-11-10 22:45:30', 0, NULL, NULL),
	(7, 'Historical', 'Historical trends', 1, 'DRA', '2024-11-10 22:23:59', 0, NULL, NULL),
	(8, 'Cycles', 'Cyclical patterns', NULL, 'DRA', '2024-11-10 22:24:40', 0, NULL, NULL),
	(9, 'Electromagnetic Radiation', 'Adverse effects of EM radiation on living systems', NULL, 'DRA', '2024-11-10 22:27:24', 0, NULL, NULL),
	(10, 'Fluoride', 'Fluoridation of public water supplies', NULL, 'DRA', '2024-11-10 22:27:42', 0, NULL, NULL),
	(11, 'Vaccines', 'Adverse effects of vaccines', NULL, 'DRA', '2024-11-10 22:29:02', 0, NULL, NULL);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
