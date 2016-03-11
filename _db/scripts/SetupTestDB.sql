SELECT * FROM setup_user('test@test.net', 'sha1$da31e3bc$1$389604bd77b1aaa3ecbf46d665b2714c1e2afbf9');
SELECT * FROM setup_user('test@test.com',   'sha1$cd97f01a$1$d266780758c4adf7dd40046baac375193e800af1');
UPDATE User_Pers SET userId=18 WHERE email='test@test.net';
UPDATE User_Pers SET userId=19 WHERE email='test@test.com';
SELECT * FROM groups_info_upsert(1, 'hello', 'world');
SELECT * FROM groups_info_upsert(2, 'grouop', 'dos');
SELECT * FROM user_groups_upsert(1, '{18, 19}');
SELECT * FROM user_groups_upsert(2, '{18}');
SELECT * FROM user_friends_upsert(18, '{19}');
