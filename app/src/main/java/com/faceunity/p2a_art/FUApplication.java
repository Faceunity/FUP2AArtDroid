package com.faceunity.p2a_art;

import android.app.Application;

import com.faceunity.FUAuthCheck;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.core.P2AClientWrapper;
import com.faceunity.p2a_art.web.OkHttpUtils;

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
        OkHttpUtils.initOkHttpUtils(OkHttpUtils.initOkHttpClient(this));
        ColorConstant.init(this);
        closeAndroidPDialog();

        //TODO 初始化部分
        //初始化nama
        FUP2ARenderer.initFURenderer(this);
        //初始化P2A Client
        P2AClientWrapper.setupData(this);
        //初始化P2A Helper
        FUAuthCheck.fuP2ASetAuth(authpack.A());
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
            e.printStackTrace();
        }
    }
}
