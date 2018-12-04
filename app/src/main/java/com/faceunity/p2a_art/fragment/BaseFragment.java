package com.faceunity.p2a_art.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.faceunity.p2a_art.MainActivity;
import com.faceunity.p2a_art.core.AvatarP2A;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.renderer.CameraRenderer;

import java.util.List;

/**
 * Created by tujh on 2018/10/19.
 */
public class BaseFragment extends Fragment {
    public static final String TAG = BaseFragment.class.getSimpleName();

    protected MainActivity mActivity;
    protected FUP2ARenderer mFUP2ARenderer;
    protected CameraRenderer mCameraRenderer;
    protected List<AvatarP2A> mAvatarP2AS;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mFUP2ARenderer = mActivity.getFURenderer();
        mCameraRenderer = mActivity.getCameraRenderer();
        mAvatarP2AS = mActivity.getAvatarP2As();
    }

    public void backToHome() {
        mActivity.showHomeFragment();
    }

    public MainActivity getMainActivity() {
        return mActivity;
    }

    public CameraRenderer getCameraRenderer() {
        return mCameraRenderer;
    }
}
