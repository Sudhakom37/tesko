package com.pactera.custom;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BaseDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.List;

public class CustomBarDataSet extends BaseDataSet<BarEntry> implements IBarDataSet {

    private int isStacked;
    @Override
    public boolean isStacked() {
        return false;
    }

    @Override
    public int getStackSize() {
        return 0;
    }

    @Override
    public int getBarShadowColor() {
        return 0;
    }

    @Override
    public float getBarBorderWidth() {
        return 0;
    }

    @Override
    public int getBarBorderColor() {
        return 0;
    }

    @Override
    public int getHighLightAlpha() {
        return 0;
    }

    @Override
    public String[] getStackLabels() {
        return new String[0];
    }

    @Override
    public int getHighLightColor() {
        return 0;
    }

    @Override
    public float getYMin() {
        return 0;
    }

    @Override
    public float getYMax() {
        return 0;
    }

    @Override
    public float getXMin() {
        return 0;
    }

    @Override
    public float getXMax() {
        return 0;
    }

    @Override
    public int getEntryCount() {
        return 0;
    }

    @Override
    public void calcMinMax() {

    }

    @Override
    public void calcMinMaxY(float fromX, float toX) {

    }

    @Override
    public BarEntry getEntryForXValue(float xValue, float closestToY, DataSet.Rounding rounding) {
        return null;
    }

    @Override
    public BarEntry getEntryForXValue(float xValue, float closestToY) {
        return null;
    }

    @Override
    public List<BarEntry> getEntriesForXValue(float xValue) {
        return null;
    }

    @Override
    public BarEntry getEntryForIndex(int index) {
        return null;
    }

    @Override
    public int getEntryIndex(float xValue, float closestToY, DataSet.Rounding rounding) {
        return 0;
    }

    @Override
    public int getEntryIndex(BarEntry e) {
        return 0;
    }

    @Override
    public boolean addEntry(BarEntry e) {
        return false;
    }

    @Override
    public void addEntryOrdered(BarEntry e) {

    }

    @Override
    public boolean removeEntry(BarEntry e) {
        return false;
    }

    @Override
    public void clear() {

    }
    // implement all by the extended class and interface required methods
}