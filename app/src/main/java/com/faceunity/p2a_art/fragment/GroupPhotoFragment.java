package com.faceunity.p2a_art.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.gif_sdk.GifEncoderWrapper;
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
    private GifEncoderWrapper mGifEncoderWrapper;

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
                mP2AMultipleCore = new P2AMultipleCore(mActivity, mFUP2ARenderer) {
                    static final int NONE_FRAME_ID = -100;
                    int frameId = NONE_FRAME_ID;

                    @Override
                    public int onDrawFrame(byte[] img, int tex, int w, int h) {
                        int fuTex = super.onDrawFrame(img, tex, w, h);
                        AvatarHandle avatarHandle = mAvatarHandleSparse.get(0);
                        if (avatarHandle != null && mGifEncoderWrapper != null) {
                            int nowFrameId = avatarHandle.getNowFrameId();
                            if (frameId > nowFrameId) {
                                releaseGifEncoder();
                                frameId = NONE_FRAME_ID;
                            } else {
                                mGifEncoderWrapper.encodeFrame(getReadBackImg(), getReadBackW(), getReadBackH(), mCameraRenderer.getCameraOrientation());
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
                        if (mAvatarP2As[i] == null && avatar.getGender() == mScenes.bundles[i].gender) {
                            avatar.setExpressionFile(mScenes.bundles[i].path);
                            final AvatarHandle avatarHandle = mAvatarHandleSparse.get(i);
                            avatarHandle.setAvatar(mAvatarP2As[i] = avatar, new Runnable() {
                                @Override
                                public void run() {
                                    mAvatarLayout.updateAvatarPoint();
                                    if (++isLoadComplete == mAvatarHandleSparse.size()) {
                                        if (isAnimationScenes) {
                                            mP2AMultipleCore.queueEvent(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mGifEncoderWrapper = new GifEncoderWrapper(mGifPath = Constant.TmpPath + DateUtil.getCurrentDate() + "_tmp.gif",
                                                            mP2AMultipleCore.getReadBackH(), mP2AMultipleCore.getReadBackW());
                                                    for (int i = 0; i < mAvatarP2As.length; i++) {
                                                        if (mAvatarHandleSparse.get(i) != null) {
                                                            mAvatarHandleSparse.get(i).seekToAnimFrameId(1);
                                                            mAvatarHandleSparse.get(i).setAnimState(1);
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            mAvatarLayout.updateNextBtn();
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

        FileUtil.deleteDirAndFile(new File(Constant.TmpPath));
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

    private void releaseGifEncoder() {
        if (mGifEncoderWrapper != null) {
            mGifEncoderWrapper.release();
            mGifEncoderWrapper = null;
            mAvatarLayout.updateNextBtn();
        }
    }
}
