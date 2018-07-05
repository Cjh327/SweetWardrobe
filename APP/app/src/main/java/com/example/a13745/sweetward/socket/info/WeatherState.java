package com.example.a13745.sweetward.socket.info;

/**
 * Created by 13745 on 2018/4/13.
 */



import com.example.a13745.sweetward.BuildConfig;

public enum WeatherState {
    DEFAULT, SUNNY, CLOUDY, SHOWERY, RAINY, FOGGY, WINDY, SNOWY;

    public static WeatherState intToEnum(int val) {
        switch(val) {
            case 0: return DEFAULT;
            case 1: return SUNNY;
            case 2: return CLOUDY;
            case 3: return SHOWERY;
            case 4: return RAINY;
            case 5: return FOGGY;
            case 6: return WINDY;
            case 7: return SNOWY;
            default:
                System.out.println("Error tranferring int to WeatherState");
                return null;
        }
    }
}

