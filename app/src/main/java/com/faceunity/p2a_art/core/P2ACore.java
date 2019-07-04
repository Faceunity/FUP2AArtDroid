package com.faceunity.p2a_art.core;

import android.content.Context;

import com.faceunity.p2a_art.constant.FilePathFactory;
import com.faceunity.p2a_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

/**
 * Created by tujh on 2018/12/17.
 */
public class P2ACore extends BaseCore {
    private static final String TAG = P2ACore.class.getSimpleName();

    private AvatarHandle avatarHandle;

    public static final int ITEM_ARRAYS_CONTROLLER = 0;
    public static final int ITEM_ARRAYS_EFFECT = 1;
    public static final int ITEM_ARRAYS_FXAA = 2;
    public static final int ITEM_ARRAYS_COUNT = 3;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];

    public int fxaaItem, bgItem;

    private boolean isNeedTrackFace = false;

    public P2ACore(P2ACore core) {
        super(core.mContext, core.mFUP2ARenderer);
        avatarHandle = core.avatarHandle;
        System.arraycopy(core.mItemsArray, 0, mItemsArray, 0, ITEM_ARRAYS_COUNT);
        fxaaItem = core.fxaaItem;
        bgItem = core.bgItem;
    }

    public P2ACore(Context context, FUP2ARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);

        mItemsArray[ITEM_ARRAYS_EFFECT] = bgItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_default_bg);
        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);
    }

    public AvatarHandle createAvatarHandle() {
        return avatarHandle = new AvatarHandle(this, mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(avatarHandle.controllerItem, "arMode", (360 - mInputImageOrientation) / 90);
                avatarHandle.resetAll();
            }
        });
    }

    @Override
    public int[] itemsArray() {
        if (avatarHandle != null) {
            mItemsArray[ITEM_ARRAYS_CONTROLLER] = avatarHandle.controllerItem;
        }
        return mItemsArray;
    }

    /**
     * fuAvatarToTexture 用于人脸驱动
     * @param img 图片buffer
     * @param tex 图片纹理
     * @param w   图片宽
     * @param h   图片高
     * @return
     */
    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        int isTracking = 0;
        //是否开启人脸驱动
        if (isNeedTrackFace && img != null) {
            faceunity.fuTrackFaceWithTongue(img, 0, w, h);
            isTracking = faceunity.fuIsTracking();
            if (isTracking > 0) {
                /**
                 *rotation 人脸三维旋转，返回值为旋转四元数，长度4
                 */
                faceunity.fuGetFaceInfo(0, "rotation", rotationData);
                /**
                 * expression  表情系数，长度56
                 */
                faceunity.fuGetFaceInfo(0, "expression", expressionData);
                /**
                 * pupil pos 眼球方向，长度2
                 */
                faceunity.fuGetFaceInfo(0, "pupil_pos", pupilPosData);
                /**
                 * rotation mode 人脸朝向，0-3分别对应手机四种朝向，长度1
                 */
                faceunity.fuGetFaceInfo(0, "rotation_mode", rotationModeData);
            }
        }
        if (isTracking <= 0) {
            Arrays.fill(rotationData, 0.0f);
            Arrays.fill(expressionData, 0.0f);
            Arrays.fill(pupilPosData, 0.0f);
            Arrays.fill(rotationModeData, 0.0f);
        }
        rotationModeData[0] = (360 - mInputImageOrientation) / 90;

        return faceunity.fuAvatarToTexture(pupilPosData, expressionData, rotationData, rotationModeData,
                0, w, h, mFrameId++, itemsArray(), isTracking);
    }

    @Override
    public void unBind() {
        if (avatarHandle != null)
            avatarHandle.unBindAll();
    }

    @Override
    public void bind() {
        if (avatarHandle != null)
            avatarHandle.bindAll();
    }

    @Override
    public void release() {
        avatarHandle.release();
        queueEvent(destroyItem(fxaaItem));
        queueEvent(destroyItem(bgItem));
    }

    public void setNeedTrackFace(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
        avatarHandle.setNeedTrackFace(isNeedTrackFace);
    }

    @Override
    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        if (isNeedTrackFace && isTracking() > 0)
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
        return landmarksData;
    }
}
