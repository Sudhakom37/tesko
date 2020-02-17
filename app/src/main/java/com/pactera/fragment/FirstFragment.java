package com.pactera.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.pactera.Util.MySharedPreference;
import com.pactera.Util.PrefKeys;
import com.pactera.api.RetrofitInstance;
import com.pactera.model.GraphModel;
import com.pactera.model.Queue;
import com.pactera.tesko.QueueAnalysisActivity;
import com.pactera.tesko.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by P0147845 on 11-11-2019.
 */

public class FirstFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "FirstFragment";
    private PieChart mPieNoOfPeople;
    private Spinner mSpinner;
    ImageView ivRefresh;
    int time;
    String queueName = " ";
    MySharedPreference preference;
    protected final String[] mNoOfPeopleArray = new String[]{
            "Q1", "Q2", "Q3", "Q4"
    };
    ArrayList<String> mNoOfPeopleArrayList = new ArrayList<>();
    int [] values = new int[]{
            20,30,35,15
    };
    ArrayList<Integer> valuesList = new ArrayList<>();
    ArrayList<Queue> sortedValuesList = new ArrayList<>();

    private QueueAnalysisActivity mQueueAnalysisActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_one, container, false);


//        TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
//        tv.setText(getArguments().getString("msg"));
        mQueueAnalysisActivity = ((QueueAnalysisActivity) getActivity());

        mSpinner =  rootView.findViewById(R.id.spnerInterval);
        mPieNoOfPeople = rootView.findViewById(R.id.noOfPeople);
        ivRefresh = rootView.findViewById(R.id.iv_refresh);
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

        // attaching data adapter to spinner
        mSpinner.setAdapter(dataAdapter);

        ivRefresh.setOnClickListener(view -> getAnalytics(time,queueName));
        //init();
        return rootView;
    }

    public static FirstFragment newInstance(String text) {

        FirstFragment f = new FirstFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


    private void init() {
        initPieChartForNumbOfPeople();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: initPieChartForNumbOfPeople");
        initPieChartForNumbOfPeople();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        Log.d(TAG, "onItemSelected: item"+item);
        //init();
        switch (position){
            case 0:
                time = 1;
                //initPieChartForNumbOfPeople();
                getAnalytics(time,queueName);
                break;
            case 1:
                time = 5;
                getAnalytics(time,queueName);
                break;
            case 2:
                time = 30;
                getAnalytics(time,queueName);
                break;
            case 3:
                time = 180;
                getAnalytics(time,queueName);
                break;
            case 4:
                time = 24;
                getAnalytics(time,queueName);
                break;
            default:
                //getAnalytics(time,queueName);
                break;
        }
        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

        Log.d(TAG, "onNothingSelected: ");

    }
    public void initPieChartForNumbOfPeople() {


        mPieNoOfPeople.getDescription().setEnabled(false);
//        mPieNoOfPeople.setExtraOffsets(-10, -5, 5, 5);
        mPieNoOfPeople.setExtraOffsets(5, -5, 5, 5);
        mPieNoOfPeople.setDragDecelerationFrictionCoef(0.95f);

//        chart.setCenterTextTypeface(tfLight);
        //mPieNoOfPeople.setCenterText(generateCenterSpannableText());

        mPieNoOfPeople.setDrawHoleEnabled(true);
        mPieNoOfPeople.setHoleColor(Color.WHITE);
        mPieNoOfPeople.setTransparentCircleColor(Color.WHITE);
        mPieNoOfPeople.setTransparentCircleAlpha(110);
        mPieNoOfPeople.setHoleRadius(40f);
        mPieNoOfPeople.setTransparentCircleRadius(0f);
        mPieNoOfPeople.setDrawCenterText(true);
        mPieNoOfPeople.setRotationAngle(0);
        mPieNoOfPeople.setUsePercentValues(true);
        // enable rotation of the mPieNoOfPeople by touch
        mPieNoOfPeople.setRotationEnabled(false);

       /* mPieNoOfPeople.setRotationEnabled(true);
        mPieNoOfPeople.setHighlightPerTapEnabled(true);*/
        // mPieNoOfPeople.setUnit(" €");
        // mPieNoOfPeople.setDrawUnitsInChart(true);
        // add a selection listener

//        mPieNoOfPeople.setOnChartValueSelectedListener(this);

        mPieNoOfPeople.animateY(1400, Easing.EaseInOutQuad);
        // mPieNoOfPeople.spin(2000, 0, 360);
        Legend l = mPieNoOfPeople.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(14f);
        l.setFormSize(14f);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(10f);
        l.setYOffset(10f);
        l.setEnabled(true);  // To hide/show Top hint square item in mPieNoOfPeople
        // entry label styling
        mPieNoOfPeople.setEntryLabelColor(Color.WHITE);
//        mPieNoOfPeople.setEntryLabelTypeface(tfRegular);
        mPieNoOfPeople.setEntryLabelTextSize(15f);
        mPieNoOfPeople.setDrawSliceText(true); // To remove slice text
        mPieNoOfPeople.setDrawMarkers(false); // To remove markers when click
        mPieNoOfPeople.setDrawEntryLabels(false); // To remove labels from piece of pie
        mPieNoOfPeople.getDescription().setEnabled(false); // To remove description of pie
        //setNoOfPeopleData(4, null);
        getAnalytics(1,queueName);

    }

    private void setNoOfPeopleData(int count, GraphModel model) {
        valuesList.clear();
        sortedValuesList.clear();
        sortedValuesList.addAll(model.getQueue());
        Collections.sort(sortedValuesList, Collections.reverseOrder());
        int size =0;
        for(Queue queue :sortedValuesList){
            if(size<7){
                valuesList.add(queue.getPercent_peop());
                mNoOfPeopleArrayList.add(queue.getqName());
            }
            size++;

        }
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the mPieChartCustProject.
        for (int i = 0; i < valuesList.size(); i++) {
//            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
//                    mEmplyeePool[i % mEmplyeePool.length],
//                    getResources().getDrawable(R.drawable.star)));

            entries.add(new PieEntry(valuesList.get(i),
                    mNoOfPeopleArrayList.get(i),
                    getResources().getDrawable(R.drawable.ic_launcher)));
        }

//        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);
        dataSet.setValueFormatter(new PercentFormatter(mPieNoOfPeople));
        dataSet.setSliceSpace(0f); // Space beetwenn slice  (white color space between pie chart)
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
      /*  for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());*/

        int[] EMP_EMPMATERIAL_COLORS1 = {
                mQueueAnalysisActivity.getResources().getColor(R.color.full_green),
                mQueueAnalysisActivity.getResources().getColor(R.color.pink),
                mQueueAnalysisActivity.getResources().getColor(R.color.gray),
                mQueueAnalysisActivity.getResources().getColor(R.color.sky_blue),
                mQueueAnalysisActivity.getResources().getColor(R.color.red),
                mQueueAnalysisActivity.getResources().getColor(R.color.green),
                mQueueAnalysisActivity.getResources().getColor(R.color.amber),
        };

        for (int c : EMP_EMPMATERIAL_COLORS1)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(mPieNoOfPeople));
        data.setValueTextSize(17f); // To hide text indie slice keep size 0
        data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(tfLight);
        mPieNoOfPeople.setData(data);

        // undo all highlights
        mPieNoOfPeople.highlightValues(null);

        mPieNoOfPeople.invalidate();
    }

    private void getAnalytics(int interval,String queueName){
        Log.d(TAG, "getAnalytics: interval "+interval);
        RetrofitInstance.getInstance(getActivity()).getRestAdapter()
                .getAnalytics(interval,queueName,preference.getPref(PrefKeys.StoreType).toLowerCase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GraphModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.getMessage());
                        //Toast.makeText(mQueueAnalysisActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GraphModel graphModel) {
                        Log.d(TAG, "onNext: graphModel"+graphModel.getQueue());


                        setNoOfPeopleData(4, graphModel);
                    }
                });

    }



}