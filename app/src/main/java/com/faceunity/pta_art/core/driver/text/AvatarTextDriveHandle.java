package com.faceunity.pta_art.core.driver.text;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.pta_art.constant.ColorConstant;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.pta_art.core.base.BasePTAHandle;
import com.faceunity.pta_art.core.base.FUItem;
import com.faceunity.pta_art.core.base.FUItemHandler;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

/**
 * AR Controller
 * Created by tujh on 2018/12/17.
 */
public class AvatarTextDriveHandle extends BasePTAHandle {
    private static final String TAG = AvatarTextDriveHandle.class.getSimpleName();

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

    public AvatarTextDriveHandle(BaseCore baseCore, FUItemHandler FUItemHandler, int controller) {
        super(baseCore, FUItemHandler);
        controllerItem = controller;
        setModelmat(false);
        enterVoiceMode();
        resetVideoDrive();
    }

    public void setAvatarForVoice(final AvatarPTA avatar, final Runnable completeListener) {
        mFUItemHandler.removeMessages(FUItemHandler_what);
        Message msg = Message.obtain(mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                loadItem(headItem, avatar.getHeadFile());
                // 当前的帽子都是帽子头发道具，所以就不需要原先的头发道具了
                if (TextUtils.isEmpty(avatar.getHatFile())) {
                    loadItem(hairItem, avatar.getHairFile());
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
                loadItem(expressionItem, FilePathFactory.bundleIdle(avatar.getGender()));
                loadItem(backgroundItem, "");

                loadItem(eyelinerItem, avatar.getEyelinerFile());
                loadItem(eyeshadowItem, avatar.getEyeshadowFile());
                loadItem(facemakeupItem, avatar.getFacemakeupFile());
                loadItem(lipglossItem, avatar.getLipglossFile());
                loadItem(pupilItem, avatar.getPupilFile());

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
                    completeListener.run();
            }
        });
        msg.what = FUItemHandler_what;
        mFUItemHandler.sendMessage(msg);
    }

    @Override
    protected void bindAll() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle,
                            beardItem.handle, eyebrowItem.handle, eyelashItem.handle, hatItem.handle,
                            bodyItem.handle, clothesItem.handle, clothesUpperItem.handle, clothesLowerItem.handle,
                            shoeItem.handle,   decorationsEarItem.handle,
                            decorationsFootItem.handle, decorationsHandItem.handle, decorationsHeadItem.handle, decorationsNeckItem.handle,
                            eyelinerItem.handle, eyeshadowItem.handle, facemakeupItem.handle, lipglossItem.handle, pupilItem.handle,
                            expressionItem.handle, backgroundItem.handle,
                            eyelinerItem.handle, eyeshadowItem.handle, facemakeupItem.handle,
                            lipglossItem.handle, pupilItem.handle,
                            otherItem[0] == null ? 0 : otherItem[0].handle, otherItem[1] == null ? 0 : otherItem[1].handle,
                            otherItem[2] == null ? 0 : otherItem[2].handle, otherItem[3] == null ? 0 : otherItem[3].handle,
                            otherItem[4] == null ? 0 : otherItem[4].handle};
                    Log.i(TAG, "bundle avatarBindItem controlItem " + controllerItem + " bindAll " + Arrays.toString(items));

                    faceunity.fuBindItems(controllerItem, items);
                    setAvatarColor();
                }
            });
    }

    @Override
    protected void unBindAll() {
        if (controllerItem > 0) {
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle,
                            eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle,
                            clothesItem.handle, clothesUpperItem.handle, clothesLowerItem.handle,
                            shoeItem.handle,  decorationsEarItem.handle,
                            decorationsFootItem.handle, decorationsHandItem.handle, decorationsHeadItem.handle, decorationsNeckItem.handle,
                            eyelinerItem.handle, eyeshadowItem.handle, facemakeupItem.handle, lipglossItem.handle, pupilItem.handle,
                            expressionItem.handle, backgroundItem.handle,
                            eyelinerItem.handle, eyeshadowItem.handle, facemakeupItem.handle,
                            lipglossItem.handle, pupilItem.handle,
                            otherItem[0] == null ? 0 : otherItem[0].handle, otherItem[1] == null ? 0 : otherItem[1].handle,
                            otherItem[2] == null ? 0 : otherItem[2].handle, otherItem[3] == null ? 0 : otherItem[3].handle,
                            otherItem[4] == null ? 0 : otherItem[4].handle};
                    Log.i(TAG, "bundle avatarBindItem controlItem " + controllerItem + " unBindAll " + Arrays.toString(items));
                    faceunity.fuUnBindItems(controllerItem, items);
                }
            });
        }
    }

    /**
     * 进入语音模式
     */
    public void enterVoiceMode() {
        if (controllerItem > 0) {
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem, "enable_expression_blend", 1.0f);
                    faceunity.fuItemSetParam(controllerItem, "expression_weight0", ColorConstant.sta_bs_blend.getExpression_weight0());
                    faceunity.fuItemSetParam(controllerItem, "expression_weight1", ColorConstant.sta_bs_blend.getExpression_weight1());
                }
            });
        }
    }

    /**
     * 退出语音模式
     */
    public void quitVoiceMode() {
        if (controllerItem > 0) {
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem, "enable_expression_blend", 0.0f);
                    double[] expressions = new double[57];
                    Arrays.fill(expressions, 0.0f);
                    faceunity.fuItemSetParam(controllerItem, "blend_expression", expressions);
                    faceunity.fuItemSetParam(controllerItem, "enable_expression_blend", 0.0f);
                }
            });
        }
    }

    @Override
    public void release() {
        quitVoiceMode();
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
        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesUpperItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesLowerItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(shoeItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsEarItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsFootItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsHandItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsHeadItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsNeckItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(expressionItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(backgroundItem.handle));

        mBaseCore.queueEvent(mBaseCore.destroyItem(eyelinerItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyeshadowItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(facemakeupItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(lipglossItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(pupilItem.handle));

        for (FUItem item : otherItem) {
            if (item != null) {
                mBaseCore.queueEvent(mBaseCore.destroyItem(item.handle));
            }
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
                    if (item != null) {
                        item.clear();
                    }
                }
            }
        });
    }

    @Override
    public void setMakeupHandleId() {
        eyebrowHandleId = eyebrowItem.handle;
        eyeshadowHandleId = eyeshadowItem.handle;
        lipglossHandleId = lipglossItem.handle;
        eyelashHandleId = eyelashItem.handle;
    }

    /**
     * 设置头发物理动效
     *
     * @param startModelmatbone 是否开启
     */
    public void setModelmat(final boolean startModelmatbone) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                /**
                 * 1 为开启，0 为关闭，开启的时候移动角色的值会被设进骨骼系统，这时候带DynamicBone的模型会有相关效果
                 * 如果添加了没有骨骼的模型，请关闭这个值，否则无法移动模型
                 * 默认开启
                 * 每个角色的这个值都是独立的
                 */
                faceunity.fuItemSetParam(controllerItem, "modelmat_to_bone", startModelmatbone ? 1 : 0);
            }
        });
    }


    /**
     * 1为开启，0为关闭，开启的时候已加载的物理会生效，
     * 同时加载新的带物理的bundle也会生效，关闭的时候已加载的物理会停止生效
     * ，但不会清除缓存（这时候再次开启物理会在此生效），这时加载带物理的bundle不会生效，
     * 且不会产生缓存，即关闭后加载的带物理的bundle，即时再次开启，物理也不会生效，需要重新加载
     */
    public void setEnableDynamicbone(boolean open) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "enable_dynamicbone", open ? 1.0 : 0.0);
            }
        });
    }

    public void resetVideoDrive() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 16.29f, -272.43f});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 3);
            }
        });
    }
}
