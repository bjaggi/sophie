package com.eva.consumer;


import com.eva.consumer.multithread.RunnableConsumer;


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