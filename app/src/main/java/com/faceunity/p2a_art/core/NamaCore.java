package com.faceunity.p2a_art.core;

import android.content.Context;

import com.faceunity.p2a_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

/**
 * Created by tujh on 2018/12/17.
 */
public class NamaCore extends BaseCore {
    private static final String TAG = NamaCore.class.getSimpleName();

    public NamaCore(Context context, FUP2ARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);
    }

    @Override
    public int[] itemsArray() {
        return new int[0];
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        if (img == null) return 0;
        return faceunity.fuDualInputToTexture(img, tex, faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, w, h, mFrameId++, itemsArray());
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
