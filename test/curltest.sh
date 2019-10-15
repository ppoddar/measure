#!/bin/sh
PORT=8080
BASE_URL=http://localhost:$PORT/resource
REPEAT=${1:-10}

for i in {1..$REPEAT}; do
	curl $BASE_URL/job/  \
		-d @jobrequest.json \
		-H 'Content-Type:text/plain'
done
