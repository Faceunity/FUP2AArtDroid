package com.faceunity.pta_art.constant;

import android.content.Context;
import android.os.AsyncTask;

import com.faceunity.pta_art.core.client.PTAClientWrapper;

/**
 * Created by tujh on 2019/4/8.
 */
public abstract class StyleSwitchUtil {

    public static void switchStyle(final Context context, final Runnable runnable) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO 风格选择后初始化 P2A client
                ColorConstant.init(context);
                //初始化P2A Client
                PTAClientWrapper.setupStyleData(context);

                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }
}
