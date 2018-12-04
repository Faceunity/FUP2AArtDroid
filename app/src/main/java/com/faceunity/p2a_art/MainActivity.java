package com.faceunity.p2a_art;

import android.content.Intent;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;

import com.faceunity.p2a_art.constant.DBHelper;
import com.faceunity.p2a_art.core.AvatarP2A;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.fragment.ARFilterFragment;
import com.faceunity.p2a_art.fragment.BaseFragment;
import com.faceunity.p2a_art.fragment.EditFaceFragment;
import com.faceunity.p2a_art.fragment.HomeFragment;
import com.faceunity.p2a_art.fragment.TakePhotoFragment;
import com.faceunity.p2a_art.renderer.CameraRenderer;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements FUP2ARenderer.OnLoadBodyListener,
        CameraRenderer.OnCameraRendererStatusListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GLSurfaceView mGLSurfaceView;
    private CameraRenderer mCameraRenderer;
    private FUP2ARenderer mFUP2ARenderer;

    private String mShowFragmentFlag;
    private HomeFragment mHomeFragment;
    private BaseFragment mBaseFragment;

    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private DBHelper mDBHelper;
    private List<AvatarP2A> mAvatarP2As;
    private int mShowIndex;
    private AvatarP2A mShowAvatarP2A;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGLSurfaceView = findViewById(R.id.main_gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(3);
        mCameraRenderer = new CameraRenderer(this, mGLSurfaceView);
        mCameraRenderer.setOnCameraRendererStatusListener(this);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mDBHelper = new DBHelper(this);
        mAvatarP2As = mDBHelper.getAllAvatarP2As();
        mShowAvatarP2A = mAvatarP2As.get(mShowIndex = 0);

        mFUP2ARenderer = new FUP2ARenderer(this);
        mFUP2ARenderer.setOnLoadBodyListener(this);

        showHomeFragment();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        final int screenHeight = metrics.heightPixels;
        mGestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (HomeFragment.TAG.equals(mShowFragmentFlag)) {
                    mHomeFragment.hideEditLayoutCheckBoxId();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (touchMode != 1) {
                    touchMode = 1;
                    return false;
                }
                float rotDelta = -distanceX / screenWidth;
                float translateDelta = distanceY / screenHeight;
                mFUP2ARenderer.setRotDelta(rotDelta);
                mFUP2ARenderer.setTranslateDelta(translateDelta);
                return distanceX != 0 || translateDelta != 0;
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (touchMode != 2) {
                    touchMode = 2;
                    return false;
                }
                float scale = detector.getScaleFactor() - 1;
                mFUP2ARenderer.setScaleDelta(scale);
                return scale != 0;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraRenderer.openCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraRenderer.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraRenderer.onDestroy();
    }

    private int touchMode = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mScaleGestureDetector.onTouchEvent(event);
        } else if (event.getPointerCount() == 1)
            mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public FUP2ARenderer getFURenderer() {
        return mFUP2ARenderer;
    }

    public CameraRenderer getCameraRenderer() {
        return mCameraRenderer;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateAvatarP2As();
        mFUP2ARenderer.loadAvatar(getShowAvatarP2A());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFUP2ARenderer.onSurfaceCreated();
        mFUP2ARenderer.loadAvatar(getShowAvatarP2A());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, final int width, int height) {
    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight) {
        if (cameraNV21Byte != null) {
            int showMode = mFUP2ARenderer.getShowAvatarMode();
            if (showMode == FUP2ARenderer.SHOW_AVATAR_MODE_P2A && mFUP2ARenderer.isNeedTrackFace()) {
                mFUP2ARenderer.trackFace(cameraNV21Byte, cameraWidth, cameraHeight);
                mCameraRenderer.refreshLandmarks(mFUP2ARenderer.getLandmarksData());
            } else if (showMode == FUP2ARenderer.SHOW_AVATAR_MODE_AR || showMode == FUP2ARenderer.SHOW_AVATAR_MODE_NONE) {
                if (mBaseFragment instanceof TakePhotoFragment) {
                    ((TakePhotoFragment) mBaseFragment).checkPic();
                }
                return mFUP2ARenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
            }
        }
        return mFUP2ARenderer.onDrawFrameAvatar(cameraWidth, cameraHeight);
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
    public void onLoadBodyCompleteListener() {
        if (HomeFragment.TAG.equals(mShowFragmentFlag))
            mHomeFragment.onLoadBodyCompleteListener();
        else if (mBaseFragment instanceof FUP2ARenderer.OnLoadBodyListener)
            ((FUP2ARenderer.OnLoadBodyListener) mBaseFragment).onLoadBodyCompleteListener();
    }

    @Override
    public void onBackPressed() {
        if (mBaseFragment != null) {
            mBaseFragment.backToHome();
            return;
        }
        super.onBackPressed();
    }

    public void showHomeFragment() {
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

        mFUP2ARenderer.setShowAvatarMode(FUP2ARenderer.SHOW_AVATAR_MODE_P2A);
    }

    public void showBaseFragment(String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mHomeFragment != null) {
            transaction.hide(mHomeFragment);
        }
        if (mBaseFragment == null) {
            if (EditFaceFragment.TAG.equals(tag)) {
                mBaseFragment = new EditFaceFragment();
                mFUP2ARenderer.setShowAvatarMode(FUP2ARenderer.SHOW_AVATAR_MODE_FACE);
            } else if (ARFilterFragment.TAG.equals(tag)) {
                mBaseFragment = new ARFilterFragment();
                mFUP2ARenderer.setShowAvatarMode(FUP2ARenderer.SHOW_AVATAR_MODE_AR);
            } else if (TakePhotoFragment.TAG.equals(tag)) {
                mBaseFragment = new TakePhotoFragment();
                mFUP2ARenderer.setShowAvatarMode(FUP2ARenderer.SHOW_AVATAR_MODE_NONE);
            }
            transaction.add(R.id.main_fragment_layout, mBaseFragment);
        } else {
            transaction.show(mBaseFragment);
        }
        mShowFragmentFlag = tag;
        mHomeFragment.setCheckGroupNoId();
        transaction.commit();
    }

    public List<AvatarP2A> getAvatarP2As() {
        return mAvatarP2As;
    }

    public void updateAvatarP2As() {
        List<AvatarP2A> avatarP2AS = mDBHelper.getAllAvatarP2As();
        mAvatarP2As.clear();
        mAvatarP2As.addAll(avatarP2AS);
        setShowIndex(avatarP2AS.contains(mShowAvatarP2A) ? avatarP2AS.indexOf(mShowAvatarP2A) : 0);
        mHomeFragment.notifyDataSetChanged();
    }

    public void setShowIndex(int showIndex) {
        this.mShowAvatarP2A = mAvatarP2As.get(mShowIndex = showIndex);
    }

    public int getShowIndex() {
        return mShowIndex;
    }

    public AvatarP2A getShowAvatarP2A() {
        return mShowAvatarP2A;
    }

    public void setShowAvatarP2A(AvatarP2A showAvatarP2A) {
        mShowIndex = mAvatarP2As.indexOf(mShowAvatarP2A = showAvatarP2A);
    }
}
