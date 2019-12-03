package com.pactera.custom;

import android.util.Log;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class MyBarDataSet extends BarDataSet {
    private static final String TAG = "MyBarDataSet";
    List<BarEntry> yVals;
    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
        Log.d(TAG, "MyBarDataSet: "+yVals);
        this.yVals= yVals;
    }

    @Override
    public int getColor(int index) {
        if(yVals != null && yVals.size()>0) {
            Log.d(TAG, "getColor: " + yVals.get(index).getY());
            if (yVals.get(index).getY() <= 3) // less than 95 green
                return mColors.get(0);
            else if (yVals.get(index).getY() ==4) // less than 100 orange
                return mColors.get(1);
            else if (yVals.get(index).getY() ==5) // less than 100 orange
                return mColors.get(2);
            else if (yVals.get(index).getY() >= 6) // less than 100 orange
                return mColors.get(3);
            else
                return mColors.get(3);
        }else{
            return 0;
        }
    }

    @Override
    public void setValues(List<BarEntry> values) {
        super.setValues(values);
        this.yVals = values;
        Log.d(TAG, "setValues: "+values);
    }
}