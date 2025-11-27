-- You can download ltwa_current.csv from https://www.issn.org/services/online-services/access-to-the-ltwa/
LOAD DATA LOCAL INFILE 'D:\\Adrian\\Downloads\\ltwa_current.csv' IGNORE INTO TABLE `evidence_engine`.`abbreviation` FIELDS TERMINATED BY '	' OPTIONALLY ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES (`word`, `abbreviation`, `languages`);
UPDATE "abbreviation" SET "is_prefix" = 1 WHERE "word" LIKE '-%';
UPDATE "abbreviation" SET "is_suffix" = 1 WHERE "word" LIKE '%-';
UPDATE "abbreviation" SET "abbreviation" = NULL WHERE LENGTH("abbreviation") = 0;
