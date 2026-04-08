SELECT "dtype" AS "entity_kind", "status", COUNT(*) AS "count"
FROM "entity"
GROUP BY "dtype", "status";