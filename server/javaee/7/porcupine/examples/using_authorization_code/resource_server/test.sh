if ["$1" == ""];
then 
   echo "Access Token is Missing. Usage: ./test.sh <acccess_token>"
   exit
fi
clear; curl -X GET -i -H "Authorization: Bearer ${1}" http://localhost:8080/testac/rest/resource; echo;echo;echo 
