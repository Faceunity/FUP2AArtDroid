package com.faceunity.pta_art;

import android.app.Application;
import android.util.DisplayMetrics;
import android.util.Log;

import com.faceunity.pta_art.constant.ColorConstant;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.pta_art.core.authpack;
import com.faceunity.pta_art.core.client.PTAClientWrapper;
import com.faceunity.pta_art.utils.sta.TtsEngineUtils;
import com.faceunity.pta_art.web.OkHttpUtils;
import com.faceunity.pta_helper.FUAuthCheck;
import com.faceunity.wrapper.faceunity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by tujh on 2018/3/30.
 */
public class FUApplication extends Application {

    public static final boolean needRestartMainActivity = false;

    private static final String TAG = FUApplication.class.getSimpleName();
    private static FUApplication fuApplication;
    private int height;

    @Override
    public void onCreate() {
        super.onCreate();
        closeAndroidPDialog();
        fuApplication = this;

        /**
         * 初始化dsp设备
         * 如果已经调用过一次了，后面再重新初始化bundle，也不需要重新再调用了。
         */
        String path = fuApplication.getApplicationInfo().nativeLibraryDir;
        faceunity.fuHexagonInitWithPath(path);

        OkHttpUtils.initOkHttpUtils(OkHttpUtils.initOkHttpClient());

        //TODO 初始化部分
        long startTime = System.currentTimeMillis();
        //初始化nama
        FUPTARenderer.initFURenderer(this);
        long endInitNamaTime = System.currentTimeMillis();

        //初始化P2A Helper ----工具类
        FUAuthCheck.fuP2ASetAuth(authpack.A());
        long endInitP2ATime = System.currentTimeMillis();

        //初始化 core data 数据---捏脸
        if (!needRestartMainActivity) {
            PTAClientWrapper.setupData(this);
            PTAClientWrapper.setupStyleData(this);
        }
        long endInitCoreDataTime = System.currentTimeMillis();

        Log.i(TAG, "InitAllTime: " + (endInitCoreDataTime - startTime)
                + "\nInitNamaTime: " + (endInitNamaTime - startTime)
                + "\nInitP2ATime: " + (endInitP2ATime - endInitNamaTime)
                + "\nInitCoreDataTime: " + (endInitCoreDataTime - endInitP2ATime));

        TtsEngineUtils.getInstance().init(this);
        initResolution();

        //风格选择后初始化 P2A client
        ColorConstant.init(this);
    }

    //
    public static FUApplication getInstance() {
        return fuApplication;
    }

    private void initResolution() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;
//        Log.d("onCreate", "width=" + dm.widthPixels + "--height=" + dm.heightPixels
//                + "--md=" + dm.density);
    }

    //
    public int getHeight() {
        return height;
    }

    /**
     * 解决部分机型 使用反射后 会弹出系统提示弹窗的问题
     */
    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
        }
    }

}
