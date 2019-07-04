package com.faceunity.p2a_art;

import android.app.Application;
import android.util.Log;

import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.core.client.P2AClientWrapper;
import com.faceunity.p2a_art.core.authpack;
import com.faceunity.p2a_art.web.OkHttpUtils;
import com.faceunity.p2a_helper.FUAuthCheck;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by tujh on 2018/3/30.
 */
public class FUApplication extends Application {
    private static final String TAG = FUApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        closeAndroidPDialog();
        OkHttpUtils.initOkHttpUtils(OkHttpUtils.initOkHttpClient(this));

        //TODO 初始化部分

        long startTime = System.currentTimeMillis();
        //初始化nama
        FUP2ARenderer.initFURenderer(this);
        long endInitNamaTime = System.currentTimeMillis();

        //初始化P2A Helper ----工具类
        FUAuthCheck.fuP2ASetAuth(authpack.A());
        long endInitP2ATime = System.currentTimeMillis();

        //初始化 core data 数据---捏脸
        P2AClientWrapper.setupData(this);
        long endInitCoreDataTime = System.currentTimeMillis();

        Log.i(TAG, "InitAllTime: " + (endInitCoreDataTime - startTime)
                + "\nInitNamaTime: " + (endInitNamaTime - startTime)
                + "\nInitP2ATime: " + (endInitP2ATime - endInitNamaTime)
                + "\nInitCoreDataTime: " + (endInitCoreDataTime - endInitP2ATime));
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
