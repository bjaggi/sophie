package com.eva.stream.consumer.json;


public class Ticker {
    String name;

    public Ticker(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    public Ticker() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    Integer price;
}
