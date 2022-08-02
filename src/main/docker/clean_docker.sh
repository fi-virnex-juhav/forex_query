echo "Hello, I am clean_docker.sh file for forex_query app by juhav."
echo "I stop & delete forex_query container"
echo "I delete forex_query image" 
echo "I copy forex_query.jar file from target folder to src/main/docker folder"
echo "I was started by user $(whoami)"
echo ""
docker stop forex_query
docker rm forex_query
docker rmi -f forex_query
#docker rmi -f forex_query:latest
cp ~/eclipse-space/forex_query/target/forex_query.jar ~/eclipse-space/forex_query/src/main/docker/
echo "output from ls -l :"
ls -l  ~/eclipse-space/forex_query/src/main/docker/
