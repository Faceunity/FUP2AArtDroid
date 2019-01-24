package com.faceunity.p2a_art.core;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.p2a_art.core.base.BaseCore;
import com.faceunity.p2a_art.core.base.FUItem;
import com.faceunity.p2a_art.core.base.FUItemHandler;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

/**
 * Created by tujh on 2018/12/17.
 */
public class AvatarHandle extends BaseP2AHandle {
    private static final String TAG = AvatarHandle.class.getSimpleName();

    private static final String EXPRESSION_ART_BOY = "male_animation.bundle";
    private static final String POSE_ART_BOY = "male_pose.bundle";
    private static final String EXPRESSION_ART_GIRL = "female_animation.bundle";
    private static final String POSE_ART_GIRL = "female_pose.bundle";
    private boolean mIsNeedTrack;

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
                                (avatar.getGender() == AvatarP2A.gender_boy ? (mIsNeedTrack ? POSE_ART_BOY : EXPRESSION_ART_BOY) : (mIsNeedTrack ? POSE_ART_GIRL : EXPRESSION_ART_GIRL))
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

    public void setNeedFacePUP(final boolean needFacePUP) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, needFacePUP ? "enter_facepup_mode" : "quit_facepup_mode", 1);
            }
        });
    }

    // __脸型__：
    public static final String key_shape_cheek = "脸颊宽度";
    public static final String PARAM_KEY_cheek_narrow = "cheek_narrow";    //|"cheek_narrow"|控制脸颊宽度，瘦|
    public static final String PARAM_KEY_Head_fat = "Head_fat";//|"Head_fat"|控制脸颊宽度，胖|
    public static final String key_shape_Head = "脸型长度";
    public static final String PARAM_KEY_Head_shrink = "Head_shrink";//|"Head_shrink"|控制人脸整体的长度，缩短|
    public static final String PARAM_KEY_Head_stretch = "Head_stretch";//|"Head_stretch"|控制人脸整体的长度,伸长|
    public static final String key_shape_forehead = "额头宽度";
    public static final String PARAM_KEY_forehead_Wide = "Forehead_Wide";//| 控制额头宽度，宽     |
    public static final String PARAM_KEY_forehead_Narrow = "Forehead_Narrow";//| 控制额头宽度，窄     |
    public static final String key_shape_HeadBone = "额头高低";
    public static final String PARAM_KEY_HeadBone_shrink = "HeadBone_shrink";//|"HeadBone_shrink"|控制额头区域高低，低|
    public static final String PARAM_KEY_HeadBone_stretch = "HeadBone_stretch";//|"HeadBone_stretch"|控制额头区域高低，高|
    public static final String key_shape_jaw = "下巴高低";
    public static final String PARAM_KEY_jaw_lower = "jaw_lower";//|"jaw_lower"|控制下巴尖/平，尖|
    public static final String PARAM_KEY_jaw_up = "jaw_up";//|"jaw_up"|控制下巴尖/平，平|
    public static final String key_shape_jawbone = "下颚宽度";
    public static final String PARAM_KEY_jawbone_Narrow = "jawbone_Narrow";//|"jawbone_Narrow"|控制下颚宽度，窄|
    public static final String PARAM_KEY_jawbone_Wide = "jawbone_Wide";//|"jawbone_Wide"|控制下颚宽度，宽|
    // __眼睛__：
    public static final String key_shape_Eye_both = "眼睛宽窄";
    public static final String PARAM_KEY_Eye_both_in = "Eye_both_in";//|"Eye_both_in"| 眼睛型宽窄,窄|
    public static final String PARAM_KEY_Eye_both_out = "Eye_both_out";//|"Eye_both_out"| 眼睛型宽窄,宽|
    public static final String key_shape_Eye_close_open = "眼睛高低";
    public static final String PARAM_KEY_Eye_close = "Eye_close";//|"Eye_close"| 眼睛型高低,闭眼|
    public static final String PARAM_KEY_Eye_open = "Eye_open";//|"Eye_open"|眼睛型高低,睁眼|
    public static final String key_shape_Eye_inner = "key_shape_Eye_inner";
    public static final String PARAM_KEY_Eye_inner_down = "Eye_inner_down";//|"Eye_inner_down"|眼角上翘/下翘，内眼角向下|
    public static final String PARAM_KEY_Eye_inner_up = "Eye_inner_up";//|"Eye_inner_up"|眼角上翘/下翘，内眼角向上 |
    public static final String key_shape_Eye_down_up = "眼睛位置";
    public static final String PARAM_KEY_Eye_down = "Eye_down";//|"Eye_down"|眼睛整体在脸部区域的位置高低,低|
    public static final String PARAM_KEY_Eye_up = "Eye_up";//|"Eye_up"|眼睛整体在脸部区域的位置高低,高|
    public static final String key_shape_Eye_outter = "眼角高低";
    public static final String PARAM_KEY_Eye_outter_down = "Eye_outter_down";//|"Eye_outter_down"|眼角上翘/下翘，外眼角向下|
    public static final String PARAM_KEY_Eye_outter_up = "Eye_outter_up";//|"Eye_outter_up"|眼角上翘/下翘，外眼角向上|
    // __嘴巴__：
    public static final String key_shape_lipCorner = "嘴唇宽度";
    public static final String PARAM_KEY_lipCorner_In = "lipCorner_In";//|"lipCorner_In"|嘴唇宽度,窄|
    public static final String PARAM_KEY_lipCorner_Out = "lipCorner_Out";//|"lipCorner_Out"|嘴唇宽度,宽|
    public static final String key_shape_lowerLip = "下唇厚度";
    public static final String PARAM_KEY_lowerLip_Thick = "lowerLip_Thick";//|"lowerLip_Thick"|下嘴唇厚度，下嘴唇厚|
    public static final String PARAM_KEY_lowerLip_Thin = "lowerLip_Thin";//|"lowerLip_Thin"|下嘴唇厚度,下嘴唇薄|
    public static final String key_shape_lowerLipSide = "key_shape_lowerLipSide";
    public static final String PARAM_KEY_lowerLipSide_Thick = "lowerLipSide_Thick";//|"lowerLipSide_Thick"|下嘴唇厚度,下嘴角厚|
    public static final String PARAM_KEY_upperLipSide_Thick = "upperLipSide_Thick";//|"upperLipSide_Thick"|上嘴唇厚度，上嘴角厚|
    public static final String key_shape_mouth = "嘴部位置";
    public static final String PARAM_KEY_mouth_Down = "mouth_Down";//|"mouth_Down"|嘴部位置高低，低|
    public static final String PARAM_KEY_mouth_Up = "mouth_Up";//|"mouth_Up"|嘴部位置高低，高|
    public static final String key_shape_upperLip = "上唇厚度";
    public static final String PARAM_KEY_upperLip_Thick = "upperLip_Thick";//|"upperLip_Thick"|上嘴唇厚度，上嘴唇厚|
    public static final String PARAM_KEY_upperLip_Thin = "upperLip_Thin";//|"upperLip_Thin"|上嘴唇厚度，上嘴唇薄|
    // __鼻子__：
    public static final String key_shape_nose = "鼻子位置";
    public static final String PARAM_KEY_nose_Down = "nose_Down";//|"nose_Down"|鼻子位置高低,低|
    public static final String PARAM_KEY_nose_UP = "nose_UP";//|"nose_UP"|鼻子位置高低，高|
    public static final String key_shape_noseTip = "鼻头高低";
    public static final String PARAM_KEY_noseTip_Down = "noseTip_Down";//|"noseTip_Down"|鼻头高低，低|
    public static final String PARAM_KEY_noseTip_Up = "noseTip_Up";//|"noseTip_Up"|鼻头高低，高|
    public static final String key_shape_nostril = "鼻翼宽窄";
    public static final String PARAM_KEY_nostril_In = "nostril_In";//|"nostril_In"|鼻翼宽窄，窄|
    public static final String PARAM_KEY_nostril_Out = "nostril_Out";//|"nostril_Out"|鼻翼宽窄，宽|

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

}
