package com.example.a13745.sweetward.database;

/**
 * Created by 13745 on 2018/4/8.
 */


import android.database.sqlite.SQLiteDatabase;

import com.example.a13745.sweetward.common.BaseApplication;

public class DbManager {
    private static DbManager DbManager;
    private DbHelper dbHelper;
    private SQLiteDatabase database;

    //构造器
    private DbManager() {
        //创建数据库
        dbHelper = DbHelper.getInstance(BaseApplication.getContext());
        database = dbHelper.getWritableDatabase();
    }

    //返回manager对象
    public static DbManager newInstances() {
        DbManager = new DbManager();
        return DbManager;
    }

    //返回database对象
    public SQLiteDatabase getDatabase() {
        return database;
    }
}