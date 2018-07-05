package com.example.a13745.sweetward.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 13745 on 2018/4/12.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}