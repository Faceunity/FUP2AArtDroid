package com.faceunity.pta_art.core;

import android.content.Context;

import com.faceunity.pta_art.MainActivity;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.pta_art.fragment.BodyDriveFragment;
import com.faceunity.wrapper.faceunity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AR场景
 * Created by tujh on 2018/12/17.
 */
public class PTAARCore extends BaseCore {
    private static final String TAG = PTAARCore.class.getSimpleName();

    private AvatarARHandle avatarARHandle;
    public static final int ITEM_ARRAYS_BG = 0;
    public static final int ITEM_ARRAYS_CONTROLLER = 1;
    public static final int ITEM_ARRAYS_EFFECT = 2;
    public static final int ITEM_ARRAYS_FXAA = 3;
    public static final int ITEM_ARRAYS_COUNT = 4;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    private boolean isNeedTrackFace = false;
    private int mode;

    public int fxaaItem;
    public int bgItem;
    private int[] bgItems = new int[1];

    //语音相关
    private boolean isPlaying;
    private int currentFrameId;
    private double changeRate;
    private List<float[]> mExpressions;
    private int renderNum;//渲染次数
    private double[] expressions = new double[57];

    private faceunity.SplitViewInfo info = new faceunity.SplitViewInfo();//分屏信息

    private WeakReference<MainActivity> weakReferenceActivity;

    public PTAARCore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);
        weakReferenceActivity = new WeakReference<>((MainActivity) context);

        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);
        bgItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_default_bg);
        bgItems[0] = bgItem;
    }

    public AvatarARHandle createAvatarARHandle(int controller) {
        return avatarARHandle = new AvatarARHandle(this, mFUItemHandler, controller);
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
        //绘制图像1/30帧，而口型系数是15ms一帧，这里需要按照图像转换，进行同步
        changeRate = 1.0f / (30 * 0.015);
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
                faceunity.fuItemSetParam(avatarARHandle.controllerItem, "blend_expression", expressions);
            }
        });
    }

    public void setRenderNum(int num) {
        renderNum = num;
    }

    @Override
    public int[] itemsArray() {
        if (avatarARHandle != null) {
            mItemsArray[ITEM_ARRAYS_CONTROLLER] = avatarARHandle.controllerItem;
            mItemsArray[ITEM_ARRAYS_EFFECT] = avatarARHandle.filterItem.handle;
            if (mode == BodyDriveFragment.TYPE_AR_DRIVE) {
                mItemsArray[ITEM_ARRAYS_BG] = 0;
            } else {
                mItemsArray[ITEM_ARRAYS_BG] = bgItem;
            }
        }
        return mItemsArray;
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        if (img == null) return 0;
        int flags = 1;
        int isTracking = 0;
        //是否开启面部追踪
        if (isNeedTrackFace && img != null) {
            //trackface
            faceunity.fuTrackFaceWithTongue(img, 0, w, h);
            isTracking = faceunity.fuIsTracking();
            if (isTracking > 0) {
                /**
                 * rotation 人脸三维旋转，返回值为旋转四元数，长度4
                 */
                faceunity.fuGetFaceInfo(0, "rotation_aligned", avatarInfo.mRotation);
                /**
                 * expression  表情系数，长度57
                 */
                faceunity.fuGetFaceInfo(0, "expression_aligned", avatarInfo.mExpression);
                /**
                 * pupil pos 眼球方向，长度2
                 */
                faceunity.fuGetFaceInfo(0, "pupil_pos", avatarInfo.mPupilPos);
                /**
                 * rotation mode 人脸朝向，0-3分别对应手机四种朝向，长度1
                 */
                faceunity.fuGetFaceInfo(0, "rotation_mode", avatarInfo.mRotationMode);
            }
        }
        if (isTracking <= 0) {
            Arrays.fill(avatarInfo.mRotation, 0.0f);
            Arrays.fill(avatarInfo.mExpression, 0.0f);
            Arrays.fill(avatarInfo.mPupilPos, 0.0f);
            Arrays.fill(avatarInfo.mRotationMode, 0.0f);
        }
        if (rotation < 0) {
            avatarInfo.mRotationMode[0] = 0;
        } else {
            avatarInfo.mRotationMode[0] = 0;
        }
        avatarInfo.mIsValid = isTracking > 0 ? true : false;

        if (mode == BodyDriveFragment.TYPE_TEXT_DRIVE) {
            //文字驱动模式
            if (isPlaying) {
                if ((int) (changeRate * currentFrameId) >= mExpressions.size()) {
                    Arrays.fill(expressions, 0.0f);
                    faceunity.fuItemSetParam(avatarARHandle.controllerItem, "blend_expression", expressions);
                    if ((int) (changeRate * (currentFrameId - 2)) >= mExpressions.size()) {
                        stopPlay();
                    }
                } else {
                    // 拷贝一次数组，不污染原有数据
                    for (int i = 0; i < expressions.length; i++) {
                        expressions[i] = mExpressions.get((int) (changeRate * currentFrameId))[i];
                    }
                    faceunity.fuItemSetParam(avatarARHandle.controllerItem, "blend_expression", expressions);
                }
                currentFrameId++;
            }
            int fuTex = faceunity.fuRenderBundles(avatarInfo,
                    0, w, h, mFrameId++, itemsArray());
            if (renderNum < 0)
                return 0;
            renderNum++;
            if (renderNum <= 3) {
                return 0;
            }
            return fuTex;
        } else if (mode == BodyDriveFragment.TYPE_AR_DRIVE) {
            //AR模式
            int fuTex = faceunity.fuRenderBundlesWithCamera(img, tex, flags, w, h, mFrameId++, itemsArray());
            faceunity.fuTrackFace(img, 0, w, h);
            return fuTex;
        }
        return 0;
    }

    /**
     * 进入身体驱动模式
     * 只使用dde检测数据，不进入面部驱动模式，只有在检测到半身及以上才可以驱动身体
     * <p>
     * 语音驱动模式
     * 只使用dde检测数据
     *
     * @param needTrackFace
     */
    public void setNeedTrackFace(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
    }

    public void enterFaceDrive(boolean needTrackFace) {
        avatarARHandle.setNeedTrackFace(needTrackFace);
    }

    @Override
    public void release() {
        if (mode != BodyDriveFragment.TYPE_AR_DRIVE) {
            faceunity.fuBindItems(avatarARHandle.controllerItem, bgItems);
        }
        avatarARHandle.release();
        queueEvent(destroyItem(fxaaItem));
        queueEvent(destroyItem(bgItem));
    }

    @Override
    public void onCameraChange(int currentCameraType, int inputImageOrientation) {
        super.onCameraChange(currentCameraType, inputImageOrientation);
        avatarARHandle.onCameraChange(currentCameraType, inputImageOrientation);
    }

    @Override
    public void unBind() {
        if (avatarARHandle != null) {
            faceunity.fuUnBindItems(avatarARHandle.controllerItem, bgItems);
            avatarARHandle.unBindAll();
        }
    }

    @Override
    public void bind() {
        if (avatarARHandle != null) {
            avatarARHandle.bindAll();
        }
    }


    public void setMode(int mode) {
        this.mode = mode;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mode == BodyDriveFragment.TYPE_AR_DRIVE) {
                    faceunity.fuUnBindItems(avatarARHandle.controllerItem, bgItems);
                } else {
                    faceunity.fuBindItems(avatarARHandle.controllerItem, bgItems);
                }
            }
        });
    }

    @Override
    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        if (isNeedTrackFace && isTracking() > 0)
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
        return landmarksData;
    }
}
