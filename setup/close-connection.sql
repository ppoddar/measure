-- closes all connection to a database
-- database name is passed while calling this PSQL
-- psql -f close-connection.sql -v db="'$DATABASE_NAME'" 

SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = :db
  AND pid <> pg_backend_pid();