package com.eva.consumer.stream.consumer.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
//import org.apache.commons.lang.builder.ToStringBuilder;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "total_price",
            "stock_count",
            "avg_price"
    })
    public class TickerAggregator {

        public TickerAggregator(Integer totalPrice, Integer stockCount, Double avgPrice) {
            this.totalPrice = totalPrice;
            this.stockCount = stockCount;
            this.avgPrice = avgPrice;
        }

        public TickerAggregator() {

        }

        @JsonProperty("total_price")
        private Integer totalPrice;
        @JsonProperty("stock_count")
        private Integer stockCount;
        @JsonProperty("avg_price")
        private Double avgPrice;

        @JsonProperty("total_price")
        public Integer gettotalPrice() {
            return totalPrice;
        }

        @JsonProperty("total_price")
        public void settotalPrice(Integer totalPrice) {
            this.totalPrice = totalPrice;
        }

        public TickerAggregator withtotalPrice(Integer totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        @JsonProperty("stock_count")
        public Integer getstockCount() {
            return stockCount;
        }

        @JsonProperty("stock_count")
        public void setstockCount(Integer stockCount) {
            this.stockCount = stockCount;
        }

        public TickerAggregator withstockCount(Integer stockCount) {
            this.stockCount = stockCount;
            return this;
        }

        @JsonProperty("avg_price")
        public Double getavgPrice() {
            return avgPrice;
        }

        @JsonProperty("avg_price")
        public void setavgPrice(Double avgPrice) {
            this.avgPrice = avgPrice;
        }

        public TickerAggregator withavgPrice(Double avgPrice) {
            this.avgPrice = avgPrice;
            return this;
        }

        @Override
        public String toString() {
            //return new ToStringBuilder(this).append("totalPrice", totalPrice).append("stockCount", stockCount).append("avgPrice", avgPrice).toString();
        return "over ride me";
        }

    }