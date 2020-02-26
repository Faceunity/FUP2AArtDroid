package com.faceunity.pta_art.ui.audio;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faceunity.pta_art.R;

/**
 * 长按录音：Dialog管理类
 * Created by Tryking on 2016/3/24.
 */
public class DialogManager {

    private Context mContext;
    private LinearLayout ll_record_dialog;
    private Dialog mDialog;
    private ImageView mIcon, mCancel;
    private TextView mLable, mTime;
    private long startTime = 0;//录制开始时间

    public DialogManager(Context context) {
        mContext = context;
    }

    /**
     * 展示正在录音的Dialog
     */
    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.AudioDialogTheme);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_audio, null);
        mDialog.setContentView(view);

        ll_record_dialog = mDialog.findViewById(R.id.ll_record_dialog);
        mIcon = mDialog.findViewById(R.id.dialog_icon);
        mCancel = mDialog.findViewById(R.id.cancel_icon);
        mLable = mDialog.findViewById(R.id.dialog_text);
        mTime = mDialog.findViewById(R.id.tv_record_time);

        mDialog.show();
        mTime.setText("00:00");
        mTime.setVisibility(View.VISIBLE);
        mIcon.setVisibility(View.VISIBLE);
        mCancel.setVisibility(View.GONE);
        startTime = 0;
        ll_record_dialog.setBackground(mContext.getResources().getDrawable(R.drawable.dialog_loading_bg));
    }

    /**
     * 设置想要取消的界面
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            ll_record_dialog.setBackground(mContext.getResources().getDrawable(R.drawable.shape_record_cancel));

            mTime.setVisibility(View.GONE);
            mIcon.setVisibility(View.GONE);
            mCancel.setVisibility(View.VISIBLE);
            mLable.setText("松开手指，取消发送");
        }
    }

    /**
     * 设置录制的时间
     *
     * @param time
     */
    public void setTime(long time) {
        if (mTime.getVisibility() == View.VISIBLE) {
            if (startTime == 0) {
                startTime = time;
                mTime.setText("00:00");
            } else {
                mTime.setText(timeToMmSS((time - startTime) / (1000 * 1000)));
            }
        }
    }

    private String timeToMmSS(long time) {
        long sec = time % 60;
        long min = (time / 60) % 60;
        return doubleVaule(min + "") + ":" + doubleVaule(sec + "");
    }

    private String doubleVaule(String time) {
        if (time.length() < 2) {
            return "0" + time;
        } else {
            return time;
        }
    }

    /**
     * 设置隐藏Dialog
     */
    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
