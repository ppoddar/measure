#!/bin/sh
PORT=8080
APPLICATION_CONFIG=application.yml

while (( "$#" )); do
  case "$1" in
    -c|--config)
      APPLICATION_CONFIG=$2
      shift 2
      ;;
    -p|--port)
      PORT=$2
      shift 2
      ;;
    --) # end argument parsing
      shift
      break
      ;;
    -*|--*=) # unsupported flags, ignore
      echo "Error: Unsupported flag $1" >&2
      shift
      ;;
    *) # preserve positional arguments
      PARAMS="$PARAMS $1"
      shift
      ;;
  esac
 done


SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
# Project Root Directory is same as script location
PROJECT_ROOT_DIR=$SCRIPT_DIR/..

# be happy, be patient
echo ----------------------------------------
fortune
echo ----------------------------------------

# Change to Project Root Dorectory
pushd  $PROJECT_ROOT_DIR > /dev/null 2>&1  

echo Starting Spring Boot based micro-service at port $PORT ...
mvn clean install 
mvn -q -pl spring package spring-boot:run \
  -DskipTests=true \
  -Dserver.port=$PORT \
  -Dspring.config.location=$SCRIPT_DIR/$APPLICATION_CONFIG \
  -Dlogging.level.org=ERROR  \
  -Dlogging.level.com.nutanix=INFO 
