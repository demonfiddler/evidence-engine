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

CREATE TABLE "abbreviation" (
	"word" VARCHAR(50) NOT NULL COMMENT 'The title word, prefix or suffix',
	"is_prefix" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Whether "word" is a prefix',
	"is_suffix" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Whether "word" is a suffix',
	"abbreviation" VARCHAR(30) DEFAULT NULL COMMENT 'The abbreviation, if any, for "word"',
	"languages" VARCHAR(50) NOT NULL COMMENT 'The applicable languages',
  PRIMARY KEY ("word")
);
CREATE INDEX "abbreviation_word" ON "abbreviation" ("word");
CREATE INDEX "abbreviation_abbreviation" ON "abbreviation" ("abbreviation");

CREATE TABLE "entity" (
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'The unique entity record identifier',
  "dtype" CHAR(3) NOT NULL COMMENT 'The entity type discriminator',
  "status" CHAR(3) DEFAULT 'DRA' NOT NULL COMMENT 'The record status',
  "rating" SMALLINT DEFAULT NULL CHECK ("rating" BETWEEN 1 AND 5) COMMENT 'Quality/significance/eminence star rating, 1..5',
  "created" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'When the record was created',
  "created_by_user_id" BIGINT COMMENT 'ID of the user who created the record',
  "updated" TIMESTAMP COMMENT 'When the record was last updated',
  "updated_by_user_id" BIGINT DEFAULT NULL COMMENT 'ID of the user who last updated the record'
);
CREATE INDEX "FK_entity_dtype" ON "entity" ("dtype");
CREATE INDEX "FK_entity_status" ON "entity" ("status");
CREATE INDEX "FK_entity_created_user" ON "entity" ("created_by_user_id");
CREATE INDEX "FK_entity_updated_user" ON "entity" ("updated_by_user_id");

CREATE TABLE "claim" (
  "id" BIGINT PRIMARY KEY COMMENT 'The unique claim identifier',
  "date" DATE DEFAULT NULL COMMENT 'The date on which the claim was first made',
  "text" VARCHAR(500) NOT NULL COMMENT 'The claim text',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the claim'
);
CALL FT_CREATE_INDEX('PUBLIC', 'claim', 'text,notes');

CREATE TABLE "comment" (
  "id" BIGINT PRIMARY KEY COMMENT 'The unique comment identifier',
  "target_id" BIGINT NOT NULL COMMENT 'The ID of the target entity with which this comment is associated',
  "parent_id" BIGINT DEFAULT NULL COMMENT 'The ID of the parent comment to which this comment is a reply.',
  "text" VARCHAR(500) NOT NULL COMMENT 'The text of the comment',
  PRIMARY KEY ("id")
);
CALL FT_CREATE_INDEX('PUBLIC', 'comment', 'text');

CREATE TABLE "country" (
  "alpha_2" CHAR(2) PRIMARY KEYCOMMENT 'ISO-3166-1 alpha-2 code',
  "alpha_3" CHAR(3) NOT NULL UNIQUE COMMENT 'ISO-3166-1 alpha-3 code',
  "numeric" CHAR(3) NOT NULL UNIQUE CHECK ("numeric" REGEXP '^\d{3}$') COMMENT 'ISO-3166-1 numeric code',
  "iso_name" VARCHAR(100) NOT NULL UNIQUE COMMENT 'Official/ISO country name',
  "common_name" VARCHAR(50) NOT NULL UNIQUE COMMENT 'Common or short name',
  "year" SMALLINT NOT NULL COMMENT 'Year alpha-2 code was first assigned',
  "cc_tld" CHAR(3) NOT NULL COMMENT 'Country code top level domain',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Remarks as per Wikipedia ISO-3166-1 entry'
);
CREATE UNIQUE INDEX "country_alpha_3" ON "country" ("alpha_3");
CREATE UNIQUE INDEX "country_numeric" ON "country" ("numeric");
CREATE UNIQUE INDEX "country_common_name" ON "country" ("common_name");
CREATE UNIQUE INDEX "country_iso_name" ON "country" ("iso_name");

CREATE TABLE "declaration" (
  "id" BIGINT PRIMARY KEY COMMENT 'The unique declaration identifier',
  "kind" VARCHAR(4) NOT NULL COMMENT 'The kind of declaration',
  "date" DATE NOT NULL COMMENT 'The date the declaration was first published',
  "title" VARCHAR(100) NOT NULL COMMENT 'The declaration name or title',
  "country" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the country to which the declaration pertains',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web URL of the original declaration',
  "cached" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Flag to indicate that url content is cached on this application server',
  "signatories" VARCHAR(65535) DEFAULT NULL COMMENT 'The list of signatories, one per line',
  "signatory_count" INT DEFAULT NULL COMMENT 'The number of signatories',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the declaration'
);
CREATE INDEX "FK_declaration_declaration_kind" ON "declaration" ("kind");
CREATE INDEX "FK_declaration_country" ON "declaration" ("country");
CALL FT_CREATE_INDEX('PUBLIC', 'declaration', 'title,signatories,notes');

CREATE TABLE "declaration_kind" (
  "kind" VARCHAR(4) PRIMARY KEY COMMENT 'The declaration kind code',
  "label" VARCHAR(20) NOT NULL UNIQUE COMMENT 'Label for the declaration kind',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the declaration kind'
);
CREATE UNIQUE INDEX "declaration_kind_label" ON "declaration_kind" ("label");

CREATE TABLE "entity_kind" (
  "code" CHAR(3) PRIMARY KEY COMMENT 'Unique code for the entity kind',
  "label" VARCHAR(20) NOT NULL UNIQUE COMMENT 'Label for the entity kind'
);
CREATE UNIQUE INDEX "entity_kind_label" ON "entity_kind" ("label");

CREATE TABLE "entity_link" (
  "id" BIGINT PRIMARY KEY COMMENT 'The unique entity link identifier',
  "from_entity_id" BIGINT NOT NULL COMMENT 'The linked-from entity ID',
  "to_entity_id" BIGINT NOT NULL COMMENT 'The linked-to entity ID',
  "from_entity_locations" VARCHAR(500) DEFAULT NULL COMMENT 'Location(s) within the linked-from entity (where applicable), one per line',
  "to_entity_locations" VARCHAR(500) DEFAULT NULL COMMENT 'Location(s) within the linked-to entity (where applicable), one per line'
);

CREATE INDEX "FK_entity_link_from" ON "entity_link" ("from_entity_id");
CREATE INDEX "FK_entity_link_to" ON "entity_link" ("to_entity_id");
CALL FT_CREATE_INDEX('PUBLIC', 'entity_link', 'from_entity_locations,to_entity_locations');

CREATE TABLE "journal" (
  "id" BIGINT PRIMARY KEY COMMENT 'The journal ID',
  "title" VARCHAR(100) NOT NULL COMMENT 'The journal, etc. title',
  "abbreviation" VARCHAR(50) DEFAULT NULL COMMENT 'The official ISO 4 title abbreviation, with periods',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web link to the journal''s home page',
  "issn" CHAR(9) DEFAULT NULL UNIQUE CHECK ("issn" REGEXP '^\\d{4}-\\d{3}[\\dX]$') COMMENT 'The International Standard Serial Number',
  "publisher_id" BIGINT DEFAULT NULL COMMENT 'The ID of the publisher',
  "notes" VARCHAR(200) DEFAULT NULL COMMENT 'A brief description of the journal',
  "peer_reviewed" BOOLEAN DEFAULT NULL COMMENT 'Whether the journal publishes peer-reviewed articles',
);
CREATE UNIQUE INDEX "journal_issn" ON "journal" ("issn");
CREATE INDEX "journal_title" ON "journal" ("title");
CREATE INDEX "journal_abbreviation" ON "journal" ("abbreviation");
CREATE INDEX "FK_journal_publisher" ON "journal" ("publisher_id");
CALL FT_CREATE_INDEX('PUBLIC', 'journal', 'title,abbreviation,url,issn,notes');

CREATE TABLE "log" (
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'The log entry ID',
  "timestamp" TIMESTAMP DEFAULT current_timestamp() NOT NULL COMMENT 'The date and time at which the log entry was made',
  "user_id" BIGINT DEFAULT 0 NOT NULL COMMENT 'The ID of the user who made the change',
  "transaction_kind" CHAR(3) NOT NULL COMMENT 'The kind of change that was made',
  "entity_kind" CHAR(3) NOT NULL COMMENT 'The kind of entity affected by the change',
  "entity_id" BIGINT NOT NULL COMMENT 'The ID of the affected entity',
  "linked_entity_kind" CHAR(3) DEFAULT NULL COMMENT 'The kind of entity that was linked/unlinked',
  "linked_entity_id" BIGINT DEFAULT NULL COMMENT 'The ID of the entity that was linked/unlinked'
);
CREATE INDEX "log_user" ON "log" ("user_id");
CREATE INDEX "log_entity" ON "log" ("entity_kind","entity_id");
CREATE INDEX "log_linked_entity" ON "log" ("linked_entity_kind","linked_entity_id");
CREATE INDEX "FK_log_transaction_kind" ON "log" ("transaction_kind");

CREATE TABLE "authority_kind" (
  "code" CHAR(3) PRIMARY KEY COMMENT 'Unique authority code',
  "label" VARCHAR(10) NOT NULL UNIQUE COMMENT 'Unique authority label',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the authority'
);
CREATE UNIQUE INDEX "authority_kind_label" ON "authority_kind" ("label");

CREATE TABLE "person" (
  "id" BIGINT PRIMARY KEY COMMENT 'Unique person identifier',
  "title" VARCHAR(10) DEFAULT NULL COMMENT 'Person''s title, e.g., Prof., Dr.',
  "first_name" VARCHAR(80) NOT NULL COMMENT 'Person''s first names and/or initials',
  "nickname" VARCHAR(40) DEFAULT NULL COMMENT 'Nickname by which commonly known',
  "prefix" VARCHAR(20) DEFAULT NULL COMMENT 'Prefix to last name, e.g., van, de',
  "last_name" VARCHAR(40) NOT NULL COMMENT 'Person''s last name,  without prefix or suffix',
  "suffix" VARCHAR(16) DEFAULT NULL COMMENT 'Suffix to last name, e.g. Jr., Sr.',
  "alias" VARCHAR(40) DEFAULT NULL COMMENT 'Alternative last name',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Brief biography, notes, etc.',
  "qualifications" VARCHAR(65535) DEFAULT NULL COMMENT 'Academic qualifications',
  "country" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for country of primary professional association',
  "checked" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Set when the person''s credentials have been checked',
  "published" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Set if person has published peer-reviewed papers on climate change'
);
CREATE INDEX "person_title" ON "person" ("title");
CREATE INDEX "person_first_name" ON "person" ("first_name");
CREATE INDEX "person_last_name" ON "person" ("last_name");
CREATE INDEX "person_qualifications" ON "person" ("qualifications");
CREATE INDEX "person_country" ON "person" ("country");
CREATE INDEX "person_notes" ON "person" ("notes");
CALL FT_CREATE_INDEX('PUBLIC', 'person', 'title,first_name,nickname,prefix,last_name,suffix,alias,notes,qualifications');

CREATE TABLE "publication" (
  "id" BIGINT PRIMARY KEY COMMENT 'Unique publication ID',
  "title" VARCHAR(200) NOT NULL COMMENT 'Publication title',
  "authors" VARCHAR(2000) NOT NULL COMMENT 'List of author names',
  "journal_id" BIGINT DEFAULT NULL COMMENT 'Journal title',
  "kind" VARCHAR(6) NOT NULL COMMENT 'The kind of publication',
  "date" DATE DEFAULT NULL COMMENT 'Publication date',
  "year" SMALLINT DEFAULT NULL COMMENT 'Publication year',
	"keywords" VARCHAR(255) DEFAULT NULL COMMENT 'Keywords per publication metadata',
	"location" VARCHAR(50) DEFAULT NULL COMMENT 'The location of the relevant section within the publication',
  "abstract" VARCHAR(65535) DEFAULT NULL COMMENT 'Abstract from the article',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the publication',
  "peer_reviewed" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Whether the article was peer-reviewed',
  "doi" VARCHAR(100) DEFAULT NULL UNIQUE CHECK("doi" REGEXP '^(10\\.\\d{4,9}/(?i)[-._;()/:A-Z0-9]+)$') COMMENT 'Digital Object Identifier',
  "isbn" VARCHAR(20) DEFAULT NULL UNIQUE CHECK("isbn" REGEXP '^(?=(?:\d[- ]?){13}$|(?:\\d[- ]?){9}[\dXx]$)\\d{1,5}[- ]?\\d{1,7}[- ]?\\d{1,7}[- ]?[\\dXx]$') COMMENT 'International Standard Book Number (printed publications only)',
	"pmcid" VARCHAR(10) DEFAULT NULL UNIQUE CHECK("pmid" REGEXP '^PMC\\d{7}$') COMMENT 'The U.S. National Library of Medicine''s PubMed Central ID',
	"pmid" VARCHAR(10) DEFAULT NULL UNIQUE CHECK("pmid" REGEXP '^\\d{1,10}$') COMMENT 'The U.S. National Library of Medicine''s PubMed ID',
	"hsid" VARCHAR(12) DEFAULT NULL UNIQUE CHECK("hsid" REGEXP '^[^/]+/.+$') COMMENT 'The Corporation for National Research Initiatives''s Handle System ID',
	"arxivid" VARCHAR(15) DEFAULT NULL UNIQUE CHECK("arxivid" REGEXP '^\\d{4}\\.\\d{4,5}(v\\d+)?$') COMMENT 'Cornell University Library''s arXiv.org ID',
	"biorxivid" VARCHAR(20) DEFAULT NULL UNIQUE CHECK("biorxivid" REGEXP '^10\\.1101\\/\\d+$') COMMENT 'Cold Spring Harbor Laboratory''s bioRxiv.org ID',
	"medrxivid" VARCHAR(20) DEFAULT NULL UNIQUE CHECK("medrxivid" REGEXP '^10\\.1101\\/\\d+$') COMMENT 'Cold Spring Harbor Laboratory''s medRxiv.org ID',
	"ericid" VARCHAR(8) DEFAULT NULL UNIQUE CHECK("ericid" REGEXP '^(?i)ED\\d{6}$') COMMENT 'U.S. Department of Education''s ERIC database ID (niche)',
	"ihepid" VARCHAR(10) DEFAULT NULL UNIQUE CHECK("ihepid" REGEXP '^\\d{6,10}$') COMMENT 'CERN''s INSPIRE-HEP ID',
	"oaipmhid" VARCHAR(50) DEFAULT NULL UNIQUE CHECK("oaipmhid" REGEXP '^oai:[^:]+:[^:]+$') COMMENT 'Open Archives Initiative''s OAI-PMH ID',
	"halid" VARCHAR(20) DEFAULT NULL UNIQUE CHECK("halid" REGEXP '^hal-\\d{6,10}$') COMMENT 'CNRS (France)''s HAL ID',
	"zenodoid" VARCHAR(10) DEFAULT NULL UNIQUE CHECK("zenodoid" REGEXP '^\\d{6,10}$') COMMENT 'CERN''s Zenodo Record ID',
	"scopuseid" VARCHAR(16) DEFAULT NULL UNIQUE CHECK("scopuseid" REGEXP '^\\d{8,16}$') COMMENT 'Elsevier''s SCOPUS database EID (proprietary)',
	"wsan" VARCHAR(25) DEFAULT NULL UNIQUE CHECK("wsan" REGEXP '[A-Z0-9]{15,25}$') COMMENT 'Clarivate''s Web of Science Accession Number (UT) (proprietary)',
	"pinfoan" VARCHAR(30) DEFAULT NULL UNIQUE CHECK("pinfoan" REGEXP '^[A-Z0-9\\-]{10,30}$') COMMENT 'American Psychological Association''s PsycINFO Accession Number (proprietary/niche)',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'URL of the publication',
  "cached" BOOLEAN DEFAULT FALSE NOT NULL COMMENT 'Flag to indicate that url content is cached on this application server',
  "accessed" DATE DEFAULT NULL COMMENT 'Date a web page was accessed'
);
CREATE UNIQUE INDEX "publication_doi" ON "publication" ("doi");
CREATE INDEX "FK_publication_journal" ON "publication" ("journal_id");
CREATE INDEX "FK_publication_publication_kind" ON "publication" ("kind");
CALL FT_CREATE_INDEX('PUBLIC', 'publication', 'title,authors,abstract,notes,keywords,doi,isbn,pmid,hsid,arxivid,biorxivid,medrxivid,ericid,ihepid,oaipmhid,halid,zenodoid,scopuseid,wsan,pinfoan,url');

CREATE TABLE "publication_kind" (
  "kind" VARCHAR(10) PRIMARY KEY COMMENT 'The publication type per TY field in RIS specification',
  "label" VARCHAR(25) NOT NULL COMMENT 'Label for the publication kind'
);

CREATE TABLE "publisher" (
  "id" BIGINT PRIMARY KEY COMMENT 'The unique publisher identifier',
  "name" VARCHAR(200) NOT NULL COMMENT 'The publisher name',
  "location" VARCHAR(50) DEFAULT NULL COMMENT 'The publisher location',
  "country" CHAR(2) DEFAULT NULL COMMENT 'The ISO-3166-1 alpha-2 code for the publisher''s country',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'URL of publisher''s home page',
  "journal_count" INT DEFAULT NULL COMMENT 'The number of journals published',
	"notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Notes on the publisher'
);
CREATE INDEX "FK_publisher_country" ON "publisher" ("country");
CREATE INDEX "publisher_name" ON "publisher" ("name");
CALL FT_CREATE_INDEX('PUBLIC', 'publisher', 'name,location,url,notes');

CREATE TABLE "quotation" (
  "id" BIGINT PRIMARY KEY COMMENT 'Unique quotation identifier',
  "quotee" VARCHAR(50) NOT NULL COMMENT 'The person(s) who made the quotation',
  "text" VARCHAR(1000) NOT NULL COMMENT 'The quotation text',
  "date" DATE DEFAULT NULL COMMENT 'The quotation date',
  "source" VARCHAR(200) DEFAULT NULL COMMENT 'The source of the quotation',
  "url" VARCHAR(200) DEFAULT NULL COMMENT 'Web url to the quotation',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the quotation'
);
CREATE INDEX "quotation_quotee" ON "quotation" ("quotee");
CALL FT_CREATE_INDEX('PUBLIC', 'quotation', 'quotee,text,source,url,notes');

CREATE TABLE "status_kind" (
  "code" CHAR(3) PRIMARY KEY COMMENT 'The status code',
  "label" VARCHAR(20) NOT NULL UNIQUE COMMENT 'The status label',
  "description" VARCHAR(100) NOT NULL COMMENT 'Defines the meaning of the status code'
);
CREATE UNIQUE INDEX "status_kind_label" ON "status_kind" ("label");

CREATE TABLE "topic" (
  "id" BIGINT PRIMARY KEY COMMENT 'The unique topic identifier',
  "label" VARCHAR(50) NOT NULL COMMENT 'The topic name/label',
  "description" VARCHAR(500) DEFAULT NULL COMMENT 'Notes on when to use the topic',
  "parent_id" BIGINT DEFAULT NULL COMMENT 'The parent topic ID'
);
CREATE INDEX "FK_topic_topic" ON "topic" ("parent_id");
CALL FT_CREATE_INDEX('PUBLIC', 'topic', 'label,description');

CREATE TABLE "transaction_kind" (
  "code" CHAR(3) PRIMARY KEY COMMENT 'The transaction code',
  "label" VARCHAR(20) NOT NULL UNIQUE COMMENT 'A UI label for the transaction kind',
  "description" VARCHAR(50) NOT NULL COMMENT 'Description of the transaction kind'
);
CREATE UNIQUE INDEX "transaction_kind_label" ON "transaction_kind" ("label");

-- Spring also recognises locked, expired and credentials_expired fields.
CREATE TABLE "user" (
  "id" BIGINT PRIMARY KEY COMMENT 'The unique system-assigned user identifier',
  "username" VARCHAR(50) NOT NULL UNIQUE COMMENT 'The unique user-assigned user name',
  "password" VARCHAR(68) NOT NULL COMMENT 'Bcrypt hash of the user''s password',
	"enabled" BOOLEAN DEFAULT TRUE NOT NULL COMMENT 'Whether the user account is enabled',
  "first_name" VARCHAR(50) NOT NULL COMMENT 'The user''s first name',
  "last_name" VARCHAR(50) NOT NULL COMMENT 'The user''s last name',
  "email" VARCHAR(100) DEFAULT NULL UNIQUE COMMENT 'The user''s email address, used for sign-in',
  "country" CHAR(2) DEFAULT NULL COMMENT 'ISO-3166-1 alpha-2 code for user''s country of residence',
  "notes" VARCHAR(65535) DEFAULT NULL COMMENT 'Added notes about the user'
);
CREATE UNIQUE INDEX "username" ON "user" ("username");
CREATE INDEX "FK_user_country" ON "user" ("country");
CALL FT_CREATE_INDEX('PUBLIC', 'user', 'username,first_name,last_name,email,notes');

-- Additional tables required by Spring Security
CREATE TABLE "user_authority" (
  "user_id" BIGINT NOT NULL COMMENT 'The user ID',
  "username" VARCHAR(50) NOT NULL COMMENT 'The login user name',
  "authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
  UNIQUE ("user_id", "username", "authority")
);
CREATE UNIQUE INDEX "user_authority" ON "user_authority" ("user_id", "username", "authority");

CREATE TABLE "group" (
	"id" BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'The unique system-assigned group identifier',
	"groupname" VARCHAR(50) NOT NULL UNIQUE COMMENT 'The group name'
);
CREATE UNIQUE INDEX "groupname" ON "group" ("groupname");

CREATE TABLE "group_authority" (
  "group_id" BIGINT NOT NULL COMMENT 'ID of a group',
  "authority" CHAR(3) NOT NULL COMMENT 'The granted authority code',
  UNIQUE ("group_id", "authority")
);
CREATE UNIQUE INDEX "group_authority" ON "group_authority" ("group_id", "authority");

CREATE TABLE "group_user" (
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'The unique system-assigned identifier',
  "group_id" BIGINT NOT NULL COMMENT 'ID of the group to which user belongs',
  "username" VARCHAR(50) NOT NULL COMMENT 'The login user name',
  UNIQUE ("username", "group_id")
);
CREATE UNIQUE INDEX "group_user" ON "group_user" ("username", "group_id");

CREATE TABLE "persistent_login" (
    "series" VARCHAR(64) PRIMARY KEY COMMENT 'Encoded random number used to detect cookie stealing',
    "username" VARCHAR(64) NOT NULL COMMENT 'The authenticated username',
    "token" VARCHAR(64) NOT NULL COMMENT 'The authentication token returned as a cookie',
    "last_used" TIMESTAMP NOT NULL COMMENT 'The date/time at which the token was last used'
);

ALTER TABLE "entity"
  ADD FOREIGN KEY ("dtype") 
  REFERENCES "entity_kind" ("code")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "entity"
  ADD FOREIGN KEY ("status") 
  REFERENCES "status_kind" ("code")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "entity"
  ADD FOREIGN KEY ("created_by_user_id") 
  REFERENCES "user" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "entity"
  ADD FOREIGN KEY ("updated_by_user_id") 
  REFERENCES "user" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "claim"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "comment"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "comment"
  ADD FOREIGN KEY ("target_id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "comment"
  ADD FOREIGN KEY ("parent_id")
  REFERENCES "comment" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "declaration"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "declaration"
  ADD FOREIGN KEY ("country") 
  REFERENCES "country" ("alpha_2")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "declaration"
  ADD FOREIGN KEY ("kind") 
  REFERENCES "declaration_kind" ("kind")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "entity_link"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "entity_link"
  ADD FOREIGN KEY ("from_entity_id") 
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "entity_link"
  ADD FOREIGN KEY ("to_entity_id") 
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "group"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "group_authority"
  ADD FOREIGN KEY ("group_id")
  REFERENCES "group" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "group_authority"
  ADD FOREIGN KEY ("authority")
  REFERENCES "authority_kind" ("code")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "group_user"
  ADD FOREIGN KEY ("group_id")
  REFERENCES "group" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "group_user"
  ADD FOREIGN KEY ("username")
  REFERENCES "user" ("username")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "journal"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "journal"
  ADD FOREIGN KEY ("publisher_id") 
  REFERENCES "publisher" ("id")
  ON UPDATE CASCADE
  ON DELETE SET NULL;


ALTER TABLE "log"
  ADD FOREIGN KEY ("transaction_kind")
  REFERENCES "transaction_kind" ("code")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "log"
  ADD FOREIGN KEY ("user_id")
  REFERENCES "user" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "log"
  ADD FOREIGN KEY ("entity_id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "log"
  ADD FOREIGN KEY ("entity_kind")
  REFERENCES "entity_kind" ("code")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "log"
  ADD FOREIGN KEY ("linked_entity_id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "log"
  ADD FOREIGN KEY ("linked_entity_kind")
  REFERENCES "entity_kind" ("code")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "person"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "person"
  ADD FOREIGN KEY ("country") 
  REFERENCES "country" ("alpha_2")
  ON UPDATE CASCADE
  ON DELETE SET NULL;


ALTER TABLE "publication"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "publication"
  ADD FOREIGN KEY ("kind") 
  REFERENCES "publication_kind" ("kind")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "publication"
  ADD FOREIGN KEY ("journal_id") 
  REFERENCES "journal" ("id")
  ON UPDATE CASCADE
  ON DELETE SET NULL;


ALTER TABLE "publisher"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "publisher"
  ADD FOREIGN KEY ("country") 
  REFERENCES "country" ("alpha_2")
  ON UPDATE CASCADE
  ON DELETE SET NULL;


ALTER TABLE "quotation"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "topic"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "topic"
  ADD FOREIGN KEY ("parent_id") 
  REFERENCES "topic" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;


ALTER TABLE "user"
  ADD FOREIGN KEY ("id")
  REFERENCES "entity" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "user"
  ADD FOREIGN KEY ("country") 
  REFERENCES "country" ("alpha_2")
  ON UPDATE CASCADE
  ON DELETE SET NULL;


ALTER TABLE "user_authority"
  ADD FOREIGN KEY ("user_id")
  REFERENCES "user" ("id")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "user_authority"
  ADD FOREIGN KEY ("username")
  REFERENCES "user" ("username")
  ON UPDATE CASCADE
  ON DELETE CASCADE;

ALTER TABLE "user_authority"
  ADD FOREIGN KEY ("authority")
  REFERENCES "authority_kind" ("code")
  ON UPDATE CASCADE
  ON DELETE CASCADE;
