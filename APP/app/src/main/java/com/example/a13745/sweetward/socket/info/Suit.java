package com.example.a13745.sweetward.socket.info;

/**
 * Created by 13745 on 2018/4/13.
 */



import java.util.List;

public class Suit {
    private List<Integer> clothesIdList;    //len = 4, 分别为外套、上衣、裤装、鞋子

    public Suit(List<Integer> clothesIdList) {
        this.clothesIdList = clothesIdList;
    }

    public List<Integer> getClothesIdList() {
        return clothesIdList;
    }

    public String transformSuitToString() {
        StringBuilder str = new StringBuilder();
        for(Integer clothesId: clothesIdList) {
            str.append(clothesId.toString());
            str.append(",");
        }
        str.append(";");
        return str.toString();
    }
}

