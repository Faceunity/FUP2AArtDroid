package com.faceunity.pta_art.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.VideoAndImageActivity;
import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.core.AvatarHandle;
import com.faceunity.pta_art.core.PTAMultipleCore;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.Scenes;
import com.faceunity.pta_art.fragment.groupavatar.GroupPhotoScenesFragment;
import com.faceunity.pta_art.gles.core.GlUtil;
import com.faceunity.pta_art.renderer.CameraRenderer;
import com.faceunity.pta_art.ui.GroupPhotoAvatar;
import com.faceunity.pta_art.ui.GroupPhotoScenes;
import com.faceunity.pta_art.utils.FileUtil;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.pta_art.utils.VideoUtil;

import java.io.File;
import java.util.Map;


/**
 * Created by tujh on 2018/8/22.
 */
public class GroupPhotoFragment extends BaseFragment {
    public static final String TAG = GroupPhotoFragment.class.getSimpleName();

    private GroupPhotoScenes mScenesLayout;
    private GroupPhotoAvatar mAvatarLayout;

    private boolean isAnimationScenes;
    private Scenes mScenes;
    private PTAMultipleCore mP2AMultipleCore;
    private SparseArray<AvatarHandle> mAvatarHandleSparse;

    private AvatarPTA[] mAvatarP2As;
    private AvatarPTA mCurrentAvatar;
    private int currentRoleId;//当前角色id
    private AvatarHandle mCurrentAvatarHandler;
    private int isLoadComplete;

    public static final int IMAGE_REQUEST_CODE = 0x102;
    private VideoUtil videoUtil;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String filePath = FileUtil.getFileAbsolutePath(mActivity, data.getData());
            File file = new File(filePath);
            if (file.exists()) {
                mAvatarLayout.removeBgBundlePosition();
                if (mP2AMultipleCore != null) {
                    mP2AMultipleCore.loadBackgroundImage(filePath);
                    //startGifEncoder();
                    if (isAnimationScenes)
                        startVideoEncoder();
                }
            } else {
                ToastUtil.showCenterToast(mActivity, "所选图片文件不存在。");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_photo, container, false);
        FileUtil.createFile(Constant.TmpPath);

        mScenesLayout = view.findViewById(R.id.group_photo_scenes);
        mScenesLayout.setBackRunnable(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        });
        mScenesLayout.setScenesSelectListener(new GroupPhotoScenes.ScenesSelectListener() {

            @Override
            public void onScenesSelectListener(boolean isAnim, Scenes scenes) {
                mScenesLayout.setBackBtnEnable(false);
                isAnimationScenes = isAnim;
                mCurrentAvatarHandler = null;
                mScenes = scenes;
                mCurrentAvatar = null;
                currentRoleId = -1;
                mActivity.setGLSurfaceViewSize(true);
                mAvatarLayout.setScenes(mScenes);
                mP2AMultipleCore = createPTAMultipleCore();
                mP2ACore.unBind();
                mP2ACore.unBindPlane();
                mFUP2ARenderer.setFUCore(mP2AMultipleCore);
                mAvatarHandleSparse = mP2AMultipleCore.createAvatarMultiple(mScenes, mAvatarHandle.controllerItem);
                mAvatarP2As = new AvatarPTA[mScenes.bundles.length];
                isLoadComplete = 0;
                mAvatarLayout.selectedDefaultScenesBg(scenes);
                mAvatarLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScenesLayout.setVisibility(View.GONE);
                        mAvatarLayout.setVisibility(View.VISIBLE);
                        mScenesLayout.setBackBtnEnable(true);
                    }
                }, 100);
            }


        });

        mAvatarLayout = view.findViewById(R.id.group_photo_avatar);
        mAvatarLayout.setFragmentManager(getChildFragmentManager());
        mAvatarLayout.setBackRunnable(new Runnable() {
            @Override
            public void run() {
                backToScenesLayout();
            }
        });
        mAvatarLayout.setAvatarSelectListener(new GroupPhotoAvatar.AvatarSelectListener() {

            @Override
            public void onAvatarSelectListener(AvatarPTA avatar, boolean isSelect, int roleId) {
                if (isSelect) {
                    mCurrentAvatar = avatar;
                    currentRoleId = roleId;
                    syncPlayAnim(avatar, roleId);
                } else {
                    mCurrentAvatar = null;
                    currentRoleId = -1;
                    mCurrentAvatarHandler = null;
                    for (int i = 0; i < mAvatarP2As.length; i++) {
                        if (mAvatarP2As[i] != null && mAvatarP2As[i].equals(avatar)) {
                            stopRecording();
                            mAvatarP2As[i] = null;
                            mP2AMultipleCore.unBindInstancceId(roleId);
                            isLoadComplete--;
                            break;
                        }
                    }
                    // 解绑阴影
                    boolean haveAvatarInScenes = false;
                    for (boolean isSelected : mAvatarLayout.getIsSelectList()) {
                        if (isSelected) {
                            haveAvatarInScenes = true;
                            break;
                        }
                    }
                    if (!haveAvatarInScenes) {
                        mP2AMultipleCore.unBindPlane();
                    }
                }

            }
        });
        mAvatarLayout.setNextRunnable(new Runnable() {
            @Override
            public void run() {
                if (isAnimationScenes) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mActivity, VideoAndImageActivity.class);
                            intent.putExtra("isAnimationScenes", isAnimationScenes);
                            intent.putExtra("path", videoUtil.getOutPath());
                            startActivity(intent);
                        }
                    });
                } else {
                    mCameraRenderer.takePic(new CameraRenderer.TakePhotoCallBack() {
                        @Override
                        public void takePhotoCallBack(final Bitmap bmp) {
                            mCameraRenderer.setNeedStopDrawFrame(false);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String resultPath = Constant.TmpPath + "tmp.jpg";
                                    FileUtil.saveBitmapToFile(resultPath, bmp);

                                    Intent intent = new Intent(mActivity, VideoAndImageActivity.class);
                                    intent.putExtra("isAnimationScenes", isAnimationScenes);
                                    intent.putExtra("path", resultPath);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }
        });
        mAvatarLayout.setBgBundleChangeListener(new GroupPhotoScenesFragment.OnBgBundleChangeListener() {
            @Override
            public void onBgBundleChangeListener(String path) {
                mP2AMultipleCore.loadBundleBg(path);
                if (isAnimationScenes) {
                    startVideoEncoder();
                }
            }
        });

        initVideoUtil();

        return view;
    }

    private int currentFrame = 0;

    private PTAMultipleCore createPTAMultipleCore() {
        if (mP2AMultipleCore == null) {
            mP2AMultipleCore = new PTAMultipleCore(mActivity, mFUP2ARenderer) {

                @Override
                public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
                    int fuTex = super.onDrawFrame(img, tex, w, h, rotation);
                    //avatarHandle为空，则生成场景图像
                    if (mCurrentAvatarHandler != null) {
                        float nowFrameId = mCurrentAvatarHandler.getAnimateProgress(mCurrentAvatarHandler.expressionItem.handle);
                        if (nowFrameId >= 1.0f) {
                            //录制mp4完成
                            stopRecording();
                            currentFrame = 0;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAvatarLayout.updateNextBtn(true);
                                }
                            });
                        } else {
                            // 等待3帧再进行录制，防止出现录制到黑屏
                            if (currentFrame > 3) {
                                videoUtil.sendRecordingData(fuTex, GlUtil.IDENTITY_MATRIX);
                            }
                            currentFrame++;
                        }
                    }
                    return fuTex;
                }
            };
        } else {
            mP2AMultipleCore.updateBg();
        }
        mP2AMultipleCore.receiveShadowItem(mP2ACore.planeItemLeft, mP2ACore.planeItemRight);
        return mP2AMultipleCore;
    }

    private void initVideoUtil() {
        videoUtil = new VideoUtil(mActivity.getmGLSurfaceView());
        videoUtil.setEndListener(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAnimationScenes) {
            startVideoEncoder();
        }
    }

    @Override
    public void onBackPressed() {
        if (mAvatarLayout.getVisibility() == View.VISIBLE) {
            backToScenesLayout();
        } else {
            backToHome();
        }
    }

    public void backToHome() {
        stopRecording();
        if (mP2AMultipleCore != null) {
            mP2AMultipleCore.release();
            mP2AMultipleCore = null;
        }
        mActivity.showHomeFragment();
        mActivity.setGLSurfaceViewSize(false);
        FileUtil.deleteDirAndFile(Constant.TmpPath);
    }

    private void backToScenesLayout() {
        mScenesLayout.setVisibility(View.VISIBLE);
        mAvatarLayout.setVisibility(View.GONE);
        mActivity.setCanClick(false, true);
        mAvatarLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.setGLSurfaceViewSize(false);
                if (mP2AMultipleCore != null) {
                    mP2AMultipleCore.release();
                }
                mP2ACore.setCurrentInstancceId(0);
                mP2ACore.bind();
                mP2ACore.bindPlane();
                mFUP2ARenderer.setFUCore(mP2ACore);
                mActivity.setCanClick(true, true);
            }
        }, 100);
    }

    private void startVideoEncoder() {
        for (int i = 0; i < mAvatarP2As.length; i++) {
            if (mAvatarP2As[i] != null && mCurrentAvatar != null && mAvatarP2As[i].equals(mCurrentAvatar)) {
                stopRecording();
                mAvatarP2As[i] = null;
                mCurrentAvatarHandler = null;
                isLoadComplete--;
                break;
            }
        }
        if (mCurrentAvatar != null) {
            syncPlayAnim(mCurrentAvatar, currentRoleId);
        }
    }

    /**
     * 同步播放动画
     *
     * @param avatar
     * @param roleId
     */
    private void syncPlayAnim(AvatarPTA avatar, int roleId) {
        for (int i = 0; i < mAvatarP2As.length; i++) {
            if (mAvatarP2As[i] == null) {
                avatar.setExpression(mScenes.bundles[i]);
                final AvatarHandle avatarHandle = mAvatarHandleSparse.get(roleId);
                avatarHandle.setNeedNextEventCallback(false);
                mP2AMultipleCore.setCurrentInstancceId(roleId);
                mActivity.setCanClick(false, true);
                avatarHandle.setAvatar(mAvatarP2As[i] = avatar, new Runnable() {
                    @Override
                    public void run() {
                        if (isAnimationScenes) {
                            mP2AMultipleCore.bindPlane();
                        }
                        mActivity.setCanClick(true, false);
                        mAvatarLayout.updateAvatarPoint();
                        if (++isLoadComplete == mAvatarHandleSparse.size()) {
                            if (isAnimationScenes) {
                                //startGifEncoder();
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAvatarLayout.updateNextBtn(false);
                                        mCurrentAvatarHandler = avatarHandle;
                                        startRecording();
                                    }
                                });
                            } else {
                                mAvatarLayout.updateNextBtn(true);
                            }
                        }
                        if (isAnimationScenes) {
                            Map<Integer, Integer> usedRole = mAvatarLayout.getUsedRoleId();
                            boolean[] isSels = mAvatarLayout.getIsSelectList();
                            for (int j = 0; j < isSels.length; j++) {
                                if (isSels[j]) {
                                    int f_roleId = usedRole.get(j);
                                    if (f_roleId != roleId) {
                                        mAvatarHandleSparse.get(f_roleId).setAnimState(3, f_roleId);
                                        mAvatarHandleSparse.get(f_roleId).seekToAnimBegin(mAvatarHandleSparse.get(f_roleId).expressionItem.handle);
                                        if (mAvatarHandleSparse.get(f_roleId).otherItem[1] != null) {
                                            mAvatarHandleSparse.get(f_roleId).seekToAnimBegin(mAvatarHandleSparse.get(f_roleId).otherItem[1].handle);
                                        }
                                    }
                                }
                            }
                            mAvatarHandleSparse.get(roleId).setAnimState(3, roleId);
                            mAvatarHandleSparse.get(roleId).seekToAnimBegin(mAvatarHandleSparse.get(roleId).expressionItem.handle);

                            if (mAvatarHandleSparse.get(roleId).otherItem[1] != null) {
                                /**
                                 *  道具动画逻辑修改
                                 *  需要加载道具模型和道具动画（图形自动寻找对应的动画）
                                 *  需要和人物动画一起开始（保持同步）
                                 *  注意：配置json信息的时候，需要将道具动画放在第二个位置
                                 */
                                mAvatarHandleSparse.get(roleId).seekToAnimBegin(mAvatarHandleSparse.get(roleId).otherItem[1].handle);
                            }
                        }
                    }
                });
                break;
            }
        }
    }

    /**
     * 开始录制
     */
    private void startRecording() {
        int dimensionPixelSize592 = getResources().getDimensionPixelSize(R.dimen.x592);
        int dimensionPixelSize480 = getResources().getDimensionPixelSize(R.dimen.x480);
        int textureWidth = mCameraRenderer.getCameraHeight();
        int textureHeight = mCameraRenderer.getCameraWidth();

        int viewWidth = dimensionPixelSize480 - (dimensionPixelSize480 % 3);
        int viewHeight = dimensionPixelSize592 - (dimensionPixelSize592 % 3);

        int scale = textureWidth * viewHeight / viewWidth / textureHeight;

        int videoWidth = 0;
        int videoHeight = 0;

        if (scale < 1) {
            //取宽度作为最小值
            videoWidth = Math.min(textureWidth, viewWidth);
            videoHeight = videoWidth * viewHeight / viewWidth;
        } else {
            // 取高度作为最小值
            videoHeight = Math.min(textureHeight, viewHeight);
            videoWidth = videoHeight * viewWidth / viewHeight;
        }

        int cropX = -(textureWidth - videoWidth) / 2;
        int cropY = -(textureHeight - videoHeight) / 2;
        videoUtil.startRecording(videoWidth, videoHeight,
                                 cropX, cropY,
                                 textureWidth, textureHeight,
                                 0, null, null);
    }

    /**
     * 停止录制
     */
    private void stopRecording() {
        videoUtil.stopRecording();
    }
}