-- Selects all entity bodies that match an entity header with the wrong entity kind (dtype)

SELECT cm."id", 'CLA' AS "body_entity_kind", e."dtype" AS "header_entity_kind"
FROM "claim" cm
JOIN "entity" e
ON e."id" = cm."id"
WHERE e."dtype" <> 'CLA'
UNION
SELECT ct."id", 'COM', e."dtype"
FROM "comment" ct
JOIN "entity" e
ON e."id" = ct."id"
WHERE e."dtype" <> 'COM'
UNION
SELECT e."id", 'DEC', e."dtype"
FROM "declaration" d
JOIN "entity" e
ON e."id" = d."id"
WHERE e."dtype" <> 'DEC'
UNION
SELECT e."id", 'LNK', e."dtype"
FROM "entity_link" el
JOIN "entity" e
ON e."id" = el."id"
WHERE e."dtype" <> 'LNK'
UNION
SELECT e."id", 'GRP', e."dtype"
FROM "group" g
JOIN "entity" e
ON e."id" = g."id"
WHERE e."dtype" <> 'GRP'
UNION
SELECT e."id", 'JOU', e."dtype"
FROM "entity" e
JOIN "journal" j
ON j."id" = e."id"
WHERE e."dtype" <> 'JOU'
UNION
SELECT e."id", 'PER', e."dtype"
FROM "person" pe
JOIN "entity" e
ON e."id" = pe."id"
WHERE e."dtype" <> 'PER'
UNION
SELECT e."id", 'PUB', e."dtype"
FROM "publication" pn
JOIN "entity" e
ON e."id" = pn."id"
WHERE e."dtype" <> 'PUB'
UNION
SELECT e."id", 'PBR', e."dtype"
FROM "publisher" pr
JOIN "entity" e
ON pr."id" = e."id"
WHERE e."dtype" <> 'PBR'
UNION
SELECT e."id", 'QUO', e."dtype"
FROM "quotation" q
JOIN "entity" e
ON e."id" = q."id"
WHERE e."dtype" <> 'QUO'
UNION
SELECT e."id", 'TOP', e."dtype"
FROM "topic" t
JOIN "entity" e
ON e."id" = t."id"
WHERE e."dtype" <> 'TOP'
UNION
SELECT e."id", 'USR', e."dtype"
FROM "user" u
JOIN "entity" e
ON e."id" = u."id"
WHERE e."dtype" <> 'USR'
;