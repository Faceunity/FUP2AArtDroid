package com.faceunity.pta_art.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tujh on 2018/8/24.
 */
public abstract class DateUtil {

    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("MMdd_HHmmss");
        return df.format(new Date());
    }
}
