# Database Scripts

These SQL files are a mixture of required database initialisation scripts and various experiments conducted while developing the database schema.
Some of these scripts have been archived to disk and deleted from version control.

## Production & Test Initialisation Scripts
NOTE: the database schema and required initialisation scripts are part of the product and may be found at /server/src/main/resources/db:
| Script | Purpose |
| ------ | ------- |
| [data.sql](../src/main/resources/db/data.sql) | Initial data to populate lookup and reference tables. |
| [schema-h2.sql](../src/main/resources/db/schema-h2.sql) | DDL for the H2 database. |
| [schema-mariadb.sql](../src/main/resources/db/schema-mariadb.sql) | DDL for the MariaDB database |

## Experimental Scripts

These include early attempts at copying data from the Climate Science database into the Evidence Engine schema. Some are works in progress and may not actually work.

| Script | Purpose |
| ------ | ------- |
| [backup.sql](backup.sql) | A manually executable version of the system backup facility. |
| [restore.sql](restore.sql) | A manually executable version of the system restore facility. |
| [drop-tables.sql](drop-tables.sql) | Drops all tables. |
| [populatedb-declaration.sql](populatedb-declaration.sql) | Inserts declarations as per the Climate Science database. |
| [populatedb-person.sql](populatedb-person.sql) | Copies person records from the Climate Science database. |
| [populatedb-publication.sql](populatedb-publication.sql) | Copies publication records from the Climate Science database. |
