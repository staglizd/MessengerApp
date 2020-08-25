package com.example.messengerapp.fragments;

import com.example.messengerapp.notifications.MyResponse;
import com.example.messengerapp.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA5xEos7w:APA91bF0gaHTjh5VHgOypljuPmLSzCKmAPnocaeWvA6DDcyNCKryBHV5RGnCQ6logUcRV0hLB8nOciNpSExv69IGx8zzUpO-jTqBmhlwQsDO33HsC3qLjThcyeXv4fSPGltod1E7--Zz"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
