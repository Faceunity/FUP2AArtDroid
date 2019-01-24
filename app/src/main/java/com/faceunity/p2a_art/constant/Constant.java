package com.faceunity.p2a_art.constant;

import android.os.Build;
import android.os.Environment;

import com.faceunity.p2a_art.utils.FileUtil;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by tujh on 2018/2/7.
 */

public abstract class Constant {
    public static final int NANO_IN_ONE_MILLI_SECOND = 1000000;

    public static final String APP_NAME = "FUP2AArtDemo";
    public static boolean is_debug = false;
    public static final int is_q = 1;

    public static String web_url_create = "https://api2.faceunity.com:2339/api/upload/image";
    public static String web_url_check = "";

    public static final String filePath = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "FaceUnity" + File.separator + APP_NAME + File.separator;

    public static final String DICMFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    public static final String photoFilePath;
    public static final String cameraFilePath;
    public static final String TestFilePath = filePath + "BundleTest" + File.separator;
    public static final String TmpPath = filePath + "tmp" + File.separator;

    static {
        if (Build.FINGERPRINT.contains("Flyme")
                || Pattern.compile("Flyme", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("Meizu")
                || Build.MANUFACTURER.contains("MeiZu")) {
            photoFilePath = DICMFilePath + File.separator + "Camera" + File.separator;
            cameraFilePath = DICMFilePath + File.separator + "Video" + File.separator;
        } else if (Build.FINGERPRINT.contains("vivo")
                || Pattern.compile("vivo", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("vivo")
                || Build.MANUFACTURER.contains("vivo")) {
            photoFilePath = cameraFilePath = Environment.getExternalStoragePublicDirectory("") + File.separator + "相机" + File.separator;
        } else {
            cameraFilePath = photoFilePath = DICMFilePath + File.separator + "Camera" + File.separator;
        }
        FileUtil.createFile(cameraFilePath);
        FileUtil.createFile(photoFilePath);

        try {
            is_debug = null != Class.forName("com.faceunity.p2a_art.debug.DebugLayout");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
