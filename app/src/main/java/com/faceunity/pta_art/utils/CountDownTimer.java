package com.faceunity.pta_art.utils;


import android.text.TextUtils;
import android.util.Log;

/**
 * 倒计时控件
 * Created by hyj on 2019/2/25.
 */

public class CountDownTimer extends android.os.CountDownTimer {

    private String tag;

    public CountDownTimer(long time, long interval) {
        super(time + 50, interval);
    }

    public CountDownTimer(long time, long interval, String tag) {
        super(time + 50, interval);
        this.tag = tag;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (!TextUtils.isEmpty(tag))
            Log.i("ssss", "tag:" + tag + "--time:" + (millisUntilFinished / 1000));
        if (listener != null) {
            listener.onTick("(" + (millisUntilFinished / 1000) + "s)");
        }
    }

    @Override
    public void onFinish() {
        this.cancel();
        if (listener != null)
            listener.onFinish();
    }

    private TimerListener listener;

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    public interface TimerListener {
        void onTick(String second);

        void onFinish();
    }
}
