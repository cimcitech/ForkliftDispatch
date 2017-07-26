package com.cimcitech.forkliftdispatch;

import android.app.Application;
import android.content.Intent;

import com.cimcitech.forkliftdispatch.utils.Constants;

/**
 * Created by cimcitech on 2017/6/8.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        Constants.CONTEXT = getApplicationContext();
        // 启动服务
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        super.onCreate();
    }
}
