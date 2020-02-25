package com.faceunity.pta_art.core;

import android.content.Context;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

/**
 * Created by tujh on 2018/12/17.
 */
public class PTACore extends BaseCore {
    private static final String TAG = PTACore.class.getSimpleName();

    private AvatarHandle avatarHandle;

    public static final int ITEM_ARRAYS_EFFECT = 0;
    public static final int ITEM_ARRAYS_CONTROLLER = 1;
    public static final int ITEM_ARRAYS_FXAA = 2;
    public static final int ITEM_ARRAYS_COUNT = 3;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];


    public int fxaaItem, bgItem;
    private int controller_config;
    private int[] bgItems = new int[1];
    private boolean isNeedTrackFace = false;

    public PTACore(PTACore core) {
        super(core.mContext, core.mFUP2ARenderer);
        avatarHandle = core.avatarHandle;
        System.arraycopy(core.mItemsArray, 0, mItemsArray, 0, ITEM_ARRAYS_COUNT);
        fxaaItem = core.fxaaItem;
        bgItem = core.bgItem;
        face_capture = mFUP2ARenderer.createFaceCapture();
    }

    public PTACore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);

        bgItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_default_bg);
        bgItems[0] = bgItem;
        controller_config = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_controller_config_new);
        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);
        face_capture = mFUP2ARenderer.createFaceCapture();
    }

    public AvatarHandle createAvatarHandle() {
        return avatarHandle = new AvatarHandle(this, mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                faceunity.fuBindItems(avatarHandle.controllerItem, new int[]{controller_config});
                //背景道具逻辑修改
                //现在需要把背景绑定到controller上
                faceunity.fuBindItems(avatarHandle.controllerItem, bgItems);

                closeDDE();

                faceunity.fuItemSetParam(avatarHandle.controllerItem, "arMode", (360 - mInputImageOrientation) / 90);
                //将这个模型注册到controller的当前角色上，并分配人脸索引，索引从0开始
                faceunity.fuItemSetParamu64(avatarHandle.controllerItem, "register_face_capture_manager", face_capture);
                faceunity.fuItemSetParam(avatarHandle.controllerItem, "register_face_capture_face_id", 0.0);

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
     *
     * @param img 图片buffer
     * @param tex 图片纹理
     * @param w   图片宽
     * @param h   图片高
     * @return
     */
    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        int isTracking = 0;
        //是否开启人脸驱动
        if (isNeedTrackFace && img != null) {
            //如果开启CNN 面部追踪，每帧都需要调用fuFaceCaptureProcessFrame处理输入图像
            faceunity.fuFaceCaptureProcessFrame(face_capture, img, w, h, faceunity.FU_FORMAT_NV21_BUFFER, 0);
            //获取识别人脸数
            int face_num = faceunity.fuFaceCaptureGetResultFaceNum(face_capture);
            if (face_num > 0) {
                isTracking = faceunity.fuFaceCaptureGetResultIsFace(face_capture, 0);
                /**
                 * rotation 人脸三维旋转，返回值为旋转四元数，长度4
                 */
                faceunity.fuFaceCaptureGetResultRotation(face_capture, 0, avatarInfo.mRotation);
                /**
                 * expression  表情系数，长度57
                 */
                faceunity.fuFaceCaptureGetResultExpression(face_capture, 0, avatarInfo.mExpression);
                /**
                 * pupil pos 眼球方向，长度4 xyzw
                 */
                faceunity.fuFaceCaptureGetResultEyesRotation(face_capture, 0, avatarInfo.mPupilPos);
                /**
                 * rotation mode 人脸朝向，0-3分别对应手机四种朝向，长度1
                 * 新接口已去除
                 */
//                faceunity.fuFaceCaptureGetResult(face_capture, 0, avatarInfo.mRotationMode);
            }
        }
        if (isTracking <= 0) {
            Arrays.fill(avatarInfo.mRotation, 0.0f);
            Arrays.fill(avatarInfo.mExpression, 0.0f);
            Arrays.fill(avatarInfo.mPupilPos, 0.0f);
            Arrays.fill(avatarInfo.mRotationMode, 0.0f);
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "face_detector_status", 0);
        }

        avatarInfo.mRotationMode[0] = 0;
        avatarInfo.mIsValid = isTracking > 0;

        return faceunity.fuRenderBundles(avatarInfo,
                0, w, h, mFrameId++, itemsArray());
    }

    public void setCurrentInstancceId(int id) {
        if (avatarHandle != null)
            avatarHandle.setCurrentInstancceId(id);
    }

    /**
     * 关闭dde
     */
    public void closeDDE() {
        faceunity.fuItemSetParam(avatarHandle.controllerItem, "is_close_dde", 1.0);
        avatarHandle.setFaceCapture(false);
    }

    @Override
    public void unBind() {
        if (avatarHandle != null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuUnBindItems(avatarHandle.controllerItem, bgItems);
                }
            });
            avatarHandle.unBindAll();
        }
    }

    @Override
    public void bind() {
        if (avatarHandle != null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuBindItems(avatarHandle.controllerItem, bgItems);
                }
            });
            avatarHandle.bindAll();
        }
    }

    @Override
    public void release() {
        avatarHandle.release();
        queueEvent(destroyItem(fxaaItem));
        queueEvent(destroyItem(bgItem));
        queueEvent(destroyFaceCaptureItem(face_capture));
    }

    public void setNeedTrackFace(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
        avatarHandle.setCNNTrackFace(isNeedTrackFace);
    }
}
