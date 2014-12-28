if ["$1" == ""];
then 
   echo "Access Token is Missing. Usage: ./test.sh <acccess_token>"
   exit
fi
clear; curl -X GET -i -H "Authorization: Bearer ${1}" https://localhost:8443/testac/rest/resource; echo;echo;echo 
