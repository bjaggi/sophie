#FROM java:8
FROM openjdk:8u131-jre-alpine

ADD target/kafka-producer-1.0-jar-with-dependencies.jar kafka-producer-1.0.jar
EXPOSE 8080
CMD java -jar kafka-producer-1.0.jar




