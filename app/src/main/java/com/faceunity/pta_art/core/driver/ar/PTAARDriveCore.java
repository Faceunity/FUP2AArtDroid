package com.faceunity.pta_art.core.driver.ar;

import android.content.Context;

import com.faceunity.pta_art.MainActivity;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

import java.lang.ref.WeakReference;

/**
 * AR场景
 * Created by tujh on 2018/12/17.
 */
public class PTAARDriveCore extends BaseCore {
    private static final String TAG = PTAARDriveCore.class.getSimpleName();

    private AvatarARDriveHandle avatarARHandle;
    public static final int ITEM_ARRAYS_BG = 0;
    public static final int ITEM_ARRAYS_CONTROLLER = 1;
    public static final int ITEM_ARRAYS_EFFECT = 2;
    public static final int ITEM_ARRAYS_FXAA = 3;
    public static final int ITEM_ARRAYS_COUNT = 4;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    public int fxaaItem;

    private WeakReference<MainActivity> weakReferenceActivity;

    public PTAARDriveCore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);
        weakReferenceActivity = new WeakReference<>((MainActivity) context);

        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);

    }

    public AvatarARDriveHandle createAvatarARHandle(int controller) {
        return avatarARHandle = new AvatarARDriveHandle(this, mFUItemHandler, controller);
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
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        if (img == null) return 0;
        int flags = faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE;

        int rotationMode = 0;
        MainActivity mainActivity = weakReferenceActivity.get();
        if (mainActivity != null) {
            rotationMode = mainActivity.getSensorOrientation();
            mainActivity.refresh(getLandmarksData());
            if (avatarARHandle != null) {
                avatarARHandle.setScreenOrientation(rotationMode);
            }
            faceunity.fuSetDefaultRotationMode(rotationMode);
        }


        return faceunity.fuRenderBundlesWithCamera(img, tex, flags, w, h, mFrameId++, itemsArray());
    }

    @Override
    public void release() {
        avatarARHandle.setModelmat(true);
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
