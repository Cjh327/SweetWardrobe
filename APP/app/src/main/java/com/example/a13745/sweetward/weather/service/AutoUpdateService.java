package com.example.a13745.sweetward.weather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by 13745 on 2018/4/12.
 */

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
