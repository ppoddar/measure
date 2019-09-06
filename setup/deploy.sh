#!/bin/sh
# -----------------------------------------
# Deploy measurment spring boot application
# -----------------------------------------

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
PROJECT_HOME_DIR=$SCRIPT_DIR/..

REMOTE_USER=root
REMOTE_HOST=10.15.254.161
REMOTE_PORT=8080
REMOTE_DIR=/root/www/bpg/
DST=$REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR

SRC_LIST=$SCRIPT_DIR/deploy_files.lst
INSTALL_SCRIPT=$SCRIPT_DIR/install_app.sh
echo copying files from $SRC_LIST to $DST ...

rsync -v -r -p --files-from=$SRC_LIST $PROJECT_HOME_DIR $DST 

echo running script $INSTALL_SCRIPT on $REMOTE_USER@$REMOTE_HOST ...
ssh $REMOTE_USER@$REMOTE_HOST 'bash -s' < $INSTALL_SCRIPT

open http://$REMOTE_HOST:$REMOTE_PORT/


