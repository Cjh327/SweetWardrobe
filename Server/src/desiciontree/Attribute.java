package desiciontree;

import database.Clothes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Attribute {
    static final List<String> attrList = new ArrayList<>();

    /**
     *  初始化静态变量，即属性列表
     */
    Attribute() {
        Collections.addAll(attrList, "天气", "最高温度", "最低温度",
                "最高湿度", "最低湿度", "最大风力", "最小风力",
                "外套", "上衣", "裤装", "鞋子");
    }

    /**
     *  判断attrName属性是不是连续属性值。
     *
     *  @param  attrName    需要判断的属性名称
     *
     *  @return 若是，返回True;否则，返回False
     */
    static private Boolean successValue(String attrName) {
        return (attrName.equals("最高温度") || attrName.equals("最低温度") ||
                attrName.equals("最高湿度") || attrName.equals("最低湿度") ||
                attrName.equals("最大风力") || attrName.equals("最小风力"));
    }

    /**
     *  将属性值依据一定规则进行处理，
     *  对连续的属性值进行划分。
     *
     *  @param  integer     需要处理的属性值
     *
     *  @param  attrName    对应的属性名称
     *
     *  @return 返回处理之后的属性值
     */
    static Integer transferKey(Integer integer, String attrName) {
        Integer key;
        if(!Attribute.successValue(attrName)) {
            key = integer;
        }
        else {
            Integer mod;
            switch(attrName) {
                case "最高温度": case "最低温度": mod = 5; break;
                case "最高湿度": case "最低湿度": mod = 20; break;
                case "最大风力": case "最小风力": mod = 3; break;
                default: mod = 0;
            }
            key = integer - integer % mod;
        }
        return key;
    }

    static Boolean isClothes(String attrName) {
        return attrName.equals("外套") || attrName.equals("上衣") ||
                attrName.equals("裤装") || attrName.equals("鞋子");
    }

    static Boolean isVoidClothes(String attrName, Integer index) {
        return isClothes(attrName) && index.equals(Clothes.defaultClothes);
    }
}
