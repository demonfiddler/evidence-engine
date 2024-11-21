INSERT INTO `evidence_engine`.`publication`
	(`title`, `authors`, `journal_id`, `kind`,                `date`,             `year`,             `abstract`, `peer_reviewed`, `doi`, `isbn`,      `url`, `accessed`)
SELECT
	 `TITLE`, `AUTHORS`, ej.`id`,      `PUBLICATION_TYPE_ID`, `PUBLICATION_DATE`, `PUBLICATION_YEAR`, `ABSTRACT`, `PEER_REVIEWED`, `DOI`, `ISSN_ISBN`, `URL`, `ACCESSED`
FROM `climate`.`publication` cp
LEFT JOIN `evidence_engine`.`journal` ej
ON cp.`JOURNAL` = ej.`title`;