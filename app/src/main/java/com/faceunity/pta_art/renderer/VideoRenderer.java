package com.faceunity.pta_art.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;

import com.faceunity.pta_art.gles.ProgramTexture2d;
import com.faceunity.pta_art.gles.core.GlUtil;
import com.faceunity.pta_art.utils.mediacoder.AvcDecoder;

import java.io.IOException;

import static com.faceunity.pta_art.utils.mediacoder.AvcDecoder.FILE_TypeNV21;

public class VideoRenderer implements AvcDecoder.DecodeListener {
    public final static String TAG = VideoRenderer.class.getSimpleName();

    private GLSurfaceView mGLSurfaceView;
    protected int mViewWidth = 1280;
    protected int mViewHeight = 720;

    protected int mVideoWidth = 720;
    protected int mVideoHeight = 1280;
    private int mVideoRotation = 0;

    private volatile byte[] mVideoNV21Byte;

    private volatile boolean isNeedStopDrawFrame = false;
    protected volatile float[] mMvpMatrix = new float[16];
    protected float[] mtx = new float[16];
    private ProgramTexture2d mFullFrameRectTexture2D;

    private int mVideoTextureId = 0;
    //视频相关
    private String path;
    private AvcDecoder avcDecoder;

    public VideoRenderer(GLSurfaceView glSurfaceView) {
        mGLSurfaceView = glSurfaceView;
        avcDecoder = new AvcDecoder();
    }

    //gl环境初始化
    public void onSurfaceCreated() {
        mFullFrameRectTexture2D = new ProgramTexture2d();
    }

    public void onSurfaceChanged(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
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
            mMvpMatrix = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mVideoWidth < mVideoHeight ? mVideoWidth : mVideoHeight,
                    mVideoWidth < mVideoHeight ? mVideoHeight : mVideoWidth);
            Log.i(TAG, "mVideoRotation=" + mVideoRotation
                    + "--videoW=" + mVideoWidth
                    + "--videoH=" + mVideoHeight);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        avcDecoder.videoDecode(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushBuffer(byte[] data) {
        mVideoNV21Byte = data;
        if (!isNeedStopDrawFrame) {
            mGLSurfaceView.requestRender();
        }
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onError(String msg) {
    }

    public void drawVideo() {
    }

    public byte[] getVideoNV21Byte() {
        return mVideoNV21Byte;
    }

    public int getVideoTextureId() {
        return 0;
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

    public float[] getMvpMatrix() {
        return mMvpMatrix;
    }

    public float[] getMtx() {
        return mtx;
    }

    private void onSurfaceDestroy() {
        if (mFullFrameRectTexture2D != null) {
            mFullFrameRectTexture2D.release();
            mFullFrameRectTexture2D = null;
        }

        if (mVideoTextureId != 0) {
            int[] textures = new int[]{mVideoTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mVideoTextureId = 0;
        }
    }
}
