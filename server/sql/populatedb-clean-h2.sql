-- Deletes all entities other than Users. RI constraints will cascade-delete associated entity links and log entries.
DELETE FROM "entity" WHERE "dtype" <> 'USR';

-- Reset the AUTO-GENERATED id field sequence number for the "entity" & "log" tables.
SET @max_id = (SELECT MAX("id") FROM "entity");
ALTER TABLE "entity" ALTER COLUMN "id" RESTART WITH @max_id + 1;
ALTER TABLE "log" ALTER COLUMN "id" RESTART WITH 1;

-- Clear all persistent login records.
DELETE FROM "persistent_login" WHERE "username" IS NOT NULL;