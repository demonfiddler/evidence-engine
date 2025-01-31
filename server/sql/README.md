# Database Scripts

These SQL files are a mixture of required database initialisation scripts and various experiments conducted while developing the database schema.

## Production & Test Initialisation Scripts
NOTE: the database schema and required initialisation scripts are part of the product and may be found at /server/src/main/resources/db:
| Script | Purpose |
| ------ | ------- |
| [data.sql](../src/main/resources/db/data.sql) | Initial data to populate lookup and reference tables. |
| [schema-h2.sql](../src/main/resources/db/schema-h2.sql) | DDL for the H2 database. |
| [schema-mariadb.sql](../src/main/resources/db/schema-mariadb.sql) | DDL for the MariaDB database |

## Initialisation Scripts

These scripts populate various lookup tables, although note that all required initialisation is performed by [data.sql](../src/main/resources/db/data.sql) as described above.

| Script | Purpose |
| ------ | ------- |
| [initdb-mariadb.sql](initdb-mariadb.sql) | Sets ANSI_QUOTE mode to enable identifier double-quoting (as opposed to the default backtick). |
| [populatedb-country.sql](populatedb-country.sql) | Populates the "country" lookup table. |
| [populatedb-declaration_kind.sql](populatedb-declaration_kind.sql) | Populates the "declaration_kind" lookup table. |
| [populatedb-entity_kind.sql](populatedb-entity_kind.sql) | Populates the "entity_kind" lookup table. |
| [populatedb-permission_kind.sql](populatedb-permission_kind.sql) | Populates the "permission_kind" lookup table. |
| [populatedb-publication_kind.sql](populatedb-publication_kind.sql) | Populates the "publication_kind" lookup table. |
| [populatedb-publisher.sql](populatedb-publisher.sql) | Populates the "publisher" lookup table with known publishers. |
| [populatedb-status_kind.sql](populatedb-status_kind.sql) | Populates the "status_kind" lookup table. |
| [populatedb-transaction_kind.sql](populatedb-transaction_kind.sql) | Populates the "transaction_kind" lookup table. |
| [populatedb-user.sql](populatedb-user.sql) | Populates the "user" lookup table with a single 'root' user with all permissions. |

## Experimental Scripts

These include early attempts at copying data from the Climate Science database into the Evidence Engine schema. Some are works in progress and may not actually work.

| Script | Purpose |
| ------ | ------- |
| [drop-tables.sql](drop-tables.sql) | Drops all tables. |
| [populatedb-declaration.sql](populatedb-declaration.sql) | Inserts declarations as per the Climate Science database. |
| [populatedb-person.sql](populatedb-person.sql) | Copies person records from the Climate Science database. |
| [populatedb-publication.sql](populatedb-publication.sql) | Copies publication records from the Climate Science database. |
| [populatedb-test-data.sql](populatedb-test-data.sql) | Attempts to replicate the records created by running the integration tests in the client project. |
| [populatedb-topic.sql](populatedb-topic.sql) | Initial thoughts on a topic hierarchy. |
