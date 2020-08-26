# Sample kafka Microservices to produce random stock data and consumers to calculate stock average in a window

## Start Kafka using docker compose or Shell Script
docker-compose up


## to Build Producer Jar
mvn clean install 
## to Build Producer Image 
docker build -t kafka-producer:v1 -f Dockerfile .
## to Run Docker Producer Image 
docker run kafka-producer:v1




