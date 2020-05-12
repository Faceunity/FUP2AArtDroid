package com.faceunity.pta_art.utils;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.faceunity.pta_art.gles.ProgramTexture2d;
import com.faceunity.pta_art.gles.core.GlUtil;

/**
 * Created by tujh on 2019/1/29.
 */
public class BackgroundUtil {

    public static float[] imgDataMatrix = new float[16];
    public static final float[] ROTATE_90 = {0.0F, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    private ProgramTexture2d mProgramTexture2d;
    private int width, height;
    private int[] fboTexs;
    private int[] fboIds;

    private int backgroundTexId;
    private float[] backgroundMVP;
    private boolean useBitmapBackground = false;

    public BackgroundUtil(int width, int height) {
        this.width = width;
        this.height = height;
        fboIds = new int[1];
        fboTexs = new int[1];
        GlUtil.createFBO(fboTexs, fboIds, width, height);
        mProgramTexture2d = new ProgramTexture2d();
        Matrix.setIdentityM(imgDataMatrix, 0);
        Matrix.scaleM(imgDataMatrix, 0, 1, -1, 1);
    }

    public void loadBackground(String path) {
        useBitmapBackground = true;
        Bitmap src = BitmapUtil.loadBitmap(path, 720);
        backgroundTexId = GlUtil.createImageTexture(src);
        backgroundMVP = changeMVPMatrix(GlUtil.IDENTITY_MATRIX, width, height, src.getWidth(), src.getHeight());
    }

    public int drawBackground(int texId) {
        if (!isHasBackground()) return texId;
        if (!useBitmapBackground) {
            return texId;
        }
        int[] fboIdNow = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboIdNow, 0);
        int[] viewPortNow = new int[4];
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewPortNow, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIds[0]);
        GLES20.glViewport(0, 0, width, height);
        mProgramTexture2d.drawFrame(backgroundTexId, imgDataMatrix, backgroundMVP);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mProgramTexture2d.drawFrame(texId, GlUtil.IDENTITY_MATRIX);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIdNow[0]);
        GLES20.glViewport(viewPortNow[0], viewPortNow[1], viewPortNow[2], viewPortNow[3]);
        return fboTexs[0];
    }

    private float[] changeMVPMatrix(float[] mvpMatrix, float viewWidth, float viewHeight, float textureWidth, float textureHeight) {
        float scale = viewWidth * textureHeight / viewHeight / textureWidth;
        if (scale == 1) {
            return mvpMatrix;
        } else {
            float[] mvp = new float[16];
            float[] tmp = new float[16];
            Matrix.setIdentityM(tmp, 0);
            Matrix.scaleM(tmp, 0, scale > 1 ? 1F : (1F / scale), scale > 1 ? scale : 1F, 1F);
            Matrix.multiplyMM(mvp, 0, tmp, 0, mvpMatrix, 0);
            return mvp;
        }
    }

    public boolean isHasBackground() {
        return backgroundTexId > 0;
    }

    public void setUseBitmapBackground(boolean useBitmapBackground) {
        this.useBitmapBackground = useBitmapBackground;
    }

    public void release() {
        GlUtil.deleteFBO(fboTexs, fboIds);
    }
}
