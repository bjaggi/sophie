package com.eva.stream.consumer.multithread;

import com.eva.stream.consumer.deserializer.JsonDeserializer;
import com.eva.stream.consumer.json.Ticker;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunnableConsumer implements Runnable {
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer<String, JsonNode> consumer;
    private final List<String> topics;
    private int consumerID;

    private ObjectCodec objectMapper;

    public RunnableConsumer(int id, String groupID, List<String> topics) {
        InputStream kafkaConfigStream;
        this.topics = topics;
        this.consumerID = id;
        objectMapper = new ObjectMapper();
        Properties properties = new Properties();

        try {
            String bootstrapServers = "127.0.0.1:9092";
            String groupId = "consumer-id";
            String topic = "test_topic";

            // create consumer configs
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        this.consumer = new KafkaConsumer<>(properties);
    }

    @Override
    public void run() {


        // subscribe consumer to our topic(s)
        consumer.subscribe(Arrays.asList("test_topic"));

        try {
while(true) {
    ConsumerRecords<String, JsonNode> records =
            consumer.poll(Duration.ofMillis(30000));//30 secs
    Map<String, BigDecimal> avgMap = new HashMap<String, BigDecimal>();
    for (ConsumerRecord<String, JsonNode> record : records) {
        //System.out.println("Key: " + record.key() + ", Value: " + record.value());
        //System.out.println("Partition: " + record.partition() + ", Offset:" + record.offset());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        try {


            // mapper.readTree(record.value().asText());

            Ticker[] tickers = mapper.readValue(record.value().asText(), Ticker[].class);
            //Map<String, Object> result = mapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>(){});
            for (Ticker ticker : tickers) {
                if (avgMap.get(ticker.getName()) == null) {
                    avgMap.put(ticker.getName(), ticker.getPrice());
                } else {
                    BigDecimal currentAvg = avgMap.get(ticker.getName());
                    currentAvg = (currentAvg.add(ticker.getPrice())).divide(new BigDecimal(2.0));
                    avgMap.put(ticker.getName(), currentAvg);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    System.out.println("===============>>>>>>>> Average as of " + new Timestamp(System.currentTimeMillis()));
    System.out.println(avgMap);
}

        } catch (WakeupException e) {
            if (!closed.get()) throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Closing consumer " + consumerID);
            consumer.close();
        }

    }

    public void shutdown() {
        closed.set(false);
        consumer.wakeup();
    }
}