package com.chzan.imageselecter;

import android.app.Application;

/**
 * Created by chenzan on 2016/6/15.
 */
public class BaseApplication extends Application {
    private static Application mApplication;
    private static int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        mApplication = this;
        mainThreadId = android.os.Process.myTid();
    }

    public static Application getmApplication() {
        return mApplication;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
