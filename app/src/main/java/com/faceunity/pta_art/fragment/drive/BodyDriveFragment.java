package com.faceunity.pta_art.fragment.drive;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.driver.body.AvatarBodyHandle;
import com.faceunity.pta_art.core.driver.body.PTABodyCore;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.fragment.VideoListFragment;
import com.faceunity.pta_art.fragment.adapter.DriveAdapter;
import com.faceunity.pta_art.ui.BodyDriveSettingComponent;
import com.faceunity.pta_art.ui.SwitchButton;
import com.faceunity.pta_art.utils.SmallCameraPositionManager;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.pta_art.utils.eventbus.FuEventBus;
import com.faceunity.pta_art.utils.eventbus.Subscribe;
import com.faceunity.pta_art.utils.eventbus.ThreadMode;
import com.faceunity.pta_art.utils.eventbus.event.MediaEvent;

/**
 * Created by hyj on 2020-04-28.
 * 身体驱动
 */
public class BodyDriveFragment extends BaseDriveFragment implements DriveAdapter.OnListener {
    public static final String TAG = BodyDriveFragment.class.getSimpleName();

    private TextView tv_body_drive;
    private ImageButton iv_change_camera;
    //身体模式
    public PTABodyCore mP2ABodyCore;
    private AvatarBodyHandle mAvatarBodyHandle;

    private BodyDriveSettingComponent bodyDriveSettingComponent;
    private SmallCameraPositionManager smallCameraPositionManager;

    @Override
    public int getResource() {
        return R.layout.fragment_body_drive;
    }

    @Override
    public void initViewData(View view) {
        FuEventBus.getDefault().register(this);

        view.findViewById(R.id.tv_text_drive).setOnClickListener(this);
        view.findViewById(R.id.tv_ar_drive).setOnClickListener(this);
        tv_body_drive = view.findViewById(R.id.tv_body_drive);
        iv_change_camera = view.findViewById(R.id.iv_change_camera);
        iv_change_camera.setOnClickListener(this);
        tv_body_drive.setSelected(true);

        // 身体驱动的布局
        bodyDriveSettingComponent = view.findViewById(R.id.body_drive_setting_view);
        bodyDriveSettingComponent.setOnSwitchCheckedChangeListener(
                new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                        if (mP2ABodyCore == null) {
                            return;
                        }
                        mP2ABodyCore.enterFaceDrive(isChecked);
                        mCameraRenderer.setVideoLandmarks(isChecked);
                    }
                }, new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                        bodyDriveSettingComponent.setBodyDriveTextViewChanged(isChecked);
                        mAvatarBodyHandle.bodyFullMode(!isChecked, mRecyclerView.getVisibility() == View.VISIBLE);
                    }
                }, null);

        mP2ABodyCore = new PTABodyCore(mActivity, mFUP2ARenderer);
        mP2ABodyCore.setFace_capture(mP2ACore.face_capture);
        mP2ACore.unBindAndBindDefault();
        mFUP2ARenderer.setFUCore(mP2ABodyCore);
        mAvatarBodyHandle = mP2ABodyCore.createAvatarBodyHandle(mAvatarHandle.controllerItem);

        smallCameraPositionManager = mCameraRenderer.getmSmallCameraPositionManager();
        smallCameraPositionManager.setBottomMargin(getResources().getDimensionPixelSize(R.dimen.x342));

        mActivity.setCanClick(false, true);
        mAvatarBodyHandle.setAvatar(mActivity.getShowAvatarP2A(), new Runnable() {
            @Override
            public void run() {
                smallCameraPositionManager.setBodyDrivenScene(true);
                mAvatarBodyHandle.bodyFullMode(false, true);
                mCameraRenderer.setBodyDrive(true);
                mCameraRenderer.setVideoLandmarks(true);
                mActivity.setCanClick(true, false);
            }
        });

    }

    @Override
    public DriveAdapter initAdapter() {
        return new DriveAdapter(mActivity, mAvatarP2AS, FilePathFactory.bodyInput(), this);
    }

    @Override
    public void initTitleGroupData() {
        mBottomTitleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case 0:
                        adapter.selectStatus(DriveAdapter.STATUS_BODY_DRIVE_HEAD);
                        scrollToPosition(adapter.getPosition());
                        break;
                    case 1:
                        adapter.selectStatus(DriveAdapter.STATUS_BODY_DRIVE_INPUT);
                        scrollToPosition(adapter.getPosition());
                        break;
                    default:
                        break;
                }
            }
        });
        mBottomTitleGroup.setResStrings(new String[]{"模型", "输入"}, new int[]{0, 1}, 0);
        adapter.setDefaultIndex(DriveAdapter.STATUS_BODY_DRIVE_HEAD, mActivity.getShowIndex());
    }

    @Override
    public void backToHome() {
        onLeave();
        mP2ACore.bind();
        mP2ACore.unBindDefault();
        mFUP2ARenderer.setFUCore(mP2ACore);
    }

    @Override
    public void onClick(int id) {
        switch (id) {
            case R.id.tv_ar_drive:
                onLeave();
                mP2ACore.unBindDefault();
                mActivity.showBaseFragment(ARFragment.TAG);
                break;
            case R.id.tv_text_drive:
                onLeave();
                mActivity.setIsAR(false);
                mActivity.showBaseFragment(TextDriveFragment.TAG);
                break;
            case R.id.iv_change_camera:
                if (mCameraRenderer.isShowVideo()) {
                    ToastUtil.showCenterToast(mActivity, "当前模式不支持旋转相机");
                    return;
                }
                mCameraRenderer.changeCamera();
                break;
        }
    }

    public void onLeave() {
        mCameraRenderer.setVideoPath("");
        smallCameraPositionManager.setBodyDrivenScene(false);
        mCameraRenderer.setBodyDrive(false);
        mCameraRenderer.setVideoLandmarks(false);
        mAvatarBodyHandle.bodyFullMode(false, false);
        if (mP2ABodyCore != null) {
            mP2ABodyCore.release();
            mP2ABodyCore = null;
        }
    }

    @Override
    public void onClickHead(int pos, AvatarPTA avatarPTA) {
        mActivity.setCanClick(false, true);
        mAvatarBodyHandle.setAvatar(avatarPTA, new Runnable() {
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
        switch (pos) {
            case 0:
                //实时视频
                mCameraRenderer.setVideoPath("");
                break;
            case 1:
                //选择视频
                mActivity.showBaseFragment(VideoListFragment.TAG);
                break;
            case 2:
                mCameraRenderer.setVideoPath("");
                //视频
                mCameraRenderer.setVideoPath(path);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onMediaBack(MediaEvent mediaEvent) {
        if (!TextUtils.isEmpty(mediaEvent.getMediaEvent())) {
            adapter.notifySelectItemChanged(1);
            mCameraRenderer.setVideoPath("");
            //视频
            mCameraRenderer.setVideoPath(mediaEvent.getMediaEvent());
        }
    }


    private void hideBottom() {
        setViewShowOrHide(false);
        smallCameraPositionManager.setBottomMargin(getResources().getDimensionPixelSize(R.dimen.x132));
    }

    private void showBottom() {
        setViewShowOrHide(true);
        smallCameraPositionManager.setBottomMargin(getResources().getDimensionPixelSize(R.dimen.x342));
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mRecyclerView.getVisibility() == View.VISIBLE) {
            hideBottom();
            changeModelWhenHideBottom();
        } else {
            showBottom();
            changeModelWhenShowBottom();
        }
        return true;
    }

    private void changeModelWhenShowBottom() {
        if (bodyDriveSettingComponent.bodyDriveIsChecked()) {
            // 全身
            mAvatarBodyHandle.resetFullBodyScreenWithBottomView();
        } else {
            // 半身
            mAvatarBodyHandle.resetHalfBodyScreenWithBottomView();
        }
    }

    private void changeModelWhenHideBottom() {
        if (bodyDriveSettingComponent.bodyDriveIsChecked()) {
            // 全身
            mAvatarBodyHandle.resetFullBodyScreenWithoutBottomView();
        } else {
            // 半身
            mAvatarBodyHandle.resetHalfBodyScreenWithoutBottomView();
        }
    }


    @Override
    public void onDestroy() {
        FuEventBus.getDefault().unRegister(this);
        super.onDestroy();
    }
}
