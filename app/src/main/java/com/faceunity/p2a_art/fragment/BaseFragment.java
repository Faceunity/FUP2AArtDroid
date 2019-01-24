package com.faceunity.p2a_art.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.faceunity.p2a_art.MainActivity;
import com.faceunity.p2a_art.core.AvatarHandle;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.core.P2ACore;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.renderer.CameraRenderer;

import java.util.List;

/**
 * Created by tujh on 2018/10/19.
 */
public class BaseFragment extends Fragment {
    public static final String TAG = BaseFragment.class.getSimpleName();

    protected MainActivity mActivity;
    protected FUP2ARenderer mFUP2ARenderer;
    protected P2ACore mP2ACore;
    protected AvatarHandle mAvatarHandle;
    protected CameraRenderer mCameraRenderer;
    protected List<AvatarP2A> mAvatarP2AS;

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
}
