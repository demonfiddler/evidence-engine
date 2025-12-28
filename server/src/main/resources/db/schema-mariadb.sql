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

CREATE TABLE IF NOT EXISTS "abbreviation" (
	"word" VARCHAR(50) NOT NULL COMMENT 'The title word, prefix or suffix',
	"is_prefix" BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Whether "word" is a prefix',
	"is_suffix" BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Whether "word" is a suffix',
	"abbreviation" VARCHAR(30) DEFAULT NULL COMMENT 'The abbreviation, if any, for "word"',
	"languages" VARCHAR(50) NOT NULL COMMENT 'The applicable languages',
  PRIMARY KEY ("word"),
	INDEX "abbreviation_abbreviation" ("abbreviation") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='The ISSN Network''s ''List of Title Word Abbreviations'' (LTWA), as used by ISO 4.';

-- Dumping structure for table evidence_engine.entity
CREATE TABLE IF NOT EXISTS "entity" (
  "id" bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The unique entity record identifier',
  "dtype" char(3) NOT NULL COMMENT 'The entity type discriminator',
  "status" char(3) NOT NULL DEFAULT 'DRA' COMMENT 'The record status',
  "rating" tinyint(3) unsigned DEFAULT NULL COMMENT 'Quality/significance/eminence star rating, 1..5',
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
  CONSTRAINT "FK_entity_updated_by_user" FOREIGN KEY ("updated_by_user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT "CC_rating" CHECK ("rating" BETWEEN 1 AND 5)
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

-- Dumping structure for table evidence_engine.comment
CREATE TABLE IF NOT EXISTS "comment" (
  "id" bigint(20) unsigned NOT NULL COMMENT 'The unique comment identifier',
  "target_id" bigint(20) unsigned NOT NULL COMMENT 'The ID of the target entity with which the comment is associated',
  "parent_id" bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the parent comment to which this comment is a reply.',
  "text" varchar(500) NOT NULL COMMENT 'The text of the comment',
  PRIMARY KEY ("id"),
  FULLTEXT KEY "comment_fulltext" ("text"),
  CONSTRAINT "FK_comment_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_comment_target" FOREIGN KEY ("target_id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_comment_parent" FOREIGN KEY ("parent_id") REFERENCES "comment" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Comments associated with a tracked entity';

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
  "country" char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the country to which the declaration pertains',
  "url" varchar(200) DEFAULT NULL COMMENT 'Web URL of the original declaration',
  "cached" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  "signatories" text DEFAULT NULL COMMENT 'The list of signatories, one per line',
  "signatory_count" smallint(6) DEFAULT NULL COMMENT 'The number of signatories',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the declaration',
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
  "abbreviation" varchar(50) DEFAULT NULL COMMENT 'The official ISO 4 title abbreviation, with periods',
  "url" varchar(200) DEFAULT NULL COMMENT 'Web link to the journal''s home page',
  "issn" char(9) DEFAULT NULL COMMENT 'The International Standard Serial Number',
  "publisher_id"  bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the publisher',
  "notes" varchar(200) DEFAULT NULL COMMENT 'A brief description of the journal',
  "peer_reviewed" BIT(1) DEFAULT NULL COMMENT 'Whether the journal publishes peer-reviewed articles',
  PRIMARY KEY ("id") USING BTREE,
  UNIQUE KEY "journal_issn" ("issn") USING BTREE,
  KEY "FK_journal_publisher" ("publisher_id"),
  KEY "journal_title" ("title") USING BTREE,
  KEY "journal_abbreviation" ("abbreviation") USING BTREE,
  FULLTEXT KEY "journal_fulltext" ("title","abbreviation","url","issn","notes"),
  CONSTRAINT "FK_journal_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_journal_publisher" FOREIGN KEY ("publisher_id") REFERENCES "publisher" ("id") ON UPDATE CASCADE,
  CONSTRAINT "CC_journal_issn" CHECK ("issn" regexp '^(\\d{4}-\\d{3}[\\dX])?$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Definitive list of journal titles, abbreviations, etc.';

-- Dumping structure for table evidence_engine.log
CREATE TABLE IF NOT EXISTS "log" (
  "id"  bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'The log entry ID',
  "timestamp" datetime NOT NULL DEFAULT current_timestamp() COMMENT 'The date and time at which the log entry was made',
  "user_id"  bigint(20) unsigned NOT NULL DEFAULT 0 COMMENT 'The ID of the user who made the change',
  "transaction_kind" char(3) NOT NULL COMMENT 'The kind of change that was made',
  "entity_id" bigint(20) unsigned NOT NULL COMMENT 'The ID of the affected entity',
  "linked_entity_id" bigint(20) unsigned DEFAULT NULL COMMENT 'The ID of the entity that was linked/unlinked',
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

-- Dumping structure for table evidence_engine.authority_kind
CREATE TABLE IF NOT EXISTS "authority_kind" (
  "code" char(3) NOT NULL COMMENT 'Unique authority code',
  "label" varchar(10) NOT NULL COMMENT 'Unique authority label',
  "description" varchar(50) NOT NULL COMMENT 'Description of the authority',
  PRIMARY KEY ("code"),
  UNIQUE KEY "authority_kind_label" ("label") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Access authorities that can be granted to users';

-- Dumping structure for table evidence_engine.person
CREATE TABLE IF NOT EXISTS "person" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'Unique person identifier',
  "title" varchar(10) DEFAULT NULL COMMENT 'Person''s title, e.g., Prof., Dr.',
  "first_name" varchar(80) NOT NULL COMMENT 'Person''s first names and/or initials',
  "nickname" varchar(40) DEFAULT NULL COMMENT 'Nickname by which commonly known',
  "prefix" varchar(20) DEFAULT NULL COMMENT 'Prefix to last name, e.g., van, de',
  "last_name" varchar(40) NOT NULL COMMENT 'Person''s last name,  without prefix or suffix',
  "suffix" varchar(16) DEFAULT NULL COMMENT 'Suffix to last name, e.g. Jr., Sr.',
  "alias" varchar(40) DEFAULT NULL COMMENT 'Alternative last name',
  "notes" text DEFAULT NULL COMMENT 'Brief biography, notes, etc.',
  "qualifications" text DEFAULT NULL COMMENT 'Academic qualifications',
  "country" char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for country of primary professional association',
  "checked" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Set when the person''s credentials have been checked',
  "published" bit(1) DEFAULT NULL COMMENT 'Set if person has published peer-reviewed papers on climate change',
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
  "id"  bigint(20) unsigned NOT NULL COMMENT 'Unique publication ID',
  "title" varchar(200) NOT NULL COMMENT 'Publication title',
  "authors" varchar(2000) NOT NULL COMMENT 'List of author names',
  "journal_id"  bigint(20) unsigned DEFAULT NULL COMMENT 'Journal title',
  "kind" varchar(6) NOT NULL COMMENT 'The kind of publication',
  "date" date DEFAULT NULL COMMENT 'Publication date',
  "year" year(4) DEFAULT NULL COMMENT 'Publication year',
	`keywords` VARCHAR(255) DEFAULT NULL COMMENT 'Keywords per publication metadata',
	"location" VARCHAR(50) DEFAULT NULL COMMENT 'The location of the relevant section within the publication',
  "abstract" text DEFAULT NULL COMMENT 'Abstract from the article',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the publication',
  "peer_reviewed" bit(1) DEFAULT NULL COMMENT 'Whether the article was peer-reviewed',
  "doi" varchar(100) DEFAULT NULL COMMENT 'Digital Object Identifier',
  "isbn" varchar(20) DEFAULT NULL COMMENT 'International Standard Book Number (printed publications only)',
	"pmcid" VARCHAR(10) DEFAULT NULL COMMENT 'The U.S. National Library of Medicine''s PubMed Central ID',
	"pmid" varchar(10) DEFAULT NULL COMMENT 'The U.S. National Library of Medicine''s PubMed ID',
	"hsid" varchar(12) DEFAULT NULL COMMENT 'The Corporation for National Research Initiatives''s Handle System ID',
	"arxivid" varchar(15) DEFAULT NULL COMMENT 'Cornell University Library''s arXiv.org ID',
	"biorxivid" varchar(20) DEFAULT NULL COMMENT 'Cold Spring Harbor Laboratory''s bioRxiv.org ID',
	"medrxivid" varchar(20) DEFAULT NULL COMMENT 'Cold Spring Harbor Laboratory''s medRxiv.org ID',
	"ericid" varchar(8) DEFAULT NULL COMMENT 'U.S. Department of Education''s ERIC database ID (niche)',
	"ihepid" varchar(10) DEFAULT NULL COMMENT 'CERN''s INSPIRE-HEP ID',
	"oaipmhid" varchar(50) DEFAULT NULL COMMENT 'Open Archives Initiative''s OAI-PMH ID',
	"halid" varchar(20) DEFAULT NULL COMMENT 'CNRS (France)''s HAL ID',
	"zenodoid" varchar(10) DEFAULT NULL COMMENT 'CERN''s Zenodo Record ID',
	"scopuseid" varchar(16) DEFAULT NULL COMMENT 'Elsevier''s SCOPUS database EID (proprietary)',
	"wsan" varchar(25) DEFAULT NULL COMMENT 'Clarivate''s Web of Science Accession Number (UT) (proprietary)',
	"pinfoan" varchar(30) DEFAULT NULL COMMENT 'American Psychological Association''s PsycINFO Accession Number (proprietary/niche)',
  "url" varchar(200) DEFAULT NULL COMMENT 'URL of the publication',
  "cached" bit(1) NOT NULL DEFAULT b'0' COMMENT 'Flag to indicate that url content is cached on this application server',
  "accessed" date DEFAULT NULL COMMENT 'Date a web page was accessed',
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
  KEY "FK_publication_publication_kind" ("kind"),
  FULLTEXT KEY "publication_fulltext" ("title","authors","abstract","keywords","notes","doi","isbn","pmcid","pmid","hsid","arxivid","biorxivid","medrxivid","ericid","ihepid","oaipmhid","halid","zenodoid","scopuseid","wsan","pinfoan","url"),
  CONSTRAINT "FK_publication_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_publication_journal" FOREIGN KEY ("journal_id") REFERENCES "journal" ("id") ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT "FK_publication_publication_kind" FOREIGN KEY ("kind") REFERENCES "publication_kind" ("kind") ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT "CC_publication_doi" CHECK("doi" regexp '^10\\.\\d{4,9}\/(?i)[-._;()/:A-Z0-9]+$'),
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
  "kind" varchar(10) NOT NULL COMMENT 'The publication type per TY field in RIS specification',
  "label" varchar(25) NOT NULL COMMENT 'Label for the publication kind',
  PRIMARY KEY ("kind") USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Publication kind per TY field in RIS specification';

-- Dumping structure for table evidence_engine.publisher
CREATE TABLE IF NOT EXISTS "publisher" (
  "id"  bigint(20) unsigned NOT NULL COMMENT 'The unique publisher identifier',
  "name" varchar(200) NOT NULL COMMENT 'The publisher name',
  "location" varchar(50) DEFAULT NULL COMMENT 'The publisher location',
  "country" char(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the publisher''s country',
  "url" varchar(200) DEFAULT NULL COMMENT 'URL of publisher''s home page',
  "journal_count" smallint(6) unsigned DEFAULT NULL COMMENT 'The number of journals published',
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
  "password" varchar(68) NOT NULL COMMENT 'Bcrypt hash of the user''s password',
	"enabled" bit(1) NOT NULL DEFAULT b'1' COMMENT 'Whether the user account is enabled',
  "first_name" varchar(50) NULL COMMENT 'The user''s first name',
  "last_name" varchar(50) NULL COMMENT 'The user''s last name',
  "email" varchar(100) DEFAULT NULL COMMENT 'The user''s email address, used for sign-in',
  "country" char(2) DEFAULT NULL COMMENT 'ISO-3166-1 alpha-2 code for user''s country of residence',
  "notes" text DEFAULT NULL COMMENT 'Added notes about the user',
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
) COMMENT='Holds authorities granted to users';

CREATE TABLE IF NOT EXISTS "group" (
	"id"  bigint(20) unsigned AUTO_INCREMENT NOT NULL COMMENT 'The unique system-assigned group identifier',
	"groupname" VARCHAR(50) NOT NULL COMMENT 'The group name',
	PRIMARY KEY ("id"),
  UNIQUE KEY "group_groupname" ("groupname"),
  FULLTEXT KEY "group_fulltext" ("groupname"),
 	CONSTRAINT "FK_group_entity" FOREIGN KEY ("id") REFERENCES "entity" ("id") ON UPDATE CASCADE ON DELETE CASCADE
) COMMENT='Holds groups to which users can belong';

CREATE TABLE IF NOT EXISTS "group_authority" (
	"group_id"  bigint(20) unsigned NOT NULL COMMENT 'ID of a group',
	"authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
	UNIQUE KEY "group_authority" ("group_id", "authority"),
	CONSTRAINT "FK_group_authority_group" FOREIGN KEY ("group_id") REFERENCES "group" ("id") ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT "FK_group_authority_authority" FOREIGN KEY ("authority") REFERENCES "authority_kind" ("code") ON UPDATE CASCADE ON DELETE CASCADE
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

CREATE TABLE IF NOT EXISTS "persistent_login" (
    "series" VARCHAR(64) COMMENT 'Encoded random number used to detect cookie stealing',
    "username" VARCHAR(64) NOT NULL COMMENT 'The authenticated username',
    "token" VARCHAR(64) NOT NULL COMMENT 'The authentication token returned as a cookie',
    "last_used" TIMESTAMP NOT NULL COMMENT 'The date/time at which the token was last used',
    PRIMARY KEY ("series")
) COMMENT='Holds login tokens that persist across HTTP sessions';

DELIMITER //

CREATE PROCEDURE export_table_to_csv(
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

CREATE PROCEDURE import_table_from_csv(
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


/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
