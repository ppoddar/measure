#!/bin/sh
# -------------------------------------------
# runs Spring Boot based application
# 
# -------------------------------------------

usage() {
	echo "Usage: $0 [-p port] [-f profile]" 1>&2 
	exit 1
}
PORT=8080
PROFILE=dev
while getopts ":p:f:" o; do
	case "{o}" in
		p) PORT=${OPTARG}
		  ;;
		f) PROFILE=${OPTARG}
            ;;
        *)
          usage
          ;;
    esac
done


SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
PROJECT_HOME=$SCRIPT_DIR/..
MAIN_JAR=$PROJECT_HOME/spring/target/spring-boot.jar
# be happy, be patient
echo ----------------------------------------
fortune
echo ----------------------------------------
. $SCRIPT_DIR/stop-process.sh $PORT

#cat $PROJECT_HOME/config/application-$PROFILE.yml
#DEBUG_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=localhost:$PORT

DEBUG_OPTIONS=-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$PORT
CONFIG=$PROJECT_HOME/config/$PROFILE/application.yml

echo Spring application configuration at $CONFIG

java \
  -Xdebug $DEBUG_OPTIONS \
  -Dconfig=$CONFIG    \
  -Dserver.port=$PORT \
  -Dspring.config.location=$CONFIG    \
  -jar $MAIN_JAR

#  -Dspring.config.location=$CONFIG_FILE    \
#  -Dspring.config.location=$PROJECT_HOME/config/application.yml
#  -Dlogging.level.org=ERROR  \
#  -Dlogging.level.com.nutanix=INFO 

