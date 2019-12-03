package com.pactera.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GraphModel {

    @Expose
    @SerializedName("queue")
    private List<Queue> queue;

    public List<Queue> getQueue() {
        return queue;
    }

    public void setQueue(List<Queue> queue) {
        this.queue = queue;
    }
}
