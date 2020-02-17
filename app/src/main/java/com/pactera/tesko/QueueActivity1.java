package com.pactera.tesko;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import com.pactera.TescoApp;
import com.pactera.Util.MySharedPreference;
import com.pactera.Util.MyValueFormatter;
import com.pactera.Util.PrefKeys;
import com.pactera.adapter.NotificationAdapter;
import com.pactera.api.RetrofitInstance;
import com.pactera.custom.MyBarDataSet;
import com.pactera.gcm.RegistrationIntentService;
import com.pactera.model.GraphModel;
import com.pactera.model.Queue;
import com.pactera.room.Notification;
import com.pactera.room.NotificationViewModel;
import com.pactera.sync.LocationUpdatesService;

import com.pushbots.push.Pushbots;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.pactera.sync.LocationUpdatesService.ACTION_RESP;

public class QueueActivity1 extends FragmentActivity implements View.OnClickListener, OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {

    private BarChart chart;
    private RecyclerView mRecNotHistory;
    private NotificationAdapter mNotIfAdapter;
    private List<String> mNotIfList = new ArrayList<>();
    private TextView mTxtNoHistory, mTxtNotIf;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String MESSAGE_RECEIVED = "message_received";
    private UIBroadCastReceiver uiBroadCastReceiver;
    String path = Environment.getExternalStorageDirectory() + File.separator + "Tesco/";
    Handler handler;
    private String TAG = "QueueActivity1";
    String[] timeSpinnerValues = {"Select", "1 min", "2 min", "5 min", "10 min"};
    Spinner timeSpinner;
    Button btnAnalytic;
    List<String> xAxisValues;
    NotificationViewModel viewModel;
    List<Notification> notificationList;
    TextView queue_lessThan_3;
    RelativeLayout rl_viewQ1_Q15, rl_viewQ16_30;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    private MyReceiver myReceiver;
    MySharedPreference preference;

    String queueName = "Q1";
    int time = 1;
    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue1);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        myReceiver = new MyReceiver();
        mRecNotHistory = findViewById(R.id.reclNotfHistory);
        mTxtNoHistory = findViewById(R.id.txtNoHistory);
        mTxtNotIf = findViewById(R.id.txtNotif);
        rl_viewQ1_Q15 = findViewById(R.id.rl_viewQ1_Q15);
        rl_viewQ16_30 = findViewById(R.id.rl_viewQ16_30);
        queue_lessThan_3 = findViewById(R.id.queue_lessthan_3);
        RelativeLayout mRlExit = findViewById(R.id.rlvtExit);
        mRlExit.setOnClickListener(this);
        queue_lessThan_3.setText("<=3");
        viewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        rl_viewQ1_Q15.setOnClickListener(view -> finish());
        //viewModel.getBarChart("2");
        chart = findViewById(R.id.bar_chart);
        timeSpinner = findViewById(R.id.sp);
        btnAnalytic = findViewById(R.id.btn_analytic);
        Log.d(TAG, "onCreate: path " + path);
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        preference = new MySharedPreference(this);
        queueName =preference.getPref(PrefKeys.QUEUE_NAME);
        Log.d(TAG, "onCreate: queueName " + queueName);
        setUpChart(chart);

        btnAnalytic.setOnClickListener(view -> startActivity(new Intent(QueueActivity1.this, QueueAnalysisActivity.class)));
        mNotIfList = new ArrayList<>();
        if (!checkPermissions()) {
            Permissions.check(this, android.Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
                @Override
                public void onGranted() {

                }
            });
        }
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(MESSAGE_RECEIVED)) {
                    String message = intent.getStringExtra("message");

                    Log.e("SyncActivity", "Notification status message : " + message);
                }
            } else {
                Log.e("SyncActivity", "intent.getAction()!=null : ");
            }
        } else {
            Log.e("SyncActivity", "intent != null : ");
        }

        prepareNotificationList();
        registerForGCM();

        uiBroadCastReceiver = new QueueActivity1.UIBroadCastReceiver();
        //registerUIBroadCastReceiver(uiBroadCastReceiver, TescoApp.UI_BROADCAST_RECEIVER_NAME);

        //Register for Push Notifications
        Pushbots.sharedInstance().registerForRemoteNotifications();
        getBarChart(1, queueName);
        setTimeSpinner();
        setAlarm(1);

    }

    private void observeData() {
        mNotIfList.clear();
        viewModel.getAllWords().observe(this, notifications -> {
            notificationList = notifications;

            if (notifications != null) {
                for (Notification notification : notifications) {
                    mNotIfList.add(notification.getNotificationText());
                    Log.d(TAG, "observeData: " + notification.getNotificationText() + " Date " + notification.getDate());
                }
            }
            prepareNotificationList();
            mNotIfAdapter = new NotificationAdapter(mNotIfList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecNotHistory.setLayoutManager(mLayoutManager);
            mRecNotHistory.setItemAnimator(new DefaultItemAnimator());
            mRecNotHistory.setAdapter(mNotIfAdapter);
            mNotIfAdapter.notifyDataSetChanged();
            //runOnUiThread(() -> exportData(notificationList));

        });

    }

    private void setAlarm(int time) {
        Log.d(TAG, "AlarmReceiver set");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * time, pendingIntent);

    }


    public void getBarChart(int interval, String queueName) {

        Log.d(TAG, "Token for my GCM Listener is : " + queueName+" , StoreType "+preference.getPref("StoreType"));

        RetrofitInstance.getInstance(this)
                .getRestAdapter()
                .getQueues1to15(interval, queueName,preference.getPref("StoreType").toLowerCase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GraphModel>() {
                    @Override
                    public void onCompleted() {

                        Log.d(TAG, "onCompleted: ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: Throwable " + e.getMessage());

                    }

                    @Override
                    public void onNext(GraphModel graphModel) {
                        //Toast.makeText(QueueActivity1.this, "Chart Updated ",Toast.LENGTH_SHORT).show();

                        setData(5, 9, graphModel.getQueue());
                        //setBars(chart,graphModel.getQueue());

                    }
                });

    }

    void setUpChart(@NonNull BarChart chart) {

        setChartConfiguration(chart);
        removeYAxisAndXAxisChartBg(chart);
        setYAxis(chart);
        setXAxis(chart);
        setLegend(chart);

    }


    void setChartConfiguration(@NonNull BarChart chart) {


        chart.setMaxVisibleValueCount(40);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setHighlightFullBarEnabled(false);
        chart.setExtraBottomOffset(15f);
        chart.getXAxis().setAxisLineWidth(1f);
        chart.getAxisLeft().setAxisLineWidth(1f);

        chart.getAxisLeft().setAxisLineColor(rgb("#000000"));
        chart.getXAxis().setAxisLineColor(rgb("#000000"));

        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);

        chart.getDescription().setEnabled(false);


    }

    void removeYAxisAndXAxisChartBg(@NonNull BarChart chart) {
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


    void setLegend(@NonNull BarChart chart) {

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

    void setYAxis(@NonNull BarChart chart) {

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.setLabelCount(5, true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setSpaceTop(15f);
        leftAxis.setDrawGridLines(false);
        //leftAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        leftAxis.setValueFormatter(new MyValueFormatter(""));
        //leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        chart.getAxisRight().setEnabled(false);

    }

    void setXAxis(@NonNull BarChart chart) {

        XAxis xLabels = chart.getXAxis();
        //xLabels.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
    }


    private void setData(int count, float range, List<Queue> queues) {

        ArrayList<BarEntry> values = new ArrayList<>();
        readBarGraphData(values, queues);
        /*float start = 1f;

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));

            if (Math.random() * 100 < 25) {
                values.add(new BarEntry(i, val));
            } else {
                values.add(new BarEntry(i, val));
            }
        }*/
        MyBarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (MyBarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new MyBarDataSet(values, "The year 2017");

            set1.setDrawIcons(false);
            set1.setColors(getColors());

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            //data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter(""));
            chart.setData(data);
            chart.invalidate();
        }
    }


    void setBars(@NonNull BarChart chart, List<Queue> queues) {

        ArrayList<BarEntry> values = new ArrayList<>();

        float start = 1f;
        for (int i = (int) start; i < start + 5; i++) {
            float val = (float) (Math.random() * (9 + 1));

            if (Math.random() * 100 < 25) {
                values.add(new BarEntry(i, val));
            } else {
                values.add(new BarEntry(i, val));
            }
        }
        //readBarGraphData(values, queues);
        //readBarGraphDataLocal(values,queues);
        //ArrayList<String> xValues = new ArrayList<>();

        MyBarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {

            set1 = (MyBarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new MyBarDataSet(values, "Queue Data");
            set1.setValues(values);
            set1.setDrawIcons(false);
            set1.setDrawValues(true);
            set1.setColors(getColors());
            //set1.setStackLabels(new String[]{"<3", "4", "5",">6"});
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
//            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);
            //data.setValueFormatter(new StackedValueFormatter(true, "", 1));
            data.setValueFormatter(new IndexAxisValueFormatter());
            //data.setBarWidth(.40f);

            //data.setValueTextColor(Color.TRANSPARENT);

            chart.setData(data);


        }

        //chart.setFitBars(true);
        chart.invalidate();
    }

    void readBarGraphData(ArrayList<BarEntry> values, List<Queue> queues) {

        ArrayList<String> xValues = new ArrayList<>();

        if (queues != null) {

            for (int i = 0; i < queues.size(); i++) {

                //JSONObject elem =(JSONObject) list.get(i);
                Queue elem = queues.get(i);
                if (elem != null) {

                    xValues.add(elem.getqName());

                    //List<Integer> prods = (List<Integer>)queues.get(i);
                    float val = (float) elem.getQueueNumber();
                    //JSONArray prods = elem.getJSONArray("queue_number");
                    BarEntry barEntry = new BarEntry(
                            i,
                            val);
                    //barEntry.setY(val);

                    values.add(barEntry);
                }
            }
        }

        xAxisValues = xValues;

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        chart.invalidate();
    }

    String myNotification = "";

    void notificationData(String notificationData) {
        // {"queue":[{"queue_number":[1,2,3,4],"qText":"1-4"},{"queue_number":[5,6,7,8],"qText":"5-9"},{"queue_number":[9,10,11,12],"qText":"9-12"},{"queue_number":[1,2,3,4],"qText":"12-15"}]}
        myNotification = notificationData;
        Log.d(TAG, "notificationData: " + notificationData);
    }



    /*public String readJSONFromAsset() {
        String json;
        try {
            String fileName = "graphFile1min.json";
            switch (time) {
                case 0:
                    fileName = "graphFile1min.json";
                    break;
                case 1:
                    fileName = "graphFile.json";
                    break;
                case 2:
                    fileName = "graphFile2min.json";
                    break;
            }

            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                json = new String(buffer, StandardCharsets.UTF_8);
            } else {
                json = new String(buffer, getResources().getString(R.string.utf));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }*/

    private int[] getColors() {

        return MATERIAL_COLORS;
    }

    public static final int[] MATERIAL_COLORS = {

            rgb("#3F51B5"), rgb("#00FF00"), rgb("#ffbf00"), rgb("#FF0000")

    };


    public static int rgb(@NonNull String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        return Color.rgb(r, g, b);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {


    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        observeData();


        //getBarChart(1,queueName);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        registerUIBroadCastReceiver(uiBroadCastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "Inside onPause");
        unregisterCallback(uiBroadCastReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Inside onDestroy");
        //unregisterCallback(uiBroadCastReceiver);

    }

    private void registerForGCM() {
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            return false;
        }
        return true;
    }

    private void prepareNotificationList() {
        if (mNotIfList.size() <= 0) {
            mTxtNoHistory.setVisibility(View.VISIBLE);
            mRecNotHistory.setVisibility(View.GONE);
        } else {
            mRecNotHistory.setVisibility(View.VISIBLE);
            mTxtNoHistory.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rlvtExit) {
            finish();
        }
    }

    private void registerUIBroadCastReceiver(BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(TescoApp.UI_BROADCAST_RECEIVER_NAME);
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class UIBroadCastReceiver extends BroadcastReceiver {

        //public boolean firstConnect = true;

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Log.e("SyncActivity", "Inside Broadcast Receiver : ");

                String notificationData = intent.getStringExtra("notification");
                String token = intent.getStringExtra("onTokenReceived");

                Log.d(TAG, "onReceive: notificationData" + notificationData);

                Log.d(TAG, "onReceive: token" + token);
                if (!TextUtils.isEmpty(token)) {
                    Log.e("SyncActivity", "Token received");
                    //writeToFile(token);
                    return;
                }
                /*String newToken = intent.getStringExtra("onTokenRefreshed");
                String reminderData = intent.getStringExtra("reminder");*/

                if (!TextUtils.isEmpty(notificationData)) {

                    Log.e("SyncActivity", "Inside Broadcast Receiver : " + notificationData);

                    mTxtNotIf.setText(notificationData);
                    notificationData(notificationData);

                    Notification n = new Notification();
                    n.setNotificationText(notificationData);
                    n.setDate(System.currentTimeMillis());
                    viewModel.insert(n);

                    handler = new Handler();
                    handler.postDelayed(() -> {
                        Log.e("SyncActivity", "Inside Broadcast Receiver : mNotIfList : " + mNotIfList.size());
                        mNotIfAdapter.notifyDataSetChanged();
                        if (mNotIfList.size() <= 0) {
                            mTxtNoHistory.setVisibility(View.VISIBLE);
                            mRecNotHistory.setVisibility(View.GONE);
                        } else {
                            mRecNotHistory.setVisibility(View.VISIBLE);
                            mTxtNoHistory.setVisibility(View.GONE);
                        }
                        observeData();
                    }, 1000);

                }

            } catch (Exception e) {

                Log.e(TAG, "Exception : " + e.toString());
            }
        }
    }

    private void unregisterCallback(BroadcastReceiver broadcastReceiver) {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void setTimeSpinner() {

        timeSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSpinnerValues);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        timeSpinner.setAdapter(timeAdapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        if (position == 1) {
            time = 1;
            //mService.setTimeInterval(time);
            //setAlarm(time);
            getBarChart(time, queueName);
        } else if (position == 2) {
            time = 2;
            //mService.setTimeInterval(time);
            //setAlarm(time);
            getBarChart(time, queueName);
        } else if (position == 3) {
            time = 5;
            //mService.setTimeInterval(time);
            //setAlarm(time);
            getBarChart(time, queueName);
        } else if (position == 4) {
            //time = 10;

            //setAlarm(time);
            getBarChart(time, queueName);
        }
        if(mService != null){
            mService.requestLocationUpdates();
            mService.setTimeInterval(time);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver((myReceiver),
                new IntentFilter(ACTION_RESP)
        );
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }

        super.onStop();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);

            Log.d(TAG, "onReceive: Synced With Server " + time);
            getBarChart(time, queueName);


        }
    }

    private void exportData(List<Notification> notificationList) {

        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "QueueData.xls";
        File directory = new File(sd.getAbsolutePath());
        //create directory if not exist
        if (!directory.isDirectory()) {
            boolean isSuccess = directory.mkdirs();
            Log.d(TAG, "exportData: directory.mkdirs" + isSuccess);
        }
        try {

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("QueueTestCases", 0);
            // column and row
            sheet.addCell(new Label(0, 0, "Queue Text"));
            sheet.addCell(new Label(1, 0, "Time Stamp"));
            int i = 0;
            Log.d(TAG, "exportData: " + viewModel.getAllWords().getValue());
            for (Notification n : notificationList) {
                Log.d(TAG, "exportData: " + n.getNotificationText());
                i++;
                String name = n.getNotificationText();
                String date = getDate(n.getDate());
                sheet.addCell(new Label(0, i, name));
                sheet.addCell(new Label(1, i, date));
            }
            workbook.write();
            workbook.close();
            Toast.makeText(getApplication(),
                    "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "exportData: ", e.getCause());
        }
    }

    private String getDate(long date) {

        SimpleDateFormat format = new SimpleDateFormat("HH:MM:SS", Locale.ENGLISH);
        return format.format(new Date(date));

    }

}
