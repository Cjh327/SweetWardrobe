package com.example.a13745.sweetward.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 13745 on 2018/4/12.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
