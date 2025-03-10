-- ----------------------------------------------------------------------------------------------------------------------
-- Evidence Engine: A system for managing evidence on arbitrary scientific topics.
-- Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
-- Copyright © 2024-25 Adrian Price. All rights reserved.
--
-- This file is part of Evidence Engine.
--
-- Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
-- GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
-- or (at your option) any later version.
--
-- Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
-- without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
-- See the GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
-- If not, see <https://www.gnu.org/licenses/>. 
-- ----------------------------------------------------------------------------------------------------------------------

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

-- Dumping structure for table evidence_engine.claim
CREATE TABLE IF NOT EXISTS "claim" (
  "id" int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique claim identifier',
  "text" varchar(500) NOT NULL COMMENT 'The claim text',
  "date" date DEFAULT NULL COMMENT 'The date on which the claim was first made',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the claim',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  KEY "FK_claim_created_user" ("created_by_user_id"),
  KEY "FK_claim_updated_user" ("updated_by_user_id"),
  KEY "FK_claim_status" ("status") USING BTREE,
  FULLTEXT KEY "claim_fulltext" ("text","notes"),
  CONSTRAINT "FK_claim_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_claim_status_kind" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_claim_updated_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Claims, typically contrarian, made in respect of an associated topic, ';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.claim_declaration
CREATE TABLE IF NOT EXISTS "claim_declaration" (
  "claim_id" int(11) unsigned NOT NULL COMMENT 'The claim ID',
  "declaration_id" int(11) unsigned NOT NULL COMMENT 'The declaration ID',
  PRIMARY KEY ("claim_id","declaration_id") USING BTREE,
  KEY "FK_claim_declaration_claim" ("claim_id") USING BTREE,
  KEY "FK_claim_declaration_declaration" ("declaration_id") USING BTREE,
  CONSTRAINT "FK_claim_declaration_claim" FOREIGN KEY ("claim_id") REFERENCES "claim" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_claim_declaration_declaration" FOREIGN KEY ("declaration_id") REFERENCES "declaration" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links claims to declarations.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.claim_person
CREATE TABLE IF NOT EXISTS "claim_person" (
  "claim_id" int(11) unsigned NOT NULL COMMENT 'The claim ID',
  "person_id" int(11) unsigned NOT NULL COMMENT 'The person ID',
  PRIMARY KEY ("claim_id","person_id") USING BTREE,
  KEY "FK_claim_person_claim" ("claim_id") USING BTREE,
  KEY "FK_claim_person_person" ("person_id") USING BTREE,
  CONSTRAINT "FK_claim_person_claim" FOREIGN KEY ("claim_id") REFERENCES "claim" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_claim_person_person" FOREIGN KEY ("person_id") REFERENCES "person" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links claims to persons.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.claim_publication
CREATE TABLE IF NOT EXISTS "claim_publication" (
  "claim_id" int(11) unsigned NOT NULL COMMENT 'The claim ID',
  "publication_id" int(11) unsigned NOT NULL COMMENT 'The publication ID',
  PRIMARY KEY ("claim_id","publication_id") USING BTREE,
  KEY "FK_claim_publication_claim" ("claim_id") USING BTREE,
  KEY "FK_claim_publication_publication" ("publication_id") USING BTREE,
  CONSTRAINT "FK_claim_publication_claim" FOREIGN KEY ("claim_id") REFERENCES "claim" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_claim_publication_publication" FOREIGN KEY ("publication_id") REFERENCES "publication" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links claims to publications.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.claim_quotation
CREATE TABLE IF NOT EXISTS "claim_quotation" (
  "claim_id" int(11) unsigned NOT NULL COMMENT 'The claim ID',
  "quotation_id" int(11) unsigned NOT NULL COMMENT 'The quotation ID',
  PRIMARY KEY ("claim_id","quotation_id") USING BTREE,
  KEY "FK_claim_quotation_claim" ("claim_id") USING BTREE,
  KEY "FK_claim_quotation_quotation" ("quotation_id") USING BTREE,
  CONSTRAINT "FK_claim_quotation_claim" FOREIGN KEY ("claim_id") REFERENCES "claim" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_claim_quotation_quotation" FOREIGN KEY ("quotation_id") REFERENCES "quotation" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links claims to quotations.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.country
CREATE TABLE IF NOT EXISTS "country" (
  "alpha_2" char(2) NOT NULL COMMENT 'ISO-3166-1 alpha-2 code',
  "alpha_3" char(3) NOT NULL COMMENT 'ISO-3166-1 alpha-3 code',
  "numeric" char(3) NOT NULL COMMENT 'ISO-3166-1 numeric code',
  "iso_name" varchar(100) NOT NULL COMMENT 'Official/ISO country name',
  "common_name" varchar(50) NOT NULL COMMENT 'Common or short name',
  "year" year(4) NOT NULL COMMENT 'Year alpha-2 code was first assigned',
  "cc_tld" char(3) NOT NULL COMMENT 'Country code top level domain',
  "notes" text DEFAULT NULL COMMENT 'Remarks as per Wikipedia ISO-3166-1 entry',
  PRIMARY KEY ("alpha_2"),
  UNIQUE KEY "country_alpha_3" ("alpha_3") USING BTREE,
  UNIQUE KEY "country_numeric" ("numeric") USING BTREE,
  UNIQUE KEY "country_common_name" ("common_name") USING BTREE,
  UNIQUE KEY "country_iso_name" ("iso_name") USING BTREE,
  CONSTRAINT "CC_country_numeric" CHECK ("numeric" regexp '^\\d{3}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Lookup table for converting ISO-3166-1 country codes to country name';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.declaration
CREATE TABLE IF NOT EXISTS "declaration" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique declaration identifier',
  "kind" varchar(4) NOT NULL COMMENT 'The kind of declaration',
  "title" varchar(100) NOT NULL COMMENT 'The declaration name or title',
  "date" date NOT NULL COMMENT 'The date the declaration was first published',
  "country_code" char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the country to which the declaration pertains',
  "url" varchar(200) DEFAULT NULL COMMENT 'Web URL of the original declaration',
  "cached" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  "signatories" text DEFAULT NULL COMMENT 'The list of signatories, one per line',
  "signatory_count" smallint(6) DEFAULT NULL COMMENT 'The number of signatories',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the declaration',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  KEY "FK_declaration_declaration_kind" ("kind"),
  KEY "FK_declaration_country" ("country_code"),
  KEY "FK_declaration_status_kind" ("status") USING BTREE,
  KEY "FK_declaration_created_user" ("created_by_user_id"),
  KEY "FK_declaration_updated_user" ("updated_by_user_id"),
  FULLTEXT KEY "declaration_fulltext" ("title","signatories","notes"),
  CONSTRAINT "FK_declaration_country" FOREIGN KEY ("country_code") REFERENCES "country" ("alpha_2") ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT "FK_declaration_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_declaration_declaration_kind" FOREIGN KEY ("kind") REFERENCES "declaration_kind" ("kind") ON UPDATE CASCADE,
  CONSTRAINT "FK_declaration_status_kind" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_declaration_updated_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Details of public declarations and open letters expressing climate scepticism';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.declaration_kind
CREATE TABLE IF NOT EXISTS "declaration_kind" (
  "kind" varchar(4) NOT NULL COMMENT 'The declaration kind code',
  "label" varchar(20) NOT NULL COMMENT 'Label for the declaration kind',
  "description" varchar(50) NOT NULL COMMENT 'Description of the declaration kind',
  PRIMARY KEY ("kind"),
  UNIQUE KEY "declaration_kind_label" ("label") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A lookup table for validating declaration records';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.declaration_person
CREATE TABLE IF NOT EXISTS "declaration_person" (
  "declaration_id" int(11) unsigned NOT NULL COMMENT 'The declaration ID',
  "person_id" int(11) unsigned NOT NULL COMMENT 'The person ID',
  PRIMARY KEY ("declaration_id","person_id") USING BTREE,
  KEY "FK_declaration_person_declaration" ("declaration_id") USING BTREE,
  KEY "FK_declaration_person_person" ("person_id") USING BTREE,
  CONSTRAINT "FK_declaration_person_declaration" FOREIGN KEY ("declaration_id") REFERENCES "declaration" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_declaration_person_person" FOREIGN KEY ("person_id") REFERENCES "person" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links declarations to persons.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.declaration_quotation
CREATE TABLE IF NOT EXISTS "declaration_quotation" (
  "declaration_id" int(11) unsigned NOT NULL COMMENT 'The declaration ID',
  "quotation_id" int(11) unsigned NOT NULL COMMENT 'The quotation ID',
  PRIMARY KEY ("declaration_id","quotation_id") USING BTREE,
  KEY "FK_declaration_quotation_declaration" ("declaration_id") USING BTREE,
  KEY "FK_declaration_quotation_quotation" ("quotation_id") USING BTREE,
  CONSTRAINT "FK_declaration_quotation_declaration" FOREIGN KEY ("declaration_id") REFERENCES "declaration" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_declaration_quotation_quotation" FOREIGN KEY ("quotation_id") REFERENCES "quotation" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links declarations to quotations.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.entity_kind
CREATE TABLE IF NOT EXISTS "entity_kind" (
  "code" char(3) NOT NULL COMMENT 'Unique code for the entity kind',
  "label" varchar(20) NOT NULL COMMENT 'Label for the entity kind',
  PRIMARY KEY ("code"),
  UNIQUE KEY "entity_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Lookup table enumerating supported entity types';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.journal
CREATE TABLE IF NOT EXISTS "journal" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The journal ID',
  "title" varchar(100) NOT NULL COMMENT 'The journal, etc. title',
  "abbreviation" varchar(50) DEFAULT NULL COMMENT 'The abbreviation for title',
  "url" varchar(200) DEFAULT NULL COMMENT 'Web link to the journal''s home page',
  "issn" char(9) DEFAULT NULL COMMENT 'The International Standard Serial Number',
  "publisher_id" int(10) unsigned DEFAULT NULL COMMENT 'The ID of the publisher',
  "notes" varchar(200) DEFAULT NULL COMMENT 'A brief description of the journal',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "journal_issn" ("issn") USING BTREE,
  KEY "FK_journal_publisher" ("publisher_id"),
  KEY "FK_journal_status_kind" ("status"),
  KEY "journal_title" ("title") USING BTREE,
  KEY "journal_abbreviation" ("abbreviation") USING BTREE,
  KEY "FK_journal_created_user" ("created_by_user_id"),
  KEY "FK_journal_updated_user" ("updated_by_user_id"),
  FULLTEXT KEY "journal_fulltext" ("title","abbreviation","url","issn","notes"),
  CONSTRAINT "FK_journal_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_journal_publisher" FOREIGN KEY ("publisher_id") REFERENCES "publisher" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_journal_status_kind" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_journal_updated_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "CC_journal_issn" CHECK ("issn" regexp '^[0-9]{4}-[0-9]{3}[0-9X]$')
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Definitive list of journal titles, abbreviations, etc.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.log
CREATE TABLE IF NOT EXISTS "log" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The log entry ID',
  "timestamp" datetime NOT NULL DEFAULT current_timestamp() COMMENT 'The date and time at which the log entry was made',
  "user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'The ID of the user who made the change',
  "transaction_kind" char(3) NOT NULL COMMENT 'The kind of change that was made',
  "entity_kind" char(3) NOT NULL COMMENT 'The kind of entity affected by the change',
  "entity_id" int(10) unsigned NOT NULL COMMENT 'The ID of the affected entity',
  "linked_entity_kind" char(3) DEFAULT NULL COMMENT 'The kind of entity that was linked/unlinked',
  "linked_entity_id" int(10) unsigned DEFAULT NULL COMMENT 'The ID of the entity that was linked/unlinked',
  PRIMARY KEY ("id"),
  KEY "log_entity" ("entity_kind","entity_id"),
  KEY "log_linked_entity" ("linked_entity_kind","linked_entity_id"),
  KEY "FK_log_transaction_kind" ("transaction_kind"),
  KEY "log_user" ("user_id") USING BTREE,
  CONSTRAINT "FK_log_entity_kind" FOREIGN KEY ("entity_kind") REFERENCES "entity_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_log_linked_entity_kind" FOREIGN KEY ("linked_entity_kind") REFERENCES "entity_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_log_transaction_kind" FOREIGN KEY ("transaction_kind") REFERENCES "transaction_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_log_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11254 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A log of all transactions';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.permission_kind
CREATE TABLE IF NOT EXISTS "permission_kind" (
  "code" char(3) NOT NULL COMMENT 'Unique permission code',
  "label" varchar(10) NOT NULL COMMENT 'Unique permission label',
  "description" varchar(50) NOT NULL COMMENT 'Description of the permission',
  PRIMARY KEY ("code"),
  UNIQUE KEY "permission_kind_label" ("label") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Access permissions that can be granted to users';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.person
CREATE TABLE IF NOT EXISTS "person" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique person identifier',
  "title" varchar(10) DEFAULT NULL COMMENT 'Person''s title, e.g., Prof., Dr.',
  "first_name" varchar(80) DEFAULT NULL COMMENT 'Person''s first names and/or initials',
  "nickname" varchar(40) DEFAULT NULL COMMENT 'Nickname by which commonly known',
  "prefix" varchar(20) DEFAULT NULL COMMENT 'Prefix to last name, e.g., van, de',
  "last_name" varchar(40) NOT NULL COMMENT 'Person''s last name,  without prefix or suffix',
  "suffix" varchar(16) DEFAULT NULL COMMENT 'Suffix to last name, e.g. Jr., Sr.',
  "alias" varchar(40) DEFAULT NULL COMMENT 'Alternative last name',
  "notes" text DEFAULT NULL COMMENT 'Brief biography, notes, etc.',
  "qualifications" text DEFAULT NULL COMMENT 'Academic qualifications',
  "country_code" char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for country of primary professional association',
  "rating" tinyint(4) NOT NULL DEFAULT 0 COMMENT 'Eminence star rating, 0..5',
  "checked" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Set when the person''s credentials have been checked',
  "published" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Set if person has published peer-reviewed papers on climate change',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  KEY "FK_person_status_kind" ("status") USING BTREE,
  KEY "person_title" ("title") USING BTREE,
  KEY "person_first_name" ("first_name") USING BTREE,
  KEY "person_last_name" ("last_name") USING BTREE,
  KEY "person_qualifications" ("qualifications") USING BTREE,
  KEY "person_rating" ("rating") USING BTREE,
  KEY "person_country" ("country_code") USING BTREE,
  KEY "person_notes" ("notes") USING BTREE,
  KEY "FK_person_created_user" ("created_by_user_id"),
  KEY "FK_person_updated_user" ("updated_by_user_id"),
  FULLTEXT KEY "person_fulltext" ("title","first_name","nickname","prefix","last_name","suffix","alias","notes","qualifications"),
  CONSTRAINT "FK_person_country" FOREIGN KEY ("country_code") REFERENCES "country" ("alpha_2") ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT "FK_person_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_person_status_kind" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_person_updated_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "CC_person_rating" CHECK ("rating" between 0 and 5)
) ENGINE=InnoDB AUTO_INCREMENT=4915 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='People who have publicly expressed contrarian/sceptical views about topic orthodoxy, whether by signing declarations, open letters or publishing science articles.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.publication
CREATE TABLE IF NOT EXISTS "publication" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique publication ID',
  "title" varchar(200) NOT NULL COMMENT 'Publication title',
  "authors" varchar(2000) NOT NULL COMMENT 'List of author names',
  "journal_id" int(10) unsigned DEFAULT NULL COMMENT 'Journal title',
  "kind" varchar(6) NOT NULL COMMENT 'The kind of publication',
  "date" date DEFAULT NULL COMMENT 'Publication date',
  "year" year(4) DEFAULT NULL COMMENT 'Publication year',
	"location" VARCHAR(50) DEFAULT NULL COMMENT 'The location of the relevant section within the publication',
  "abstract" text DEFAULT NULL COMMENT 'Abstract from the article',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the publication',
  "peer_reviewed" bit(1) DEFAULT NULL COMMENT 'Whether the article was peer-reviewed',
  "doi" varchar(255) DEFAULT NULL COMMENT 'Digital Object Identifier',
  "isbn" varchar(20) DEFAULT NULL COMMENT 'International Standard Book Number (printed publications only)',
  "url" varchar(200) DEFAULT NULL COMMENT 'URL of the publication',
  "cached" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  "accessed" date DEFAULT NULL COMMENT 'Date a web page was accessed',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "publication_doi" ("doi") USING BTREE,
	UNIQUE KEY "publication_isbn" ("isbn") USING BTREE,
  KEY "FK_publication_journal" ("journal_id"),
  KEY "FK_publication_publication_kind" ("kind"),
  KEY "FK_publication_status_kind" ("status") USING BTREE,
  KEY "FK_publication_created_user" ("created_by_user_id"),
  KEY "FK_publication_updated_user" ("updated_by_user_id"),
  FULLTEXT KEY "publication_fulltext" ("title","authors","abstract","notes","doi","isbn","url"),
  CONSTRAINT "FK_publication_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_publication_journal" FOREIGN KEY ("journal_id") REFERENCES "journal" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_publication_publication_kind" FOREIGN KEY ("kind") REFERENCES "publication_kind" ("kind") ON UPDATE CASCADE,
  CONSTRAINT "FK_publication_status_kind" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_publication_updated_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='References to published articles, peer-reviewed or otherwise.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.publication_kind
CREATE TABLE IF NOT EXISTS "publication_kind" (
  "kind" varchar(10) NOT NULL COMMENT 'The publication type per TY field in RIS specification',
  "label" varchar(25) NOT NULL COMMENT 'Label for the publication kind',
  PRIMARY KEY ("kind") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Publication kind per TY field in RIS specification';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.publication_person
CREATE TABLE IF NOT EXISTS "publication_person" (
  "publication_id" int(11) unsigned NOT NULL COMMENT 'The publication ID',
  "person_id" int(11) unsigned NOT NULL COMMENT 'The person ID',
  PRIMARY KEY ("publication_id","person_id") USING BTREE,
  KEY "FK_publication_person_publication" ("publication_id") USING BTREE,
  KEY "FK_publication_person_person" ("person_id") USING BTREE,
  CONSTRAINT "FK_publication_person_person" FOREIGN KEY ("person_id") REFERENCES "person" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_publication_person_publication" FOREIGN KEY ("publication_id") REFERENCES "publication" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links publications to persons.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.publisher
CREATE TABLE IF NOT EXISTS "publisher" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique publisher identifier',
  "name" varchar(200) NOT NULL COMMENT 'The publisher name',
  "location" varchar(50) DEFAULT NULL COMMENT 'The publisher location',
  "country_code" char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the publisher''s country',
  "url" varchar(200) DEFAULT NULL COMMENT 'URL of publisher''s home page',
  "journal_count" smallint(6) unsigned DEFAULT NULL COMMENT 'The number of journals published',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  KEY "FK_publisher_country" ("country_code") USING BTREE,
  KEY "publisher_name" ("name"),
  KEY "FK_publisher_status_kind" ("status"),
  KEY "FK_publisher_created_user" ("created_by_user_id"),
  KEY "FK_publisher_updated_user" ("updated_by_user_id"),
  FULLTEXT KEY "publisher_fulltext" ("name","location","url"),
  CONSTRAINT "FK_publisher_country" FOREIGN KEY ("country_code") REFERENCES "country" ("alpha_2") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "FK_publisher_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_publisher_status_kind" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_publisher_updated_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5940 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A list of book, journal, etc. publishers. The table can contain duplicate entries in the name column, reflecting the same publisher in different locations.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.quotation
CREATE TABLE IF NOT EXISTS "quotation" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique quotation identifier',
  "quotee" varchar(50) NOT NULL COMMENT 'The person(s) who made the quotation',
  "text" varchar(1000) NOT NULL COMMENT 'The quotation text',
  "date" date DEFAULT NULL COMMENT 'The quotation date',
  "source" varchar(200) DEFAULT NULL COMMENT 'The source of the quotation',
  "url" varchar(200) DEFAULT NULL COMMENT 'Web url to the quotation',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the quotation',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  KEY "FK_quotation_status" ("status") USING BTREE,
  KEY "quotation_quotee" ("quotee") USING BTREE,
  KEY "FK_quotation_created_user" ("created_by_user_id"),
  KEY "FK_quotation_updated_user" ("updated_by_user_id"),
  FULLTEXT KEY "quotation_fulltext" ("quotee","text","source","url","notes"),
  CONSTRAINT "FK_quotation_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_quotation_status" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_quotation_updated_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Quotations by leading sceptics and contrarians';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.quotation_person
CREATE TABLE IF NOT EXISTS "quotation_person" (
  "quotation_id" int(11) unsigned NOT NULL COMMENT 'The quotation ID',
  "person_id" int(11) unsigned NOT NULL COMMENT 'The person ID',
  PRIMARY KEY ("quotation_id","person_id") USING BTREE,
  KEY "FK_quotation_person_quotation" ("quotation_id") USING BTREE,
  KEY "FK_quotation_person_person" ("person_id") USING BTREE,
  CONSTRAINT "FK_quotation_person_person" FOREIGN KEY ("person_id") REFERENCES "person" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_quotation_person_quotation" FOREIGN KEY ("quotation_id") REFERENCES "quotation" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links quotations to persons.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.quotation_publication
CREATE TABLE IF NOT EXISTS "quotation_publication" (
  "quotation_id" int(11) unsigned NOT NULL COMMENT 'The quotation ID',
  "publication_id" int(11) unsigned NOT NULL COMMENT 'The publication ID',
  PRIMARY KEY ("quotation_id","publication_id") USING BTREE,
  KEY "FK_quotation_publication_quotation" ("quotation_id") USING BTREE,
  KEY "FK_quotation_publication_publication" ("publication_id") USING BTREE,
  CONSTRAINT "FK_quotation_publication_publication" FOREIGN KEY ("publication_id") REFERENCES "publication" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_quotation_publication_quotation" FOREIGN KEY ("quotation_id") REFERENCES "quotation" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links quotations to publications';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.status_kind
CREATE TABLE IF NOT EXISTS "status_kind" (
  "code" char(3) NOT NULL COMMENT 'The status code',
  "label" varchar(20) NOT NULL COMMENT 'The status label',
  "description" varchar(100) NOT NULL COMMENT 'Defines the meaning of the status code',
  PRIMARY KEY ("code"),
  UNIQUE KEY "status_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='The set of status values defining an entity''s lifecycle.';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.topic
CREATE TABLE IF NOT EXISTS "topic" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique topic identifier',
  "label" varchar(50) NOT NULL COMMENT 'The topic name/label',
  "description" varchar(500) DEFAULT NULL COMMENT 'Notes on when to use the topic',
  "parent_id" int(10) unsigned DEFAULT NULL COMMENT 'The parent topic ID',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  KEY "FK_topic_topic" ("parent_id"),
  KEY "FK_topic_created_user" ("created_by_user_id"),
  KEY "FK_topic_updated_user" ("updated_by_user_id"),
  KEY "FK_topic_status_kind" ("status") USING BTREE,
  FULLTEXT KEY "topic_fulltext" ("label","description","status"),
  CONSTRAINT "FK_topic_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_topic_status" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_topic_updated_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "fk_topic_topic" FOREIGN KEY ("parent_id") REFERENCES "topic" ("id") ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A topic in a hierarchy of such topics, with arbitrary breadth and depth';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.topic_claim_ref
CREATE TABLE IF NOT EXISTS "topic_claim_ref" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" int(10) unsigned NOT NULL COMMENT 'The associated topic ID',
  "claim_id" int(10) unsigned NOT NULL COMMENT 'The associated claim ID',
  "locations" varchar(2048) DEFAULT NULL COMMENT 'Location(s) within referenced claim (where applicable), one per line',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "topic_claim_ref_topic_claim" ("topic_id","claim_id"),
  KEY "FK_topic_claim_ref_topic" ("topic_id") USING BTREE,
  KEY "FK_topic_claim_ref_claim" ("claim_id") USING BTREE,
  FULLTEXT KEY "topic_claim_ref_fulltext" ("locations"),
  CONSTRAINT "FK_topic_claim_ref_claim" FOREIGN KEY ("claim_id") REFERENCES "claim" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_topic_claim_ref_topic" FOREIGN KEY ("topic_id") REFERENCES "topic" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links topics to claims';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.topic_declaration_ref
CREATE TABLE IF NOT EXISTS "topic_declaration_ref" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" int(10) unsigned NOT NULL COMMENT 'The associated topic ID',
  "declaration_id" int(10) unsigned NOT NULL COMMENT 'The associated declaration ID',
  "locations" varchar(2048) DEFAULT NULL COMMENT 'Location(s) within referenced declaration (where applicable)',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "topic_declaration_ref_topic_declaration" ("topic_id","declaration_id"),
  KEY "FK_topic_declaration_ref_topic" ("topic_id") USING BTREE,
  KEY "FK_topic_declaration_ref_declaration" ("declaration_id") USING BTREE,
  FULLTEXT KEY "topic_declaration_ref_fulltext" ("locations"),
  CONSTRAINT "FK_topic_declaration_ref_declaration" FOREIGN KEY ("declaration_id") REFERENCES "declaration" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_topic_declaration_ref_topic" FOREIGN KEY ("topic_id") REFERENCES "topic" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links topics to declarations';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.topic_person_ref
CREATE TABLE IF NOT EXISTS "topic_person_ref" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" int(10) unsigned NOT NULL COMMENT 'The associated topic ID',
  "person_id" int(10) unsigned NOT NULL COMMENT 'The associated person ID',
  "locations" varchar(2048) DEFAULT NULL COMMENT 'Location(s) within referenced person (where applicable)',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "topic_person_ref_topic_person" ("topic_id","person_id") USING BTREE,
  KEY "FK_topic_person_ref_topic" ("topic_id") USING BTREE,
  KEY "FK_topic_person_ref_person" ("person_id") USING BTREE,
  FULLTEXT KEY "topic_person_ref_fulltext" ("locations"),
  CONSTRAINT "FK_topic_person_ref_person" FOREIGN KEY ("person_id") REFERENCES "person" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_topic_person_ref_topic" FOREIGN KEY ("topic_id") REFERENCES "topic" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links topics to persons';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.topic_publication_ref
CREATE TABLE IF NOT EXISTS "topic_publication_ref" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" int(10) unsigned NOT NULL COMMENT 'The associated topic ID',
  "publication_id" int(10) unsigned NOT NULL COMMENT 'The associated publication ID',
  "locations" varchar(2048) DEFAULT NULL COMMENT 'Location(s) within referenced publication (where applicable)',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "topic_publication_ref_topic_publication" ("topic_id","publication_id"),
  KEY "FK_topic_publication_ref_topic" ("topic_id") USING BTREE,
  KEY "FK_topic_publication_ref_publication" ("publication_id") USING BTREE,
  FULLTEXT KEY "topic_publication_ref_fulltext" ("locations"),
  CONSTRAINT "FK_topic_publication_ref_publication" FOREIGN KEY ("publication_id") REFERENCES "publication" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_topic_publication_ref_topic" FOREIGN KEY ("topic_id") REFERENCES "topic" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links topics to publications';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.topic_quotation_ref
CREATE TABLE IF NOT EXISTS "topic_quotation_ref" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" int(10) unsigned NOT NULL COMMENT 'The associated topic ID',
  "quotation_id" int(10) unsigned NOT NULL COMMENT 'The associated quotation ID',
  "locations" varchar(2048) DEFAULT NULL COMMENT 'Location(s) within referenced quotation (where applicable)',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "topic_quotation_ref_topic_quotation" ("topic_id","quotation_id"),
  KEY "FK_topic_quotation_ref_topic" ("topic_id") USING BTREE,
  KEY "FK_topic_quotation_ref_quotation" ("quotation_id") USING BTREE,
  CONSTRAINT "FK_topic_quotation_ref_quotation" FOREIGN KEY ("quotation_id") REFERENCES "quotation" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_topic_quotation_ref_topic" FOREIGN KEY ("topic_id") REFERENCES "topic" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Association table that links topics to quotations';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.transaction_kind
CREATE TABLE IF NOT EXISTS "transaction_kind" (
  "code" char(3) NOT NULL COMMENT 'The transaction code',
  "label" varchar(20) NOT NULL COMMENT 'A UI label for the transaction kind',
  "description" varchar(50) NOT NULL COMMENT 'Description of the transaction kind',
  PRIMARY KEY ("code"),
  UNIQUE KEY "transaction_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Enumerates the possible transaction kinds';

-- Data exporting was unselected.

-- Dumping structure for table evidence_engine.user
CREATE TABLE IF NOT EXISTS "user" (
  "id" int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique system-assigned user identifier',
  "username" varchar(50) NOT NULL COMMENT 'The unique user-assigned user name',
  "password" varchar(500) NOT NULL COMMENT 'Hash of the user''s password',
	"enabled" BIT(1) NOT NULL COMMENT 'Whether the user account is enabled',
  "first_name" varchar(50) NULL COMMENT 'The user''s first name',
  "last_name" varchar(50) NULL COMMENT 'The user''s last name',
  "email" varchar(100) DEFAULT NULL COMMENT 'The user''s email address, used for sign-in',
  "country_code" char(2) DEFAULT NULL COMMENT 'ISO-3166-1 alpha-2 code for user''s country of residence',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the user',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" int(10) unsigned NOT NULL DEFAULT 0 COMMENT 'ID of the user who created the record',
  "updated" timestamp DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" int(10) unsigned DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  UNIQUE KEY ("username"),
  KEY "FK_user_country" ("country_code"),
  KEY "FK_user_updated_user" ("updated_by_user_id"),
  KEY "FK_user_status" ("status"),
  KEY "FK_user_created_user" ("created_by_user_id"),
  FULLTEXT KEY "user_fulltext" ("username","first_name","last_name","email","notes"),
  CONSTRAINT "FK_user_country" FOREIGN KEY ("country_code") REFERENCES "country" ("alpha_2") ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT "FK_user_created_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_user_status" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds details of authenticatable users';

-- Data exporting was unselected.

-- Additional tables required by Spring Security
CREATE TABLE IF NOT EXISTS "user_authority" (
	"username" VARCHAR(50) NOT NULL COMMENT 'The login user name',
	"authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
	UNIQUE KEY "user_authority" ("username", "authority"),
	CONSTRAINT "FK_user_authority_user" FOREIGN KEY ("username") REFERENCES "user"("username") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_user_authority_permission" FOREIGN KEY ("authority") REFERENCES "permission_kind" ("code") ON UPDATE CASCADE
) COMMENT='Holds authorities granted to users';

CREATE TABLE IF NOT EXISTS "group" (
	"id" int(10) unsigned AUTO_INCREMENT NOT NULL COMMENT 'The unique system-assigned group identifier',
	"group_name" VARCHAR(50) NOT NULL COMMENT 'The group name',
	PRIMARY KEY ("id")
) COMMENT='Holds groups to which users can belong';

CREATE TABLE IF NOT EXISTS "group_authority" (
	"group_id" int(10) unsigned NOT NULL COMMENT 'ID of a group',
	"authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
	UNIQUE KEY "group_authority" ("group_id", "authority"),
	CONSTRAINT "FK_group_authority_group" FOREIGN KEY ("group_id") REFERENCES "group"("id"),
  CONSTRAINT "FK_group_authority_permission" FOREIGN KEY ("authority") REFERENCES "permission_kind" ("code") ON UPDATE CASCADE
) COMMENT='Holds authorities granted to groups';

CREATE TABLE IF NOT EXISTS "group_user" (
	"id" int(10) unsigned AUTO_INCREMENT NOT NULL COMMENT 'The unique system-assigned identifier',
	"username" VARCHAR(50) NOT NULL COMMENT 'The login user name',
	"group_id" int(10) unsigned NOT NULL COMMENT 'ID of the group to which user belongs',
	PRIMARY KEY ("id"),
  UNIQUE KEY "group_user" ("username", "group_id"),
	CONSTRAINT "FK_group_user_group" FOREIGN KEY ("group_id") REFERENCES "group"("id")
) COMMENT='Defines group membership';

CREATE TABLE "persistent_login" (
    "series" VARCHAR(64) COMMENT 'Encoded random number used to detect cookie stealing',
    "username" VARCHAR(64) NOT NULL COMMENT 'The authenticated username',
    "token" VARCHAR(64) NOT NULL COMMENT 'The authentication token returned as a cookie',
    "last_used" TIMESTAMP NOT NULL COMMENT 'The date/time at which the token was last used',
    PRIMARY KEY ("series")
);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
