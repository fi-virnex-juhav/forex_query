FROM openjdk:17
ARG JAR_FILE=*.jar
EXPOSE 8080
COPY ${JAR_FILE} forex_query.jar
ENTRYPOINT ["java", "-jar", "forex_query.jar"]
# docker rmi forex_query:latest
# docker build -t forex_query .
# image: forex_query:latest
