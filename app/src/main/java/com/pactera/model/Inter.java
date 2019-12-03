package com.pactera.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Inter {
    @Expose
    @SerializedName("number")
    private int number;
    @Expose
    @SerializedName("intervals")
    private int intervals;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getIntervals() {
        return intervals;
    }

    public void setIntervals(int intervals) {
        this.intervals = intervals;
    }

    @Override
    public String toString() {
        return "Inter{" +
                "number=" + number +
                ", intervals=" + intervals +
                '}';
    }
}
