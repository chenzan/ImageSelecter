package com.chzan.imageselecter.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

import com.chzan.imageselecter.BaseApplication;

/**
 * Created by chenzan on 2016/6/15.
 */
public class UIUtil {
    private static Handler handler;

    public static Context getContext() {
        return BaseApplication.getmApplication();
    }

    public static Resources getResource() {
        return getContext().getResources();
    }

    public static String getString(int rid) {
        return getContext().getString(rid);
    }

    /**
     * 主线程运行
     *
     * @param runnable 任务
     */
    public static void runOnUIThread(Runnable runnable) {
        if (BaseApplication.getMainThreadId() == android.os.Process.myTid()) {
            runnable.run();
        } else {
            if (handler == null)
                handler = new Handler(Looper.getMainLooper());
            handler.post(runnable);
        }
    }
}
