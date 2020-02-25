package com.faceunity.pta_art.utils;

import android.content.Context;
import android.widget.Toast;

import com.faceunity.pta_art.BuildConfig;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.client.PTAClientWrapper;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isNoFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static boolean generateAllHair = true;

    public static void generateAllHair(Context context, AvatarPTA avatarPTA) {
        if (generateAllHair && BuildConfig.DEBUG) {

            final List<BundleRes> bundleRes = FilePathFactory.hairBundleRes(avatarPTA.getGender());
            for (int i = 0; i < bundleRes.size(); i++) {
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                int finalI = i;
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        byte[] objData = FileUtil.readBytes(avatarPTA.getHeadFile());
                        if (objData == null)
                            return;
                        BundleRes hair = bundleRes.get(finalI);
                        try {
                            PTAClientWrapper.deformHairByServer(context, objData, hair.path, avatarPTA.getBundleDir() + hair.name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            Toast.makeText(context, "生成完毕", Toast.LENGTH_SHORT).show();
        }
    }
}
