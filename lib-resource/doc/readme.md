#Prism API access
curl -X GET \
    -k   \
    --header 'Accept: application/json'  \
    -u admin:Nutanix.1 \
    https://tomahawk-v1.eng.nutanix.com:9440/PrismGateway/services/rest/v2.0/clusters/ \
    jq 
