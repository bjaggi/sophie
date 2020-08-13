package com.eva.consumer.stream.consumer;


import com.eva.consumer.stream.consumer.deserializer.JsonDeserializer;
import com.eva.consumer.stream.consumer.deserializer.JsonSerializer;
import com.eva.consumer.stream.consumer.json.Ticker;
import com.eva.consumer.stream.consumer.json.TickerAggregator;
import com.eva.consumer.stream.consumer.serde.JsonPOJODeserializer;
import com.eva.consumer.stream.consumer.serde.JsonPOJOSerializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class StreamsDemo {


    public static void main(String[] args) {
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "consumer-id";
        String topic = "test_topic";
        final Properties properties = new Properties();


        Map<String, Object> serdeProps = new HashMap<>();

        Serializer<JsonNode> pageViewSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", JsonNode.class);
        pageViewSerializer.configure(serdeProps, false);

        final Deserializer<JsonNode> pageViewDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", JsonNode.class);
        pageViewDeserializer.configure(serdeProps, false);


        final Serde<JsonNode> jsonNodeSerde = Serdes.serdeFrom(pageViewSerializer, pageViewDeserializer);


        // create consumer configs
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(StreamsConfig.STATE_DIR_CONFIG, "state-store");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, jsonNodeSerde.getClass());
        //properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, jsonNodeSerde.getClass().getName());
        properties.put("serializer.class", "kafka.serializer.DefaultEncoder");

        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty("application.id","streams-app");

        StreamsBuilder streamBuilder = new StreamsBuilder();
        KStream<String, JsonNode> KS0 = streamBuilder.stream("test_topic", Consumed.with(Serdes.String(), jsonNodeSerde ) );



        TimeWindowedKStream<String, Integer> KGS0 =
                // KTable<String, JsonNode> stockTicker =
                KS0.flatMapValues(tickers -> {
                            ObjectMapper mapper = new ObjectMapper();
                            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

                            Ticker[] arr = new Ticker[0];

                            try {
                                arr=  mapper.readValue(tickers.asText(), Ticker[].class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            List<JsonNode> flattenedTickers = new ArrayList<JsonNode>() ;
                            for (Ticker ticker : arr){
                                Ticker newTicker = new Ticker(ticker.getName(), ticker.getPrice());
                                JsonNode node = mapper.convertValue(newTicker, JsonNode.class);

                                flattenedTickers.add(node);
                            }

                            return flattenedTickers;
                        }

                ) .map((key,values)->{
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                    mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);


                    Ticker ticker = new Ticker();
                    try {
                        ticker = mapper.readValue(values.toString(), Ticker.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(ticker.getName()+", "+ticker.getPrice());
                    return new KeyValue<>(ticker.getName(), ticker.getPrice());
                }).groupByKey()
                        .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                ;





       /* KTable<String, TickerAggregator> KT2 = KGS0
                .aggregate(
                //Initializer
                () -> new TickerAggregator().withstockCount(0).withtotalPrice(0).withavgPrice(0D),
                //Aggregator

                (k, v, aggV) -> new TickerAggregator()
                        .withstockCount(aggV.getstockCount() + 1)
                        .withtotalPrice( (v + aggV.gettotalPrice()) )).intValue())//TODO simplify this
                        .withavgPrice(aggV.gettotalPrice() + v) / (aggV.getstockCount() + 1D)),
                //Serializer
                Materialized.<String, TickerAggregator, KeyValueStore<Bytes, byte[]>>as("agg-store")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new TickerAggSerde())
        );
*/


        KGS0.count().toStream().foreach(
                (k, v) -> System.out.println("Ticker = " + k + " Avg Price = " + v));

        KafkaStreams streams = new KafkaStreams(streamBuilder.build(), properties);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }



    static final class TickerAggSerde extends Serdes.WrapperSerde<TickerAggregator> {
        TickerAggSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }


}