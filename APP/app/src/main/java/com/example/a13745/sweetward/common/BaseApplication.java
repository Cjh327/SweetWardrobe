package com.example.a13745.sweetward.common;

/**
 * Created by 13745 on 2018/4/7.
 */
import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

public class BaseApplication extends Application {
    private static Context context;

    /**
     * 获取Context
     * @return 返回Context的对象
     */
    public static Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }
}
