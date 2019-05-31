package com.faceunity.p2a_art.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.core.AvatarHandle;
import com.faceunity.p2a_art.core.P2AMultipleCore;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.Scenes;
import com.faceunity.p2a_art.renderer.CameraRenderer;
import com.faceunity.p2a_art.ui.GroupPhotoAvatar;
import com.faceunity.p2a_art.ui.GroupPhotoScenes;
import com.faceunity.p2a_art.ui.GroupPhotoShow;
import com.faceunity.p2a_art.utils.DateUtil;
import com.faceunity.p2a_art.utils.FileUtil;
import com.faceunity.p2a_art.utils.ToastUtil;
import com.faceunity.p2a_helper.gif.GifHardEncoderWrapper;

import java.io.File;
import java.io.IOException;


/**
 * Created by tujh on 2018/8/22.
 */
public class GroupPhotoFragment extends BaseFragment {
    public static final String TAG = GroupPhotoFragment.class.getSimpleName();

    private GroupPhotoScenes mScenesLayout;
    private GroupPhotoAvatar mAvatarLayout;
    private GroupPhotoShow mShowLayout;

    private boolean isAnimationScenes;
    private Scenes mScenes;
    private P2AMultipleCore mP2AMultipleCore;
    private SparseArray<AvatarHandle> mAvatarHandleSparse;

    private AvatarP2A[] mAvatarP2As;
    private int isLoadComplete;

    private Bitmap mShowBitmap;

    private String mGifPath = "";
    private GifHardEncoderWrapper mGifHardEncoder;
    static final int NONE_FRAME_ID = -100;
    int frameId = NONE_FRAME_ID;

    private static final int IMAGE_REQUEST_CODE = 0x102;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String filePath = FileUtil.getFileAbsolutePath(mActivity, data.getData());
            File file = new File(filePath);
            if (file.exists()) {
                if (mP2AMultipleCore != null) {
                    mP2AMultipleCore.loadBackgroundImage(filePath);
                    startGifEncoder();
                }
            } else {
                ToastUtil.showCenterToast(mActivity, "所选图片文件不存在。");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                isAnimationScenes = isAnim;
                mScenes = scenes;
                mAvatarLayout.setScenes(mScenes);
                mActivity.setGLSurfaceViewSize(true);
                mP2AMultipleCore = new P2AMultipleCore(mActivity, mFUP2ARenderer, mScenes.bg) {

                    @Override
                    public int onDrawFrame(byte[] img, int tex, int w, int h) {
                        int fuTex = super.onDrawFrame(img, tex, w, h);
                        AvatarHandle avatarHandle = mAvatarHandleSparse.get(0);
                        if (avatarHandle != null && mGifHardEncoder != null) {
                            int nowFrameId = avatarHandle.getNowFrameId();
                            if (frameId > nowFrameId) {
                                releaseGifEncoder();
                                frameId = NONE_FRAME_ID;
                            } else {
                                mGifHardEncoder.encodeFrame(fuTex);
                                frameId = nowFrameId;
                            }
                        }
                        return fuTex;
                    }
                };
                mP2ACore.unBind();
                mFUP2ARenderer.setFUCore(mP2AMultipleCore);
                mAvatarHandleSparse = mP2AMultipleCore.createAvatarMultiple(mScenes);
                mAvatarP2As = new AvatarP2A[mScenes.bundles.length];
                isLoadComplete = 0;

                mAvatarLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScenesLayout.setVisibility(View.GONE);
                        mAvatarLayout.setVisibility(View.VISIBLE);
                    }
                }, 100);
            }
        });

        mAvatarLayout = view.findViewById(R.id.group_photo_avatar);
        mAvatarLayout.setBackRunnable(new Runnable() {
            @Override
            public void run() {
                backToScenesLayout();
            }
        });
        mAvatarLayout.setAvatarSelectListener(new GroupPhotoAvatar.AvatarSelectListener() {

            @Override
            public void onAvatarSelectListener(AvatarP2A avatar, boolean isSelect) {
                if (isSelect) {
                    for (int i = 0; i < mAvatarP2As.length; i++) {
                        if (mAvatarP2As[i] == null && (Constant.style == Constant.style_new || (Constant.style == Constant.style_art && avatar.getGender() == mScenes.bundles[i].gender))) {
                            avatar.setExpression(mScenes.bundles[i]);
                            final AvatarHandle avatarHandle = mAvatarHandleSparse.get(i);
                            avatarHandle.setAvatar(mAvatarP2As[i] = avatar, new Runnable() {
                                @Override
                                public void run() {
                                    mAvatarLayout.updateAvatarPoint();
                                    if (++isLoadComplete == mAvatarHandleSparse.size()) {
                                        if (isAnimationScenes) {
                                            startGifEncoder();
                                        } else {
                                            mAvatarLayout.updateNextBtn(true);
                                        }
                                    }
                                    avatarHandle.seekToAnimFrameId(1);
                                    avatarHandle.setAnimState(2);
                                }
                            });
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < mAvatarP2As.length; i++) {
                        if (mAvatarP2As[i] != null && mAvatarP2As[i].equals(avatar)) {
                            releaseGifEncoder();
                            mAvatarP2As[i] = null;
                            mAvatarHandleSparse.get(i).setAvatar(new AvatarP2A());
                            isLoadComplete--;
                            break;
                        }
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
                            mAvatarLayout.setVisibility(View.GONE);
                            mShowLayout.setVisibility(View.VISIBLE);
                            mShowLayout.setShowGIF(mGifPath);
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
                                    mAvatarLayout.setVisibility(View.GONE);
                                    mShowLayout.setVisibility(View.VISIBLE);
                                    mShowLayout.setShowImg(mShowBitmap = bmp);
                                }
                            });
                        }
                    });
                }
            }
        });
        mAvatarLayout.setBackgroundRunnable(new Runnable() {
            @Override
            public void run() {
                Intent intent2 = new Intent();
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setType("image/*");
                if (Build.VERSION.SDK_INT < 19) {
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent2.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                startActivityForResult(intent2, IMAGE_REQUEST_CODE);
            }
        });

        mShowLayout = view.findViewById(R.id.group_photo_show);
        mShowLayout.setBackRunnable(new Runnable() {
            @Override
            public void run() {
                backToAvatarLayout();
            }
        });
        mShowLayout.setHomeRunnable(new Runnable() {
            @Override
            public void run() {
                mP2ACore.bind();
                mFUP2ARenderer.setFUCore(mP2ACore);
                backToHome();
            }
        });
        mShowLayout.setSaveRunnable(new Runnable() {
            @Override
            public void run() {
                if (isAnimationScenes) {
                    String resultPath = Constant.photoFilePath + Constant.APP_NAME + "_" + DateUtil.getCurrentDate() + ".gif";
                    try {
                        FileUtil.copyFileTo(new File(mGifPath), new File(resultPath));
                        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(resultPath))));
                        ToastUtil.showCenterToast(getContext(), "动图已保存到相册");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mShowBitmap != null && !mShowBitmap.isRecycled()) {
                        String resultPath = Constant.photoFilePath + Constant.APP_NAME + "_" + DateUtil.getCurrentDate() + ".jpg";
                        FileUtil.saveBitmapToFile(resultPath, mShowBitmap);
                        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(resultPath))));
                        ToastUtil.showCenterToast(getContext(), "合影已保存到相册");
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onBackPressed() {
        if (mShowLayout.getVisibility() == View.VISIBLE) {
            backToAvatarLayout();
        } else if (mAvatarLayout.getVisibility() == View.VISIBLE) {
            backToScenesLayout();
        } else {
            backToHome();
        }
    }

    private void backToHome() {
        mActivity.showHomeFragment();
        mActivity.setGLSurfaceViewSize(false);

        if (mP2AMultipleCore != null) {
            mP2AMultipleCore.release();
            mP2AMultipleCore = null;
        }

        FileUtil.deleteDirAndFile(Constant.TmpPath);
    }

    private void backToScenesLayout() {
        mScenesLayout.setVisibility(View.VISIBLE);
        mAvatarLayout.setVisibility(View.GONE);

        mAvatarLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.setGLSurfaceViewSize(false);
                mP2ACore.bind();
                mFUP2ARenderer.setFUCore(mP2ACore);
                if (mP2AMultipleCore != null) {
                    mP2AMultipleCore.release();
                    mP2AMultipleCore = null;
                }
            }
        }, 100);
    }

    private void backToAvatarLayout() {
        mAvatarLayout.setVisibility(View.VISIBLE);
        mShowLayout.setVisibility(View.GONE);
        mShowLayout.setShowImg(null);
        mShowLayout.setShowGIF("");
    }

    private void startGifEncoder() {
        mP2AMultipleCore.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mGifHardEncoder != null) {
                    mGifHardEncoder.release();
                }
                mGifHardEncoder = new GifHardEncoderWrapper(mGifPath = Constant.TmpPath + DateUtil.getCurrentDate() + "_tmp.gif",
                        mCameraRenderer.getCameraHeight() / 2, mCameraRenderer.getCameraWidth() / 2);
                for (int i = 0; i < mAvatarP2As.length; i++) {
                    if (mAvatarHandleSparse.get(i) != null) {
                        mAvatarHandleSparse.get(i).seekToAnimFrameId(1);
                        mAvatarHandleSparse.get(i).setAnimState(1);
                    }
                }
                frameId = NONE_FRAME_ID;
                mAvatarLayout.updateNextBtn(false);
            }
        });
    }

    private void releaseGifEncoder() {
        if (mGifHardEncoder != null) {
            mGifHardEncoder.release();
            mGifHardEncoder = null;
            mAvatarLayout.updateNextBtn(true);
        }
    }
}