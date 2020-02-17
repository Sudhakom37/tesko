package com.pactera.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

import static java.lang.Integer.compare;

public class Queue implements Comparable<Queue>{
    @Expose
    @SerializedName("count_peop")
    private int queueNumber;
    @Expose
    @SerializedName("queue_name")
    private String qName;
    @Expose
    @SerializedName("percent_peop")
    private int percent_peop;

    public int getPercent_peop() {
        return percent_peop;
    }

    public void setPercent_peop(int percent_peop) {
        this.percent_peop = percent_peop;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getqName() {
        return qName;
    }

    public void setqName(String qName) {
        this.qName = qName;
    }


    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int compareTo(@NonNull Queue o) {
        return compare(this.percent_peop,o.getPercent_peop());

    }
}
