package com.faceunity.p2a_art.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.faceunity.p2a_art.constant.FilePathFactory;
import com.faceunity.p2a_art.core.base.BaseCore;
import com.faceunity.p2a_art.entity.Scenes;
import com.faceunity.p2a_art.utils.BackgroundUtil;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

/**
 * Created by tujh on 2018/12/17.
 */
public class P2AMultipleCore extends BaseCore {
    private static final String TAG = P2AMultipleCore.class.getSimpleName();

    private final SparseArray<AvatarHandle> mAvatarHandles = new SparseArray<>();

    private BackgroundUtil mBackgroundUtil;
    public int fxaaItem, bgItem;

    public P2AMultipleCore(Context context, FUP2ARenderer fuP2ARenderer, String bg) {
        super(context, fuP2ARenderer);
        bgItem = mFUItemHandler.loadFUItem(TextUtils.isEmpty(bg) ? FilePathFactory.BUNDLE_default_bg : bg);
        fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);
    }

    public SparseArray<AvatarHandle> createAvatarMultiple(Scenes scenes) {
        for (int i = 0; i < scenes.bundles.length; i++) {
            final int finalI = i;
            AvatarHandle avatarHandle = new AvatarHandle(this, mFUItemHandler, new Runnable() {
                @Override
                public void run() {
                    AvatarHandle handle = mAvatarHandles.get(finalI);
                    if (handle != null)
                        handle.resetAllMinGroup();
                }
            });
            mAvatarHandles.put(i, avatarHandle);
        }
        return mAvatarHandles;
    }

    @Override
    public int[] itemsArray() {
        int[] itemsArray = new int[mAvatarHandles.size() + 2];
        for (int i = 0; i < mAvatarHandles.size(); i++) {
            itemsArray[i] = mAvatarHandles.get(mAvatarHandles.keyAt(i)).controllerItem;
        }
        itemsArray[itemsArray.length - 1] = fxaaItem;
        if (mBackgroundUtil != null && !mBackgroundUtil.isHasBackground())
            itemsArray[itemsArray.length - 2] = bgItem;
        return itemsArray;
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        if (mBackgroundUtil == null) {
            mBackgroundUtil = new BackgroundUtil(w, h);
        }
        Arrays.fill(landmarksData, 0.0f);
        Arrays.fill(rotationData, 0.0f);
        Arrays.fill(expressionData, 0.0f);
        Arrays.fill(pupilPosData, 0.0f);
        Arrays.fill(rotationModeData, 0.0f);
        rotationModeData[0] = (360 - mInputImageOrientation) / 90;

        int fuTex = faceunity.fuAvatarToTexture(pupilPosData, expressionData, rotationData, rotationModeData,
                faceunity.FU_ADM_FLAG_RGBA_BUFFER, w, h, mFrameId++, itemsArray(), 0);
        return mBackgroundUtil.drawBackground(fuTex);
    }

    @Override
    public void unBind() {
        for (int i = 0; i < mAvatarHandles.size(); i++) {
            AvatarHandle avatarHandle = mAvatarHandles.get(mAvatarHandles.keyAt(i));
            avatarHandle.unBindAll();
        }
    }

    @Override
    public void bind() {
        for (int i = 0; i < mAvatarHandles.size(); i++) {
            AvatarHandle avatarHandle = mAvatarHandles.get(mAvatarHandles.keyAt(i));
            avatarHandle.bindAll();
        }
    }

    @Override
    public void release() {
        for (int i = 0; i < mAvatarHandles.size(); i++) {
            AvatarHandle avatarHandle = mAvatarHandles.get(mAvatarHandles.keyAt(i));
            avatarHandle.release();
        }
        mAvatarHandles.clear();
        queueEvent(destroyItem(fxaaItem));
        queueEvent(destroyItem(bgItem));
    }

    public void loadBackgroundImage(final String path) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mBackgroundUtil.loadBackground(path);
            }
        });
    }
}
