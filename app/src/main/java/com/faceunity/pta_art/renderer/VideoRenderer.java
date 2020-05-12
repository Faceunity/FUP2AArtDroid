package com.faceunity.pta_art.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.gles.ProgramLandmarks;
import com.faceunity.pta_art.gles.core.GlUtil;
import com.faceunity.pta_art.gles.yuv.ProgramYUV;
import com.faceunity.pta_art.utils.SmallCameraPositionManager;
import com.faceunity.pta_art.utils.mediacoder.AvcDecoder;

import java.io.IOException;
import java.util.concurrent.Executors;

import static com.faceunity.pta_art.utils.mediacoder.AvcDecoder.FILE_TypeNV21;

public class VideoRenderer implements AvcDecoder.DecodeListener {
    public final static String TAG = VideoRenderer.class.getSimpleName();

    private int temp180, temp320, cropX, cropY;
    private int temp20, temp342;
    private int mGLSurfaceViewWidth, mGLSurfaceViewHeight;

    private GLSurfaceView mGLSurfaceView;
    protected int mViewWidth = 720;
    protected int mViewHeight = 1280;
    protected int mVideoWidth = 720;
    protected int mVideoHeight = 1280;
    private int mVideoRotation = 0;
    private volatile byte[] mVideoNV21Byte;
    private volatile boolean isNeedStopDrawFrame = false;
    protected float[] mtx = new float[16];
    protected float[] mvp = new float[16];
    //视频相关
    private String path;
    private AvcDecoder avcDecoder;
    private VideoListener videoListener;
    private int[] wh = new int[2];
    private SmallCameraPositionManager mSmallCameraPositionManager;
    private ProgramYUV programYUV;
    //点位相关
    private ProgramLandmarks mProgramLandmarks;
    private boolean isShowLandmarks = true;//是否显示点位

    public VideoRenderer(GLSurfaceView glSurfaceView) {
        mGLSurfaceView = glSurfaceView;
        avcDecoder = new AvcDecoder();
        temp20 = mGLSurfaceView.getContext().getResources().getDimensionPixelSize(R.dimen.x20);
        temp180 = mGLSurfaceView.getContext().getResources().getDimensionPixelSize(R.dimen.x180);
        temp320 = mGLSurfaceView.getContext().getResources().getDimensionPixelSize(R.dimen.x320);
        temp342 = mGLSurfaceView.getContext().getResources().getDimensionPixelSize(R.dimen.x342);
    }

    //gl环境初始化
    public void onSurfaceCreated() {
        programYUV = new ProgramYUV();
        mProgramLandmarks = new ProgramLandmarks();
    }

    public void onSurfaceChange(int width, int height) {
        mGLSurfaceViewWidth = width;
        mGLSurfaceViewHeight = height;
    }

    public void setPath(String inputFile, VideoListener videoListener) {
        this.path = inputFile;
        resetByte();
        this.videoListener = videoListener;
    }

    public void resetByte() {
        mVideoNV21Byte = null;
    }

    public boolean isDecodeFinish() {
        return avcDecoder.isDecodeFinish();
    }

    public void play() {
        try {
            avcDecoder.setDecoderParams(path, FILE_TypeNV21);
            avcDecoder.setDecodeListener(this);
            int[] wh = avcDecoder.getVideoWH();
            mVideoWidth = wh[0];
            mVideoHeight = wh[1];
            mVideoRotation = wh[2];

            Matrix.setIdentityM(mtx, 0);
            setWH();
            Log.i(TAG, "mVideoRotation=" + mVideoRotation
                    + "--videoW=" + mVideoWidth
                    + "--videoH=" + mVideoHeight);
            Executors.newCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        avcDecoder.videoDecode(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushBuffer(byte[] data) {
//        Log.d(TAG, "pushBuffer:" + data.length);
        if (mVideoNV21Byte == null || mVideoNV21Byte.length != data.length) {
            mVideoNV21Byte = new byte[data.length];
        }
        System.arraycopy(data, 0, mVideoNV21Byte, 0, data.length);
        if (!isNeedStopDrawFrame) {
            mGLSurfaceView.requestRender();
        }
    }

    @Override
    public void onComplete() {
        if (videoListener != null) {
            videoListener.onComplete();
        }
    }

    @Override
    public void onEnd() {
        if (videoListener != null) {
            videoListener.onEnd();
        }
    }

    @Override
    public void onError(String msg) {
        if (videoListener != null) {
            videoListener.onError(msg);
        }
    }

    /**
     * 获取录制视频的宽高
     *
     * @return
     */
    public void setWH() {
        wh[0] = mVideoWidth;
        wh[1] = mVideoHeight;
        switch (mVideoRotation) {
            case 90:
            case 270:
                wh[0] = mVideoHeight;
                wh[1] = mVideoWidth;
                break;
        }

        programYUV.setYuvDataSize(wh[0], wh[1]);
        if (wh[0] > wh[1]) {
            mViewWidth = temp320;
            mViewHeight = temp180;
        } else {
            mViewWidth = temp180;
            mViewHeight = temp320;
        }
        cropX = mGLSurfaceViewWidth - mViewWidth - temp20;

        mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, wh[0],
                wh[1]);
    }

    /**
     * 绘制相机buffer
     *
     * @param w
     * @param h
     * @param nv21
     */
    public void drawCamera(int w, int h, byte[] nv21) {
        programYUV.setYuvDataSize(w, h);
        programYUV.feedDataNV21(nv21);

        cropX = mGLSurfaceViewWidth - temp180 - temp20;
        cropY = mSmallCameraPositionManager.getStartY();
        mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, temp180, temp320, w,
                h);

        programYUV.drawNV21(cropX, cropY, temp180, temp320, mvp);
        if (isShowLandmarks) {
            mProgramLandmarks.drawFrame(cropX, cropY, temp180, temp320);
        }
    }

    public void drawVideo(byte[] img) {
        if (img == null) {
            return;
        }
        cropY = mSmallCameraPositionManager.getStartY();
        programYUV.feedDataNV21(img);
        programYUV.drawNV21(cropX, cropY, mViewWidth, mViewHeight, mvp);
        if (isShowLandmarks) {
            mProgramLandmarks.drawFrame(cropX, cropY, mViewWidth, mViewHeight);
        }
    }

    /***********************************点位相关**********************************/
    public void refreshLandmarks(float[] landmarks, int width, int height, int rotation, int type) {
        mProgramLandmarks.refresh(landmarks, width, height, rotation, type);
    }


    public byte[] getVideoNV21Byte() {
        return mVideoNV21Byte;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoRotation() {
        return mVideoRotation;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setNeedStopDrawFrame(boolean needStopDrawFrame) {
        isNeedStopDrawFrame = needStopDrawFrame;
    }

    public void setShowLandmarks(boolean isShowLandmarks) {
        this.isShowLandmarks = isShowLandmarks;
    }

    public void onSurfaceChanged(SmallCameraPositionManager mSmallCameraPositionManager) {
        this.mSmallCameraPositionManager = mSmallCameraPositionManager;
    }

    public interface VideoListener {
        void onComplete();

        void onEnd();

        void onError(String error);
    }
}
