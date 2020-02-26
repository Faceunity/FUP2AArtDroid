package com.faceunity.pta_art.utils.sta;

import android.content.Context;
import android.support.annotation.NonNull;

import com.faceunity.FUTtsEngine;
import com.faceunity.pta_art.core.authpack;

/**
 * FUStaEngine 工具类
 */
public final class TtsEngineUtils {
    private FUTtsEngine mFUTtsEngine;
    private Context mContext;

    private TtsEngineUtils() {
    }

    public static TtsEngineUtils getInstance() {
        return StaUnityHolder.INSTANCE;
    }

    /**
     * 初始化 FUStaEngine
     *
     * @param context
     */
    public void init(@NonNull Context context) {
        mContext = context.getApplicationContext();
        FUTtsEngine.Builder builder = new FUTtsEngine
                //传入上下文，必要
                .Builder(context)
                //验证证书，必要
                .setAuth(authpack.A());

        mFUTtsEngine = builder.build();
    }

    public Context getContext() {
        return mContext;
    }

    public FUTtsEngine getFUStaEngine() {
        return mFUTtsEngine;
    }

    private static class StaUnityHolder {
        private static final TtsEngineUtils INSTANCE = new TtsEngineUtils();
    }

}
