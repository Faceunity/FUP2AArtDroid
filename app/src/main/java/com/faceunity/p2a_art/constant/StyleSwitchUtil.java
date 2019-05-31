package com.faceunity.p2a_art.constant;

import android.content.Context;
import android.os.AsyncTask;

import com.faceunity.p2a_art.core.client.P2AClientWrapper;

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
                P2AClientWrapper.setupStyleData(context);

                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }
}
