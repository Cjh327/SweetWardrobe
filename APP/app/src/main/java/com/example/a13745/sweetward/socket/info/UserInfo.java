package com.example.a13745.sweetward.socket.info;

/**
 * Created by 13745 on 2018/4/13.
 */



public class UserInfo {
    private Integer id;
    private String name;
    private String password;
    private ClothesInfo clothesInfo;

    public UserInfo() {
        id = null;
        name = null;
        password = null;
        clothesInfo = null;
    }

    public UserInfo(Integer id, String name, String password, ClothesInfo clothesInfo) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.clothesInfo = clothesInfo;
    }

    public void setInfo(Integer id, String name, String password, ClothesInfo clothesInfo) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.clothesInfo = clothesInfo;
    }

    public void addClothes(Clothes clothes) {
        clothesInfo.addClothes(clothes);
    }

    public void addHistory(Suit suit, Weather weather) {
        clothesInfo.addHistory(suit, weather);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public ClothesInfo getClothesInfo() {
        return clothesInfo;
    }
}
