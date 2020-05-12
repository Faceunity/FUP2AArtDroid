package com.faceunity.pta_art.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.core.NamaCore;
import com.faceunity.pta_art.core.client.AvatarBuilder;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.DBHelper;
import com.faceunity.pta_art.renderer.CameraRenderer;
import com.faceunity.pta_art.ui.CreateAvatarDialog;
import com.faceunity.pta_art.ui.NormalDialog;
import com.faceunity.pta_art.utils.BitmapUtil;
import com.faceunity.pta_art.utils.FaceCheckUtil;
import com.faceunity.pta_art.utils.FileUtil;
import com.faceunity.pta_art.utils.LightSensorUtil;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.pta_art.web.CreateFailureToast;
import com.faceunity.pta_art.web.OkHttpUtils;
import com.faceunity.pta_art.web.ProgressRequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
            public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
                //因为已经对双输入的cpu buffer进行旋转、镜像，使其与texture对齐
                //所以这边不需要其他处理
                int fu = super.onDrawFrame(img, tex, w,
                                           h, rotation);
                checkPic(w, h);
                return fu;
            }
        };
        mNamaCore.setFace_capture(mP2ACore.face_capture);
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

    private Class debugCreateInfo;
    private Object debugCreateInfoObject;

    public void createDebugCreateInfo() {
        try {
            debugCreateInfo = Class.forName("com.faceunity.pta_art.debug.DebugCreateInfo");
            debugCreateInfoObject = debugCreateInfo == null ?
                    null : debugCreateInfo.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
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
                        createAvatarStart();
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

    private long startGetToken, endGetToken;//获取token的时间
    private long endUploadImage;//上传图片的时间
    private long endPollDownload;//轮询下载的时间
    private long startDown, endDown;//下载bundle的时间
    private long endCreateAvatar;//生成avatar时间

    private void createAvatar(final String dir, final int gender) {
        startGetToken = System.currentTimeMillis();
        OkHttpUtils.getAvatarToken(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getAvatarToken response onFailure " + call.toString() + "\n IOException：\n" + e.toString());
                requestFailure(call, dir);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                final String tokenStr = response.body().string();
                endGetToken = System.currentTimeMillis();
                Log.i(TAG, "time: getTokenTime=" + (endGetToken - startGetToken) + "ms");
                Log.i(TAG, "getAvatarToken response message " + response.message() + " code " + response.code() + " serverjson " + tokenStr);
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(tokenStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String token = jsonObject.optString("token");
                    Object latest = jsonObject.opt("latest");
                    Object type = jsonObject.opt("type");
                    if (latest != null && type != null) {
                        Log.i(TAG, "latest=" + latest + "--type=" + type);
                        if (!((String) latest).equals(Constant.pta_client_version_new)) {
                            if (mCreateAvatarDialog != null)
                                mCreateAvatarDialog.dismiss();
                            FileUtil.deleteDirAndFile(dir);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(mActivity)
                                            .setTitle("警告")
                                            .setCancelable(false)
                                            .setMessage("当前版本与服务器版本不一致，无法生成")
                                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            });
                            return;
                        }
                    }
                    OkHttpUtils.updatePicRequest(token, dir + AvatarPTA.FILE_NAME_CLIENT_DATA_ORIGIN_PHOTO, gender, new Callback() {
                        @Override
                        public void onFailure(Call c, IOException e) {
                            Log.e(TAG, "updatePicRequest response onFailure " + c.toString() + "\n IOException：\n" + e.toString());
                            requestFailure(c, dir);
                        }

                        @Override
                        public void onResponse(Call c, Response r) throws IOException {
                            endUploadImage = System.currentTimeMillis();
                            Log.i(TAG, "time: UploadImageTime=" + (endUploadImage - endGetToken) + "ms");
                            Log.i(TAG, "updatePicRequest response message " + r.message() + " code " + r.code());
                            if (r.isSuccessful()) {
                                String json = r.body().string();
                                Log.i(TAG, "updatePicRequest response json " + json);
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    if (2 == jsonObject.getInt("code")) {
                                        JSONObject object = jsonObject.getJSONObject("data");
                                        final String taskid = object.getString("taskid");
                                        Log.i(TAG, "updatePicRequest response taskid " + taskid);
                                        download(token, taskid, dir, gender);
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            requestFailure(c, dir);
                        }
                    }, new ProgressRequestBody.UploadProgressListener() {
                        @Override
                        public void onUploadRequestProgress(long byteWritten, long contentLength) {
                            if (byteWritten == contentLength) {
                                uploadDataComplete();
                            }
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
                            downloadAvatarStart();
                            String json = response.body().string();
                            Log.i(TAG, "json " + json);
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if (2 == jsonObject.getInt("code")) {
                                    endPollDownload = System.currentTimeMillis();
                                    Log.i(TAG, "time: PollDownloadTime=" + (endPollDownload - endUploadImage) + "ms");
                                    String data = jsonObject.getString("data");
                                    startDown = System.currentTimeMillis();
                                    Log.i(TAG, "time: ParsingJsonTime=" + (startDown - endPollDownload) + "ms");
                                    //下载bundle
                                    OkHttpUtils.downServiceBundle(data, dir, new OkHttpUtils.OnDownloadListener() {
                                        @Override
                                        public void onDownloadStart() {
                                        }

                                        @Override
                                        public void onDownProgress(long currentLen, long allLen) {

                                        }

                                        @Override
                                        public void onDownloadSuccess(byte[] bytes) {
                                            endDown = System.currentTimeMillis();
                                            Log.i(TAG, "time: downBundleTime=" + (endDown - startDown) + "ms");
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final AvatarPTA avatarP2A = mAvatarBuilder.createAvatar(bytes, dir, gender, new Runnable() {
                                                        @Override
                                                        public void run() {

                                                        }
                                                    });
                                                    endCreateAvatar = System.currentTimeMillis();
                                                    Log.i(TAG, "time: createAvatarTime=" + (endCreateAvatar - endDown) + "ms");
                                                    Log.i(TAG, "time: createAvatarAllTime=" + (endCreateAvatar - startGetToken) + "ms");
                                                    createAvatarComplete(dir, bytes);
                                                    if (avatarP2A != null) {
                                                        showAvatar(avatarP2A, mCreateAvatarDialog);
                                                        return;
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onDownloadFailed() {
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    CreateFailureToast.onCreateFailure(mActivity, CreateFailureToast.CreateFailureFile);
                                                    FileUtil.deleteDirAndFile(dir);
                                                    if (mCreateAvatarDialog != null)
                                                        mCreateAvatarDialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                    return;
                                    //byte[] bytes = Base64.decode(data, Base64.NO_WRAP);
//                                    downloadAvatarComplete();
                                } else if (1 == jsonObject.getInt("code")
                                        && jsonObject.getString("message").equals("PROCESSING")) {
                                    download(token, taskid, dir, gender);
                                    return;
                                } else {
                                    JSONObject object = jsonObject.optJSONObject("data");
                                    Log.i(TAG, "error_code=" + object.getInt("err_code"));
                                    CreateFailureToast.onCreateFailure(mActivity, object.getInt("err_code") + "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                // 图片格式不对
                                CreateFailureToast.onCreateFailure(mActivity, CreateFailureToast.CreateFailureFile);
                            }
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

    public void showAvatar(AvatarPTA avatarP2A, final CreateAvatarDialog createAvatarDialog) {
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

    private void checkPic(int w, int h) {
        isTracking = mNamaCore.isTracking();
        mFaceRect = mNamaCore.getFaceRectData();
        if (mFrameId++ % 15 > 0)
            return;
        if (isTracking != 1) {
            showCheckPic("请保持1个人输入");
        } else if (FaceCheckUtil.checkRotation(mNamaCore.getRotationData())) {
            showCheckPic("识别失败，需要人物正脸完整出镜哦~");
        } else if (FaceCheckUtil.checkFaceRect(mFaceRect, w, h)) {
            showCheckPic("识别失败，请再试一次~");
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
            Class aClass = Class.forName("com.faceunity.pta_art.debug.DebugCreateAvatar");
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
            Class aClass = Class.forName("com.faceunity.pta_art.debug.DebugCreateAvatar");
            if (aClass != null) {
                Method createAvatarDebug = aClass.getMethod("createAvatarDebug", new Class[]{TakePhotoFragment.class, Bitmap.class, float[].class});
                createAvatarDebug.invoke(null, new Object[]{TakePhotoFragment.this, bitmap, faceRect});
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void createAvatarStart() {
        if (debugCreateInfo != null) {
            Method createAvatarDebug = null;
            try {
                createAvatarDebug = debugCreateInfo.getMethod("createAvatarStart", new Class[]{});
                createAvatarDebug.invoke(debugCreateInfoObject, new Object[]{});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void createAvatarComplete(String dir, byte[] objData) {
        if (debugCreateInfo != null) {
            Method createAvatarDebug = null;
            try {
                createAvatarDebug = debugCreateInfo.getMethod("createAvatarComplete", new Class[]{String.class, byte[].class});
                createAvatarDebug.invoke(debugCreateInfoObject, new Object[]{dir, objData});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadDataComplete() {
        if (debugCreateInfo != null) {
            Method createAvatarDebug = null;
            try {
                createAvatarDebug = debugCreateInfo.getMethod("uploadDataComplete", new Class[]{});
                createAvatarDebug.invoke(debugCreateInfoObject, new Object[]{});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadAvatarStart() {
        if (debugCreateInfo != null) {
            Method createAvatarDebug = null;
            try {
                createAvatarDebug = debugCreateInfo.getMethod("downloadAvatarStart", new Class[]{});
                createAvatarDebug.invoke(debugCreateInfoObject, new Object[]{});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadAvatarComplete() {
        if (debugCreateInfo != null) {
            Method createAvatarDebug = null;
            try {
                createAvatarDebug = debugCreateInfo.getMethod("downloadAvatarComplete", new Class[]{});
                createAvatarDebug.invoke(debugCreateInfoObject, new Object[]{});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
