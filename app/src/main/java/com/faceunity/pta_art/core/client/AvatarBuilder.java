package com.faceunity.pta_art.core.client;

import android.content.Context;
import android.os.AsyncTask;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;

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
                    if (createComplete != null)
                        createComplete.run();
                }
            });
            //PTAClientWrapper.createHead(objData, avatarP2A.getHeadFile());
            PTAClientWrapper.createNewHead(objData, avatarP2A.getHeadFile());
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
