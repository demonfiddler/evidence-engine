-- Selects entity records with no corresponding joined table.

SELECT e."id", e."dtype" AS "entity_kind", e."status"
FROM "entity" e
LEFT JOIN "claim" cm
ON cm."id" = e."id"
LEFT JOIN "comment" ct
ON ct."id" = e."id"
LEFT JOIN "declaration" d
ON d."id" = e."id"
LEFT JOIN "entity_link" el
ON el."id" = e."id"
LEFT JOIN "group" g
ON g."id" = e."id"
LEFT JOIN "journal" j
ON j."id" = e."id"
LEFT JOIN "person" pe
ON pe."id" = e."id"
LEFT JOIN "publication" pn
ON pn."id" = e."id"
LEFT JOIN "publisher" pr
ON pr."id" = e."id"
LEFT JOIN "quotation" q
ON q."id" = e."id"
LEFT JOIN "topic" t
ON t."id" = e."id"
LEFT JOIN "user" u
ON u."id" = e."id"
WHERE cm."id" IS NULL
  AND ct."id" IS NULL
  AND d."id" IS NULL
  AND el."id" IS NULL
  AND g."id" IS NULL
  AND j."id" IS NULL
  AND pe."id" IS NULL
  AND pn."id" IS NULL
  AND pr."id" IS NULL
  AND q."id" IS NULL
  AND t."id" IS NULL
  AND u."id" IS NULL
ORDER BY "entity_kind", "id";