if ["$1" == ""];
then 
   echo "Access Token is Missing. Usage: ./test.sh <acccess_token>"
   exit
fi
clear; curl -X POST -i -H "Authorization: Bearer ${1}" http://localhost:8080/testropc/rest/resource; echo;echo;echo 
