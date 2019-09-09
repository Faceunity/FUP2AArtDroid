package com.faceunity.pta_art.core.base;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;

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
    protected float[] expressionData = new float[56];
    protected float[] rotationData = new float[4];
    protected float[] pupilPosData = new float[2];
    protected float[] faceRectData = new float[4];
    protected float[] rotationModeData = new float[1];

    public BaseCore(Context context, FUPTARenderer fuP2ARenderer) {
        this.mContext = context.getApplicationContext();
        this.mFUP2ARenderer = fuP2ARenderer;
        this.mFUItemHandler = fuP2ARenderer.getFUItemHandler();
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
    public abstract int onDrawFrame(byte[] img, int tex, int w, int h);

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
                faceunity.fuOnCameraChange();
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
        return faceunity.fuIsTracking();
    }

    /**
     * landmarks 2D人脸特征点，返回值为75个二维坐标，长度75*2
     */
    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
        return landmarksData;
    }

    /**
     *rotation 人脸三维旋转，返回值为旋转四元数，长度4
     */
    public float[] getRotationData() {
        Arrays.fill(rotationData, 0.0f);
        faceunity.fuGetFaceInfo(0, "rotation", rotationData);
        return rotationData;
    }

    public float[] getFaceRectData() {
        Arrays.fill(faceRectData, 0.0f);
        faceunity.fuGetFaceInfo(0, "face_rect", faceRectData);
        return faceRectData;
    }

    /**
     * expression  表情系数，长度46
     */
    public float[] getExpressionData() {
        Arrays.fill(expressionData, 0.0f);
        faceunity.fuGetFaceInfo(0, "expression", expressionData);
        return expressionData;
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
}
