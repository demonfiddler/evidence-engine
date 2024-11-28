INSERT INTO "user" ("id", "login", "first_name", "last_name", "country_code", "password_hash")
  VALUES (0, 'root', 'Root', 'User', 'GB', '');
INSERT INTO "user_permission" ("user_id", "permission_code") VALUES
  (0, 'ADM'),
  (0, 'CRE'),
  (0, 'DEL'),
  (0, 'LNK'),
  (0, 'REA'),
  (0, 'UPD'),
  (0, 'UPL');