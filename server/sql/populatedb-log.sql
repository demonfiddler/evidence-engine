-- Substitute correct values for 'XXX' and 'xxxxxxxx'.
INSERT INTO "log"
	("timestamp", "user_id",            "transaction_kind", "entity_kind", "entity_id")
SELECT
	 "created",   "created_by_user_id", 'CRE',              'XXX',         "id"
FROM "xxxxxxxx";