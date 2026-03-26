-- N.B. BIT columns must SELECT @user-variable and use SET to cast their (string) value to UNSIGNED, as per example.

LOAD DATA LOCAL INFILE '<PATH_TO_TABLE>.csv'
INTO TABLE "<TABLE>"
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
ESCAPED BY ''
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(<COLUMNS>)
SET
    "bit_column" = CAST(@bit_column AS UNSIGNED),...
;