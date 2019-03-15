package com.faceunity.p2a_art.utils;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.faceunity.p2a_art.gles.ProgramTexture2d;
import com.faceunity.p2a_art.gles.core.GlUtil;

/**
 * Created by tujh on 2019/1/29.
 */
public class BackgroundUtil {

    public static final float[] imgDataMatrix = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    public static final float[] ROTATE_90 = {0.0F, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    private ProgramTexture2d mProgramTexture2d;
    private int width, height;
    private int fboId, fboTex;

    private int backgroundTexId;
    private float[] backgroundMVP;

    public BackgroundUtil(int width, int height) {
        this.width = width;
        this.height = height;
        int[] fboIds = new int[1];
        int[] fboTexs = new int[1];
        GlUtil.createFBO(fboTexs, fboIds, width, height);
        fboId = fboIds[0];
        fboTex = fboTexs[0];
        mProgramTexture2d = new ProgramTexture2d();
    }

    public void loadBackground(String path) {
        Bitmap src = BitmapUtil.loadBitmap(path, 720);
        backgroundTexId = GlUtil.createImageTexture(src);
        backgroundMVP = changeMVPMatrix(GlUtil.IDENTITY_MATRIX, width, height, src.getHeight(), src.getWidth());
    }

    public int drawBackground(int texId) {
        if (!isHasBackground()) return texId;
        int[] fboIdNow = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboIdNow, 0);
        int[] viewPortNow = new int[4];
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewPortNow, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glViewport(0, 0, width, height);
        mProgramTexture2d.drawFrame(backgroundTexId, imgDataMatrix, backgroundMVP);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mProgramTexture2d.drawFrame(texId, GlUtil.IDENTITY_MATRIX);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIdNow[0]);
        GLES20.glViewport(viewPortNow[0], viewPortNow[1], viewPortNow[2], viewPortNow[3]);
        return fboTex;
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
}
