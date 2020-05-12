package com.faceunity.pta_art.fragment.drive;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.driver.ar.AvatarARDriveHandle;
import com.faceunity.pta_art.core.driver.ar.PTAARDriveCore;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.fragment.adapter.DriveAdapter;

/**
 * Created by hyj on 2020-04-28.
 * ar驱动
 */
public class ARFragment extends BaseDriveFragment implements DriveAdapter.OnListener {
    public static final String TAG = ARFragment.class.getSimpleName();
    private ImageView iv_change_camera;
    private TextView tv_ar_drive, tv_text_drive, tv_body_drive;
    //ar模式
    public PTAARDriveCore mP2AARCore;
    private AvatarARDriveHandle mAvatarARHandle;

    @Override
    public int getResource() {
        return R.layout.fragment_ar_drive;
    }

    @Override
    public void initViewData(View view) {
        tv_ar_drive = view.findViewById(R.id.tv_ar_drive);
        tv_text_drive = view.findViewById(R.id.tv_text_drive);
        tv_text_drive.setOnClickListener(this);
        tv_body_drive = view.findViewById(R.id.tv_body_drive);
        tv_body_drive.setOnClickListener(this);
        iv_change_camera = view.findViewById(R.id.iv_change_camera);
        iv_change_camera.setOnClickListener(this);
        tv_ar_drive.setSelected(true);

        mP2AARCore = new PTAARDriveCore(mActivity, mFUP2ARenderer);
        mP2AARCore.setFace_capture(mP2ACore.face_capture);
        mFUP2ARenderer.setFUCore(mP2AARCore);
        mAvatarARHandle = mP2AARCore.createAvatarARHandle(mAvatarHandle.controllerItem);

        mActivity.setCanClick(false, true);
        mAvatarARHandle.setARAvatar(mActivity.getShowAvatarP2A(), new Runnable() {
            @Override
            public void run() {
                mActivity.setCanClick(true, false);
            }
        });
    }

    @Override
    public DriveAdapter initAdapter() {
        return new DriveAdapter(mActivity, mAvatarP2AS, FilePathFactory.filterBundleRes(), this);
    }

    @Override
    public void initTitleGroupData() {
        mBottomTitleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case 0:
                        adapter.selectStatus(DriveAdapter.STATUS_AR_DRIVE_HEAD);
                        scrollToPosition(adapter.getPosition());
                        break;
                    case 1:
                        adapter.selectStatus(DriveAdapter.STATUS_AR_DRIVE_FILTER);
                        scrollToPosition(adapter.getPosition());
                        break;
                    default:
                        break;
                }
            }
        });
        mBottomTitleGroup.setResStrings(new String[]{"模型", "滤镜"}, new int[]{0, 1}, 0);
        adapter.setDefaultIndex(DriveAdapter.STATUS_AR_DRIVE_HEAD, mActivity.getShowIndex());
    }

    @Override
    public void onClick(int id) {
        switch (id) {
            /**
             * 切换相机
             */
            case R.id.iv_change_camera:
                mCameraRenderer.changeCamera();
                break;
            case R.id.tv_body_drive:
                if (mP2AARCore != null) {
                    mP2AARCore.release();
                    mP2AARCore = null;
                }
                mP2ACore.bindDefault();
                mActivity.showBaseFragment(BodyDriveFragment.TAG);
                break;
            case R.id.tv_text_drive:
                if (mP2AARCore != null) {
                    mP2AARCore.release();
                    mP2AARCore = null;
                }
                mActivity.setIsAR(true);
                mP2ACore.bindDefault();
                mActivity.showBaseFragment(TextDriveFragment.TAG);
                break;
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mRecyclerView.getVisibility() == View.VISIBLE) {
            setViewShowOrHide(false);
        } else {
            setViewShowOrHide(true);
        }
        return true;
    }


    /**
     * 退到首页前的准备
     */
    @Override
    public void backToHome() {
        if (mP2AARCore != null) {
            mP2AARCore.release();
            mP2AARCore = null;
        }
        mP2ACore.bind();
        mFUP2ARenderer.setFUCore(mP2ACore);
    }

    @Override
    public void onClickHead(int pos, AvatarPTA avatarPTA) {
        mActivity.setCanClick(false, true);
        mAvatarARHandle.setARAvatar(avatarPTA, new Runnable() {
            @Override
            public void run() {
                mActivity.setCanClick(true, false);
            }
        });
        adapter.notifySelectItemChanged(pos);
        scrollToPosition(pos);
    }

    @Override
    public void onClickARFilter(int pos, String path) {
        mAvatarARHandle.setFilter(path);
        adapter.notifySelectItemChanged(pos);
        scrollToPosition(pos);
    }
}
