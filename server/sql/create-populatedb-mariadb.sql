-- --------------------------------------------------------
-- Host:                         DT-ADRIAN
-- Server version:               10.10.2-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for evidence_engine
CREATE DATABASE IF NOT EXISTS `evidence_engine` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `evidence_engine`;

-- Dumping structure for table evidence_engine.claim
CREATE TABLE IF NOT EXISTS `claim` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'The unique claim identifier',
  `date` date DEFAULT NULL COMMENT 'The date on which the claim was first made',
  `text` varchar(500) NOT NULL COMMENT 'The claim text',
  `notes` text DEFAULT NULL COMMENT 'Added notes about the claim',
  PRIMARY KEY (`id`),
  FULLTEXT KEY `claim_fulltext` (`text`,`notes`),
  CONSTRAINT `FK_claim_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Claims made in respect of an associated topic';

-- Dumping data for table evidence_engine.claim: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.country
CREATE TABLE IF NOT EXISTS `country` (
  `alpha_2` char(2) NOT NULL COMMENT 'ISO-3166-1 alpha-2 code',
  `alpha_3` char(3) NOT NULL COMMENT 'ISO-3166-1 alpha-3 code',
  `numeric` char(3) NOT NULL COMMENT 'ISO-3166-1 numeric code',
  `iso_name` varchar(100) NOT NULL COMMENT 'Official/ISO country name',
  `common_name` varchar(50) NOT NULL COMMENT 'Common or short name',
  `year` year(4) NOT NULL COMMENT 'Year alpha-2 code was first assigned',
  `cc_tld` char(3) NOT NULL COMMENT 'Country code top level domain',
  `notes` text DEFAULT NULL COMMENT 'Remarks as per Wikipedia ISO-3166-1 entry',
  PRIMARY KEY (`alpha_2`),
  UNIQUE KEY `country_alpha_3` (`alpha_3`) USING BTREE,
  UNIQUE KEY `country_numeric` (`numeric`) USING BTREE,
  UNIQUE KEY `country_common_name` (`common_name`) USING BTREE,
  UNIQUE KEY `country_iso_name` (`iso_name`) USING BTREE,
  CONSTRAINT `CC_country_numeric` CHECK (`numeric` regexp '^\\d{3}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Lookup table for converting ISO-3166-1 country codes to country name';

-- Dumping data for table evidence_engine.country: ~250 rows (approximately)
INSERT IGNORE INTO `country` (`alpha_2`, `alpha_3`, `numeric`, `iso_name`, `common_name`, `year`, `cc_tld`, `notes`) VALUES
	('AD', 'AND', '020', 'Andorra', 'Andorra', '1974', '.ad', NULL),
	('AE', 'ARE', '784', 'United Arab Emirates', 'United Arab Emirates', '1974', '.ae', 'Formerly Trucial States'),
	('AF', 'AFG', '004', 'Afghanistan', 'Afghanistan', '1974', '.af', NULL),
	('AG', 'ATG', '028', 'Antigua and Barbuda', 'Antigua and Barbuda', '1974', '.ag', NULL),
	('AI', 'AIA', '660', 'Anguilla', 'Anguilla', '1985', '.ai', 'AI previously represented French Afars and Issas. Until 1985 part of Saint Kitts-Nevis-Anguilla'),
	('AL', 'ALB', '008', 'Albania', 'Albania', '1974', '.al', NULL),
	('AM', 'ARM', '051', 'Armenia', 'Armenia', '1992', '.am', 'Until 1991 part of the USSR'),
	('AO', 'AGO', '024', 'Angola', 'Angola', '1974', '.ao', NULL),
	('AQ', 'ATA', '010', 'Antarctica', 'Antarctica', '1974', '.aq', 'Covers the territories south of 60° south latitude. Code taken from name in French: Antarctique'),
	('AR', 'ARG', '032', 'Argentina', 'Argentina', '1974', '.ar', NULL),
	('AS', 'ASM', '016', 'American Samoa', 'American Samoa', '1974', '.as', NULL),
	('AT', 'AUT', '040', 'Austria', 'Austria', '1974', '.at', NULL),
	('AU', 'AUS', '036', 'Australia', 'Australia', '1974', '.au', 'Includes the Ashmore and Cartier Islands and the Coral Sea Islands'),
	('AW', 'ABW', '533', 'Aruba', 'Aruba', '1986', '.aw', 'Until 1986 part of the Netherlands Antilles'),
	('AX', 'ALA', '248', 'Åland Islands', 'Åland Islands', '2004', '.ax', 'An autonomous county of Finland. Until 2004 included with Finland'),
	('AZ', 'AZE', '031', 'Azerbaijan', 'Azerbaijan', '1992', '.az', 'Until 1991 part of the USSR'),
	('BA', 'BIH', '070', 'Bosnia and Herzegovina', 'Bosnia and Herzegovina', '1992', '.ba', 'Until 1992 part of Yugoslavia'),
	('BB', 'BRB', '052', 'Barbados', 'Barbados', '1974', '.bb', NULL),
	('BD', 'BGD', '050', 'Bangladesh', 'Bangladesh', '1974', '.bd', NULL),
	('BE', 'BEL', '056', 'Belgium', 'Belgium', '1974', '.be', NULL),
	('BF', 'BFA', '854', 'Burkina Faso', 'Burkina Faso', '1984', '.bf', 'Name changed from Upper Volta (HV)'),
	('BG', 'BGR', '100', 'Bulgaria', 'Bulgaria', '1974', '.bg', NULL),
	('BH', 'BHR', '048', 'Bahrain', 'Bahrain', '1974', '.bh', NULL),
	('BI', 'BDI', '108', 'Burundi', 'Burundi', '1974', '.bi', NULL),
	('BJ', 'BEN', '204', 'Benin', 'Benin', '1977', '.bj', 'Name changed from Dahomey (DY). Formerly Dahomey'),
	('BL', 'BLM', '652', 'Saint Barthélemy', 'Saint Barthélemy', '2007', '.bl', 'Until 2007 part of Guadeloupe'),
	('BM', 'BMU', '060', 'Bermuda', 'Bermuda', '1974', '.bm', NULL),
	('BN', 'BRN', '096', 'Brunei Darussalam', 'Brunei', '1974', '.bn', 'Previous ISO country name: Brunei'),
	('BO', 'BOL', '068', 'Bolivia, Plurinational State of', 'Bolivia', '1974', '.bo', 'Previous ISO country name: Bolivia'),
	('BQ', 'BES', '535', 'Bonaire, Sint Eustatius and Saba', 'Bonaire, Sint Eustatius and Saba', '2010', '.bq', 'Consists of three Caribbean \'special municipalities\', which are part of the Netherlands proper: Bonaire, Sint Eustatius, and Saba (the BES Islands). Previous ISO country name: Bonaire, Saint Eustatius and Saba. BQ previously represented British Antarctic Territory. Until 2010 part of the Netherlands Antilles.'),
	('BR', 'BRA', '076', 'Brazil', 'Brazil', '1974', '.br', NULL),
	('BS', 'BHS', '044', 'Bahamas', 'Bahamas', '1974', '.bs', NULL),
	('BT', 'BTN', '064', 'Bhutan', 'Bhutan', '1974', '.bt', NULL),
	('BV', 'BVT', '074', 'Bouvet Island', 'Bouvet Island', '1974', '.bv', 'Dependency of Norway'),
	('BW', 'BWA', '072', 'Botswana', 'Botswana', '1974', '.bw', NULL),
	('BY', 'BLR', '112', 'Belarus', 'Belarus', '1974', '.by', 'Code taken from previous ISO country name: Byelorussian SSR (now assigned ISO 3166-3 code BYAA). Code assigned as the country was already a UN member since 1945. Formerly Byelorussian SSR.'),
	('BZ', 'BLZ', '084', 'Belize', 'Belize', '1974', '.bz', 'Formerly British Honduras'),
	('CA', 'CAN', '124', 'Canada', 'Canada', '1974', '.ca', NULL),
	('CC', 'CCK', '166', 'Cocos (Keeling) Islands', 'Cocos Islands', '1974', '.cc', 'External territory of Australia'),
	('CD', 'COD', '180', 'Congo, Democratic Republic of the', 'Congo, Democratic Republic of the', '1997', '.cd', 'Name changed from Zaire (ZR)'),
	('CF', 'CAF', '140', 'Central African Republic', 'Central African Republic', '1974', '.cf', NULL),
	('CG', 'COG', '178', 'Congo', 'Congo', '1974', '.cg', NULL),
	('CH', 'CHE', '756', 'Switzerland', 'Switzerland', '1974', '.ch', 'Code taken from name in Latin: Confoederatio Helvetica'),
	('CI', 'CIV', '384', 'Côte d\'Ivoire', 'Ivory Coast', '1974', '.ci', 'ISO country name follows UN designation (common name and previous ISO country name: Ivory Coast). Formerly Ivory Coast.'),
	('CK', 'COK', '184', 'Cook Islands', 'Cook Islands', '1974', '.ck', NULL),
	('CL', 'CHL', '152', 'Chile', 'Chile', '1974', '.cl', NULL),
	('CM', 'CMR', '120', 'Cameroon', 'Cameroon', '1974', '.cm', 'Previous ISO country name: Cameroon, United Republic of'),
	('CN', 'CHN', '156', 'China', 'China', '1974', '.cn', NULL),
	('CO', 'COL', '170', 'Colombia', 'Colombia', '1974', '.co', NULL),
	('CR', 'CRI', '188', 'Costa Rica', 'Costa Rica', '1974', '.cr', NULL),
	('CU', 'CUB', '192', 'Cuba', 'Cuba', '1974', '.cu', NULL),
	('CV', 'CPV', '132', 'Cabo Verde', 'Cape Verde', '1974', '.cv', 'ISO country name follows UN designation (common name and previous ISO country name: Cape Verde, another previous ISO country name: Cape Verde Islands)'),
	('CW', 'CUW', '531', 'Curaçao', 'Curaçao', '2010', '.cw', NULL),
	('CX', 'CXR', '162', 'Christmas Island', 'Christmas Island', '1974', '.cx', 'External territory of Australia'),
	('CY', 'CYP', '196', 'Cyprus', 'Cyprus', '1974', '.cy', NULL),
	('CZ', 'CZE', '203', 'Czechia', 'Czechia', '1993', '.cz', 'Previous ISO country name: Czech Republic. Until 1993 part of Czechoslovakia.'),
	('DE', 'DEU', '276', 'Germany', 'Germany', '1974', '.de', 'Code taken from name in German: Deutschland. Code used for West Germany before 1990 (previous ISO country name: Germany, Federal Republic of). Unified country since 1990.'),
	('DJ', 'DJI', '262', 'Djibouti', 'Djibouti', '1977', '.dj', 'Name changed from French Afars and Issas (AI). Formerly French Territory of the Afars and the Issas.'),
	('DK', 'DNK', '208', 'Denmark', 'Denmark', '1974', '.dk', NULL),
	('DM', 'DMA', '212', 'Dominica', 'Dominica', '1974', '.dm', NULL),
	('DO', 'DOM', '214', 'Dominican Republic', 'Dominican Republic', '1974', '.do', NULL),
	('DZ', 'DZA', '012', 'Algeria', 'Algeria', '1974', '.dz', 'Code taken from name in Arabic الجزائر al-Djazā\'ir, Algerian Arabic الدزاير al-Dzāyīr, or Berber ⴷⵣⴰⵢⵔ Dzayer'),
	('EC', 'ECU', '218', 'Ecuador', 'Ecuador', '1974', '.ec', NULL),
	('EE', 'EST', '233', 'Estonia', 'Estonia', '1992', '.ee', 'Code taken from name in Estonian: Eesti. Until 1991 part of the USSR.'),
	('EG', 'EGY', '818', 'Egypt', 'Egypt', '1974', '.eg', 'Formerly United Arab Republic'),
	('EH', 'ESH', '732', 'Western Sahara', 'Western Sahara', '1974', '.eh', 'Previous ISO country name: Spanish Sahara (code taken from name in Spanish: Sahara español)'),
	('ER', 'ERI', '232', 'Eritrea', 'Eritrea', '1993', '.er', NULL),
	('ES', 'ESP', '724', 'Spain', 'Spain', '1974', '.es', 'Code taken from name in Spanish: España'),
	('ET', 'ETH', '231', 'Ethiopia', 'Ethiopia', '1974', '.et', NULL),
	('EU', 'EEE', '000', 'European Union', 'Europe', '2024', '.eu', 'Only the alpha_2 code is officially reserved. The alpha_3 is a NATO code'),
	('FI', 'FIN', '246', 'Finland', 'Finland', '1974', '.fi', NULL),
	('FJ', 'FJI', '242', 'Fiji', 'Fiji', '1974', '.fj', NULL),
	('FK', 'FLK', '238', 'Falkland Islands (Malvinas)', 'Falkland Islands', '1974', '.fk', 'ISO country name follows UN designation due to the Falkland Islands sovereignty dispute (local common name: Falkland Islands)[15]'),
	('FM', 'FSM', '583', 'Micronesia, Federated States of', 'Micronesia', '1986', '.fm', 'Previous ISO country name: Micronesia. Until 1986 part of Pacific Islands (Trust Territory).'),
	('FO', 'FRO', '234', 'Faroe Islands', 'Faroe Islands', '1974', '.fo', 'Code taken from name in Faroese: Føroyar. Previously spelled Faeroe Islands.'),
	('FR', 'FRA', '250', 'France', 'France', '1974', '.fr', 'Includes Clipperton Island'),
	('GA', 'GAB', '266', 'Gabon', 'Gabon', '1974', '.ga', NULL),
	('GB', 'GBR', '826', 'United Kingdom of Great Britain and Northern Ireland', 'United Kingdom', '1974', '.uk', 'Includes Akrotiri and Dhekelia (Sovereign Base Areas). Code taken from Great Britain (from official name: United Kingdom of Great Britain and Northern Ireland). .uk is the primary ccTLD of the United Kingdom instead of .gb (see code UK, which is exceptionally reserved)'),
	('GD', 'GRD', '308', 'Grenada', 'Grenada', '1974', '.gd', NULL),
	('GE', 'GEO', '268', 'Georgia', 'Georgia', '1992', '.ge', 'GE previously represented Gilbert and Ellice Islands. Until 1991 part of the USSR.'),
	('GF', 'GUF', '254', 'French Guiana', 'French Guiana', '1974', '.gf', 'Code taken from name in French: Guyane française'),
	('GG', 'GGY', '831', 'Guernsey', 'Guernsey', '2006', '.gg', 'A British Crown Dependency. Until 2006 included with the United Kingdom.'),
	('GH', 'GHA', '288', 'Ghana', 'Ghana', '1974', '.gh', NULL),
	('GI', 'GIB', '292', 'Gibraltar', 'Gibraltar', '1974', '.gi', NULL),
	('GL', 'GRL', '304', 'Greenland', 'Greenland', '1974', '.gl', NULL),
	('GM', 'GMB', '270', 'Gambia', 'Gambia', '1974', '.gm', NULL),
	('GN', 'GIN', '324', 'Guinea', 'Guinea', '1974', '.gn', NULL),
	('GP', 'GLP', '312', 'Guadeloupe', 'Guadeloupe', '1974', '.gp', NULL),
	('GQ', 'GNQ', '226', 'Equatorial Guinea', 'Equatorial Guinea', '1974', '.gq', 'Code taken from name in French: Guinée équatoriale'),
	('GR', 'GRC', '300', 'Greece', 'Greece', '1974', '.gr', NULL),
	('GS', 'SGS', '239', 'South Georgia and the South Sandwich Islands', 'South Georgia', '1993', '.gs', 'Until 1993 part of the Falkland Islands'),
	('GT', 'GTM', '320', 'Guatemala', 'Guatemala', '1974', '.gt', NULL),
	('GU', 'GUM', '316', 'Guam', 'Guam', '1974', '.gu', NULL),
	('GW', 'GNB', '624', 'Guinea-Bissau', 'Guinea-Bissau', '1974', '.gw', 'Formerly Portuguese Guinea'),
	('GY', 'GUY', '328', 'Guyana', 'Guyana', '1974', '.gy', NULL),
	('HK', 'HKG', '344', 'Hong Kong', 'Hong Kong', '1974', '.hk', 'Hong Kong is officially a Special Administrative Region of the People\'s Republic of China since 1 July 1997'),
	('HM', 'HMD', '334', 'Heard Island and McDonald Islands', 'Heard Island and McDonald Islands', '1974', '.hm', 'External territory of Australia'),
	('HN', 'HND', '340', 'Honduras', 'Honduras', '1974', '.hn', NULL),
	('HR', 'HRV', '191', 'Croatia', 'Croatia', '1992', '.hr', 'Code taken from name in Croatian: Hrvatska. Until 1992 part of Yugoslavia.'),
	('HT', 'HTI', '332', 'Haiti', 'Haiti', '1974', '.ht', NULL),
	('HU', 'HUN', '348', 'Hungary', 'Hungary', '1974', '.hu', NULL),
	('ID', 'IDN', '360', 'Indonesia', 'Indonesia', '1974', '.id', NULL),
	('IE', 'IRL', '372', 'Ireland', 'Ireland', '1974', '.ie', NULL),
	('IL', 'ISR', '376', 'Israel', 'Israel', '1974', '.il', NULL),
	('IM', 'IMN', '833', 'Isle of Man', 'Isle of Man', '2006', '.im', 'A British Crown Dependency. Until 2006 included with the United Kingdom.'),
	('IN', 'IND', '356', 'India', 'India', '1974', '.in', NULL),
	('IO', 'IOT', '086', 'British Indian Ocean Territory', 'British Indian Ocean Territory', '1974', '.io', NULL),
	('IQ', 'IRQ', '368', 'Iraq', 'Iraq', '1974', '.iq', NULL),
	('IR', 'IRN', '364', 'Iran, Islamic Republic of', 'Iran', '1974', '.ir', 'Previous ISO country name: Iran'),
	('IS', 'ISL', '352', 'Iceland', 'Iceland', '1974', '.is', 'Code taken from name in Icelandic: Ísland'),
	('IT', 'ITA', '380', 'Italy', 'Italy', '1974', '.it', NULL),
	('JE', 'JEY', '832', 'Jersey', 'Jersey', '2006', '.je', 'A British Crown Dependency. Until 2006 included with the United Kingdom.'),
	('JM', 'JAM', '388', 'Jamaica', 'Jamaica', '1974', '.jm', NULL),
	('JO', 'JOR', '400', 'Jordan', 'Jordan', '1974', '.jo', NULL),
	('JP', 'JPN', '392', 'Japan', 'Japan', '1974', '.jp', NULL),
	('KE', 'KEN', '404', 'Kenya', 'Kenya', '1974', '.ke', NULL),
	('KG', 'KGZ', '417', 'Kyrgyzstan', 'Kyrgyzstan', '1992', '.kg', 'Until 1991 part of the USSR'),
	('KH', 'KHM', '116', 'Cambodia', 'Cambodia', '1974', '.kh', 'Code taken from former name: Khmer Republic. Previous ISO country name: Kampuchea, Democratic'),
	('KI', 'KIR', '296', 'Kiribati', 'Kiribati', '1979', '.ki', 'Name changed from Gilbert Islands (GE). Formerly Gilbert and Ellice Islands.'),
	('KM', 'COM', '174', 'Comoros', 'Comoros', '1974', '.km', 'Code taken from name in Comorian: Komori Previous ISO country name: Comoro Islands'),
	('KN', 'KNA', '659', 'Saint Kitts and Nevis', 'Saint Kitts and Nevis', '1974', '.kn', 'Previous ISO country name: Saint Kitts-Nevis-Anguilla. Until 1985 part of Saint Kitts-Nevis-Anguilla.'),
	('KP', 'PRK', '408', 'Korea, Democratic People\'s Republic of', 'North Korea', '1974', '.kp', 'ISO country name follows UN designation (common name: North Korea)'),
	('KR', 'KOR', '410', 'Korea, Republic of', 'South Korea', '1974', '.kr', 'ISO country name follows UN designation (common name: South Korea)'),
	('KW', 'KWT', '414', 'Kuwait', 'Kuwait', '1974', '.kw', NULL),
	('KY', 'CYM', '136', 'Cayman Islands', 'Cayman Islands', '1974', '.ky', NULL),
	('KZ', 'KAZ', '398', 'Kazakhstan', 'Kazakhstan', '1992', '.kz', 'Previous ISO country name: Kazakstan. Until 1991 part of the USSR.'),
	('LA', 'LAO', '418', 'Lao People\'s Democratic Republic', 'Laos', '1974', '.la', 'ISO country name follows UN designation (common name and previous ISO country name: Laos)'),
	('LB', 'LBN', '422', 'Lebanon', 'Lebanon', '1974', '.lb', NULL),
	('LC', 'LCA', '662', 'Saint Lucia', 'Saint Lucia', '1974', '.lc', NULL),
	('LI', 'LIE', '438', 'Liechtenstein', 'Liechtenstein', '1974', '.li', NULL),
	('LK', 'LKA', '144', 'Sri Lanka', 'Sri Lanka', '1974', '.lk', 'Formerly Ceylon'),
	('LR', 'LBR', '430', 'Liberia', 'Liberia', '1974', '.lr', NULL),
	('LS', 'LSO', '426', 'Lesotho', 'Lesotho', '1974', '.ls', NULL),
	('LT', 'LTU', '440', 'Lithuania', 'Lithuania', '1992', '.lt', 'LT formerly reserved indeterminately for Libya Tripoli. Until 1991 part of the USSR.'),
	('LU', 'LUX', '442', 'Luxembourg', 'Luxembourg', '1974', '.lu', NULL),
	('LV', 'LVA', '428', 'Latvia', 'Latvia', '1992', '.lv', 'Until 1991 part of the USSR'),
	('LY', 'LBY', '434', 'Libya', 'Libya', '1974', '.ly', 'Previous ISO country name: Libyan Arab Jamahiriya'),
	('MA', 'MAR', '504', 'Morocco', 'Morocco', '1974', '.ma', 'Code taken from name in French: Maroc'),
	('MC', 'MCO', '492', 'Monaco', 'Monaco', '1974', '.mc', NULL),
	('MD', 'MDA', '498', 'Moldova, Republic of', 'Moldova', '1992', '.md', 'Previous ISO country name: Moldova (briefly from 2008 to 2009). Until 1991 part of the USSR.'),
	('ME', 'MNE', '499', 'Montenegro', 'Montenegro', '2006', '.me', 'ME formerly reserved indeterminately for Western Sahara. Until 2006 part of Yugoslavia/Serbia and Montenegro.'),
	('MF', 'MAF', '663', 'Saint Martin (French part)', 'Saint Martin (French)', '2007', '.mf', 'The Dutch part of Saint Martin island is assigned code SX. Until 2007 part of Guadeloupe.'),
	('MG', 'MDG', '450', 'Madagascar', 'Madagascar', '1974', '.mg', NULL),
	('MH', 'MHL', '584', 'Marshall Islands', 'Marshall Islands', '1986', '.mh', NULL),
	('MK', 'MKD', '807', 'North Macedonia', 'North Macedonia', '1993', '.mk', 'Code taken from name in Macedonian: Severna Makedonija. Previous ISO country name: Macedonia, the former Yugoslav Republic of (designated as such due to Macedonia naming dispute). Until 1993 part of Yugoslavia.'),
	('ML', 'MLI', '466', 'Mali', 'Mali', '1974', '.ml', NULL),
	('MM', 'MMR', '104', 'Myanmar', 'Myanmar', '1989', '.mm', 'Name changed from Burma (BU)'),
	('MN', 'MNG', '496', 'Mongolia', 'Mongolia', '1974', '.mn', NULL),
	('MO', 'MAC', '446', 'Macao', 'Macao', '1974', '.mo', 'Previous ISO country name: Macau; Macao is officially a Special Administrative Region of the People\'s Republic of China since 20 December 1999'),
	('MP', 'MNP', '580', 'Northern Mariana Islands', 'Northern Mariana Islands', '1986', '.mp', 'Until 1986 part of Pacific Islands (Trust Territory)'),
	('MQ', 'MTQ', '474', 'Martinique', 'Martinique', '1974', '.mq', NULL),
	('MR', 'MRT', '478', 'Mauritania', 'Mauritania', '1974', '.mr', NULL),
	('MS', 'MSR', '500', 'Montserrat', 'Montserrat', '1974', '.ms', NULL),
	('MT', 'MLT', '470', 'Malta', 'Malta', '1974', '.mt', NULL),
	('MU', 'MUS', '480', 'Mauritius', 'Mauritius', '1974', '.mu', NULL),
	('MV', 'MDV', '462', 'Maldives', 'Maldives', '1974', '.mv', NULL),
	('MW', 'MWI', '454', 'Malawi', 'Malawi', '1974', '.mw', NULL),
	('MX', 'MEX', '484', 'Mexico', 'Mexico', '1974', '.mx', NULL),
	('MY', 'MYS', '458', 'Malaysia', 'Malaysia', '1974', '.my', NULL),
	('MZ', 'MOZ', '508', 'Mozambique', 'Mozambique', '1974', '.mz', NULL),
	('NA', 'NAM', '516', 'Namibia', 'Namibia', '1974', '.na', NULL),
	('NC', 'NCL', '540', 'New Caledonia', 'New Caledonia', '1974', '.nc', NULL),
	('NE', 'NER', '562', 'Niger', 'Niger', '1974', '.ne', NULL),
	('NF', 'NFK', '574', 'Norfolk Island', 'Norfolk Island', '1974', '.nf', 'External territory of Australia'),
	('NG', 'NGA', '566', 'Nigeria', 'Nigeria', '1974', '.ng', NULL),
	('NI', 'NIC', '558', 'Nicaragua', 'Nicaragua', '1974', '.ni', NULL),
	('NL', 'NLD', '528', 'Netherlands, Kingdom of the', 'Netherlands', '1974', '.nl', 'Officially includes the islands Bonaire, Saint Eustatius and Saba, which also have code BQ in ISO 3166-1. Within ISO 3166-2, Aruba (AW), Curaçao (CW), and Sint Maarten (SX) are also coded as subdivisions of NL. Previous ISO country name: Netherlands.'),
	('NO', 'NOR', '578', 'Norway', 'Norway', '1974', '.no', NULL),
	('NP', 'NPL', '524', 'Nepal', 'Nepal', '1974', '.np', NULL),
	('NR', 'NRU', '520', 'Nauru', 'Nauru', '1974', '.nr', NULL),
	('NU', 'NIU', '570', 'Niue', 'Niue', '1974', '.nu', 'Previous ISO country name: Niue Island'),
	('NZ', 'NZL', '554', 'New Zealand', 'New Zealand', '1974', '.nz', NULL),
	('OM', 'OMN', '512', 'Oman', 'Oman', '1974', '.om', 'Formerly Muscat and Oman'),
	('PA', 'PAN', '591', 'Panama', 'Panama', '1974', '.pa', NULL),
	('PE', 'PER', '604', 'Peru', 'Peru', '1974', '.pe', NULL),
	('PF', 'PYF', '258', 'French Polynesia', 'French Polynesia', '1974', '.pf', 'Code taken from name in French: Polynésie française'),
	('PG', 'PNG', '598', 'Papua New Guinea', 'Papua New Guinea', '1974', '.pg', NULL),
	('PH', 'PHL', '608', 'Philippines', 'Philippines', '1974', '.ph', NULL),
	('PK', 'PAK', '586', 'Pakistan', 'Pakistan', '1974', '.pk', NULL),
	('PL', 'POL', '616', 'Poland', 'Poland', '1974', '.pl', NULL),
	('PM', 'SPM', '666', 'Saint Pierre and Miquelon', 'Saint Pierre and Miquelon', '1974', '.pm', NULL),
	('PN', 'PCN', '612', 'Pitcairn', 'Pitcairn', '1974', '.pn', 'Previous ISO country name: Pitcairn Islands'),
	('PR', 'PRI', '630', 'Puerto Rico', 'Puerto Rico', '1974', '.pr', NULL),
	('PS', 'PSE', '275', 'Palestine, State of', 'Palestine', '1999', '.ps', 'Previous ISO country name: Palestinian Territory, Occupied. Consists of the West Bank and the Gaza Strip. Replaced the Gaza Strip, which was assigned code 274 by the United Nations Statistics Division.'),
	('PT', 'PRT', '620', 'Portugal', 'Portugal', '1974', '.pt', NULL),
	('PW', 'PLW', '585', 'Palau', 'Palau', '1986', '.pw', 'Until 1986 part of Pacific Islands (Trust Territory)'),
	('PY', 'PRY', '600', 'Paraguay', 'Paraguay', '1974', '.py', NULL),
	('QA', 'QAT', '634', 'Qatar', 'Qatar', '1974', '.qa', NULL),
	('RE', 'REU', '638', 'Réunion', 'Réunion', '1974', '.re', NULL),
	('RO', 'ROU', '642', 'Romania', 'Romania', '1974', '.ro', NULL),
	('RS', 'SRB', '688', 'Serbia', 'Serbia', '2006', '.rs', 'Republic of Serbia. Until 2006 part of Yugoslavia/Serbia and Montenegro.'),
	('RU', 'RUS', '643', 'Russian Federation', 'Russia', '1992', '.ru', 'ISO country name follows UN designation (common name: Russia); RU formerly reserved indeterminately for Burundi. Until 1991 part of the USSR.'),
	('RW', 'RWA', '646', 'Rwanda', 'Rwanda', '1974', '.rw', NULL),
	('SA', 'SAU', '682', 'Saudi Arabia', 'Saudi Arabia', '1974', '.sa', NULL),
	('SB', 'SLB', '090', 'Solomon Islands', 'Solomon Islands', '1974', '.sb', 'Code taken from former name: British Solomon Islands. Formerly British Solomon Islands.'),
	('SC', 'SYC', '690', 'Seychelles', 'Seychelles', '1974', '.sc', NULL),
	('SD', 'SDN', '729', 'Sudan', 'Sudan', '1974', '.sd', NULL),
	('SE', 'SWE', '752', 'Sweden', 'Sweden', '1974', '.se', NULL),
	('SG', 'SGP', '702', 'Singapore', 'Singapore', '1974', '.sg', NULL),
	('SH', 'SHN', '654', 'Saint Helena, Ascension and Tristan da Cunha', 'Saint Helena', '1974', '.sh', 'Previous ISO country name: Saint Helena.'),
	('SI', 'SVN', '705', 'Slovenia', 'Slovenia', '1992', '.si', 'Until 1992 part of Yugoslavia'),
	('SJ', 'SJM', '744', 'Svalbard and Jan Mayen', 'Svalbard and Jan Mayen', '1974', '.sj', 'Previous ISO name: Svalbard and Jan Mayen Islands. Consists of two Arctic territories of Norway: Svalbard and Jan Mayen'),
	('SK', 'SVK', '703', 'Slovakia', 'Slovakia', '1993', '.sk', 'SK previously represented the Kingdom of Sikkim. Until 1993 part of Czechoslovakia.'),
	('SL', 'SLE', '694', 'Sierra Leone', 'Sierra Leone', '1974', '.sl', NULL),
	('SM', 'SMR', '674', 'San Marino', 'San Marino', '1974', '.sm', NULL),
	('SN', 'SEN', '686', 'Senegal', 'Senegal', '1974', '.sn', NULL),
	('SO', 'SOM', '706', 'Somalia', 'Somalia', '1974', '.so', NULL),
	('SR', 'SUR', '740', 'Suriname', 'Suriname', '1974', '.sr', 'Previous ISO country name: Surinam'),
	('SS', 'SSD', '728', 'South Sudan', 'South Sudan', '2011', '.ss', 'Until 2011 part of Sudan'),
	('ST', 'STP', '678', 'Sao Tome and Principe', 'Sao Tome and Principe', '1974', '.st', NULL),
	('SV', 'SLV', '222', 'El Salvador', 'El Salvador', '1974', '.sv', NULL),
	('SX', 'SXM', '534', 'Sint Maarten (Dutch part)', 'Saint Martin (Dutch)', '2010', '.sx', 'The French part of Saint Martin island is assigned code MF. Until 2010 part of the Netherlands Antilles.'),
	('SY', 'SYR', '760', 'Syrian Arab Republic', 'Syria', '1974', '.sy', 'ISO country name follows UN designation (common name and previous ISO country name: Syria)'),
	('SZ', 'SWZ', '748', 'Eswatini', 'Eswatini', '1974', '.sz', 'Previous ISO country name: Swaziland. Formerly Swaziland'),
	('TC', 'TCA', '796', 'Turks and Caicos Islands', 'Turks and Caicos Islands', '1974', '.tc', NULL),
	('TD', 'TCD', '148', 'Chad', 'Chad', '1974', '.td', 'Code taken from name in French: Tchad'),
	('TF', 'ATF', '260', 'French Southern Territories', 'French Southern Territories', '1979', '.tf', 'Covers the French Southern and Antarctic Lands except Adélie Land. Code taken from name in French: Terres australes françaises'),
	('TG', 'TGO', '768', 'Togo', 'Togo', '1974', '.tg', NULL),
	('TH', 'THA', '764', 'Thailand', 'Thailand', '1974', '.th', NULL),
	('TJ', 'TJK', '762', 'Tajikistan', 'Tajikistan', '1992', '.tj', 'Until 1991 part of the USSR'),
	('TK', 'TKL', '772', 'Tokelau', 'Tokelau', '1974', '.tk', 'Previous ISO country name: Tokelau Islands'),
	('TL', 'TLS', '626', 'Timor-Leste', 'Timor-Leste', '2002', '.tl', 'Name changed from East Timor (TP). Formerly Portuguese Timor and East Timor.'),
	('TM', 'TKM', '795', 'Turkmenistan', 'Turkmenistan', '1992', '.tm', 'Until 1991 part of the USSR'),
	('TN', 'TUN', '788', 'Tunisia', 'Tunisia', '1974', '.tn', NULL),
	('TO', 'TON', '776', 'Tonga', 'Tonga', '1974', '.to', NULL),
	('TR', 'TUR', '792', 'Türkiye', 'Türkiye', '1974', '.tr', 'Previous ISO country name: Turkey'),
	('TT', 'TTO', '780', 'Trinidad and Tobago', 'Trinidad and Tobago', '1974', '.tt', NULL),
	('TV', 'TUV', '798', 'Tuvalu', 'Tuvalu', '1977', '.tv', NULL),
	('TW', 'TWN', '158', 'Taiwan, Province of China', 'Taiwan', '1974', '.tw', 'Covers the current jurisdiction of the Republic of China. ISO country name follows UN designation (due to political status of Taiwan within the UN)[16] (common name: Taiwan)'),
	('TZ', 'TZA', '834', 'Tanzania, United Republic of', 'Tanzania', '1974', '.tz', NULL),
	('UA', 'UKR', '804', 'Ukraine', 'Ukraine', '1974', '.ua', 'Previous ISO country name: Ukrainian SSR. Code assigned as the country was already a UN member since 1945. Until 1991 part of the USSR.'),
	('UG', 'UGA', '800', 'Uganda', 'Uganda', '1974', '.ug', NULL),
	('UM', 'UMI', '581', 'United States Minor Outlying Islands', 'US Minor Outlying Islands', '1986', '.um', 'Consists of nine minor insular areas of the United States: Baker Island, Howland Island, Jarvis Island, Johnston Atoll, Kingman Reef, Midway Islands, Navassa Island, Palmyra Atoll, and Wake Island. .um ccTLD was revoked in 2007. The United States Department of State uses the following user assigned alpha-2 codes for the nine territories, respectively, XB, XH, XQ, XU, XM, QM, XV, XL and QW. Merger of uninhabited U.S. islands on the Pacific Ocean in 1986.'),
	('US', 'USA', '840', 'United States of America', 'United States of America', '1974', '.us', 'Previous ISO country name: United States'),
	('UY', 'URY', '858', 'Uruguay', 'Uruguay', '1974', '.uy', NULL),
	('UZ', 'UZB', '860', 'Uzbekistan', 'Uzbekistan', '1992', '.uz', 'Until 1991 part of the USSR'),
	('VA', 'VAT', '336', 'Holy See', 'Vatican City State', '1974', '.va', 'Covers Vatican City, territory of the Holy See. Previous ISO country names: Vatican City State (Holy See) and Holy See (Vatican City State)'),
	('VC', 'VCT', '670', 'Saint Vincent and the Grenadines', 'Saint Vincent and the Grenadines', '1974', '.vc', NULL),
	('VE', 'VEN', '862', 'Venezuela, Bolivarian Republic of', 'Venezuela', '1974', '.ve', 'Previous ISO country name: Venezuela'),
	('VG', 'VGB', '092', 'Virgin Islands (British)', 'Virgin Islands (British)', '1974', '.vg', NULL),
	('VI', 'VIR', '850', 'Virgin Islands (U.S.)', 'Virgin Islands (U.S.)', '1974', '.vi', NULL),
	('VN', 'VNM', '704', 'Viet Nam', 'Vietnam', '1974', '.vn', 'ISO country name follows UN designation (common name: Vietnam). Code used for Republic of Viet Nam (common name: South Vietnam) before 1977. Official name Socialist Republic of Viet Nam.'),
	('VU', 'VUT', '548', 'Vanuatu', 'Vanuatu', '1980', '.vu', 'Name changed from New Hebrides (NH)'),
	('WF', 'WLF', '876', 'Wallis and Futuna', 'Wallis and Futuna', '1974', '.wf', 'Previous ISO country name: Wallis and Futuna Islands'),
	('WS', 'WSM', '882', 'Samoa', 'Samoa', '1974', '.ws', 'Code taken from former name: Western Samoa'),
	('YE', 'YEM', '887', 'Yemen', 'Yemen', '1974', '.ye', 'Previous ISO country name: Yemen, Republic of (for three years after the unification). Code used for North Yemen before unification in 1990.'),
	('YT', 'MYT', '175', 'Mayotte', 'Mayotte', '1993', '.yt', 'Until 1975 part of Comoros, own ISO code since 1993'),
	('ZA', 'ZAF', '710', 'South Africa', 'South Africa', '1974', '.za', 'Code taken from name in Dutch: Zuid-Afrika'),
	('ZM', 'ZMB', '894', 'Zambia', 'Zambia', '1974', '.zm', NULL),
	('ZW', 'ZWE', '716', 'Zimbabwe', 'Zimbabwe', '1980', '.zw', 'Name changed from Southern Rhodesia (RH)');

-- Dumping structure for table evidence_engine.declaration
CREATE TABLE IF NOT EXISTS `declaration` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'The unique declaration identifier',
  `kind` varchar(4) NOT NULL COMMENT 'The kind of declaration',
  `date` date NOT NULL COMMENT 'The date the declaration was first published',
  `title` varchar(100) NOT NULL COMMENT 'The declaration name or title',
  `country_code` char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the country to which the declaration pertains',
  `url` varchar(200) DEFAULT NULL COMMENT 'Web URL of the original declaration',
  `cached` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  `signatories` text DEFAULT NULL COMMENT 'The list of signatories, one per line',
  `signatory_count` smallint(6) DEFAULT NULL COMMENT 'The number of signatories',
  `notes` text DEFAULT NULL COMMENT 'Added notes about the declaration',
  PRIMARY KEY (`id`),
  KEY `FK_declaration_declaration_kind` (`kind`),
  KEY `FK_declaration_country` (`country_code`),
  FULLTEXT KEY `declaration_fulltext` (`title`,`signatories`,`notes`),
  CONSTRAINT `FK_declaration_country` FOREIGN KEY (`country_code`) REFERENCES `country` (`alpha_2`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_declaration_declaration_kind` FOREIGN KEY (`kind`) REFERENCES `declaration_kind` (`kind`) ON UPDATE CASCADE,
  CONSTRAINT `FK_declaration_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Details of public declarations and open letters expressing climate scepticism';

-- Dumping data for table evidence_engine.declaration: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.declaration_kind
CREATE TABLE IF NOT EXISTS `declaration_kind` (
  `kind` varchar(4) NOT NULL COMMENT 'The declaration kind code',
  `label` varchar(20) NOT NULL COMMENT 'Label for the declaration kind',
  `description` varchar(50) NOT NULL COMMENT 'Description of the declaration kind',
  PRIMARY KEY (`kind`),
  UNIQUE KEY `declaration_kind_label` (`label`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A lookup table for validating declaration records';

-- Dumping data for table evidence_engine.declaration_kind: ~3 rows (approximately)
INSERT IGNORE INTO `declaration_kind` (`kind`, `label`, `description`) VALUES
	('DECL', 'Declaration', 'A public declaration'),
	('OPLE', 'Open Letter', 'An open letter'),
	('PETN', 'Petition', 'A petition');

-- Dumping structure for table evidence_engine.entity
CREATE TABLE IF NOT EXISTS `entity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique entity record identifier',
  `dtype` char(3) NOT NULL COMMENT 'The entity type discriminator',
  `status` char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  `created` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  `created_by_user_id` bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the user who created the record',
  `updated` timestamp NULL DEFAULT NULL COMMENT 'When the record was last updated',
  `updated_by_user_id` bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the user who last updated the record',
  PRIMARY KEY (`id`),
  KEY `FK_entity_dtype` (`dtype`),
  KEY `FK_entity_status` (`status`),
  KEY `FK_entity_created_by_user` (`created_by_user_id`),
  KEY `FK_entity_updated_by_user` (`updated_by_user_id`),
  CONSTRAINT `FK_entity_created_by_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_entity_dtype` FOREIGN KEY (`dtype`) REFERENCES `entity_kind` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_entity_status` FOREIGN KEY (`status`) REFERENCES `status_kind` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_entity_updated_by_user` FOREIGN KEY (`updated_by_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Base table for all tracked and linkable entities';

-- Dumping data for table evidence_engine.entity: ~2 rows (approximately)
INSERT IGNORE INTO `entity` (`id`, `dtype`, `status`, `created`, `created_by_user_id`, `updated`, `updated_by_user_id`) VALUES
	(1, 'USR', 'PUB', '2025-03-22 15:37:45', 1, NULL, NULL),
	(2, 'USR', 'PUB', '2025-03-22 15:48:00', 1, NULL, NULL),
	(3, 'USR', 'PUB', '2025-03-22 15:48:00', 1, NULL, NULL),
	(4, 'USR', 'PUB', '2025-03-22 15:48:01', 1, NULL, NULL);

-- Dumping structure for table evidence_engine.entity_kind
CREATE TABLE IF NOT EXISTS `entity_kind` (
  `code` char(3) NOT NULL COMMENT 'Unique code for the entity kind',
  `label` varchar(20) NOT NULL COMMENT 'Label for the entity kind',
  PRIMARY KEY (`code`),
  UNIQUE KEY `entity_kind_label` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Lookup table enumerating supported entity types';

-- Dumping data for table evidence_engine.entity_kind: ~11 rows (approximately)
INSERT IGNORE INTO `entity_kind` (`code`, `label`) VALUES
	('CLA', 'Claim'),
	('COU', 'Country'),
	('DEC', 'Declaration'),
	('GRP', 'Group'),
	('LNK', 'EntityLink'),
	('JOU', 'Journal'),
	('PER', 'Person'),
	('PUB', 'Publication'),
	('PBR', 'Publisher'),
	('QUO', 'Quotation'),
	('TOP', 'Topic'),
	('USR', 'User');

-- Dumping structure for table evidence_engine.entity_link
CREATE TABLE IF NOT EXISTS `entity_link` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'The unique entity link identifier',
  `from_entity_id` bigint(20) unsigned NOT NULL COMMENT 'The linked-from entity ID',
  `to_entity_id` bigint(20) unsigned NOT NULL COMMENT 'The linked-to entity ID',
  `from_entity_locations` varchar(500) NOT NULL DEFAULT '' COMMENT 'Location(s) within the linked-from entity (where applicable), one per line',
  `to_entity_locations` varchar(500) NOT NULL DEFAULT '' COMMENT 'Location(s) within the linked-to entity (where applicable), one per line',
  PRIMARY KEY (`id`),
  KEY `FK_entity_link_from_entity` (`from_entity_id`),
  KEY `FK_entity_link_to_entity` (`to_entity_id`),
  FULLTEXT KEY `entity_fulltext` (`from_entity_locations`,`to_entity_locations`),
  CONSTRAINT `FK_entity_link_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_entity_link_from_entity` FOREIGN KEY (`from_entity_id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_entity_link_to_entity` FOREIGN KEY (`to_entity_id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Self-association table to hold links between linkable entities';

-- Dumping data for table evidence_engine.entity_link: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.group
CREATE TABLE IF NOT EXISTS `group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique system-assigned group identifier',
  `groupname` varchar(50) NOT NULL COMMENT 'The group name',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_groupname` (`groupname`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds groups to which users can belong';

-- Dumping data for table evidence_engine.group: ~3 rows (approximately)
INSERT IGNORE INTO `group` (`id`, `groupname`) VALUES
	(1, 'Administrators'),
	(2, 'Editors'),
	(3, 'Users');

-- Dumping structure for table evidence_engine.group_authority
CREATE TABLE IF NOT EXISTS `group_authority` (
  `group_id` bigint(20) unsigned NOT NULL COMMENT 'ID of a group',
  `authority` char(3) NOT NULL COMMENT 'The granted authority code',
  UNIQUE KEY `group_authority` (`group_id`,`authority`),
  KEY `FK_group_authority_authority` (`authority`),
  CONSTRAINT `FK_group_authority_group` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_group_authority_authority` FOREIGN KEY (`authority`) REFERENCES `authority_kind` (`code`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds authorities granted to groups';

-- Dumping data for table evidence_engine.group_authority: ~13 rows (approximately)
INSERT IGNORE INTO `group_authority` (`group_id`, `authority`) VALUES
	(1, 'ADM'),
	(1, 'CRE'),
	(1, 'DEL'),
	(1, 'LNK'),
	(1, 'REA'),
	(1, 'UPD'),
	(1, 'UPL'),
	(2, 'CRE'),
	(2, 'LNK'),
	(2, 'REA'),
	(2, 'UPD'),
	(2, 'UPL'),
	(3, 'REA');

-- Dumping structure for table evidence_engine.group_user
CREATE TABLE IF NOT EXISTS `group_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique system-assigned identifier',
  `group_id` bigint(20) unsigned NOT NULL COMMENT 'ID of the group to which user belongs',
  `username` varchar(50) NOT NULL COMMENT 'The login user name',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_user` (`username`,`group_id`),
  KEY `FK_group_user_group` (`group_id`),
  CONSTRAINT `FK_group_user_group` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_group_user_user` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Defines group membership';

-- Dumping data for table evidence_engine.group_user: ~4 rows (approximately)
INSERT IGNORE INTO `group_user` (`id`, `group_id`, `username`) VALUES
	(2, 1, 'admin'),
	(3, 2, 'editor'),
	(1, 1, 'root'),
	(4, 3, 'user');

-- Dumping structure for table evidence_engine.journal
CREATE TABLE IF NOT EXISTS `journal` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'The journal ID',
  `title` varchar(100) NOT NULL COMMENT 'The journal, etc. title',
  `abbreviation` varchar(50) DEFAULT NULL COMMENT 'The abbreviation for title',
  `url` varchar(200) DEFAULT NULL COMMENT 'Web link to the journal''s home page',
  `issn` char(9) DEFAULT NULL COMMENT 'The International Standard Serial Number',
  `publisher_id` bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the publisher',
  `notes` varchar(200) DEFAULT NULL COMMENT 'A brief description of the journal',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `journal_issn` (`issn`) USING BTREE,
  KEY `FK_journal_publisher` (`publisher_id`),
  KEY `journal_title` (`title`) USING BTREE,
  KEY `journal_abbreviation` (`abbreviation`) USING BTREE,
  FULLTEXT KEY `journal_fulltext` (`title`,`abbreviation`,`url`,`issn`,`notes`),
  CONSTRAINT `FK_journal_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_journal_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `CC_journal_issn` CHECK (`issn` regexp '^[0-9]{4}-[0-9]{3}[0-9X]$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Definitive list of journal titles, abbreviations, etc.';

-- Dumping data for table evidence_engine.journal: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.log
CREATE TABLE IF NOT EXISTS `log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The log entry ID',
  `timestamp` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'The date and time at which the log entry was made',
  `user_id` bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT 'The ID of the user who made the change',
  `transaction_kind` char(3) NOT NULL COMMENT 'The kind of change that was made',
  `entity_kind` char(3) NOT NULL COMMENT 'The kind of entity affected by the change',
  `entity_id` bigint(20) unsigned NOT NULL COMMENT 'The ID of the affected entity',
  `linked_entity_kind` char(3) DEFAULT NULL COMMENT 'The kind of entity that was linked/unlinked',
  `linked_entity_id` bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the entity that was linked/unlinked',
  PRIMARY KEY (`id`),
  KEY `log_entity` (`entity_kind`,`entity_id`),
  KEY `log_linked_entity` (`linked_entity_kind`,`linked_entity_id`),
  KEY `FK_log_transaction_kind` (`transaction_kind`),
  KEY `log_user` (`user_id`) USING BTREE,
  KEY `FK_log_entity_id` (`entity_id`),
  KEY `FK_log_linked_entity_id` (`linked_entity_id`),
  CONSTRAINT `FK_log_entity_id` FOREIGN KEY (`entity_id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_log_entity_kind` FOREIGN KEY (`entity_kind`) REFERENCES `entity_kind` (`code`) ON UPDATE CASCADE,
  CONSTRAINT `FK_log_linked_entity_id` FOREIGN KEY (`linked_entity_id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_log_linked_entity_kind` FOREIGN KEY (`linked_entity_kind`) REFERENCES `entity_kind` (`code`) ON UPDATE CASCADE,
  CONSTRAINT `FK_log_transaction_kind` FOREIGN KEY (`transaction_kind`) REFERENCES `transaction_kind` (`code`) ON UPDATE CASCADE,
  CONSTRAINT `FK_log_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A log of all transactions';

-- Dumping data for table evidence_engine.log: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.authority_kind
CREATE TABLE IF NOT EXISTS `authority_kind` (
  `code` char(3) NOT NULL COMMENT 'Unique authority code',
  `label` varchar(10) NOT NULL COMMENT 'Unique authority label',
  `description` varchar(50) NOT NULL COMMENT 'Description of the authority',
  PRIMARY KEY (`code`),
  UNIQUE KEY `authority_kind_label` (`label`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Access authorities that can be granted to users';

-- Dumping data for table evidence_engine.authority_kind: ~7 rows (approximately)
INSERT IGNORE INTO `authority_kind` (`code`, `label`, `description`) VALUES
	('ADM', 'Administer', 'Use administrative functions'),
	('CRE', 'Create', 'Insert new record'),
	('DEL', 'Delete', 'Delete existing record'),
	('LNK', 'Link', 'Link existing records'),
	('REA', 'Read', 'Read existing record'),
	('UPD', 'Update', 'Update existing record'),
	('UPL', 'Upload', 'Upload file');

-- Dumping structure for table evidence_engine.persistent_login
CREATE TABLE IF NOT EXISTS `persistent_login` (
  `series` varchar(64) NOT NULL COMMENT 'Encoded random number used to detect cookie stealing',
  `username` varchar(64) NOT NULL COMMENT 'The authenticated username',
  `token` varchar(64) NOT NULL COMMENT 'The authentication token returned as a cookie',
  `last_used` timestamp NOT NULL COMMENT 'The date/time at which the token was last used',
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table evidence_engine.persistent_login: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.person
CREATE TABLE IF NOT EXISTS `person` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'Unique person identifier',
  `title` varchar(10) DEFAULT NULL COMMENT 'Person''s title, e.g., Prof., Dr.',
  `first_name` varchar(80) DEFAULT NULL COMMENT 'Person''s first names and/or initials',
  `nickname` varchar(40) DEFAULT NULL COMMENT 'Nickname by which commonly known',
  `prefix` varchar(20) DEFAULT NULL COMMENT 'Prefix to last name, e.g., van, de',
  `last_name` varchar(40) NOT NULL COMMENT 'Person''s last name,  without prefix or suffix',
  `suffix` varchar(16) DEFAULT NULL COMMENT 'Suffix to last name, e.g. Jr., Sr.',
  `alias` varchar(40) DEFAULT NULL COMMENT 'Alternative last name',
  `notes` text DEFAULT NULL COMMENT 'Brief biography, notes, etc.',
  `qualifications` text DEFAULT NULL COMMENT 'Academic qualifications',
  `country_code` char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for country of primary professional association',
  `rating` tinyint(4) NOT NULL DEFAULT 0 COMMENT 'Eminence star rating, 0..5',
  `checked` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Set when the person''s credentials have been checked',
  `published` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Set if person has published peer-reviewed papers on climate change',
  PRIMARY KEY (`id`),
  KEY `person_title` (`title`) USING BTREE,
  KEY `person_first_name` (`first_name`) USING BTREE,
  KEY `person_last_name` (`last_name`) USING BTREE,
  KEY `person_qualifications` (`qualifications`(768)) USING BTREE,
  KEY `person_rating` (`rating`) USING BTREE,
  KEY `person_country` (`country_code`) USING BTREE,
  KEY `person_notes` (`notes`(768)) USING BTREE,
  FULLTEXT KEY `person_fulltext` (`title`,`first_name`,`nickname`,`prefix`,`last_name`,`suffix`,`alias`,`notes`,`qualifications`),
  CONSTRAINT `FK_person_country` FOREIGN KEY (`country_code`) REFERENCES `country` (`alpha_2`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_person_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `CC_person_rating` CHECK (`rating` between 0 and 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='People who have publicly expressed contrarian/sceptical views about topic orthodoxy, whether by signing declarations, open letters or publishing science articles.';

-- Dumping data for table evidence_engine.person: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.publication
CREATE TABLE IF NOT EXISTS `publication` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'Unique publication ID',
  `title` varchar(200) NOT NULL COMMENT 'Publication title',
  `authors` varchar(2000) NOT NULL COMMENT 'List of author names',
  `journal_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Journal title',
  `kind` varchar(6) NOT NULL COMMENT 'The kind of publication',
  `date` date DEFAULT NULL COMMENT 'Publication date',
  `year` year(4) DEFAULT NULL COMMENT 'Publication year',
  `location` varchar(50) DEFAULT NULL COMMENT 'The location of the relevant section within the publication',
  `abstract` text DEFAULT NULL COMMENT 'Abstract from the article',
  `notes` text DEFAULT NULL COMMENT 'Added notes about the publication',
  `peer_reviewed` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Whether the article was peer-reviewed',
  `doi` varchar(255) DEFAULT NULL COMMENT 'Digital Object Identifier',
  `isbn` varchar(20) DEFAULT NULL COMMENT 'International Standard Book Number (printed publications only)',
  `url` varchar(200) DEFAULT NULL COMMENT 'URL of the publication',
  `cached` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  `accessed` date DEFAULT NULL COMMENT 'Date a web page was accessed',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `publication_doi` (`doi`) USING BTREE,
  UNIQUE KEY `publication_isbn` (`isbn`) USING BTREE,
  KEY `FK_publication_journal` (`journal_id`),
  KEY `FK_publication_publication_kind` (`kind`),
  FULLTEXT KEY `publication_fulltext` (`title`,`authors`,`abstract`,`notes`,`doi`,`isbn`,`url`),
  CONSTRAINT `FK_publication_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_publication_journal` FOREIGN KEY (`journal_id`) REFERENCES `journal` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_publication_publication_kind` FOREIGN KEY (`kind`) REFERENCES `publication_kind` (`kind`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='References to published articles, peer-reviewed or otherwise.';

-- Dumping data for table evidence_engine.publication: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.publication_kind
CREATE TABLE IF NOT EXISTS `publication_kind` (
  `kind` varchar(10) NOT NULL COMMENT 'The publication type per TY field in RIS specification',
  `label` varchar(25) NOT NULL COMMENT 'Label for the publication kind',
  PRIMARY KEY (`kind`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Publication kind per TY field in RIS specification';

-- Dumping data for table evidence_engine.publication_kind: ~57 rows (approximately)
INSERT IGNORE INTO `publication_kind` (`kind`, `label`) VALUES
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

-- Dumping structure for table evidence_engine.publisher
CREATE TABLE IF NOT EXISTS `publisher` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'The unique publisher identifier',
  `name` varchar(200) NOT NULL COMMENT 'The publisher name',
  `location` varchar(50) DEFAULT NULL COMMENT 'The publisher location',
  `country_code` char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the publisher''s country',
  `url` varchar(200) DEFAULT NULL COMMENT 'URL of publisher''s home page',
  `journal_count` smallint(6) unsigned DEFAULT NULL COMMENT 'The number of journals published',
  PRIMARY KEY (`id`),
  KEY `FK_publisher_country` (`country_code`) USING BTREE,
  KEY `publisher_name` (`name`),
  FULLTEXT KEY `publisher_fulltext` (`name`,`location`,`url`),
  CONSTRAINT `FK_publisher_country` FOREIGN KEY (`country_code`) REFERENCES `country` (`alpha_2`) ON UPDATE CASCADE,
  CONSTRAINT `FK_publisher_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A list of book, journal, etc. publishers. The table can contain duplicate entries in the name column, reflecting the same publisher in different locations.';

-- Dumping data for table evidence_engine.publisher: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.quotation
CREATE TABLE IF NOT EXISTS `quotation` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'Unique quotation identifier',
  `quotee` varchar(50) NOT NULL COMMENT 'The person(s) who made the quotation',
  `text` varchar(1000) NOT NULL COMMENT 'The quotation text',
  `date` date DEFAULT NULL COMMENT 'The quotation date',
  `source` varchar(200) DEFAULT NULL COMMENT 'The source of the quotation',
  `url` varchar(200) DEFAULT NULL COMMENT 'Web url to the quotation',
  `notes` text DEFAULT NULL COMMENT 'Added notes about the quotation',
  PRIMARY KEY (`id`),
  KEY `quotation_quotee` (`quotee`) USING BTREE,
  FULLTEXT KEY `quotation_fulltext` (`quotee`,`text`,`source`,`url`,`notes`),
  CONSTRAINT `FK_quotation_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Quotations by leading sceptics and contrarians';

-- Dumping data for table evidence_engine.quotation: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.status_kind
CREATE TABLE IF NOT EXISTS `status_kind` (
  `code` char(3) NOT NULL COMMENT 'The status code',
  `label` varchar(20) NOT NULL COMMENT 'The status label',
  `description` varchar(100) NOT NULL COMMENT 'Defines the meaning of the status code',
  PRIMARY KEY (`code`),
  UNIQUE KEY `status_kind_label` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='The set of status values defining an entity''s lifecycle.';

-- Dumping data for table evidence_engine.status_kind: ~4 rows (approximately)
INSERT IGNORE INTO `status_kind` (`code`, `label`, `description`) VALUES
	('DEL', 'Deleted', 'The record still exists but is flagged as deleted'),
	('DRA', 'Draft', 'A draft/newly inserted record'),
	('PUB', 'Published', 'The record is officially published'),
	('SUS', 'Suspended', 'The record is suspended/disabled/hidden from ordinary users');

-- Dumping structure for table evidence_engine.topic
CREATE TABLE IF NOT EXISTS `topic` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'The unique topic identifier',
  `label` varchar(50) NOT NULL COMMENT 'The topic name/label',
  `description` varchar(500) DEFAULT NULL COMMENT 'Notes on when to use the topic',
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT 'The parent topic ID',
  PRIMARY KEY (`id`),
  KEY `FK_topic_topic` (`parent_id`),
  FULLTEXT KEY `topic_fulltext` (`label`,`description`),
  CONSTRAINT `FK_topic_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_topic_topic` FOREIGN KEY (`parent_id`) REFERENCES `topic` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A topic in a hierarchy of such topics, with arbitrary breadth and depth';

-- Dumping data for table evidence_engine.topic: ~0 rows (approximately)

-- Dumping structure for table evidence_engine.transaction_kind
CREATE TABLE IF NOT EXISTS `transaction_kind` (
  `code` char(3) NOT NULL COMMENT 'The transaction code',
  `label` varchar(20) NOT NULL COMMENT 'A UI label for the transaction kind',
  `description` varchar(50) NOT NULL COMMENT 'Description of the transaction kind',
  PRIMARY KEY (`code`),
  UNIQUE KEY `transaction_kind_label` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Enumerates the possible transaction kinds';

-- Dumping data for table evidence_engine.transaction_kind: ~5 rows (approximately)
INSERT IGNORE INTO `transaction_kind` (`code`, `label`, `description`) VALUES
	('CRE', 'Create', 'A new record was created'),
	('DEL', 'Delete', 'A record was marked as deleted'),
	('LNK', 'Link', 'One record was linked to another'),
	('UNL', 'Unlink', 'Two linked records were unlinked'),
	('UPD', 'Update', 'A record was updated');

-- Dumping structure for table evidence_engine.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'The unique system-assigned user identifier',
  `username` varchar(50) NOT NULL COMMENT 'The unique user-assigned user name',
  `password` varchar(500) NOT NULL COMMENT 'Hash of the user''s password',
  `enabled` bit(1) NOT NULL DEFAULT b'1' COMMENT 'Whether the user account is enabled',
  `first_name` varchar(50) DEFAULT NULL COMMENT 'The user''s first name',
  `last_name` varchar(50) DEFAULT NULL COMMENT 'The user''s last name',
  `email` varchar(100) DEFAULT NULL COMMENT 'The user''s email address, used for sign-in',
  `country_code` char(2) DEFAULT NULL COMMENT 'ISO-3166-1 alpha-2 code for user''s country of residence',
  `notes` text DEFAULT NULL COMMENT 'Added notes about the user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `FK_user_country` (`country_code`),
  FULLTEXT KEY `user_fulltext` (`username`,`first_name`,`last_name`,`email`,`notes`),
  CONSTRAINT `FK_user_country` FOREIGN KEY (`country_code`) REFERENCES `country` (`alpha_2`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `FK_user_entity` FOREIGN KEY (`id`) REFERENCES `entity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds details of authenticatable users';

-- Dumping data for table evidence_engine.user: ~4 rows (approximately)
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `enabled`, `first_name`, `last_name`, `email`, `country_code`, `notes`) VALUES
	(1, 'root', '{bcrypt}$2a$10$xL02gfgl.dEJBRzsgics5.fglRXyl.iQBNjfyXhCU14UQf2MJUHFK', b'1', 'Root', 'User', NULL, 'GB', NULL),
	(2, 'admin', '{bcrypt}$2a$10$y9JB/y3fdX7.PUsOEadAi.gErBWCd.8oGn8IEE0KWjURLZEJ20GQi', b'1', 'Administrative', 'User', NULL, 'GB', NULL),
	(3, 'editor', '{bcrypt}$2a$10$Yjve/6JOwx4vbmpCv7GXO.VAqSWaO8jgxjUXYh6H/fqaKq9WOaMbm', b'1', 'Editing', 'User', NULL, 'GB', NULL),
	(4, 'user', '{bcrypt}$2a$10$Yjve/6JOwx4vbmpCv7GXO.VAqSWaO8jgxjUXYh6H/fqaKq9WOaMbm', b'1', 'Ordinary', 'User', NULL, 'GB', NULL);

-- Dumping structure for table evidence_engine.user_authority
CREATE TABLE IF NOT EXISTS `user_authority` (
  `username` varchar(50) NOT NULL COMMENT 'The login user name',
  `authority` char(3) NOT NULL COMMENT 'The granted authority code',
  UNIQUE KEY `user_authority` (`username`,`authority`),
  KEY `FK_user_authority_authority` (`authority`),
  CONSTRAINT `FK_user_authority_authority` FOREIGN KEY (`authority`) REFERENCES `authority_kind` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_authority_user` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds authorities granted to users';

-- Dumping data for table evidence_engine.user_authority: ~0 rows (approximately)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
