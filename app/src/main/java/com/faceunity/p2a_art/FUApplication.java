package com.faceunity.p2a_art;

import android.app.Application;

import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.core.P2AClientWrapper;
import com.faceunity.p2a_art.web.OkHttpUtils;


/**
 * Created by tujh on 2018/3/30.
 */
public class FUApplication extends Application {
    private static final String TAG = FUApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpUtils.initOkHttpUtils(OkHttpUtils.initOkHttpClient(this));
        FUP2ARenderer.initFURenderer(this);
        ColorConstant.init(this);
        P2AClientWrapper.setupData(this);
    }

}
