package com.pactera.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.JsonObject;
import com.pactera.TescoApp;
import com.pactera.api.RetrofitInstance;
import com.pactera.model.Success;
import com.pactera.tesko.R;

import java.io.IOException;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by parthvora on 18/12/16.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationService";
    private static final String[] TOPICS = {"allcustomers"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);
//            subscribeTopics(token);
            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            //sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
           // Logger.e(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            //sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        //Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Log.d(TAG,"Token for my GCM Listener is : "+token);

        JsonObject object = new JsonObject();
        object.addProperty("key",token);
        RetrofitInstance.getInstance(RegistrationIntentService.this).
                getRestAdapter().
                sendFCMToken(object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Success>() {
                    @Override
                    public void onCompleted() {

                        Log.d(TAG, "onCompleted: ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage());

                    }

                    @Override
                    public void onNext(Success success) {
                        Log.d(TAG, "onNext:success.getResult "+success.getResult());
                    }
                });


        try{
            Intent localIntent = new Intent(TescoApp.UI_BROADCAST_RECEIVER_NAME);
            localIntent.putExtra("onTokenReceived",token);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void subscribeTopics(String token) throws IOException {
        try{
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            for (String topic : TOPICS) {
                pubSub.subscribe(token, "/topics/" + topic, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
