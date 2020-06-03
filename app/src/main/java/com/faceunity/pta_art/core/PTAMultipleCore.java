package com.faceunity.pta_art.core;

import android.content.Context;
import android.util.SparseArray;

import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.base.BaseCore;
import com.faceunity.pta_art.entity.Scenes;
import com.faceunity.pta_art.utils.BackgroundUtil;
import com.faceunity.wrapper.faceunity;

import java.util.Arrays;

/**
 * 多人合照场景
 * Created by tujh on 2018/12/17.
 */
public class PTAMultipleCore extends BaseCore {
    private static final String TAG = PTAMultipleCore.class.getSimpleName();

    private final SparseArray<AvatarHandle> mAvatarHandles = new SparseArray<>();

    public static final int ITEM_ARRAYS_BG = 0;
    public static final int ITEM_ARRAYS_CONTROLLER = 1;
    public static final int ITEM_ARRAYS_EFFECT = 2;
    public static final int ITEM_ARRAYS_FXAA = 3;
    public static final int ITEM_ARRAYS_COUNT = 4;
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];

    private BackgroundUtil mBackgroundUtil;
    public int fxaaItem, defaultBgItem, currentBgItem;
    private int[] bgItems = new int[1];
    public int cameraItem;//相机轨迹
    private int controllerItem;
    // 平地阴影道具
    public int planeItemLeft, planeItemRight;

    public PTAMultipleCore(Context context, FUPTARenderer fuP2ARenderer) {
        super(context, fuP2ARenderer);
        defaultBgItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_default_bg);
        bgItems[0] = currentBgItem = defaultBgItem;
        mItemsArray[ITEM_ARRAYS_FXAA] = fxaaItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_fxaa);
    }

    public void updateBg() {
        defaultBgItem = mFUItemHandler.loadFUItem(FilePathFactory.BUNDLE_default_bg);
        bgItems[0] = currentBgItem = defaultBgItem;
    }

    public void receiveShadowItem(int leftShadowItem, int rightShadowItem) {
        this.planeItemLeft = leftShadowItem;
        this.planeItemRight = rightShadowItem;
    }

    public SparseArray<AvatarHandle> createAvatarMultiple(Scenes scenes, int controller) {
        for (int i = 0; i < scenes.bundles.length; i++) {
            AvatarHandle avatarHandle = new AvatarHandle(this, mFUItemHandler, controller);
            avatarHandle.setPose(false);
            avatarHandle.setGroupPhoto(true);
            mAvatarHandles.put(i, avatarHandle);
        }
        cameraItem = mFUItemHandler.loadFUItem(scenes.camera);
        this.controllerItem = controller;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (cameraItem > 0) {
                    int[] items = new int[]{cameraItem};
                    faceunity.fuBindItems(controllerItem, items);
                }
                faceunity.fuBindItems(controllerItem, bgItems);
            }
        });
        mItemsArray[ITEM_ARRAYS_CONTROLLER] = controllerItem;
        return mAvatarHandles;
    }

    @Override
    public int[] itemsArray() {
        return mItemsArray;
    }

    @Override
    public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
        if (mBackgroundUtil == null) {
            mBackgroundUtil = new BackgroundUtil(w, h);
        }
        Arrays.fill(landmarksData, 0.0f);
        Arrays.fill(avatarInfo.mRotation, 0.0f);
        Arrays.fill(avatarInfo.mExpression, 0.0f);
        Arrays.fill(avatarInfo.mPupilPos, 0.0f);
        Arrays.fill(avatarInfo.mRotationMode, 0.0f);
        avatarInfo.mRotationMode[0] = (360 - mInputImageOrientation) / 90;
        avatarInfo.mIsValid = false;

        int fuTex = faceunity.fuRenderBundles(avatarInfo,
                                              faceunity.FU_ADM_FLAG_RGBA_BUFFER, w, h, mFrameId++, itemsArray());
        return mBackgroundUtil.drawBackground(fuTex);
    }

    /**
     * 解绑相机
     */
    public void unBindCamera() {
        if (cameraItem > 0) {
            int[] items = new int[]{cameraItem};
            faceunity.fuUnBindItems(this.controllerItem, items);
        }
    }

    @Override
    public void unBind() {
        for (int i = 0; i < mAvatarHandles.size(); i++) {
            unBindInstancceId(i);
        }
    }

    @Override
    public void bind() {
        for (int i = 0; i < mAvatarHandles.size(); i++) {
            bindInstancceId(i);
        }
    }

    @Override
    public void release() {
        for (int i = mAvatarHandles.size() - 1; i >= 0; i--) {
            unBindInstancceId(i);

        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mBackgroundUtil != null) {
                    mBackgroundUtil.release();
                    mBackgroundUtil = null;
                }
                unBindCamera();
                faceunity.fuUnBindItems(controllerItem, bgItems);
            }
        });
        mAvatarHandles.clear();
        queueEvent(destroyItem(fxaaItem));
        queueEvent(destroyItem(currentBgItem));
        queueEvent(destroyItem(cameraItem));
        setCurrentInstancceId(0);
    }

    /**
     * 设置当前controller控制的人物id（默认：0）
     *
     * @param id
     */
    public void setCurrentInstancceId(int id) {
        if (controllerItem > 0) {
            queueEvent(new Runnable() {
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

    /**
     * 解绑当前controller控制的人物id（默认：0）
     *
     * @param id
     */
    public void unBindInstancceId(int id) {
        if (controllerItem > 0) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem,
                                             "current_instance_id", id);
                }
            });
            mAvatarHandles.get(id).releaseNoController();
        }
    }

    /**
     * 绑定当前controller控制的人物id（默认：0）
     *
     * @param id
     */
    public void bindInstancceId(int id) {
        if (controllerItem > 0) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuItemSetParam(controllerItem,
                                             "current_instance_id", id);
                }
            });
            mAvatarHandles.get(id).bindAll();
        }
    }

    public void loadBackgroundImage(final String path) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuUnBindItems(controllerItem, bgItems);
                mBackgroundUtil.loadBackground(path);
            }
        });
    }

    public void loadBundleBg(String bgPath) {
        setCurrentInstancceId(0);
        int fuItem = mFUItemHandler.loadFUItem(bgPath);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuUnBindItems(controllerItem, bgItems);
                faceunity.fuDestroyItem(currentBgItem);
                bgItems[0] = currentBgItem = fuItem;
                faceunity.fuBindItems(controllerItem, bgItems);
                mBackgroundUtil.setUseBitmapBackground(false);
            }
        });
    }

    public void bindPlane() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuBindItems(controllerItem, new int[]{planeItemLeft, planeItemRight});
            }
        });
    }

    public void unBindPlane() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuUnBindItems(controllerItem, new int[]{planeItemLeft, planeItemRight});
            }
        });
    }
}
