-- Selects entity records with no corresponding joined table.
-- Clean-up examples:
-- DELETE FROM "entity"
-- WHERE "id" IN (10277,10329,10340,10451,10430,10178,10186,10188,10203,10206,10208,10209,10211,10213,10215,10217,10219,10221,10226,10230,10232,10146,10240,10296,10301,10373,10383);
-- WHERE "id" IN (1042,6147,6154,6167,6220,6246,6248,6302,6366,6400,9945,9948);
-- WHERE "id" IN (1043,6148,6155,6168,6221,6247,6249,6303,6367,6401,6511,6512,6513,6517,6526,6527,6564,6565,6566,6567,6583,6584,6585,6586,6627,7370,8692,9891,9949,9950,10003,10009,10010,10043,10064,10065,10094,10112,10113,10114,10137,10138);

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
;