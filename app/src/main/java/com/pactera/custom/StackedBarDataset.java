package com.pactera.custom;

import android.util.Log;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.Arrays;
import java.util.List;

public class StackedBarDataset extends BarDataSet {
    private static final String TAG = "MyBarDataSet";
    List<BarEntry> yVals;
    public StackedBarDataset(List<BarEntry> yVals, String label) {
        super(yVals, label);
        Log.d(TAG, "MyBarDataSet: "+yVals);
        this.yVals= yVals;
    }

    @Override
    public int getColor(int index) {
        Log.d(TAG, "getColor: "+index);
        int position = index/4;

        Log.d(TAG, "getColor: position "+position);

        if(yVals != null && yVals.size()>0) {
            //Log.d(TAG, "getColor: " + yVals.get(index).getY());
            Log.d(TAG, "getColor: " + Arrays.toString(yVals.get(index-1).getYVals()));
            //return mColors.get(0);
            for(int i=0;i<=yVals.size();i++){
                for(int j=0;j<=yVals.get(i).getYVals().length-1;j++){
                    float data =yVals.get(index-1).getYVals()[j];
                    if(data<3){
                        return mColors.get(2);
                    }else if(data>3 && data<5){
                        return mColors.get(1);
                    }else if(data>5 && data<7){
                        return mColors.get(3);
                    }else{
                        return mColors.get(0);
                    }

                }
            }

            //return mColors.get(0);

            /*if (yVals.get(index).getY() < 3) // less than 95 green
                return mColors.get(2);
            else if (yVals.get(index).getY() > 3 && yVals.get(index).getY() < 5) // less than 100 orange
                return mColors.get(1);
            else if (yVals.get(index).getY() > 5 && yVals.get(index).getY() < 7) // less than 100 orange
                return mColors.get(3);
            else if (yVals.get(index).getY() > 7) // less than 100 orange
                return mColors.get(0);
            else
                return mColors.get(0);*/
        }else{
            return mColors.get(1);
        }
        return mColors.get(0);
    }

    @Override
    public void setValues(List<BarEntry> values) {
        super.setValues(values);
        this.yVals = values;
        Log.d(TAG, "setValues: "+values);
    }
}