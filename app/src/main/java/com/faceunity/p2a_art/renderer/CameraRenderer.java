package com.faceunity.p2a_art.renderer;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AlertDialog;

import com.faceunity.p2a_art.gles.ProgramLandmarks;
import com.faceunity.p2a_art.gles.ProgramTexture2d;
import com.faceunity.p2a_art.gles.ProgramTextureOES;
import com.faceunity.p2a_art.gles.core.GlUtil;
import com.faceunity.p2a_art.utils.CameraUtils;
import com.faceunity.p2a_art.utils.FPSUtil;
import com.faceunity.p2a_helper.pic.PictureEncoder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Camera相关处理
 * Camera.PreviewCallback camera数据回调
 * GLSurfaceView.Renderer GLSurfaceView相应的创建销毁与绘制回调
 * <p>
 * Created by tujh on 2018/3/2.
 */

public class CameraRenderer implements Camera.PreviewCallback, GLSurfaceView.Renderer {
    public final static String TAG = CameraRenderer.class.getSimpleName();

    private Activity mActivity;
    private GLSurfaceView mGLSurfaceView;

    public interface OnCameraRendererStatusListener {
        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight);

        void onSurfaceDestroy();

        void onCameraChange(int currentCameraType, int cameraOrientation);
    }

    private OnCameraRendererStatusListener mOnCameraRendererStatusListener;

    public void setOnCameraRendererStatusListener(OnCameraRendererStatusListener onCameraRendererStatusListener) {
        mOnCameraRendererStatusListener = onCameraRendererStatusListener;
    }

    private int mViewWidth = 1280;
    private int mViewHeight = 720;

    private boolean isOpenCamera;
    private boolean isChangeCamera;
    private final Object mCameraLock = new Object();
    private Camera mCamera;
    private static final int PREVIEW_BUFFER_COUNT = 3;
    private byte[][] previewCallbackBuffer;
    private int mCameraOrientation;
    private int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mCameraWidth = 1280;
    private int mCameraHeight = 720;
    private HandlerThread mCameraThread;
    private Handler mCameraHandler;

    private byte[] mCameraNV21Byte;
    private SurfaceTexture mSurfaceTexture;
    private int mCameraTextureId;

    private int mFuTextureId;
    private static final float[] mtxAvatar = {0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F};
    private final float[] mtx = {0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F};
    private float[] mvp = new float[16];

    private ProgramTexture2d mFullFrameRectTexture2D;
    private ProgramTextureOES mFullFrameRectTextureOES;
    private ProgramLandmarks mProgramLandmarks;
    private boolean isShowCamera = false;
    private boolean isNeedStopDrawFrame = false;

    private FPSUtil mFPSUtil;

    public CameraRenderer(Activity activity, GLSurfaceView GLSurfaceView) {
        mActivity = activity;
        mGLSurfaceView = GLSurfaceView;
        mCameraThread = new HandlerThread(TAG);
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    public void onDestroy() {
        final CountDownLatch count = new CountDownLatch(1);
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                onSurfaceDestroy();
                count.countDown();
            }
        });
        try {
            count.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mGLSurfaceView.onPause();
        mCameraThread.quitSafely();
        mCameraThread = null;
        mCameraHandler = null;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCameraNV21Byte = data;
        mCamera.addCallbackBuffer(data);
        if (!isNeedStopDrawFrame)
            mGLSurfaceView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullFrameRectTexture2D = new ProgramTexture2d();
        mFullFrameRectTextureOES = new ProgramTextureOES();
        mProgramLandmarks = new ProgramLandmarks();
        mCameraTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        cameraStartPreview();

        mOnCameraRendererStatusListener.onSurfaceCreated(gl, config);
        mFPSUtil = new FPSUtil();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, mViewWidth = width, mViewHeight = height);
        mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
        mOnCameraRendererStatusListener.onSurfaceChanged(gl, width, height);
        mFPSUtil.resetLimit();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mFullFrameRectTexture2D == null) return;
        if (isChangeCamera || (isOpenCamera && mCameraNV21Byte == null)) {
            drawToScreen();
            return;
        } else if (mCameraNV21Byte != null) {
            try {
                mSurfaceTexture.updateTexImage();
                mSurfaceTexture.getTransformMatrix(mtx);
            } catch (Exception e) {
                return;
            }
        }

        if (!isNeedStopDrawFrame)
            mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(mCameraNV21Byte, mCameraTextureId, mCameraWidth, mCameraHeight);
        drawToScreen();

        if (isShowCamera && mCameraNV21Byte != null) {
            mFullFrameRectTextureOES.drawFrame(mCameraTextureId, mtx, mvp, 0, mViewHeight * 2 / 3, mViewWidth / 3, mViewHeight / 3);
            mProgramLandmarks.drawFrame(0, mViewHeight * 2 / 3, mViewWidth / 3, mViewHeight / 3);
        }
        checkPic(mFuTextureId, mtx, mCameraHeight, mCameraWidth);
        if (!isNeedStopDrawFrame)
            mGLSurfaceView.requestRender();
        mFPSUtil.limit();
    }

    private void drawToScreen() {
        if (mFuTextureId > 0)
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, mtx, mvp);
        else
            mFullFrameRectTextureOES.drawFrame(mCameraTextureId, mtx, mvp);
    }

    private void onSurfaceDestroy() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mCameraTextureId != 0) {
            int[] textures = new int[]{mCameraTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mCameraTextureId = 0;
        }

        if (mFullFrameRectTexture2D != null) {
            mFullFrameRectTexture2D.release();
            mFullFrameRectTexture2D = null;
        }

        if (mFullFrameRectTextureOES != null) {
            mFullFrameRectTextureOES.release();
            mFullFrameRectTextureOES = null;
        }

        if (mProgramLandmarks != null) {
            mProgramLandmarks.release();
            mProgramLandmarks = null;
        }
        mOnCameraRendererStatusListener.onSurfaceDestroy();
    }

    public void openCamera() {
        openCamera(mCurrentCameraType);
    }

    @SuppressWarnings("deprecation")
    public void openCamera(final int cameraType) {
        try {
            synchronized (mCameraLock) {
                isOpenCamera = true;
                mCameraNV21Byte = null;
                Camera.CameraInfo info = new Camera.CameraInfo();
                int cameraId = 0;
                int numCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numCameras; i++) {
                    Camera.getCameraInfo(i, info);
                    if (info.facing == cameraType) {
                        cameraId = i;
                        final int finalI = i;
                        final CountDownLatch count = new CountDownLatch(1);
                        mCameraHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCamera = Camera.open(finalI);
                                count.countDown();
                            }
                        });
                        count.await();
                        mCurrentCameraType = cameraType;
                        break;
                    }
                }

                mCameraOrientation = CameraUtils.getCameraOrientation(cameraId);
                CameraUtils.setCameraDisplayOrientation(mActivity, cameraId, mCamera);

                Camera.Parameters parameters = mCamera.getParameters();

                int[] size = CameraUtils.choosePreviewSize(parameters, mCameraWidth, mCameraHeight);
                mCameraWidth = size[0];
                mCameraHeight = size[1];
                mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
                if (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    System.arraycopy(mtxAvatar, 0, mtx, 0, mtx.length);
                mCamera.setParameters(parameters);

                cameraStartPreview();
            }

            mOnCameraRendererStatusListener.onCameraChange(mCurrentCameraType, mCameraOrientation);
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
            new AlertDialog.Builder(mActivity)
                    .setTitle("警告")
                    .setMessage("相机权限被禁用或者相机被别的应用占用！")
                    .setNegativeButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            openCamera(cameraType);
                        }
                    })
                    .setNeutralButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mActivity.onBackPressed();
                        }
                    })
                    .show();
        }
    }

    private void cameraStartPreview() {
        try {
            if (mCameraTextureId == 0 || mCamera == null) {
                return;
            }
            synchronized (mCameraLock) {
                previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight * 3 / 2];
                mCamera.setPreviewCallbackWithBuffer(this);
                for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++)
                    mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
                if (mSurfaceTexture != null)
                    mSurfaceTexture.release();
                mCamera.setPreviewTexture(mSurfaceTexture = new SurfaceTexture(mCameraTextureId));
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseCamera() {
        try {
            synchronized (mCameraLock) {
                isOpenCamera = false;
                mCameraNV21Byte = null;
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.setPreviewTexture(null);
                    mCamera.setPreviewCallbackWithBuffer(null);
                    mCamera.release();
                    mCamera = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeCamera() {
        if (mCameraNV21Byte == null) {
            return;
        }
        isChangeCamera = true;
        releaseCamera();
        openCamera(mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
        isChangeCamera = false;
    }

    public int getCameraOrientation() {
        return mCameraOrientation;
    }

    public int getCurrentCameraType() {
        return mCurrentCameraType;
    }

    public int getCameraWidth() {
        return mCameraWidth;
    }

    public int getCameraHeight() {
        return mCameraHeight;
    }

    public void setNeedStopDrawFrame(boolean needStopDrawFrame) {
        isNeedStopDrawFrame = needStopDrawFrame;
        if (!isNeedStopDrawFrame)
            mGLSurfaceView.requestRender();
    }

    public boolean isNeedStopDrawFrame() {
        return isNeedStopDrawFrame;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    private boolean mTakePicing = false;
    private boolean mIsNeedTakePic = false;
    private TakePhotoCallBack mTakePhotoCallBack;

    public void takePic(TakePhotoCallBack takePhotoCallBack) {
        if (mTakePicing) {
            return;
        }
        mTakePhotoCallBack = takePhotoCallBack;
        mIsNeedTakePic = true;
        mTakePicing = true;
    }

    private void checkPic(int textureId, float[] mtx, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        setNeedStopDrawFrame(true);
        PictureEncoder.encoderPicture(textureId, mtx, GlUtil.IDENTITY_MATRIX, texWidth, texHeight, new PictureEncoder.OnEncoderPictureListener() {
            @Override
            public void onEncoderPictureListener(Bitmap bitmap) {
                if (mTakePhotoCallBack != null) {
                    mTakePhotoCallBack.takePhotoCallBack(bitmap);
                }
                mTakePicing = false;
            }
        });
    }

    public interface TakePhotoCallBack {
        void takePhotoCallBack(Bitmap bmp);
    }

    public void refreshLandmarks(float[] landmarks) {
        mProgramLandmarks.refresh(landmarks, mCameraWidth, mCameraHeight, mCameraOrientation, mCurrentCameraType);
    }
}
