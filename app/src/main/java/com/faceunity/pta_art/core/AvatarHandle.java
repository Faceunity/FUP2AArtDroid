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
    private static double mLastHairFollowingState = 0.0;

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
    public final FUItem decorationsItem = new FUItem();

    public final FUItem eyelinerItem = new FUItem();
    public final FUItem eyeshadowItem = new FUItem();
    public final FUItem facemakeupItem = new FUItem();
    public final FUItem lipglossItem = new FUItem();
    public final FUItem pupilItem = new FUItem();

    public final FUItem expressionItem = new FUItem();
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
                openHairFollowing(1.0);
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
        setAvatar(avatar, false, null);
    }

    public void setAvatar(AvatarPTA avatar, Runnable completeListener) {
        setAvatar(avatar, false, completeListener);
    }

    public void setAvatar(final AvatarPTA avatar, final boolean mustLoadHead, final Runnable completeListener) {
        mFUItemHandler.removeMessages(FUItemHandler_what);
        Message msg = Message.obtain(mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                loadItem(headItem, avatar.getHeadFile(), mustLoadHead);
                loadItem(hairItem, avatar.getHairFile());
                loadItem(glassItem, avatar.getGlassesFile());
                loadItem(beardItem, avatar.getBeardFile());
                loadItem(eyebrowItem, avatar.getEyebrowFile());
                loadItem(eyelashItem, avatar.getEyelashFile());
                loadItem(hatItem, avatar.getHatFile());
                loadItem(bodyItem, FilePathFactory.bodyBundle(avatar.getGender(), avatar.getBodyLevel()));
                loadItem(hatItem, avatar.getClothesFile());

                loadItem(clothesUpperItem, avatar.getClothesUpperFile());
                loadItem(clothesLowerItem, avatar.getClothesLowerFile());
                loadItem(shoeItem, avatar.getShoeFile());
                loadItem(decorationsItem, avatar.getDecorationsFile());

                loadItem(eyelinerItem, avatar.getEyelinerFile());
                loadItem(eyeshadowItem, avatar.getEyeshadowFile());
                loadItem(facemakeupItem, avatar.getFacemakeupFile());
                loadItem(lipglossItem, avatar.getLipglossFile());
                loadItem(pupilItem, avatar.getPupilFile());

                loadItem(expressionItem,
                        TextUtils.isEmpty(avatar.getExpressionFile()) ?
                                isPose ? FilePathFactory.bundlePose(avatar.getGender()) :
                                        mIsNeedTrack || mIsNeedFacePUP ? FilePathFactory.bundleIdle(avatar.getGender()) : FilePathFactory.bundleAnim(avatar.getGender())
                                : avatar.getExpressionFile());
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
                if (completeListener != null)
                    mBaseCore.queueNextEvent(completeListener);
            }
        });
        msg.what = FUItemHandler_what;
        mFUItemHandler.sendMessage(msg);
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
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle,
                            eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle, clothesItem.handle,
                            clothesUpperItem.handle, clothesLowerItem.handle, shoeItem.handle, decorationsItem.handle,
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
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle,
                            eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle, clothesItem.handle,
                            clothesUpperItem.handle, clothesLowerItem.handle, shoeItem.handle, decorationsItem.handle,
                            eyelinerItem.handle, eyeshadowItem.handle, facemakeupItem.handle, lipglossItem.handle,
                            pupilItem.handle,
                            expressionItem.handle, otherItem[0] == null ? 0 : otherItem[0].handle, otherItem[1] == null ? 0 : otherItem[1].handle, otherItem[2] == null ? 0 : otherItem[2].handle, otherItem[3] == null ? 0 : otherItem[3].handle, otherItem[4] == null ? 0 : otherItem[4].handle};
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
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsItem.handle));

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
                decorationsItem.clear();
                expressionItem.clear();

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
     * //NAMA中使用右手坐标系，X轴水平向右，Y轴竖直向上，Z轴垂直屏幕向外
     * x范围[-100,100]
     * y范围[-300,400]
     * z范围[-1000,200]
     */
    public void resetAvatar() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 11.76, -183.89});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }

    public void resetAll() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 11.76, -183.89});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }

    public void resetAllFront() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, -10.85, -11.12});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 3);
            }
        });
    }

    public void resetAllSide() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, -10.85, -11.12});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0.125);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 3);
            }
        });
    }

    public void resetAllMin() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 53.14, -476.6});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }

    public void resetAllMinTop() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 93.01, -683.57});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }

    public void resetAllMinBottom() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 103.13, -1000});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }

    public void resetAllMinGroup() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 65, 350});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 1);
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

    //--------------------------------------动画----------------------------------------

    /**
     * 相机动画控制
     *
     * @param state
     */
    public void setCameraAnim(int state) {
        switch (state) {
            case 1:
                //启用/暂停当前动画
                //1为开启，0为关闭
                faceunity.fuItemSetParam(controllerItem, "enable_camera_animation", 1);
                //循环动画
                //1为循环，0为不循环
                faceunity.fuItemSetParam(controllerItem, "camera_animation_loop", 1);
                break;
            case 2:
                faceunity.fuItemSetParam(controllerItem, "enable_camera_animation", 0);
                faceunity.fuItemSetParam(controllerItem, "camera_animation_loop", 0);
                break;
        }
    }

    //从头播放句柄为anim_id的动画（循环）
    public void seekToAnimBegin(final int anim_id) {
        faceunity.fuItemSetParam(controllerItem, "play_animation", anim_id);
    }

    /**
     * @param state   1：播放 2：暂停 3：停止
     * @param role_id 角色id
     */
    public void setAnimState(final int state, int role_id) {
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
     * @param openStatus 1表示开启
     *                   0表示关闭
     */
    public void openHairFollowing(double openStatus) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mLastHairFollowingState == openStatus) {
                    return;
                }
                mLastHairFollowingState = openStatus;
                faceunity.fuItemSetParam(controllerItem, "modelmat_to_bone", openStatus);
            }
        });

    }
}
