//package com.faceunity.pta_art.ui;
//
//import android.content.Context;
//import android.support.constraint.ConstraintLayout;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.TextView;
//
//import com.faceunity.pta_art.R;
//import com.faceunity.pta_art.fragment.BodyDriveFragment;
//
///**
// * Created by jiangyongxing on 2020/4/13.
// * 描述：
// */
//public class DriveBottomComponent extends ConstraintLayout {
//
//    private TextView tvBodyDrive;
//    private TextView tvTextDrive;
//    private TextView tvVoiceDrive;
//    private TextView tvArDrive;
//
//    public DriveBottomComponent(Context context) {
//        super(context);
//    }
//
//    public DriveBottomComponent(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public DriveBottomComponent(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//
//        tvBodyDrive = findViewById(R.id.tv_body_drive);
//        tvTextDrive = findViewById(R.id.tv_text_drive);
//        tvVoiceDrive = findViewById(R.id.tv_voice_drive);
//        tvArDrive = findViewById(R.id.tv_ar_drive);
//        tvBodyDrive.setSelected(true);
//
//        tvBodyDrive.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTvSelected(tvBodyDrive);
//                if (modeSelectedListener != null) {
//                    modeSelectedListener.onModeSelected(BodyDriveFragment.TYPE_BODY_DRIVE);
//                }
//            }
//        });
//        tvTextDrive.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTvSelected(tvTextDrive);
//                if (modeSelectedListener != null) {
//                    modeSelectedListener.onModeSelected(BodyDriveFragment.TYPE_TEXT_DRIVE);
//                }
//            }
//        });
//        tvArDrive.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTvSelected(tvArDrive);
//                if (modeSelectedListener != null) {
//                    modeSelectedListener.onModeSelected(BodyDriveFragment.TYPE_AR_DRIVE);
//                }
//            }
//        });
//    }
//
//    private void setTvSelected(TextView selectedView) {
//        tvBodyDrive.setSelected(selectedView == tvBodyDrive);
//        tvTextDrive.setSelected(selectedView == tvTextDrive);
//        tvVoiceDrive.setSelected(selectedView == tvVoiceDrive);
//        tvArDrive.setSelected(selectedView == tvArDrive);
//    }
//
//
//    public interface ModeSelectedListener {
//        void onModeSelected(int mode);
//    }
//
//    private ModeSelectedListener modeSelectedListener;
//
//    public void setModeSelectedListener(ModeSelectedListener modeSelectedListener) {
//        this.modeSelectedListener = modeSelectedListener;
//    }
//
//    public void setCurrentSelectedMode(int mode) {
//        switch (mode) {
//            case BodyDriveFragment.TYPE_BODY_DRIVE:
//                setTvSelected(tvBodyDrive);
//                break;
//            case BodyDriveFragment.TYPE_TEXT_DRIVE:
//                setTvSelected(tvTextDrive);
//                break;
//            case BodyDriveFragment.TYPE_AR_DRIVE:
//                setTvSelected(tvArDrive);
//                break;
//        }
//    }
//}
