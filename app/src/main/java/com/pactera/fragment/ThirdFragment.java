package com.pactera.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.pactera.Util.StackedValueFormatter;
import com.pactera.api.RetrofitInstance;
import com.pactera.custom.MyBarDataSet;
import com.pactera.model.GraphModel;
import com.pactera.model.Queue;
import com.pactera.tesko.QueueAnalysisActivity;
import com.pactera.tesko.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.pactera.tesko.MainActivity.rgb;

/**
 * Created by P0147845 on 11-11-2019.
 */

public class ThirdFragment extends Fragment {
    private static final String TAG = "ThirdFragment";
    private BarChart chart;
    List<String> xAxisValues;
    private QueueAnalysisActivity mQueueAnalysisActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_three, container, false);

        mQueueAnalysisActivity = ((QueueAnalysisActivity) getActivity());
        init(rootView);

        return rootView;
    }

    public static ThirdFragment newInstance(String text) {

        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


    private void init(View rootView) {
        chart = rootView.findViewById(R.id.barChartAlert);

        getBarChart(1);
        //setAlertBarData(11, 10,null);
    }

    /*private void initAlertBarData() {

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.getDescription().setEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setOnTouchListener(null);  // To disable click listner on bar

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(10);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(11);  // To add number of label in x-Axis i.e no. of bar == no.of lables
        xAxis.setValueFormatter(xAxisFormatter);

        xAxis.setDrawLabels(true);  // To hide x-axis labels

//        ValueFormatter custom = new MyValueFormatter("$");
        ValueFormatter custom = new MyValueFormatter("");


        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(10, false);   // To show the number of label on left line
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        int maxCapacity = 6;
        LimitLine ll;
         ll = new LimitLine(maxCapacity, "");
        ll.setLineWidth(3f);
//        ll.setTextSize(12f);  // To add text above line
//        ll.enableDashedLine(2.5f, 1.5f, 0);  // To add dash line
        ll.setLineColor(mQueueAnalysisActivity.getResources().getColor(R.color.green));
        chart.getAxisLeft().addLimitLine(ll);

        ll = new LimitLine(9, "");
        ll.setLineWidth(3f);
//        ll.setTextSize(12f); // To add text above line
//        ll.enableDashedLine(2f, 2f, 0); // To add dash line
        ll.setLineColor(mQueueAnalysisActivity.getResources().getColor(R.color.orange));
        chart.getAxisLeft().addLimitLine(ll);


        ll = new LimitLine(15, "");
        ll.setLineWidth(3f);
//        ll.setTextSize(12f); // To add text above line
//        ll.enableDashedLine(2f, 2f, 0); // To add dash line
        ll.setLineColor(mQueueAnalysisActivity.getResources().getColor(R.color.red));
        chart.getAxisLeft().addLimitLine(ll);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
//        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(0, true);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);  // To hide right hand y-axis line

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        l.setEnabled(false);  // To hide/show bottom hint square item in chart

        XYMarkerView mv = new XYMarkerView(mQueueAnalysisActivity, xAxisFormatter);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

    }

    private void setAlertBarData(int count, float range, List<Queue> queues) {

        //List<String> xAxisValues = new ArrayList<>(Arrays.asList("Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11"));
        List<String> xAxisValues = new ArrayList<>();
        chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        float start = 0f;

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {
            float value = Float.valueOf(queues.get(i).getQueueNumber());
            xAxisValues.add(queues.get(i).getqName());
            values.add(new BarEntry(i, value ));
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setValueTextSize(10f);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);

          *//*  int[] NO_OF_PERSON_COLORS = {
                    mQueueAnalysisActivity.getResources().getColor(R.color.red),
                    mQueueAnalysisActivity.getResources().getColor(R.color.orange),
                    mQueueAnalysisActivity.getResources().getColor(R.color.blue),
                    mQueueAnalysisActivity.getResources().getColor(R.color.gray),
            };
//            set1.setColors(NO_OF_PERSON_COLORS);*//*
            set1.setColors(mQueueAnalysisActivity.getResources().getColor(R.color.blue));


            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(0f);
//            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);
            chart.setData(data);
        }
    }*/

    public void getBarChart(int interval) {

        Log.d(TAG, "Token for my GCM Listener is : " + interval);

        RetrofitInstance.getInstance(getActivity())
                .getRestAdapter()
                .getQueues(interval)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GraphModel>() {
                    @Override
                    public void onCompleted() {

                        Log.d(TAG, "onCompleted: ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());

                    }

                    @Override
                    public void onNext(GraphModel graphModel) {
                        //Toast.makeText(getActivity(), "Chart Updated ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onNext: graphModel.getQueue().toString "+graphModel.getQueue().toString());
                        //setAlertBarData(1,4,graphModel.getQueue());
                        setUpChart(graphModel.getQueue());


                    }
                });

    }
    void setUpChart(List<Queue> list) {

        setChartConfiguration();
        removeYAxisAndXAxisChartBg();
        setYAxis();
        setXAxis();
        setLegend();
        setBars(list);
    }


    void setChartConfiguration() {

        chart.setOnChartValueSelectedListener(null);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(40);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setHighlightFullBarEnabled(false);
        chart.setExtraBottomOffset(15f);
        chart.getXAxis().setAxisLineWidth(1f);
        chart.getAxisLeft().setAxisLineWidth(1f);

        chart.getAxisLeft().setAxisLineColor(rgb("#000000"));
        chart.getXAxis().setAxisLineColor(rgb("#000000"));

        int maxCapacity = 6;
        LimitLine ll;
        ll = new LimitLine(maxCapacity, "");
        ll.setLineWidth(3f);
//        ll.setTextSize(12f);  // To add text above line
//        ll.enableDashedLine(2.5f, 1.5f, 0);  // To add dash line
        ll.setLineColor(mQueueAnalysisActivity.getResources().getColor(R.color.green));
        chart.getAxisLeft().addLimitLine(ll);

        ll = new LimitLine(3, "");
        ll.setLineWidth(3f);
//        ll.setTextSize(12f); // To add text above line
//        ll.enableDashedLine(2f, 2f, 0); // To add dash line
        ll.setLineColor(mQueueAnalysisActivity.getResources().getColor(R.color.orange));
        chart.getAxisLeft().addLimitLine(ll);


        ll = new LimitLine(15, "");
        ll.setLineWidth(3f);
//        ll.setTextSize(12f); // To add text above line
//        ll.enableDashedLine(2f, 2f, 0); // To add dash line
        ll.setLineColor(mQueueAnalysisActivity.getResources().getColor(R.color.red));
        chart.getAxisLeft().addLimitLine(ll);


        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);

    }

    void removeYAxisAndXAxisChartBg() {
        // Remove the grid line from background
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(false);

        // Disable the right y axis
        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);
        rightYAxis.setDrawGridLines(false);

    }


    void setLegend() {
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(2f);
        l.setXEntrySpace(12f);
        l.setEnabled(false);
    }

    void setYAxis() {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawLabels(true);
        leftAxis.setLabelCount(1, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setDrawGridLines(true);
        //leftAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        //leftAxis.setValueFormatter(new MyValueFormatter(""));
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        chart.getAxisRight().setEnabled(false);

    }

    void setXAxis() {
        XAxis xLabels = chart.getXAxis();
        xLabels.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    void setBars(List<Queue> queues) {

        ArrayList<BarEntry> values = new ArrayList<>();

        readBarGraphData(values, queues);
        //readBarGraphDataLocal(values,queues);
        ArrayList<String> xValues = new ArrayList<>();

        MyBarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {

            set1 = (MyBarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new MyBarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setColors(getActivity().getResources().getColor(R.color.blue));
            //set1.setColors(getColors());
            //set1.setStackLabels(new String[]{"<3", "4", "5",">6"});
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);

            data.setValueFormatter(new StackedValueFormatter(true, "", 1));
            //data.setValueFormatter(new IndexAxisValueFormatter());
            data.setBarWidth(.40f);

            data.setValueTextColor(Color.TRANSPARENT);

            chart.setData(data);
        }

        chart.setFitBars(true);
        chart.invalidate();
    }

    void readBarGraphData(ArrayList<BarEntry> values, List<Queue> queues) {

        ArrayList<String> xVlaues = new ArrayList<>();

        JSONObject mainObj = null;
        try {
            if (queues != null) {

                for (int i = 0; i < queues.size(); i++) {

                    //JSONObject elem =(JSONObject) list.get(i);
                    Queue elem = queues.get(i);
                    if (elem != null) {

                        xVlaues.add(elem.getqName());

                        //List<Integer> prods = (List<Integer>)queues.get(i);
                        float val = Float.valueOf(elem.getQueueNumber());
                        //JSONArray prods = elem.getJSONArray("queue_number");
                        BarEntry barEntry = new BarEntry(
                                i,
                                val);
                        barEntry.setY(val);

                        values.add(barEntry);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        xAxisValues = xVlaues;

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        //chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter());
        //chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter());

    }
}