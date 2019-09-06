#!/bin/sh
PORT=${1:-8080}
PID=`jps -m | grep $PORT | awk '{print $1}'`
if [ -n $PORT ]; then
	echo no process is listening on port $PORT
else
	echo process $PID listening on port $PORT. It would be killed...
#	kill -9 $PID
fi