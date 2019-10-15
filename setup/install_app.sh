#!/bin/sh
# --------------------------------------------
# script runs on remote host 
# assumes that artifcats have been deployed
# --------------------------------------------
# directory where this script will run
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
INSTALL_DIR=$SCRIPT_DIR/..

cd $INSTALL_DIR
# main jar assumed to have been deployed
MAIN_JAR=$INSTALL_DIR/spring/target/spring-boot.jar
PORT=10000

. $INSTALL_DIR/setup/stop-process.sh $PORT

CONFIG=$INSTALL_DIR/config/stage/application.yml
echo starting web service at $PORT with $CONFIG...

nohup \
java  \
  -Dconfig=$CONFIG    \
  -Dserver.port=$PORT \
  -Dspring.config.location=$CONFIG \
  -jar $MAIN_JAR >>spring-output.log 2>&1    &

