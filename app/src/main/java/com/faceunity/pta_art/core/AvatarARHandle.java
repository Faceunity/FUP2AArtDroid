package com.faceunity.pta_art.core;

import android.hardware.Camera;
import android.os.Message;
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
public class AvatarARHandle extends BasePTAHandle {
    private static final String TAG = AvatarARHandle.class.getSimpleName();
    final int FUItemHandler_what_filter = FUItemHandler_what + 2;

    public final FUItem filterItem = new FUItem();
    public final FUItem headItem = new FUItem();
    public final FUItem hairItem = new FUItem();
    public final FUItem glassItem = new FUItem();
    public final FUItem beardItem = new FUItem();
    public final FUItem eyebrowItem = new FUItem();
    public final FUItem eyelashItem = new FUItem();
    public final FUItem hatItem = new FUItem();
    public final FUItem bodyItem = new FUItem();
    public final FUItem clothesItem = new FUItem();
    //    public final FUItem clothesSuitUpperItem = new FUItem();
//    public final FUItem clothesSuitLowerItem = new FUItem();
    public final FUItem clothesUpperItem = new FUItem();
    public final FUItem clothesLowerItem = new FUItem();
    public final FUItem shoeItem = new FUItem();
    public final FUItem decorationsItem = new FUItem();
    public final FUItem expressionItem = new FUItem();
    public final FUItem otherItem[] = new FUItem[5];
    public int hairMask;

    public AvatarARHandle(BaseCore baseCore, FUItemHandler FUItemHandler, int controller) {
        super(baseCore, FUItemHandler);
        hairMask = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_hair_mask);
        controllerItem = controller;
    }

    public void setARAvatar(final AvatarPTA avatar, boolean needDestory, Runnable runnable) {
        mFUItemHandler.removeMessages(FUItemHandler_what);
        Message msg = Message.obtain(mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                while (clearState == 1) {
                    waitingWhilePolling();
                }
                loadItemNew(headItem, avatar.getHeadFile(), needDestory);
                loadItemNew(hairItem, avatar.getHairFile(), needDestory);
                loadItemNew(glassItem, avatar.getGlassesFile(), needDestory);
                loadItemNew(beardItem, avatar.getBeardFile(), needDestory);
                loadItemNew(eyebrowItem, avatar.getEyebrowFile(), needDestory);
                loadItemNew(eyelashItem, avatar.getEyelashFile(), needDestory);
                loadItemNew(hatItem, avatar.getHatFile(), needDestory);
                commitItem(avatar);
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        msg.what = FUItemHandler_what;
        mFUItemHandler.sendMessage(msg);
    }

    public void setAvatar(final AvatarPTA avatar, boolean needDestory, Runnable runnable) {
        mFUItemHandler.removeMessages(FUItemHandler_what);
        Message msg = Message.obtain(mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                while (clearState == 1) {

                }
                loadItemNew(headItem, avatar.getHeadFile(), needDestory);
                loadItemNew(hairItem, avatar.getHairFile(), needDestory);
                loadItemNew(glassItem, avatar.getGlassesFile(), needDestory);
                loadItemNew(beardItem, avatar.getBeardFile(), needDestory);
                loadItemNew(eyebrowItem, avatar.getEyebrowFile(), needDestory);
                loadItemNew(eyelashItem, avatar.getEyelashFile(), needDestory);
                loadItemNew(hatItem, avatar.getHatFile(), needDestory);
                loadItemNew(bodyItem, FilePathFactory.bodyBundle(avatar.getGender()), needDestory);
                //if (avatar.getClothesIndex() != 0) {
                loadItemNew(clothesItem, avatar.getClothesFile(), needDestory);
                //}
//                if (avatar.getClothesIndex() != 0) {
//                    loadItemNew(clothesSuitUpperItem, avatar.getClothesFile().replace("_female", "").replace(".bundle", "_upper.bundle"),
//                            needDestory);
//                    loadItemNew(clothesSuitLowerItem, avatar.getClothesFile().replace("_female", "").replace(".bundle", "_lower.bundle"),
//                            needDestory);
//                } else {
//                    loadItemNew(clothesSuitUpperItem, avatar.getClothesFile(), needDestory);
//                    loadItemNew(clothesSuitLowerItem, avatar.getClothesFile(), needDestory);
//                }
                loadItemNew(clothesUpperItem, avatar.getClothesUpperFile(), needDestory);
                loadItemNew(clothesLowerItem, avatar.getClothesLowerFile(), needDestory);
                loadItemNew(shoeItem, avatar.getShoeFile(), needDestory);
                loadItemNew(decorationsItem, avatar.getDecorationsFile(), needDestory);
                loadItemNew(expressionItem, FilePathFactory.bundleAnim(avatar.getGender()), needDestory);

                String[] others = avatar.getOtherFile();
                for (int i = 0; i < otherItem.length; i++) {
                    if (others != null && i < others.length) {
                        loadItemNew(otherItem[i], others[i], needDestory);
                    } else if (otherItem[i] != null) {
                        final int finalI = i;
                        mBaseCore.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                faceunity.fuUnBindItems(controllerItem, new int[]{otherItem[finalI].handle});
                                if (needDestory) {
                                    mBaseCore.destroyItem(otherItem[finalI].handle).run();
                                    otherItem[finalI].clear();
                                }
                            }
                        });
                    }
                }
                commitItem(avatar);
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        msg.what = FUItemHandler_what;
        mFUItemHandler.sendMessage(msg);
    }

    public void setAvatarForVoice(final AvatarPTA avatar, boolean needDestory, final Runnable completeListener) {
        mFUItemHandler.removeMessages(FUItemHandler_what);
        Message msg = Message.obtain(mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                while (clearState == 1) {
                    waitingWhilePolling();
                }
                loadItemNew(headItem, avatar.getHeadFile(), needDestory);
                loadItemNew(hairItem, avatar.getHairFile(), needDestory);
                loadItemNew(glassItem, avatar.getGlassesFile(), needDestory);
                loadItemNew(beardItem, avatar.getBeardFile(), needDestory);
                loadItemNew(eyebrowItem, avatar.getEyebrowFile(), needDestory);
                loadItemNew(eyelashItem, avatar.getEyelashFile(), needDestory);
                loadItemNew(hatItem, avatar.getHatFile(), needDestory);
                loadItemNew(bodyItem, FilePathFactory.bodyBundle(avatar.getGender()), needDestory);
                loadItemNew(clothesItem, avatar.getClothesFile(), needDestory);
//                if (avatar.getClothesIndex() != 0) {
//                    loadItemNew(clothesSuitUpperItem, avatar.getClothesFile().replace("_female", "").replace(".bundle", "_upper.bundle"), needDestory);
//                    loadItemNew(clothesSuitLowerItem, avatar.getClothesFile().replace("_female", "").replace(".bundle", "_lower.bundle"), needDestory);
//                } else {
//                    loadItemNew(clothesSuitUpperItem, avatar.getClothesFile(), needDestory);
//                    loadItemNew(clothesSuitLowerItem, avatar.getClothesFile(), needDestory);
//                }
                loadItemNew(clothesUpperItem, avatar.getClothesUpperFile(), needDestory);
                loadItemNew(clothesLowerItem, avatar.getClothesLowerFile(), needDestory);
                loadItemNew(shoeItem, avatar.getShoeFile(), needDestory);
                loadItemNew(decorationsItem, avatar.getDecorationsFile(), needDestory);
                loadItemNew(expressionItem, FilePathFactory.bundleIdle(avatar.getGender()), needDestory);
                String[] others = avatar.getOtherFile();
                for (int i = 0; i < otherItem.length; i++) {
                    if (others != null && i < others.length) {
                        loadItemNew(otherItem[i], others[i], needDestory);
                    } else if (otherItem[i] != null) {
                        final int finalI = i;
                        mBaseCore.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                faceunity.fuUnBindItems(controllerItem, new int[]{otherItem[finalI].handle});
                                if (needDestory) {
                                    mBaseCore.destroyItem(otherItem[finalI].handle).run();
                                    otherItem[finalI].clear();
                                }
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

    public void setFilter(final String filter) {
        if (!filter.equals(filterItem.name)) {
            mFUItemHandler.removeMessages(FUItemHandler_what_filter);
            mFUItemHandler.loadFUItem(FUItemHandler_what_filter, new FUItemHandler.LoadFUItemListener(filter) {

                @Override
                public void onLoadComplete(FUItem fuItem) {
                    filterItem.handle = fuItem.handle;
                    filterItem.name = fuItem.name;
                }
            });
        }
    }

    private int clearState = 0;//是否销毁完成

    /**
     * 取消绑定并销毁句柄
     *
     * @param isAR
     */
    public void unBindAndDestory(boolean isAR) {
        clearState = 1;
        if (isAR) {
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle, eyebrowItem.handle,
                            eyelashItem.handle, hatItem.handle};
                    Log.i(TAG, "bundle unBindAndDestory controlItem " + controllerItem + " unBindARAll " + Arrays.toString(items));
                    faceunity.fuUnBindItems(controllerItem, items);
                }
            });
            mBaseCore.queueEvent(mBaseCore.destroyItem(headItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(hairItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(glassItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(beardItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(eyebrowItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(eyelashItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(hatItem.handle));
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

                    clearState = 0;
                }
            });
        } else {
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{bodyItem.handle,
                            clothesItem.handle,
//                            clothesSuitUpperItem.handle, clothesSuitLowerItem.handle,
                            clothesUpperItem.handle, clothesLowerItem.handle,
                            shoeItem.handle, decorationsItem.handle,
                            expressionItem.handle,
                            otherItem[0] == null ? 0 : otherItem[0].handle, otherItem[1] == null ? 0 : otherItem[1].handle, otherItem[2] == null ? 0 : otherItem[2].handle, otherItem[3] == null ? 0 : otherItem[3].handle, otherItem[4] == null ? 0 : otherItem[4].handle};
                    Log.i(TAG, "bundle unBindAndDestory controlItem " + controllerItem + " unBindAll " + Arrays.toString(items));
                    faceunity.fuUnBindItems(controllerItem, items);
                }
            });
            mBaseCore.queueEvent(mBaseCore.destroyItem(bodyItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(clothesItem.handle));
//            mBaseCore.queueEvent(mBaseCore.destroyItem(clothesSuitUpperItem.handle));
//            mBaseCore.queueEvent(mBaseCore.destroyItem(clothesSuitLowerItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(clothesUpperItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(clothesLowerItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(shoeItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsItem.handle));
            mBaseCore.queueEvent(mBaseCore.destroyItem(expressionItem.handle));
            for (FUItem item : otherItem) {
                if (item != null) {
                    mBaseCore.queueEvent(mBaseCore.destroyItem(item.handle));
                }
            }
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    bodyItem.clear();
                    clothesItem.clear();
//                    clothesSuitUpperItem.clear();
//                    clothesSuitLowerItem.clear();
                    clothesUpperItem.clear();
                    clothesLowerItem.clear();
                    shoeItem.clear();
                    decorationsItem.clear();
                    expressionItem.clear();
                    for (FUItem item : otherItem) {
                        if (item != null) {
                            item.clear();
                        }
                    }

                    clearState = 0;
                }
            });
        }
    }

    private void waitingWhilePolling() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void bindAll() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle, eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle,
                            clothesItem.handle,
//                            clothesSuitUpperItem.handle, clothesSuitLowerItem.handle,
                            clothesUpperItem.handle, clothesLowerItem.handle, shoeItem.handle, decorationsItem.handle,
                            expressionItem.handle,
                            otherItem[0] == null ? 0 : otherItem[0].handle, otherItem[1] == null ? 0 : otherItem[1].handle, otherItem[2] == null ? 0 : otherItem[2].handle, otherItem[3] == null ? 0 : otherItem[3].handle, otherItem[4] == null ? 0 : otherItem[4].handle};
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
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle, beardItem.handle, eyebrowItem.handle, eyelashItem.handle, hatItem.handle, bodyItem.handle,
                            clothesItem.handle,
//                            clothesSuitUpperItem.handle, clothesSuitLowerItem.handle,
                            clothesUpperItem.handle, clothesLowerItem.handle,
                            shoeItem.handle, decorationsItem.handle,
                            expressionItem.handle,
                            hairMask,
                            otherItem[0] == null ? 0 : otherItem[0].handle, otherItem[1] == null ? 0 : otherItem[1].handle, otherItem[2] == null ? 0 : otherItem[2].handle, otherItem[3] == null ? 0 : otherItem[3].handle, otherItem[4] == null ? 0 : otherItem[4].handle};
                    Log.i(TAG, "bundle avatarBindItem controlItem " + controllerItem + " unBindAll " + Arrays.toString(items));
                    faceunity.fuUnBindItems(controllerItem, items);
                }
            });
        }
    }

    /**
     * 退出ar模式
     */
    public void quitArMode() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuUnBindItems(controllerItem, new int[]{hairMask});
                    faceunity.fuItemSetParam(controllerItem, "quit_ar_mode", 1);
                }
            });
    }

    /**
     * 进入ar模式
     */
    public void enterArMode() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem, "enter_ar_mode", 1);
                    faceunity.fuBindItems(controllerItem, new int[]{hairMask});
                }
            });
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
//        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesSuitLowerItem.handle));
//        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesSuitUpperItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesUpperItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(clothesLowerItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(shoeItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(expressionItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(hairMask));
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
//                clothesSuitUpperItem.clear();
//                clothesSuitLowerItem.clear();
                clothesUpperItem.clear();
                clothesLowerItem.clear();
                shoeItem.clear();
                decorationsItem.clear();
                expressionItem.clear();
                for (FUItem item : otherItem) {
                    if (item != null) {
                        item.clear();
                    }
                }
            }
        });
    }

    public void onCameraChange(final int currentCameraType, final int inputImageOrientation) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "is3DFlipH", Camera.CameraInfo.CAMERA_FACING_BACK == currentCameraType ? 1 : 0);
                faceunity.fuItemSetParam(controllerItem, "arMode", (Camera.CameraInfo.CAMERA_FACING_BACK == currentCameraType ? inputImageOrientation : (360 - inputImageOrientation)) / 90);
            }
        });
    }

    public void setNeedTrackFace(boolean needTrackFace) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, needTrackFace ? "enter_track_rotation_mode" : "quit_track_rotation_mode", 1);
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

    public void resetHalfScreen() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 56.48, -1000});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 1);
            }
        });
    }

}
