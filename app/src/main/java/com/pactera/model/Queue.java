package com.pactera.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Queue {
    @Expose
    @SerializedName("count_peop")
    private String queueNumber;
    @Expose
    @SerializedName("queue_Name")
    private String qName;
    @Expose
    @SerializedName("percent_peop")
    private String percent_peop;

    public String getPercent_peop() {
        return percent_peop;
    }

    public void setPercent_peop(String percent_peop) {
        this.percent_peop = percent_peop;
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(String queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getqName() {
        return qName;
    }

    public void setqName(String qName) {
        this.qName = qName;
    }
}
