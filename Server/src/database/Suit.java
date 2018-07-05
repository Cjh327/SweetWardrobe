package database;

import java.util.ArrayList;
import java.util.List;

public class Suit {

    private List<Integer> clothesIdList;

    public Suit(List<Integer> u_clotheIdList) {
        clothesIdList = u_clotheIdList;
    }


    public List<Integer> getClothesIdList() {
        return clothesIdList;
    }

    public String transformSuitToString() {
        String str = new String();
        for (Integer clothesId : clothesIdList) {
            str += clothesId.toString() + ",";
        }
        str += ";";
        return str;
    }

}
