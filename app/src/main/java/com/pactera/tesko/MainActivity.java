package com.pactera.tesko;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.pactera.TescoApp;
import com.pactera.Util.StackedValueFormatter;
import com.pactera.adapter.NotificationAdapter;
import com.pactera.api.RetrofitInstance;
import com.pactera.custom.MyBarDataSet;
import com.pactera.custom.StackedBarDataset;
import com.pactera.gcm.RegistrationIntentService;
import com.pactera.model.GraphModel;
import com.pactera.model.Queue;
import com.pactera.room.Notification;
import com.pactera.room.NotificationViewModel;
import com.pactera.sync.LocationUpdatesService;
import com.pushbots.push.Pushbots;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import android.widget.Spinner;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.pactera.sync.LocationUpdatesService.ACTION_RESP;

public class MainActivity extends FragmentActivity implements View.OnClickListener, OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {

    private BarChart chart;
    private RecyclerView mReclNotfHistory;
    private NotificationAdapter mNotifAdapter;
    private List<String> mNotfList = new ArrayList<>();
    private TextView mTxtNoHistory, mTxtNotif;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String MESSAGE_RECEIVED = "message_received";
    private UIBroadCastReceiver uiBroadCastReceiver;
    //    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/instinctcoder/readwrite/";
    String path = Environment.getExternalStorageDirectory() + File.separator + "Tesco/";
    Handler handler;
    private String TAG = "MainActivity";
    String[] timeSpinnerValues = {"Select", "1 min", "2 min", "5 min", "10 min"};
    Spinner timeSpinner;
    Button btnAnalytic;
    List<String> xAxisValues;
    NotificationViewModel viewModel;
    List<Notification> notificationList;
    TextView queue_lessthan_3;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    private MyReceiver myReceiver;

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
        setContentView(R.layout.activity_main_new);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        myReceiver = new MyReceiver();
        mReclNotfHistory = findViewById(R.id.reclNotfHistory);
        mTxtNoHistory = findViewById(R.id.txtNoHistory);
        mTxtNotif = findViewById(R.id.txtNotif);
        queue_lessthan_3 = findViewById(R.id.queue_lessthan_3);
        RelativeLayout mrlvtExit = findViewById(R.id.rlvtExit);
        mrlvtExit.setOnClickListener(this);
        queue_lessthan_3.setText("<3");
        viewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        //viewModel.getBarChart("2");
        chart = findViewById(R.id.chart1);
        timeSpinner = findViewById(R.id.sp);
        btnAnalytic = findViewById(R.id.btn_analytic);
        Log.d(TAG, "onCreate: path " + path);
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        btnAnalytic.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, QueueAnalysisActivity.class)));
        mNotfList = new ArrayList<>();
        if (!checkPermissions()) {
            Permissions.check(this, Manifest.permission.ACCESS_FINE_LOCATION, null, new PermissionHandler() {
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

        prepareNotificaitonList();
        registerForGCM();

        uiBroadCastReceiver = new UIBroadCastReceiver();
        registerUIBroadCastReceiver(uiBroadCastReceiver, TescoApp.UI_BROADCAST_RECEIVER_NAME);

        //Register for Push Notifications
        Pushbots.sharedInstance().registerForRemoteNotifications();

        setTimeSpinner();
        setAlarm(1);

    }

    private void observeData() {

        viewModel.getAllWords().observe(this, notifications -> {
            notificationList = notifications;

            if (notifications != null) {
                for (Notification notification : notifications) {
                    mNotfList.add(notification.getNotificationText());
                    Log.d(TAG, "observeData: "+notification.getNotificationText()+" Date "+notification.getDate());
                }
            }
            prepareNotificaitonList();
            mNotifAdapter = new NotificationAdapter(MainActivity.this, mNotfList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mReclNotfHistory.setLayoutManager(mLayoutManager);
            mReclNotfHistory.setItemAnimator(new DefaultItemAnimator());
            mReclNotfHistory.setAdapter(mNotifAdapter);
            mNotifAdapter.notifyDataSetChanged();


        });

    }

    private void setAlarm(int time) {
        Log.d(TAG, "AlarmReceiver set");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * time, pendingIntent);

    }


    public void getBarChart(int interval) {

        Log.d(TAG, "Token for my GCM Listener is : " + interval);

        RetrofitInstance.getInstance(this)
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
                        //Toast.makeText(MainActivity.this, "Chart Updated ",Toast.LENGTH_SHORT).show();
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

        chart.setOnChartValueSelectedListener(this);
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

            set1.setColors(getColors());
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

    void setBarsLocal(List<Queue> queues) {

        ArrayList<BarEntry> values = new ArrayList<>();
        //readBarGraphData(values,queues);
        readBarGraphDataLocal(values, queues);
        //ArrayList<String> xVlaues= new ArrayList<>();

        StackedBarDataset set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {

            set1 = (StackedBarDataset) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new StackedBarDataset(values, "");
            set1.setDrawIcons(false);

            set1.setColors(getColors());
            //set1.setStackLabels(new String[]{"<3", "4", "5",">6"});
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);

            //data.setValueFormatter(new StackedValueFormatter(true, "", 1));
            data.setValueFormatter(new IndexAxisValueFormatter());
            data.setBarWidth(.40f);

            data.setValueTextColor(Color.TRANSPARENT);

            chart.setData(data);
        }

        chart.setFitBars(true);
        chart.invalidate();
    }


    void readBarGraphData(ArrayList<BarEntry> values, List<Queue> queues) {

        ArrayList<String> xVlaues = new ArrayList<>();
        Log.d(TAG, "readBarGraphData: " + readJSONFromAsset());

        JSONObject mainObj = null;
        try {
            mainObj = new JSONObject(readJSONFromAsset());


            JSONArray list = mainObj.getJSONArray("queue");


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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xAxisValues = xVlaues;

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        //chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter());
        //chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter());

    }

    void readBarGraphDataLocal(ArrayList<BarEntry> values, List<Queue> queues) {
        try {

            ArrayList<String> xVlaues = new ArrayList<>();


            JSONObject mainObj = new JSONObject(readJSONFromAsset());

            JSONArray list = mainObj.getJSONArray("queue");
            if (list != null) {

                for (int i = 0; i < list.length(); i++) {

                    JSONObject elem = list.getJSONObject(i);
                    if (elem != null) {

                        xVlaues.add(elem.getString("qText"));
                        JSONArray prods = elem.getJSONArray("queue_number");


                        float[] q1Vals = new float[prods.length()];
                        for (int j = 0; j < prods.length(); j++) {
                            q1Vals[j] = prods.optInt(j);
                            BarEntry barEntry = new BarEntry(
                                    i,
                                    q1Vals,
                                    getResources().getDrawable(R.drawable.ic_launcher_foreground));
                            values.add(barEntry);
                        }
                    }
                }
            }
            xAxisValues = xVlaues;
            chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    String myNotification = "";

    void notificationData(String notificationData) {
        // {"queue":[{"queue_number":[1,2,3,4],"qText":"1-4"},{"queue_number":[5,6,7,8],"qText":"5-9"},{"queue_number":[9,10,11,12],"qText":"9-12"},{"queue_number":[1,2,3,4],"qText":"12-15"}]}
        myNotification = notificationData;
        Log.d(TAG, "notificationData: " + notificationData);
        //setUpChart();
    }

    int time = 0;

    public String readJSONFromAsset() {
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
            is.read(buffer);
            is.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                json = new String(buffer, StandardCharsets.UTF_8);
            } else {
                json = new String(buffer, "UTF-8");
            }
            //json = myNotification;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private int[] getColors() {

        // have as many colors as stack-values per entry
        //   int[] colors = new int[4];

        //System.arraycopy(MATERIAL_COLORS, 0, colors, 0, 6);
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
        getBarChart(1);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        registerUIBroadCastReceiver(uiBroadCastReceiver, TescoApp.UI_BROADCAST_RECEIVER_NAME);
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
        unregisterCallback(uiBroadCastReceiver);

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

    private void prepareNotificaitonList() {
        if (mNotfList.size() <= 0) {
            mTxtNoHistory.setVisibility(View.VISIBLE);
            mReclNotfHistory.setVisibility(View.GONE);
        } else {
            mReclNotfHistory.setVisibility(View.VISIBLE);
            mTxtNoHistory.setVisibility(View.GONE);
        }
//        setList("notfHistory",mNotfList);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rlvtExit) {
            finish();
        }
    }

    private void registerUIBroadCastReceiver(BroadcastReceiver receiver, String action) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class GcmBroadcastReceiver extends BroadcastReceiver {

        // Incoming Intent key for extended data
        public static final String KEY_SYNC_REQUEST =
                "com.example.android.datasync.KEY_SYNC_REQUEST";

        @Override
        public void onReceive(Context context, Intent intent) {
            // Get a GCM object instance
            GoogleCloudMessaging gcm =
                    GoogleCloudMessaging.getInstance(context);
            // Get the type of GCM message
            String messageType = gcm.getMessageType(intent);
            /*
             * Test the message type and examine the message contents.
             * Since GCM is a general-purpose messaging system, you
             * may receive normal messages that don't require a sync
             * adapter run.
             * The following code tests for a a boolean flag indicating
             * that the message is requesting a transfer from the device.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)
                    &&
                    intent.getBooleanExtra(KEY_SYNC_REQUEST, false)) {
                /*
                 * Signal the framework to run your sync adapter. Assume that
                 * app initialization has already created the account.
                 */
                //ContentResolver.requestSync(mAccount, AUTHORITY, null);

            }
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

//                Toast.makeText(SyncActivity.this,getString(R.string.thresold_msg)+" "+notificationData,Toast.LENGTH_SHORT).show();
                    Log.e("SyncActivity", "Inside Broadcast Receiver : " + notificationData);

                    mTxtNotif.setText(notificationData);
                    notificationData(notificationData);
                   /* mNotfList.add(notificationData);
//                            mNotfList.add(intent.getStringExtra("onNotificationReceived"));
                    Collections.reverse(mNotfList);
                    Log.e("MainACtivity", "Inside Broadcasr REceiver : mNotfList : " + mNotfList.size());
//                Notification mNotication = new Notification(notificationData);
                    mNotifAdapter.notifyDataSetChanged();
                    if (mNotfList.size() <= 0) {
                        mTxtNoHistory.setVisibility(View.VISIBLE);
                        mReclNotfHistory.setVisibility(View.GONE);
                    } else {
                        mReclNotfHistory.setVisibility(View.VISIBLE);
                        mTxtNoHistory.setVisibility(View.GONE);
                    }*/
//                    mTxtNotif.setText("-");
                    /*String s = new Gson().toJson(notificationData);

                    JSONObject object = new JSONObject(s);

                    Log.d(TAG, "onReceive: Gson"+object.getInt("Q2"));*/

                    //mNotfList.add(notificationData);
                    Notification n = new Notification();
                    n.setNotificationText(notificationData);
                    n.setDate(System.currentTimeMillis());
                    viewModel.insert(n);

                    handler = new Handler();
                    handler.postDelayed(() -> {
                        //Do something after 1 min =60000

//                            mNotfList.add(intent.getStringExtra("onNotificationReceived"));
                        //Collections.reverse(mNotfList);
                        Log.e("SyncActivity", "Inside Broadcast Receiver : mNotfList : " + mNotfList.size());
//                Notification mNotication = new Notification(notificationData);
                        mNotifAdapter.notifyDataSetChanged();
                        if (mNotfList.size() <= 0) {
                            mTxtNoHistory.setVisibility(View.VISIBLE);
                            mReclNotfHistory.setVisibility(View.GONE);
                        } else {
                            mReclNotfHistory.setVisibility(View.VISIBLE);
                            mTxtNoHistory.setVisibility(View.GONE);
                        }
                        observeData();
                        //mNotifAdapter.notifyDataSetChanged();
                        //mTxtNotif.setText("-");
                    }, 1500);


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

    /*public boolean writeToFile(String data) {
        try {

            File mediaStorageDir = new File(path);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "Oops! Failed create "
                            + path + " directory");
                    return false;
                }
            }

            File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "Tesco.txt");
            mediaFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(mediaFile, false);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());
            fileOutputStream.close();

            return true;
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return false;
    }*/

    void setTimeSpinner() {

        timeSpinner.setOnItemSelectedListener(this);
        ArrayAdapter timeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSpinnerValues);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        timeSpinner.setAdapter(timeAdapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        if (position == 1) {
            time = 1;
            mService.requestLocationUpdates();
            mService.setTimeInterval(time);


            //setDataInterval(time);
            setAlarm(time);
            getBarChart(time);
        } else if (position == 2) {
            time = 2;
            mService.setTimeInterval(time);
            //setDataInterval(time);
            setAlarm(time);
            getBarChart(time);
        } else if (position == 3) {
            time = 5;
            mService.setTimeInterval(time);
            //setDataInterval(time);
            setAlarm(time);
            getBarChart(time);
        } else if (position == 4) {
            time = 10;
            mService.setTimeInterval(time);
            //setDataInterval(time);
            setAlarm(time);
            getBarChart(time);
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
            getBarChart(time);


        }
    }

}
