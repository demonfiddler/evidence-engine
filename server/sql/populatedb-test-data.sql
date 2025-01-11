-- These records are those created by the entity integration tests.

INSERT INTO "claim"
	("status", "date", "text", "notes")
VALUES
	('DEL', CURRENT_DATE(), 'Updated Test claim', 'Updated Test notes'),
	('DRA', CURRENT_DATE(), 'CLAIM ONE', 'Notes #1'),
	('DRA', CURRENT_DATE(), 'Claim two', 'Notes #2'),
	('DRA', CURRENT_DATE(), 'CLAIM THREE', null),
	('DRA', CURRENT_DATE(), 'Claim four', 'Notes #4'),
	('DRA', CURRENT_DATE(), 'CLAIM FIVE', 'Notes #5 (filtered)'),
	('DRA', CURRENT_DATE(), 'Claim six', null),
	('DRA', CURRENT_DATE(), 'CLAIM SEVEN', 'Notes #7 (filtered)'),
	('DRA', CURRENT_DATE(), 'Claim eight', 'Notes #8 (filtered)');

INSERT INTO "declaration"
	("status", "kind", "date", "title", "country_code", "url", "signatories", "notes")
VALUES
	('DEL', 'OPLE', CURRENT_DATE(), 'Updated test title', 'US', 'https://updated-domain.tld', 'Martin Phillips\nOscar Diamentes\nLouise Pendros', 'Updated test notes'),
	('DRA', 'DECL', CURRENT_DATE(), 'DECLARATION ONE', null, null, null, 'Notes #1'),
	('DRA', 'DECL', CURRENT_DATE(), 'Declaration two', null, null, null, 'Notes #2'),
	('DRA', 'DECL', CURRENT_DATE(), 'DECLARATION THREE', null, null, null, null),
	('DRA', 'DECL', CURRENT_DATE(), 'Declaration four', null, null, null, 'Notes #4'),
	('DRA', 'DECL', CURRENT_DATE(), 'DECLARATION FIVE', null, null, null, 'Notes #5 (filtered)'),
	('DRA', 'DECL', CURRENT_DATE(), 'Declaration six', null, null, null, null),
	('DRA', 'DECL', CURRENT_DATE(), 'DECLARATION SEVEN', null, null, null, 'Notes #7 (filtered)'),
	('DRA', 'DECL', CURRENT_DATE(), 'Declaration eight', null, null, null, 'Notes #8 (filtered)');

INSERT INTO "journal"
	("status", "title", "abbreviation", "url", "issn", "notes")
VALUES
	('DEL', 'Updated Test Title', 'Upd Tst Jour', 'http://updated-domain.org', '1234-5678', 'Updated Test notes'),
	('DRA', 'JOURNAL ONE', 'Jnl 1', null, null, 'Notes #1'),
	('DRA', 'Journal two', 'Jnl 2', null, null, 'Notes #2'),
	('DRA', 'JOURNAL three', 'Jnl 3', null, null, null),
	('DRA', 'Journal four', 'Jnl 4', null, null, 'Notes #4'),
	('DRA', 'JOURNAL five', 'Jnl 5', null, null, 'Notes #5 (filtered)'),
	('DRA', 'Journal six', 'Jnl 6', null, null, null),
	('DRA', 'JOURNAL seven', 'Jnl 7', null, null, 'Notes #7 (filtered)'),
	('DRA', 'Journal eight', 'Jnl 8', null, null, 'Notes #8 (filtered)');

INSERT INTO "person"
	("status", "first_name", "last_name", "alias", "notes", "rating", "checked", "published")
VALUES
	('DEL', 'Joanne', 'Smythe', 'Smutt', 'Updated test notes', 4, TRUE, TRUE),
	('DRA', 'Heidi', 'Andrews', 'Z', 'Notes #1', 1, TRUE, TRUE),
	('DRA', 'Gary', 'Bosworth', null, 'NOTES #2', 1, TRUE, TRUE),
	('DRA', 'Fiona', 'Charlton', 'X', null, 1, TRUE, TRUE),
	('DRA', 'Eric', 'Douglas', null, 'NOTES #4', 1, TRUE, TRUE),
	('DRA', 'Desmond', 'Edwards', 'v', 'Notes #5 (filtered)', 1, TRUE, TRUE),
	('DRA', 'Charles', 'Farquhar', null, null, 1, TRUE, TRUE),
	('DRA', 'Beth', 'Gibson', 't', 'Notes #7 (filtered)', 1, TRUE, TRUE),
	('DRA', 'Alison', 'Heath', null, 'NOTES #8 (filtered)', 1, TRUE, TRUE);

INSERT INTO "publication"
	("status", "kind", "title", "authors", "date", "year", "abstract", "notes", "peer_reviewed", "doi", "isbn", "url", "cached", "accessed")
VALUES
	('DEL', 'BOOK', 'Updated test title', U&'Joanna Smith\000AJane Doe', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Updated test abstract', 'Updated test notes', TRUE, '10.1718/73656', '978-1-7235-2714-2', 'http://updated-domain.tld', TRUE, CURRENT_DATE()),
	('DRA', 'JOUR', 'PUBLICATION ONE', 'Author one', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Abstract one', 'Notes #1', FALSE, null, null, null, FALSE, null),
	('DRA', 'JOUR', 'Publication two', 'Author two', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Abstract two', 'Notes #2', FALSE, null, null, null, FALSE, null),
	('DRA', 'JOUR', 'PUBLICATION THREE', 'Author three', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Abstract three', null, FALSE, null, null, null, FALSE, null),
	('DRA', 'JOUR', 'Publication four', 'Author four', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Abstract four', 'Notes #4', FALSE, null, null, null, FALSE, null),
	('DRA', 'JOUR', 'PUBLICATION FIVE', 'Author five', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Abstract five', 'Notes #5', FALSE, null, null, null, FALSE, null),
	('DRA', 'JOUR', 'Publication six', 'Author six', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Abstract six', null, FALSE, null, null, null, FALSE, null),
	('DRA', 'JOUR', 'PUBLICATION SEVEN', 'Author seven', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Abstract seven', 'Notes #7', FALSE, null, null, null, FALSE, null),
	('DRA', 'JOUR', 'Publication eight', 'Author eight', CURRENT_DATE(), EXTRACT(YEAR FROM CURRENT_DATE()), 'Abstract eight', 'Notes #8', FALSE, null, null, null, FALSE, null);

INSERT INTO "publisher"
	("status", "name", "location", "url", "journal_count")
VALUES
	('DEL', 'Updated test name', 'Updated test location', 'http://updated-domain.org', 3),
	('DRA', 'PUBLISHER ONE', 'Location #1', null, null),
	('DRA', 'Publisher two', 'Location #2', null, null),
	('DRA', 'PUBLISHER THREE', null, null, null),
	('DRA', 'Publisher four', 'Location #4', null, null),
	('DRA', 'PUBLISHER FIVE', 'Location #5 (filtered)', null, null),
	('DRA', 'Publisher six', null, null, null),
	('DRA', 'PUBLISHER SEVEN', 'Location #7 (filtered)', null, null),
	('DRA', 'Publisher eight', 'Location #8 (filtered)', null, null);

INSERT INTO "quotation"
	("status", "quotee", "date", "text", "source", "url", "notes")
VALUES
	('DEL', 'John Doe', CURRENT_DATE(), 'Updated test text', 'Updated test source', 'http://updated-domain.tld', 'Updated test notes'),
	('DRA', 'Quotee one', CURRENT_DATE(), 'QUOTATION ONE', null, null, 'Notes #1'),
	('DRA', 'Quotee two', CURRENT_DATE(), 'Quotation two', null, null, 'Notes #2'),
	('DRA', 'Quotee three', CURRENT_DATE(), 'QUOTATION ONE', null, null, null),
	('DRA', 'Quotee four', CURRENT_DATE(), 'Quotation two', null, null, 'Notes #4'),
	('DRA', 'Quotee five', CURRENT_DATE(), 'QUOTATION ONE', null, null, 'Notes #5 (filtered)'),
	('DRA', 'Quotee six', CURRENT_DATE(), 'Quotation two', null, null, null),
	('DRA', 'Quotee seven', CURRENT_DATE(), 'QUOTATION ONE', null, null, 'Notes #7 (filtered)'),
	('DRA', 'Quotee eight', CURRENT_DATE(), 'Quotation two', null, null, 'Notes #8 (filtered)');

INSERT INTO "topic"
	("status", "parent_id", "label", "description")
VALUES
	('DEL', null, 'Updated parent label', 'Updated parent description');
SET @parent_id = (SELECT "id" FROM "topic" WHERE "label" = 'Updated parent label');
INSERT INTO "topic"
	("status", "parent_id", "label", "description")
VALUES
	('DEL', @parent_id, 'Updated child label', 'Updated child description'),
	('DRA', null, 'TOPIC ONE', 'Description #1'),
	('DRA', null, 'Topic two', 'Description #2'),
	('DRA', null, 'TOPIC THREE', null),
	('DRA', null, 'Topic four', 'Description #4'),
	('DRA', null, 'TOPIC FIVE', 'Description #5 (filtered)'),
	('DRA', null, 'Topic six', null),
	('DRA', null, 'TOPIC SEVEN', 'Description #7 (filtered)'),
	('DRA', null, 'Topic eight', 'Description #8 (filtered)');
