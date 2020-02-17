package com.pactera.api;

import android.content.Context;

import com.pactera.tesko.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstance {

    private static RetrofitInstance instance;
    private Context mContext;
    private  static String AUTH_URL = "http://52.172.157.210:8000/";


    public RetrofitInstance(Context mContext){
        this.mContext = mContext;
    }
    public static RetrofitInstance getInstance(Context mContext){

       if(instance == null){
           instance = new RetrofitInstance(mContext);
       }
        return instance;
    }

    public Api getRestAdapter(){

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(
                        chain -> {
                            Request request = chain.request().newBuilder()
                                    .addHeader("Content-Type", "Application/JSON")
                                    .build();
                            return chain.proceed(request);
                        }).build();


        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AUTH_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        return retrofit.create(Api.class);
    }
}