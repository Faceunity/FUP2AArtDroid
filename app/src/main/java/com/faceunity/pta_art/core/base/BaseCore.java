package com.faceunity.pta_art.core.base;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;
import java.util.List;

/**
 * 场景
 * Created by tujh on 2018/12/18.
 */
public abstract class BaseCore {
    private static final String TAG = BaseCore.class.getSimpleName();

    protected Context mContext;
    protected FUPTARenderer mFUP2ARenderer;
    protected FUItemHandler mFUItemHandler;

    protected int mFrameId;
    protected int mInputImageOrientation = 270;
    protected int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;

    protected float[] landmarksData = new float[150];
    protected faceunity.AvatarInfo avatarInfo = new faceunity.AvatarInfo();
    protected float[] faceRectData = new float[4];
    public long face_capture;//创建面部追踪模型

    protected int wholeBodyCameraItem;
    protected int smallWholeBodyCameraItem;
    protected int halfLengthBodyCameraItem;
    protected int bigHalfLengthBodyCameraItem;
    protected int currentCameraItem;

    public BaseCore(Context context, FUPTARenderer fuP2ARenderer) {
        this.mContext = context.getApplicationContext();
        this.mFUP2ARenderer = fuP2ARenderer;
        this.mFUItemHandler = fuP2ARenderer.getFUItemHandler();

        avatarInfo.mExpression = new float[57];
        avatarInfo.mRotation = new float[4];
        avatarInfo.mPupilPos = new float[4];
        avatarInfo.mRotationMode = new float[1];
    }

    public void setFace_capture(long face_capture) {
        this.face_capture = face_capture;
    }


    /**
     * 获取所有道具句柄
     *
     * @return
     */
    public abstract int[] itemsArray();

    /**
     * 图片渲染
     *
     * @param img 图片buffer
     * @param tex 图片纹理
     * @param w   图片宽
     * @param h   图片高
     * @return
     */
    public abstract int onDrawFrame(byte[] img, int tex, int w, int h, int rotarion);

    /**
     * 切换相机
     *
     * @param currentCameraType     相机方向
     * @param inputImageOrientation 相机获取的图片旋转角度
     */
    public void onCameraChange(final int currentCameraType, final int inputImageOrientation) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCurrentCameraType = currentCameraType;
                mInputImageOrientation = inputImageOrientation;
                if (face_capture != 0) {
                    faceunity.fuFaceCaptureReset(face_capture);
                }
            }
        });
    }

    public abstract void unBind();

    public abstract void bind();

    public abstract void release();

    /**
     * 类似GLSurfaceView的queueEvent机制
     */
    public void queueEvent(@NonNull Runnable r) {
        mFUP2ARenderer.queueEvent(r);
    }

    /**
     * 类似GLSurfaceView的queueEvent机制
     */
    public void queueEvent(@NonNull List<Runnable> rs) {
        mFUP2ARenderer.queueEvent(rs);
    }

    /**
     * 相较于queueEvent，延后一次render被调用
     */
    public void queueNextEvent(@NonNull Runnable r) {
        mFUP2ARenderer.queueNextEvent(r);
    }

    //******************nama SDK中的人脸信息相关参数*****************************//
    public int isTracking() {
        int isTracking = 0;
        if (face_capture > 0) {
            int face_num = faceunity.fuFaceCaptureGetResultFaceNum(face_capture);
            if (face_num > 0) {
                isTracking = faceunity.fuFaceCaptureGetResultIsFace(face_capture, 0);
            }
        }
        return isTracking;

    }

    /**
     * landmarks 2D人脸特征点，返回值为75个二维坐标，长度75*2
     */
    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        if (face_capture > 0) {
            int face_num = faceunity.fuFaceCaptureGetResultFaceNum(face_capture);
            if (face_num > 0) {
                faceunity.fuFaceCaptureGetResultLandmarks(face_capture, 0, landmarksData);
            }
        }
        return landmarksData;
    }

    /**
     * rotation 人脸三维旋转，返回值为旋转四元数，长度4
     */
    public float[] getRotationData() {
        Arrays.fill(avatarInfo.mRotation, 0.0f);
        if (face_capture > 0) {
            int face_num = faceunity.fuFaceCaptureGetResultFaceNum(face_capture);
            if (face_num > 0) {
                faceunity.fuFaceCaptureGetResultRotation(face_capture, 0, avatarInfo.mRotation);
            }
        }
        return avatarInfo.mRotation;
    }

    public float[] getFaceRectData() {
        Arrays.fill(faceRectData, 0.0f);
        if (face_capture > 0) {
            int face_num = faceunity.fuFaceCaptureGetResultFaceNum(face_capture);
            if (face_num > 0) {
                faceunity.fuFaceCaptureGetResultFaceBbox(face_capture, 0, faceRectData);
            }
        }
        return faceRectData;
    }

    /**
     * expression  表情系数，长度57
     */
    public float[] getExpressionData() {
        Arrays.fill(avatarInfo.mExpression, 0.0f);
        if (face_capture > 0) {
            int face_num = faceunity.fuFaceCaptureGetResultFaceNum(face_capture);
            if (face_num > 0) {
                faceunity.fuFaceCaptureGetResultExpression(face_capture, 0, avatarInfo.mExpression);
            }
        }
        return avatarInfo.mExpression;
    }

    /**
     * 绑定与解绑道具：
     * -  该接口可以将avatar道具绑定到controller句柄上，并解绑老的avatar道具
     *
     * @param oldItem 需要解绑的道具句柄
     * @param newItem 需要绑定的道具句柄
     */
    public Runnable avatarBindItem(final int controllerItem, final int oldItem, final int newItem) {
        return new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "bundle avatarBindItem controlItem " + controllerItem + " oldItem " + oldItem + " newItem " + newItem);
                if (oldItem > 0)
                    faceunity.fuUnBindItems(controllerItem, new int[]{oldItem});
                if (newItem > 0)
                    faceunity.fuBindItems(controllerItem, new int[]{newItem});
            }
        };
    }

    /**
     * 销毁单个道具：
     * - 通过道具句柄销毁道具，并释放相关资源
     *
     * @param oldItem 需要销毁的道具句柄
     */
    public Runnable destroyItem(final int oldItem) {
        return new Runnable() {
            @Override
            public void run() {
                if (oldItem > 0) {
                    faceunity.fuDestroyItem(oldItem);
                    Log.i(TAG, "bundle destroyItem oldItem " + oldItem);
                }
            }
        };
    }

    /**
     * 销毁面部追踪模型
     *
     * @param oldItem
     * @return
     */
    public Runnable destroyFaceCaptureItem(final long oldItem) {
        return new Runnable() {
            @Override
            public void run() {
                if (oldItem > 0) {
                    faceunity.fuFaceCaptureDestory(oldItem);
                    Log.i(TAG, "bundle destroyItem oldItem " + oldItem);
                }
            }
        };
    }


    public Runnable fu3DBodyTrackerDestroy(final long oldItem) {
        return new Runnable() {
            @Override
            public void run() {
                if (oldItem > 0) {
                    faceunity.fu3DBodyTrackerDestroy(oldItem);
                    Log.i(TAG, "bundle fu3DBodyTrackerDestroy oldItem " + oldItem);
                }
            }
        };
    }


    /**
     * 相机bundle - 全身
     */
    public void loadWholeBodyCamera() {
        if (currentCameraItem != 0 && currentCameraItem == wholeBodyCameraItem) {
            return;
        }
        wholeBodyCameraItem = createAndLoadCameraItem(wholeBodyCameraItem, FilePathFactory.CAMERA_WHOLE_BODY);
    }

    /**
     * 相机bundle - 全身-更小
     */
    public void loadSmallWholeBodyCamera() {
        if (currentCameraItem != 0 && currentCameraItem == smallWholeBodyCameraItem) {
            return;
        }
        smallWholeBodyCameraItem = createAndLoadCameraItem(smallWholeBodyCameraItem, FilePathFactory.CAMERA_SMALL_WHOLE_BODY);

    }

    /**
     * 相机bundle - 半身
     */
    public void loadHalfLengthBodyCamera() {
        if (currentCameraItem != 0 && currentCameraItem == halfLengthBodyCameraItem) {
            return;
        }

        halfLengthBodyCameraItem = createAndLoadCameraItem(halfLengthBodyCameraItem, FilePathFactory.CAMERA_HALF_LENGTH_BODY);
    }

    /**
     * 相机bundle - 半身-更大
     */
    public void loadBigHalfLengthBodyCamera() {
        if (currentCameraItem != 0 && currentCameraItem == bigHalfLengthBodyCameraItem) {
            return;
        }
        bigHalfLengthBodyCameraItem = createAndLoadCameraItem(bigHalfLengthBodyCameraItem, FilePathFactory.CAMERA_BIG_HALF_LENGTH_BODY);
    }

    protected int createAndLoadCameraItem(int itemId, String bundlePath) {
        return 0;
    }
}
