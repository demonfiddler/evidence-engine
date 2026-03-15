SELECT q.*, ele."from_entity_kind", ele."from_entity_id"
FROM "quotation" q
LEFT JOIN "entity_link_entity_vw" ele
ON q."id" = ele."to_entity_id"
AND ele."from_entity_kind" = 'CLA'
WHERE ele."from_entity_kind" IS NULL;