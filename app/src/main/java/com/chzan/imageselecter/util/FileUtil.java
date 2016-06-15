package com.chzan.imageselecter.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by chenzan on 2016/6/15.
 */
public class FileUtil {
    public static File createTempFile(Context context) throws Exception {
        File fileDir;
        if (ExternalStorageReady()) {
            String path = Environment.getExternalStorageDirectory().getPath();
            path = path + "/chenZPicture";
            fileDir = new File(path);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
        } else {
            fileDir = getCacheDirectory(context);
        }
        return File.createTempFile("IMG_", ".jpg", fileDir);
    }

    private static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        String cacheDir = context.getCacheDir().getPath();
        cacheDir += "/chenZPicture";
        appCacheDir = new File(cacheDir);
        if (!appCacheDir.exists()) {
            appCacheDir.mkdirs();
        }
        return appCacheDir;
    }

    public static boolean ExternalStorageReady() {
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
