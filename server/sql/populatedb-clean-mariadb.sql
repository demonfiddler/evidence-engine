-- Deletes all entities other than Users. RI constraints will cascade-delete associated entity links and log entries.
DELETE FROM "entity" WHERE "dtype" <> 'USR';

-- Reset the AUTO-GENERATED id field sequence number for the "entity" & "log" tables.
-- SET @max_id = (SELECT MAX("id") FROM "entity");
-- ALTER TABLE "entity" AUTO_INCREMENT = @max_id + 1;
-- AUTO_INCREMENT = <expression> is not valid MariaDB syntax, so hard-code the value.
ALTER TABLE "entity" AUTO_INCREMENT = 5;
ALTER TABLE "log" AUTO_INCREMENT = 1;

-- Clear all persistent login records.
DELETE FROM "persistent_login" WHERE "username" IS NOT NULL;