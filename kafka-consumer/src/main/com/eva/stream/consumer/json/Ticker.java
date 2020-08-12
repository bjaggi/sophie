package com.eva.stream.consumer.json;

import java.math.BigDecimal;

public class Ticker {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    BigDecimal price;
}
