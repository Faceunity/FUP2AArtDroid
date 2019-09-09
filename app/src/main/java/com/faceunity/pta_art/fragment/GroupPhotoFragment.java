package com.faceunity.pta_art.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.faceunity.pta_art.gles.core.GlUtil;
import com.faceunity.pta_art.renderer.CameraRenderer;
import com.faceunity.pta_art.ui.GroupPhotoAvatar;
import com.faceunity.pta_art.ui.GroupPhotoScenes;
import com.faceunity.pta_art.utils.DateUtil;
import com.faceunity.pta_art.utils.FileUtil;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.p2a_helper.video.MediaEncoder;
import com.faceunity.p2a_helper.video.MediaMuxerWrapper;
import com.faceunity.p2a_helper.video.MediaVideoEncoder;

import java.io.File;


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
    private int isLoadComplete;

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
                isAnimationScenes = isAnim;
                mScenes = scenes;
                mAvatarLayout.setScenes(mScenes);
                mActivity.setGLSurfaceViewSize(true);
                mP2AMultipleCore = new PTAMultipleCore(mActivity, mFUP2ARenderer, mScenes.bg) {

                    @Override
                    public int onDrawFrame(byte[] img, int tex, int w, int h) {
                        int fuTex = super.onDrawFrame(img, tex, w, h);
                        Log.i("wh", "w=" + w + "--h=" + h +
                                "--mScenes=" + mScenes.bundles[0].path);
                        AvatarHandle avatarHandle = mAvatarHandleSparse.get(0);
                        if (avatarHandle != null) {
                            int nowFrameId = avatarHandle.getNowFrameId();
                            if (frameId > nowFrameId) {
                                frameId = NONE_FRAME_ID;
                                stopRecording();
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAvatarLayout.updateNextBtn(true);
                                    }
                                });
                            } else {
                                if (mVideoEncoder != null) {
                                    mVideoEncoder.frameAvailableSoon(fuTex, mActivity.getCameraRenderer().getMtx(), GlUtil.IDENTITY_MATRIX);
                                }
                                frameId = nowFrameId;
                            }
                        }
                        return fuTex;
                    }
                };
                mP2ACore.unBind();
                mFUP2ARenderer.setFUCore(mP2AMultipleCore);
                mAvatarHandleSparse = mP2AMultipleCore.createAvatarMultiple(mScenes);
                mAvatarP2As = new AvatarPTA[mScenes.bundles.length];
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
            public void onAvatarSelectListener(AvatarPTA avatar, boolean isSelect) {
                if (isSelect) {
                    mCurrentAvatar = avatar;
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
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startRecording();
                                                }
                                            });
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
                    mCurrentAvatar = null;
                    for (int i = 0; i < mAvatarP2As.length; i++) {
                        if (mAvatarP2As[i] != null && mAvatarP2As[i].equals(avatar)) {
                            stopRecording();
                            mAvatarP2As[i] = null;
                            mAvatarHandleSparse.get(i).setAvatar(new AvatarPTA());
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
                            Intent intent = new Intent(mActivity, VideoAndImageActivity.class);
                            intent.putExtra("isAnimationScenes", isAnimationScenes);
                            intent.putExtra("path", mOutFile.getAbsolutePath());
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

        return view;
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

    private void startGifEncoder() {
        mP2AMultipleCore.queueEvent(new Runnable() {
            @Override
            public void run() {
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

    private void startVideoEncoder() {
        for (int i = 0; i < mAvatarP2As.length; i++) {
            if (mAvatarP2As[i] != null && mCurrentAvatar != null && mAvatarP2As[i].equals(mCurrentAvatar)) {
                stopRecording();
                mAvatarP2As[i] = null;
                mAvatarHandleSparse.get(i).setAvatar(new AvatarPTA());
                isLoadComplete--;
                break;
            }
        }
        if (mCurrentAvatar == null)
            return;
        for (int i = 0; i < mAvatarP2As.length; i++) {
            if (mAvatarP2As[i] == null && (Constant.style == Constant.style_new || (Constant.style == Constant.style_art && mCurrentAvatar.getGender() == mScenes.bundles[i].gender))) {
                mCurrentAvatar.setExpression(mScenes.bundles[i]);
                final AvatarHandle avatarHandle = mAvatarHandleSparse.get(i);
                avatarHandle.setAvatar(mAvatarP2As[i] = mCurrentAvatar, new Runnable() {
                    @Override
                    public void run() {
                        mAvatarLayout.updateAvatarPoint();
                        if (++isLoadComplete == mAvatarHandleSparse.size()) {
                            if (isAnimationScenes) {
                                startGifEncoder();
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startRecording();
                                    }
                                });
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
    }

    private File mOutFile;
    private MediaVideoEncoder mVideoEncoder;

    /**
     * 录制封装回调
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                final MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                mP2AMultipleCore.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                        mVideoEncoder = videoEncoder;
                    }
                });
            }
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                mVideoEncoder = null;
                Log.e(TAG, "stop encoder success");
            }
        }
    };

    private MediaMuxerWrapper mMuxer;

    /**
     * 开始录制
     */
    private void startRecording() {
//        try {
        stopRecording();
        String videoFileName = DateUtil.getCurrentDate() + "_tmp.mp4";
        mOutFile = new File(Constant.TmpPath, videoFileName);
        mMuxer = new MediaMuxerWrapper(mOutFile.getAbsolutePath());

        // for video capturing
        new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraRenderer.getCameraHeight(), mCameraRenderer.getCameraWidth());
        //new MediaAudioEncoder(mMuxer, mMediaEncoderListener);//去除音频录制

        mMuxer.prepare();
        mMuxer.startRecording();
//        } catch (final IOException e) {
//            Log.e(TAG, "startCapture:", e);
//        }
    }

    /**
     * 停止录制
     */
    private void stopRecording() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
        }
    }

}