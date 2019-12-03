package com.pactera.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IntervalModel {


    @Expose
    @SerializedName("inter")
    private List<Inter> inter;

    public List<Inter> getInter() {
        return inter;
    }

    public void setInter(List<Inter> inter) {
        this.inter = inter;
    }
}
