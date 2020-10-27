package com.faceunity.pta_art.core.driver.ar;

import android.hardware.Camera;
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

import java.util.Arrays;

/**
 * AR Controller
 * Created by tujh on 2018/12/17.
 */
public class AvatarARDriveHandle extends BasePTAHandle {
    private static final String TAG = AvatarARDriveHandle.class.getSimpleName();
    final int FUItemHandler_what_filter = FUItemHandler_what + 2;

    public final FUItem filterItem = new FUItem();
    public final FUItem headItem = new FUItem();
    public final FUItem hairItem = new FUItem();
    public final FUItem glassItem = new FUItem();
    public final FUItem beardItem = new FUItem();
    public final FUItem eyebrowItem = new FUItem();
    public final FUItem eyelashItem = new FUItem();
    public final FUItem hatItem = new FUItem();
    public final FUItem shoeItem = new FUItem();
    public final FUItem decorationsEarItem = new FUItem();
    public final FUItem decorationsHeadItem = new FUItem();

    public final FUItem eyelinerItem = new FUItem();
    public final FUItem eyeshadowItem = new FUItem();
    public final FUItem facemakeupItem = new FUItem();
    public final FUItem lipglossItem = new FUItem();
    public final FUItem pupilItem = new FUItem();

    public final FUItem expressionItem = new FUItem();
    public final FUItem otherItem[] = new FUItem[5];
    public int hairMask;

    public AvatarARDriveHandle(BaseCore baseCore, FUItemHandler FUItemHandler, int controller) {
        super(baseCore, FUItemHandler);
        hairMask = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_hair_mask);
        controllerItem = controller;
        setModelmat(false);
        enterArMode();
    }

    public void setARAvatar(final AvatarPTA avatar, Runnable runnable) {
        setARAvatar(avatar, true, runnable);
    }

    public void setARAvatar(final AvatarPTA avatar, boolean needDestory, Runnable runnable) {
        mFUItemHandler.removeMessages(FUItemHandler_what);
        Message msg = Message.obtain(mFUItemHandler, new Runnable() {
            @Override
            public void run() {
                loadItemNew(headItem, avatar.getHeadFile(), needDestory);
                // 当前的帽子都是帽子头发道具，所以就不需要原先的头发道具了
                if (TextUtils.isEmpty(avatar.getHatFile())) {
                    loadItem(hairItem, avatar.getHairFile(), needDestory);
                    loadItem(hatItem, avatar.getHatFile(), needDestory);
                } else {
                    loadItem(hairItem, "", needDestory);
                    loadItem(hatItem, avatar.getHatFile(), needDestory);
                }
                loadItemNew(glassItem, avatar.getGlassesFile(), needDestory);
                loadItemNew(beardItem, avatar.getBeardFile(), needDestory);
                loadItemNew(eyebrowItem, avatar.getEyebrowFile(), needDestory);
                loadItemNew(eyelashItem, avatar.getEyelashFile(), needDestory);
                loadItem(decorationsEarItem, avatar.getEarDecorationsFile());
                loadItem(decorationsHeadItem, avatar.getHeadDecorationsFile());

                loadItem(eyelinerItem, avatar.getEyelinerFile());
                loadItem(eyeshadowItem, avatar.getEyeshadowFile());
                loadItem(facemakeupItem, avatar.getFacemakeupFile());
                loadItem(lipglossItem, avatar.getLipglossFile());
                loadItem(pupilItem, avatar.getPupilFile());

                commitItem(avatar);
                if (runnable != null) {
                    runnable.run();
                }
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

    @Override
    protected void bindAll() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int[] items = new int[]{headItem.handle, hairItem.handle, glassItem.handle,
                            beardItem.handle, eyebrowItem.handle, eyelashItem.handle, hatItem.handle,
                            shoeItem.handle, expressionItem.handle, decorationsEarItem.handle, decorationsHeadItem.handle,
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
                            eyebrowItem.handle, eyelashItem.handle, hatItem.handle,
                            shoeItem.handle, expressionItem.handle, hairMask, decorationsEarItem.handle, decorationsHeadItem.handle,
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
     * 退出ar模式
     */
    public void quitArMode() {
        if (controllerItem > 0)
            mBaseCore.queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuUnBindItems(controllerItem, new int[]{hairMask});
                    faceunity.fuItemSetParam(controllerItem, "quit_ar_mode", 1);
                    //3.设置enable_face_processor，说明启用或者关闭面部追踪，value = 1.0表示开启，value = 0.0表示关闭
                    faceunity.fuItemSetParam(controllerItem, "enable_face_processor", 0.0);
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
                    //3.设置enable_face_processor，说明启用或者关闭面部追踪，value = 1.0表示开启，value = 0.0表示关闭
                    faceunity.fuItemSetParam(controllerItem, "enable_face_processor", 1.0);
                }
            });

    }

    @Override
    public void release() {
        quitArMode();
        unBindAll();
        mBaseCore.queueEvent(mBaseCore.destroyItem(headItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(hairItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(glassItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(beardItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyebrowItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyelashItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(hatItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(shoeItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(expressionItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsEarItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(decorationsHeadItem.handle));

        mBaseCore.queueEvent(mBaseCore.destroyItem(eyelinerItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(eyeshadowItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(facemakeupItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(lipglossItem.handle));
        mBaseCore.queueEvent(mBaseCore.destroyItem(pupilItem.handle));

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
                shoeItem.clear();
                expressionItem.clear();
                decorationsEarItem.clear();
                decorationsHeadItem.clear();

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

    public void onCameraChange(final int currentCameraType, final int inputImageOrientation) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "is3DFlipH", Camera.CameraInfo.CAMERA_FACING_BACK == currentCameraType ? 1 : 0);
                faceunity.fuItemSetParam(controllerItem, "arMode", (Camera.CameraInfo.CAMERA_FACING_BACK == currentCameraType ? inputImageOrientation : (360 - inputImageOrientation)) / 90);
            }
        });
    }

    /**
     * //AR模式下，为了支持旋转屏幕时，同时旋转头发遮罩
     * //0表示设备未旋转，1表示逆时针旋转90度，2表示逆时针旋转180度，3表示逆时针旋转270度
     *
     * @param screen_orientation
     */
    public void setScreenOrientation(int screen_orientation) {
        faceunity.fuItemSetParam(controllerItem, "screen_orientation", screen_orientation);
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

    public void resetAll() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, "target_position", new double[]{0.0, 0, 0});
                faceunity.fuItemSetParam(controllerItem, "target_angle", 0);
                faceunity.fuItemSetParam(controllerItem, "reset_all", 6);
            }
        });
    }
}
