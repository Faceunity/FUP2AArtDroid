package com.faceunity.pta_art.constant;

import android.os.Build;
import android.os.Environment;

import com.faceunity.pta_art.utils.FileUtil;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by tujh on 2018/2/7.
 */

public abstract class Constant {

    public static final String APP_NAME = "FUP2AArtDemo";
    public static final boolean is_debug;
    public static final int is_q = 1;

    public static final int style_art = 1;
    public static final int style_new = 2;
    public static int style = style_new;

    public static String web_url_get_token = "";
    public static String web_url_create_upload_image = "";
    public static String web_url_create_download = "";
    public static String pta_client_version_new = "";
    public static String pta_client_version_art = "";

    public static String web_url_check = "";
    public static String netType = "";//请求的风格类型

    public static final String filePath = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "FaceUnity" + File.separator + APP_NAME + File.separator;

    public static final String versionPath = filePath + "versionPath.json";//版本号
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
                || Build.MANUFACTURER.contains("vivo")) {
            photoFilePath = cameraFilePath = Environment.getExternalStoragePublicDirectory("") + File.separator + "相机" + File.separator;
        } else {
            cameraFilePath = photoFilePath = DICMFilePath + File.separator + "Camera" + File.separator;
        }
        FileUtil.createFile(cameraFilePath);
        FileUtil.createFile(photoFilePath);

        boolean isDebug = false;
        try {
            isDebug = null != Class.forName("com.faceunity.pta_art.debug.DebugLayout");
        } catch (ClassNotFoundException e) {
        }
        is_debug = isDebug;
    }
}
