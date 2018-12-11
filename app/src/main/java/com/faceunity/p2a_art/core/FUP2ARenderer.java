package com.faceunity.p2a_art.core;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.wrapper.faceunity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 一个基于Faceunity Nama SDK的简单封装，方便简单集成，理论上简单需求的步骤：
 * <p>
 * 1.通过OnEffectSelectedListener在UI上进行交互
 * 2.合理调用FURenderer构造函数
 * 3.对应的时机调用onSurfaceCreated和onSurfaceDestroyed
 * 4.处理图像时调用onDrawFrame
 * <p>
 * 如果您有更高级的定制需求，Nama API文档请参考http://www.faceunity.com/technical/android-api.html
 */
public class FUP2ARenderer {
    private static final String TAG = FUP2ARenderer.class.getSimpleName();

    private Context mContext;

    /**
     * 目录assets下的 *.bundle为程序的数据文件。
     * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
     * anim_model.bundle：优化表情跟踪功能所需要加载的动画数据文件；适用于使用Animoji和avatar功能的用户，如果不是，可不加载
     * fxaa.bundle：3D绘制抗锯齿数据文件。加载后，会使得3D绘制效果更加平滑。
     * controller.bundle：controller数据文件，用于控制和显示avatar。
     * default_bg.bundle：背景道具，使用方法与普通道具相同。
     * 目录effects下是我们打包签名好的道具
     */
    public static final String BUNDLE_v3 = "v3.bundle";
    public static final String BUNDLE_anim_model = "anim_model.bundle";
    public static final String BUNDLE_fxaa = "fxaa.bundle";
    public static final String BUNDLE_controller = "controller.bundle";
    public static final String BUNDLE_default_bg = "default_bg.bundle";

    private static final int ITEM_ARRAYS_CONTROLLER = 0;
    private static final int ITEM_ARRAYS_EFFECT = 1;
    private static final int ITEM_ARRAYS_FXAA = 2;
    private static final int ITEM_ARRAYS_COUNT = 3;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    public int controllerItem, fxaaItem, bgItem;
    public int headItem, hairItem, glassItem, beardItem, hatItem, bodyItem, clothesItem, expressionItem;
    public String headFile, hairFile, glassFile, beardFile, hatFile, bodyFile, clothesFile, expressionFile;

    public int headARItem, hairARItem, glassARItem, beardARItem, hatARItem;
    public String headARFile, hairARFile, glassARFile, beardARFile, hatARFile;

    //用于和异步加载道具的线程交互
    private HandlerThread mFuItemHandlerThread;
    private Handler mFuItemHandler;

    private int mFrameId = 0;
    private int mInputImageOrientation = 270;
    private int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean isNeedTrackFace = false;
    private int mIsTracking = 0;

    private float[] landmarksData = new float[150];
    private float[] expressionData = new float[46];
    private float[] rotationData = new float[4];
    private float[] pupilPosData = new float[2];
    private float[] rotationModeData = new float[1];
    private float[] faceRectData = new float[4];

    private List<Runnable> mEventQueue;
    private List<Runnable> mNextEventQueue;

    /**
     * 全局加载相应的底层数据包
     */
    public static void initFURenderer(Context ct) {
        Context context = ct.getApplicationContext();
        try {
            //获取faceunity SDK版本信息
            Log.i(TAG, "fu sdk version " + faceunity.fuGetVersion());

            /**
             * fuSetup faceunity初始化
             * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
             *      authpack：用于鉴权证书内存数组。若没有,请咨询support@faceunity.com
             * 首先调用完成后再调用其他FU API
             */
            InputStream v3 = context.getAssets().open(BUNDLE_v3);
            byte[] v3Data = new byte[v3.available()];
            v3.read(v3Data);
            v3.close();
            faceunity.fuSetup(v3Data, null, authpack.A());

            /**
             * 加载优化表情跟踪功能所需要加载的动画数据文件anim_model.bundle；
             * 启用该功能可以使表情系数及avatar驱动表情更加自然，减少异常表情、模型缺陷的出现。该功能对性能的影响较小。
             * 启用该功能时，通过 fuLoadAnimModel 加载动画模型数据，加载成功即可启动。该功能会影响通过fuGetFaceInfo获取的expression表情系数，以及通过表情驱动的avatar模型。
             * 适用于使用Animoji和avatar功能的用户，如果不是，可不加载
             */
            InputStream animModel = context.getAssets().open(BUNDLE_anim_model);
            byte[] animModelData = new byte[animModel.available()];
            animModel.read(animModelData);
            animModel.close();
            faceunity.fuLoadAnimModel(animModelData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取faceunity sdk 版本库
     */
    public static String getVersion() {
        return faceunity.fuGetVersion();
    }

    /**
     * FURenderer构造函数
     */
    public FUP2ARenderer(Context context) {
        this.mContext = context.getApplicationContext();
        mEventQueue = Collections.synchronizedList(new ArrayList<Runnable>());
        mNextEventQueue = Collections.synchronizedList(new ArrayList<Runnable>());
        mFuItemHandlerThread = new HandlerThread("FUItemHandlerThread");
        mFuItemHandlerThread.start();
        mFuItemHandler = new FUItemHandler(mFuItemHandlerThread.getLooper());
    }

    /**
     * 创建及初始化faceunity相应的资源
     */
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");

        faceunity.fuSetExpressionCalibration(2);
        faceunity.fuSetMaxFaces(1);//设置多脸，目前最多支持8人。
        faceunity.fuSetAsyncTrackFace(0);

        mItemsArray[ITEM_ARRAYS_CONTROLLER] = controllerItem = loadItem(BUNDLE_controller);
        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = loadItem(BUNDLE_fxaa);
        mItemsArray[ITEM_ARRAYS_EFFECT] = bgItem = loadItem(BUNDLE_default_bg);
    }

    /**
     * 双输入接口(fuDualInputToTexture)(处理后的画面数据并不会回写到数组)，由于省去相应的数据拷贝性能相对最优，推荐使用。
     *
     * @param img NV21数据
     * @param tex 纹理ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        if (tex <= 0 || img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame date null");
            return 0;
        }
        prepareDrawFrame();

        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuDualInputToTexture(img, tex, faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 检测人脸接口
     *
     * @param img NV21数据
     * @param w
     * @param h
     */
    public void trackFace(byte[] img, int w, int h) {
        if (img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "trackFace img " + img + " w " + w + " h " + h);
            return;
        }
        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        faceunity.fuTrackFace(img, 0, w, h);
    }

    /**
     * 使用 fuTrackFace + fuAvatarToTexture 的方法组合绘制画面，该组合没有camera画面绘制，适用于animoji等相关道具的绘制。
     * fuTrackFace 获取识别到的人脸信息
     * fuAvatarToTexture 依据人脸信息绘制道具
     *
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrameAvatar(int w, int h) {
        Arrays.fill(landmarksData, 0.0f);
        Arrays.fill(rotationData, 0.0f);
        Arrays.fill(expressionData, 0.0f);
        Arrays.fill(pupilPosData, 0.0f);
        Arrays.fill(rotationModeData, 0.0f);

        mIsTracking = faceunity.fuIsTracking();

        if (mIsTracking > 0 && isNeedTrackFace) {
            /**
             * landmarks 2D人脸特征点，返回值为75个二维坐标，长度75*2
             */
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
            /**
             *rotation 人脸三维旋转，返回值为旋转四元数，长度4
             */
            faceunity.fuGetFaceInfo(0, "rotation", rotationData);
            /**
             * expression  表情系数，长度46
             */
            faceunity.fuGetFaceInfo(0, "expression", expressionData);
            /**
             * pupil pos 人脸朝向，0-3分别对应手机四种朝向，长度1
             */
            faceunity.fuGetFaceInfo(0, "pupil_pos", pupilPosData);
            /**
             * rotation mode
             */
            faceunity.fuGetFaceInfo(0, "rotation_mode", rotationModeData);
        }
        rotationModeData[0] = (360 - mInputImageOrientation) / 90;

        prepareDrawFrame();
        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int tex = faceunity.fuAvatarToTexture(pupilPosData, expressionData, rotationData, rotationModeData,
                0, w, h, mFrameId++, mItemsArray, mIsTracking);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return tex;
    }

    /**
     * 销毁faceunity相关的资源
     */
    public void onSurfaceDestroyed() {
        if (mFuItemHandlerThread != null) {
            mFuItemHandlerThread.quitSafely();
            mFuItemHandlerThread = null;
            mFuItemHandler = null;
        }
        Arrays.fill(mItemsArray, 0);
        faceunity.fuUnBindItems(controllerItem, new int[]{headItem, bodyItem, hairItem, glassItem, beardItem, hatItem, expressionItem, clothesItem});
        faceunity.fuDestroyAllItems();
        faceunity.fuOnDeviceLost();
        if (mEventQueue != null) {
            mEventQueue.clear();
            mEventQueue = null;
        }
        if (mNextEventQueue != null) {
            mNextEventQueue.clear();
            mNextEventQueue = null;
        }
    }

    /**
     * 每帧处理画面时被调用
     */
    private void prepareDrawFrame() {
        //计算FPS等数据
        benchmarkFPS();

        //获取人脸是否识别，并调用回调接口
        int isTracking = faceunity.fuIsTracking();
        if (mOnTrackingStatusChangedListener != null && mTrackingStatus != isTracking) {
            mOnTrackingStatusChangedListener.onTrackingStatusChanged(mTrackingStatus = isTracking);
        }

        //获取faceunity错误信息，并调用回调接口
        int error = faceunity.fuGetSystemError();
        if (error != 0)
            Log.e(TAG, "fuGetSystemErrorString " + faceunity.fuGetSystemErrorString(error));
        if (mOnSystemErrorListener != null && error != 0) {
            mOnSystemErrorListener.onSystemError(error == 0 ? "" : faceunity.fuGetSystemErrorString(error));
        }

        //获取是否正在表情校准，并调用回调接口
        final float[] isCalibratingTmp = new float[1];
        faceunity.fuGetFaceInfo(0, "is_calibrating", isCalibratingTmp);
        if (mOnCalibratingListener != null && isCalibratingTmp[0] != mIsCalibrating) {
            mOnCalibratingListener.OnCalibrating(mIsCalibrating = isCalibratingTmp[0]);
        }

        //queueEvent的Runnable在此处被调用
        while (mEventQueue != null && !mEventQueue.isEmpty()) {
            Runnable r = mEventQueue.remove(0);
            if (r != null)
                r.run();
        }
        mEventQueue.addAll(mNextEventQueue);
        mNextEventQueue.clear();
    }

    //--------------------------------------对外可使用的接口----------------------------------------

    /**
     * 类似GLSurfaceView的queueEvent机制
     */
    public void queueEvent(@NonNull Runnable r) {
        if (mEventQueue != null)
            mEventQueue.add(r);
    }

    /**
     * 相较于queueEvent，延后一次render被调用
     */
    public void queueNextEvent(@NonNull Runnable r) {
        if (mNextEventQueue != null)
            mNextEventQueue.add(r);
    }

    /**
     * camera切换时需要调用
     *
     * @param currentCameraType     前后置摄像头ID
     * @param inputImageOrientation
     */
    public void onCameraChange(final int currentCameraType, final int inputImageOrientation) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCurrentCameraType = currentCameraType;
                mInputImageOrientation = inputImageOrientation;
                faceunity.fuOnCameraChange();
                faceunity.fuItemSetParam(controllerItem, "is3DFlipH", Camera.CameraInfo.CAMERA_FACING_BACK == mCurrentCameraType ? 1 : 0);
                faceunity.fuItemSetParam(controllerItem, "arMode", (Camera.CameraInfo.CAMERA_FACING_BACK == mCurrentCameraType ? mInputImageOrientation : (360 - mInputImageOrientation)) / 90);
            }
        });
    }

    private int mDefaultOrientation;

    public void setTrackOrientation(final int rotation) {
        if (mTrackingStatus == 0 && mDefaultOrientation != rotation) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mDefaultOrientation = rotation;
                    faceunity.fuSetDefaultOrientation(rotation / 90);//设置识别人脸默认方向，能够提高首次识别的速度
                }
            });
        }
    }

    public int isTracking() {
        return faceunity.fuIsTracking();
    }

    public boolean isNeedTrackFace() {
        return isNeedTrackFace;
    }

    public void setNeedTrackFace(boolean needTrackFace) {
        isNeedTrackFace = needTrackFace;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuOnCameraChange();
            }
        });
    }

    public float[] getLandmarksData() {
        Arrays.fill(landmarksData, 0.0f);
        faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
        return landmarksData;
    }

    public float[] getRotationData() {
        Arrays.fill(rotationData, 0.0f);
        faceunity.fuGetFaceInfo(0, "rotation", rotationData);
        return rotationData;
    }

    public float[] getFaceRectData() {
        Arrays.fill(faceRectData, 0.0f);
        faceunity.fuGetFaceInfo(0, "face_rect", faceRectData);
        return faceRectData;
    }

    public float[] getExpressionData() {
        Arrays.fill(expressionData, 0.0f);
        faceunity.fuGetFaceInfo(0, "expression", expressionData);
        return expressionData;
    }

    /**
     * avatar水平方向旋转角度
     *
     * @param rotDelta 水平方向旋转角度增量
     */
    public void setRotDelta(final float rotDelta) {
        if (showAvatarMode == SHOW_AVATAR_MODE_P2A || showAvatarMode == SHOW_AVATAR_MODE_FACE)
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem, "rot_delta", rotDelta);
                }
            });
    }

    /**
     * avatar所在位置高度
     *
     * @param translateDelta avatar所在位置高度增量
     */
    public void setTranslateDelta(final float translateDelta) {
        if (showAvatarMode == SHOW_AVATAR_MODE_P2A || showAvatarMode == SHOW_AVATAR_MODE_FACE)
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem, "translate_delta", translateDelta);
                }
            });
    }

    /**
     * avatar缩放比例
     *
     * @param scaleDelta avatar缩放比例增量
     */
    public void setScaleDelta(final float scaleDelta) {
        if (controllerItem <= 0) return;
        if (showAvatarMode == SHOW_AVATAR_MODE_P2A || showAvatarMode == SHOW_AVATAR_MODE_FACE)
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem, "scale_delta", scaleDelta);
                }
            });
    }

    public void resetAll(final int gender) {
        if (showAvatarMode == SHOW_AVATAR_MODE_P2A || showAvatarMode == SHOW_AVATAR_MODE_FACE)
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem, "target_scale", gender == 1 ? 0 : 20);
                    faceunity.fuItemSetParam(controllerItem, "target_trans", gender == 1 ? 0 : -10);
                    faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                    faceunity.fuItemSetParam(controllerItem, "reset_all", 30);
                }
            });
    }

    public void resetAllMin(final int gender) {
        if (showAvatarMode == SHOW_AVATAR_MODE_P2A || showAvatarMode == SHOW_AVATAR_MODE_FACE)
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem, "target_scale", 140);
                    faceunity.fuItemSetParam(controllerItem, "target_trans", gender == 1 ? 70 : 60);
                    faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                    faceunity.fuItemSetParam(controllerItem, "reset_all", 30);
                }
            });
    }

    //--------------------------------------showAvatarMode----------------------------------------

    public static final int SHOW_AVATAR_MODE_NONE = 0;
    public static final int SHOW_AVATAR_MODE_P2A = 1;
    public static final int SHOW_AVATAR_MODE_AR = 2;
    public static final int SHOW_AVATAR_MODE_FACE = 3;
    private int showAvatarMode = SHOW_AVATAR_MODE_P2A;

    public int getShowAvatarMode() {
        return showAvatarMode;
    }

    /**
     * 仅用于本demo界面显示不同模式的切换，仅供参考。
     *
     * @param mode
     */
    public void setShowAvatarMode(final int mode) {
        mFuItemHandler.post(new Runnable() {
            @Override
            public void run() {
                final int oldMode = showAvatarMode;
                showAvatarMode = mode;
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        switch (mode) {
                            case SHOW_AVATAR_MODE_NONE:
                                mItemsArray[ITEM_ARRAYS_EFFECT] = 0;
                                mItemsArray[ITEM_ARRAYS_CONTROLLER] = 0;
                                mItemsArray[ITEM_ARRAYS_FXAA] = 0;
                                break;
                            case SHOW_AVATAR_MODE_P2A:
                                if (oldMode == SHOW_AVATAR_MODE_AR) {
                                    faceunity.fuItemSetParam(controllerItem, "quit_ar_mode", 1);

                                    destroyItem(mItemsArray[ITEM_ARRAYS_EFFECT], bgItem);

                                    avatarBindItem(headARItem, headItem);
                                    avatarBindItem(hairARItem, hairItem);
                                    avatarBindItem(glassARItem, glassItem);
                                    avatarBindItem(beardARItem, beardItem);
                                    avatarBindItem(hatARItem, hatItem);
                                    avatarBindItem(0, bodyItem);
                                    avatarBindItem(0, clothesItem);
                                    avatarBindItem(0, expressionItem);

                                    destroyItem(headARItem, headItem);
                                    destroyItem(hairARItem, hairItem);
                                    destroyItem(glassARItem, glassItem);
                                    destroyItem(beardARItem, beardItem);
                                    destroyItem(hatARItem, hatItem);

                                    headARItem = hairARItem = glassARItem = 0;
                                    headARFile = hairARFile = glassARFile = null;
                                } else if (oldMode == SHOW_AVATAR_MODE_FACE) {
                                    faceunity.fuItemSetParam(controllerItem, "quit_facepup_mode", 1);
                                }
                                mItemsArray[ITEM_ARRAYS_EFFECT] = bgItem;
                                mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem;
                                mItemsArray[ITEM_ARRAYS_CONTROLLER] = controllerItem;
                                break;
                            case SHOW_AVATAR_MODE_AR:
                                mItemsArray[ITEM_ARRAYS_EFFECT] = 0;
                                mItemsArray[ITEM_ARRAYS_CONTROLLER] = controllerItem;

                                avatarBindItem(bodyItem, 0);
                                avatarBindItem(clothesItem, 0);
                                avatarBindItem(expressionItem, 0);

                                faceunity.fuItemSetParam(controllerItem, "reset_all", 1);
                                faceunity.fuItemSetParam(controllerItem, "enter_ar_mode", 1);
                                headARItem = headItem;
                                hairARItem = hairItem;
                                glassARItem = glassItem;
                                beardARItem = beardItem;
                                hatARItem = hatItem;
                                break;
                            case SHOW_AVATAR_MODE_FACE:
                                mItemsArray[ITEM_ARRAYS_CONTROLLER] = controllerItem;
                                faceunity.fuItemSetParam(controllerItem, "enter_facepup_mode", 1);
                                break;
                        }

                    }
                });
            }
        });
    }

    /**
     * 进入检测人脸模式
     */
    public void enterTrackMode() {
        if (showAvatarMode != SHOW_AVATAR_MODE_P2A) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "enter_track_rotation_mode", 1);
            }
        });
    }

    /**
     * 退出检测人脸模式
     */
    public void quitTrackMode() {
        if (showAvatarMode != SHOW_AVATAR_MODE_P2A) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "quit_track_rotation_mode", 1);
            }
        });
    }

    //--------------------------------------捏脸----------------------------------------

    public void fuItemSetParamFuItemHandler(final String key, final double values) {
        mFuItemHandler.post(new Runnable() {
            @Override
            public void run() {
                fuItemSetParam(key, values);
                Log.i(TAG, "fuItemSetParamFuItemHandler key " + key + " values " + values);
            }
        });
    }

    private void fuItemSetParam(final String key, final double values) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, key, values);
                Log.i(TAG, "fuItemSetParam key " + key + " values " + values);
            }
        });
    }

    public void fuItemSetParamFuItemHandler(final String key, final double[] values) {
        mFuItemHandler.post(new Runnable() {
            @Override
            public void run() {
                fuItemSetParam(key, values);
                Log.i(TAG, "fuItemSetParamFuItemHandler key " + key + " values " + Arrays.toString(values));
            }
        });
    }

    private void fuItemSetParam(final String key, final double[] values) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, key, values);
                Log.i(TAG, "fuItemSetParam key " + key + " values " + Arrays.toString(values));
            }
        });
    }

    public static final String PARAM_KEY_skin_color = "skin_color";
    public static final String PARAM_KEY_hair_color = "hair_color";
    public static final String PARAM_KEY_hair_color_intensity = "hair_color_intensity";
    public static final String PARAM_KEY_beard_color = "beard_color";
    public static final String PARAM_KEY_hat_color = "hat_color";
    public static final String PARAM_KEY_iris_color = "iris_color";
    public static final String PARAM_KEY_lip_color = "lip_color";
    public static final String PARAM_KEY_glass_color = "glass_color";
    public static final String PARAM_KEY_glass_frame_color = "glass_frame_color";

    // __脸型__：
    public static final String key_shape_cheek = "key_shape_cheek";
    public static final String PARAM_KEY_cheek_narrow = "cheek_narrow";    //|"cheek_narrow"|控制脸颊宽度，瘦|
    public static final String PARAM_KEY_Head_fat = "Head_fat";//|"Head_fat"|控制脸颊宽度，胖|
    public static final String key_shape_Head = "key_shape_Head";
    public static final String PARAM_KEY_Head_shrink = "Head_shrink";//|"Head_shrink"|控制人脸整体的长度，缩短|
    public static final String PARAM_KEY_Head_stretch = "Head_stretch";//|"Head_stretch"|控制人脸整体的长度,伸长|
    public static final String key_shape_HeadBone = "key_shape_HeadBone";
    public static final String PARAM_KEY_HeadBone_shrink = "HeadBone_shrink";//|"HeadBone_shrink"|控制额头区域高低，低|
    public static final String PARAM_KEY_HeadBone_stretch = "HeadBone_stretch";//|"HeadBone_stretch"|控制额头区域高低，高|
    public static final String key_shape_jaw = "key_shape_jaw";
    public static final String PARAM_KEY_jaw_lower = "jaw_lower";//|"jaw_lower"|控制下巴尖/平，尖|
    public static final String PARAM_KEY_jaw_up = "jaw_up";//|"jaw_up"|控制下巴尖/平，平|
    public static final String key_shape_jawbone = "key_shape_jawbone";
    public static final String PARAM_KEY_jawbone_Narrow = "jawbone_Narrow";//|"jawbone_Narrow"|控制下颚宽度，窄|
    public static final String PARAM_KEY_jawbone_Wide = "jawbone_Wide";//|"jawbone_Wide"|控制下颚宽度，宽|
    // __眼睛__：
    public static final String key_shape_Eye_both = "key_shape_Eye_both";
    public static final String PARAM_KEY_Eye_both_in = "Eye_both_in";//|"Eye_both_in"| 眼睛型宽窄,窄|
    public static final String PARAM_KEY_Eye_both_out = "Eye_both_out";//|"Eye_both_out"| 眼睛型宽窄,宽|
    public static final String key_shape_Eye_close_open = "key_shape_Eye_close_open";
    public static final String PARAM_KEY_Eye_close = "Eye_close";//|"Eye_close"| 眼睛型高低,闭眼|
    public static final String PARAM_KEY_Eye_open = "Eye_open";//|"Eye_open"|眼睛型高低,睁眼|
    public static final String key_shape_Eye_inner = "key_shape_Eye_inner";
    public static final String PARAM_KEY_Eye_inner_down = "Eye_inner_down";//|"Eye_inner_down"|眼角上翘/下翘，内眼角向下|
    public static final String PARAM_KEY_Eye_inner_up = "Eye_inner_up";//|"Eye_inner_up"|眼角上翘/下翘，内眼角向上 |
    public static final String key_shape_Eye_down_up = "key_shape_Eye_down_up";
    public static final String PARAM_KEY_Eye_down = "Eye_down";//|"Eye_down"|眼睛整体在脸部区域的位置高低,低|
    public static final String PARAM_KEY_Eye_up = "Eye_up";//|"Eye_up"|眼睛整体在脸部区域的位置高低,高|
    public static final String key_shape_Eye_outter = "key_shape_Eye_outter";
    public static final String PARAM_KEY_Eye_outter_down = "Eye_outter_down";//|"Eye_outter_down"|眼角上翘/下翘，外眼角向下|
    public static final String PARAM_KEY_Eye_outter_up = "Eye_outter_up";//|"Eye_outter_up"|眼角上翘/下翘，外眼角向上|
    // __嘴巴__：
    public static final String key_shape_lipCorner = "key_shape_lipCorner";
    public static final String PARAM_KEY_lipCorner_In = "lipCorner_In";//|"lipCorner_In"|嘴唇宽度,窄|
    public static final String PARAM_KEY_lipCorner_Out = "lipCorner_Out";//|"lipCorner_Out"|嘴唇宽度,宽|
    public static final String key_shape_lowerLip = "key_shape_lowerLip";
    public static final String PARAM_KEY_lowerLip_Thick = "lowerLip_Thick";//|"lowerLip_Thick"|下嘴唇厚度，下嘴唇厚|
    public static final String PARAM_KEY_lowerLip_Thin = "lowerLip_Thin";//|"lowerLip_Thin"|下嘴唇厚度,下嘴唇薄|
    public static final String key_shape_lowerLipSide = "key_shape_lowerLipSide";
    public static final String PARAM_KEY_lowerLipSide_Thick = "lowerLipSide_Thick";//|"lowerLipSide_Thick"|下嘴唇厚度,下嘴角厚|
    public static final String PARAM_KEY_upperLipSide_Thick = "upperLipSide_Thick";//|"upperLipSide_Thick"|上嘴唇厚度，上嘴角厚|
    public static final String key_shape_mouth = "key_shape_mouth";
    public static final String PARAM_KEY_mouth_Down = "mouth_Down";//|"mouth_Down"|嘴部位置高低，低|
    public static final String PARAM_KEY_mouth_Up = "mouth_Up";//|"mouth_Up"|嘴部位置高低，高|
    public static final String key_shape_upperLip = "key_shape_upperLip";
    public static final String PARAM_KEY_upperLip_Thick = "upperLip_Thick";//|"upperLip_Thick"|上嘴唇厚度，上嘴唇厚|
    public static final String PARAM_KEY_upperLip_Thin = "upperLip_Thin";//|"upperLip_Thin"|上嘴唇厚度，上嘴唇薄|
    // __鼻子__：
    public static final String key_shape_nose = "key_shape_nose";
    public static final String PARAM_KEY_nose_Down = "nose_Down";//|"nose_Down"|鼻子位置高低,低|
    public static final String PARAM_KEY_nose_UP = "nose_UP";//|"nose_UP"|鼻子位置高低，高|
    public static final String key_shape_noseTip = "key_shape_noseTip";
    public static final String PARAM_KEY_noseTip_Down = "noseTip_Down";//|"noseTip_Down"|鼻头高低，低|
    public static final String PARAM_KEY_noseTip_Up = "noseTip_Up";//|"noseTip_Up"|鼻头高低，高|
    public static final String key_shape_nostril = "key_shape_nostril";
    public static final String PARAM_KEY_nostril_In = "nostril_In";//|"nostril_In"|鼻翼宽窄，窄|
    public static final String PARAM_KEY_nostril_Out = "nostril_Out";//|"nostril_Out"|鼻翼宽窄，宽|

    public void fuItemSetParamFaceShape(final String key, final double values) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "{\"name\":\"facepup\",\"param\":\"" + key + "\"}", values);
            }
        });
    }

    public float fuItemGetParamShape(final String key) {
        return (float) faceunity.fuItemGetParam(controllerItem, "{\"name\":\"facepup\",\"param\":\"" + key + "\"}");
    }

    public int fuItemGetParamSkinColorIndex() {
        return (int) faceunity.fuItemGetParam(controllerItem, "skin_color_index");
    }

    public int fuItemGetParamLipColorIndex() {
        return (int) faceunity.fuItemGetParam(controllerItem, "lip_color_index");
    }

    //--------------------------------------道具（异步加载道具）----------------------------------------

    class FUItemHandler extends Handler {

        static final int HANDLE_CREATE_EFFECT = 0;
        static final int HANDLE_CREATE_AVATAR = 1;
        static final int HANDLE_CREATE_AVATAR_AR = 2;

        FUItemHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //加载普通道具
                case HANDLE_CREATE_EFFECT:
                    final int itemBundle = loadItem((String) msg.obj);
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mItemsArray[ITEM_ARRAYS_EFFECT] = itemBundle;
                        }
                    });
                    break;
                //切换avatar
                case HANDLE_CREATE_AVATAR: {
                    final AvatarP2A avatarP2A = (AvatarP2A) msg.obj;
                    int oldHeadItem = headItem;
                    int oldBodyItem = bodyItem;
                    int oldHairItem = hairItem;
                    int oldGlassItem = glassItem;
                    int oldBeardItem = beardItem;
                    int oldHatItem = hatItem;
                    int oldClothesItem = clothesItem;
                    int oldExpressionItem = expressionItem;

                    //加载新道具
                    headItem = avatarP2A.getHeadFile().equals(headFile) && msg.arg1 == 0 ? oldHeadItem : loadItem(headFile = avatarP2A.getHeadFile());
                    bodyItem = avatarP2A.getBodyFile().equals(bodyFile) ? oldBodyItem : loadItem(bodyFile = avatarP2A.getBodyFile());
                    hairItem = avatarP2A.getHairFile().equals(hairFile) ? oldHairItem : loadItem(hairFile = avatarP2A.getHairFile());
                    glassItem = avatarP2A.getGlassesFile().equals(glassFile) ? oldGlassItem : loadItem(glassFile = avatarP2A.getGlassesFile());
                    beardItem = avatarP2A.getBeardFile().equals(beardFile) ? oldBeardItem : loadItem(beardFile = avatarP2A.getBeardFile());
                    hatItem = avatarP2A.getHatFile().equals(hatFile) ? oldHatItem : loadItem(hatFile = avatarP2A.getHatFile());
                    clothesItem = avatarP2A.getClothesFile().equals(clothesFile) ? oldClothesItem : loadItem(clothesFile = avatarP2A.getClothesFile());
                    expressionItem = avatarP2A.getExpressionFile().equals(expressionFile) ? oldExpressionItem : loadItem(expressionFile = avatarP2A.getExpressionFile());

                    //解绑与绑定道具
                    avatarBindItem(oldHeadItem, headItem);
                    avatarBindItem(oldBodyItem, bodyItem);
                    avatarBindItem(oldHairItem, hairItem);
                    avatarBindItem(oldGlassItem, glassItem);
                    avatarBindItem(oldBeardItem, beardItem);
                    avatarBindItem(oldHatItem, hatItem);
                    avatarBindItem(oldClothesItem, clothesItem);
                    avatarBindItem(oldExpressionItem, expressionItem);

                    //销毁老道具
                    destroyItem(oldHeadItem, headItem);
                    destroyItem(oldBodyItem, bodyItem);
                    destroyItem(oldHairItem, hairItem);
                    destroyItem(oldGlassItem, glassItem);
                    destroyItem(oldBeardItem, beardItem);
                    destroyItem(oldHatItem, hatItem);
                    destroyItem(oldClothesItem, clothesItem);
                    destroyItem(oldExpressionItem, expressionItem);

                    //avatar 各类道具的颜色设置
                    if (avatarP2A.getSkinColorValue() >= 0) {
                        fuItemSetParam(PARAM_KEY_skin_color, ColorConstant.getColor(ColorConstant.skin_color, avatarP2A.getSkinColorValue()));
                    }
                    if (avatarP2A.getLipColorValue() >= 0) {
                        fuItemSetParam(PARAM_KEY_lip_color, ColorConstant.getColor(ColorConstant.lip_color, avatarP2A.getLipColorValue()));
                    }
                    fuItemSetParam(PARAM_KEY_iris_color, ColorConstant.getColor(ColorConstant.iris_color, avatarP2A.getIrisColorValue()));
                    fuItemSetParam(PARAM_KEY_hair_color, ColorConstant.getColor(ColorConstant.hair_color, avatarP2A.getHairColorValue()));
                    fuItemSetParam(PARAM_KEY_hair_color_intensity, ColorConstant.getColor(ColorConstant.hair_color, avatarP2A.getHairColorValue())[3]);
                    fuItemSetParam(PARAM_KEY_glass_color, ColorConstant.getColor(ColorConstant.glass_color, avatarP2A.getGlassesColorValue()));
                    fuItemSetParam(PARAM_KEY_glass_frame_color, ColorConstant.getColor(ColorConstant.glass_frame_color, avatarP2A.getGlassesFrameColorValue()));
                    fuItemSetParam(PARAM_KEY_beard_color, ColorConstant.getColor(ColorConstant.beard_color, avatarP2A.getBeardColorValue()));
                    fuItemSetParam(PARAM_KEY_hat_color, ColorConstant.getColor(ColorConstant.hat_color, avatarP2A.getHatColorValue()));
                    //avatar 加载完成回调
                    queueNextEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnLoadBodyListener != null)
                                mOnLoadBodyListener.onLoadBodyCompleteListener();
                        }
                    });
                }
                break;
                //切换ar avatar
                case HANDLE_CREATE_AVATAR_AR: {
                    final AvatarP2A avatarP2A = (AvatarP2A) msg.obj;
                    int oldHeadItem = headARItem;
                    int oldHairItem = hairARItem;
                    int oldGlassItem = glassARItem;
                    int oldBeardItem = beardARItem;
                    int oldHatItem = hatARItem;
                    headARItem = avatarP2A.getHeadFile().equals(headARFile) ? oldHeadItem : loadItem(headARFile = avatarP2A.getHeadFile());
                    hairARItem = avatarP2A.getHairFile().equals(hairARFile) ? oldHairItem : loadItem(hairARFile = avatarP2A.getHairFile());
                    glassARItem = avatarP2A.getGlassesFile().equals(glassARFile) ? oldGlassItem : loadItem(glassARFile = avatarP2A.getGlassesFile());
                    beardARItem = avatarP2A.getBeardFile().equals(beardARFile) ? oldBeardItem : loadItem(beardARFile = avatarP2A.getBeardFile());
                    hatARItem = avatarP2A.getHatFile().equals(hatARFile) ? oldHatItem : loadItem(hatARFile = avatarP2A.getHatFile());
                    avatarBindItem(oldHeadItem, headARItem);
                    avatarBindItem(oldHairItem, hairARItem);
                    avatarBindItem(oldGlassItem, glassARItem);
                    avatarBindItem(oldBeardItem, beardARItem);
                    avatarBindItem(oldHatItem, hatARItem);

                    if (oldHeadItem != headItem)
                        destroyItem(oldHeadItem, headARItem);
                    if (oldHairItem != hairItem)
                        destroyItem(oldHairItem, hairARItem);
                    if (oldGlassItem != glassItem)
                        destroyItem(oldGlassItem, glassARItem);
                    if (oldBeardItem != beardItem)
                        destroyItem(oldBeardItem, beardARItem);
                    if (oldHatItem != hatItem)
                        destroyItem(oldHatItem, hatARItem);

                    if (avatarP2A.getSkinColorValue() >= 0) {
                        fuItemSetParam(PARAM_KEY_skin_color, ColorConstant.getColor(ColorConstant.skin_color, avatarP2A.getSkinColorValue()));
                    }
                    if (avatarP2A.getLipColorValue() >= 0) {
                        fuItemSetParam(PARAM_KEY_lip_color, ColorConstant.getColor(ColorConstant.lip_color, avatarP2A.getLipColorValue()));
                    }
                    fuItemSetParam(PARAM_KEY_iris_color, ColorConstant.getColor(ColorConstant.iris_color, avatarP2A.getIrisColorValue()));
                    fuItemSetParam(PARAM_KEY_hair_color, ColorConstant.getColor(ColorConstant.hair_color, avatarP2A.getHairColorValue()));
                    fuItemSetParam(PARAM_KEY_hair_color_intensity, ColorConstant.getColor(ColorConstant.hair_color, avatarP2A.getHairColorValue())[3]);
                    fuItemSetParam(PARAM_KEY_glass_color, ColorConstant.getColor(ColorConstant.glass_color, avatarP2A.getGlassesColorValue()));
                    fuItemSetParam(PARAM_KEY_glass_frame_color, ColorConstant.getColor(ColorConstant.glass_frame_color, avatarP2A.getGlassesFrameColorValue()));
                    fuItemSetParam(PARAM_KEY_beard_color, ColorConstant.getColor(ColorConstant.beard_color, avatarP2A.getBeardColorValue()));
                    fuItemSetParam(PARAM_KEY_hat_color, ColorConstant.getColor(ColorConstant.hat_color, avatarP2A.getHatColorValue()));
                }
                break;
            }
        }
    }

    /**
     * 绑定与解绑道具：
     * -  该接口可以将avatar道具绑定到controller句柄上，并解绑老的avatar道具
     *
     * @param oldItem 需要解绑的道具句柄
     * @param newItem 需要绑定的道具句柄
     */
    private void avatarBindItem(final int oldItem, final int newItem) {
        if (oldItem == newItem) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "avatarBindItem controlItem " + controllerItem + " oldItem " + oldItem + " newItem " + newItem);
                if (oldItem > 0)
                    faceunity.fuUnBindItems(controllerItem, new int[]{oldItem});
                if (newItem > 0)
                    faceunity.fuBindItems(controllerItem, new int[]{newItem});
            }
        });
    }

    /**
     * 销毁单个道具：
     * - 通过道具句柄销毁道具，并释放相关资源
     *
     * @param oldItem 需要销毁的道具句柄
     * @param newItem 用于判断oldItem是否重新被使用而放弃销毁
     */
    private void destroyItem(final int oldItem, int newItem) {
        if (oldItem == newItem || oldItem <= 0) return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "destroyItem oldItem " + oldItem);
                faceunity.fuDestroyItem(oldItem);
            }
        });
    }

    /**
     * 通过道具文件路径创建道具：
     *
     * @param bundle 道具文件路径
     * @return 创建的道具句柄
     */
    private int loadItem(String bundle) {
        int item = 0;
        try {
            if (TextUtils.isEmpty(bundle)) {
                item = 0;
            } else {
                InputStream is = bundle.startsWith(Constant.filePath) ? new FileInputStream(new File(bundle)) : mContext.getAssets().open(bundle);
                byte[] itemData = new byte[is.available()];
                is.read(itemData);
                is.close();
                item = faceunity.fuCreateItemFromPackage(itemData);
            }
            Log.i(TAG, "loadItem " + bundle + " item " + item);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 加载avatar
     *
     * @param avatar 需要加载的avatar
     */
    public void loadAvatar(@NonNull AvatarP2A avatar) {
        loadAvatar(avatar, false);
    }

    /**
     * 加载avatar
     *
     * @param avatar           需要加载的avatar
     * @param isNeedChangeHead 是否需要强制重新加载head bundle
     */
    public void loadAvatar(@NonNull AvatarP2A avatar, boolean isNeedChangeHead) {
        if (mFuItemHandler == null) return;
        Log.i(TAG, "loadAvatar " + avatar.toString());
        mFuItemHandler.removeMessages(FUItemHandler.HANDLE_CREATE_AVATAR);
        Message message = Message.obtain(mFuItemHandler, FUItemHandler.HANDLE_CREATE_AVATAR, isNeedChangeHead ? 1 : 0, 0, avatar.clone());
        mFuItemHandler.sendMessage(message);
    }

    /**
     * 加载ar滤镜的avatar
     *
     * @param avatar 需要加载ar滤镜的avatar
     */
    public void loadARAvatar(@NonNull AvatarP2A avatar) {
        if (mFuItemHandler == null) return;
        Log.i(TAG, "loadARAvatar " + avatar.toString());
        mFuItemHandler.removeMessages(FUItemHandler.HANDLE_CREATE_AVATAR_AR);
        Message message = Message.obtain(mFuItemHandler, FUItemHandler.HANDLE_CREATE_AVATAR_AR, avatar.clone());
        mFuItemHandler.sendMessage(message);
    }

    /**
     * 加载普通道具
     *
     * @param effect 普通道具路径
     */
    public void loadEffect(String effect) {
        Log.i(TAG, "loadEffect " + effect);
        mFuItemHandler.removeMessages(FUItemHandler.HANDLE_CREATE_EFFECT);
        mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, FUItemHandler.HANDLE_CREATE_EFFECT, effect));
    }

    //--------------------------------------IsTracking（人脸识别回调相关定义）----------------------------------------

    private int mTrackingStatus = 0;

    public interface OnTrackingStatusChangedListener {
        void onTrackingStatusChanged(int status);
    }

    private OnTrackingStatusChangedListener mOnTrackingStatusChangedListener;

    public void setOnTrackingStatusChangedListener(OnTrackingStatusChangedListener mOnTrackingStatusChangedListener) {
        this.mOnTrackingStatusChangedListener = mOnTrackingStatusChangedListener;
    }
//--------------------------------------FaceUnitySystemError（faceunity错误信息回调相关定义）----------------------------------------

    public interface OnSystemErrorListener {
        void onSystemError(String error);
    }

    private OnSystemErrorListener mOnSystemErrorListener;

    public void setOnSystemErrorListener(OnSystemErrorListener mOnSystemErrorListener) {
        this.mOnSystemErrorListener = mOnSystemErrorListener;
    }
//--------------------------------------mIsCalibrating（表情校准回调相关定义）----------------------------------------

    private float mIsCalibrating = 0;

    public interface OnCalibratingListener {
        void OnCalibrating(float isCalibrating);
    }

    private OnCalibratingListener mOnCalibratingListener;

    public void setOnCalibratingListener(OnCalibratingListener mOnCalibratingListener) {
        this.mOnCalibratingListener = mOnCalibratingListener;
    }
//--------------------------------------加载完成身体Bundle的回调----------------------------------------

    public interface OnLoadBodyListener {
        void onLoadBodyCompleteListener();
    }

    private OnLoadBodyListener mOnLoadBodyListener;

    public void setOnLoadBodyListener(OnLoadBodyListener mOnLoadBodyListener) {
        this.mOnLoadBodyListener = mOnLoadBodyListener;
    }
//--------------------------------------FPS（FPS相关定义）----------------------------------------

    private static final float NANO_IN_ONE_MILLI_SECOND = 1000000.0f;
    private static final float TIME = 5f;
    private int mCurrentFrameCnt = 0;
    private long mLastOneHundredFrameTimeStamp = 0;
    private long mOneHundredFrameFUTime = 0;
    private boolean mNeedBenchmark = true;
    private long mFuCallStartTime = 0;

    private OnFUDebugListener mOnFUDebugListener;

    public interface OnFUDebugListener {
        void onFpsChange(double fps, double renderTime);
    }

    public void setOnFUDebugListener(OnFUDebugListener mOnFUDebugListener) {
        this.mOnFUDebugListener = mOnFUDebugListener;
    }

    private void benchmarkFPS() {
        if (!mNeedBenchmark) return;
        if (++mCurrentFrameCnt == TIME) {
            mCurrentFrameCnt = 0;
            long tmp = System.nanoTime();
            double fps = (1000.0f * NANO_IN_ONE_MILLI_SECOND / ((tmp - mLastOneHundredFrameTimeStamp) / TIME));
            mLastOneHundredFrameTimeStamp = tmp;
            double renderTime = mOneHundredFrameFUTime / TIME / NANO_IN_ONE_MILLI_SECOND;
            mOneHundredFrameFUTime = 0;

            if (mOnFUDebugListener != null) {
                mOnFUDebugListener.onFpsChange(fps, renderTime);
            }
        }
    }
}
