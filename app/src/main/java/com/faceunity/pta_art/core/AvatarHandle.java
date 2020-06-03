package com.faceunity.pta_art.core;

import android.graphics.Point;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.pta_art.core.base.BasePTAHandle;
import com.faceunity.pta_art.core.base.FUItem;
import com.faceunity.pta_art.core.base.FUItemHandler;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.wrapper.faceunity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * Avatar Controller
 * Created by tujh on 2018/12/17.
 */
public class AvatarHandle extends BasePTAHandle {
    private static final String TAG = AvatarHandle.class.getSimpleName();

    private boolean mIsNeedTrack;
    private boolean mIsNeedFacePUP;
    private boolean isPose;//是否是静止动画
    private boolean mIsNeedIdle;// 是否需要idle动画
    // 当前是否为合影界面
    private boolean isGroupPhoto = false;
    // 设置Avatar以后是否需要在下一帧再进行回调
    private boolean needNextEventCallback = true;

    public final FUItem headItem = new FUItem();
    public final FUItem hairItem = new FUItem();
    public final FUItem glassItem = new FUItem();
    public final FUItem beardItem = new FUItem();
    public final FUItem eyebrowItem = new FUItem();
    public final FUItem eyelashItem = new FUItem();
    public final FUItem hatItem = new FUItem();
    public final FUItem bodyItem = new FUItem();
    public final FUItem clothesItem = new FUItem();
    public final FUItem clothesUpperItem = new FUItem();
    public final FUItem clothesLowerItem = new FUItem();
    public final FUItem shoeItem = new FUItem();
    public final FUItem decorationsEarItem = new FUItem();
    public final FUItem decorationsFootItem = new FUItem();
    public final FUItem decorationsHandItem = new FUItem();
    public final FUItem decorationsHeadItem = new FUItem();
    public final FUItem decorationsNeckItem = new FUItem();

    public final FUItem eyelinerItem = new FUItem();
    public final FUItem eyeshadowItem = new FUItem();
    public final FUItem facemakeupItem = new FUItem();
    public final FUItem lipglossItem = new FUItem();
    public final FUItem pupilItem = new FUItem();

    public final FUItem expressionItem = new FUItem();
    public final FUItem backgroundItem = new FUItem();
    public final FUItem otherItem[] = new FUItem[5];


    {
        for (int i = 0; i < otherItem.length; i++) {
            otherItem[i] = new FUItem();
        }
    }

    public AvatarHandle(BaseCore baseCore, FUItemHandler FUItemHandler, final Runnable prepare) {
        super(baseCore, FUItemHandler);
        isPose = false;
        mFUItemHandler.loadFUItem(FUItemHandler_what_controller, new com.faceunity.pta_art.core.base.FUItemHandler.LoadFUItemListener(FilePathFactory.bundleController()) {

            @Override
            public void onLoadComplete(FUItem fuItem) {
                openHairFollowing(true);
                controllerItem = fuItem.handle;
                if (prepare != null)
                    prepare.run();
            }
        });
    }

    public AvatarHandle(BaseCore baseCore, FUItemHandler FUItemHandler, int controller) {
        super(baseCore, FUItemHandler);
        isPose = false;
        controllerItem = controller;
    }

    public void setAvatar(AvatarPTA avatar) {
        setAvatar(avatar, false, false, null);
    }

    public void setAvatar(AvatarPTA avatar, Runnable completeListener) {
        setAvatar(avatar, false, false, completeListener);
    }

    public void setAvatar(AvatarPTA avatar, boolean mustLoadHead, Runnable completeListener) {
        setAvatar(avatar, mustLoadHead, false, completeListener);
    }

    public void setAvatar(final AvatarPTA avatar, final boolean mustLoadHead, final boolean mistLoadHair,
                          final Runnable completeListener) {
        mFUItemHandler.removeMessages(FUItemHandler_what);
        Message msg = Message.obtain(mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                loadItem(headItem, avatar.getHeadFile(), mustLoadHead);
                // 当前的帽子都是帽子头发道具，所以就不需要原先的头发道具了
                if (TextUtils.isEmpty(avatar.getHatFile())) {
                    loadItem(hairItem, avatar.getHairFile(), mistLoadHair);
                    loadItem(hatItem, avatar.getHatFile());

                } else {
                    loadItem(hairItem, "");
                    loadItem(hatItem, avatar.getHatFile());
                }
                loadItem(glassItem, avatar.getGlassesFile());
                loadItem(beardItem, avatar.getBeardFile());
                loadItem(eyebrowItem, avatar.getEyebrowFile());
                loadItem(eyelashItem, avatar.getEyelashFile());

                loadItem(bodyItem, FilePathFactory.bodyBundle(avatar.getClothesGender(), avatar.getBodyLevel()));
                loadItem(clothesItem, avatar.getClothesFile());

                loadItem(clothesUpperItem, avatar.getClothesUpperFile());
                loadItem(clothesLowerItem, avatar.getClothesLowerFile());
                loadItem(shoeItem, avatar.getShoeFile());
                loadItem(decorationsEarItem, avatar.getEarDecorationsFile());
                loadItem(decorationsFootItem, avatar.getFootDecorationsFile());
                loadItem(decorationsHandItem, avatar.getHandDecorationsFile());
                loadItem(decorationsHeadItem, avatar.getHeadDecorationsFile());
                loadItem(decorationsNeckItem, avatar.getNeckDecorationsFile());

                loadItem(eyelinerItem, avatar.getEyelinerFile());
                loadItem(eyeshadowItem, avatar.getEyeshadowFile());
                loadItem(facemakeupItem, avatar.getFacemakeupFile());
                loadItem(lipglossItem, avatar.getLipglossFile());
                loadItem(pupilItem, avatar.getPupilFile());

                loadItem(expressionItem, loadExpressionBundle(avatar));
                loadItem(backgroundItem, isGroupPhoto ? "" : avatar.getBackgroundFile());
                String[] others = avatar.getOtherFile();
                for (int i = 0; i < otherItem.length; i++) {
                    if (others != null && i < others.length) {
                        loadItem(otherItem[i], others[i]);
                    } else if (otherItem[i] != null) {
                        final int finalI = i;
                        mBaseCore.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                faceunity.fuUnBindItems(controllerItem, new int[]{otherItem[finalI].handle});
                                mBaseCore.destroyItem(otherItem[finalI].handle).run();
                                otherItem[finalI].clear();
                            }
                        });
                    }
                }
                commitItem(avatar);
                if (completeListener != null) {
                    if (needNextEventCallback) {
                        mBaseCore.queueNextEvent(completeListener);
                    } else {
                        mBaseCore.queueEvent(completeListener);
                    }
                }
            }
        });
        msg.what = FUItemHandler_what;
        mFUItemHandler.sendMessage(msg);
    }

    private String loadExpressionBundle(AvatarPTA avatar) {
        String bundlePath = null;
        if (TextUtils.isEmpty(avatar.getExpressionFile())) {
            if (isPose) {
                bundlePath = FilePathFactory.bundlePose(avatar.getGender());
            } else if (mIsNeedTrack || mIsNeedFacePUP || mIsNeedIdle) {
                bundlePath = FilePathFactory.bundleIdle(avatar.getGender());
            } else {
                bundlePath = FilePathFactory.bundleAnim(avatar.getGender());
            }
        } else {
            bundlePath = avatar.getExpressionFile();
        }
        return bundlePath;
    }

    /**
     * 设置当前controller控制的人物id（默认：0）
     *
     * @param id
     */
    public void setCurrentInstancceId(int id) {
        if (controllerItem > 0) {
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem,
                                             "current_instance_id", id);
                    faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0, 0, 0});//必须重新设置初始值
                    faceunity.fuItemSetParam(controllerItem, "reset_all", 1.0f);//必须设置后生效
                }
            });
        }
    }

    @Override
    protected void bindAll() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{
                            backgroundItem.handle,
                            headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle,
                            eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle, clothesItem.handle,
                            clothesUpperItem.handle, clothesLowerItem.handle, shoeItem.handle, decorationsEarItem.handle,
                            decorationsFootItem.handle, decorationsHandItem.handle, decorationsHeadItem.handle, decorationsNeckItem.handle,
                            eyelinerItem.handle, eyeshadowItem.handle, facemakeupItem.handle, lipglossItem.handle, pupilItem.handle,
                            expressionItem.handle, otherItem[0] == null ? 0 : otherItem[0].handle, otherItem[1] == null ? 0 : otherItem[1].handle, otherItem[2] == null ? 0 : otherItem[2].handle, otherItem[3] == null ? 0 : otherItem[3].handle, otherItem[4] == null ? 0 : otherItem[4].handle};
                    Log.i(TAG, "bundle avatarBindItem controlItem " + controllerItem + " bindAll " + Arrays.toString(items));
                    faceunity.fuBindItems(controllerItem, items);
                    setAvatarColor();
                }
            });
    }

    @Override
    protected void unBindAll() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{backgroundItem.handle, headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle,
                            eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle, clothesItem.handle,
                            clothesUpperItem.handle, clothesLowerItem.handle, shoeItem.handle, decorationsEarItem.handle,
                            decorationsFootItem.handle, decorationsHandItem.handle, decorationsHeadItem.handle, decorationsNeckItem.handle,
                            eyelinerItem.handle, eyeshadowItem.handle, facemakeupItem.handle, lipglossItem.handle,
                            pupilItem.handle, expressionItem.handle,
                            otherItem[0] == null ? 0 : otherItem[0].handle,
                            otherItem[1] == null ? 0 : otherItem[1].handle,
                            otherItem[2] == null ? 0 : otherItem[2].handle,
                            otherItem[3] == null ? 0 : otherItem[3].handle,
                            otherItem[4] == null ? 0 : otherItem[4].handle};
                    Log.i(TAG, "bundle avatarBindItem controlItem " + controllerItem + " unBindAll " + Arrays.toString(items));
                    faceunity.fuUnBindItems(controllerItem, items);
                }
            });
    }

    @Override
    public void release() {
        unBindAll();
        releaseAll(true);
    }

    public void setMakeupHandleId() {
        eyebrowHandleId = eyebrowItem.handle;
        eyeshadowHandleId = eyeshadowItem.handle;
        lipglossHandleId = lipglossItem.handle;
        eyelashHandleId = eyelashItem.handle;
    }

    /*
     * controllerItem不释放
     */
    public void releaseNoController() {
        unBindAll();
        releaseAll(false);
    }

    public void releaseAll(boolean isControllerRelease) {
        mBaseCore.queueEvent(mBaseCore.destroyItem(headItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(hairItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(glassItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(beardItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyebrowItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyelashItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(hatItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(bodyItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesLowerItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesUpperItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(shoeItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(expressionItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsEarItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsFootItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsHandItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsHeadItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsNeckItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(backgroundItem.handle));

        mBaseCore.queueEvent(mBaseCore.destroyItem(eyelinerItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyeshadowItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(facemakeupItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(lipglossItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(pupilItem.handle));

        for (FUItem item : otherItem) {
            mBaseCore.queueEvent(mBaseCore.destroyItem(item.handle));
        }
        if (isControllerRelease) {
            mBaseCore.queueEvent(mBaseCore.destroyItem(controllerItem));
        }
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                headItem.clear();
                hairItem.clear();
                glassItem.clear();
                beardItem.clear();
                eyebrowItem.clear();
                eyelashItem.clear();
                hatItem.clear();
                bodyItem.clear();
                clothesItem.clear();
                clothesUpperItem.clear();
                clothesLowerItem.clear();
                shoeItem.clear();
                decorationsEarItem.clear();
                decorationsFootItem.clear();
                decorationsHandItem.clear();
                decorationsHeadItem.clear();
                decorationsNeckItem.clear();
                expressionItem.clear();
                backgroundItem.clear();

                eyelinerItem.clear();
                eyeshadowItem.clear();
                facemakeupItem.clear();
                lipglossItem.clear();
                pupilItem.clear();
                for (FUItem item : otherItem) {
                    item.clear();
                }
                if (isControllerRelease) {
                    controllerItem = 0;
                }
                if (isControllerRelease) {
                    closeLight();
                }
            }
        });
    }

    /**
     * avatar水平方向旋转角度
     *
     * @param rotDelta 水平方向旋转角度增量
     */
    public void setRotDelta(final float rotDelta) {
        mBaseCore.queueEvent(new Runnable() {
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
        mBaseCore.queueEvent(new Runnable() {
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
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "scale_delta", scaleDelta);
            }
        });
    }

    /**
     * avatar缩放比例
     *
     * @param scaleDelta avatar缩放比例增量
     */
    public void setScaleDelta(final float scaleDelta, double maxScale, double minScale) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                double[] lastScale = getCurrent_position();
                if ((lastScale[2] >= maxScale && scaleDelta > 0) || (lastScale[2] <= minScale && scaleDelta < 0)) {
                    return;
                }
                if (scaleDelta > 0 && lastScale[2] + scaleDelta > maxScale) {
                    faceunity.fuItemSetParam(controllerItem, "scale_delta", maxScale - lastScale[2]);
                } else if (scaleDelta < 0 && lastScale[2] + scaleDelta < minScale) {
                    faceunity.fuItemSetParam(controllerItem, "scale_delta", minScale - lastScale[2]);
                } else {
                    faceunity.fuItemSetParam(controllerItem, "scale_delta", scaleDelta);
                }
            }
        });
    }

    /**
     * 设置缩放
     *
     * @param xyz
     */
    public void setScale(double[] xyz) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{xyz[0], xyz[1], xyz[2]});
                faceunity.fuItemSetParam(controllerItem, "reset_all", 1);
            }
        });
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public double[] getCurrent_position() {
        if (controllerItem > 0) {
            return faceunity.fuItemGetParamdv(controllerItem, "current_position");
        } else {
            return new double[]{0, 0, 0};
        }
    }

    /**
     * 该方法只做对模型的旋转
     */
    public void resetAllFront() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 3);
            }
        });
    }

    /**
     * 该方法只做对模型的旋转
     */
    public void resetAllSide() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0.125);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 3);
            }
        });
    }


    public void setNeedTrackFace(boolean needTrackFace) {
        mIsNeedTrack = needTrackFace;
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, mIsNeedTrack ? "enter_track_rotation_mode" : "quit_track_rotation_mode", 1);
            }
        });
    }

    /**
     * CNN 面部追踪
     *
     * @param needTrackFace
     */
    public void setCNNTrackFace(boolean needTrackFace) {
        mIsNeedTrack = needTrackFace;
        setFaceCapture(needTrackFace);
    }

    public void setFaceCapture(boolean isOpen) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                //3.设置close_face_capture，说明启用或者关闭CNN面部追踪，value = 0.0表示开启，value = 1.0表示关闭
                faceunity.fuItemSetParam(controllerItem, "close_face_capture", isOpen ? 0.0 : 1.0);
            }
        });
    }

    public void setPose(boolean isPose) {
        this.isPose = isPose;
    }

    public void setGroupPhoto(boolean groupPhoto) {
        isGroupPhoto = groupPhoto;
    }

    public void setNeedNextEventCallback(boolean needNextEventCallback) {
        this.needNextEventCallback = needNextEventCallback;
    }

    //--------------------------------------动画----------------------------------------

    /**
     * 相机动画控制
     * <p>
     * start_camera_animation 方法表示开启相机动画
     * pause_camera_animation 方法表示暂停相机动画
     * stop_camera_animation 方法表示停止相机动画并且回到第一帧
     * <p>
     * camera_animation_loop 方法表示相机动画是否需要循环  1表示循环  0 表示不需要循环，也就是播放一遍
     *
     * @param state
     */
    public void setCameraAnim(int state) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case 1:
                        // 启动当前Camera动画
                        faceunity.fuItemSetParam(controllerItem, "play_camera_animation", 1);
                        break;
                    case 2:
                        // 停止当前Camera动画
                        faceunity.fuItemSetParam(controllerItem, "stop_camera_animation", 1);
                        break;
                    case 3:
                        // 暂停当前Camera动画
                        faceunity.fuItemSetParam(controllerItem, "pause_camera_animation", 1);
                        break;
                    case 4:
                        //重置相机动画，参数无意义，效果相当于先调用stop_camera_animation再调用start_camera_animation
                        faceunity.fuItemSetParam(controllerItem, "reset_camera_animation", 1);
                        break;
                }
            }
        });
    }

    /**
     * 从头播放句柄为cameraId的相机动画（不循环）
     *
     * @param cameraId
     */
    public void setCameraAnimPlayOnce(int cameraId) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "play_camera_animation_once", cameraId);
            }
        });
    }

    //从头播放句柄为anim_id的动画（循环）
    public void seekToAnimBegin(final int anim_id) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "play_animation", anim_id);
            }
        });
    }

    /**
     * @param state   1：播放 2：暂停 3：停止
     * @param role_id 角色id
     */
    public void setAnimState(final int state, int role_id) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem,
                                         "current_instance_id", role_id);
                switch (state) {
                    case 1:
                        faceunity.fuItemSetParam(controllerItem, "start_animation", role_id);//当前帧开始播放
                        break;
                    case 2:
                        faceunity.fuItemSetParam(controllerItem, "pause_animation", role_id);
                        break;
                    case 3:
                        faceunity.fuItemSetParam(controllerItem, "stop_animation", role_id);//删除所有帧
                        break;
                }
            }
        });
    }

    /**
     * 获取某个动画的播放进度
     * 进度0-0.9999为第一次循环，1-1.9999为第二次循环，以此类推
     * 即使play_animation_once,进度也会突破1.0，照常运行
     *
     * @param anim_id 当前动画的句柄
     * @return
     */
    public float getAnimateProgress(final int anim_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "get_animation_progress");
            jsonObject.put("anim_id", anim_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        float progress = (float) faceunity.fuItemGetParam(controllerItem, jsonObject.toString());
        return progress;
    }
    //--------------------------------------捏脸----------------------------------------

    public void setNeedFacePUP(boolean needFacePUP) {
        mIsNeedFacePUP = needFacePUP;
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, mIsNeedFacePUP ? "enter_facepup_mode" : "quit_facepup_mode", 1);
            }
        });
    }

    public void fuItemSetParamFaceShape(final String key, final double values) {
        if (values < 0 || values > 1) {
            Log.e(TAG, "fuItemSetParamFaceShape error key " + key + " values " + values);
            return;
        }
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "{\"name\":\"facepup\",\"param\":\"" + key + "\"}", values);
            }
        });
    }

    /**
     * 隐藏脖子
     */
    public void hide_neck() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "hide_neck", 1.0);
            }
        });
    }

    public float fuItemGetParamShape(final String key) {
        return (float) faceunity.fuItemGetParam(controllerItem, "{\"name\":\"facepup\",\"param\":\"" + key + "\"}");
    }

    private float[] expressions;

    public float[] fuItemGetParamFaceShape() {
        expressions = null;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                expressions = faceunity.fuItemGetParamfv(controllerItem, "facepup_expression");
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return expressions;
    }

    public Point getPointByIndex(int index) {
        faceunity.fuItemSetParam(controllerItem, "query_vert", index);
        int x = (int) faceunity.fuItemGetParam(controllerItem, "query_vert_x");
        int y = (int) faceunity.fuItemGetParam(controllerItem, "query_vert_y");
        return new Point(x, y);
    }

    /**
     * 是否开启头发跟随
     *
     * @param isOpen true 开启
     *               false 关闭
     */
    public void openHairFollowing(boolean isOpen) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                /**
                 * 1 为开启，0 为关闭，开启的时候移动角色的值会被设进骨骼系统，这时候带DynamicBone的模型会有相关效果
                 * 如果添加了没有骨骼的模型，请关闭这个值，否则无法移动模型
                 * 默认开启
                 * 每个角色的这个值都是独立的
                 */
                faceunity.fuItemSetParam(controllerItem, "modelmat_to_bone", isOpen ? 1.0 : 0.0);
            }
        });

    }

    /**
     * 关闭加载的头发物理动效
     */
    public void setDynamicBone(boolean isOpen) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                //1为开启，0为关闭，开启的时候已加载的物理会生效，同时加载新的带物理的bundle也会生效，
                // 关闭的时候已加载的物理会停止生效，但不会清除缓存（这时候再次开启物理会在此生效），
                // 这时加载带物理的bundle不会生效，且不会产生缓存，即关闭后加载的带物理的bundle，
                // 即时再次开启，物理也不会生效，需要重新加载
                faceunity.fuItemSetParam(controllerItem, "enable_dynamicbone", isOpen ? 1.0 : 0.0);
            }
        });
    }


    /**
     * 设置模型动画
     *
     * @param mShowAvatarP2A
     * @param bundleRes
     */
    public void setExpression(AvatarPTA mShowAvatarP2A, BundleRes bundleRes, int loadCount) {
        setExpression(mShowAvatarP2A, bundleRes, true, loadCount);
    }

    /**
     * 设置模型动画
     *
     * @param mShowAvatarP2A
     * @param bundleRes
     */
    public void setExpression(AvatarPTA mShowAvatarP2A, BundleRes bundleRes, boolean needResetAvatar, int loadCount) {
        mShowAvatarP2A.setExpression(bundleRes);
        setCurrentAniLoadCount(loadCount);
        needResetAvatar(mShowAvatarP2A, needResetAvatar);
    }


    /**
     * 取消模型动画
     *
     * @param mShowAvatarP2A
     */
    public void clearExpression(AvatarPTA mShowAvatarP2A, boolean needResetAvatar) {
        if (!TextUtils.isEmpty(mShowAvatarP2A.getExpressionFile())) {
            setCurrentAniLoadCount(Integer.MAX_VALUE);
            mShowAvatarP2A.setExpression(new BundleRes(""));
            needResetAvatar(mShowAvatarP2A, needResetAvatar);
        }
    }

    private void needResetAvatar(AvatarPTA mShowAvatarP2A, boolean needResetAvatar) {
        if (needResetAvatar) {
            setAvatar(mShowAvatarP2A);
        }
    }

    public void setCurrentAniLoadCount(int loadCount) {
        this.loadCount = loadCount;
    }

    public int getLoadCount() {
        return loadCount;
    }

    private int loadCount = Integer.MAX_VALUE;

    /**
     * 光照
     */
    private int lightItem;

    /**
     * 开启光照
     */
    public void openLight(String lightPath) {
        if (controllerItem > 0) {
            closeLight();
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    lightItem = mFUItemHandler.loadFUItem(lightPath);
                    if (lightItem > 0) {
                        faceunity.fuBindItems(controllerItem, new int[]{lightItem});
                    }
                }
            });
        }
    }

    /**
     * 关闭光照
     */
    public void closeLight() {
        if (controllerItem > 0) {
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (lightItem > 0) {
                        faceunity.fuUnBindItems(controllerItem, new int[]{lightItem});
                        faceunity.fuDestroyItem(lightItem);
                        lightItem = 0;
                    }
                }
            });
        }
    }

    public void setmIsNeedIdle(boolean mIsNeedIdle) {
        this.mIsNeedIdle = mIsNeedIdle;
    }
}
