package com.faceunity.pta_art.core.driver.text;

import android.content.Context;

import com.faceunity.pta_art.MainActivity;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文字驱动场景
 * Created by tujh on 2018/12/17.
 */
public class PTATextDriveCore extends BaseCore {
    private static final String TAG = PTATextDriveCore.class.getSimpleName();

    private AvatarTextDriveHandle avatarTextDriveHandle;
    public static final int ITEM_ARRAYS_BG = 0;
    public static final int ITEM_ARRAYS_CONTROLLER = 1;
    public static final int ITEM_ARRAYS_EFFECT = 2;
    public static final int ITEM_ARRAYS_FXAA = 3;
    public static final int ITEM_ARRAYS_COUNT = 4;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    public int fxaaItem;
    //语音相关
    private boolean isPlaying;
    private int currentFrameId;
    public static double changeRate;
    private List<float[]> mExpressions;
    private double[] expressions = new double[57];

    private WeakReference<MainActivity> weakReferenceActivity;

    public PTATextDriveCore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);
        weakReferenceActivity = new WeakReference<>((MainActivity) context);

        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);

        Arrays.fill(avatarInfo.mRotation, 0.0f);
        Arrays.fill(avatarInfo.mExpression, 0.0f);
        Arrays.fill(avatarInfo.mPupilPos, 0.0f);
        Arrays.fill(avatarInfo.mRotationMode, 0.0f);
    }

    public AvatarTextDriveHandle createAvatarHandle(int controller) {
        return avatarTextDriveHandle = new AvatarTextDriveHandle(this, mFUItemHandler, controller);
    }

    /**
     * 渲染语音Expressions
     *
     * @param Expression
     */
    public void startPlay(List<float[]> Expression) {
        isPlaying = true;
        currentFrameId = 0;
        if (mExpressions == null) {
            mExpressions = new ArrayList<>();
        }
        mExpressions.clear();
        mExpressions.addAll(Expression);
        //绘制图像1/60帧，而口型系数是15ms一帧，这里需要按照图像转换，进行同步
        changeRate = 1.0f / (60 * 0.015);
    }

    /**
     * 停止渲染语音Expressions
     *
     * @param
     */
    public void stopPlay() {
        isPlaying = false;
        currentFrameId = 0;
        if (mExpressions != null) {
            mExpressions.clear();
        }
    }

    /**
     * 重置口型
     */
    public void resetBlendExpression() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                double[] expressions = new double[57];
                Arrays.fill(expressions, 0.0f);
                faceunity.fuItemSetParam(avatarTextDriveHandle.controllerItem, "blend_expression", expressions);
            }
        });
    }

    @Override
    public int[] itemsArray() {
        if (avatarTextDriveHandle != null) {
            mItemsArray[ITEM_ARRAYS_CONTROLLER] = avatarTextDriveHandle.controllerItem;
        }
        return mItemsArray;
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        if (img == null) return 0;

        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "face_detector_status", 0);
        avatarInfo.mRotationMode[0] = 0;
        avatarInfo.mIsValid = false;

        //文字驱动模式
        if (isPlaying && avatarTextDriveHandle != null) {
            if ((int) (changeRate * currentFrameId) >= mExpressions.size()) {
                Arrays.fill(expressions, 0.0f);
                faceunity.fuItemSetParam(avatarTextDriveHandle.controllerItem, "blend_expression", expressions);
                if ((int) (changeRate * (currentFrameId - 2)) >= mExpressions.size()) {
                    stopPlay();
                }
            } else {
                // 拷贝一次数组，不污染原有数据
                for (int i = 0; i < expressions.length; i++) {
                    expressions[i] = mExpressions.get((int) (changeRate * currentFrameId))[i];
                }
                faceunity.fuItemSetParam(avatarTextDriveHandle.controllerItem, "blend_expression", expressions);
            }
            currentFrameId++;
        }
        return faceunity.fuRenderBundles(avatarInfo,
                0, w, h, mFrameId++, itemsArray());
    }

    @Override
    public void release() {
        avatarTextDriveHandle.setModelmat(true);
        avatarTextDriveHandle.release();
        queueEvent(destroyItem(fxaaItem));
    }

    @Override
    public void unBind() {
        if (avatarTextDriveHandle != null)
            avatarTextDriveHandle.unBindAll();
    }

    @Override
    public void bind() {
        if (avatarTextDriveHandle != null)
            avatarTextDriveHandle.bindAll();
    }
}
