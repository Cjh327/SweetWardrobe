package com.example.a13745.sweetward.socket.info;

/**
 * Created by 13745 on 2018/4/13.
 */



import java.util.ArrayList;
import java.util.List;

public class ClothesInfo {
    private List<Suit> suitHistory;   //len = 30, 30天内用户的穿衣历史
    private List<Weather> weatherHistory;         //len = 30, 30天内的天气情况
    private List<Clothes> clothesList;      //用户衣物列表

    public ClothesInfo() {
        suitHistory = new ArrayList<>();
        weatherHistory = new ArrayList<>();
        clothesList = new ArrayList<>();
    }

    public ClothesInfo(List<Suit> suitHistory, List<Weather> weatherHistory, List<Clothes> clothesList) {
        this.suitHistory = suitHistory;
        this.weatherHistory = weatherHistory;
        this.clothesList = clothesList;
    }

    void addClothes(Clothes clothes) {
        clothesList.add(clothes);
    }

    void addWeatherHistory(Weather weather) {
        weatherHistory.add(weather);
    }

    void addSuitHistory(Suit suit) {
        suitHistory.add(suit);
    }

    void addHistory(Suit suit, Weather weather) {
        suitHistory.add(suit);
        weatherHistory.add(weather);
    }

    public List<Suit> getSuitHistory() {
        return suitHistory;
    }

    public List<Weather> getWeatherHistory() {
        return weatherHistory;
    }

    public List<Clothes> getClothesList() {
        return clothesList;
    }
}

