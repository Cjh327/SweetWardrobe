package com.example.a13745.sweetward.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 13745 on 2018/4/12.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    @SerializedName("drsg")
    public DressSuggestion dressSuggestion;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }

    public class DressSuggestion {
        @SerializedName("txt")
        public String info;
    }
}
