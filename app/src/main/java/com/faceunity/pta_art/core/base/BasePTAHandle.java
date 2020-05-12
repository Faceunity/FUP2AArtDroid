package com.faceunity.pta_art.core.base;

import android.util.Log;

import com.faceunity.pta_art.constant.ColorConstant;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.wrapper.faceunity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基础的P2A Controller
 * Created by tujh on 2018/12/18.
 */
public abstract class BasePTAHandle extends BaseHandle {
    private static final String TAG = BasePTAHandle.class.getSimpleName();
    protected final int FUItemHandler_what_controller = FUItemHandler.generateWhatIndex() + 1;

    public int controllerItem;

    /**
     * 美妆bundle
     */
    public int eyebrowHandleId, eyeshadowHandleId, lipglossHandleId,
            eyelashHandleId;
    protected final List<Runnable> bindEvents = new ArrayList<>();
    protected final List<Runnable> destroyEvents = new ArrayList<>();

    public BasePTAHandle(BaseCore baseCore, FUItemHandler FUItemHandler) {
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

    protected void loadItemNew(FUItem fuItem, String name, boolean needDestroy) {
        if (name == null) return;
        if (!name.equals(fuItem.name)) {
            int item = mFUItemHandler.loadFUItem(name);
            bindEvents.add(mBaseCore.avatarBindItem(controllerItem, fuItem.handle, item));
            if (needDestroy) {
                destroyEvents.add(mBaseCore.destroyItem(fuItem.handle));
            }
            fuItem.name = name;
            fuItem.handle = item;
        }
    }

    private AvatarPTA mAvatarP2A;

    public void setAvatarP2A(AvatarPTA avatarP2A) {
        this.mAvatarP2A = avatarP2A;
    }

    protected void commitItem(AvatarPTA avatar) {
        mBaseCore.queueEvent(bindEvents);
        mBaseCore.queueEvent(destroyEvents);
        bindEvents.clear();
        destroyEvents.clear();
        mAvatarP2A = avatar;
        setMakeupHandleId();
        setAvatarColor();
    }

    protected void setAvatarColor() {
        mBaseCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mAvatarP2A.getSkinColorValue() >= 0) {
                    fuItemSetParam(PARAM_KEY_skin_color, ColorConstant.getRadioColor(mAvatarP2A.getSkinColorValue()));
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


                /**
                 * 美妆色卡相关
                 */
                if (eyebrowHandleId > 0) {
                    setMakeupColor(eyebrowHandleId, ColorConstant.getMakeupColor(ColorConstant.makeup_color, mAvatarP2A.getEyebrowColorValue()));
                }
                if (eyeshadowHandleId > 0) {
                    setMakeupColor(eyeshadowHandleId, ColorConstant.getMakeupColor(ColorConstant.makeup_color, mAvatarP2A.getEyeshadowColorValue()));
                }
                if (lipglossHandleId > 0) {
                    setMakeupColor(lipglossHandleId, ColorConstant.getMakeupColor(ColorConstant.lip_color, mAvatarP2A.getLipglossColorValue()));
                }
                if (eyelashHandleId > 0) {
                    setMakeupColor(eyelashHandleId, ColorConstant.getMakeupColor(ColorConstant.makeup_color, mAvatarP2A.getEyelashColorValue()));
                }
            }
        });
    }

    public abstract void setMakeupHandleId();

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

    /**
     * 设置美妆颜色
     *
     * @param color
     */
    public void setMakeupColor(int makeupHandleId, double[] color) {
        //设置美妆的颜色
        //美妆参数名为json结构，
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "global");
            jsonObject.put("type", "face_detail");
            jsonObject.put("param", "blend_color");
            jsonObject.put("UUID", makeupHandleId);//需要修改的美妆道具bundle handle id
        } catch (JSONException e) {
            e.printStackTrace();
        }
        double[] makeupColor = new double[color.length];
        for (int i = 0; i < color.length; i++) {
            makeupColor[i] = color[i] * 1.0 / 255;
        }
        //美妆参数值为0-1之间的RGB设置，美妆颜色原始为RGB色值(sRGB空间)，RGB/255得到传给controller的值
        //例如要替换的美妆颜色为[255,0,0], 传给controller的值为[1,0,0]
        faceunity.fuItemSetParam(controllerItem, jsonObject.toString(), makeupColor);
    }


    public int fuItemGetParamSkinColorIndex() {
        return (int) faceunity.fuItemGetParam(controllerItem, "skin_color_index");//从0开始
    }

    public int fuItemGetParamLipColorIndex() {
        return (int) faceunity.fuItemGetParam(controllerItem, "lip_color_index");//从0开始
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
