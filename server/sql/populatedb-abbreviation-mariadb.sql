DELETE FROM "abbreviation";
LOAD DATA LOCAL INFILE 'ltwa_current.csv'
    INTO TABLE "abbreviation"
    FIELDS TERMINATED BY '\t'
    OPTIONALLY ENCLOSED BY '"'
    ESCAPED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (`word`, `abbreviation`, `languages`);
UPDATE "abbreviation" SET "abbreviation" = NULL WHERE LENGTH("abbreviation") = 0;
UPDATE "abbreviation" SET "is_prefix" = 1 WHERE "word" LIKE '%-';
UPDATE "abbreviation" SET "is_suffix" = 1 WHERE "word" LIKE '-%';