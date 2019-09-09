package com.faceunity.pta_art.core;

import android.content.Context;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

/**
 * AR场景
 * Created by tujh on 2018/12/17.
 */
public class PTAARCore extends BaseCore {
    private static final String TAG = PTAARCore.class.getSimpleName();

    private AvatarARHandle avatarARHandle;

    public static final int ITEM_ARRAYS_CONTROLLER = 0;
    public static final int ITEM_ARRAYS_EFFECT = 1;
    public static final int ITEM_ARRAYS_FXAA = 2;
    public static final int ITEM_ARRAYS_COUNT = 3;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];

    public int fxaaItem;

    public PTAARCore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);

        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);
    }

    public AvatarARHandle createAvatarARHandle() {
        return avatarARHandle = new AvatarARHandle(this, mFUItemHandler);
    }

    @Override
    public int[] itemsArray() {
        if (avatarARHandle != null) {
            mItemsArray[ITEM_ARRAYS_CONTROLLER] = avatarARHandle.controllerItem;
            mItemsArray[ITEM_ARRAYS_EFFECT] = avatarARHandle.filterItem.handle;
        }
        return mItemsArray;
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        if (img == null) return 0;
        return faceunity.fuDualInputToTexture(img, tex, faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, w, h, mFrameId++, itemsArray());
    }

    @Override
    public void release() {
        avatarARHandle.release();
        queueEvent(destroyItem(fxaaItem));
    }

    @Override
    public void onCameraChange(int currentCameraType, int inputImageOrientation) {
        super.onCameraChange(currentCameraType, inputImageOrientation);
        avatarARHandle.onCameraChange(currentCameraType, inputImageOrientation);
    }

    @Override
    public void unBind() {
        if (avatarARHandle != null)
            avatarARHandle.unBindAll();
    }

    @Override
    public void bind() {
        if (avatarARHandle != null)
            avatarARHandle.bindAll();
    }
}
