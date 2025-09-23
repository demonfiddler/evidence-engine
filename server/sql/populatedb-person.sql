INSERT INTO "evidence_engine"."person"
	   ("rating", "title", "first_name", "nickname", "prefix", "last_name", "suffix", "alias", "notes", "qualifications", "country_code", "checked", "published")
SELECT "RATING", "TITLE", "FIRST_NAME", "NICKNAME", "PREFIX", "LAST_NAME", "SUFFIX", "ALIAS", "DESCRIPTION", "QUALIFICATIONS", ec."alpha_2", "CHECKED", "PUBLISHED"
FROM "climate"."person" cp
LEFT JOIN "evidence_engine"."country" ec
ON cp."COUNTRY" = ec."common_name";