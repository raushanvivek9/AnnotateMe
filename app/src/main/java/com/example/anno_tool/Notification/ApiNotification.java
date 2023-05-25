package com.example.anno_tool.Notification;

import static com.example.anno_tool.Notification.ValuesClass.CONTENT_TYPE;
import static com.example.anno_tool.Notification.ValuesClass.SERVER_KEY;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiNotification {
    @Headers({"Authorization:"+SERVER_KEY,"Content-Type:"+CONTENT_TYPE})
    @POST("fcm/send")
    Call<NotificationPush> sendNotification(@Body NotificationPush notificationPush);
}
