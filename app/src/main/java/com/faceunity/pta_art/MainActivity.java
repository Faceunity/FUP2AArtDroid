package com.faceunity.pta_art;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.faceunity.pta_art.constant.ColorConstant;
import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.FUPTAClient;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.AvatarHandle;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.pta_art.core.PTACore;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.entity.DBHelper;
import com.faceunity.pta_art.fragment.AvatarFragment;
import com.faceunity.pta_art.fragment.BaseFragment;
import com.faceunity.pta_art.fragment.EditFaceFragment;
import com.faceunity.pta_art.fragment.GroupPhotoFragment;
import com.faceunity.pta_art.fragment.HomeFragment;
import com.faceunity.pta_art.fragment.TakePhotoFragment;
import com.faceunity.pta_art.fragment.VideoListFragment;
import com.faceunity.pta_art.fragment.drive.ARFragment;
import com.faceunity.pta_art.fragment.drive.BodyDriveFragment;
import com.faceunity.pta_art.fragment.drive.TextDriveFragment;
import com.faceunity.pta_art.renderer.CameraRenderer;
import com.faceunity.pta_art.utils.SurfaceViewOutlineProvider;
import com.faceunity.pta_art.utils.ToastUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements SensorEventListener,
        CameraRenderer.OnCameraRendererStatusListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RelativeLayout mMainLayout;
    private GLSurfaceView mGLSurfaceView;
    private CameraRenderer mCameraRenderer;
    private FUPTARenderer mFUP2ARenderer;
    private PTACore mP2ACore;
    private AvatarHandle mAvatarHandle;

    private String mShowFragmentFlag;
    private HomeFragment mHomeFragment;
    private BaseFragment mBaseFragment;

    private boolean isCanController = true;
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private DBHelper mDBHelper;
    private List<AvatarPTA> mAvatarP2As;
    private int mShowIndex;
    private AvatarPTA mShowAvatarP2A;

    /**
     * 添加重力感应
     */
    private SensorManager mSensorManager;
    private Sensor mSensor;

    /**
     * 是否可以点击
     */
    private View v_is_canClick;
    private boolean isCanClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mMainLayout = findViewById(R.id.main_layout);
        v_is_canClick = findViewById(R.id.v_is_canClick);
        v_is_canClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCanClick) {
                    ToastUtil.showCenterToast(MainActivity.this, "模型载入中，请稍后...");
                    return;
                }
            }
        });

        mGLSurfaceView = findViewById(R.id.main_gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(3);
        mCameraRenderer = new CameraRenderer(this, mGLSurfaceView);
        mCameraRenderer.setOnCameraRendererStatusListener(this);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mDBHelper = new DBHelper(this);
        mAvatarP2As = mDBHelper.getAllAvatarP2As();
        mShowAvatarP2A = mAvatarP2As.get(mShowIndex = 0);
        mFUP2ARenderer = new FUPTARenderer(this);
        mP2ACore = new PTACore(this, mFUP2ARenderer);
        mFUP2ARenderer.setFUCore(mP2ACore);
        mAvatarHandle = mP2ACore.createAvatarHandle();
        mAvatarHandle.setAvatar(getShowAvatarP2A(), new Runnable() {
            @Override
            public void run() {
                mAvatarHandle.openLight(FilePathFactory.BUNDLE_light);
                mHomeFragment.checkGuide();
            }
        });

        showHomeFragment();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        final int screenHeight = metrics.heightPixels;
        mGestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (HomeFragment.TAG.equals(mShowFragmentFlag)) {
                    return mHomeFragment.onSingleTapUp(e);
                } else if (mBaseFragment != null) {
                    return mBaseFragment.onSingleTapUp(e);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (touchMode != 1) {
                    touchMode = 1;
                    return false;
                }
                if (BodyDriveFragment.TAG.equals(mShowFragmentFlag) || ARFragment.TAG.equals(mShowFragmentFlag) ||
                        TextDriveFragment.TAG.equals(mShowFragmentFlag)) {
                    return false;
                }
                float rotDelta = -distanceX / screenWidth;
                mAvatarHandle.setRotDelta(rotDelta);
                return distanceX != 0;
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = detector.getScaleFactor() - 1;
                return scale != 0;
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mP2ACore.setAniLoadCompletedListener(listener);

        mP2ACore.setAniRefreshNowListener(new PTACore.AniRefreshNowListener() {
            @Override
            public void refreshNow(int currentHomeAnimationPosition) {
                listener.loadCompleted(0, currentHomeAnimationPosition, true);
            }
        });
    }

    private PTACore.AniLoadCompletedListener listener = new PTACore.AniLoadCompletedListener() {
        @Override
        public void loadCompleted(int loadCount, int nextAnimationPosition, boolean haveNextAni) {
            if (haveNextAni) {
                List<String> homeSwitchAnimation = FilePathFactory.getHomeSwitchAnimation();
                if (nextAnimationPosition >= homeSwitchAnimation.size()) {
                    return;
                }
                mAvatarHandle.setExpression(mShowAvatarP2A, new BundleRes(homeSwitchAnimation.get(nextAnimationPosition)), 1);
            } else {
                mAvatarHandle.clearExpression(mShowAvatarP2A, true);
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mCameraRenderer.openCamera();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mCameraRenderer.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        mCameraRenderer.onDestroy();
        Glide.get(FUApplication.getInstance()).clearMemory();
        super.onDestroy();
    }


    private volatile boolean isClose = false;
    private volatile int closeNum = 5;
    private CountDownLatch destroyCount;

    public void onFinish() {
        if (mCameraRenderer != null) {
            mCameraRenderer.setNeedStopDrawToScreen(true);
        }
        mP2ACore.queueNextEvent(new Runnable() {
            @Override
            public void run() {
                closeNum = 5;
                isClose = true;
                mP2ACore.release();
                mP2ACore.face_capture = 0;
            }
        });
        destroyCount = new CountDownLatch(1);
        try {
            destroyCount.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finish();
    }


    private int touchMode = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCanController && (HomeFragment.TAG.equals(mShowFragmentFlag)
                || EditFaceFragment.TAG.equals(mShowFragmentFlag)
                || AvatarFragment.TAG.equals(mShowFragmentFlag))) {
            if (event.getPointerCount() == 2) {
                mScaleGestureDetector.onTouchEvent(event);
            } else if (event.getPointerCount() == 1) {
                mGestureDetector.onTouchEvent(event);
            }
        } else if ((BodyDriveFragment.TAG.equals(mShowFragmentFlag) ||
                ARFragment.TAG.equals(mShowFragmentFlag) ||
                TextDriveFragment.TAG.equals(mShowFragmentFlag)) && event.getPointerCount() == 1) {
            mGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public FUPTARenderer getFUP2ARenderer() {
        return mFUP2ARenderer;
    }

    public PTACore getP2ACore() {
        return mP2ACore;
    }

    public AvatarHandle getAvatarHandle() {
        return mAvatarHandle;
    }

    public CameraRenderer getCameraRenderer() {
        return mCameraRenderer;
    }

    public GLSurfaceView getmGLSurfaceView() {
        return mGLSurfaceView;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean isToHome = false;
        boolean isNeedLoad = true;
        if (intent != null && intent.getExtras() != null) {
            isToHome = intent.getBooleanExtra("isToHome", false);
            isNeedLoad = false;
        }
        if (isNeedLoad) {
            updateAvatarP2As();
            mAvatarHandle.setAvatar(getShowAvatarP2A());
        }
        if (isToHome) {
            if (mBaseFragment instanceof GroupPhotoFragment) {
                ((GroupPhotoFragment) mBaseFragment).backToHome();
                mP2ACore.setCurrentInstancceId(0);
                mP2ACore.bind();
                mFUP2ARenderer.setFUCore(mP2ACore);
                mP2ACore.loadWholeBodyCamera();
            }
        }
    }

    /**
     * 控制切换模型时，不能点击
     *
     * @param isCanClick
     */
    public void setCanClick(boolean isCanClick, boolean isUIThread) {
        this.isCanClick = isCanClick;
        if (isUIThread) {
            v_is_canClick.setVisibility(isCanClick ? View.GONE : View.VISIBLE);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    v_is_canClick.setVisibility(isCanClick ? View.GONE : View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFUP2ARenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, final int width, int height) {
    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, int rotation) {
        if (isClose) {
            closeNum--;
            if (closeNum <= 0) {
                destroyCount.countDown();
            }
        } else {
            mCameraRenderer.refreshLandmarks(mP2ACore.getLandmarksData());
        }
        int fuTextureId = mFUP2ARenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight, rotation);
        return fuTextureId;
    }

    public void refresh(float[] LandmarksData) {
        mCameraRenderer.refreshLandmarks(LandmarksData);
    }

    public void refreshVideo(float[] LandmarksData, int width, int height) {
        mCameraRenderer.refreshVideoLandmarks(LandmarksData, width, height);
    }

    public void setBodyStatus(int num) {
    }

    private boolean isAR;

    public void setIsAR(boolean isAR) {
        this.isAR = isAR;
    }

    public boolean getIsAR() {
        return isAR;
    }

    @Override
    public void onSurfaceDestroy() {
        mFUP2ARenderer.onSurfaceDestroyed();
    }

    @Override
    public void onCameraChange(int currentCameraType, int cameraOrientation) {
        mFUP2ARenderer.onCameraChange(currentCameraType, cameraOrientation);
    }

    @Override
    public void onBackPressed() {
        if (mBaseFragment != null) {
            if (!isCanClick) {
                ToastUtil.showCenterToast(MainActivity.this, "模型载入中，请稍后...");
                return;
            }
            mBaseFragment.onBackPressed();
            return;
        }

        FilePathFactory.clearCache();
        ColorConstant.release();

        if (FUApplication.needRestartMainActivity) {
            FUPTAClient.isCoreInit = false;
            FUPTAClient.isStyleInit = false;
            onFinish();
        } else {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            Runtime.getRuntime().gc();
        }
    }

    public void showHomeFragment() {
//        mP2ACore.changeBgItem(true);
        if (mCameraRenderer.getCurrentCameraType() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraRenderer.changeCamera();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mBaseFragment != null) {
            transaction.remove(mBaseFragment);
            mBaseFragment = null;
        }
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
            transaction.add(R.id.main_fragment_layout, mHomeFragment);
        } else {
            transaction.show(mHomeFragment);
        }
        mShowFragmentFlag = HomeFragment.TAG;
        transaction.commit();
        mP2ACore.loadWholeBodyCamera();
    }

    public void showBaseFragment(String tag) {
        if (mCameraRenderer.getCurrentCameraType() == Camera.CameraInfo.CAMERA_FACING_BACK
                && !TakePhotoFragment.TAG.equals(tag)) {
            mCameraRenderer.changeCamera();
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (HomeFragment.TAG.equals(mShowFragmentFlag) && mHomeFragment != null) {
            transaction.hide(mHomeFragment);
        }
        if (mBaseFragment != null) {
            if (VideoListFragment.TAG.equals(tag)) {
                transaction.hide(mBaseFragment);
            } else {
                transaction.remove(mBaseFragment);
            }
        }
        Fragment fragment = manager.findFragmentByTag(tag);
        // 在跳转新的页面的时候，判断是否需要重新调用setAvatar方法
        boolean needResetAvatar = true;
        if (fragment == null) {
            if (EditFaceFragment.TAG.equals(tag)) {
                needResetAvatar = false;
                mBaseFragment = new EditFaceFragment();
            }
//            else if (BodyDriveFragment.TAG.equals(tag)) {
//                mBaseFragment = new BodyDriveFragment();
//            }
            else if (TakePhotoFragment.TAG.equals(tag)) {
                mBaseFragment = new TakePhotoFragment();
            } else if (GroupPhotoFragment.TAG.equals(tag)) {
                mBaseFragment = new GroupPhotoFragment();
            } else if (AvatarFragment.TAG.equals(tag)) {
                mBaseFragment = new AvatarFragment();
            } else if (VideoListFragment.TAG.equals(tag)) {
                mBaseFragment = new VideoListFragment();
            } else if (BodyDriveFragment.TAG.equals(tag)) {
                mBaseFragment = new BodyDriveFragment();
            } else if (ARFragment.TAG.equals(tag)) {
                mBaseFragment = new ARFragment();
            } else if (TextDriveFragment.TAG.equals(tag)) {
                mBaseFragment = new TextDriveFragment();
            }
            transaction.add(R.id.main_fragment_layout, mBaseFragment, tag);
        } else {
            transaction.show(mBaseFragment = (BaseFragment) fragment);
        }
        mAvatarHandle.clearExpression(mShowAvatarP2A, needResetAvatar);
        mShowFragmentFlag = tag;
//        mP2ACore.changeBgItem(false);
        transaction.commit();
    }

    public List<AvatarPTA> getAvatarP2As() {
        return mAvatarP2As;
    }

    public void updateStyle(Runnable runnable) {
        updateAvatarP2As();

        mP2ACore.release();
        mP2ACore = new PTACore(this, mFUP2ARenderer);
        mFUP2ARenderer.setFUCore(mP2ACore);
        mAvatarHandle = mP2ACore.createAvatarHandle();
        mAvatarHandle.setAvatar(getShowAvatarP2A(), runnable);
    }

    public void updateAvatarP2As() {
        List<AvatarPTA> avatarP2AS = mDBHelper.getAllAvatarP2As();
        mAvatarP2As.clear();
        mAvatarP2As.addAll(avatarP2AS);
        setShowIndex(avatarP2AS.contains(mShowAvatarP2A) ? avatarP2AS.indexOf(mShowAvatarP2A) : 0);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(AvatarFragment.TAG);
        if (fragment instanceof AvatarFragment) {
            ((AvatarFragment) fragment).notifyDataSetChanged();
        }
    }

    public void setShowIndex(int showIndex) {
        this.mShowAvatarP2A = mAvatarP2As.get(mShowIndex = showIndex);
    }

    public int getShowIndex() {
        return mShowIndex;
    }

    public AvatarPTA getShowAvatarP2A() {
        return mShowAvatarP2A;
    }

    public void setShowAvatarP2A(AvatarPTA showAvatarP2A) {
        mShowIndex = mAvatarP2As.indexOf(mShowAvatarP2A = showAvatarP2A);
    }

    public void setGLSurfaceViewSize(boolean isMin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGLSurfaceView.getLayoutParams();
        params.width = isMin ? getResources().getDimensionPixelSize(R.dimen.x480) : RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = isMin ? getResources().getDimensionPixelSize(R.dimen.x592) : RelativeLayout.LayoutParams.MATCH_PARENT;
        params.topMargin = isMin ? getResources().getDimensionPixelSize(R.dimen.x130) : 0;
        mGLSurfaceView.setLayoutParams(params);

        mGLSurfaceView.setOutlineProvider(new SurfaceViewOutlineProvider(isMin ? getResources().getDimensionPixelSize(R.dimen.x16) : 0));
        mGLSurfaceView.setClipToOutline(isMin);
    }

    public void setCanController(boolean canController) {
        isCanController = canController;
    }

    public void initDebug(View clickView) {
        if (!Constant.is_debug) return;
        try {
            Log.e(TAG, "initDebug");
            Class aClass = Class.forName("com.faceunity.pta_art.debug.DebugLayout");
            if (aClass != null) {
                View debugLayout = null;
                Constructor[] cons = aClass.getDeclaredConstructors();
                for (Constructor con : cons) {
                    Class<?>[] parameterTypes = con.getParameterTypes();
                    Log.e(TAG, "initDebug " + parameterTypes.length);
                    if (parameterTypes.length == 1 && parameterTypes[0] == Context.class) {
                        Log.e(TAG, "initDebug " + parameterTypes[0]);
                        debugLayout = (View) con.newInstance(new Object[]{this});
                        break;
                    }
                }
                Log.e(TAG, "initDebug " + debugLayout);
                if (debugLayout != null) {
                    Method initData = aClass.getMethod("initData", new Class[]{MainActivity.class, FUPTARenderer.class, AvatarHandle.class, View.class});
                    initData.invoke(debugLayout, new Object[]{this, mFUP2ARenderer, mAvatarHandle, clickView});
                    mMainLayout.addView(debugLayout);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    //手机物理旋转角度
    private int mSensorOrientation = 0;

    public int getSensorOrientation() {
        return mSensorOrientation;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
//                    mSensorOrientation = x > 0 ? 0 : 180;
                    mSensorOrientation = x > 0 ? 1 : 3;
                } else {
//                    mSensorOrientation = y > 0 ? 90 : 270;
                    mSensorOrientation = y > 0 ? 0 : 2;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
