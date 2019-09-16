#!/bin/sh
PORT=${1:-8080}
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
PROJECT_HOME=$SCRIPT_DIR/..
MAIN_JAR=$PROJECT_HOME/spring/target/spring-0.0.1-SNAPSHOT.jar
# be happy, be patient
echo ----------------------------------------
fortune
echo ----------------------------------------
. $SCRIPT_DIR/stop-process.sh $PORT

PROFILE=dev
echo Spring application configuration at $PROJECT_HOME/config/application.yml 
cat $PROJECT_HOME/config/application-$PROFILE.yml

java \
  -Dspring.profiles.active=$PROFILE \
  -Dserver.port=$PORT \
  -jar $MAIN_JAR 

#  -Dspring.config.location=$PROJECT_HOME/config/application.yml
#  -Dlogging.level.org=ERROR  \
#  -Dlogging.level.com.nutanix=INFO 

