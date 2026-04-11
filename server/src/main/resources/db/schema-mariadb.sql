-- ----------------------------------------------------------------------------------------------------------------------
-- Evidence Engine: A system for managing evidence on arbitrary scientific topics.
-- Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
-- Copyright © 2024-26 Adrian Price. All rights reserved.
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

CREATE TABLE IF NOT EXISTS "abbreviation" (
  "word" VARCHAR(50) NOT NULL COMMENT 'The title word, prefix or suffix',
  "is_prefix" BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Whether "word" is a prefix',
  "is_suffix" BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Whether "word" is a suffix',
  "abbreviation" VARCHAR(30) DEFAULT NULL COMMENT 'The abbreviation, if any, for "word"',
  "languages" VARCHAR(50) NOT NULL COMMENT 'The applicable languages',
  PRIMARY KEY ("word"),
  INDEX "abbreviation_abbreviation" ("abbreviation") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='The ISSN Network''s ''List of Title Word Abbreviations'' (LTWA), as used by ISO 4.';

CREATE TABLE IF NOT EXISTS "config" (
    "property" VARCHAR(50) NOT NULL COMMENT 'The property name',
    "subscript" TINYINT(4) NOT NULL DEFAULT '0' COMMENT 'For a multi-valued property, the unique item index',
    "value" VARCHAR(255) NULL DEFAULT NULL COMMENT 'The property value as a string',
    PRIMARY KEY ("property", "subscript")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A general purpose lookup table for persisting dynamic configuration properties';

-- Dumping structure for table evidence_engine.entity
CREATE TABLE IF NOT EXISTS "entity" (
  "id" BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The unique entity record identifier',
  "dtype" CHAR(3) NOT NULL COMMENT 'The entity type discriminator',
  "status" CHAR(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "rating" TINYINT(3) UNSIGNED DEFAULT NULL COMMENT 'Quality/significance/eminence star rating, 1..5',
  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp() COMMENT 'When the record was created',
  "created_by_user_id" BIGINT(20) UNSIGNED COMMENT 'The ID of the user who created the record',
  "updated" TIMESTAMP NULL DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT(20) UNSIGNED DEFAULT NULL COMMENT 'The ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  KEY "FK_entity_dtype" ("dtype"),
  KEY "FK_entity_status" ("status"),
  KEY "FK_entity_created_by_user" ("created_by_user_id"),
  KEY "FK_entity_updated_by_user" ("updated_by_user_id"),
  CONSTRAINT "FK_entity_dtype" FOREIGN KEY ("dtype") REFERENCES "entity_kind" ("code") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_entity_status" FOREIGN KEY ("status") REFERENCES "status_kind" ("code") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_entity_created_by_user" FOREIGN KEY ("created_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_entity_updated_by_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "CC_rating" CHECK ("rating" BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Base table for all tracked and linkable entities';

-- Dumping structure for table evidence_engine.claim
CREATE TABLE IF NOT EXISTS "claim" (
  "id" BIGINT(20) UNSIGNED NOT NULL COMMENT 'The unique claim identifier',
  "date" DATE DEFAULT NULL COMMENT 'The date on which the claim was first made',
  "text" VARCHAR(500) NOT NULL COMMENT 'The claim text',
  "notes" TEXT DEFAULT NULL COMMENT 'Added notes about the claim',
  PRIMARY KEY ("id"),
  FULLTEXT KEY "claim_fulltext" ("text","notes"),
  CONSTRAINT "FK_claim_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Claims made in respect of an associated topic';

-- Dumping structure for table evidence_engine.comment
CREATE TABLE IF NOT EXISTS "comment" (
  "id" BIGINT(20) UNSIGNED NOT NULL COMMENT 'The unique comment identifier',
  "target_id" BIGINT(20) UNSIGNED NOT NULL COMMENT 'The ID of the target entity with which the comment is associated',
  "parent_id" BIGINT(20) UNSIGNED DEFAULT NULL COMMENT 'The ID of the parent comment to which this comment is a reply.',
  "text" VARCHAR(500) NOT NULL COMMENT 'The text of the comment',
  PRIMARY KEY ("id"),
  FULLTEXT KEY "comment_fulltext" ("text"),
  CONSTRAINT "FK_comment_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_comment_target" FOREIGN KEY ("target_id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_comment_parent" FOREIGN KEY ("parent_id") REFERENCES "comment" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Comments associated with a tracked entity';

-- Dumping structure for table evidence_engine.country
CREATE TABLE IF NOT EXISTS "country" (
  "alpha_2" CHAR(2) NOT NULL COMMENT 'ISO-3166-1 alpha-2 code',
  "alpha_3" CHAR(3) NOT NULL COMMENT 'ISO-3166-1 alpha-3 code',
  "numeric" CHAR(3) NOT NULL COMMENT 'ISO-3166-1 numeric code',
  "iso_name" VARCHAR(100) NOT NULL COMMENT 'Official/ISO country name',
  "common_name" VARCHAR(50) NOT NULL COMMENT 'Common or short name',
  "year" year(4) NOT NULL COMMENT 'Year alpha-2 code was first assigned',
  "cc_tld" VARCHAR(6) NOT NULL COMMENT 'Country code top level domain',
  "notes" TEXT DEFAULT NULL COMMENT 'Remarks as per Wikipedia ISO-3166-1 entry',
  PRIMARY KEY ("alpha_2"),
  UNIQUE KEY "country_alpha_3" ("alpha_3") USING BTREE,
  UNIQUE KEY "country_numeric" ("numeric") USING BTREE,
  UNIQUE KEY "country_common_name" ("common_name") USING BTREE,
  UNIQUE KEY "country_iso_name" ("iso_name") USING BTREE,
  CONSTRAINT "CC_country_numeric" CHECK ("numeric" regexp '^\\d{3}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Lookup table for converting ISO-3166-1 country codes to country name';

CREATE VIEW IF NOT EXISTS "comment_entity_vw" AS
SELECT 
  c."id",
  ce."status",
  te."dtype" AS "target_entity_kind",
  c."target_id" AS "target_entity_id",
  c."text"
FROM "comment" c
JOIN "entity" ce
ON ce."id" = c."id"
JOIN "entity" te
ON te."id" = c."target_id";

-- Dumping structure for table evidence_engine.declaration
CREATE TABLE IF NOT EXISTS "declaration" (
  "id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'The unique declaration identifier',
  "kind" VARCHAR(4) NOT NULL COMMENT 'The kind of declaration',
  "date" DATE NOT NULL COMMENT 'The date the declaration was first published',
  "title" VARCHAR(100) NOT NULL COMMENT 'The declaration name or title',
  "country" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the country to which the declaration pertains',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web URL of the original declaration',
  "cached" BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  "signatories" TEXT DEFAULT NULL COMMENT 'The list of signatories, one per line',
  "signatory_count" SMALLINT(6) DEFAULT NULL COMMENT 'The number of signatories',
  "notes" TEXT DEFAULT NULL COMMENT 'Added notes about the declaration',
  PRIMARY KEY ("id"),
  KEY "FK_declaration_declaration_kind" ("kind"),
  KEY "FK_declaration_country" ("country"),
  FULLTEXT KEY "declaration_fulltext" ("title","signatories","notes"),
  CONSTRAINT "FK_declaration_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_declaration_country" FOREIGN KEY ("country") REFERENCES "country" ("alpha_2") ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT "FK_declaration_declaration_kind" FOREIGN KEY ("kind") REFERENCES "declaration_kind" ("kind") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Details of public declarations and open letters expressing climate scepticism';

-- Dumping structure for table evidence_engine.declaration_kind
CREATE TABLE IF NOT EXISTS "declaration_kind" (
  "kind" VARCHAR(4) NOT NULL COMMENT 'The declaration kind code',
  "label" VARCHAR(20) NOT NULL COMMENT 'Label for the declaration kind',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the declaration kind',
  PRIMARY KEY ("kind"),
  UNIQUE KEY "declaration_kind_label" ("label") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A lookup table for validating declaration records';

-- Dumping structure for table evidence_engine.entity_kind
CREATE TABLE IF NOT EXISTS "entity_kind" (
  "code" CHAR(3) NOT NULL COMMENT 'Unique code for the entity kind',
  "label" VARCHAR(20) NOT NULL COMMENT 'Label for the entity kind',
  PRIMARY KEY ("code"),
  UNIQUE KEY "entity_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Lookup table enumerating supported entity types';

-- Dumping structure for table evidence_engine.entity_link
CREATE TABLE IF NOT EXISTS "entity_link" (
  "id" BIGINT(20) UNSIGNED NOT NULL COMMENT 'The unique entity link identifier',
  "from_entity_id" BIGINT(20) UNSIGNED NOT NULL COMMENT 'The linked-from entity ID',
  "to_entity_id" BIGINT(20) UNSIGNED NOT NULL COMMENT 'The linked-to entity ID',
  "from_entity_locations" VARCHAR(500) NULL DEFAULT NULL COMMENT 'Location(s) within the linked-from entity (where applicable), one per line',
  "to_entity_locations" VARCHAR(500) NULL DEFAULT NULL COMMENT 'Location(s) within the linked-to entity (where applicable), one per line',
  PRIMARY KEY ("id"),
  UNIQUE INDEX `entity_link_unique` (`from_entity_id`, `to_entity_id`),
  FULLTEXT KEY "entity_fulltext" ("from_entity_locations","to_entity_locations"),
  CONSTRAINT "FK_entity_link_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_entity_link_from_entity" FOREIGN KEY ("from_entity_id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_entity_link_to_entity" FOREIGN KEY ("to_entity_id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Self-association table to hold links between linkable entities';

CREATE VIEW IF NOT EXISTS "entity_link_entity_vw" AS
SELECT 
  el."id",
  ele."status",
  fe."dtype" AS "from_entity_kind",
  el."from_entity_id",
  el."from_entity_locations",
  te."dtype" AS "to_entity_kind",
  el."to_entity_id",
  el."to_entity_locations"
FROM "entity_link" el
JOIN "entity" ele
ON ele."id" = el."id"
JOIN "entity" fe
ON fe.id = el."from_entity_id"
JOIN "entity" te
ON te.id = el."to_entity_id";

-- Dumping structure for table evidence_engine.journal
CREATE TABLE IF NOT EXISTS "journal" (
  "id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'The journal ID',
  "title" VARCHAR(200) NOT NULL COMMENT 'The journal, etc. title',
  "abbreviation" VARCHAR(100) DEFAULT NULL COMMENT 'The official ISO 4 title abbreviation, with periods',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web link to the journal''s home page',
  "issn" CHAR(9) DEFAULT NULL COMMENT 'The International Standard Serial Number',
  "publisher_id"  BIGINT(20) UNSIGNED DEFAULT NULL COMMENT 'The ID of the publisher',
  "notes" TEXT DEFAULT NULL COMMENT 'A brief description of the journal',
  "peer_reviewed" BIT(1) DEFAULT NULL COMMENT 'Whether the journal publishes peer-reviewed articles',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "journal_issn" ("issn") USING BTREE,
  KEY "FK_journal_publisher" ("publisher_id"),
  KEY "journal_title" ("title") USING BTREE,
  KEY "journal_abbreviation" ("abbreviation") USING BTREE,
  FULLTEXT KEY "journal_fulltext" ("title","abbreviation","url","issn","notes"),
  CONSTRAINT "FK_journal_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_journal_publisher" FOREIGN KEY ("publisher_id") REFERENCES "publisher" ("id") ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT "CC_journal_issn" CHECK ("issn" regexp '^(\\d{4}-\\d{3}[\\dX])?$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Definitive list of journal titles, abbreviations, etc.';

-- Dumping structure for table evidence_engine.log
CREATE TABLE IF NOT EXISTS "log" (
  "id"  BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'The log entry ID',
  "timestamp" DATETIME NOT NULL DEFAULT current_timestamp() COMMENT 'The date and time at which the log entry was made',
  "user_id"  BIGINT(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'The ID of the user who made the change',
  "transaction_kind" CHAR(3) NOT NULL COMMENT 'The kind of change that was made',
  "entity_id" BIGINT(20) UNSIGNED NOT NULL COMMENT 'The ID of the affected entity',
  "linked_entity_id" BIGINT(20) UNSIGNED DEFAULT NULL COMMENT 'The ID of the entity that was linked/unlinked',
  PRIMARY KEY ("id"),
  KEY "log_entity" ("entity_id"),
  KEY "log_linked_entity" ("linked_entity_id"),
  KEY "FK_log_transaction_kind" ("transaction_kind"),
  KEY "log_user" ("user_id") USING BTREE,
  CONSTRAINT "FK_log_transaction_kind" FOREIGN KEY ("transaction_kind") REFERENCES "transaction_kind" ("code") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_log_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_log_entity_id" FOREIGN KEY ("entity_id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_log_linked_entity_id" FOREIGN KEY ("linked_entity_id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A log of all transactions';

CREATE VIEW IF NOT EXISTS `log_vw` AS
SELECT l."id", l."timestamp", l."user_id", l."transaction_kind", e."dtype" "entity_kind", l."entity_id", le."dtype" "linked_entity_kind", l."linked_entity_id" FROM "log" l
JOIN "entity" e ON l."entity_id" = e."id"
LEFT JOIN "entity" le ON l."linked_entity_id" = le."id"
ORDER BY l."id";

-- Dumping structure for table evidence_engine.authority_kind
CREATE TABLE IF NOT EXISTS "authority_kind" (
  "code" CHAR(3) NOT NULL COMMENT 'Unique authority code',
  "label" VARCHAR(10) NOT NULL COMMENT 'Unique authority label',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the authority',
  PRIMARY KEY ("code"),
  UNIQUE KEY "authority_kind_label" ("label") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Access authorities that can be granted to users';

-- Dumping structure for table evidence_engine.person
CREATE TABLE IF NOT EXISTS "person" (
  "id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'Unique person identifier',
  "title" VARCHAR(10) DEFAULT NULL COMMENT 'Person''s title, e.g., Prof., Dr.',
  "first_name" VARCHAR(80) NOT NULL COMMENT 'Person''s first names and/or initials',
  "nickname" VARCHAR(40) DEFAULT NULL COMMENT 'Nickname by which commonly known',
  "prefix" VARCHAR(20) DEFAULT NULL COMMENT 'Prefix to last name, e.g., van, de',
  "last_name" VARCHAR(40) NOT NULL COMMENT 'Person''s last name,  without prefix or suffix',
  "suffix" VARCHAR(16) DEFAULT NULL COMMENT 'Suffix to last name, e.g. Jr., Sr.',
  "alias" VARCHAR(40) DEFAULT NULL COMMENT 'Alternative last name',
  "notes" TEXT DEFAULT NULL COMMENT 'Brief biography, notes, etc.',
  "qualifications" TEXT DEFAULT NULL COMMENT 'Academic qualifications',
  "country" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for country of primary professional association',
  "checked" BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Set when the person''s credentials have been checked',
  "published" BIT(1) DEFAULT NULL COMMENT 'Set if person has published peer-reviewed papers on climate change',
  PRIMARY KEY ("id"),
  KEY "person_title" ("title") USING BTREE,
  KEY "person_first_name" ("first_name") USING BTREE,
  KEY "person_last_name" ("last_name") USING BTREE,
  KEY "person_qualifications" ("qualifications") USING BTREE,
  KEY "person_country" ("country") USING BTREE,
  KEY "person_notes" ("notes") USING BTREE,
  FULLTEXT KEY "person_fulltext" ("title","first_name","nickname","prefix","last_name","suffix","alias","notes","qualifications"),
  CONSTRAINT "FK_person_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_person_country" FOREIGN KEY ("country") REFERENCES "country" ("alpha_2") ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='People who have publicly expressed contrarian/sceptical views about topic orthodoxy, whether by signing declarations, open letters or publishing science articles.';

-- Dumping structure for table evidence_engine.publication
CREATE TABLE IF NOT EXISTS "publication" (
  "id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'Unique publication ID',
  "title" VARCHAR(200) NOT NULL COMMENT 'Publication title',
  "authors" VARCHAR(2000) NOT NULL COMMENT 'List of author names',
  "journal_id"  BIGINT(20) UNSIGNED DEFAULT NULL COMMENT 'The ID of the Journal',
  "publisher_id" BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT 'The ID of the Publisher',
  "kind" VARCHAR(6) NOT NULL COMMENT 'The kind of publication',
  "date" DATE DEFAULT NULL COMMENT 'Publication date',
  "year" year(4) DEFAULT NULL COMMENT 'Publication year',
  "keywords" VARCHAR(255) DEFAULT NULL COMMENT 'Keywords per publication metadata',
  "location" VARCHAR(50) DEFAULT NULL COMMENT 'The location of the relevant section within the publication',
  "abstract" TEXT DEFAULT NULL COMMENT 'Abstract from the article',
  "notes" TEXT DEFAULT NULL COMMENT 'Added notes about the publication',
  "peer_reviewed" BIT(1) DEFAULT NULL COMMENT 'Whether the article was peer-reviewed',
  "doi" VARCHAR(100) DEFAULT NULL COMMENT 'Digital Object Identifier',
  "isbn" VARCHAR(20) DEFAULT NULL COMMENT 'International Standard Book Number (printed publications only)',
  "pmcid" VARCHAR(10) DEFAULT NULL COMMENT 'The U.S. National Library of Medicine''s PubMed Central ID',
  "pmid" VARCHAR(10) DEFAULT NULL COMMENT 'The U.S. National Library of Medicine''s PubMed ID',
  "hsid" VARCHAR(12) DEFAULT NULL COMMENT 'The Corporation for National Research Initiatives''s Handle System ID',
  "arxivid" VARCHAR(15) DEFAULT NULL COMMENT 'Cornell University Library''s arXiv.org ID',
  "biorxivid" VARCHAR(20) DEFAULT NULL COMMENT 'Cold Spring Harbor Laboratory''s bioRxiv.org ID',
  "medrxivid" VARCHAR(20) DEFAULT NULL COMMENT 'Cold Spring Harbor Laboratory''s medRxiv.org ID',
  "ericid" VARCHAR(8) DEFAULT NULL COMMENT 'U.S. Department of Education''s ERIC database ID (niche)',
  "ihepid" VARCHAR(10) DEFAULT NULL COMMENT 'CERN''s INSPIRE-HEP ID',
  "oaipmhid" VARCHAR(50) DEFAULT NULL COMMENT 'Open Archives Initiative''s OAI-PMH ID',
  "halid" VARCHAR(20) DEFAULT NULL COMMENT 'CNRS (France)''s HAL ID',
  "zenodoid" VARCHAR(10) DEFAULT NULL COMMENT 'CERN''s Zenodo Record ID',
  "scopuseid" VARCHAR(16) DEFAULT NULL COMMENT 'Elsevier''s SCOPUS database EID (proprietary)',
  "wsan" VARCHAR(25) DEFAULT NULL COMMENT 'Clarivate''s Web of Science Accession Number (UT) (proprietary)',
  "pinfoan" VARCHAR(30) DEFAULT NULL COMMENT 'American Psychological Association''s PsycINFO Accession Number (proprietary/niche)',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'URL of the publication',
  "cached" BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  "accessed" DATE DEFAULT NULL COMMENT 'Date a web page was accessed',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "publication_doi" ("doi") USING BTREE,
  UNIQUE KEY "publication_isbn" ("isbn") USING BTREE,
  UNIQUE KEY "publication_pmcid" ("pmcid") USING BTREE,
  UNIQUE KEY "publication_pmid" ("pmid") USING BTREE,
  UNIQUE KEY "publication_hsid" ("hsid") USING BTREE,
  UNIQUE KEY "publication_arxivid" ("arxivid") USING BTREE,
  UNIQUE KEY "publication_biorxivid" ("biorxivid") USING BTREE,
  UNIQUE KEY "publication_medrxivid" ("medrxivid") USING BTREE,
  UNIQUE KEY "publication_ericid" ("ericid") USING BTREE,
  UNIQUE KEY "publication_ihepid" ("ihepid") USING BTREE,
  UNIQUE KEY "publication_oaipmhid" ("oaipmhid") USING BTREE,
  UNIQUE KEY "publication_halid" ("halid") USING BTREE,
  UNIQUE KEY "publication_zenodoid" ("zenodoid") USING BTREE,
  UNIQUE KEY "publication_scopuseid" ("scopuseid") USING BTREE,
  UNIQUE KEY "publication_wsan" ("wsan") USING BTREE,
  UNIQUE KEY "publication_pinfoan" ("pinfoan") USING BTREE,
  KEY "FK_publication_journal" ("journal_id"),
  KEY "FK_publication_publisher" ("publisher_id"),
  KEY "FK_publication_publication_kind" ("kind"),
  FULLTEXT KEY "publication_fulltext" ("title","authors","abstract","keywords","notes","doi","isbn","pmcid","pmid","hsid","arxivid","biorxivid","medrxivid","ericid","ihepid","oaipmhid","halid","zenodoid","scopuseid","wsan","pinfoan","url"),
  CONSTRAINT "FK_publication_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_publication_journal" FOREIGN KEY ("journal_id") REFERENCES "journal" ("id") ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT "FK_publication_publisher" FOREIGN KEY ("publisher_id") REFERENCES "publisher" ("id") ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT "FK_publication_publication_kind" FOREIGN KEY ("kind") REFERENCES "publication_kind" ("kind") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "CC_publication_doi" CHECK("doi" regexp '^10\\.\\d{4,9}/[A-Z0-9-._:;()<>/]+$'),
  CONSTRAINT "CC_publication_pmcid" CHECK("pmcid" regexp '^PMC\\d{7}$'),
  CONSTRAINT "CC_publication_pmid" CHECK("pmid" regexp '^\\d{1,10}$'),
  CONSTRAINT "CC_publication_hsid" CHECK("hsid" regexp '^[^/]+/.+$'),
  CONSTRAINT "CC_publication_arxivid" CHECK("arxivid" regexp '^\\d{4}\\.\\d{4,5}(v\\d+)?$'),
  CONSTRAINT "CC_publication_biorxivid" CHECK("biorxivid" regexp '^10\\.1101\\/\\d+$'),
  CONSTRAINT "CC_publication_medrxivid" CHECK("medrxivid" regexp '^10\\.1101\\/\\d+$'),
  CONSTRAINT "CC_publication_ericid" CHECK("ericid" regexp '^(?i)ED\\d{6}$'),
  CONSTRAINT "CC_publication_ihepid" CHECK("ihepid" regexp '^\\d{6,10}$'),
  CONSTRAINT "CC_publication_oaipmhid" CHECK("oaipmhid" regexp '^oai:[^:]+:[^:]+$'),
  CONSTRAINT "CC_publication_halid" CHECK("halid" regexp '^hal-\\d{6,10}$'),
  CONSTRAINT "CC_publication_zenodoid" CHECK("zenodoid" regexp '^\\d{6,10}$'),
  CONSTRAINT "CC_publication_scopuseid" CHECK("scopuseid" regexp '^\\d{8,16}$'),
  CONSTRAINT "CC_publication_wsan" CHECK("wsan" regexp '^[A-Z0-9]{15,25}$'),
  CONSTRAINT "CC_publication_pinfoan" CHECK("pinfoan" regexp '^[A-Z0-9\\-]{10,30}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='References to published articles, peer-reviewed or otherwise.';

-- Dumping structure for table evidence_engine.publication_kind
CREATE TABLE IF NOT EXISTS "publication_kind" (
  "kind" VARCHAR(10) NOT NULL COMMENT 'The publication type per TY field in RIS specification',
  "label" VARCHAR(25) NOT NULL COMMENT 'Label for the publication kind',
  PRIMARY KEY ("kind") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Publication kind per TY field in RIS specification';

-- Dumping structure for table evidence_engine.publisher
CREATE TABLE IF NOT EXISTS "publisher" (
  "id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'The unique publisher identifier',
  "name" VARCHAR(200) NOT NULL COMMENT 'The publisher name',
  "location" VARCHAR(50) DEFAULT NULL COMMENT 'The publisher location',
  "country" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the publisher''s country',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'URL of publisher''s home page',
  "journal_count" SMALLINT(6) UNSIGNED DEFAULT NULL COMMENT 'The number of journals published',
  "notes" TEXT DEFAULT NULL COMMENT 'Notes on the publisher',
  PRIMARY KEY ("id"),
  KEY "FK_publisher_country" ("country") USING BTREE,
  KEY "publisher_name" ("name"),
  FULLTEXT KEY "publisher_fulltext" ("name","location","url","notes"),
  CONSTRAINT "FK_publisher_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_publisher_country" FOREIGN KEY ("country") REFERENCES "country" ("alpha_2") ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A list of book, journal, etc. publishers. The table can contain duplicate entries in the name column, reflecting the same publisher in different locations.';

-- Dumping structure for table evidence_engine.quotation
CREATE TABLE IF NOT EXISTS "quotation" (
  "id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'Unique quotation identifier',
  "quotee" VARCHAR(50) NOT NULL COMMENT 'The person(s) who made the quotation',
  "text" VARCHAR(1000) NOT NULL COMMENT 'The quotation text',
  "date" DATE DEFAULT NULL COMMENT 'The quotation date',
  "source" VARCHAR(200) DEFAULT NULL COMMENT 'The source of the quotation',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web url to the quotation',
  "notes" TEXT DEFAULT NULL COMMENT 'Added notes about the quotation',
  PRIMARY KEY ("id"),
  KEY "quotation_quotee" ("quotee") USING BTREE,
  FULLTEXT KEY "quotation_fulltext" ("quotee","text","source","url","notes"),
  CONSTRAINT "FK_quotation_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Quotations by leading sceptics and contrarians';

-- Dumping structure for table evidence_engine.status_kind
CREATE TABLE IF NOT EXISTS "status_kind" (
  "code" CHAR(3) NOT NULL COMMENT 'The status code',
  "label" VARCHAR(20) NOT NULL COMMENT 'The status label',
  "description" VARCHAR(100) NOT NULL COMMENT 'Defines the meaning of the status code',
  PRIMARY KEY ("code"),
  UNIQUE KEY "status_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='The set of status values defining an entity''s lifecycle.';

-- Dumping structure for table evidence_engine.topic
CREATE TABLE IF NOT EXISTS "topic" (
  "id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'The unique topic identifier',
  "label" VARCHAR(50) NOT NULL COMMENT 'The topic name/label',
  "description" VARCHAR(500) DEFAULT NULL COMMENT 'Notes on when to use the topic',
  "parent_id"  BIGINT(20) UNSIGNED DEFAULT NULL COMMENT 'The parent topic ID',
  PRIMARY KEY ("id"),
  KEY "FK_topic_topic" ("parent_id"),
  FULLTEXT KEY "topic_fulltext" ("label","description"),
  CONSTRAINT "FK_topic_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "fk_topic_topic" FOREIGN KEY ("parent_id") REFERENCES "topic" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='A topic in a hierarchy of such topics, with arbitrary breadth and depth';

-- Dumping structure for table evidence_engine.transaction_kind
CREATE TABLE IF NOT EXISTS "transaction_kind" (
  "code" CHAR(3) NOT NULL COMMENT 'The transaction code',
  "label" VARCHAR(20) NOT NULL COMMENT 'A UI label for the transaction kind',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the transaction kind',
  PRIMARY KEY ("code"),
  UNIQUE KEY "transaction_kind_label" ("label")
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Enumerates the possible transaction kinds';

-- Dumping structure for table evidence_engine.user
CREATE TABLE IF NOT EXISTS "user" (
  "id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'The unique system-assigned user identifier',
  "username" VARCHAR(50) NOT NULL COMMENT 'The unique user-assigned user name',
  "password" VARCHAR(68) NOT NULL COMMENT 'Bcrypt hash of the user''s password',
  "enabled" BIT(1) NOT NULL DEFAULT b'1' COMMENT 'Whether the user account is enabled',
  "first_name" VARCHAR(50) NULL COMMENT 'The user''s first name',
  "last_name" VARCHAR(50) NULL COMMENT 'The user''s last name',
  "email" VARCHAR(100) DEFAULT NULL COMMENT 'The user''s email address, used for sign-in',
  "country" CHAR(2) DEFAULT NULL COMMENT 'ISO-3166-1 alpha-2 code for user''s country of residence',
  "notes" TEXT DEFAULT NULL COMMENT 'Added notes about the user',
  PRIMARY KEY ("id"),
  UNIQUE KEY ("username"),
  KEY "FK_user_country" ("country"),
  FULLTEXT KEY "user_fulltext" ("username","first_name","last_name","email","notes"),
  CONSTRAINT "FK_user_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_user_country" FOREIGN KEY ("country") REFERENCES "country" ("alpha_2") ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds details of authenticatable users';

-- Additional tables required by Spring Security
CREATE TABLE IF NOT EXISTS "user_authority" (
  "user_id" BIGINT(20) UNSIGNED NULL DEFAULT NULL COMMENT 'The user ID',
  "username" VARCHAR(50) NOT NULL COMMENT 'The login user name',
  "authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
  UNIQUE KEY "user_authority" ("user_id", "username", "authority"),
  CONSTRAINT "FK_user_authority_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_user_authority_username" FOREIGN KEY ("username") REFERENCES "user" ("username") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_user_authority_authority" FOREIGN KEY ("authority") REFERENCES "authority_kind" ("code") ON UPDATE CASCADE ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds authorities granted to users';

-- This Trigger is required in order to populate user_authority.username on INSERT,
-- because the application level JPA code uses user_id as the key,
-- whereas Spring Security uses username as the key.
DELIMITER //

CREATE TRIGGER IF NOT EXISTS "TR_user_authority_username"
BEFORE INSERT ON "user_authority"
FOR EACH ROW
BEGIN
  DECLARE v_username VARCHAR(50);

  SELECT "username" INTO v_username
  FROM "user" u
  WHERE u."id" = NEW."user_id";

  SET NEW."username" = v_username;
END//

DELIMITER ;

CREATE TABLE IF NOT EXISTS "group" (
  "id"  BIGINT(20) UNSIGNED AUTO_INCREMENT NOT NULL COMMENT 'The unique system-assigned group identifier',
  "groupname" VARCHAR(50) NOT NULL COMMENT 'The group name',
  PRIMARY KEY ("id"),
  UNIQUE KEY "group_groupname" ("groupname"),
  FULLTEXT KEY "group_fulltext" ("groupname"),
  CONSTRAINT "FK_group_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds groups to which users can belong';

CREATE TABLE IF NOT EXISTS "group_authority" (
  "group_id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'ID of a group',
  "authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
  UNIQUE KEY "group_authority" ("group_id", "authority"),
  CONSTRAINT "FK_group_authority_group" FOREIGN KEY ("group_id") REFERENCES "group" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_group_authority_authority" FOREIGN KEY ("authority") REFERENCES "authority_kind" ("code") ON UPDATE CASCADE ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds authorities granted to groups';

CREATE TABLE IF NOT EXISTS "group_user" (
  "id"  BIGINT(20) UNSIGNED AUTO_INCREMENT NOT NULL COMMENT 'The unique system-assigned identifier',
  "group_id"  BIGINT(20) UNSIGNED NOT NULL COMMENT 'ID of the group to which user belongs',
  "username" VARCHAR(50) NOT NULL COMMENT 'The login user name',
  PRIMARY KEY ("id"),
  UNIQUE KEY "group_user" ("username", "group_id"),
  CONSTRAINT "FK_group_user_group" FOREIGN KEY ("group_id") REFERENCES "group" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_group_user_user" FOREIGN KEY ("username") REFERENCES "user" ("username") ON UPDATE CASCADE ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Defines group membership';

CREATE TABLE IF NOT EXISTS "persistent_login" (
  "series" VARCHAR(64) COMMENT 'Encoded random number used to detect cookie stealing',
  "username" VARCHAR(64) NOT NULL COMMENT 'The authenticated username',
  "token" VARCHAR(64) NOT NULL COMMENT 'The authentication token returned as a cookie',
  "last_used" TIMESTAMP NOT NULL COMMENT 'The date/time at which the token was last used',
  PRIMARY KEY ("series")
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Holds login tokens that persist across HTTP sessions';

/*
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS export_table_to_csv(
    IN p_table_name VARCHAR(255),
    IN p_output_path VARCHAR(1024)
)
COMMENT 'Exports a table to a CSV format that can be read by import_table_from_csv'
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE col_name VARCHAR(255);
    DECLARE col_type VARCHAR(255);
    DECLARE select_list TEXT DEFAULT '';
    DECLARE header_list TEXT DEFAULT '';

    DECLARE cur CURSOR FOR
        SELECT COLUMN_NAME, DATA_TYPE
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
        ORDER BY ORDINAL_POSITION;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO col_name, col_type;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Header row
        SET header_list = CONCAT(
            header_list,
            IF(header_list = '', '', ','),
            '''', col_name, ''''
        );

        -- BIT → INT conversion (no alias)
        IF col_type = 'bit' THEN
            SET select_list = CONCAT(
                select_list,
                IF(select_list = '', '', ','),
                '"', col_name, '" + 0'
            );
        ELSE
            SET select_list = CONCAT(
                select_list,
                IF(select_list = '', '', ','),
                '"', col_name, '"'
            );
        END IF;
    END LOOP;

    CLOSE cur;

    SET @sql = CONCAT(
        'SELECT ', header_list, '\n',
        'UNION ALL\n',
        'SELECT ', select_list, '\n',
        'FROM "', p_table_name, '"\n',
        'INTO OUTFILE ''', p_output_path, '''\n',
        'FIELDS TERMINATED BY '',''\n',
        'OPTIONALLY ENCLOSED BY ''"''\n',
        'ESCAPED BY ''''\n',
        'LINES TERMINATED BY ''\n'';'
    );

    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END//

DELIMITER ;


DELIMITER //

CREATE PROCEDURE IF NOT EXISTS import_table_from_csv(
    IN p_table_name VARCHAR(255),
    IN p_input_path VARCHAR(1024)
)
COMMENT 'Imports a table from the CSV format produced by export_table_to_csv'
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE col_name VARCHAR(255);
    DECLARE col_type VARCHAR(255);
    DECLARE col_list TEXT DEFAULT '';
    DECLARE set_list TEXT DEFAULT '';

    DECLARE cur CURSOR FOR
        SELECT COLUMN_NAME, DATA_TYPE
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
        ORDER BY ORDINAL_POSITION;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO col_name, col_type;
        IF done THEN
            LEAVE read_loop;
        END IF;

        IF col_type = 'bit' THEN
            SET col_list = CONCAT(
                col_list,
                IF(col_list = '', '', ','),
                '@v_', col_name
            );

            SET set_list = CONCAT(
                set_list,
                IF(set_list = '', '', ','),
                '"', col_name, '" = CAST(@v_', col_name, ' AS UNSIGNED)'
            );
        ELSE
            SET col_list = CONCAT(
                col_list,
                IF(col_list = '', '', ','),
                '"', col_name, '"'
            );
        END IF;
    END LOOP;

    CLOSE cur;

    SET @sql = CONCAT(
        'LOAD DATA LOCAL INFILE ''', p_input_path, '''\n',
        'INTO TABLE "', p_table_name, '"\n',
        'CHARACTER SET utf8mb4\n',
        'FIELDS TERMINATED BY '',''\n',
        'OPTIONALLY ENCLOSED BY ''"''\n',
        'ESCAPED BY ''''\n',
        'LINES TERMINATED BY ''\n''\n',
        'IGNORE 1 LINES\n',
        '(', col_list, ')\n',
        IF(set_list = '', '', CONCAT('SET ', set_list, '\n'))
    );

    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
*/

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
