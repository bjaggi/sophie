package com.eva.stream.consumer.json;

import com.eva.stream.consumer.json.Ticker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)

public class Tickers {


    public List<Ticker> getTickers() {
        return tickers;
    }

    public void setTickers(List<Ticker> tickers) {
        this.tickers = tickers;
    }

    List<Ticker> tickers ;

}
