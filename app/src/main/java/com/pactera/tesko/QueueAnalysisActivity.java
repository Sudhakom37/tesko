package com.pactera.tesko;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pactera.Util.MySharedPreference;
import com.pactera.Util.PrefKeys;
import com.pactera.adapter.QueueAnalysisAdapter;

import java.util.ArrayList;
import java.util.List;

@Keep
public class QueueAnalysisActivity extends FragmentActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    private static String TAG="QueueAnalysisActivity";
    private RadioGroup mPageGroup;
    private ViewPager viewPager;
    private Spinner mSpinner;
    RelativeLayout rlvtHome,rlvtExit,rl_my_hr,rlvtIncActivty;
    QueueAnalysisAdapter adapter;
    TextView tv_view_msg ;
    MySharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_analysis);
        tv_view_msg = findViewById(R.id.tv_view_msg);
        viewPager =  findViewById(R.id.viewpager);
        rlvtHome = findViewById(R.id.rl_viewQ1_Q15);
        rl_my_hr = findViewById(R.id.rl_my_hr);
        rlvtIncActivty = findViewById(R.id.rlvtIncActivty);
        adapter = new QueueAnalysisAdapter(getSupportFragmentManager());
        rlvtExit = findViewById(R.id.rlvtExit);
        viewPager.setAdapter(adapter);
        preference = new MySharedPreference(this);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.setupWithViewPager(pager, true);

        mPageGroup =  findViewById(R.id.page_group);
        mPageGroup.setOnCheckedChangeListener(this);

        viewPager.addOnPageChangeListener(this);
        mSpinner =  findViewById(R.id.spnerInterval);

        mSpinner.setOnItemSelectedListener(this);

        rl_my_hr.setOnClickListener(view -> startActivity(new Intent(QueueAnalysisActivity.this,MyHrActivity.class)));
        rlvtIncActivty.setOnClickListener(view -> startActivity(new Intent(QueueAnalysisActivity.this,MyHrActivity.class)));

        if(preference.getPref(PrefKeys.QUEUE_NAME).equalsIgnoreCase("Q1")){
            tv_view_msg.setBackgroundColor(getResources().getColor(R.color.blue));
            tv_view_msg.setText("You have currently selected to view status of Queues 1 - 15");
        }else{
            tv_view_msg.setBackgroundColor(getResources().getColor(R.color.pactera_red));
            tv_view_msg.setText("You have currently selected to view status of Queues 16 - 30");
        }

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("1-Min");
        categories.add("5-Min");
        categories.add("30-Min");
        categories.add("3-Hrs");
        categories.add("Full Day");

        rlvtHome.setOnClickListener(view -> finish());
        rlvtExit.setOnClickListener(view -> finish());
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinner.setAdapter(dataAdapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.e(TAG,"Page scroll : "+position);
        int radioButtonId = mPageGroup.getChildAt(position).getId();
        mPageGroup.check(radioButtonId);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        RadioButton checkedRadioButton =  radioGroup.findViewById(checkedId);
        // get index of checked radio button
        int index = radioGroup.indexOfChild(checkedRadioButton);

        // update current page
        viewPager.setCurrentItem(index, true);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
