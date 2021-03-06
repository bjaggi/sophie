/*
 * Copyright (c) 2019. Prashant Kumar Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.eva.consumer.stream.consumer.serde;

import com.eva.consumer.stream.consumer.deserializer.JsonDeserializer;
import com.eva.consumer.stream.consumer.deserializer.JsonSerializer;
import com.eva.consumer.stream.consumer.json.Ticker;
import com.eva.consumer.stream.consumer.json.TickerAggregator;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.HashMap;
import java.util.Map;

/**
 * Application level Serdes
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
public class AppSerdes extends Serdes {

    static final class EmployeeSerde extends WrapperSerde<Ticker> {
        EmployeeSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

/*    static Serde<Employee> Employee() {
        EmployeeSerde serde = new EmployeeSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("specific.class.name", Employee.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }*/

    static final class TickerAggSerde extends WrapperSerde<TickerAggregator> {
        TickerAggSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<TickerAggregator> TickerAggregate() {
        TickerAggSerde serde = new TickerAggSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put("specific.class.name", TickerAggregator.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }
}
