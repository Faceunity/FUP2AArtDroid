package com.faceunity.pta_art.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.faceunity.pta_art.BuildConfig;
import com.faceunity.pta_art.R;
import com.faceunity.p2a_client.FUP2AClient;

/**
 * Created by tujh on 2018/8/22.
 */
public class HomeFragment extends BaseFragment
        implements View.OnClickListener {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private CheckBox mTrackBtn;

    private View mGuideView;
    private TextView mVersionText;
    private String text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        mActivity.initDebug(mVersionText);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mFUP2ARenderer = mActivity.getFUP2ARenderer();
            mP2ACore = mActivity.getP2ACore();
            mAvatarHandle = mActivity.getAvatarHandle();
            mCameraRenderer = mActivity.getCameraRenderer();
            mAvatarP2AS = mActivity.getAvatarP2As();
            mTrackBtn.setChecked(mCameraRenderer.isShowCamera());
        }
    }

    private void initView(View view) {
        mGuideView = view.findViewById(R.id.main_guide_img);
        mGuideView.bringToFront();
        mGuideView.setVisibility(View.VISIBLE);
        mVersionText = view.findViewById(R.id.main_version_text);

        text = String.format("DigiMe Art v%s\nSDK v%s", BuildConfig.VERSION_NAME, FUP2AClient.getVersion());
        mVersionText.setText(text);
        Log.e(TAG, "FUP2AClient.Version " + FUP2AClient.getVersion());

        mTrackBtn = view.findViewById(R.id.avatar_track_image_btn);
        mTrackBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mP2ACore.setNeedTrackFace(isChecked);
                mAvatarHandle.setAvatar(mActivity.getShowAvatarP2A());
                mCameraRenderer.setShowCamera(isChecked);
            }
        });

        view.findViewById(R.id.main_avatar_image_btn).setOnClickListener(this);
        view.findViewById(R.id.main_edit_image_btn).setOnClickListener(this);
        view.findViewById(R.id.main_ar_filter_image_btn).setOnClickListener(this);
        view.findViewById(R.id.main_group_photo_image_btn).setOnClickListener(this);

    }

//    public void setExpress(float[] express) {
//        String t1 = String.format("%.2f", express[14]);
//        String t2 = String.format("%.2f", express[15]);
//        String t3 = String.format("%.2f", express[42]);
//        Log.e("express", t1 + "--" + t2 + "--" + t3 + "");
//        mVersionText.setTextColor(Color.RED);
//        mVersionText.setText(text + String.format("\nExpression 15:%s 16:%s  43:%s", t1 + "", t2 + "", t3 + ""));
//    }

    public void checkGuide() {
        if (mGuideView.getVisibility() == View.VISIBLE) {
            mGuideView.post(new Runnable() {
                @Override
                public void run() {
                    mGuideView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_avatar_image_btn:
                mActivity.showBaseFragment(AvatarFragment.TAG);
                break;
            case R.id.main_edit_image_btn:
                mActivity.showBaseFragment(EditFaceFragment.TAG);
                mTrackBtn.setChecked(false);
                break;
            case R.id.main_ar_filter_image_btn:
                mActivity.showBaseFragment(ARFilterFragment.TAG);
                mTrackBtn.setChecked(false);
                break;
            case R.id.main_group_photo_image_btn:
                mActivity.showBaseFragment(GroupPhotoFragment.TAG);
                mTrackBtn.setChecked(false);
                break;
        }
    }
}
