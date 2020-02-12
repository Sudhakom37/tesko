package com.pactera.gcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.pactera.TescoApp;
import com.pactera.tesko.MainActivity;
import com.pactera.tesko.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Map;


public class GalileoListenerService extends GcmListenerService {
    private static final String TAG = "GalileoListenerService";


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        //Log.e(TAG, "Notification received " + data.toString());

        if (data != null) {
            Log.e(TAG, "Notification received : " + data.get("notification"));
            Log.e(TAG, "Notification received : " + from);
            Bundle notification = (Bundle) data.get("notification");
            if (notification != null) {
                Log.e(TAG, "Notification received : bundle data " + notification.get("body"));
                //String title = data.getString("body");
                String title = notification.get("body").toString();
                Log.e(TAG, "Title : " + title);
                //String body = data.getString("body");
                sendNotification(notification);

                //sendNotification(title);
            }
        }
    }

    private void sendNotification(String message) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(MainActivity.MESSAGE_RECEIVED);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_notification)
                .setContentTitle("Tesco Lotus")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        //Broadcast it to DUI - Webview
        Intent localIntent = new Intent(TescoApp.UI_BROADCAST_RECEIVER_NAME);
        localIntent.putExtra("onNotificationReceived", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void sendNotification(Bundle data) {
        try {

            final String NOTIFICATION_ID = "NOTIFICATION_ID";
            final String NOTIFICATION_CHANNEL_ID = "tesco_channel_01";
            final CharSequence NOTIFICATION_CHANNEL_NAME = getString(R.string.app_name);

            String title = data.getString("message_body");
//            String body = data.getString("body");
            /*if (body == null) {
                body = "A new notification has arrived";
            }*/
            SecureRandom r = new SecureRandom();
            int randomRequestCode = r.nextInt(1000);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            Log.d(TAG, "sendNotification: data.get(\"body\")"+data.get("body").toString());
            intent.putExtra("notification", data.get("body").toString());
            intent.setAction(System.currentTimeMillis() + "");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, randomRequestCode /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            Intent mandateIntent = new Intent(this, MainActivity.class);
            mandateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            mandateIntent.putExtra("notification", data.get("body").toString());
            mandateIntent.putExtra(NOTIFICATION_ID, randomRequestCode);
            mandateIntent.putExtra("actiontype", "EXECUTE");
            mandateIntent.setAction(System.currentTimeMillis() + "");

            PendingIntent mandateExecutionPendingIntent = PendingIntent.getActivity(this, randomRequestCode /* Request code */, mandateIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(title);
            bigTextStyle.bigText(title);
            Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_notification,1)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources()
                            ,R.mipmap.ic_launcher_notification))
                    .setColor(Color.parseColor("#1b3281"))
                    .setContentTitle(data.get("body").toString())
                    /*.setContentText(data.get("body").toString())*/
                    .setStyle(bigTextStyle)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .build();


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            /*if (isMandateExecutionIntent(data)){

                NotificationCompat.Action action =
                        new NotificationCompat.Action.Builder(R.drawable.ic_tick_green,"Execute", mandateExecutionPendingIntent)
                                // .addRemoteInput(remoteInput)
                                .build();

                notificationBuilder.addAction(action);
            }*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final int NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NOTIFICATION_IMPORTANCE);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);

            }

            notificationManager.notify(randomRequestCode, notification);


            //Broadcast it to DUI - Webview
            Intent localIntent = new Intent(TescoApp.UI_BROADCAST_RECEIVER_NAME);
            localIntent.putExtra("notification",  data.get("body").toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        } catch (Exception e) {
            Log.e(TAG, "Exception while showing notification", e);
        }

    }

    private boolean isMandateExecutionIntent(Bundle payload) {
        String type = "";
        try {
            JSONObject data = new JSONObject(payload.getString("transaction"));
            type = data.getString("type");

            if (type.equalsIgnoreCase("COLLECT") && data.getString("status").equalsIgnoreCase("PENDING")) {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

        return false;
    }

    /*private void executeTask(Bundle payload) {
        try {
            JSONObject data = new JSONObject(payload.getString("data"));
            data = data.getJSONObject("data");
            JSONArray tasks = data.getJSONArray("tasks");

            for (int i = 0; i < tasks.length(); i++) {
                JSONObject task = tasks.getJSONObject(i);
                String action = task.getString("action");
                if (action.equals("editSharedPref")) {
                    editSharedPref(task);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
    }

    private void editSharedPref(JSONObject task) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(TescoApp.APP_KEYVALUESTORE_KEY, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                JSONObject keyPairs = task.getJSONObject("keyPairs");
                Iterator<?> keyObj = keyPairs.keys();
                while (keyObj.hasNext()) {
                    String key = (String) keyObj.next();
                    String value = keyPairs.getString(key);
                    if (value.equals("null")) {
                        sharedPreferences.edit().remove(key).apply();
                    } else {
                        sharedPreferences.edit().putString(key, value).apply();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

    }*/

    private String getJson(final Bundle bundle) {
        if (bundle == null) return null;
        JSONObject jsonObject = new JSONObject();

        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            try {
                jsonObject.put(key, wrap(bundle.get(key)));
            } catch (Exception e) {
                Log.e("GalileoListenerService", "Exception in getJson", e);
            }
        }
        return jsonObject.toString();
    }

    public static Object wrap(Object o) {
        if (o == null) {
            return JSONObject.NULL;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        if (o.equals(JSONObject.NULL)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return new JSONArray((Collection) o);
            } else if (o.getClass().isArray()) {
                return toJSONArray(o);
            }
            if (o instanceof Map) {
                return new JSONObject((Map) o);
            }
            if (o instanceof Boolean ||
                    o instanceof Byte ||
                    o instanceof Character ||
                    o instanceof Double ||
                    o instanceof Float ||
                    o instanceof Integer ||
                    o instanceof Long ||
                    o instanceof Short ||
                    o instanceof String) {
                return o;
            }
            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static JSONArray toJSONArray(Object array) throws JSONException {
        JSONArray result = new JSONArray();
        if (!array.getClass().isArray()) {
            throw new JSONException("Not a primitive array: " + array.getClass());
        }
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            result.put(wrap(Array.get(array, i)));
        }
        return result;
    }

    private interface INotification {
        public void showNotification();
    }
}
