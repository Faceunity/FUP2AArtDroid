package com.faceunity.pta_art.ui;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.pta_art.R;

/**
 * Created by jiangyongxing on 2020/4/7.
 * 描述：
 */
public class BodyDriveSettingComponent extends ConstraintLayout {


    private SwitchButton faceDriveSb;
    private TextView bodyDriveTv;
    private SwitchButton bodyDriveSb;
    //private SwitchButton followingSb;

    public BodyDriveSettingComponent(Context context) {
        super(context);
    }

    public BodyDriveSettingComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BodyDriveSettingComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ImageView showSettingView = findViewById(R.id.body_drive_setting_show_view);
        View settingViewBg = findViewById(R.id.body_drive_setting_bg_view);
        TextView faceDriveTv = findViewById(R.id.body_drive_setting_face_drive_tv);
        faceDriveSb = findViewById(R.id.body_drive_setting_face_drive_sb);
        bodyDriveTv = findViewById(R.id.body_drive_setting_body_drive_tv);
        bodyDriveSb = findViewById(R.id.body_drive_setting_body_drive_sb);
        //TextView followingTv = findViewById(R.id.body_drive_setting_following_tv);
        //followingSb = findViewById(R.id.body_drive_setting_following_sb);

        showSettingView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingViewBg.getVisibility() == VISIBLE) {
                    settingViewBg.setVisibility(INVISIBLE);
                    faceDriveTv.setVisibility(INVISIBLE);
                    faceDriveSb.setVisibility(INVISIBLE);
                    bodyDriveTv.setVisibility(INVISIBLE);
                    bodyDriveSb.setVisibility(INVISIBLE);
                    //followingTv.setVisibility(INVISIBLE);
                    //followingSb.setVisibility(INVISIBLE);
                } else {
                    settingViewBg.setVisibility(VISIBLE);
                    faceDriveTv.setVisibility(VISIBLE);
                    faceDriveSb.setVisibility(VISIBLE);
                    bodyDriveTv.setVisibility(VISIBLE);
                    bodyDriveSb.setVisibility(VISIBLE);
                    //followingTv.setVisibility(VISIBLE);
                    //followingSb.setVisibility(VISIBLE);
                }
            }
        });

        showSettingView.performClick();
    }

    public void setOnSwitchCheckedChangeListener(SwitchButton.OnCheckedChangeListener faceDriveListener,
                                                 SwitchButton.OnCheckedChangeListener bodyDriveListener,
                                                 SwitchButton.OnCheckedChangeListener followingListener) {

        faceDriveSb.setOnCheckedChangeListener(faceDriveListener);
        bodyDriveSb.setOnCheckedChangeListener(bodyDriveListener);
        //followingSb.setOnCheckedChangeListener(followingListener);
    }


    public boolean faceDriveIsChecked() {
        return faceDriveSb.isChecked();
    }

    public boolean bodyDriveIsChecked() {
        return bodyDriveSb.isChecked();
    }

//    public boolean followingIsChecked() {
//        return followingSb.isChecked();
//    }

    public void setFaceDriveChecked(boolean checked, boolean needAni) {
        faceDriveSb.setCheck(checked, needAni);
    }

    public void setFaceDriveChecked(boolean checked) {
        faceDriveSb.setChecked(checked);
    }

    public void setBodyDriveChecked(boolean checked, boolean needAni) {
        bodyDriveSb.setCheck(checked, needAni);
    }

    public void setBodyDriveChecked(boolean checked) {
        bodyDriveSb.setChecked(checked);
    }

//    public void setFollowingChecked(boolean checked, boolean needAni) {
//        followingSb.setCheck(checked, needAni);
//    }
//
//    public void setFollowingChecked(boolean checked) {
//        followingSb.setChecked(checked);
//    }

    public void setBodyDriveTextViewChanged(boolean isChecked) {
        if (isChecked) {
            bodyDriveTv.setText("全身驱动");
        } else {
            bodyDriveTv.setText("半身驱动");
        }
    }
}
