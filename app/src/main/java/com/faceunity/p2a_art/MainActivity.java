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
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.faceunity.p2a_art.core.AvatarHandle;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.core.P2ACore;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.DBHelper;
import com.faceunity.p2a_art.fragment.ARFilterFragment;
import com.faceunity.p2a_art.fragment.BaseFragment;
import com.faceunity.p2a_art.fragment.EditFaceFragment;
import com.faceunity.p2a_art.fragment.GroupPhotoFragment;
import com.faceunity.p2a_art.fragment.HomeFragment;
import com.faceunity.p2a_art.fragment.TakePhotoFragment;
import com.faceunity.p2a_art.renderer.CameraRenderer;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements
        CameraRenderer.OnCameraRendererStatusListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private View mGroupPhotoRound;
    private GLSurfaceView mGLSurfaceView;
    private CameraRenderer mCameraRenderer;
    private FUP2ARenderer mFUP2ARenderer;
    private P2ACore mP2ACore;
    private AvatarHandle mAvatarHandle;

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

        mGroupPhotoRound = findViewById(R.id.group_photo_round);
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
        mP2ACore = new P2ACore(this, mFUP2ARenderer);
        mFUP2ARenderer.setFUCore(mP2ACore);
        mAvatarHandle = mP2ACore.createAvatarHandle();
        mAvatarHandle.setAvatar(getShowAvatarP2A(), new Runnable() {
            @Override
            public void run() {
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
                float rotDelta = -distanceX / screenWidth;
                float translateDelta = distanceY / screenHeight;
                mAvatarHandle.setRotDelta(rotDelta);
                mAvatarHandle.setTranslateDelta(translateDelta);
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
                mAvatarHandle.setScaleDelta(scale);
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
        if (HomeFragment.TAG.equals(mShowFragmentFlag) || EditFaceFragment.TAG.equals(mShowFragmentFlag)) {
            if (event.getPointerCount() == 2) {
                mScaleGestureDetector.onTouchEvent(event);
            } else if (event.getPointerCount() == 1)
                mGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public FUP2ARenderer getFUP2ARenderer() {
        return mFUP2ARenderer;
    }

    public P2ACore getP2ACore() {
        return mP2ACore;
    }

    public AvatarHandle getAvatarHandle() {
        return mAvatarHandle;
    }

    public CameraRenderer getCameraRenderer() {
        return mCameraRenderer;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateAvatarP2As();
        mAvatarHandle.setAvatar(getShowAvatarP2A());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFUP2ARenderer.onSurfaceCreated();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, final int width, int height) {
    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight) {
        mCameraRenderer.refreshLandmarks(mP2ACore.getLandmarksData());
        return mFUP2ARenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
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
            mBaseFragment.onBackPressed();
            return;
        }
        super.onBackPressed();
    }

    public void showHomeFragment() {
        if (mCameraRenderer.getCurrentCameraType() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraRenderer.updateMTX();
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
        mAvatarHandle.resetAllMin();
    }

    public void showBaseFragment(String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mHomeFragment != null) {
            transaction.hide(mHomeFragment);
        }
        if (mBaseFragment == null) {
            if (EditFaceFragment.TAG.equals(tag)) {
                mBaseFragment = new EditFaceFragment();
            } else if (ARFilterFragment.TAG.equals(tag)) {
                mBaseFragment = new ARFilterFragment();
            } else if (TakePhotoFragment.TAG.equals(tag)) {
                mBaseFragment = new TakePhotoFragment();
            } else if (GroupPhotoFragment.TAG.equals(tag)) {
                mBaseFragment = new GroupPhotoFragment();
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

    public void setGLSurfaceViewSize(boolean isMin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGLSurfaceView.getLayoutParams();
        params.width = isMin ? getResources().getDimensionPixelSize(R.dimen.x480) : RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = isMin ? getResources().getDimensionPixelSize(R.dimen.x592) : RelativeLayout.LayoutParams.MATCH_PARENT;
        params.topMargin = isMin ? getResources().getDimensionPixelSize(R.dimen.x158) : 0;
        mGLSurfaceView.setLayoutParams(params);
        mGroupPhotoRound.setVisibility(isMin ? View.VISIBLE : View.GONE);
    }
}
