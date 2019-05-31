package com.faceunity.p2a_art.core.base;

import android.util.Log;

import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.wrapper.faceunity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tujh on 2018/12/18.
 */
public abstract class BaseP2AHandle extends BaseHandle {
    private static final String TAG = BaseP2AHandle.class.getSimpleName();
    protected final int FUItemHandler_what_controller = FUItemHandler.generateWhatIndex() + 1;

    public int controllerItem;

    protected final List<Runnable> bindEvents = new ArrayList<>();
    protected final List<Runnable> destroyEvents = new ArrayList<>();

    public BaseP2AHandle(BaseCore baseCore, FUItemHandler FUItemHandler) {
        super(baseCore, FUItemHandler);
    }

    protected abstract void bindAll();

    protected abstract void unBindAll();

    public abstract void release();

    protected void loadItem(FUItem fuItem, String name) {
        loadItem(fuItem, name, false);
    }

    protected void loadItem(FUItem fuItem, String name, boolean mustLoadHead) {
        if (name == null) return;
        if (!name.equals(fuItem.name) || mustLoadHead) {
            int item = mFUItemHandler.loadFUItem(name);
            bindEvents.add(mBaseCore.avatarBindItem(controllerItem, fuItem.handle, item));
            destroyEvents.add(mBaseCore.destroyItem(fuItem.handle));
            fuItem.name = name;
            fuItem.handle = item;
        }
    }

    private AvatarP2A mAvatarP2A;

    protected void commitItem(AvatarP2A avatar) {
        mBaseCore.queueEvent(bindEvents);
        mBaseCore.queueEvent(destroyEvents);
        bindEvents.clear();
        destroyEvents.clear();
        mAvatarP2A = avatar;
        setAvatarColor();
    }

    protected void setAvatarColor() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mAvatarP2A.getSkinColorValue() >= 0) {
                    fuItemSetParam(PARAM_KEY_skin_color, ColorConstant.getColor(ColorConstant.skin_color, mAvatarP2A.getSkinColorValue()));
                }
                if (mAvatarP2A.getLipColorValue() >= 0) {
                    fuItemSetParam(PARAM_KEY_lip_color, ColorConstant.getColor(ColorConstant.lip_color, mAvatarP2A.getLipColorValue()));
                }
                fuItemSetParam(PARAM_KEY_iris_color, ColorConstant.getColor(ColorConstant.iris_color, mAvatarP2A.getIrisColorValue()));
                fuItemSetParam(PARAM_KEY_hair_color, ColorConstant.getColor(ColorConstant.hair_color, mAvatarP2A.getHairColorValue()));
                fuItemSetParam(PARAM_KEY_hair_color_intensity, ColorConstant.getColor(ColorConstant.hair_color, mAvatarP2A.getHairColorValue())[3]);
                fuItemSetParam(PARAM_KEY_glass_color, ColorConstant.getColor(ColorConstant.glass_color, mAvatarP2A.getGlassesColorValue()));
                fuItemSetParam(PARAM_KEY_glass_frame_color, ColorConstant.getColor(ColorConstant.glass_frame_color, mAvatarP2A.getGlassesFrameColorValue()));
                fuItemSetParam(PARAM_KEY_beard_color, ColorConstant.getColor(ColorConstant.beard_color, mAvatarP2A.getBeardColorValue()));
                fuItemSetParam(PARAM_KEY_hat_color, ColorConstant.getColor(ColorConstant.hat_color, mAvatarP2A.getHatColorValue()));
            }
        });
    }

    public void fuItemSetParamFuItemHandler(final String key, final double[] values) {
        mFUItemHandler.post(new Runnable() {
            @Override
            public void run() {
                fuItemSetParam(key, values);
                Log.i(TAG, "fuItemSetParamFuItemHandler key " + key + " values " + Arrays.toString(values));
            }
        });
    }

    public void fuItemSetParam(final String key, final double[] values) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, key, values);
                Log.i(TAG, "fuItemSetParam key " + key + " values " + Arrays.toString(values));
            }
        });
    }

    public void fuItemSetParamFuItemHandler(final String key, final double values) {
        mFUItemHandler.post(new Runnable() {
            @Override
            public void run() {
                fuItemSetParam(key, values);
                Log.i(TAG, "fuItemSetParamFuItemHandler key " + key + " values " + values);
            }
        });
    }

    public void fuItemSetParam(final String key, final double values) {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(controllerItem, key, values);
                Log.i(TAG, "fuItemSetParam key " + key + " values " + values);
            }
        });
    }

    public int fuItemGetParamSkinColorIndex() {
        return (int) faceunity.fuItemGetParam(controllerItem, "skin_color_index");
    }

    public int fuItemGetParamLipColorIndex() {
        return (int) faceunity.fuItemGetParam(controllerItem, "lip_color_index");
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

}
