package com.faceunity.pta_art.core;

import android.content.Context;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.pta_art.core.base.FUItemHandler;
import com.faceunity.wrapper.faceunity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一个基于Faceunity Nama SDK的简单封装，方便简单集成，理论上简单需求的步骤：
 * <p>
 * 1.通过OnEffectSelectedListener在UI上进行交互
 * 2.合理调用FURenderer构造函数
 * 3.对应的时机调用onSurfaceCreated和onSurfaceDestroyed
 * 4.处理图像时调用onDrawFrame
 * <p>
 * 如果您有更高级的定制需求，Nama API文档请参考http://www.faceunity.com/technical/android-api.html
 */
public class FUPTARenderer {
    private static final String TAG = FUPTARenderer.class.getSimpleName();

    /**
     * 获取faceunity sdk 版本库
     */
    public static String getVersion() {
        return faceunity.fuGetVersion();
    }

    /**
     * 全局加载相应的底层数据包
     */
    public static void initFURenderer(Context ct) {
        Context context = ct.getApplicationContext();
        try {
            //获取faceunity SDK版本信息
            Log.i(TAG, "fu sdk version " + faceunity.fuGetVersion());

            /**
             * fuSetup faceunity初始化
             * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
             *      authpack：用于鉴权证书内存数组。若没有,请咨询support@faceunity.com
             * 首先调用完成后再调用其他FU API
             */
            InputStream v3 = context.getAssets().open(FilePathFactory.BUNDLE_v3);
            byte[] v3Data = new byte[v3.available()];
            v3.read(v3Data);
            v3.close();
            faceunity.fuSetup(v3Data, authpack.A());

            /**
             * fuLoadTongueModel 识别舌头动作数据包加载
             * 其中 tongue.bundle：头动作驱动数据包；
             */
            InputStream tongue = context.getAssets().open(FilePathFactory.BUNDLE_tongue);
            byte[] tongueDate = new byte[tongue.available()];
            tongue.read(tongueDate);
            tongue.close();
            faceunity.fuLoadTongueModel(tongueDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Context mContext;

    //用于和异步加载道具的线程交互
    private HandlerThread mFUItemHandlerThread;
    private FUItemHandler mFUItemHandler;

    private BaseCore mFUCore;

    private List<Runnable> mEventQueue;
    private List<Runnable> mNextEventQueue;

    /**
     * FURenderer构造函数
     */
    public FUPTARenderer(Context context) {
        mContext = context.getApplicationContext();
        mEventQueue = Collections.synchronizedList(new ArrayList<Runnable>());
        mNextEventQueue = Collections.synchronizedList(new ArrayList<Runnable>());

        mFUItemHandlerThread = new HandlerThread("FUItemHandlerThread");
        mFUItemHandlerThread.start();
        mFUItemHandler = new FUItemHandler(mFUItemHandlerThread.getLooper(), mContext);
    }

    public FUItemHandler getFUItemHandler() {
        return mFUItemHandler;
    }

    public void setFUCore(@NonNull BaseCore core) {
        this.mFUCore = core;
    }

    /**
     * 创建及初始化faceunity相应的资源
     */
    public void onSurfaceCreated() {
        faceunity.fuSetExpressionCalibration(2);
        faceunity.fuSetMaxFaces(1);//设置多脸，目前最多支持8人。
        faceunity.fuSetAsyncTrackFace(0);
    }

    /**
     * 双输入接口(fuDualInputToTexture)(处理后的画面数据并不会回写到数组)，由于省去相应的数据拷贝性能相对最优，推荐使用。
     *
     * @param img NV21数据
     * @param tex 纹理ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        if (tex <= 0 || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame date error");
            return 0;
        }
        prepareDrawFrame();
        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = mFUCore.onDrawFrame(img, tex, w, h);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 销毁faceunity相关的资源
     */
    public void onSurfaceDestroyed() {
        mFUCore.release();
        faceunity.fuDestroyAllItems();
        faceunity.fuOnDeviceLost();
    }

    /**
     * camera切换时需要调用
     *
     * @param currentCameraType     前后置摄像头ID
     * @param inputImageOrientation
     */
    public void onCameraChange(int currentCameraType, int inputImageOrientation) {
        mFUCore.onCameraChange(currentCameraType, inputImageOrientation);
    }

    public void release() {
        if (mFUItemHandlerThread != null) {
            mFUItemHandlerThread.quitSafely();
            mFUItemHandlerThread = null;
            mFUItemHandler = null;
        }

        if (mEventQueue != null) {
            mEventQueue.clear();
            mEventQueue = null;
        }
        if (mNextEventQueue != null) {
            mNextEventQueue.clear();
            mNextEventQueue = null;
        }
    }

    /**
     * 每帧处理画面时被调用
     */
    private void prepareDrawFrame() {
        //计算FPS等数据
        benchmarkFPS();

        //获取人脸是否识别，并调用回调接口
        int isTracking = faceunity.fuIsTracking();
        if (mOnTrackingStatusChangedListener != null && mTrackingStatus != isTracking) {
            mOnTrackingStatusChangedListener.onTrackingStatusChanged(mTrackingStatus = isTracking);
        }

        //获取faceunity错误信息，并调用回调接口
        int error = faceunity.fuGetSystemError();
        if (error != 0)
            Log.e(TAG, "fuGetSystemErrorString " + faceunity.fuGetSystemErrorString(error));
        if (mOnSystemErrorListener != null && error != 0) {
            mOnSystemErrorListener.onSystemError(error == 0 ? "" : faceunity.fuGetSystemErrorString(error));
        }

        //获取是否正在表情校准，并调用回调接口
        final float[] isCalibratingTmp = new float[1];
        faceunity.fuGetFaceInfo(0, "is_calibrating", isCalibratingTmp);
        if (mOnCalibratingListener != null && isCalibratingTmp[0] != mIsCalibrating) {
            mOnCalibratingListener.OnCalibrating(mIsCalibrating = isCalibratingTmp[0]);
        }

        //queueEvent的Runnable在此处被调用
        while (mEventQueue != null && !mEventQueue.isEmpty()) {
            Runnable r = mEventQueue.remove(0);
            if (r != null)  
                r.run();
        }
        mEventQueue.addAll(mNextEventQueue);
        mNextEventQueue.clear();
    }

    //--------------------------------------对外可使用的接口----------------------------------------

    /**
     * 类似GLSurfaceView的queueEvent机制
     */
    public void queueEvent(@NonNull Runnable r) {
        if (mEventQueue != null)
            mEventQueue.add(r);
    }

    /**
     * 类似GLSurfaceView的queueEvent机制
     */
    public void queueEvent(@NonNull List<Runnable> rs) {
        if (mEventQueue != null && !rs.isEmpty())
            mEventQueue.addAll(rs);
    }

    /**
     * 相较于queueEvent，延后一次render被调用
     */
    public void queueNextEvent(@NonNull Runnable r) {
        if (mNextEventQueue != null)
            mNextEventQueue.add(r);
    }

    //--------------------------------------IsTracking（人脸识别回调相关定义）----------------------------------------

    private int mTrackingStatus = 0;

    public interface OnTrackingStatusChangedListener {
        void onTrackingStatusChanged(int status);
    }

    private OnTrackingStatusChangedListener mOnTrackingStatusChangedListener;

    public void setOnTrackingStatusChangedListener(OnTrackingStatusChangedListener mOnTrackingStatusChangedListener) {
        this.mOnTrackingStatusChangedListener = mOnTrackingStatusChangedListener;
    }
//--------------------------------------FaceUnitySystemError（faceunity错误信息回调相关定义）----------------------------------------

    public interface OnSystemErrorListener {
        void onSystemError(String error);
    }

    private OnSystemErrorListener mOnSystemErrorListener;

    public void setOnSystemErrorListener(OnSystemErrorListener mOnSystemErrorListener) {
        this.mOnSystemErrorListener = mOnSystemErrorListener;
    }
//--------------------------------------mIsCalibrating（表情校准回调相关定义）----------------------------------------

    private float mIsCalibrating = 0;

    public interface OnCalibratingListener {
        void OnCalibrating(float isCalibrating);
    }

    private OnCalibratingListener mOnCalibratingListener;

    public void setOnCalibratingListener(OnCalibratingListener mOnCalibratingListener) {
        this.mOnCalibratingListener = mOnCalibratingListener;
    }
//--------------------------------------FPS（FPS相关定义）----------------------------------------

    private static final float NANO_IN_ONE_MILLI_SECOND = 1000000.0f;
    private static final float TIME = 10f;
    private int mCurrentFrameCnt = 0;
    private long mLastOneHundredFrameTimeStamp = 0;
    private long mOneHundredFrameFUTime = 0;
    private boolean mNeedBenchmark = true;
    private long mFuCallStartTime = 0;

    private OnFUDebugListener mOnFUDebugListener;

    public interface OnFUDebugListener {
        void onFpsChange(double fps, double renderTime);
    }

    public void setOnFUDebugListener(OnFUDebugListener mOnFUDebugListener) {
        this.mOnFUDebugListener = mOnFUDebugListener;
    }

    private void benchmarkFPS() {
        if (!mNeedBenchmark) return;
        if (++mCurrentFrameCnt == TIME) {
            mCurrentFrameCnt = 0;
            long tmp = System.nanoTime();
            double fps = (1000.0f * NANO_IN_ONE_MILLI_SECOND / ((tmp - mLastOneHundredFrameTimeStamp) / TIME));
            mLastOneHundredFrameTimeStamp = tmp;
            double renderTime = mOneHundredFrameFUTime / TIME / NANO_IN_ONE_MILLI_SECOND;
            mOneHundredFrameFUTime = 0;

            if (mOnFUDebugListener != null) {
                mOnFUDebugListener.onFpsChange(fps, renderTime);
            }
        }
    }
}
