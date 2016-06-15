package com.chzan.imageselecter.util;

import android.widget.Toast;

/**
 * Created by chenzan on 2016/6/15.
 */
public class ToastUtil {
    private static Toast toast;

    /**
     * Toast 测试短toast
     */
    public static void showTestShortToast(final String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    /**
     * Toast 短toast
     */
    public static void showShortToast(final String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    /**
     * Toast 短toast
     */
    public static void showShortToast(int strId) {
        showToast(UIUtil.getString(strId), Toast.LENGTH_SHORT);
    }

    /**
     * 长 toast
     *
     * @param text
     */
    public static void showLongToast(String text) {
        showToast(text, Toast.LENGTH_LONG);
    }

    /**
     * 长 toast
     *
     * @param strId
     */
    public static void showLongToast(int strId) {
        showToast(UIUtil.getString(strId), Toast.LENGTH_LONG);
    }

    private static void showToast(final String text, final int type) {
        UIUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(UIUtil.getContext(), text, type);
                } else {
                    toast.setText(text + "");
                }
                toast.show();
            }
        });
    }

}
