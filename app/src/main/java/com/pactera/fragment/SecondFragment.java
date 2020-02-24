package com.pactera.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.pactera.Util.MySharedPreference;
import com.pactera.Util.MyValueFormatter;
import com.pactera.Util.PrefKeys;
import com.pactera.Util.StackedValueFormatter;
import com.pactera.api.RetrofitInstance;
import com.pactera.custom.DayAxisValueFormatter;
import com.pactera.custom.MyBarDataSet;
import com.pactera.custom.XYMarkerView;
import com.pactera.model.Inter;
import com.pactera.model.IntervalModel;
import com.pactera.tesko.QueueAnalysisActivity;
import com.pactera.tesko.R;


import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by P0147845 on 11-11-2019.
 */

public class SecondFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "SecondFragment";
    private BarChart mBarChartQueueWait;
    private QueueAnalysisActivity mQueueAnalysisActivity;
    private List<Inter> intervalModels;
    ImageView ivRefresh;
    ArrayList<String> xAxisValues;
    private BarChart chart ;
    int time ;
    MySharedPreference preference;
    String queueName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_two, container, false);
        mQueueAnalysisActivity = ((QueueAnalysisActivity) getActivity());
        chart = rootView.findViewById(R.id.barChartQuequeWait);
        ivRefresh = rootView.findViewById(R.id.iv_refresh);
        Spinner mSpinner = rootView.findViewById(R.id.spnerInterval);
        preference = new MySharedPreference(getActivity());
        queueName = preference.getPref(PrefKeys.QUEUE_NAME);
        mSpinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("1-Min");
        categories.add("5-Min");
        categories.add("30-Min");
        categories.add("3-Hrs");
        categories.add("Full Day");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mBarChartQueueWait = rootView.findViewById(R.id.barChartQuequeWait);
        // attaching data adapter to spinner
        getIntervalData(1,queueName);
        ivRefresh.setOnClickListener(view -> getIntervalData(time,queueName));
        mSpinner.setAdapter(dataAdapter);
        //init(rootView);

        return rootView;
    }

    public static SecondFragment newInstance(String text) {

        SecondFragment f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    private void init(View rootView) {
        mBarChartQueueWait = rootView.findViewById(R.id.barChartQuequeWait);

        initQueueWaitData();
        getIntervalData(2,queueName);


        //setQueueWaitData(4,10);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(intervalModels != null){
            setUpChart(intervalModels);
        }
    }

    private void initQueueWaitData() {

        mBarChartQueueWait.setDrawBarShadow(false);
        mBarChartQueueWait.setDrawValueAboveBar(false);
        mBarChartQueueWait.getDescription().setEnabled(false);
        mBarChartQueueWait.setHighlightPerDragEnabled(false);
        mBarChartQueueWait.setOnTouchListener(null);  // To disable click listner on bar

        // if more than 60 entries are displayed in the mBarChartQueueWait, no values will be
        // drawn
        //mBarChartQueueWait.setMaxVisibleValueCount(100);

        // scaling can now only be done on x- and y-axis separately
        mBarChartQueueWait.setPinchZoom(false);

        mBarChartQueueWait.setDrawGridBackground(false);
        // mBarChartQueueWait.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(mBarChartQueueWait);

        XAxis xAxis = mBarChartQueueWait.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(4); // To add number of label in x-Axis i.e no. of bar == no.of lables
        xAxis.setValueFormatter(xAxisFormatter);

        xAxis.setDrawLabels(true);  // To hide x-axis labels

//        ValueFormatter custom = new MyValueFormatter("$");
        ValueFormatter custom = new IndexAxisValueFormatter();


        YAxis leftAxis = mBarChartQueueWait.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(1, false);   // To show the number of label on left line
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
        mBarChartQueueWait.getAxisLeft().addLimitLine(ll);

        ll = new LimitLine(9, "");
        ll.setLineWidth(3f);
//        ll.setTextSize(12f); // To add text above line
//        ll.enableDashedLine(2f, 2f, 0); // To add dash line
        ll.setLineColor(mQueueAnalysisActivity.getResources().getColor(R.color.orange));
        mBarChartQueueWait.getAxisLeft().addLimitLine(ll);


        ll = new LimitLine(15, "");
        ll.setLineWidth(3f);
//        ll.setTextSize(12f); // To add text above line
//        ll.enableDashedLine(2f, 2f, 0); // To add dash line
        ll.setLineColor(mQueueAnalysisActivity.getResources().getColor(R.color.red));
        mBarChartQueueWait.getAxisLeft().addLimitLine(ll);

        YAxis rightAxis = mBarChartQueueWait.getAxisRight();
        rightAxis.setDrawGridLines(false);
//        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(1, true);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);  // To hide right hand y-axis line

        Legend l = mBarChartQueueWait.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        l.setEnabled(false);  // To hide/show bottom hint square item in mBarChartQueueWait

        XYMarkerView mv = new XYMarkerView(mQueueAnalysisActivity, xAxisFormatter);
        mv.setChartView(mBarChartQueueWait); // For bounds control
        mBarChartQueueWait.setMarker(mv); // Set the marker to the mBarChartQueueWait

    }

    /*private void setQueueWaitData(int count, int dataList) {

        List<String> xAxisValues = new ArrayList<>(Arrays.asList("4 Min", "5-Min", ">5 Min", ">7 Min"));
        mBarChartQueueWait.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

        float start = 0f;

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {

            values.add(new BarEntry(i, i + 10));

        }

        BarDataSet set1;

        if (mBarChartQueueWait.getData() != null &&
                mBarChartQueueWait.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChartQueueWait.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mBarChartQueueWait.getData().notifyDataChanged();
            mBarChartQueueWait.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setValueTextSize(10f);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);

            *//*int[] NO_OF_PERSON_COLORS = {
                    mQueueAnalysisActivity.getResources().getColor(R.color.red),
                    mQueueAnalysisActivity.getResources().getColor(R.color.orange),
                    mQueueAnalysisActivity.getResources().getColor(R.color.blue),
                    mQueueAnalysisActivity.getResources().getColor(R.color.gray),
            };

            set1.setColors(NO_OF_PERSON_COLORS);*//*

            set1.setColors(mQueueAnalysisActivity.getResources().getColor(R.color.blue));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(0f);
//            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);
            mBarChartQueueWait.setData(data);
        }
    }
    private void setQueueWaitServerData( List<Inter> dataList) {

        List<String> xAxisValues = new ArrayList<>();

        float start = 0f;



        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = (int)start; i < start+dataList.size(); i++) {
            Inter inter = dataList.get(i);
            float val = Float.valueOf( inter.getNumber());
            xAxisValues.add(String.valueOf(inter.getIntervals()));
            BarEntry entry = new BarEntry(i,15);
            entry.setY(15);
            values.add(entry);
        }

        mBarChartQueueWait.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        BarDataSet set1;

        if (mBarChartQueueWait.getData() != null &&
                mBarChartQueueWait.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChartQueueWait.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mBarChartQueueWait.getData().notifyDataChanged();
            mBarChartQueueWait.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setValueTextSize(10f);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);

            *//*int[] NO_OF_PERSON_COLORS = {
                    mQueueAnalysisActivity.getResources().getColor(R.color.red),
                    mQueueAnalysisActivity.getResources().getColor(R.color.orange),
                    mQueueAnalysisActivity.getResources().getColor(R.color.blue),
                    mQueueAnalysisActivity.getResources().getColor(R.color.gray),
            };

            set1.setColors(NO_OF_PERSON_COLORS);*//*

            set1.setColors(mQueueAnalysisActivity.getResources().getColor(R.color.blue));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            Log.d(TAG, "setQueueWaitServerData: "+dataSets.size());
            BarData data = new BarData(dataSets);
            data.setValueTextSize(0f);
//            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);
            mBarChartQueueWait.setData(data);
        }
    }*/

    private void getIntervalData(int threshold,String queueName){

        RetrofitInstance.getInstance(getActivity()).getRestAdapter()
                .getIntervalData(threshold,queueName,preference.getPref(PrefKeys.StoreType).toLowerCase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<IntervalModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.getMessage());
                        //Toast.makeText(mQueueAnalysisActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(IntervalModel intervalModel) {
                        Log.d(TAG, "onNext: intervalModel"+intervalModel);
                        if(intervalModel != null){
                            intervalModels = intervalModel.getInter();
                            Log.d(TAG, "onNext: intervalModel"+intervalModel.getInter());
                            //setQueueWaitServerData(intervalModels);
                            setUpChart(intervalModels);
                        }

                    }
                });

    }

    // Main Content

    void setUpChart(List<Inter> list){

        setChartConfiguration();
        removeYAxisAndXAxisChartBg();
        setYAxis();
        setXAxis();
        setLegend();
        setBars(list);
    }



    void setChartConfiguration(){

        chart.setOnChartValueSelectedListener(null);
        chart.getDescription().setEnabled(false);
        //chart.setMaxVisibleValueCount(40);
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
    public static int rgb(@NonNull String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        return Color.rgb(r, g, b);
    }
    void removeYAxisAndXAxisChartBg(){
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


    void setLegend(){
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

    void setYAxis(){
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawLabels(true);
        leftAxis.setLabelCount(0, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setDrawGridLines(true);
        //leftAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        leftAxis.setValueFormatter(new MyValueFormatter(""));
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)
        chart.getAxisRight().setEnabled(false);

    }

    void setXAxis(){
        XAxis xLabels = chart.getXAxis();
        xLabels.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
    }



    void setBars(List<Inter> queues){

        ArrayList<BarEntry> values = new ArrayList<>();
        readBarGraphData(values,queues);
        //readBarGraphDataLocal(values,queues);
        ArrayList<String> xValues= new ArrayList<>();

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
            //set1.setStackLabels(new String[]{"<3", "4", "5",">6"});
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);

            //data.setValueFormatter(new StackedValueFormatter(true, "", 0));
            //data.setValueFormatter(new IndexAxisValueFormatter());
            data.setBarWidth(.40f);

            data.setValueTextColor(Color.TRANSPARENT);

            chart.setData(data);
        }

        chart.setFitBars(true);
        chart.invalidate();
    }
    void readBarGraphData(ArrayList<BarEntry> values,List<Inter> queues){

        intervalModels = queues;
        ArrayList<String> xVlaues= new ArrayList<>();

        try {
            if(queues != null){

                for(int i = 0; i < queues.size();i++){

                    //JSONObject elem =(JSONObject) list.get(i);
                    Inter elem = queues.get(i);
                    if(elem != null){

                        xVlaues.add(elem.getIntervals() + " Min");

                        //List<Integer> prods = (List<Integer>)queues.get(i);
                        float val = (float) elem.getNumber();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (i){
            case 0:
                time = 1;
                getIntervalData(time,queueName);
                break;
            case 1:
                time = 5;
                getIntervalData(time,queueName);
                break;
            case 2:
                time = 30;
                getIntervalData(time,queueName);
                break;
            case 3:
                time = 180;
                getIntervalData(time,queueName);
                break;
            case 4:
                time = 24;
                getIntervalData(time,queueName);
                break;
                default:
                    getIntervalData(time,queueName);
                    break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}