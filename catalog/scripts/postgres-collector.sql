-- command-line parameter passing in SQL script
-- following SQL takes a single parameter named 'db'
-- from command-line, pass 'db' argument as follows:
-- $ psql -f {this sql} -v db="'metrics'"
-- notice quote around the parameter
-- to refer the parameter in SQL, use :parameter_name
SELECT relname, n_tup_ins FROM pg_stat_user_tables	

