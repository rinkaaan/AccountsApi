docker run --name postgres -e POSTGRES_PASSWORD=<password> -p 5432:5432 -it postgres
psql -h 127.0.0.1 -p 5432 -U postgres
grant usage on schema rikagu_accounts to public;
