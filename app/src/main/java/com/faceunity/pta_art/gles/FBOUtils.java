package com.faceunity.pta_art.gles;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class FBOUtils {
    //--------------------------------------FBO绘制----------------------------------------
    private ProgramTexture2d programTexture2d;
    private int[] mOriginViewPort = new int[4];
    private int[] mFboId = new int[1];

    public void init() {
        programTexture2d = new ProgramTexture2d();
    }

    public int drawFBO(int texId, int width, int height, int rotation) {
        createFBO(width, height);
        float[] transformMatrix = new float[16];
        Matrix.setIdentityM(transformMatrix, 0);
        Matrix.setRotateM(transformMatrix, 0, 360 - rotation, 0, 0, 1);

        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, mFboId, 0);
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, mOriginViewPort, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);
        GLES20.glViewport(0, 0, width, height);
        programTexture2d.drawFrameMVP(texId, transformMatrix);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId[0]);
        GLES20.glViewport(mOriginViewPort[0], mOriginViewPort[1], mOriginViewPort[2], mOriginViewPort[3]);

        return fboTex[0];
    }

    private int fboId[];
    private int fboTex[];
    private int renderBufferId[];

    private int fboWidth, fboHeight;
    private int num = 2;

    private void createFBO(int width, int height) {
        if (fboTex != null && (fboWidth != width || fboHeight != height)) {
            deleteFBO();
        }

        fboWidth = width;
        fboHeight = height;

        if (fboTex == null) {
            fboId = new int[num];
            fboTex = new int[num];
            renderBufferId = new int[num];

//generate fbo id
            GLES20.glGenFramebuffers(num, fboId, 0);
//generate texture
            GLES20.glGenTextures(num, fboTex, 0);
//generate render buffer
            GLES20.glGenRenderbuffers(num, renderBufferId, 0);

            for (int i = 0; i < fboId.length; i++) {
//Bind Frame buffer
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[i]);
//Bind texture
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex[i]);
//Define texture parameters
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//Bind render buffer and define buffer dimension
                GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferId[i]);
                GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
//Attach texture FBO color attachment
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTex[i], 0);
//Attach render buffer to depth attachment
                GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferId[i]);
//we are done, reset
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
        }
    }

    public void deleteFBO() {
        if (fboId == null || fboTex == null || renderBufferId == null) {
            return;
        }
        GLES20.glDeleteFramebuffers(num, fboId, 0);
        GLES20.glDeleteTextures(num, fboTex, 0);
        GLES20.glDeleteRenderbuffers(num, renderBufferId, 0);
        fboId = null;
        fboTex = null;
        renderBufferId = null;
    }
}
