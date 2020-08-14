package com.eva.consumer.stream.consumer.deserializer;

import com.eva.consumer.stream.consumer.json.Ticker;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

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
                return new Ticker(s.split(",")[0], Integer.parseInt(s.split(",")[1]));
            }

            @Override
            public void close() {

            }
        };
    }
}