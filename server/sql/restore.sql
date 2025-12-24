-- First, all existing entities must be deleted
DELETE FROM "entity";

-- Static lookup tables

-- All existing lookup records must be deleted
DELETE FROM "abbreviation";
DELETE FROM "authority_kind";
DELETE FROM "country";
DELETE FROM "declaration_kind";
DELETE FROM "entity_kind";
DELETE FROM "publication_kind";
DELETE FROM "status_kind";
DELETE FROM "transaction_kind";

CALL import_table_from_csv('abbreviation', CONCAT(:in_dir, 'abbreviation.csv'));
CALL import_table_from_csv('authority_kind', CONCAT(:in_dir, 'authority_kind.csv'));
CALL import_table_from_csv('country', CONCAT(:in_dir, 'country.csv'));
CALL import_table_from_csv('declaration_kind', CONCAT(:in_dir, 'declaration_kind.csv'));
CALL import_table_from_csv('entity_kind', CONCAT(:in_dir, 'entity_kind.csv'));
CALL import_table_from_csv('publication_kind', CONCAT(:in_dir, 'publication_kind.csv'));
CALL import_table_from_csv('status_kind', CONCAT(:in_dir, 'status_kind.csv'));
CALL import_table_from_csv('transaction_kind', CONCAT(:in_dir, 'transaction_kind.csv'));

-- Application data tables

-- Temporarily disable referential integrity checks to allow insertion of entity and user records,
-- which contain circular references.
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
CALL import_table_from_csv('entity', CONCAT(:in_dir, 'entity.csv'));
CALL import_table_from_csv('user', CONCAT(:in_dir, 'user.csv'));
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;

CALL import_table_from_csv('user_authority', CONCAT(:in_dir, 'user_authority.csv'));
CALL import_table_from_csv('group', CONCAT(:in_dir, 'group.csv'));
CALL import_table_from_csv('group_authority', CONCAT(:in_dir, 'group_authority.csv'));
CALL import_table_from_csv('group_user', CONCAT(:in_dir, 'group_user.csv'));

CALL import_table_from_csv('entity_link', CONCAT(:in_dir, 'entity_link.csv'));
CALL import_table_from_csv('comment', CONCAT(:in_dir, 'comment.csv'));
CALL import_table_from_csv('log', CONCAT(:in_dir, 'log.csv'));

CALL import_table_from_csv('publisher', CONCAT(:in_dir, 'publisher.csv'));
CALL import_table_from_csv('journal', CONCAT(:in_dir, 'journal.csv'));

CALL import_table_from_csv('claim', CONCAT(:in_dir, 'claim.csv'));
CALL import_table_from_csv('declaration', CONCAT(:in_dir, 'declaration.csv'));
CALL import_table_from_csv('person', CONCAT(:in_dir, 'person.csv'));
CALL import_table_from_csv('publication', CONCAT(:in_dir, 'publication.csv'));
CALL import_table_from_csv('quotation', CONCAT(:in_dir, 'quotation.csv'));
CALL import_table_from_csv('topic', CONCAT(:in_dir, 'topic.csv'));