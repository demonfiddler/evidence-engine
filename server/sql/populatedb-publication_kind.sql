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

-- Dumping data for table evidence_engine.publication_kind: ~57 rows (approximately)
INSERT INTO `publication_kind` (`kind`, `label`) VALUES
	('ABST', 'Abstract'),
	('ADVS', 'Audiovisual material'),
	('AGGR', 'Aggregated database'),
	('ANCIENT', 'Ancient text'),
	('ART', 'Art work'),
	('BILL', 'Bill/resolution'),
	('BLOG', 'Blog'),
	('BOOK', 'Book, whole'),
	('CASE', 'Case'),
	('CHAP', 'Book section'),
	('CHART', 'Chart'),
	('CLSWK', 'Classical work'),
	('COMP', 'Computer program'),
	('CONF', 'Conference proceeding'),
	('CPAPER', 'Conference paper'),
	('CTLG', 'Catalogue'),
	('DATA', 'Dataset'),
	('DBASE', 'Online database'),
	('DICT', 'Dictionary'),
	('EBOOK', 'Electronic book'),
	('ECHAP', 'Electronic book section'),
	('EDBOOK', 'Edited book'),
	('EJOUR', 'Electronic article'),
	('ELEC', 'Electronic citation'),
	('ENCYC', 'Encyclopaedia article'),
	('EQUA', 'Equation'),
	('FIGURE', 'Figure'),
	('GEN', 'Generic'),
	('GOVDOC', 'Government document'),
	('GRANT', 'Grant'),
	('HEAR', 'Hearing'),
	('ICOMM', 'Internet communication'),
	('INPR', 'In Press'),
	('JFULL', 'Journal (full)'),
	('JOUR', 'Journal'),
	('LEGAL', 'Legal rule or regulation'),
	('MANSCPT', 'Manuscript'),
	('MAP', 'Map'),
	('MGZN', 'Magazine article'),
	('MPCT', 'Motion picture'),
	('MULTI', 'Online multimedia'),
	('MUSIC', 'Music score'),
	('NEWS', 'Newspaper'),
	('PAMP', 'Pamphlet'),
	('PAT', 'Patent'),
	('PCOMM', 'Personal communication'),
	('RPRT', 'Report'),
	('SER', 'Serial publication'),
	('SLIDE', 'Slide presentation'),
	('SOUND', 'Sound recording'),
	('STAND', 'Standard'),
	('STAT', 'Statute'),
	('THES', 'Thesis/dissertation'),
	('UNBILL', 'Unenacted bill/resolution'),
	('UNPB', 'Unpublished work'),
	('VIDEO', 'Video recording'),
	('WEB', 'Web page');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
