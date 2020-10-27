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

    private final int controllerItem;
    int flags = faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE;

    public NamaCore(Context context, FUPTARenderer fuP2ARenderer, int controllerItem) {
        super(context, fuP2ARenderer);
        this.controllerItem = controllerItem;
        setFaceCapture(true);
    }

    @Override
    public int[] itemsArray() {
        return new int[0];
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        if (img == null) return 0;
        return faceunity.fuRenderBundlesWithCamera(img, tex, flags, w, h, mFrameId++, itemsArray());
    }

    @Override
    public void unBind() {

    }

    @Override
    public void bind() {

    }

    @Override
    public void release() {
        setFaceCapture(false);
    }

    public void setFaceCapture(boolean isOpen) {
        mFUP2ARenderer.queueEvent(new Runnable() {
            @Override
            public void run() {
                //3.设置enable_face_processor，说明启用或者关闭面部追踪，value = 1.0表示开启，value = 0.0表示关闭
                faceunity.fuItemSetParam(controllerItem, "enable_face_processor", isOpen ? 1.0 : 0.0);
            }
        });
    }
}
