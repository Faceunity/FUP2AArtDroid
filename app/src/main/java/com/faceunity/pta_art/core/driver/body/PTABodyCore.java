package com.faceunity.pta_art.core.driver.body;

import android.content.Context;

import com.faceunity.pta_art.MainActivity;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

import java.lang.ref.WeakReference;

/**
 * 身体驱动场景
 * Created by tujh on 2018/12/17.
 */
public class PTABodyCore extends BaseCore {
    private static final String TAG = PTABodyCore.class.getSimpleName();

    private AvatarBodyHandle avatarBodyHandle;
    public static final int ITEM_ARRAYS_BG = 0;
    public static final int ITEM_ARRAYS_CONTROLLER = 1;
    public static final int ITEM_ARRAYS_EFFECT = 2;
    public static final int ITEM_ARRAYS_FXAA = 3;
    public static final int ITEM_ARRAYS_COUNT = 4;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    private boolean isNeedTrackFace = false;

    public int fxaaItem;
    private int fuTex;

    private WeakReference<MainActivity> weakReferenceActivity;

    public PTABodyCore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);
        weakReferenceActivity = new WeakReference<>((MainActivity) context);
        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);
        fuP2ARenderer.createHuman3d(context);
    }

    public AvatarBodyHandle createAvatarBodyHandle(int controller) {
        avatarBodyHandle = new AvatarBodyHandle(this, mFUItemHandler, controller);
        enterFaceDrive(true);
        return avatarBodyHandle;
    }

    @Override
    public int[] itemsArray() {
        if (avatarBodyHandle != null) {
            mItemsArray[ITEM_ARRAYS_CONTROLLER] = avatarBodyHandle.controllerItem;
        }
        return mItemsArray;
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        if (img == null) return 0;
        if (weakReferenceActivity.get() != null) {
            weakReferenceActivity.get().refreshVideo(getLandmarksData(), w, h);
        }
        if (avatarBodyHandle == null) {
            return fuTex;
        }
        return fuTex = faceunity.fuRenderBundlesWithCamera(img, tex, faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, w, h, mFrameId++, itemsArray());
    }

    public void enterFaceDrive(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
        avatarBodyHandle.setCNNTrackFace(needTrackFace);
    }


    @Override
    public void release() {
        enterFaceDrive(false);
        avatarBodyHandle.setModelmat(true);
        avatarBodyHandle.release();
        queueEvent(destroyItem(fxaaItem));
        queueEvent(destroyAIHumanProcessorModel());
    }


    @Override
    public void onCameraChange(int currentCameraType, int inputImageOrientation) {
        super.onCameraChange(currentCameraType, inputImageOrientation);
        avatarBodyHandle.onCameraChange(currentCameraType, inputImageOrientation);
    }

    @Override
    public void unBind() {
        if (avatarBodyHandle != null)
            avatarBodyHandle.unBindAll();
    }

    @Override
    public void bind() {
        if (avatarBodyHandle != null)
            avatarBodyHandle.bindAll();
    }
}
