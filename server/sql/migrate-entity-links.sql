-- SQL script to migrate entity links to match the new link direction rules.
-- See https://github.com/demonfiddler/evidence-engine/issues/86.

UPDATE "entity_link" el
JOIN "entity" fe
ON fe."id" = el."from_entity_id"
JOIN "entity" te
ON te."id" = el."to_entity_id"
SET
	"from_entity_id" = "to_entity_id",
	"to_entity_id" = "from_entity_id",
	"from_entity_locations" = "to_entity_locations",
	"to_entity_locations" = "from_entity_locations"
WHERE
	NOT (fe."dtype" = 'DEC' AND te."dtype" = 'QUO');

UPDATE "config"
SET "value" = '3'
WHERE "property" = 'schema_version';
