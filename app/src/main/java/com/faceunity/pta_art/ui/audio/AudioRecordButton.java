package com.faceunity.pta_art.ui.audio;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AudioRecordButton extends AppCompatButton {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private static final int DISTANCE_Y_CANCEL = 50;
    private int mCurrentState = STATE_NORMAL;
    private DialogManager mDialogManager;
    // 是否触发了onLongClick，准备好了
    private boolean mReady;
    private boolean isRecording = false;
    private boolean isCancel = false;

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startRecording();
                return false;
            }
        });
    }

    //改变状态
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (mCurrentState) {
                case STATE_NORMAL:
                    this.setText("按住 说话");
                    this.setSelected(false);
                    break;
                case STATE_RECORDING:
                case STATE_WANT_TO_CANCEL:
                    this.setText("松开 结束");
                    this.setSelected(true);
                    break;
                default:
                    break;
            }
        }
    }

    private int lastDownX, lastDownY;

    /**
     * 直接复写这个监听函数
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = x;
                lastDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 根据x，y来判断用户是否想要取消
                    if (wantToCancel(x, y)) {
                        mDialogManager.wantToCancel();
                        isCancel = true;
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // 首先判断是否有触发onLongClick事件，没有的话直接返回reset
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (isCancel) {//取消录制
                    if (listener != null) {
                        listener.cancle();
                    }
                } else if (mCurrentState == STATE_RECORDING) {//正常录制结束
                    if (listener != null) {
                        listener.endRecord();
                    }
                }
                reset();// 恢复标志位
                break;
        }
        return super.onTouchEvent(event);
    }

    //开始录音
    private void startRecording() {
        mReady = true;
        mDialogManager.showRecordingDialog();
        isRecording = true;
        changeState(STATE_RECORDING);
        if (listener != null) {
            listener.startRecord();
        }
    }


    /*
     * 恢复标志位以及状态
     */
    private void reset() {
        isCancel = false;
        isRecording = false;
        mDialogManager.dismissDialog();
        changeState(STATE_NORMAL);
        mReady = false;
    }

    private boolean wantToCancel(int x, int y) {
        if (lastDownY - y > DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    public void setTime(long time) {
        mDialogManager.setTime(time);
    }

    private VoiceSendListener listener;

    public void setVoiceSendListener(VoiceSendListener listener) {
        this.listener = listener;
    }

    public interface VoiceSendListener {
        void startRecord();

        void endRecord();

        void cancle();
    }
}
