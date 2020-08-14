package com.eva.consumer.stream.consumer.deserializer;

import com.eva.consumer.stream.consumer.json.Ticker;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class TickerAggSerde implements Serde<Ticker> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<Ticker> serializer() {
        return new Serializer<Ticker>() {
            @Override
            public void configure(Map<String, ?> map, boolean b) {

            }

            @Override
            public byte[] serialize(String string, Ticker kc) {
                String s = kc.getName() + "," + kc.getPrice();
                return s.getBytes();
            }

            @Override
            public void close() {

            }
        };
    }

    @Override
    public Deserializer<Ticker> deserializer() {
        return new Deserializer<Ticker>() {
            @Override
            public void configure(Map<String, ?> map, boolean b) {

            }

            @Override
            public Ticker deserialize(String string, byte[] bytes) {
                String s = new String(bytes);


                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
                mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

                Ticker[] arr = new Ticker[0];
                String tokens[] = new String[2];
                JsonNode parentJson;
                try {
                    // parentJson=  mapper.readTree(s);
                    //arr = mapper.readValue(parentJson.asText(), Ticker[].class);
                    tokens = s.split(",");
                    //System.out.println(" Sucess Processing "+s);
                } catch (Exception e) {
                    System.out.println(" Error Processing "+s);
                    e.printStackTrace();
                }



                Ticker newTicker = new Ticker();

try {
    newTicker = new Ticker(tokens[0], Integer.valueOf(tokens[1]));
}catch (Exception e){
    e.printStackTrace();
}

                return newTicker;
            }

            @Override
            public void close() {

            }
        };
    }
}