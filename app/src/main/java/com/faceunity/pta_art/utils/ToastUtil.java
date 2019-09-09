package com.faceunity.pta_art.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by tujh on 2018/8/24.
 */
public abstract class ToastUtil {

    public static void showCenterToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
