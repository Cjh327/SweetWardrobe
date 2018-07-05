package com.example.a13745.sweetward.database;

/**
 * Created by 13745 on 2018/4/8.
 */



import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DbOperator {
    private DbManager dbManager;
    private SQLiteDatabase database;

    public DbOperator() {
        //创建数据库
        dbManager = DbManager.newInstances();
        database = dbManager.getDatabase();
    }

    //增加数据
    //tableName:数据库表名 cloth_info & history_info
    //object:插入的对象
    public void insert(String tableName, Object object) {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        ContentValues contentValues = new ContentValues();

        for(Field field : fields) {
            try {
                if(field.getName().equals("$change") || field.getName().equals("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);
                if(field.getType().getCanonicalName().equals("java.lang.String")) {
                    String content = (String) field.get(object);
                    contentValues.put(field.getName(), content);
                }
                else {
                    int content = (int) field.get(object);
                    contentValues.put(field.getName(), content);
                }
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        database.insert(tableName, null, contentValues);
    }

    //删除数据
    //删除的表名
    //删除的字段名
    //删除的字段值
    public void delete(String tableName, String fieldName, String value) {
        database.delete(tableName, fieldName + "=?", new String[]{value});
    }

    //更改数据库数据
    public void update(String tableName, String columnName, String columnValue, Object object) {
        try {
            Class clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            ContentValues contentValues = new ContentValues();
            for (Field field : fields) {
                if(field.getName().equals("$change") || field.getName().equals("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);
                if(field.getType().getCanonicalName().equals("java.lang.String")) {
                    String content = (String) field.get(object);
                    contentValues.put(field.getName(), content);
                }
                else {
                    int content = (int) field.get(object);
                    contentValues.put(field.getName(), content);
                }
                field.setAccessible(false);
            }
            database.update(tableName, contentValues, columnName + "=?", new String[]{columnValue});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //查询数据
    public <T> ArrayList<T> query(String tableName, Class<T> entityType, String fieldName, String value) {
        ArrayList<T> list = new ArrayList<>();
        Cursor cursor = database.query(tableName, null, fieldName + " like ?", new String[]{value}, null, null, " id desc");
        cursor.moveToFirst();
        //Log.d("QUERY", Integer.toString(cursor.getColumnCount()));
        while (!cursor.isAfterLast()) {
            try {
                T t = entityType.newInstance();
                for(int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    Class type = cursor.getClass();
                    //Log.d("QUERY", content + '|' + columnName + '|' + type.getCanonicalName());
                    if(columnName.equals("clothFile")) {
                        String content = cursor.getString(i);
                        Field field = entityType.getDeclaredField(columnName);
                        field.setAccessible(true);
                        field.set(t, content);
                        field.setAccessible(false);
                    }
                    else {
                        int content = cursor.getInt(i);
                        Field field = entityType.getDeclaredField(columnName);
                        field.setAccessible(true);
                        field.set(t, content);
                        field.setAccessible(false);
                    }
                }
                list.add(t);
                cursor.moveToNext();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}

