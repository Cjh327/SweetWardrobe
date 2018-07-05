package com.example.a13745.sweetward.socket.info;

/**
 * Created by 13745 on 2018/4/13.
 */


import java.util.ArrayList;
import java.util.List;

public class Weather {
    private WeatherState state;
    private List<Integer> temperature;  //len = 2, 分别存储用户发送信息当天的最高、最低温度
    private List<Integer> humidity;     //len = 2, 分别存储用户发送信息当天的最高、最低湿度
    private List<Integer> windForce;    //len = 2, 分别存储用户发送信息当天的最高、最低风力等级

    public Weather(List<Integer> list) {
        this.state = WeatherState.values()[list.get(0)];
        this.temperature = new ArrayList<>(2);
        temperature.add(list.get(1));
        temperature.add(list.get(2));
        this.humidity = new ArrayList<>(2);
        humidity.add(list.get(3));
        humidity.add(list.get(4));
        this.windForce = new ArrayList<>(2);
        windForce.add(list.get(5));
        windForce.add(list.get(6));
    }

    public Weather(WeatherState state, List<Integer> temperature, List<Integer> humidity, List<Integer> windForce) {
        this.state = state;
        assert temperature.size() == 2;
        assert humidity.size() == 2;
        assert windForce.size() == 2;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windForce = windForce;
    }

    public WeatherState getState() {
        return state;
    }

    public Integer getUpperTemperature() {
        return temperature.get(0);
    }

    public Integer getLowerTemperature() {
        return temperature.get(1);
    }

    public Integer getUpperHumidity() {
        return humidity.get(0);
    }

    public Integer getLowerHumidity() {
        return humidity.get(1);
    }

    public Integer getUpperWindForce() {
        return windForce.get(0);
    }

    public Integer getLowerWindForce() {
        return windForce.get(1);
    }

    public List<Integer> formatWeather() {
        List<Integer> digitalWeather = new ArrayList<>();
        Integer digitalState;
        switch (state) {
            case DEFAULT: digitalState = -1; break;
            case SUNNY: digitalState = 0; break;
            case CLOUDY: digitalState = 1; break;
            case SHOWERY: digitalState = 2; break;
            case RAINY: digitalState = 3; break;
            case FOGGY: digitalState = 4; break;
            case WINDY: digitalState = 5; break;
            case SNOWY: digitalState = 6; break;
            default: digitalState = -1; break;
        }
        digitalWeather.add(digitalState);
        digitalWeather.addAll(temperature);
        digitalWeather.addAll(humidity);
        digitalWeather.addAll(windForce);
        return digitalWeather;
    }
}
