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

-- Dumping data for table evidence_engine.journal: ~30 rows (approximately)
INSERT INTO "journal" ("id", "title", "abbreviation", "url", "issn", "publisher_id", "notes", "status", "created", "created_by_user_id", "updated", "updated_by_user_id") VALUES
	(1, 'Advances in Space Research', 'Adv Space Res', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(2, 'Annales Geophysicae', 'Ann Geophys', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(3, 'Applied Physics Research', 'Appl Phys Res', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(4, 'Asia-Pacific Journal of Atmospheric Sciences', 'Asia-Pac J Atmos Sci', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(5, 'Atmosphere', 'Atmos', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(6, 'Atmospheric Research', 'Atmos Res', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(7, 'Bulletin of the American Meteorological Society', 'Bull Am Meteorol Soc', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(8, 'Climate Research', 'Clim Res', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(9, 'Climatic Change', 'Clim Change', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(10, 'Ecological Complexity', 'Ecol Complexity', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(11, 'Ecological Modelling', 'Ecol Modell', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(12, 'Energy & Environment', 'Energy Environ', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(13, 'Environmental and Experimental Botany', 'Environ Exp Bot', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(14, 'Environmental Conservation', 'Environ Conserv', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(15, 'Environmental Geology', 'Environ Geol', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(16, 'Environmental Pollution', 'Environ Pollut', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(17, 'Environmental Science & Technology', 'Environ Sci Technol', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(18, 'Geophysical Research Letters', 'Geophys Res Lett', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(19, 'Journal of American Physicians and Surgeons', 'J Am Physicians Surg', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(20, 'Journal of Atmospheric and Solar-Terrestrial Physics', 'J Atmos Sol Terr Phys', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(21, 'Journal of Geophysical Research', 'J Geophys Res', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(22, 'Journal of Geophysical Research: Atmospheres', 'J Geophys Res: Atmos', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(23, 'Meteorology and Atmospheric Physics', 'Meteorol Atmos Phys', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(24, 'New Astronomy', 'New Astron', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(25, 'Open Journal of Statistics', 'Open J Stat', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(26, 'Physical Geography', 'Phys Geogr', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(27, 'Proceedings of the Institution of Civil Engineers - Civil Engineering', 'Proc Inst Civ Eng - Civ Eng', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(28, 'Science and Life', 'Sci Life', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(29, 'The Astrophysical Journal', 'Astrophys J', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL),
	(30, 'Theoretical and Applied Climatology', 'Theor Appl Climatol', NULL, NULL, NULL, NULL, 'PUB', '2024-10-17 17:22:38', 0, NULL, NULL);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
