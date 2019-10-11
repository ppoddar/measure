#!/bin/sh
PORT=${1:-8080}
PROFILE=${2:-dev}

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
PROJECT_HOME=$SCRIPT_DIR/..
MAIN_JAR=$PROJECT_HOME/spring/target/spring-boot.jar
# be happy, be patient
echo ----------------------------------------
fortune
echo ----------------------------------------
. $SCRIPT_DIR/stop-process.sh $PORT

echo Spring application configuration at $PROJECT_HOME/config/application.yml 
#cat $PROJECT_HOME/config/application-$PROFILE.yml
#DEBUG_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=localhost:$PORT

DEBUG_OPTIONS=-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$PORT
java -Xdebug $DEBUG_OPTIONS \
  -Dconfig=config/application-$PROFILE.yml \
  -Dserver.port=$PORT \
  -jar $MAIN_JAR

#  -Dspring.config.location=$PROJECT_HOME/config/application.yml
#  -Dlogging.level.org=ERROR  \
#  -Dlogging.level.com.nutanix=INFO 

