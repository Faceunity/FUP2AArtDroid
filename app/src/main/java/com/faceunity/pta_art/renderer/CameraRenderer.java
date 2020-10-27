package com.faceunity.pta_art.renderer;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.faceunity.pta_art.gles.FBOUtils;
import com.faceunity.pta_art.gles.ProgramLandmarks;
import com.faceunity.pta_art.gles.ProgramTexture2d;
import com.faceunity.pta_art.gles.ProgramTextureOES;
import com.faceunity.pta_art.gles.core.GlUtil;
import com.faceunity.pta_art.utils.CameraUtils;
import com.faceunity.pta_art.utils.FPSUtil;
import com.faceunity.pta_art.utils.SmallCameraPositionManager;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.pta_helper.pic.PictureEncoder;
import com.faceunity.wrapper.faceunity;

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
    private final SmallCameraPositionManager mSmallCameraPositionManager;

    public interface OnCameraRendererStatusListener {
        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, int rotation);

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
    private int arTextureHeight = 1280;
    private int arTextureWidth = 720;
//    private HandlerThread mCameraThread;
//    private Handler mCameraHandler;

    private byte[] mCameraNV21Byte;
    private faceunity.RotatedImage mRotatedImage;//cpu buffer
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
    private boolean isShowLandmarks = false;
    private boolean isNeedStopDrawFrame = false;
    private boolean isNeedStopDrawToScreen = false;

    private FPSUtil mFPSUtil;

    public CameraRenderer(Activity activity, GLSurfaceView GLSurfaceView) {
        mActivity = activity;
        mGLSurfaceView = GLSurfaceView;
        //视频渲染接口
        videoRenderer = new VideoRenderer(mGLSurfaceView);
        mSmallCameraPositionManager = new SmallCameraPositionManager(mViewHeight, mViewWidth);
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
        mActivity = null;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCameraNV21Byte = data;
        mCamera.addCallbackBuffer(data);
        if (!isNeedStopDrawFrame && !isShowVideo) {
            mGLSurfaceView.requestRender();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullFrameRectTexture2D = new ProgramTexture2d();
        mFullFrameRectTextureOES = new ProgramTextureOES();
        mProgramLandmarks = new ProgramLandmarks();
        mCameraTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        videoRenderer.onSurfaceCreated();
        mRotatedImage = new faceunity.RotatedImage();
        cameraStartPreview();

        mOnCameraRendererStatusListener.onSurfaceCreated(gl, config);
        mFPSUtil = new FPSUtil();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, mViewWidth = width, mViewHeight = height);
        mSmallCameraPositionManager.updataWH(mViewHeight, mViewWidth);
        videoRenderer.onSurfaceChanged(mSmallCameraPositionManager);
        // 固定身体驱动-视频驱动的模式下的纹理高度为1280，宽度根据屏幕比例进行计算
        arTextureWidth = arTextureHeight * width / height;
        mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
        videoRenderer.onSurfaceChange(width, height);
        mOnCameraRendererStatusListener.onSurfaceChanged(gl, width, height);
        mFPSUtil.resetLimit();
    }

    private int rendWidth, rendHeight;//渲染的宽和高
    private boolean isBodyDrive = false;//是否是身体驱动

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mFullFrameRectTexture2D == null) return;
        if (isChangeCamera || mCameraNV21Byte == null) {
            if (!isShowVideo) {
                drawToScreen();
                return;
            }
        } else if (!isShowVideo) {
            try {
                mSurfaceTexture.updateTexImage();
                mSurfaceTexture.getTransformMatrix(mtx);
            } catch (Exception e) {
                return;
            }
        }

        rendWidth = mCameraHeight;
        rendHeight = mCameraWidth;//渲染的宽和高

        //对双输入的cpu buffer进行旋转、镜像，使其与texture对齐
        int NVFormat = faceunity.FU_FORMAT_NV21_BUFFER;
        int rotateMode = faceunity.FU_ROTATION_MODE_0;
        int flipX;
        int flipY;
        if (!isNeedStopDrawFrame) {
            if (isShowVideo) {
                if (videoRenderer.getVideoNV21Byte() == null) {
                    return;
                }
                //绘制视频YUV
                int videoRotation = 0;
                flipX = 0;
                flipY = 0;
                switch (videoRenderer.getVideoRotation()) {
                    case 90:
                        rotateMode = faceunity.FU_ROTATION_MODE_90;
                        break;
                    case 180:
                        rotateMode = faceunity.FU_ROTATION_MODE_180;
                        break;
                    case 270:
                        rotateMode = faceunity.FU_ROTATION_MODE_270;
                        break;
                    default:
                        break;
                }
                faceunity.fuSetOutputResolution(mCameraHeight, mCameraWidth);
                faceunity.fuSetInputCameraMatrix(flipX, flipY, videoRenderer.getVideoRotation());
                if (rotateMode == 0) {
                    mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(videoRenderer.getVideoNV21Byte(), 0, videoRenderer.getVideoWidth(), videoRenderer.getVideoHeight(),
                            videoRotation);
                } else {
                    faceunity.fuRotateImage(mRotatedImage, videoRenderer.getVideoNV21Byte(), NVFormat, videoRenderer.getVideoWidth(), videoRenderer.getVideoHeight(), rotateMode, flipX, flipY);
                    mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(mRotatedImage.mData, 0, mRotatedImage.mWidth, mRotatedImage.mHeight,
                            videoRotation);
                }
            } else {
                if (mCameraNV21Byte == null) {
                    return;
                }
                rotateMode = mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? faceunity.FU_ROTATION_MODE_270 : faceunity.FU_ROTATION_MODE_90;
                flipX = mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 1 : 0;
                flipY = 0;

                faceunity.fuRotateImage(mRotatedImage, mCameraNV21Byte, NVFormat, mCameraWidth, mCameraHeight, rotateMode, flipX, flipY);
                //设置texture的绘制方式
                faceunity.fuSetInputCameraMatrix(flipX, flipY, rotateMode);

                rendWidth = mRotatedImage.mWidth;
                rendHeight = mRotatedImage.mHeight;
                faceunity.fuSetOutputResolution(rendWidth, rendHeight);
                if (offlineNum >= 0 && mIsNeedTakePic) {
                    rendWidth = offlineW;
                    rendHeight = offlineH;
                    mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(null, 0, rendWidth, rendHeight, 0);
                } else {
                    mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(mRotatedImage.mData, mCameraTextureId, rendWidth, rendHeight, 0);
                }
            }
        }
        drawToScreen();

        if (isShowCamera && mCameraNV21Byte != null) {
            if (!isShowVideo)
                mFullFrameRectTextureOES.drawFrame(mCameraTextureId, mtx, mvp,
                        mSmallCameraPositionManager.getStartX(),
                        mSmallCameraPositionManager.getStartY(),
                        mSmallCameraPositionManager.getPreviewCameraWidth(),
                        mSmallCameraPositionManager.getPreviewCameraHeight());
            if (isShowLandmarks) {
                mProgramLandmarks.drawFrame(mSmallCameraPositionManager.getStartX(),
                        mSmallCameraPositionManager.getStartY(),
                        mSmallCameraPositionManager.getPreviewCameraWidth(),
                        mSmallCameraPositionManager.getPreviewCameraHeight());
            }
        }

        if (mRotatedImage != null) {
            if (offlineNum == -1) {
                checkPic(mFuTextureId, GlUtil.IDENTITY_MATRIX, mRotatedImage.mWidth, mRotatedImage.mHeight);
            } else {
                checkPicOffline(mFuTextureId, GlUtil.IDENTITY_MATRIX, rendWidth, rendHeight);
            }
        } else {
            if (offlineNum == -1) {
                checkPic(mFuTextureId, mtx, mCameraHeight, mCameraWidth);
            } else {
                checkPicOffline(mFuTextureId, mtx, mCameraHeight, mCameraWidth);
            }
        }

        if (!isNeedStopDrawFrame && !isShowVideo) {
            mGLSurfaceView.requestRender();
        }
        if (!isShowVideo) {
            mFPSUtil.limit();
        }
    }

    private void drawToScreen() {
        /**
         * 拍照时停止绘制纹理到屏幕
         * 拍照完成后恢复渲染
         */
        if (mIsNeedTakePic) {
            return;
        }
        if (offlineNum >= 0) {
            offlineNum--;
            return;
        }
        /**
         * 拍照时，不清除原来的屏幕内容
         * 如果在拍照时清屏，则会造成闪屏
         */
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (isNeedStopDrawToScreen) {
            // 绘制上一帧的数据
            mFullFrameRectTexture2D.drawFrame(fboTextureId, GlUtil.IDENTITY_MATRIX, mLastMvp);
            return;
        }

        if (isShowVideo) {
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, GlUtil.IDENTITY_MATRIX, mvp);
            //绘制视频buffer
            videoRenderer.drawVideo(videoRenderer.getVideoRotation() == 0 ?
                    videoRenderer.getVideoNV21Byte() : mRotatedImage.mData);
        } else if (mFuTextureId > 0) {
            //纹理矩阵传单位阵接口，因为已经预先把纹理和buffer旋转成竖直的
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, GlUtil.IDENTITY_MATRIX, mvp);
            if (isBodyDrive) {
                videoRenderer.drawCamera(rendWidth, rendHeight, mRotatedImage.mData);
            }
        } else {
            mFullFrameRectTextureOES.drawFrame(mCameraTextureId, mtx, mvp);
        }
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
        if (fboUtils != null) {
            fboUtils.deleteFBO();
            fboUtils = null;
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
                    Camera.getCameraInfo(i, info);
                    if (info.facing == cameraType) {
                        cameraId = i;
                        mCamera = Camera.open(i);
                        mCurrentCameraType = cameraType;
                        break;
                    }
                }
                if (mCamera == null) {
                    cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    Camera.getCameraInfo(cameraId, info);
                    mCamera = Camera.open(cameraId);
                    mCurrentCameraType = cameraId;
                }
                if (mCamera == null) {
                    throw new RuntimeException("No cameras");
                }


                mCameraOrientation = CameraUtils.getCameraOrientation(cameraId);
                CameraUtils.setCameraDisplayOrientation(mActivity, cameraId, mCamera);

                Camera.Parameters parameters = mCamera.getParameters();
                CameraUtils.setFocusModes(parameters);

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

    public float[] getMtx() {
        return mtx;
    }

    public void setNeedStopDrawFrame(boolean needStopDrawFrame) {
        isNeedStopDrawFrame = needStopDrawFrame;
        if (!isNeedStopDrawFrame) {
            if (!isShowVideo)
                mGLSurfaceView.requestRender();
        }
    }


    private float[] mLastMvp;
    private FBOUtils fboUtils;
    private int fboTextureId;

    public void setNeedStopDrawToScreen(boolean needStopDrawToScreen) {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (needStopDrawToScreen) {
                    mLastMvp = mvp;
                    fboUtils = new FBOUtils();
                    fboUtils.init();
                    fboTextureId = fboUtils.drawFBO(mFuTextureId, mCameraHeight, mCameraWidth, 0);
                }
                isNeedStopDrawToScreen = needStopDrawToScreen;
            }
        });
    }

    public boolean isNeedStopDrawFrame() {
        return isNeedStopDrawFrame;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public void setShowLandmarks(boolean showLandmarks) {
        isShowLandmarks = showLandmarks;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public boolean isShowLandmarks() {
        return isShowLandmarks;
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

    private int offlineNum = -1;
    private TakePhotoCallBackOffline takePhotoCallBackOffline;
    private int offlineW, offlineH;//自定义离屏拍照的大小

    /**
     * 离屏拍照
     *
     * @param takePhotoOffline
     */
    public void takePicForOffline(TakePhotoCallBackOffline takePhotoOffline,
                                  int offlineW, int offlineH) {
        if (mTakePicing) {
            return;
        }
        //拍照完成的回调
        takePhotoCallBackOffline = takePhotoOffline;
        //是否开始拍照
        mIsNeedTakePic = true;
        //是否正在拍照
        mTakePicing = true;
        //拍照开始渲染的帧数
        offlineNum = 0;
        this.offlineW = offlineW;
        this.offlineH = offlineH;
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

    private void checkPicOffline(int textureId, float[] mtx, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        if (offlineNum < 3) {
            offlineNum++;
            return;
        }
        mIsNeedTakePic = false;
        if (takePhotoCallBackOffline != null) {
            takePhotoCallBackOffline.endTackPhoto();
        }
        PictureEncoder.encoderPicture(textureId, mtx, GlUtil.IDENTITY_MATRIX, texWidth, texHeight, new PictureEncoder.OnEncoderPictureListener() {
            @Override
            public void onEncoderPictureListener(Bitmap bitmap) {
                if (takePhotoCallBackOffline != null) {
                    takePhotoCallBackOffline.takePhotoCallBack(bitmap);
                }
                mTakePicing = false;
            }
        });
    }

    public interface TakePhotoCallBack {
        void takePhotoCallBack(Bitmap bmp);
    }

    public interface TakePhotoCallBackOffline {
        void takePhotoCallBack(Bitmap bmp);

        void endTackPhoto();
    }

    public void refreshLandmarks(float[] landmarks) {
        mProgramLandmarks.refresh(landmarks, mCameraHeight, mCameraWidth, mCameraOrientation, mCurrentCameraType);
    }

    public void refreshVideoLandmarks(float[] landmarks, int width, int height) {
        videoRenderer.refreshLandmarks(landmarks, width, height, isShowVideo ? 270 : mCameraOrientation, isShowVideo ? 1 : mCurrentCameraType);
    }

    //视频渲染
    private boolean isShowVideo = false;
    private VideoRenderer videoRenderer;
    private VideoRenderer.VideoListener listener;
    private String path;
    private boolean isDecodeFinish;
//    private boolean IsVertical = true;//是否是竖屏

    public void setVideoPath(String videoPath) {
        this.path = videoPath;
        if (listener == null) {
            listener = new VideoRenderer.VideoListener() {
                @Override
                public void onComplete() {
                    setVideoPath(path);
                }

                @Override
                public void onEnd() {
                    isDecodeFinish = true;
                }

                @Override
                public void onError(String error) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showCenterToast(mActivity,
                                    error);
                        }
                    });
                }
            };
        }
        if (TextUtils.isEmpty(videoPath)) {
            videoRenderer.resetByte();
            isDecodeFinish = videoRenderer.isDecodeFinish();
            while (!isDecodeFinish) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isShowVideo = false;
        } else {
            isShowVideo = true;
            videoRenderer.setPath(videoPath, listener);
            videoRenderer.play();
        }
    }

    public void setStopVideo(boolean isNeedStopVideo) {
        videoRenderer.setNeedStopDrawFrame(isNeedStopVideo);
        if (isNeedStopVideo) {
            videoRenderer.resetByte();
        }
    }

    /**
     * 显示视频驱动的点位
     */
    public void setVideoLandmarks(boolean isShowLandmarks) {
        videoRenderer.setShowLandmarks(isShowLandmarks);
    }

    public void setBodyDrive(boolean isBodyDrive) {
        this.isBodyDrive = isBodyDrive;
    }

    public boolean isShowVideo() {
        return isShowVideo;
    }


    public SmallCameraPositionManager getmSmallCameraPositionManager() {
        return mSmallCameraPositionManager;
    }
}
