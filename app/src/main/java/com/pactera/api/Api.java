package com.pactera.api;

import com.google.gson.JsonObject;
import com.pactera.model.GraphModel;
import com.pactera.model.IntervalModel;
import com.pactera.model.Success;


import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface Api {

    @POST("api/apiuser")
    Observable<Success> sendFCMToken(@Body() JsonObject object);

    @GET("api/chatbot")
    Observable<GraphModel> getQueues(@Query("interval") int interval,@Query("Queue") String queueName,@Query("storetype") String storeType);

    @GET("api/home")
    Observable<GraphModel> getQueues1to15(@Query("interval") int interval,@Query("Queue") String queueName,@Query("storetype") String storeType);


    @GET("api/apianalytics")
    Observable<GraphModel> getAnalytics(@Query("interval") int interval,@Query("Queue") String queueName,@Query("storetype") String storeType);

    @GET("api/analyticsqueu")
    Observable<IntervalModel> getIntervalData(@Query("threshold") int threshold,@Query("Queue") String queueName,@Query("storetype") String storeType);

}
