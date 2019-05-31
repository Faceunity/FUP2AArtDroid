package com.faceunity.p2a_art.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.core.client.AvatarBuilder;
import com.faceunity.p2a_art.core.NamaCore;
import com.faceunity.p2a_art.entity.AvatarP2A;
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

import org.json.JSONObject;

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

    private CreateAvatarDialog mCreateAvatarDialog;
    private NormalDialog mCancelDialog;

    private DBHelper mDBHelper;
    private AvatarBuilder mAvatarBuilder;

    private NamaCore mNamaCore;

    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_photo, container, false);
        mDBHelper = new DBHelper(mActivity);
        mAvatarBuilder = new AvatarBuilder(mActivity);
        mSensorManager = LightSensorUtil.getSensorManager(mActivity);
        mHandler = new Handler(Looper.getMainLooper());

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
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String filePath = FileUtil.getFileAbsolutePath(mActivity, data.getData());
            File file = new File(filePath);
            if (file.exists()) {
                if (Constant.is_debug) {
                    createAvatarDebug(file);
                } else {
                    Bitmap bitmap = BitmapUtil.loadBitmap(file.getPath(), 720);
                    createAvatar(bitmap, file.getName());
                }
            } else {
                ToastUtil.showCenterToast(mActivity, "所选图片文件不存在。");
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
                            float[] faceRect = isTracking == 1 ? mFaceRect : null;
                            if (Constant.is_debug) {
                                createAvatarDebug(bmp, faceRect);
                            } else {
                                createAvatar(bmp, faceRect);
                            }
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
        onBackPressed(mCreateAvatarDialog);
    }

    public void onBackPressed(final CreateAvatarDialog createAvatarDialog) {
        mFUP2ARenderer.setFUCore(mP2ACore);
        mNamaCore.release();
        if (createAvatarDialog != null) {
            mFUP2ARenderer.queueNextEvent(new Runnable() {
                @Override
                public void run() {
                    mActivity.showBaseFragment(AvatarFragment.TAG);
                    createAvatarDialog.dismiss();
                }
            });
        } else {
            mActivity.showBaseFragment(AvatarFragment.TAG);
        }
    }

    public void createAvatar(Bitmap bitmap, float[] faceRect) {
        createAvatar(bitmap, faceRect, null);
    }

    public void createAvatar(Bitmap bitmap, String name) {
        createAvatar(bitmap, null, name);
    }

    public void createAvatar(final Bitmap bitmap, final float[] faceRect, final String name) {
        Log.e(TAG, "createAvatar");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCreateAvatarDialog = new CreateAvatarDialog();
                mCreateAvatarDialog.setPhotoBitmap(bitmap);
                mCreateAvatarDialog.show(mActivity.getSupportFragmentManager(), CreateAvatarDialog.TAG);
                mCreateAvatarDialog.setSelectParamListener(new CreateAvatarDialog.SelectParamListener() {
                    String dir;

                    @Override
                    public void selectParamListener(int gender) {
                        dir = FileUtil.createFilePath(gender, name);
                        BitmapUtil.saveBitmap(dir, bitmap, faceRect);
                        createAvatar(dir, gender);
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
                                mAvatarBuilder.cancel();
                                OkHttpUtils.cancelAll();
                                FileUtil.deleteDirAndFile(dir);
                                if (mDownloadRunnable != null) {
                                    mHandler.removeCallbacks(mDownloadRunnable);
                                }
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

    private void createAvatar(final String dir, final int gender) {
        OkHttpUtils.getAvatarToken(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getAvatarToken response onFailure " + call.toString() + "\n IOException：\n" + e.toString());
                requestFailure(call, dir);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                final String token = response.body().string();
                Log.i(TAG, "getAvatarToken response message " + response.message() + " code " + response.code() + " token " + token);
                if (response.isSuccessful()) {
                    OkHttpUtils.updatePicRequest(token, dir + AvatarP2A.FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO, gender, new Callback() {
                        @Override
                        public void onFailure(Call c, IOException e) {
                            Log.e(TAG, "updatePicRequest response onFailure " + c.toString() + "\n IOException：\n" + e.toString());
                            requestFailure(c, dir);
                        }

                        @Override
                        public void onResponse(Call c, Response r) throws IOException {
                            Log.i(TAG, "updatePicRequest response message " + r.message() + " code " + r.code());
                            if (r.isSuccessful()) {
                                String json = r.body().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    if (2 == jsonObject.getInt("code")) {
                                        JSONObject object = jsonObject.getJSONObject("data");
                                        final String taskid = object.getString("taskid");
                                        download(token, taskid, dir, gender);
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            requestFailure(c, dir);
                        }
                    });
                } else {
                    requestFailure(call, dir);
                }
            }
        });
    }

    private Runnable mDownloadRunnable;

    public void download(final String token, final String taskid, final String dir, final int gender) {
        mHandler.postDelayed(mDownloadRunnable = new Runnable() {
            @Override
            public void run() {
                OkHttpUtils.downloadAvatarRequest(token, taskid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "downloadAvatarRequest response onFailure " + call.toString() + "\n IOException：\n" + e.toString());
                        requestFailure(call, dir);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "downloadAvatarRequest response message " + response.message() + " code " + response.code());
                        if (response.isSuccessful()) {
                            String json = response.body().string();
                            Log.i(TAG, "json " + json);
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if (2 == jsonObject.getInt("code")) {
                                    String data = jsonObject.getString("data");
                                    byte[] bytes = Base64.decode(data, Base64.NO_WRAP);
                                    final AvatarP2A avatarP2A = mAvatarBuilder.createAvatar(bytes, dir, gender);
                                    if (avatarP2A != null) {
                                        showAvatar(avatarP2A, mCreateAvatarDialog);
                                        return;
                                    }
                                } else if (1 == jsonObject.getInt("code")) {
                                    download(token, taskid, dir, gender);
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            CreateFailureToast.onCreateFailure(mActivity, CreateFailureToast.CreateFailureFile);
                        } else {
                            CreateFailureToast.onCreateFailure(mActivity, response.code() == 500 ? response.body().string() : CreateFailureToast.CreateFailureNet);
                        }
                        FileUtil.deleteDirAndFile(dir);
                        if (mCreateAvatarDialog != null)
                            mCreateAvatarDialog.dismiss();
                    }
                });
            }
        }, 1000);
    }

    public void requestFailure(Call call, String dir) {
        if (!call.isCanceled()) {
            CreateFailureToast.onCreateFailure(mActivity, CreateFailureToast.CreateFailureNet);
            if (mCreateAvatarDialog != null)
                mCreateAvatarDialog.dismiss();
        }
        FileUtil.deleteDirAndFile(dir);
    }

    public void showAvatar(AvatarP2A avatarP2A, final CreateAvatarDialog createAvatarDialog) {
        mDBHelper.insertHistory(avatarP2A);
        mActivity.updateAvatarP2As();
        mActivity.setShowAvatarP2A(avatarP2A);
        mAvatarHandle.setAvatar(avatarP2A, new Runnable() {
            @Override
            public void run() {
                onBackPressed(createAvatarDialog);
            }
        });
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
    private float[] mFaceRect;
    private int mFrameId = 0;

    private void checkPic() {
        isTracking = mNamaCore.isTracking();
        mFaceRect = mNamaCore.getFaceRectData();
        if (mFrameId++ % 15 > 0)
            return;
        if (isTracking != 1) {
            showCheckPic("请保持1个人输入");
        } else if (FaceCheckUtil.checkRotation(mNamaCore.getRotationData())) {
            showCheckPic("请保持正面");
        } else if (FaceCheckUtil.checkFaceRect(mFaceRect, mCameraRenderer.getCameraWidth(), mCameraRenderer.getCameraHeight())) {
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

    private void createAvatarDebug(File file) {
        try {
            Class aClass = Class.forName("com.faceunity.p2a_art.debug.DebugCreateAvatar");
            if (aClass != null) {
                Method createAvatarDebug = aClass.getMethod("createAvatarDebug", new Class[]{TakePhotoFragment.class, File.class});
                createAvatarDebug.invoke(null, new Object[]{TakePhotoFragment.this, file});
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void createAvatarDebug(Bitmap bitmap, float[] faceRect) {
        try {
            Class aClass = Class.forName("com.faceunity.p2a_art.debug.DebugCreateAvatar");
            if (aClass != null) {
                Method createAvatarDebug = aClass.getMethod("createAvatarDebug", new Class[]{TakePhotoFragment.class, Bitmap.class, float[].class});
                createAvatarDebug.invoke(null, new Object[]{TakePhotoFragment.this, bitmap, faceRect});
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
