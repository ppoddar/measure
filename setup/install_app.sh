#!/bin/sh
# --------------------------------------------
# script runs on remote host 
# assumes that artifcats have been deployed
# --------------------------------------------
# directory where this script will run
DIR=${1:-/root/www/bpg}
cd $DIR 

# main jar assumed to have been deployed
MAIN_JAR=./spring/target/spring-0.0.1-SNAPSHOT.jar
PORT=${2:-8080}
DATABASE_NAME=bpg

echo installing measurement app from $DIR at $PORT

echo setup databse schema $DATABASE_NAME
sh setup/setup-database.sh $DATABASE_NAME
echo starting main service at $PORT

./setup/stop-process.sh $PORT

java -jar $MAIN_JAR \
  -Dserver.port=$PORT \
  -Dspring.config.location=$DIR/setup/application.yml \
  -Dlogging.config=$DIR/setup/logback.xml

