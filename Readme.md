# sophie

## to Build Producer Jar
mvn clean install 
## to Build Producer Image 
docker build -t kafka-producer:v1 -f Dockerfile .
## to Build Producer Image 
docker run kafka-producer:v1




