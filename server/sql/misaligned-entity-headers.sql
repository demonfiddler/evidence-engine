-- Selects all entity headers that match an entity body in the wrong table

SELECT e."id", e."dtype" AS "entity_kind"
FROM "entity" e
LEFT JOIN "claim" cm
ON cm."id" = e."id"
WHERE e."dtype" = 'CLA' AND cm."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "comment" ct
ON ct."id" = e."id"
WHERE e."dtype" = 'COM' AND ct."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "declaration" d
ON d."id" = e."id"
WHERE e."dtype" = 'DEC' AND d."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "entity_link" el
ON el."id" = e."id"
WHERE e."dtype" = 'LNK' AND el."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "group" g
ON g."id" = e."id"
WHERE e."dtype" = 'GRP' AND g."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "journal" j
ON j."id" = e."id"
WHERE e."dtype" = 'JOU' AND j."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "person" pe
ON pe."id" = e."id"
WHERE e."dtype" = 'PER' AND pe."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "publication" pn
ON pn."id" = e."id"
WHERE e."dtype" = 'PUB' AND pn."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "publisher" pr
ON pr."id" = e."id"
WHERE e."dtype" = 'PBR' AND pr."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "quotation" q
ON q."id" = e."id"
WHERE e."dtype" = 'QUO' AND q."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "topic" t
ON t."id" = e."id"
WHERE e."dtype" = 'TOP' AND t."id" IS NULL
UNION
SELECT e."id", e."dtype"
FROM "entity" e
LEFT JOIN "user" u
ON u."id" = e."id"
WHERE e."dtype" = 'USR' AND u."id" IS NULL
;