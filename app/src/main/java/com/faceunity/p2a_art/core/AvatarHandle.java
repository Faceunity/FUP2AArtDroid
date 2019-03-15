package com.faceunity.p2a_art.core;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.p2a_art.core.base.BaseCore;
import com.faceunity.p2a_art.core.base.FUItem;
import com.faceunity.p2a_art.core.base.FUItemHandler;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditFacePoint;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

/**
 * Created by tujh on 2018/12/17.
 */
public class AvatarHandle extends BaseP2AHandle {
    private static final String TAG = AvatarHandle.class.getSimpleName();

    private static final String EXPRESSION_ART_BOY = "male_animation.bundle";
    private static final String POSE_ART_BOY = "male_pose_v2.bundle";
    private static final String EXPRESSION_ART_GIRL = "female_animation.bundle";
    private static final String POSE_ART_GIRL = "female_pose.bundle";
    private boolean mIsNeedTrack;
    private boolean mIsNeedFacePUP;

    public final FUItem headItem = new FUItem();
    public final FUItem hairItem = new FUItem();
    public final FUItem glassItem = new FUItem();
    public final FUItem beardItem = new FUItem();
    public final FUItem eyebrowItem = new FUItem();
    public final FUItem eyelashItem = new FUItem();
    public final FUItem hatItem = new FUItem();
    public final FUItem bodyItem = new FUItem();
    public final FUItem clothesItem = new FUItem();
    public final FUItem expressionItem = new FUItem();

    public AvatarHandle(BaseCore baseCore, FUItemHandler FUItemHandler, final Runnable prepare) {
        super(baseCore, FUItemHandler);
        mFUItemHandler.loadFUItem(FUItemHandler_what_controller, new FUItemHandler.LoadFUItemListener(FUP2ARenderer.BUNDLE_controller) {

            @Override
            public void onLoadComplete(FUItem fuItem) {
                controllerItem = fuItem.handle;
                if (prepare != null)
                    prepare.run();
            }
        });
    }

    public void setAvatar(AvatarP2A avatar) {
        setAvatar(avatar, false, null);
    }

    public void setAvatar(AvatarP2A avatar, Runnable completeListener) {
        setAvatar(avatar, false, completeListener);
    }

    public void setAvatar(final AvatarP2A avatar, final boolean mustLoadHead, final Runnable completeListener) {
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
                loadItem(bodyItem, avatar.getBodyFile());
                loadItem(clothesItem, avatar.getClothesFile());
                loadItem(expressionItem,
                        TextUtils.isEmpty(avatar.getExpressionFile()) ?
                                (avatar.getGender() == AvatarP2A.gender_boy ? (mIsNeedTrack || mIsNeedFacePUP ? POSE_ART_BOY : EXPRESSION_ART_BOY) : (mIsNeedTrack || mIsNeedFacePUP ? POSE_ART_GIRL : EXPRESSION_ART_GIRL))
                                : avatar.getExpressionFile());

                commitItem(avatar);
                if (completeListener != null)
                    mBaseCore.queueNextEvent(completeListener);
            }
        });
        msg.what = FUItemHandler_what;
        mFUItemHandler.sendMessage(msg);
    }

    @Override
    void bindAll() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle, eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle, clothesItem.handle, expressionItem.handle};
                    Log.i(TAG, "bundle avatarBindItem controlItem " + controllerItem + " bindAll " + Arrays.toString(items));
                    faceunity.fuBindItems(controllerItem, items);
                    setAvatarColor();
                }
            });
    }

    @Override
    void unBindAll() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle, eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle, clothesItem.handle, expressionItem.handle};
                    Log.i(TAG, "bundle avatarBindItem controlItem " + controllerItem + " unBindAll " + Arrays.toString(items));
                    faceunity.fuUnBindItems(controllerItem, items);
                }
            });
    }

    @Override
    public void release() {
        unBindAll();
        mBaseCore.queueEvent(mBaseCore.destroyItem(headItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(hairItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(glassItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(beardItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyebrowItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyelashItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(hatItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(bodyItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(expressionItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(controllerItem));
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                headItem.handle = 0;
                hairItem.handle = 0;
                glassItem.handle = 0;
                beardItem.handle = 0;
                eyebrowItem.handle = 0;
                eyelashItem.handle = 0;
                hatItem.handle = 0;
                bodyItem.handle = 0;
                clothesItem.handle = 0;
                expressionItem.handle = 0;
                controllerItem = 0;
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

    public void resetAll() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_scale", 20);
                faceunity.fuItemSetParam(controllerItem, "target_trans", 5);
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }

    public void resetAllFront() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_scale", -10);
                faceunity.fuItemSetParam(controllerItem, "target_trans", -2);
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 3);
            }
        });
    }

    public void resetAllSide() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_scale", -10);
                faceunity.fuItemSetParam(controllerItem, "target_trans", -2);
                faceunity.fuItemSetParam(controllerItem, "target_angle", -1);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 3);
            }
        });
    }

    public void resetAllMin() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_scale", 220);
                faceunity.fuItemSetParam(controllerItem, "target_trans", 70);
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }

    public void resetAllMinTop() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_scale", 350);
                faceunity.fuItemSetParam(controllerItem, "target_trans", 120);
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }

    public void resetAllMinGroup() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_scale", 350);
                faceunity.fuItemSetParam(controllerItem, "target_trans", 65);
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
    //--------------------------------------动画----------------------------------------

    public void seekToAnimFrameId(final int frameId) {
        faceunity.fuItemSetParam(controllerItem, "animFrameId", frameId);
    }

    /**
     * @param state 1：播放 2：暂停 3：停止
     */
    public void setAnimState(final int state) {
        faceunity.fuItemSetParam(controllerItem, "animState", state);
    }

    public int getNowFrameId() {
        return (int) faceunity.fuItemGetParam(controllerItem, "animFrameId");
    }

    public int getMaxFrameNum() {
        return (int) faceunity.fuItemGetParam(controllerItem, "maxFrameNum");
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
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "{\"name\":\"facepup\",\"param\":\"" + key + "\"}", values);
            }
        });
    }

    public float fuItemGetParamShape(final String key) {
        return (float) faceunity.fuItemGetParam(controllerItem, "{\"name\":\"facepup\",\"param\":\"" + key + "\"}");
    }

    public void parsePoint(EditFacePoint[] editFacePoints, int width, int height, int widthView, int heightView) {
        for (EditFacePoint point : editFacePoints) {
            faceunity.fuItemSetParam(controllerItem, "query_vert", point.index);
            int x = (int) faceunity.fuItemGetParam(controllerItem, "query_vert_x");
            int y = (int) faceunity.fuItemGetParam(controllerItem, "query_vert_y");
            x = width - x;
            y = height - y;

            float sW = (float) heightView / width;
            float sH = (float) widthView / height;
            if (sW > sH) {
                x = (int) (x * sW);
                y = (int) (y * sW - (height * sW - widthView) / 2);
            } else {
                x = (int) (x * sH - (width * sH - heightView) / 2);
                y = (int) (y * sH);
            }

            point.set(y, x);
        }
    }
}
