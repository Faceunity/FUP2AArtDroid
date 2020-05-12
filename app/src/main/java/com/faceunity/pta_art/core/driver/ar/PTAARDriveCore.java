package com.faceunity.pta_art.core.driver.ar;

import android.content.Context;

import com.faceunity.pta_art.MainActivity;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

import java.lang.ref.WeakReference;
import java.util.Arrays;

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
        int isTracking = 0;
        int face_num = 0;
        //是否开启面部追踪
        if (img != null) {
            /**
             * 根据重力感应获取方向，然后设置给算法接口
             */
            int rotMode = 0;
            if (weakReferenceActivity.get() != null) {
                rotMode = weakReferenceActivity.get().getSensorOrientation();
            }
            //如果开启CNN 面部追踪，每帧都需要调用fuFaceCaptureProcessFrame处理输入图像
            faceunity.fuFaceCaptureProcessFrame(face_capture, img, w, h, faceunity.FU_FORMAT_NV21_BUFFER, rotMode);
            //获取识别人脸数
            face_num = faceunity.fuFaceCaptureGetResultFaceNum(face_capture);
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

        weakReferenceActivity.get().refresh(getLandmarksData());
        avatarInfo.mRotationMode[0] = 0;
        avatarInfo.mIsValid = isTracking > 0;

        if (weakReferenceActivity.get() != null && avatarARHandle != null) {
            avatarARHandle.setScreenOrientation(weakReferenceActivity.get().getSensorOrientation());
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
