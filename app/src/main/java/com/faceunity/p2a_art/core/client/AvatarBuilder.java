package com.faceunity.p2a_art.core.client;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.faceunity.p2a_art.constant.FilePathFactory;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.BundleRes;

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

    public AvatarP2A createAvatar(byte[] objData, String dir, int gender) {
        return createAvatar(objData, dir, gender, null);
    }

    public AvatarP2A createAvatar(final byte[] objData, final String dir, final int gender, final Runnable createComplete) {
        try {
            isCancel = false;
            final AvatarP2A avatarP2A = P2AClientWrapper.initializeAvatarP2A(dir, gender);
            if (isCancel) return null;
            isCreateIndex = 2;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<BundleRes> hairBundles = FilePathFactory.hairBundleRes(gender);
                    try {
                        P2AClientWrapper.initializeAvatarP2AData(objData, avatarP2A);
                        P2AClientWrapper.deformHairByServer(mContext, objData, hairBundles.get(avatarP2A.getHairIndex()).path, avatarP2A.getHairFile());
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
                                P2AClientWrapper.deformHairByServer(mContext, objData, hair.path, avatarP2A.getBundleDir() + hair.name);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (createComplete != null)
                        createComplete.run();
                }
            });
            P2AClientWrapper.createHead(objData, avatarP2A.getHeadFile());
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
