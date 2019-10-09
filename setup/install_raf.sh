#!/bin/sh
# --------------------------------------------
# script runs on remote host 
# assumes that artifcats have been deployed
# --------------------------------------------
# directory where this script will run
DIR=${1:-/root/www/raf}
cd $DIR 

# main jar assumed to have been deployed
MAIN_JAR=./spring/target/spring-boot.jar
PORT=${2:-8090}

echo installing RAF app from $DIR at $PORT
echo starting main service at $PORT
./setup/stop-process.sh $PORT
echo starting web service at $PORT ...
nohup \
java \
  -Dconfig=config/application-dev.yml \
  -Dserver.port=$PORT \
  -jar $MAIN_JAR      > server.log \
  &

