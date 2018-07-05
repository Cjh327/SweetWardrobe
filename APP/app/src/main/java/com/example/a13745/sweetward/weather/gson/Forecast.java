package com.example.a13745.sweetward.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 13745 on 2018/4/12.
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {
        public String max;
        public String min;
    }


    public class More {
        @SerializedName("txt_d")
        public String info_d;

        @SerializedName("txt_n")
        public String info_n;
    }
}
