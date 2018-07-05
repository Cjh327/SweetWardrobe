package com.example.a13745.sweetward.database;

/**
 * Created by 13745 on 2018/4/8.
 */



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static DbHelper dbHelper;

    //衣物信息表
    private static final String CREATE_CLOTHINFO_SQL = "CREATE TABLE cloth_info "
            + "(id integer primary key autoincrement, "
            + "clothType integer, "
            + "clothKind integer, "
            + "clothColor integer, "
            + "clothDirtyIndex integer,"
            + "clothFile text);";

    //历史信息表
    private static final String CREATE_HISTORYINFO_SQL = "CREATE TABLE history_info "
            + "(weather integer, "
            + "maxTemp integer, "
            + "minTemp integer, "
            + "maxHumi integer, "
            + "minHumi integer, "
            + "maxWind integer, "
            + "minWind integer, "
            + "coat integer, "
            + "cloth integer, "
            + "trousers integer, "
            + "shoes integer);";

    //构造器
    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //简化构造器
    private DbHelper(Context context, String name) {
        this(context, name, null, 1);
    }

    //将自定义的数据库创建类单例
    public static  synchronized  DbHelper getInstance(Context context) {
        if(dbHelper==null){
            dbHelper = new DbHelper(context, "JYFdb");//数据库名称为create_db
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据库
        sqLiteDatabase.execSQL(CREATE_CLOTHINFO_SQL);
        sqLiteDatabase.execSQL(CREATE_HISTORYINFO_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //升级数据库

    }
}