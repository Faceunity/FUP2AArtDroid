package com.faceunity.pta_art.core.base;

/**
 * Controller
 * Created by tujh on 2018/12/18.
 */
public abstract class BaseHandle {
    private static final String TAG = BaseHandle.class.getSimpleName();
    protected final int FUItemHandler_what = FUItemHandler.generateWhatIndex();

    protected BaseCore mBaseCore;
    protected FUItemHandler mFUItemHandler;

    public BaseHandle(BaseCore baseCore, FUItemHandler FUItemHandler) {
        mBaseCore = baseCore;
        mFUItemHandler = FUItemHandler;
    }

    public abstract void release();
}
