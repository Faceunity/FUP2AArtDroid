package com.faceunity.p2a_art.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.core.NamaCore;
import com.faceunity.p2a_art.core.P2AClientWrapper;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.BundleRes;
import com.faceunity.p2a_art.entity.DBHelper;
import com.faceunity.p2a_art.renderer.CameraRenderer;
import com.faceunity.p2a_art.ui.CreateAvatarDialog;
import com.faceunity.p2a_art.ui.NormalDialog;
import com.faceunity.p2a_art.utils.BitmapUtil;
import com.faceunity.p2a_art.utils.FaceCheckUtil;
import com.faceunity.p2a_art.utils.FileUtil;
import com.faceunity.p2a_art.utils.LightSensorUtil;
import com.faceunity.p2a_art.utils.ToastUtil;
import com.faceunity.p2a_art.web.CreateFailureToast;
import com.faceunity.p2a_art.web.OkHttpUtils;
import com.faceunity.p2a_art.web.ProgressRequestBody;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by tujh on 2018/10/26.
 */
public class TakePhotoFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = TakePhotoFragment.class.getSimpleName();

    private TextView mTakePhotoPoint;

    private boolean isCancel = false;
    private CreateAvatarDialog mCreateAvatarDialog;
    private NormalDialog mCancelDialog;

    private DBHelper mDBHelper;

    private NamaCore mNamaCore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);
        mDBHelper = new DBHelper(mActivity);
        mSensorManager = LightSensorUtil.getSensorManager(mActivity);

        mTakePhotoPoint = view.findViewById(R.id.take_photo_point);
        view.findViewById(R.id.take_photo_back).setOnClickListener(this);
        view.findViewById(R.id.take_photo_change_camera).setOnClickListener(this);
        view.findViewById(R.id.take_photo_select).setOnClickListener(this);
        view.findViewById(R.id.take_photo_btn).setOnClickListener(this);

        LightSensorUtil.registerLightSensor(mSensorManager, mSensorEventListener);

        mNamaCore = new NamaCore(getContext(), mFUP2ARenderer) {
            @Override
            public int onDrawFrame(byte[] img, int tex, int w, int h) {
                int fu = super.onDrawFrame(img, tex, w, h);
                checkPic();
                return fu;
            }
        };
        mFUP2ARenderer.setFUCore(mNamaCore);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LightSensorUtil.unregisterLightSensor(mSensorManager, mSensorEventListener);
    }

    private static final int IMAGE_REQUEST_CODE = 0x102;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                String filePath = FileUtil.getFileAbsolutePath(mActivity, uri);
                File file = new File(filePath);
                if (!Constant.is_debug || !createAvatarDebug(file)) {
                    if (file.exists()) {
                        Bitmap bitmap = BitmapUtil.loadBitmap(filePath, 720);
                        String dir = BitmapUtil.saveBitmap(bitmap, null);
                        createAvatar(bitmap, dir);
                        return;
                    } else {
                        ToastUtil.showCenterToast(mActivity, "所选图片文件不存在。");
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo_back:
                onBackPressed();
                break;
            case R.id.take_photo_change_camera:
                mCameraRenderer.changeCamera();
                break;
            case R.id.take_photo_select:
                if (mCameraRenderer.isNeedStopDrawFrame()) return;
                Intent intent2 = new Intent();
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setType("image/*");
                if (Build.VERSION.SDK_INT < 19) {
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent2.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                startActivityForResult(intent2, IMAGE_REQUEST_CODE);
                break;
            case R.id.take_photo_btn:
                if (isTracking > 0) {
                    mCameraRenderer.takePic(new CameraRenderer.TakePhotoCallBack() {
                        @Override
                        public void takePhotoCallBack(final Bitmap bmp) {
                            mCameraRenderer.setNeedStopDrawFrame(false);
                            String dir = BitmapUtil.saveBitmap(bmp, isTracking == 1 ? faceRect : null);
                            createAvatar(bmp, dir);
                        }
                    });
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showCenterToast(mActivity, "面部识别失败，请重新尝试");
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mFUP2ARenderer.setFUCore(mP2ACore);
        mNamaCore.release();
        if (mCreateAvatarDialog != null) {
            mCameraRenderer.updateMTX();
            mFUP2ARenderer.queueNextEvent(new Runnable() {
                @Override
                public void run() {
                    mActivity.showHomeFragment();
                    mCreateAvatarDialog.dismiss();
                }
            });
        } else {
            mActivity.showHomeFragment();
        }
    }

    private void createAvatar(final Bitmap bitmap, final String dir) {
        Log.e(TAG, "createAvatar");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isCancel = false;
                mCreateAvatarDialog = new CreateAvatarDialog();
                mCreateAvatarDialog.setPhotoBitmap(bitmap);
                mCreateAvatarDialog.show(mActivity.getSupportFragmentManager(), CreateAvatarDialog.TAG);
                mCreateAvatarDialog.setSelectParamListener(new CreateAvatarDialog.SelectParamListener() {

                    @Override
                    public void selectParamListener(int gender, int style) {
                        createAvatar(dir, gender, style);
                    }

                    @Override
                    public void cancelListener() {
                        mCancelDialog = new NormalDialog();
                        mCancelDialog.setNormalDialogTheme(R.style.FullScreenTheme);
                        mCancelDialog.setMessageStr("您确认放弃生成么？");
                        mCancelDialog.setNegativeStr("取消");
                        mCancelDialog.setPositiveStr("确认");
                        mCancelDialog.show(mActivity.getSupportFragmentManager(), NormalDialog.TAG);
                        mCancelDialog.setOnClickListener(new NormalDialog.OnSimpleClickListener() {
                            @Override
                            public void onPositiveListener() {
                                if (mCreateAvatarDialog != null)
                                    mCreateAvatarDialog.dismiss();
                                isCancel = true;
                                OkHttpUtils.cancelAll();
                                FileUtil.deleteDirAndFile(new File(dir));
                            }

                            @Override
                            public void onDismissListener() {
                                mCancelDialog = null;
                            }
                        });
                    }

                    @Override
                    public void dismissListener() {
                        mCreateAvatarDialog = null;
                        if (mCancelDialog != null) {
                            mCancelDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private long uploadDataTime;
    private long downloadDataTime;
    private long serverDataTime;
    private long headDataTime;
    private long allTime;

    private void createAvatar(final String dir, final int gender, final int style) {
        final long createAvatarTime = System.nanoTime();
        OkHttpUtils.createAvatarRequest(dir + AvatarP2A.FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO, gender, style, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()) {
                    Log.e(TAG, "response onFailure " + call.toString() + "\n IOException：\n" + e.toString());
                    CreateFailureToast.onCreateFailure(mActivity, CreateFailureToast.CreateFailureNet);
                    mCreateAvatarDialog.dismiss();
                }
                FileUtil.deleteDirAndFile(new File(dir));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "response message " + response.message() + " code " + response.code());
                if (response.isSuccessful()) {
                    long onResponseTime = System.nanoTime();
                    byte[] bytes = response.body().bytes();
                    long downloadTime = System.nanoTime();
                    downloadDataTime = (downloadTime - onResponseTime) / Constant.NANO_IN_ONE_MILLI_SECOND;
                    serverDataTime = (downloadTime - createAvatarTime) / Constant.NANO_IN_ONE_MILLI_SECOND;
                    final AvatarP2A avatarP2A = handleP2AConvert(bytes, dir, gender, style);
                    long completeTime = System.nanoTime();
                    headDataTime = (completeTime - downloadTime) / Constant.NANO_IN_ONE_MILLI_SECOND;
                    allTime = (completeTime - createAvatarTime) / Constant.NANO_IN_ONE_MILLI_SECOND;
                    if (avatarP2A != null) {
                        mDBHelper.insertHistory(avatarP2A);
                        mActivity.updateAvatarP2As();
                        mActivity.setShowAvatarP2A(avatarP2A);
                        mAvatarHandle.setAvatar(avatarP2A, new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        });
                        return;
                    } else {
                        CreateFailureToast.onCreateFailure(mActivity, CreateFailureToast.CreateFailureFile);
                    }
                } else {
                    CreateFailureToast.onCreateFailure(mActivity, response.code() == 500 ? response.body().string() : CreateFailureToast.CreateFailureNet);
                }
                FileUtil.deleteDirAndFile(new File(dir));
                mCreateAvatarDialog.dismiss();
            }
        }, new ProgressRequestBody.UploadProgressListener() {
            @Override
            public void onUploadRequestProgress(long byteWritten, long contentLength) {
                if (byteWritten == contentLength) {
                    uploadDataTime = (System.nanoTime() - createAvatarTime) / Constant.NANO_IN_ONE_MILLI_SECOND;
                }
            }
        });
    }

    private volatile int isCreateIndex = 2;

    public AvatarP2A handleP2AConvert(final byte[] objData, final String dir, final int gender, final int style) {
        try {
            final AvatarP2A avatarP2A = P2AClientWrapper.initializeAvatarP2A(dir, gender, style);
            if (isCancel) return null;
            isCreateIndex = 2;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BundleRes[] hairBundles = AvatarConstant.hairBundleRes(gender);
                    try {
                        P2AClientWrapper.initializeAvatarP2AData(objData, avatarP2A);
                        P2AClientWrapper.createHair(mActivity, objData, hairBundles[avatarP2A.getHairIndex()].path, avatarP2A.getHairFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        synchronized (avatarP2A) {
                            if (--isCreateIndex == 0)
                                avatarP2A.notify();
                            else
                                avatarP2A.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isCancel) return;
                    try {
                        for (int i = 0; i < hairBundles.length; i++) {
                            String hairPath = hairBundles[i].path;
                            if (!TextUtils.isEmpty(hairPath) && i != avatarP2A.getHairIndex()) {
                                P2AClientWrapper.createHair(mActivity, objData, hairPath, avatarP2A.getHairFileList()[i]);
                            }
                        }
                        handleP2AConvertDebug(dir, objData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            P2AClientWrapper.createHead(objData, avatarP2A.getHeadFile());
            synchronized (avatarP2A) {
                if (--isCreateIndex == 0)
                    avatarP2A.notify();
                else
                    avatarP2A.wait();
            }
            if (isCancel) return null;
            return avatarP2A;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    //*****************************人脸检测*********************************

    private SensorManager mSensorManager;
    private float mLight;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                //光线强度
                mLight = event.values[0];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private int isTracking;
    private float[] faceRect;
    private int mFrameId = 0;

    private void checkPic() {
        isTracking = mNamaCore.isTracking();
        faceRect = mNamaCore.getFaceRectData();
        if (mFrameId++ % 15 > 0)
            return;
        if (isTracking != 1) {
            showCheckPic("请保持1个人输入");
        } else if (FaceCheckUtil.checkRotation(mNamaCore.getRotationData())) {
            showCheckPic("请保持正面");
        } else if (FaceCheckUtil.checkFaceRect(faceRect, mCameraRenderer.getCameraWidth(), mCameraRenderer.getCameraHeight())) {
            showCheckPic("请将人脸对准虚线框");
        } else if (FaceCheckUtil.checkExpression(mNamaCore.getExpressionData())) {
            showCheckPic("请保持面部无夸张表情");
        } else if (mLight < 5) {
            showCheckPic("光线不充足");
        } else {
            showCheckPic("完美");
        }
    }

    private void showCheckPic(final String message) {
        if (mTakePhotoPoint == null) return;
        mTakePhotoPoint.post(new Runnable() {
            @Override
            public void run() {
                mTakePhotoPoint.setText(message);
            }
        });
    }

    //*****************************debug部分代码*********************************

    private boolean createAvatarDebug(File file) {
        if (!Constant.is_debug) return false;
        try {
            Class aClass = Class.forName("com.faceunity.p2a_art.debug.DebugP2AClientWrapper");
            if (aClass != null) {
                Method createAvatarDebug = aClass.getMethod("createAvatarDebug", new Class[]{File.class, TakePhotoFragment.class});
                return (boolean) createAvatarDebug.invoke(null, new Object[]{file, TakePhotoFragment.this});
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    private void handleP2AConvertDebug(String dir, byte[] objData) {
        if (!Constant.is_debug) return;
        try {
            Class aClass = Class.forName("com.faceunity.p2a_art.debug.DebugP2AClientWrapper");
            if (aClass != null) {
                Method handleP2AConvertDebug = aClass.getMethod("handleP2AConvertDebug", new Class[]{String.class, byte[].class, Long.class, Long.class, Long.class, Long.class, Long.class});
                handleP2AConvertDebug.invoke(null, new Object[]{dir, objData, uploadDataTime, downloadDataTime, serverDataTime, headDataTime, allTime});
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
