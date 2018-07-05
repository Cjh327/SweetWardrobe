package com.example.a13745.sweetward.socket.info;

/**
 * Created by 13745 on 2018/4/13.
 */



import com.example.a13745.sweetward.BuildConfig;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Clothes {
    private List<Integer> clothes;
    public static final Integer defaultClothes = -1;
    /*
            编号  类别  种类  颜色  脏衣指数
            外套      上衣      裤装      鞋子
        0:  夹克      卫衣      休闲短裤    休闲/运动鞋
        1:  大衣      毛衣      休闲长裤    靴子
        2:  风衣      针织衫     牛仔裤     雪地靴
        3:  棉衣      衬衫      工装裤     凉鞋
        4:  皮衣      马甲      西裤
        5:  羽绒服     T恤
        6:  西装      POLO
        7:  冲锋衣     背心
     */


    public Clothes(List<Integer> features) {
        assert features.size() == 5;
        clothes = features;
    }

    public Clothes(Integer id, Integer category, Integer type, Integer color, Integer dirtyDegree) {
        clothes = new ArrayList<>(5);
        clothes.add(id);
        clothes.add(category);
        clothes.add(type);
        clothes.add(color);
        clothes.add(dirtyDegree);
    }

    public List<Integer> getClothes() {
        return clothes;
    }

    public Integer getClothesId() {
        return clothes.get(0);
    }

    public Integer getClothesClass() {
        return clothes.get(1);
    }

    public Integer getClothesType() {
        return clothes.get(2);
    }

    public Integer getClothesColor() {
        return clothes.get(3);
    }

    public Integer getClothesDirtyDegree() {
        return clothes.get(4);
    }

    public String transformClothesToString() {
        return getClothesId().toString() + ",";
    }
}
