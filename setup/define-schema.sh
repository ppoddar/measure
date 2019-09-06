#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

export DATABASE_NAME=${1:-bpg}
export PGOPTIONS='--client-min-messages=warning'

psql -q -d "$DATABASE_NAME" -f $DIR/define-schema.sql

