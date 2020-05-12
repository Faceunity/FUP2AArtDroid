package com.faceunity.pta_art.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.faceunity.pta_art.MainActivity;
import com.faceunity.pta_art.core.AvatarHandle;
import com.faceunity.pta_art.core.FUPTARenderer;
import com.faceunity.pta_art.core.PTACore;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.renderer.CameraRenderer;

import java.util.List;

/**
 * Created by tujh on 2018/10/19.
 */
public class BaseFragment extends Fragment {
    public static final String TAG = BaseFragment.class.getSimpleName();

    protected MainActivity mActivity;
    protected FUPTARenderer mFUP2ARenderer;
    protected PTACore mP2ACore;
    protected AvatarHandle mAvatarHandle;
    protected CameraRenderer mCameraRenderer;
    protected List<AvatarPTA> mAvatarP2AS;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mFUP2ARenderer = mActivity.getFUP2ARenderer();
        mP2ACore = mActivity.getP2ACore();
        mAvatarHandle = mActivity.getAvatarHandle();
        mCameraRenderer = mActivity.getCameraRenderer();
        mAvatarP2AS = mActivity.getAvatarP2As();
    }

    public void onBackPressed() {
        mActivity.showHomeFragment();
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public MainActivity getMainActivity() {
        return mActivity;
    }

    public CameraRenderer getCameraRenderer() {
        return mCameraRenderer;
    }

    public void setmP2ACore(PTACore mP2ACore) {
        this.mP2ACore = mP2ACore;
    }
}
