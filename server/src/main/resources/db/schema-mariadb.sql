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
SET SQL_MODE = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION,ANSI,NO_AUTO_VALUE_ON_ZERO';

-- Dumping structure for table evidence_engine.entity
CREATE TABLE IF NOT EXISTS "entity" (
  "id" bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique entity record identifier',
  "dtype" char(3) NOT NULL COMMENT 'The entity type discriminator',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "created" timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id"  bigint(20) unsigned COMMENT 'The ID of the user who created the record',
  "updated" timestamp NULL DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id"  bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  KEY "FK_entity_dtype" ("dtype"),
  KEY "FK_entity_status" ("status"),
  KEY "FK_entity_created_by_user" ("created_by_user_id"),
  KEY "FK_entity_updated_by_user" ("updated_by_user_id"),
  CONSTRAINT "FK_entity_dtype" FOREIGN KEY ("dtype") REFERENCES "entity_kind" ("code") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_entity_status" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_entity_created_by_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_entity_updated_by_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Base table for all tracked and linkable entities';

-- Dumping structure for table evidence_engine.claim
CREATE TABLE IF NOT EXISTS "claim" (
  "id" bigint(20) unsigned NOT NULL COMMENT 'The unique claim identifier',
  "date" date DEFAULT NULL COMMENT 'The date on which the claim was first made',
  "text" varchar(500) NOT NULL COMMENT 'The claim text',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the claim',
  PRIMARY KEY ("id"),
  FULLTEXT KEY "claim_fulltext" ("text","notes"),
  CONSTRAINT "FK_claim_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Claims made in respect of an associated topic';

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

-- Dumping structure for table evidence_engine.declaration
CREATE TABLE IF NOT EXISTS "declaration" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'The unique declaration identifier',
  "kind" varchar(4) NOT NULL COMMENT 'The kind of declaration',
  "date" date NOT NULL COMMENT 'The date the declaration was first published',
  "title" varchar(100) NOT NULL COMMENT 'The declaration name or title',
  "country_code" char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the country to which the declaration pertains',
  "url" varchar(200) DEFAULT NULL COMMENT 'Web URL of the original declaration',
  "cached" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  "signatories" text DEFAULT NULL COMMENT 'The list of signatories, one per line',
  "signatory_count" smallint(6) DEFAULT NULL COMMENT 'The number of signatories',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the declaration',
  PRIMARY KEY ("id"),
  KEY "FK_declaration_declaration_kind" ("kind"),
  KEY "FK_declaration_country" ("country_code"),
  FULLTEXT KEY "declaration_fulltext" ("title","signatories","notes"),
  CONSTRAINT "FK_declaration_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_declaration_country" FOREIGN KEY ("country_code") REFERENCES "country" ("alpha_2") ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT "FK_declaration_declaration_kind" FOREIGN KEY ("kind") REFERENCES "declaration_kind" ("kind") ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Details of public declarations and open letters expressing climate scepticism';

-- Dumping structure for table evidence_engine.declaration_kind
CREATE TABLE IF NOT EXISTS "declaration_kind" (
  "kind" varchar(4) NOT NULL COMMENT 'The declaration kind code',
  "label" varchar(20) NOT NULL COMMENT 'Label for the declaration kind',
  "description" varchar(50) NOT NULL COMMENT 'Description of the declaration kind',
  PRIMARY KEY ("kind"),
  UNIQUE KEY "declaration_kind_label" ("label") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A lookup table for validating declaration records';

-- Dumping structure for table evidence_engine.entity_kind
CREATE TABLE IF NOT EXISTS "entity_kind" (
  "code" char(3) NOT NULL COMMENT 'Unique code for the entity kind',
  "label" varchar(20) NOT NULL COMMENT 'Label for the entity kind',
  PRIMARY KEY ("code"),
  UNIQUE KEY "entity_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Lookup table enumerating supported entity types';

-- Dumping structure for table evidence_engine.entity_link
CREATE TABLE IF NOT EXISTS "entity_link" (
  "id" bigint(20) unsigned NOT NULL COMMENT 'The unique entity link identifier',
  "from_entity_id" bigint(20) unsigned NOT NULL COMMENT 'The linked-from entity ID',
  "to_entity_id" bigint(20) unsigned NOT NULL COMMENT 'The linked-to entity ID',
  "from_entity_locations" varchar(500) NULL DEFAULT NULL COMMENT 'Location(s) within the linked-from entity (where applicable), one per line',
  "to_entity_locations" varchar(500) NULL DEFAULT NULL COMMENT 'Location(s) within the linked-to entity (where applicable), one per line',
  PRIMARY KEY ("id"),
  FULLTEXT KEY "entity_fulltext" ("from_entity_locations","to_entity_locations"),
  CONSTRAINT "FK_entity_link_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_entity_link_from_entity" FOREIGN KEY ("from_entity_id") REFERENCES "entity" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "FK_entity_link_to_entity" FOREIGN KEY ("to_entity_id") REFERENCES "entity" ("id") ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Self-association table to hold links between linkable entities';

-- Dumping structure for table evidence_engine.journal
CREATE TABLE IF NOT EXISTS "journal" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'The journal ID',
  "title" varchar(100) NOT NULL COMMENT 'The journal, etc. title',
  "abbreviation" varchar(50) DEFAULT NULL COMMENT 'The ISO 4 title abbreviation',
  "url" varchar(200) DEFAULT NULL COMMENT 'Web link to the journal''s home page',
  "issn" char(9) DEFAULT NULL COMMENT 'The International Standard Serial Number',
  "publisher_id"  bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the publisher',
  "notes" varchar(200) DEFAULT NULL COMMENT 'A brief description of the journal',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "journal_issn" ("issn") USING BTREE,
  KEY "FK_journal_publisher" ("publisher_id"),
  KEY "journal_title" ("title") USING BTREE,
  KEY "journal_abbreviation" ("abbreviation") USING BTREE,
  FULLTEXT KEY "journal_fulltext" ("title","abbreviation","url","issn","notes"),
  CONSTRAINT "FK_journal_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_journal_publisher" FOREIGN KEY ("publisher_id") REFERENCES "publisher" ("id") ON UPDATE CASCADE,
  CONSTRAINT "CC_journal_issn" CHECK ("issn" regexp '^[0-9]{4}-[0-9]{3}[0-9X]$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Definitive list of journal titles, abbreviations, etc.';

-- Dumping structure for table evidence_engine.log
CREATE TABLE IF NOT EXISTS "log" (
  "id"  bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The log entry ID',
  "timestamp" datetime NOT NULL DEFAULT current_timestamp() COMMENT 'The date and time at which the log entry was made',
  "user_id"  bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT 'The ID of the user who made the change',
  "transaction_kind" char(3) NOT NULL COMMENT 'The kind of change that was made',
  "entity_kind" char(3) NOT NULL COMMENT 'The kind of entity affected by the change',
  "entity_id"  bigint(20) unsigned NOT NULL COMMENT 'The ID of the affected entity',
  "linked_entity_kind" char(3) DEFAULT NULL COMMENT 'The kind of entity that was linked/unlinked',
  "linked_entity_id"  bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the entity that was linked/unlinked',
  PRIMARY KEY ("id"),
  KEY "log_entity" ("entity_kind","entity_id"),
  KEY "log_linked_entity" ("linked_entity_kind","linked_entity_id"),
  KEY "FK_log_transaction_kind" ("transaction_kind"),
  KEY "log_user" ("user_id") USING BTREE,
  CONSTRAINT "FK_log_transaction_kind" FOREIGN KEY ("transaction_kind") REFERENCES "transaction_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_log_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_log_entity_id" FOREIGN KEY ("entity_id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_log_entity_kind" FOREIGN KEY ("entity_kind") REFERENCES "entity_kind" ("code") ON UPDATE CASCADE,
  CONSTRAINT "FK_log_linked_entity_id" FOREIGN KEY ("linked_entity_id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_log_linked_entity_kind" FOREIGN KEY ("linked_entity_kind") REFERENCES "entity_kind" ("code") ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A log of all transactions';

-- Dumping structure for table evidence_engine.permission_kind
CREATE TABLE IF NOT EXISTS "permission_kind" (
  "code" char(3) NOT NULL COMMENT 'Unique permission code',
  "label" varchar(10) NOT NULL COMMENT 'Unique permission label',
  "description" varchar(50) NOT NULL COMMENT 'Description of the permission',
  PRIMARY KEY ("code"),
  UNIQUE KEY "permission_kind_label" ("label") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Access permissions that can be granted to users';

-- Dumping structure for table evidence_engine.person
CREATE TABLE IF NOT EXISTS "person" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'Unique person identifier',
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
  PRIMARY KEY ("id"),
  KEY "person_title" ("title") USING BTREE,
  KEY "person_first_name" ("first_name") USING BTREE,
  KEY "person_last_name" ("last_name") USING BTREE,
  KEY "person_qualifications" ("qualifications") USING BTREE,
  KEY "person_rating" ("rating") USING BTREE,
  KEY "person_country" ("country_code") USING BTREE,
  KEY "person_notes" ("notes") USING BTREE,
  FULLTEXT KEY "person_fulltext" ("title","first_name","nickname","prefix","last_name","suffix","alias","notes","qualifications"),
  CONSTRAINT "FK_person_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_person_country" FOREIGN KEY ("country_code") REFERENCES "country" ("alpha_2") ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT "CC_person_rating" CHECK ("rating" between 0 and 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='People who have publicly expressed contrarian/sceptical views about topic orthodoxy, whether by signing declarations, open letters or publishing science articles.';

-- Dumping structure for table evidence_engine.publication
CREATE TABLE IF NOT EXISTS "publication" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'Unique publication ID',
  "title" varchar(200) NOT NULL COMMENT 'Publication title',
  "authors" varchar(2000) NOT NULL COMMENT 'List of author names',
  "journal_id"  bigint(20) unsigned DEFAULT NULL COMMENT 'Journal title',
  "kind" varchar(6) NOT NULL COMMENT 'The kind of publication',
  "date" date DEFAULT NULL COMMENT 'Publication date',
  "year" year(4) DEFAULT NULL COMMENT 'Publication year',
	"location" VARCHAR(50) DEFAULT NULL COMMENT 'The location of the relevant section within the publication',
  "abstract" text DEFAULT NULL COMMENT 'Abstract from the article',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the publication',
  "peer_reviewed" bit(1) DEFAULT DEFAULT b'0' COMMENT 'Whether the article was peer-reviewed',
  "doi" varchar(255) DEFAULT NULL COMMENT 'Digital Object Identifier',
  "isbn" varchar(20) DEFAULT NULL COMMENT 'International Standard Book Number (printed publications only)',
  "url" varchar(200) DEFAULT NULL COMMENT 'URL of the publication',
  "cached" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  "accessed" date DEFAULT NULL COMMENT 'Date a web page was accessed',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "publication_doi" ("doi") USING BTREE,
	UNIQUE KEY "publication_isbn" ("isbn") USING BTREE,
  KEY "FK_publication_journal" ("journal_id"),
  KEY "FK_publication_publication_kind" ("kind"),
  FULLTEXT KEY "publication_fulltext" ("title","authors","abstract","notes","doi","isbn","url"),
  CONSTRAINT "FK_publication_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_publication_journal" FOREIGN KEY ("journal_id") REFERENCES "journal" ("id") ON UPDATE CASCADE,
  CONSTRAINT "FK_publication_publication_kind" FOREIGN KEY ("kind") REFERENCES "publication_kind" ("kind") ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='References to published articles, peer-reviewed or otherwise.';

-- Dumping structure for table evidence_engine.publication_kind
CREATE TABLE IF NOT EXISTS "publication_kind" (
  "kind" varchar(10) NOT NULL COMMENT 'The publication type per TY field in RIS specification',
  "label" varchar(25) NOT NULL COMMENT 'Label for the publication kind',
  PRIMARY KEY ("kind") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Publication kind per TY field in RIS specification';

-- Dumping structure for table evidence_engine.publisher
CREATE TABLE IF NOT EXISTS "publisher" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'The unique publisher identifier',
  "name" varchar(200) NOT NULL COMMENT 'The publisher name',
  "location" varchar(50) DEFAULT NULL COMMENT 'The publisher location',
  "country_code" char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the publisher''s country',
  "url" varchar(200) DEFAULT NULL COMMENT 'URL of publisher''s home page',
  "journal_count" smallint(6) unsigned DEFAULT NULL COMMENT 'The number of journals published',
  PRIMARY KEY ("id"),
  KEY "FK_publisher_country" ("country_code") USING BTREE,
  KEY "publisher_name" ("name"),
  FULLTEXT KEY "publisher_fulltext" ("name","location","url"),
  CONSTRAINT "FK_publisher_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_publisher_country" FOREIGN KEY ("country_code") REFERENCES "country" ("alpha_2") ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A list of book, journal, etc. publishers. The table can contain duplicate entries in the name column, reflecting the same publisher in different locations.';

-- Dumping structure for table evidence_engine.quotation
CREATE TABLE IF NOT EXISTS "quotation" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'Unique quotation identifier',
  "quotee" varchar(50) NOT NULL COMMENT 'The person(s) who made the quotation',
  "text" varchar(1000) NOT NULL COMMENT 'The quotation text',
  "date" date DEFAULT NULL COMMENT 'The quotation date',
  "source" varchar(200) DEFAULT NULL COMMENT 'The source of the quotation',
  "url" varchar(200) DEFAULT NULL COMMENT 'Web url to the quotation',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the quotation',
  PRIMARY KEY ("id"),
  KEY "quotation_quotee" ("quotee") USING BTREE,
  FULLTEXT KEY "quotation_fulltext" ("quotee","text","source","url","notes"),
  CONSTRAINT "FK_quotation_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Quotations by leading sceptics and contrarians';

-- Dumping structure for table evidence_engine.status_kind
CREATE TABLE IF NOT EXISTS "status_kind" (
  "code" char(3) NOT NULL COMMENT 'The status code',
  "label" varchar(20) NOT NULL COMMENT 'The status label',
  "description" varchar(100) NOT NULL COMMENT 'Defines the meaning of the status code',
  PRIMARY KEY ("code"),
  UNIQUE KEY "status_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='The set of status values defining an entity''s lifecycle.';

-- Dumping structure for table evidence_engine.topic
CREATE TABLE IF NOT EXISTS "topic" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'The unique topic identifier',
  "label" varchar(50) NOT NULL COMMENT 'The topic name/label',
  "description" varchar(500) DEFAULT NULL COMMENT 'Notes on when to use the topic',
  "parent_id"  bigint(20) unsigned DEFAULT NULL COMMENT 'The parent topic ID',
  PRIMARY KEY ("id"),
  KEY "FK_topic_topic" ("parent_id"),
  FULLTEXT KEY "topic_fulltext" ("label","description"),
  CONSTRAINT "FK_topic_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "fk_topic_topic" FOREIGN KEY ("parent_id") REFERENCES "topic" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A topic in a hierarchy of such topics, with arbitrary breadth and depth';

-- Dumping structure for table evidence_engine.transaction_kind
CREATE TABLE IF NOT EXISTS "transaction_kind" (
  "code" char(3) NOT NULL COMMENT 'The transaction code',
  "label" varchar(20) NOT NULL COMMENT 'A UI label for the transaction kind',
  "description" varchar(50) NOT NULL COMMENT 'Description of the transaction kind',
  PRIMARY KEY ("code"),
  UNIQUE KEY "transaction_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Enumerates the possible transaction kinds';

-- Dumping structure for table evidence_engine.user
CREATE TABLE IF NOT EXISTS "user" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'The unique system-assigned user identifier',
  "username" varchar(50) NOT NULL COMMENT 'The unique user-assigned user name',
  "password" varchar(500) NOT NULL COMMENT 'Hash of the user''s password',
	"enabled" bit(1) NOT NULL DEFAULT b'1' COMMENT 'Whether the user account is enabled',
  "first_name" varchar(50) NULL COMMENT 'The user''s first name',
  "last_name" varchar(50) NULL COMMENT 'The user''s last name',
  "email" varchar(100) DEFAULT NULL COMMENT 'The user''s email address, used for sign-in',
  "country_code" char(2) DEFAULT NULL COMMENT 'ISO-3166-1 alpha-2 code for user''s country of residence',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the user',
  PRIMARY KEY ("id"),
  UNIQUE KEY ("username"),
  KEY "FK_user_country" ("country_code"),
  FULLTEXT KEY "user_fulltext" ("username","first_name","last_name","email","notes"),
  CONSTRAINT "FK_user_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_user_country" FOREIGN KEY ("country_code") REFERENCES "country" ("alpha_2") ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds details of authenticatable users';

-- Additional tables required by Spring Security
CREATE TABLE IF NOT EXISTS "user_authority" (
	"username" VARCHAR(50) NOT NULL COMMENT 'The login user name',
	"authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
	UNIQUE KEY "user_authority" ("username", "authority"),
	CONSTRAINT "FK_user_authority_user" FOREIGN KEY ("username") REFERENCES "user"("username") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_user_authority_permission" FOREIGN KEY ("authority") REFERENCES "permission_kind" ("code") ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT='Holds authorities granted to users';

CREATE TABLE IF NOT EXISTS "group" (
	"id"  bigint(20) unsigned AUTO_INCREMENT NOT NULL COMMENT 'The unique system-assigned group identifier',
	"group_name" VARCHAR(50) NOT NULL COMMENT 'The group name',
	PRIMARY KEY ("id"),
  UNIQUE KEY "group_group_name" ("group_name")
) COMMENT='Holds groups to which users can belong';

CREATE TABLE IF NOT EXISTS "group_authority" (
	"group_id"  bigint(20) unsigned NOT NULL COMMENT 'ID of a group',
	"authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
	UNIQUE KEY "group_authority" ("group_id", "authority"),
	CONSTRAINT "FK_group_authority_group" FOREIGN KEY ("group_id") REFERENCES "group" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_group_authority_permission" FOREIGN KEY ("authority") REFERENCES "permission_kind" ("code") ON UPDATE CASCADE
) COMMENT='Holds authorities granted to groups';

CREATE TABLE IF NOT EXISTS "group_user" (
	"id"  bigint(20) unsigned AUTO_INCREMENT NOT NULL COMMENT 'The unique system-assigned identifier',
	"group_id"  bigint(20) unsigned NOT NULL COMMENT 'ID of the group to which user belongs',
	"username" VARCHAR(50) NOT NULL COMMENT 'The login user name',
	PRIMARY KEY ("id"),
  UNIQUE KEY "group_user" ("username", "group_id"),
	CONSTRAINT "FK_group_user_group" FOREIGN KEY ("group_id") REFERENCES "group" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_group_user_user" FOREIGN KEY ("username") REFERENCES "user" ("username") ON UPDATE CASCADE ON DELETE CASCADE
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
