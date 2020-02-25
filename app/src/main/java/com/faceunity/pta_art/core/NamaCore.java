package com.faceunity.pta_art.core;

import android.content.Context;

import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

/**
 * nama场景
 * Created by tujh on 2018/12/17.
 */
public class NamaCore extends BaseCore {
    private static final String TAG = NamaCore.class.getSimpleName();

    public NamaCore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);
    }

    @Override
    public int[] itemsArray() {
        return new int[0];
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        if (img == null) return 0;
        //如果道具为空，则不进行图片等识别操作
        int fuTex = faceunity.fuRenderBundlesWithCamera(img, tex, faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, w, h, mFrameId++, itemsArray());
        faceunity.fuFaceCaptureProcessFrame(face_capture, img, w, h, faceunity.FU_FORMAT_NV21_BUFFER, 0);
        return fuTex;
    }

    @Override
    public void unBind() {

    }

    @Override
    public void bind() {

    }

    @Override
    public void release() {

    }
}
