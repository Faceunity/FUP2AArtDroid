package com.faceunity.p2a_art.core.base;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;

import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tujh on 2018/12/18.
 */
public abstract class BaseCore {
    private static final String TAG = BaseCore.class.getSimpleName();

    protected Context mContext;
    protected FUP2ARenderer mFUP2ARenderer;
    protected FUItemHandler mFUItemHandler;

    protected int mFrameId;
    protected int mInputImageOrientation = 270;
    protected int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;

    protected float[] landmarksData = new float[150];
    protected float[] expressionData = new float[56];
    protected float[] rotationData = new float[4];
    protected float[] pupilPosData = new float[2];
    protected float[] rotationModeData = new float[1];
    protected float[] faceRectData = new float[4];

    public BaseCore(Context context, FUP2ARenderer fuP2ARenderer) {
        this.mContext = context.getApplicationContext();
        this.mFUP2ARenderer = fuP2ARenderer;
        this.mFUItemHandler = fuP2ARenderer.getFUItemHandler();
    }

    public abstract int[] itemsArray();

    public abstract int onDrawFrame(byte[] img, int tex, int w, int h);

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

    public int isTracking() {
        return faceunity.fuIsTracking();
    }

    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
        return landmarksData;
    }

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
