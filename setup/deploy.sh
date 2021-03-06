#!/bin/sh
# -------------------------------------------------------
# Deploy JOB FARM application
# ------------------------------------------------------

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
PROJECT_HOME_DIR=$SCRIPT_DIR/..

REMOTE_USER=root
REMOTE_HOST=10.15.254.161
REMOTE_PORT=10000
REMOTE_DIR=/root/job-farm/install
DST=$REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR

SRC_LIST=$SCRIPT_DIR/deploy_files.lst
rsync -v -r -p --files-from=$SRC_LIST $PROJECT_HOME_DIR $DST 

ssh $REMOTE_USER@$REMOTE_HOST 'cd '"$REMOTE_DIR"'; setup/install_app.sh'

echo service has been installed at $REMOTE_HOST Opening page...
sleep 5
open http://$REMOTE_HOST:$REMOTE_PORT/dynamic-gui.html


