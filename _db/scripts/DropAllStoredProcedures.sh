%MU_PSQL%
DROP FUNCTION setup_user(text, text);
DROP FUNCTION user_friends_data(int);
DROP FUNCTION user_groups_info(int);
DROP FUNCTION user_friends_upsert(int, int[]);
DROP FUNCTION user_groups_upsert(int, int[]);
DROP FUNCTION user_location_upsert(int, numeric, numeric);
DROP FUNCTION get_group_info(int);
DROP FUNCTION groups_info_upsert(int, text, text);
