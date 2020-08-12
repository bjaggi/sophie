package  com.eva.stream.consumer;


import com.eva.stream.consumer.deserializer.JsonDeserializer;
import com.eva.stream.consumer.json.Ticker;
import com.eva.stream.consumer.multithread.RunnableConsumer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Timestamp;
import java.time.Duration;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsumerDemo {


    public static void main(String[] args) {
int noOfConsumers = 1;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        final List<RunnableConsumer> consumers = new ArrayList<>();
        for (int i = 0; i < noOfConsumers; i++) {
            RunnableConsumer consumer = new RunnableConsumer(i, "test_group", Arrays.asList("test_topic"));
            consumers.add(consumer);
            executor.submit(consumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping Consumers...");
            for (RunnableConsumer c : consumers) {
                c.shutdown();
            }
            System.out.println("Closing Application");
            executor.shutdown();
            try {
                executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }


}