#!/bin/sh
PORT=${1:-8080}
PID=`jps -m | grep spring | awk '{print $1}'`
 echo process id listeing on port $PORT is [$PID]
if [ -z $PID ]; then
	echo no process is listening on port $PORT
else
	echo *** process $PID listening on port $PORT. It would be killed
	kill -9 $PID
fi