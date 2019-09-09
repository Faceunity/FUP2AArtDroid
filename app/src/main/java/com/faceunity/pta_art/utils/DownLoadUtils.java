package com.faceunity.pta_art.utils;

import android.content.Context;
import android.text.TextUtils;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.client.PTAClientWrapper;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class DownLoadUtils {

    public static void downAllHair(final Context mContext, ExecutorService executorService, final AvatarPTA avatarP2A, final Runnable createComplete) {
        File file = new File(avatarP2A.getBundleDir() + "hair_down.json");
        if (file.exists()) {
            try {
                String info = FileUtil.readTextFile(avatarP2A.getBundleDir() + "hair_down.json");
                JSONObject jsonObject = new JSONObject(info);
                int hair_down_state = jsonObject.getInt("down_hair_end");
                if (hair_down_state == 1) {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final int[] isCreateIndex = {2};
                final byte[] objData = readBytes(avatarP2A.getHeadFile());
                if (objData == null)
                    return;
                File hairFile = new File(avatarP2A.getHairFile());
                List<BundleRes> hairBundles = FilePathFactory.hairBundleRes(avatarP2A.getGender());
                if (!hairFile.exists()) {
                    try {
                        PTAClientWrapper.initializeAvatarP2AData(objData, avatarP2A);
                        PTAClientWrapper.deformHairByServer(mContext, objData, hairBundles.get(avatarP2A.getHairIndex()).path, avatarP2A.getHairFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        synchronized (avatarP2A) {
                            if (--isCreateIndex[0] == 0)
                                avatarP2A.notify();
                            else
                                avatarP2A.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    for (int i = 0; i < hairBundles.size(); i++) {
                        BundleRes hair = hairBundles.get(i);
                        File file = new File(avatarP2A.getBundleDir() + hair.name);
                        if (!TextUtils.isEmpty(hair.path) && i != avatarP2A.getHairIndex()
                                && !file.isFile()) {
                            PTAClientWrapper.deformHairByServer(mContext, objData, hair.path, avatarP2A.getBundleDir() + hair.name);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("down_hair_end", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    FileUtil.writeToFile(avatarP2A.getBundleDir() + "hair_down.json", jsonObject.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (createComplete != null)
                    createComplete.run();
            }
        });
    }

    private static byte[] readBytes(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
