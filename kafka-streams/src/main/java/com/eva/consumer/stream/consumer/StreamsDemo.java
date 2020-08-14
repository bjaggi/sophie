package com.eva.consumer.stream.consumer;

import com.eva.consumer.stream.consumer.deserializer.TickerAggSerde;
import com.eva.consumer.stream.consumer.json.Ticker;
import com.eva.consumer.stream.consumer.json.TickerAggregator;
import com.eva.consumer.stream.consumer.serde.AppSerdes;
import com.eva.consumer.stream.consumer.serde.JsonPOJODeserializer;
import com.eva.consumer.stream.consumer.serde.JsonPOJOSerializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class StreamsDemo {

    public static void main(String[] args) {

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "consumer-id";
        String topic = "test_topic";
        final Properties properties = new Properties();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streamsTest");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.STATE_DIR_CONFIG, "state-store");


        Map<String, Object> serdeProps = new HashMap<>();

        Serializer<JsonNode> pageViewSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", JsonNode.class);
        pageViewSerializer.configure(serdeProps, false);

        final Deserializer<JsonNode> pageViewDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", JsonNode.class);
        pageViewDeserializer.configure(serdeProps, false);


        final Serde<JsonNode> jsonNodeSerde = Serdes.serdeFrom(pageViewSerializer, pageViewDeserializer);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, JsonNode> KS0 = streamsBuilder.stream(topic,
                Consumed.with(Serdes.String(), jsonNodeSerde));




       // KGroupedStream<String, Ticker> KGS1 = KS0.flatMapValues(tickers -> {
                    TimeWindowedKStream<String, Ticker> KGS1 = KS0.flatMapValues(tickers -> {
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
            //System.out.println(ticker.getName()+", "+ticker.getPrice());
            //return new KeyValue<>(ticker.getName(), ticker.getPrice());
            return new KeyValue<>(ticker.getName(), ticker);
        }).groupByKey(
                Serialized.with(Serdes.String(),
                        new TickerAggSerde()))
.windowedBy(TimeWindows.of(Duration.ofMinutes(30)));


        KTable<Windowed<String>, TickerAggregator> KT2 = KGS1.aggregate(
                //Initializer
                () -> new TickerAggregator().withstockCount(0).withtotalPrice(0).withavgPrice(0D)
                ,
                //Aggregator
                (k, v, aggV) -> new TickerAggregator()
                        .withstockCount(aggV.getstockCount() + 1)
                        .withtotalPrice(aggV.gettotalPrice() + v.getPrice())
                        .withavgPrice((aggV.gettotalPrice() + v.getPrice()) / (aggV.getstockCount() + 1D)),
                //Serializer
                Materialized.<String, TickerAggregator, WindowStore<Bytes, byte[]>> as("agg-store")
                        .withKeySerde(AppSerdes.String())
                        .withValueSerde( AppSerdes.TickerAggregate())
        );

        KT2.toStream().foreach(
                (k, v) -> System.out.println(Instant.now() + ",  Stock = " + k + ",  Average = " + v.getavgPrice() + "\n"));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
