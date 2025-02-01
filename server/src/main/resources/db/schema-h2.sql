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

CREATE ALIAS IF NOT EXISTS FT_INIT FOR "org.h2.fulltext.FullText.init";
CALL FT_INIT();

CREATE TABLE "claim" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'The unique claim identifier',
  "text" VARCHAR(500) NOT NULL COMMENT 'The claim text',
  "date" DATE DEFAULT NULL COMMENT 'The date on which the claim was first made',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the claim',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id")
);
CREATE INDEX "FK_claim_created_user" ON "claim" ("created_by_user_id");
CREATE INDEX "FK_claim_updated_user" ON "claim" ("updated_by_user_id");
CREATE INDEX "FK_claim_status" ON "claim" ("status");
CALL FT_CREATE_INDEX('PUBLIC', 'claim', 'text,notes');

CREATE TABLE "claim_declaration" (
  "claim_id" BIGINT NOT NULL COMMENT 'The claim ID',
  "declaration_id" BIGINT NOT NULL COMMENT 'The declaration ID',
  PRIMARY KEY ("claim_id","declaration_id")
);
CREATE INDEX "FK_claim_declaration_claim" ON "claim_declaration" ("claim_id");
CREATE INDEX "FK_claim_declaration_declaration" ON "claim_declaration" ("declaration_id");

CREATE TABLE "claim_person" (
  "claim_id" BIGINT NOT NULL COMMENT 'The claim ID',
  "person_id" BIGINT NOT NULL COMMENT 'The person ID',
  PRIMARY KEY ("claim_id","person_id")
);
CREATE INDEX "FK_claim_person_claim" ON "claim_person" ("claim_id");
CREATE INDEX "FK_claim_person_person" ON "claim_person" ("person_id");

CREATE TABLE "claim_publication" (
  "claim_id" BIGINT NOT NULL COMMENT 'The claim ID',
  "publication_id" BIGINT NOT NULL COMMENT 'The publication ID',
  PRIMARY KEY ("claim_id","publication_id")
);
CREATE INDEX "FK_claim_publication_claim" ON "claim_publication" ("claim_id");
CREATE INDEX "FK_claim_publication_publication" ON "claim_publication" ("publication_id");

CREATE TABLE "claim_quotation" (
  "claim_id" BIGINT NOT NULL COMMENT 'The claim ID',
  "quotation_id" BIGINT NOT NULL COMMENT 'The quotation ID',
  PRIMARY KEY ("claim_id","quotation_id")
);
CREATE INDEX "FK_claim_quotation_claim" ON "claim_quotation" ("claim_id");
CREATE INDEX "FK_claim_quotation_quotation" ON "claim_quotation" ("quotation_id");

CREATE TABLE "country" (
  "alpha_2" CHAR(2) NOT NULL COMMENT 'ISO-3166-1 alpha-2 code',
  "alpha_3" CHAR(3) NOT NULL COMMENT 'ISO-3166-1 alpha-3 code',
  "numeric" CHAR(3) NOT NULL CHECK ("numeric" REGEXP '^\d{3}$') COMMENT 'ISO-3166-1 numeric code',
  "iso_name" VARCHAR(100) NOT NULL COMMENT 'Official/ISO country name',
  "common_name" VARCHAR(50) NOT NULL COMMENT 'Common or short name',
  "year" SMALLINT NOT NULL COMMENT 'Year alpha-2 code was first assigned',
  "cc_tld" CHAR(3) NOT NULL COMMENT 'Country code top level domain',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Remarks as per Wikipedia ISO-3166-1 entry',
  PRIMARY KEY ("alpha_2"),
  UNIQUE ("alpha_3"),
  UNIQUE ("numeric"),
  UNIQUE ("common_name"),
  UNIQUE ("iso_name")
);
CREATE UNIQUE INDEX "country_alpha_3" ON "country" ("alpha_3");
CREATE UNIQUE INDEX "country_numeric" ON "country" ("numeric");
CREATE UNIQUE INDEX "country_common_name" ON "country" ("common_name");
CREATE UNIQUE INDEX "country_iso_name" ON "country" ("iso_name");

CREATE TABLE "declaration" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'The unique declaration identifier',
  "kind" VARCHAR(4) NOT NULL COMMENT 'The kind of declaration',
  "title" VARCHAR(100) NOT NULL COMMENT 'The declaration name or title',
  "date" DATE NOT NULL COMMENT 'The date the declaration was first published',
  "country_code" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the country to which the declaration pertains',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web URL of the original declaration',
  "cached" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Flag to indicate that url content is cached on this application server',
  "signatories" VARCHAR(65535) DEFAULT NULL COMMENT 'The list of signatories, one per line',
  "signatory_count" INT DEFAULT NULL COMMENT 'The number of signatories',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the declaration',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id")
);
CREATE INDEX "FK_declaration_declaration_kind" ON "declaration" ("kind");
CREATE INDEX "FK_declaration_country" ON "declaration" ("country_code");
CREATE INDEX "FK_declaration_status_kind" ON "declaration" ("status");
CREATE INDEX "FK_declaration_created_user" ON "declaration" ("created_by_user_id");
CREATE INDEX "FK_declaration_updated_user" ON "declaration" ("updated_by_user_id");
CALL FT_CREATE_INDEX('PUBLIC', 'declaration', 'title,signatories,notes');

CREATE TABLE "declaration_kind" (
  "kind" VARCHAR(4) NOT NULL COMMENT 'The declaration kind code',
  "label" VARCHAR(20) NOT NULL COMMENT 'Label for the declaration kind',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the declaration kind',
  PRIMARY KEY ("kind"),
  UNIQUE ("label")
);
CREATE UNIQUE INDEX "declaration_kind_label" ON "declaration_kind" ("label");

CREATE TABLE "declaration_person" (
  "declaration_id" BIGINT NOT NULL COMMENT 'The declaration ID',
  "person_id" BIGINT NOT NULL COMMENT 'The person ID',
  PRIMARY KEY ("declaration_id","person_id")
);
CREATE INDEX "FK_declaration_person_declaration" ON "declaration_person" ("declaration_id");
CREATE INDEX "FK_declaration_person_person" ON "declaration_person" ("person_id");

CREATE TABLE "declaration_quotation" (
  "declaration_id" BIGINT NOT NULL COMMENT 'The declaration ID',
  "quotation_id" BIGINT NOT NULL COMMENT 'The quotation ID',
  PRIMARY KEY ("declaration_id","quotation_id")
);
CREATE INDEX "FK_declaration_quotation_declaration" ON "declaration_quotation" ("declaration_id");
CREATE INDEX "FK_declaration_quotation_quotation" ON "declaration_quotation" ("quotation_id");

CREATE TABLE "entity_kind" (
  "code" CHAR(3) NOT NULL COMMENT 'Unique code for the entity kind',
  "label" VARCHAR(20) NOT NULL COMMENT 'Label for the entity kind',
  PRIMARY KEY ("code"),
  UNIQUE ("label")
);
CREATE UNIQUE INDEX "entity_kind_label" ON "entity_kind" ("label");

CREATE TABLE "journal" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'The journal ID',
  "title" VARCHAR(100) NOT NULL COMMENT 'The journal, etc. title',
  "abbreviation" VARCHAR(50) DEFAULT NULL COMMENT 'The abbreviation for title',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web link to the journal''s home page',
  "issn" CHAR(9) DEFAULT NULL CHECK ("issn" REGEXP '^[0-9]{4}-[0-9]{3}[0-9X]$') COMMENT 'The International Standard Serial Number',
  "publisher_id" BIGINT DEFAULT NULL COMMENT 'The ID of the publisher',
  "notes" VARCHAR(200) DEFAULT NULL COMMENT 'A brief description of the journal',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP DEFAULT NULL COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  UNIQUE ("issn")
);
CREATE UNIQUE INDEX "journal_issn" ON "journal" ("issn");
CREATE INDEX "journal_title" ON "journal" ("title");
CREATE INDEX "journal_abbreviation" ON "journal" ("abbreviation");
CREATE INDEX "FK_journal_publisher" ON "journal" ("publisher_id");
CREATE INDEX "FK_journal_created_user" ON "journal" ("created_by_user_id");
CREATE INDEX "FK_journal_updated_user" ON "journal" ("updated_by_user_id");
CREATE INDEX "FK_journal_status_kind" ON "journal" ("status");
CALL FT_CREATE_INDEX('PUBLIC', 'journal', 'title,abbreviation,url,issn,notes');

CREATE TABLE "log" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'The log entry ID',
  "timestamp" TIMESTAMP DEFAULT current_timestamp() NOT NULL COMMENT 'The date and time at which the log entry was made',
  "user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'The ID of the user who made the change',
  "transaction_kind" CHAR(3) NOT NULL COMMENT 'The kind of change that was made',
  "entity_kind" CHAR(3) NOT NULL COMMENT 'The kind of entity affected by the change',
  "entity_id" BIGINT NOT NULL COMMENT 'The ID of the affected entity',
  "linked_entity_kind" CHAR(3) DEFAULT NULL COMMENT 'The kind of entity that was linked/unlinked',
  "linked_entity_id" BIGINT DEFAULT NULL COMMENT 'The ID of the entity that was linked/unlinked',
  PRIMARY KEY ("id")
);
CREATE INDEX "log_user" ON "log" ("user_id");
CREATE INDEX "log_entity" ON "log" ("entity_kind","entity_id");
CREATE INDEX "log_linked_entity" ON "log" ("linked_entity_kind","linked_entity_id");
CREATE INDEX "FK_log_transaction_kind" ON "log" ("transaction_kind");

CREATE TABLE "permission_kind" (
  "code" CHAR(3) NOT NULL COMMENT 'Unique permission code',
  "label" VARCHAR(10) NOT NULL COMMENT 'Unique permission label',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the permission',
  PRIMARY KEY ("code"),
  UNIQUE ("label")
);
CREATE UNIQUE INDEX "permission_kind_label" ON "permission_kind" ("label");

CREATE TABLE "person" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Unique person identifier',
  "title" VARCHAR(10) DEFAULT NULL COMMENT 'Person''s title, e.g., Prof., Dr.',
  "first_name" VARCHAR(80) DEFAULT NULL COMMENT 'Person''s first names and/or initials',
  "nickname" VARCHAR(40) DEFAULT NULL COMMENT 'Nickname by which commonly known',
  "prefix" VARCHAR(20) DEFAULT NULL COMMENT 'Prefix to last name, e.g., van, de',
  "last_name" VARCHAR(40) NOT NULL COMMENT 'Person''s last name,  without prefix or suffix',
  "suffix" VARCHAR(16) DEFAULT NULL COMMENT 'Suffix to last name, e.g. Jr., Sr.',
  "alias" VARCHAR(40) DEFAULT NULL COMMENT 'Alternative last name',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Brief biography, notes, etc.',
  "qualifications" VARCHAR(65535) DEFAULT NULL COMMENT 'Academic qualifications',
  "country_code" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for country of primary professional association',
  "rating" INT DEFAULT 0 NOT NULL CHECK ("rating" BETWEEN 0 AND 5) COMMENT 'Eminence star rating, 0..5',
  "checked" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Set when the person''s credentials have been checked',
  "published" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Set if person has published peer-reviewed papers on climate change',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id")
);
CREATE INDEX "person_title" ON "person" ("title");
CREATE INDEX "person_first_name" ON "person" ("first_name");
CREATE INDEX "person_last_name" ON "person" ("last_name");
CREATE INDEX "person_qualifications" ON "person" ("qualifications");
CREATE INDEX "person_rating" ON "person" ("rating");
CREATE INDEX "person_country" ON "person" ("country_code");
CREATE INDEX "person_notes" ON "person" ("notes");
CREATE INDEX "FK_person_status_kind" ON "person" ("status");
CREATE INDEX "FK_person_created_user" ON "person" ("created_by_user_id");
CREATE INDEX "FK_person_updated_user" ON "person" ("updated_by_user_id");
CALL FT_CREATE_INDEX('PUBLIC', 'person', 'title,first_name,nickname,prefix,last_name,suffix,alias,notes,qualifications');

CREATE TABLE "publication" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Unique publication ID',
  "title" VARCHAR(200) NOT NULL COMMENT 'Publication title',
  "authors" VARCHAR(2000) NOT NULL COMMENT 'List of author names',
  "journal_id" BIGINT DEFAULT NULL COMMENT 'Journal title',
  "kind" VARCHAR(6) NOT NULL COMMENT 'The kind of publication',
  "date" DATE DEFAULT NULL COMMENT 'Publication date',
  "year" SMALLINT DEFAULT NULL COMMENT 'Publication year',
	"location" VARCHAR(50) DEFAULT NULL COMMENT 'The location of the relevant section within the publication',
  "abstract" VARCHAR(65535) DEFAULT NULL COMMENT 'Abstract from the article',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the publication',
  "peer_reviewed" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Whether the article was peer-reviewed',
  "doi" VARCHAR(255) DEFAULT NULL COMMENT 'Digital Object Identifier',
  "isbn" VARCHAR(20) DEFAULT NULL COMMENT 'International Standard Book Number (printed publications only)',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'URL of the publication',
  "cached" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Flag to indicate that url content is cached on this application server',
  "accessed" DATE DEFAULT NULL COMMENT 'Date a web page was accessed',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id"),
  UNIQUE ("doi"),
  UNIQUE ("isbn")
);
CREATE UNIQUE INDEX "publication_doi" ON "publication" ("doi");
CREATE INDEX "FK_publication_journal" ON "publication" ("journal_id");
CREATE INDEX "FK_publication_publication_kind" ON "publication" ("kind");
CREATE INDEX "FK_publication_status_kind" ON "publication" ("status");
CREATE INDEX "FK_publication_created_user" ON "publication" ("created_by_user_id");
CREATE INDEX "FK_publication_updated_user" ON "publication" ("updated_by_user_id");
CALL FT_CREATE_INDEX('PUBLIC', 'publication', 'title,authors,abstract,notes,doi,isbn,url');

CREATE TABLE "publication_kind" (
  "kind" VARCHAR(10) NOT NULL COMMENT 'The publication type per TY field in RIS specification',
  "label" VARCHAR(25) NOT NULL COMMENT 'Label for the publication kind',
  PRIMARY KEY ("kind")
);

CREATE TABLE "publication_person" (
  "publication_id" BIGINT NOT NULL COMMENT 'The publication ID',
  "person_id" BIGINT NOT NULL COMMENT 'The person ID',
  PRIMARY KEY ("publication_id","person_id")
);
CREATE INDEX "FK_publication_person_publication" ON "publication_person" ("publication_id");
CREATE INDEX "FK_publication_person_person" ON "publication_person" ("person_id");

CREATE TABLE "publisher" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'The unique publisher identifier',
  "name" VARCHAR(200) NOT NULL COMMENT 'The publisher name',
  "location" VARCHAR(50) DEFAULT NULL COMMENT 'The publisher location',
  "country_code" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the publisher''s country',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'URL of publisher''s home page',
  "journal_count" INT DEFAULT NULL COMMENT 'The number of journals published',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id")
);
CREATE INDEX "FK_publisher_country" ON "publisher" ("country_code");
CREATE INDEX "publisher_name" ON "publisher" ("name");
CREATE INDEX "FK_publisher_status_kind" ON "publisher" ("status");
CREATE INDEX "FK_publisher_created_user" ON "publisher" ("created_by_user_id");
CREATE INDEX "FK_publisher_updated_user" ON "publisher" ("updated_by_user_id");
CALL FT_CREATE_INDEX('PUBLIC', 'publisher', 'name,location,url');

CREATE TABLE "quotation" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Unique quotation identifier',
  "quotee" VARCHAR(50) NOT NULL COMMENT 'The person(s) who made the quotation',
  "text" VARCHAR(1000) NOT NULL COMMENT 'The quotation text',
  "date" DATE DEFAULT NULL COMMENT 'The quotation date',
  "source" VARCHAR(200) DEFAULT NULL COMMENT 'The source of the quotation',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web url to the quotation',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the quotation',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id")
);
CREATE INDEX "quotation_quotee" ON "quotation" ("quotee");
CREATE INDEX "FK_quotation_status" ON "quotation" ("status");
CREATE INDEX "FK_quotation_created_user" ON "quotation" ("created_by_user_id");
CREATE INDEX "FK_quotation_updated_user" ON "quotation" ("updated_by_user_id");
CALL FT_CREATE_INDEX('PUBLIC', 'quotation', 'quotee,text,source,url,notes');

CREATE TABLE "quotation_person" (
  "quotation_id" BIGINT NOT NULL COMMENT 'The quotation ID',
  "person_id" BIGINT NOT NULL COMMENT 'The person ID',
  PRIMARY KEY ("quotation_id","person_id")
);
CREATE INDEX "FK_quotation_person_quotation" ON "quotation_person" ("quotation_id");
CREATE INDEX "FK_quotation_person_person" ON "quotation_person" ("person_id");

CREATE TABLE "quotation_publication" (
  "quotation_id" BIGINT NOT NULL COMMENT 'The quotation ID',
  "publication_id" BIGINT NOT NULL COMMENT 'The publication ID',
  PRIMARY KEY ("quotation_id","publication_id")
);
CREATE INDEX "FK_quotation_publication_quotation" ON "quotation_publication" ("quotation_id");
CREATE INDEX "FK_quotation_publication_publication" ON "quotation_publication" ("publication_id");

CREATE TABLE "status_kind" (
  "code" CHAR(3) NOT NULL COMMENT 'The status code',
  "label" VARCHAR(20) NOT NULL COMMENT 'The status label',
  "description" VARCHAR(100) NOT NULL COMMENT 'Defines the meaning of the status code',
  PRIMARY KEY ("code"),
  UNIQUE ("label")
);
CREATE UNIQUE INDEX "status_kind_label" ON "status_kind" ("label");

CREATE TABLE "topic" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'The unique topic identifier',
  "label" VARCHAR(50) NOT NULL COMMENT 'The topic name/label',
  "description" VARCHAR(500) DEFAULT NULL COMMENT 'Notes on when to use the topic',
  "parent_id" BIGINT DEFAULT NULL COMMENT 'The parent topic ID',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id")
);
CREATE INDEX "FK_topic_topic" ON "topic" ("parent_id");
CREATE INDEX "FK_topic_created_user" ON "topic" ("created_by_user_id");
CREATE INDEX "FK_topic_updated_user" ON "topic" ("updated_by_user_id");
CREATE INDEX "FK_topic_status_kind" ON "topic" ("status");
CALL FT_CREATE_INDEX('PUBLIC', 'topic', 'label,description,status');

CREATE TABLE "topic_claim_ref" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" BIGINT NOT NULL COMMENT 'The associated topic ID',
  "claim_id" BIGINT NOT NULL COMMENT 'The associated claim ID',
  "locations" VARCHAR(2048) DEFAULT NULL COMMENT 'Location(s) within referenced claim (where applicable), one per line',
  PRIMARY KEY ("id"),
  UNIQUE ("topic_id","claim_id")
);
CREATE UNIQUE INDEX "topic_claim_ref_topic_claim" ON "topic_claim_ref" ("topic_id","claim_id");
CREATE INDEX "FK_topic_claim_ref_topic" ON "topic_claim_ref" ("topic_id");
CREATE INDEX "FK_topic_claim_ref_claim" ON "topic_claim_ref" ("claim_id");
CALL FT_CREATE_INDEX('PUBLIC', 'topic_claim_ref', 'locations');

CREATE TABLE "topic_declaration_ref" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" BIGINT NOT NULL COMMENT 'The associated topic ID',
  "declaration_id" BIGINT NOT NULL COMMENT 'The associated declaration ID',
  "locations" VARCHAR(2048) DEFAULT NULL COMMENT 'Location(s) within referenced declaration (where applicable)',
  PRIMARY KEY ("id"),
  UNIQUE ("topic_id","declaration_id")
);
CREATE UNIQUE INDEX "topic_declaration_ref_topic_declaration" ON "topic_declaration_ref" ("topic_id","declaration_id");
CREATE INDEX "FK_topic_declaration_ref_topic" ON "topic_declaration_ref" ("topic_id");
CREATE INDEX "FK_topic_declaration_ref_declaration" ON "topic_declaration_ref" ("declaration_id");
CALL FT_CREATE_INDEX('PUBLIC', 'topic_declaration_ref', 'locations');

CREATE TABLE "topic_person_ref" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" BIGINT NOT NULL COMMENT 'The associated topic ID',
  "person_id" BIGINT NOT NULL COMMENT 'The associated person ID',
  "locations" VARCHAR(2048) DEFAULT NULL COMMENT 'Location(s) within referenced person (where applicable)',
  PRIMARY KEY ("id"),
  UNIQUE ("topic_id","person_id")
);
CREATE UNIQUE INDEX "topic_person_ref_topic_person" ON "topic_person_ref" ("topic_id","person_id");
CREATE INDEX "FK_topic_person_ref_topic" ON "topic_person_ref" ("topic_id");
CREATE INDEX "FK_topic_person_ref_person" ON "topic_person_ref" ("person_id");
CALL FT_CREATE_INDEX('PUBLIC', 'topic_person_ref', 'locations');

CREATE TABLE "topic_publication_ref" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" BIGINT NOT NULL COMMENT 'The associated topic ID',
  "publication_id" BIGINT NOT NULL COMMENT 'The associated publication ID',
  "locations" VARCHAR(2048) DEFAULT NULL COMMENT 'Location(s) within referenced publication (where applicable)',
  PRIMARY KEY ("id"),
  UNIQUE ("topic_id","publication_id")
);
CREATE UNIQUE INDEX "topic_publication_ref_topic_publication" ON "topic_publication_ref" ("topic_id","publication_id");
CREATE INDEX "FK_topic_publication_ref_topic" ON "topic_publication_ref" ("topic_id");
CREATE INDEX "FK_topic_publication_ref_publication" ON "topic_publication_ref" ("publication_id");
CALL FT_CREATE_INDEX('PUBLIC', 'topic_publication_ref', 'locations');

CREATE TABLE "topic_quotation_ref" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Unique identifier, needed because GraphQL doesn''t support compound primary keys',
  "topic_id" BIGINT NOT NULL COMMENT 'The associated topic ID',
  "quotation_id" BIGINT NOT NULL COMMENT 'The associated quotation ID',
  "locations" VARCHAR(2048) DEFAULT NULL COMMENT 'Location(s) within referenced quotation (where applicable)',
  PRIMARY KEY ("id"),
  UNIQUE ("topic_id","quotation_id")
);
CREATE UNIQUE INDEX "topic_quotation_ref_topic_quotation" ON "topic_quotation_ref" ("topic_id","quotation_id");
CREATE INDEX "FK_topic_quotation_ref_topic" ON "topic_quotation_ref" ("topic_id");
CREATE INDEX "FK_topic_quotation_ref_quotation" ON "topic_quotation_ref" ("quotation_id");
CALL FT_CREATE_INDEX('PUBLIC', 'topic_quotation_ref', 'locations');

CREATE TABLE "transaction_kind" (
  "code" CHAR(3) NOT NULL COMMENT 'The transaction code',
  "label" VARCHAR(20) NOT NULL COMMENT 'A UI label for the transaction kind',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the transaction kind',
  PRIMARY KEY ("code"),
  UNIQUE ("label")
);
CREATE UNIQUE INDEX "transaction_kind_label" ON "transaction_kind" ("label");

CREATE TABLE "user" (
  "id" BIGINT AUTO_INCREMENT NOT NULL COMMENT 'The unique system-assigned user identifier',
  "login" VARCHAR(20) NOT NULL COMMENT 'The unique user-assigned login name',
  "first_name" VARCHAR(50) NOT NULL COMMENT 'The user''s first name',
  "last_name" VARCHAR(50) NOT NULL COMMENT 'The user''s last name',
  "email" VARCHAR(100) DEFAULT NULL COMMENT 'The user''s email address, used for sign-in',
  "country_code" CHAR(2) DEFAULT NULL COMMENT 'ISO-3166-1 alpha-2 code for user''s country of residence',
  "password_hash" CHAR(60) NOT NULL COMMENT 'Hash of the user''s password',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the user',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record',
  PRIMARY KEY ("id")
);
CREATE INDEX "FK_user_country" ON "user" ("country_code");
CREATE INDEX "FK_user_updated_user" ON "user" ("updated_by_user_id");
CREATE INDEX "FK_user_status" ON "user" ("status");
CREATE INDEX "FK_user_created_user" ON "user" ("created_by_user_id");
CALL FT_CREATE_INDEX('PUBLIC', 'user', 'login,first_name,last_name,email,notes');

CREATE TABLE "user_permission" (
  "user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'The user id',
  "permission_code" CHAR(3) NOT NULL COMMENT 'The permission code',
  PRIMARY KEY ("user_id","permission_code")
);
CREATE INDEX "FK_user_permission_permission" ON "user_permission" ("permission_code");

ALTER TABLE "claim"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "claim"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "claim"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");


ALTER TABLE "claim_declaration"
  ADD FOREIGN KEY ("declaration_id") 
  REFERENCES "declaration" ("id");

ALTER TABLE "claim_declaration"
  ADD FOREIGN KEY ("claim_id") 
  REFERENCES "claim" ("id");


ALTER TABLE "claim_person"
  ADD FOREIGN KEY ("person_id") 
  REFERENCES "person" ("id");

ALTER TABLE "claim_person"
  ADD FOREIGN KEY ("claim_id") 
  REFERENCES "claim" ("id");


ALTER TABLE "claim_publication"
  ADD FOREIGN KEY ("publication_id") 
  REFERENCES "publication" ("id");

ALTER TABLE "claim_publication"
  ADD FOREIGN KEY ("claim_id") 
  REFERENCES "claim" ("id");


ALTER TABLE "claim_quotation"
  ADD FOREIGN KEY ("quotation_id") 
  REFERENCES "quotation" ("id");

ALTER TABLE "claim_quotation"
  ADD FOREIGN KEY ("claim_id") 
  REFERENCES "claim" ("id");


ALTER TABLE "declaration"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");

ALTER TABLE "declaration"
  ADD FOREIGN KEY ("country_code") 
  REFERENCES "country" ("alpha_2");

ALTER TABLE "declaration"
  ADD FOREIGN KEY ("kind") 
  REFERENCES "declaration_kind" ("kind");

ALTER TABLE "declaration"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "declaration"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");


ALTER TABLE "declaration_person"
  ADD FOREIGN KEY ("person_id") 
  REFERENCES "person" ("id");

ALTER TABLE "declaration_person"
  ADD FOREIGN KEY ("declaration_id") 
  REFERENCES "declaration" ("id");


ALTER TABLE "declaration_quotation"
  ADD FOREIGN KEY ("quotation_id") 
  REFERENCES "quotation" ("id");

ALTER TABLE "declaration_quotation"
  ADD FOREIGN KEY ("declaration_id") 
  REFERENCES "declaration" ("id");


ALTER TABLE "journal"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "journal"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");

ALTER TABLE "journal"
  ADD FOREIGN KEY ("publisher_id") 
  REFERENCES "publisher" ("id");

ALTER TABLE "journal"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id");


ALTER TABLE "log"
  ADD FOREIGN KEY ("linked_entity_kind") 
  REFERENCES "entity_kind" ("code");

ALTER TABLE "log"
  ADD FOREIGN KEY ("user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "log"
  ADD FOREIGN KEY ("entity_kind") 
  REFERENCES "entity_kind" ("code");

ALTER TABLE "log"
  ADD FOREIGN KEY ("transaction_kind") 
  REFERENCES "transaction_kind" ("code");


ALTER TABLE "person"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "person"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "person"
  ADD FOREIGN KEY ("country_code") 
  REFERENCES "country" ("alpha_2");

ALTER TABLE "person"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");


ALTER TABLE "publication"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "publication"
  ADD FOREIGN KEY ("kind") 
  REFERENCES "publication_kind" ("kind");

ALTER TABLE "publication"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "publication"
  ADD FOREIGN KEY ("journal_id") 
  REFERENCES "journal" ("id");

ALTER TABLE "publication"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");


ALTER TABLE "publication_person"
  ADD FOREIGN KEY ("person_id") 
  REFERENCES "person" ("id");

ALTER TABLE "publication_person"
  ADD FOREIGN KEY ("publication_id") 
  REFERENCES "publication" ("id");


ALTER TABLE "publisher"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "publisher"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "publisher"
  ADD FOREIGN KEY ("country_code") 
  REFERENCES "country" ("alpha_2");

ALTER TABLE "publisher"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");


ALTER TABLE "quotation"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");

ALTER TABLE "quotation"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "quotation"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id");


ALTER TABLE "quotation_person"
  ADD FOREIGN KEY ("person_id") 
  REFERENCES "person" ("id");

ALTER TABLE "quotation_person"
  ADD FOREIGN KEY ("quotation_id") 
  REFERENCES "quotation" ("id");


ALTER TABLE "quotation_publication"
  ADD FOREIGN KEY ("publication_id") 
  REFERENCES "publication" ("id");

ALTER TABLE "quotation_publication"
  ADD FOREIGN KEY ("quotation_id") 
  REFERENCES "quotation" ("id");


ALTER TABLE "topic"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");

ALTER TABLE "topic"
  ADD FOREIGN KEY ("parent_id") 
  REFERENCES "topic" ("id");

ALTER TABLE "topic"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "topic"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id");


ALTER TABLE "topic_claim_ref"
  ADD FOREIGN KEY ("topic_id") 
  REFERENCES "topic" ("id");

ALTER TABLE "topic_claim_ref"
  ADD FOREIGN KEY ("claim_id") 
  REFERENCES "claim" ("id");


ALTER TABLE "topic_declaration_ref"
  ADD FOREIGN KEY ("topic_id") 
  REFERENCES "topic" ("id");

ALTER TABLE "topic_declaration_ref"
  ADD FOREIGN KEY ("declaration_id") 
  REFERENCES "declaration" ("id");


ALTER TABLE "topic_person_ref"
  ADD FOREIGN KEY ("topic_id") 
  REFERENCES "topic" ("id");

ALTER TABLE "topic_person_ref"
  ADD FOREIGN KEY ("person_id") 
  REFERENCES "person" ("id");


ALTER TABLE "topic_publication_ref"
  ADD FOREIGN KEY ("topic_id") 
  REFERENCES "topic" ("id");

ALTER TABLE "topic_publication_ref"
  ADD FOREIGN KEY ("publication_id") 
  REFERENCES "publication" ("id");


ALTER TABLE "topic_quotation_ref"
  ADD FOREIGN KEY ("topic_id") 
  REFERENCES "topic" ("id");

ALTER TABLE "topic_quotation_ref"
  ADD FOREIGN KEY ("quotation_id") 
  REFERENCES "quotation" ("id");


ALTER TABLE "user"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id");

ALTER TABLE "user"
  ADD FOREIGN KEY ("country_code") 
  REFERENCES "country" ("alpha_2");

ALTER TABLE "user"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code");


ALTER TABLE "user_permission"
  ADD FOREIGN KEY ("permission_code") 
  REFERENCES "permission_kind" ("code");

ALTER TABLE "user_permission"
  ADD FOREIGN KEY ("user_id") 
  REFERENCES "user" ("id");

