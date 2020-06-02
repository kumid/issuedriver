package com.ru.test.issuedriver.taxi.helpers.fsm;

import com.google.gson.JsonObject;
import com.ru.test.issuedriver.taxi.BuildConfig;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationApiService {

  @Headers({
      "Authorization: key="+ BuildConfig.FCM_SERVER_KEY ,
      "Content-Type: application/json"
  })
  @POST("fcm/send")
  Call<JsonObject> sendNotification(@Body JsonObject payload);
}
