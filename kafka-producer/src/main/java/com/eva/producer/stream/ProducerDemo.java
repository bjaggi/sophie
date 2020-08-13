package com.eva.producer.stream;


import com.eva.producer.stream.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.xml.datatype.Duration;
import java.util.Properties;
import java.util.Random;

public class ProducerDemo {


    public static void main(String[] args) {

        String bootstrapServers = "127.0.0.1:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        // create the producer
        KafkaProducer<String, JsonNode> producer = new KafkaProducer<String, JsonNode>(properties);

        // create a producer record
        ProducerRecord<String, JsonNode> record ;

        for (int i=0; i<Integer.MAX_VALUE; i++ ) {
            String randomJson = getRandomJsonData();
            record = new ProducerRecord<String, JsonNode>("test_topic", new ObjectMapper().valueToTree(randomJson));
            System.out.println(randomJson);
            // send data - asynchronous
            producer.send(record);
            try {
                Thread.sleep(100);// 10 times per second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();

    }


    public static String getRandomJsonData(){
        String randomJson =
                //"{\n" +
                //"   \"tickers\":[\n" +
                "[\n" +
                "      {\n" +
                "         \"name\":\"AMZN\",\n" +
                //"         \"price\":1902\n" +
                "         \"price\":" + getRandomNumberUsingNextInt(1700,2100) + "\n" +
                "      },\n" +
                "      {\n" +
                "         \"name\":\"MSFT\",\n" +
                "         \"price\":" + getRandomNumberUsingNextInt(90,110) + "\n" +
                "      },\n" +
                "      {\n" +
                "         \"name\":\"AAPL\",\n" +
                "         \"price\":" + getRandomNumberUsingNextInt(175,215) + "\n" +
                "      }\n" +
                "   ]\n" ;
                //"}";

        return randomJson;
    }

    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}