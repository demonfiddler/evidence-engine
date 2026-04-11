-- Deletes all entities other than Users. RI constraints will cascade-delete associated entity links and log entries.
DELETE FROM "entity" WHERE "dtype" <> 'USR';

-- Reset the AUTO-GENERATED id field sequence number for the "entity" & "log" tables.
-- ALTER TABLE "entity" AUTO_INCREMENT = <max_entity_id>;
-- ALTER TABLE "log" AUTO_INCREMENT = <max_log_id>;

-- Clear all persistent login records.
DELETE FROM "persistent_login" WHERE "username" IS NOT NULL;