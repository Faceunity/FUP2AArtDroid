package com.faceunity.pta_art.core.driver.body;

import android.content.Context;
import android.util.Log;

import com.faceunity.pta_art.MainActivity;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

import java.lang.ref.WeakReference;
import java.util.Arrays;

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
    public long human3d;
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
        human3d = fuP2ARenderer.createHuman3d();
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
        int isTracking = 0;
        int face_num = 0;
        //是否开启面部追踪
        if (isNeedTrackFace && img != null) {
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
        if (weakReferenceActivity.get() != null) {
            weakReferenceActivity.get().refreshVideo(getLandmarksData(), w, h);
            weakReferenceActivity.get().setBodyStatus(face_num);
        }
        avatarInfo.mRotationMode[0] = 0;
        avatarInfo.mIsValid = isTracking > 0;

        if (avatarBodyHandle == null || human3d == 0) {
            return fuTex;
        }

        /**
         * human3d:faceunity.fu3DBodyTrackerCreate（human3d.bundle）方法创建的句柄
         * img：输入的图像数据（如相机输入的数据）
         * imgType：输入图像数据的类型（如NV21类型）
         * rotationMode：图像方向（取avatarInfo.mRotationMode的第一个数据）
         */
        faceunity.fu3DBodyTrackerRun(human3d, 0, img, w, h, faceunity.FU_FORMAT_NV21_BUFFER, (int) avatarInfo.mRotationMode[0]);
        fuTex = faceunity.fuRenderBundles(avatarInfo,
                0, w, h, mFrameId++, itemsArray());
        return fuTex;
    }

    public void enterFaceDrive(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
        avatarBodyHandle.setCNNTrackFace(needTrackFace);
    }



    @Override
    public void release() {
        long tempHunman3d = human3d;
        human3d = 0;
        enterFaceDrive(false);
        avatarBodyHandle.setModelmat(true);
        avatarBodyHandle.release();
        queueEvent(destroyItem(fxaaItem));
        queueEvent(fu3DBodyTrackerDestroy(tempHunman3d));
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
