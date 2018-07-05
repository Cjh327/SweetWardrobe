package com.example.a13745.sweetward.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 13745 on 2018/4/12.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    @SerializedName("wind_sc")
    public String windsc;

    @SerializedName("hum")
    public String hum;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
