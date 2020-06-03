package com.faceunity.pta_art.core;

import android.content.Context;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

/**
 * Created by tujh on 2018/12/17.
 */
public class PTACore extends BaseCore {
    private static final String TAG = PTACore.class.getSimpleName();

    private AvatarHandle avatarHandle;

    public static final int ITEM_ARRAYS_EFFECT = 0;
    public static final int ITEM_ARRAYS_CONTROLLER = 1;
    public static final int ITEM_ARRAYS_FXAA = 2;
    public static final int ITEM_ARRAYS_COUNT = 3;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];

    public int fxaaItem;
    private int controller_config;
    private boolean isNeedTrackFace = false;
    // 设置即将要播放的动画位置
    private int currentHomeAnimationPosition = -1;
    // 是否可以再次设置下一个播放动画
    private boolean canResetHomeAnimationPosition = true;
    // 默认背景
    public int defaultItem;
    // 平地阴影道具
    public int planeItemLeft, planeItemRight;
    private int lastLoadCompletedFrameId = 0;

    public PTACore(PTACore core) {
        super(core.mContext, core.mFUP2ARenderer);
        avatarHandle = core.avatarHandle;
        System.arraycopy(core.mItemsArray, 0, mItemsArray, 0, ITEM_ARRAYS_COUNT);
        fxaaItem = core.fxaaItem;
        face_capture = core.face_capture;
        currentCameraItem = mFUItemHandler.loadFUItem(FilePathFactory.CAMERA_WHOLE_BODY);
    }

    public PTACore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);

        controller_config = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_controller_config_new);
        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);
        face_capture = mFUP2ARenderer.createFaceCapture();
        currentCameraItem = mFUItemHandler.loadFUItem(FilePathFactory.CAMERA_WHOLE_BODY);
        defaultItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_default_bg);
        planeItemLeft = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_plane_left);
        planeItemRight = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_plane_right);
    }


    public AvatarHandle createAvatarHandle() {
        return avatarHandle = new AvatarHandle(this, mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                faceunity.fuBindItems(avatarHandle.controllerItem, new int[]{controller_config});

                closeDDE();

                faceunity.fuItemSetParam(avatarHandle.controllerItem, "arMode", (360 - mInputImageOrientation) / 90);
                //将这个模型注册到controller的当前角色上，并分配人脸索引，索引从0开始
                faceunity.fuItemSetParamu64(avatarHandle.controllerItem, "register_face_capture_manager", face_capture);
                faceunity.fuItemSetParam(avatarHandle.controllerItem, "register_face_capture_face_id", 0.0);

                faceunity.fuItemSetParam(avatarHandle.controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(avatarHandle.controllerItem, "reset_all", 3);
                faceunity.fuBindItems(avatarHandle.controllerItem, new int[]{currentCameraItem});

                bindPlane();
            }
        });
    }

    @Override
    public int[] itemsArray() {
        if (avatarHandle != null) {
            mItemsArray[ITEM_ARRAYS_CONTROLLER] = avatarHandle.controllerItem;
        }
        return mItemsArray;
    }

    /**
     * fuAvatarToTexture 用于人脸驱动
     *
     * @param img 图片buffer
     * @param tex 图片纹理
     * @param w   图片宽
     * @param h   图片高
     * @return
     */
    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        int isTracking = 0;
        //是否开启人脸驱动
        if (isNeedTrackFace && img != null) {
            //如果开启CNN 面部追踪，每帧都需要调用fuFaceCaptureProcessFrame处理输入图像
            faceunity.fuFaceCaptureProcessFrame(face_capture, img, w, h, faceunity.FU_FORMAT_NV21_BUFFER, 0);
            //获取识别人脸数
            int face_num = faceunity.fuFaceCaptureGetResultFaceNum(face_capture);
            if (face_num > 0) {
                isTracking = faceunity.fuFaceCaptureGetResultIsFace(face_capture, 0);
                /**
                 * rotation 人脸三维旋转，返回值为旋转四元数，长度4
                 */
                faceunity.fuFaceCaptureGetResultRotation(face_capture, 0, avatarInfo.mRotation);
                /**
                 * expression  表情系数，长度57
                 */
                faceunity.fuFaceCaptureGetResultExpression(face_capture, 0, avatarInfo.mExpression);
                /**
                 * pupil pos 眼球方向，长度4 xyzw
                 */
                faceunity.fuFaceCaptureGetResultEyesRotation(face_capture, 0, avatarInfo.mPupilPos);
                /**
                 * rotation mode 人脸朝向，0-3分别对应手机四种朝向，长度1
                 * 新接口已去除
                 */
//                faceunity.fuFaceCaptureGetResult(face_capture, 0, avatarInfo.mRotationMode);
            }
        }
        if (isTracking <= 0) {
            Arrays.fill(avatarInfo.mRotation, 0.0f);
            Arrays.fill(avatarInfo.mExpression, 0.0f);
            Arrays.fill(avatarInfo.mPupilPos, 0.0f);
            Arrays.fill(avatarInfo.mRotationMode, 0.0f);
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CONTROLLER], "face_detector_status", 0);
        }

        avatarInfo.mRotationMode[0] = 0;
        avatarInfo.mIsValid = isTracking > 0;


        int loadCount = avatarHandle.getLoadCount();
        if (loadCount != Integer.MAX_VALUE) {
            if (avatarHandle.expressionItem.handle > 0) {
                float progress = avatarHandle.getAnimateProgress(avatarHandle.expressionItem.handle);
                if (loadCount - progress < 0.05 && loadCount - progress > 0) {
                    // progress并不是一个整型，我们这里取一个范围，也就是loadCount± 0.05 就算是播放完毕

                    if (aniLoadCompletedListener != null && (mFrameId - lastLoadCompletedFrameId > 1)) {
                        // (mFrameId - lastLoadCompletedFrameId > 1) 表示上次结束动画在上一帧，这一帧又结束一个新动画，
                        // 这显然是不合理的，所以做过滤
                        aniLoadCompletedListener.loadCompleted(Math.round(progress), currentHomeAnimationPosition, !canResetHomeAnimationPosition);
                        canResetHomeAnimationPosition = true;
                        lastLoadCompletedFrameId = mFrameId;
                    }
                }
            }
        }
        return faceunity.fuRenderBundles(avatarInfo,
                                         0, w, h, mFrameId++, itemsArray());
    }

    public void setCurrentInstancceId(int id) {
        if (avatarHandle != null)
            avatarHandle.setCurrentInstancceId(id);
    }

    /**
     * 关闭dde
     */
    public void closeDDE() {
        faceunity.fuItemSetParam(avatarHandle.controllerItem, "is_close_dde", 1.0);
        avatarHandle.setFaceCapture(false);
    }

    /**
     * 解绑之前的2D背景，并绑定上默认背景
     */
    public void unBindAndBindDefault() {
        unBind();
        bindDefault();
    }

    public void unBindDefault() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuUnBindItems(avatarHandle.controllerItem, new int[]{defaultItem});
            }
        });
    }

    public void bindDefault() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuBindItems(avatarHandle.controllerItem, new int[]{defaultItem});
            }
        });
    }

    public void bindPlane() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuBindItems(avatarHandle.controllerItem, new int[]{planeItemLeft, planeItemRight});
            }
        });
    }

    public void unBindPlane() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuUnBindItems(avatarHandle.controllerItem, new int[]{planeItemLeft, planeItemRight});
            }
        });
    }

    @Override
    public void unBind() {
        if (avatarHandle != null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuUnBindItems(avatarHandle.controllerItem, new int[]{currentCameraItem});
                }
            });
            avatarHandle.unBindAll();
        }
    }

    @Override
    public void bind() {
        if (avatarHandle != null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(avatarHandle.controllerItem, "target_position", new double[]{0.0, 0.0f, 0.0f});
                    faceunity.fuItemSetParam(avatarHandle.controllerItem, "target_angle", 0);
                    faceunity.fuItemSetParam(avatarHandle.controllerItem, "reset_all", 3);
                    faceunity.fuBindItems(avatarHandle.controllerItem, new int[]{currentCameraItem});
                }
            });
            avatarHandle.bindAll();
        }
    }

    @Override
    public void release() {
        wholeBodyCameraItem = 0;
        smallWholeBodyCameraItem = 0;
        halfLengthBodyCameraItem = 0;
        bigHalfLengthBodyCameraItem = 0;
        currentCameraItem = 0;
        canResetHomeAnimationPosition = true;
        currentHomeAnimationPosition = -1;

        unBindPlane();
        faceunity.fuUnBindItems(avatarHandle.controllerItem, new int[]{controller_config});
        queueEvent(destroyItem(controller_config));

        queueEvent(destroyItem(defaultItem));
        queueEvent(destroyItem(fxaaItem));
        queueEvent(destroyItem(wholeBodyCameraItem));
        queueEvent(destroyItem(smallWholeBodyCameraItem));
        queueEvent(destroyItem(halfLengthBodyCameraItem));
        queueEvent(destroyItem(bigHalfLengthBodyCameraItem));
        queueEvent(destroyFaceCaptureItem(face_capture));
        queueEvent(destroyItem(planeItemLeft));
        queueEvent(destroyItem(planeItemRight));

        avatarHandle.release();
    }

    public void setNeedTrackFace(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
        avatarHandle.setCNNTrackFace(isNeedTrackFace);
    }


    public interface AniLoadCompletedListener {
        /**
         * 当前的动画已经播放了一遍，也可能是多遍，主要根据loadCount来判断
         *
         * @param loadCount             当前动画播放了多少遍
         * @param nextAnimationPosition 需要播放的下一个动画所在FilePathFactory.getHomeSwitchAnimation()集合中的位置
         * @param haveNextAni           是否有下一个动画，如果没有下一个动画，nextAnimationPosition的值为当前动画
         *                              所在FilePathFactory.getHomeSwitchAnimation()集合中的位置
         */
        void loadCompleted(int loadCount, int nextAnimationPosition, boolean haveNextAni);
    }

    private AniLoadCompletedListener aniLoadCompletedListener;

    public void setAniLoadCompletedListener(AniLoadCompletedListener aniLoadCompletedListener) {
        this.aniLoadCompletedListener = aniLoadCompletedListener;
    }

    public interface AniRefreshNowListener {
        /**
         * 动画需要立即播放
         *
         * @param currentHomeAnimationPosition 动画所在FilePathFactory.getHomeSwitchAnimation()集合中的位置
         */
        void refreshNow(int currentHomeAnimationPosition);
    }

    private AniRefreshNowListener aniRefreshNowListener;

    public void setAniRefreshNowListener(AniRefreshNowListener aniRefreshNowListener) {
        this.aniRefreshNowListener = aniRefreshNowListener;
    }

    public void setNextHomeAnimationPosition() {
        int size = FilePathFactory.getHomeSwitchAnimation().size();
        if (size == 0) {
            return;
        }
        if (canResetHomeAnimationPosition) {
            currentHomeAnimationPosition = ++currentHomeAnimationPosition % size;
            if (size == 1) {
                // 如果只有一个动画，则需要改动avatarHandle中的loadCount，否则会导致动画的异常结束
                // 因为我们是通过loadCount - progress 去判断动画是否执行完毕，如果重复的执行同一个动画，
                // progress是不会重置为0 的，所以我们需要增加loadCount
                avatarHandle.setCurrentAniLoadCount(avatarHandle.getLoadCount() + 1);
            }
            canResetHomeAnimationPosition = false;
        }
        if (avatarHandle.getLoadCount() == Integer.MAX_VALUE) {
            // 当前为idle动画，可以立即刷新动画
            if (aniRefreshNowListener != null) {
                aniRefreshNowListener.refreshNow(currentHomeAnimationPosition);
                canResetHomeAnimationPosition = true;
            }
        }
    }

    @Override
    public void loadWholeBodyCamera() {
        avatarHandle.resetAllFront();
        super.loadWholeBodyCamera();
    }

    @Override
    public void loadSmallWholeBodyCamera() {
        avatarHandle.resetAllFront();
        super.loadSmallWholeBodyCamera();
    }

    @Override
    public void loadHalfLengthBodyCamera() {
        avatarHandle.resetAllFront();
        super.loadHalfLengthBodyCamera();
    }

    @Override
    public void loadBigHalfLengthBodyCamera() {
        avatarHandle.resetAllFront();
        super.loadBigHalfLengthBodyCamera();
    }

    @Override
    protected int createAndLoadCameraItem(int itemId, String bundlePath) {
        if (itemId == 0) {
            itemId = mFUItemHandler.loadFUItem(bundlePath);
        }
        int finalItemId = itemId;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuUnBindItems(avatarHandle.controllerItem, new int[]{currentCameraItem});
                faceunity.fuBindItems(avatarHandle.controllerItem, new int[]{currentCameraItem = finalItemId});
            }
        });
        return itemId;
    }
}
