#!/bin/sh

API=${1:-/}
PAYLOAD=${2:-}

export BASE_URL=http://localhost:8080/resource/
echo 'BASE_URL='$BASE_URL

# -----------  curl -----------------------------
get_api() {
   curl -v  $BASE_URL/$1 \
-H "Accept: application/json" \
-H "Content-type: application/json" \
  | jq
}

post_api() {
	if [ -f $2 ]; then
       echo "posting with $2 as payload"
       curl -v  $BASE_URL/$1 \
        -H "Accept: application/json" \
        -H "Content-type: application/json" \
        -d @$2 | jq
   else 
       echo "***ERROR: can not find payload  $2"
    fi

}



case $API in 
	"allocate/")
	   post_api $API $PAYLOAD ;;
	*)
	   echo 'unrecognized api '  $API
	   # get_api $API;;
esac



# curl -v  $BASE_URL/databases/ \
# -H "Accept: application/json" \
# -H "Content-type: application/json" \
# -d @database.json | jq 
# 
# curl -v  $BASE_URL/snapshot/test/test_db/pg_stat_activity/ \
# -H "Accept: application/json" \
# -H "Content-type: application/json" \
# -d @snapshot.json | jq 

# curl -v $BASE_URL/benchmark/test/test_db \
# -H "Accept: application/json" \
# -H "Content-type: application/json" \
# -d @benchmark.json | jq 
