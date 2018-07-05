package com.example.a13745.sweetward.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 13745 on 2018/4/12.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
