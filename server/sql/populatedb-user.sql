INSERT INTO "user" ("id", "username", "first_name", "last_name", "country_code", "password")
  VALUES (0, 'root', 'Root', 'User', 'GB', '');
INSERT INTO "user_authority" ("user_id", "authority_code") VALUES
  (0, 'ADM'),
  (0, 'CRE'),
  (0, 'DEL'),
  (0, 'LNK'),
  (0, 'REA'),
  (0, 'UPD'),
  (0, 'UPL');