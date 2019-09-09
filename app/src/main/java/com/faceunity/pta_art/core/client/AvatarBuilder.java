package com.faceunity.pta_art.core.client;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.utils.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by tujh on 2019/2/22.
 */
public class AvatarBuilder {

    private Context mContext;
    private boolean isCancel;
    private volatile int isCreateIndex = 2;

    public AvatarBuilder(Context context) {
        mContext = context;
    }

    public void cancel() {
        isCancel = true;
    }

    public AvatarPTA createAvatar(byte[] objData, String dir, int gender) {
        return createAvatar(objData, dir, gender, null);
    }

    public AvatarPTA createAvatar(final byte[] objData, final String dir, final int gender, final Runnable createComplete) {
        try {
            isCancel = false;
            final AvatarPTA avatarP2A = PTAClientWrapper.initializeAvatarP2A(dir, gender);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("down_hair_end", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                FileUtil.writeToFile(avatarP2A.getBundleDir() + "hair_down.json", jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isCancel) return null;
            isCreateIndex = 2;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<BundleRes> hairBundles = FilePathFactory.hairBundleRes(gender);
                    try {
                        PTAClientWrapper.initializeAvatarP2AData(objData, avatarP2A);
                        PTAClientWrapper.deformHairByServer(mContext, objData, hairBundles.get(avatarP2A.getHairIndex()).path, avatarP2A.getHairFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        synchronized (avatarP2A) {
                            if (--isCreateIndex == 0)
                                avatarP2A.notify();
                            else
                                avatarP2A.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        for (int i = 0; i < hairBundles.size(); i++) {
                            if (isCancel) return;
                            BundleRes hair = hairBundles.get(i);
                            if (!TextUtils.isEmpty(hair.path) && i != avatarP2A.getHairIndex()) {
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
            PTAClientWrapper.createHead(objData, avatarP2A.getHeadFile());
            synchronized (avatarP2A) {
                if (--isCreateIndex == 0)
                    avatarP2A.notify();
                else
                    avatarP2A.wait();
            }
            if (isCancel) return null;
            return avatarP2A;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
