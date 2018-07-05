package com.example.a13745.sweetward.database.info;

/**
 * Created by 13745 on 2018/4/8.
 */

public class ClothesInfo {
    //编号   类别      种类      颜色      脏衣指数
    private int id;
    private int clothType;
    private int clothKind;
    private int clothColor;
    private int clothDirtyIndex;
    private String clothFile;

    public ClothesInfo(){}
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setClothType(int clothType) {
        this.clothType = clothType;
    }

    public int getClothType() {
        return this.clothType;
    }

    public void setClothKind(int clothKind) {
        this.clothKind = clothKind;
    }

    public int getClothKind() {
        return this.clothKind;
    }

    public void setClothColor(int clothColor) {
        this.clothColor = clothColor;
    }

    public int getClothColor() {
        return clothColor;
    }

    public void setClothDirtyIndex(int clothDirtyIndex) {
        this.clothDirtyIndex = clothDirtyIndex;
    }

    public int getClothDirtyIndex() {
        return this.clothDirtyIndex;
    }

    public void setClothFile(String clothFile) {
        this.clothFile = clothFile;
    }

    public String getClothFile() {
        return this.clothFile;
    }
}

