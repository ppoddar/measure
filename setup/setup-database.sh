#!/bin/sh
# Drops database and cretae a new database
DATABASE_NAME=${1:-bpg}
DATABASE_USER=${2:-postgres}

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
DATABASE_SCHEMA=$SCRIPT_DIR/define-schema.sql

export DATABASE_NAME=$DATABASE_NAME
export DATABASE_USER=$DATABASE_USER
export PGOPTIONS='--client-min-messages=warning'
# close connectio if any. Notice quote around database name in comand
psql -U $DATABASE_USER --tuples-only -f $SCRIPT_DIR/close-connection.sql -v db="'$DATABASE_NAME'"
psql -U $DATABASE_USER -c 'DROP DATABASE '"$DATABASE_NAME"''
psql -U $DATABASE_USER -c 'CREATE DATABASE '"$DATABASE_NAME"''

echo Setting up $DATABASE_NAME database with schema $DATABASE_SCHEMA
psql -U $DATABASE_USER -d "$DATABASE_NAME" -f $SCRIPT_DIR/define-schema.sql
