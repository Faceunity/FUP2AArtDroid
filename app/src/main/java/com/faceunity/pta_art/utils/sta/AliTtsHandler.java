package com.faceunity.pta_art.utils.sta;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.FUTtsEngine;
import com.faceunity.PrepareOptions;
import com.faceunity.TtsOptions;
import com.faceunity.futtsexp.TtsCallback;
import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.utils.DateUtil;
import com.faceunity.pta_art.utils.sta.audio.PcmToWav;

import java.io.File;
import java.util.List;

/**
 * @author luteng on 2019/10/10 17:51
 */
public class AliTtsHandler {
    private String mText;
    private AliTtsCallback aliTtsCallback;
    private OnTtsCallback mOnTtsCallback;
    private FUTtsEngine fuTtsEngine;
    private String voice = "Siqi";

    public AliTtsHandler() {
        aliTtsCallback = new AliTtsCallback();
        initTts();
    }

    private void initTts() {
        // 创建tts实例并初始化
        fuTtsEngine = TtsEngineUtils.getInstance().getFUStaEngine();
        PrepareOptions popts = new PrepareOptions();
        //TODO #error 请联系Faceunity获取tts地址
        popts.setHost(/*"http://xxxxxxxx.com"*/);
        popts.setPort(80);
        popts.setBranch(/*"/xxxxxxxx"*/);
        fuTtsEngine.prepare(popts);
    }

    public void configTts(String speaker) {
        if (fuTtsEngine == null) {
            initTts();
        }
        voice = speaker;
    }

    public void start(@NonNull String text, @NonNull OnTtsCallback onTtsCallback) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (fuTtsEngine == null) {
            initTts();
        }
        mText = text;
        TtsOptions ttsOptions = new TtsOptions();
        ttsOptions.setIdentity("1");
        ttsOptions.setWord(text);
        ttsOptions.setVoice(voice);
        ttsOptions.setFormat("pcm");
        ttsOptions.setLanguage("chinese");
        ttsOptions.setVolume(0.5f);
        ttsOptions.setSpeed(1);
        ttsOptions.setSampleRate(16000);
        ttsOptions.setEncode("Buffer");
        mOnTtsCallback = onTtsCallback;
        fuTtsEngine.speak(ttsOptions, aliTtsCallback);
    }

    public void cancel() {
        if (fuTtsEngine != null) {
            fuTtsEngine.cancel();
        }
    }

    public void release() {
        if (fuTtsEngine != null) {
            fuTtsEngine.release();
            fuTtsEngine = null;
        }
    }

    public interface OnTtsCallback {
        /**
         * 成功
         *
         * @param aacFile
         * @param expressionList
         */
        void onSuccess(File aacFile, List<float[]> expressionList);

        /**
         * 失败
         *
         * @param error
         */
        void onFailure(String error);
    }

    private long startTTS;
    private long endTTS;

    // 下面几个回调都在工作线程
    private class AliTtsCallback implements TtsCallback {

        @Override
        public void onStart(String identity) {
//            logger.debug("onStart. identity:{}", identity);
            startTTS = System.currentTimeMillis();
        }

        @Override
        public void onComplete(String identity, byte[] data, List<float[]> Expression) {
//            logger.verbose("onComplete. identity:{}, data length:{}", identity, data != null ? data.length : 0);
//            mOnTtsCallback.onSuccess(data, Expression);
            endTTS = System.currentTimeMillis();
//            Log.i("Expression:", Expression.size() + ":size");
            String date = DateUtil.getCurrentDate();
            File wavFile = new File(Constant.TmpPath, date + "_temp.wav");
            if (!wavFile.getParentFile().exists()) {
                wavFile.getParentFile().mkdirs();
            }
            PcmToWav.makePcmStreamToWavFile(data, wavFile.getAbsolutePath());
            Log.i("timeTest", "ttsTime=" + (endTTS - startTTS));
//            mOnTtsCallback.onSuccess(wavFile, Expression);
            mOnTtsCallback.onSuccess(wavFile, Expression);
        }

        @Override
        public void onCancel(String identity) {
//            logger.debug("onCancel. identity:{}", identity);
        }

        @Override
        public void onError(String identity, String msg) {
//            logger.debug("onCancel. identity:{}, code:{}", identity, msg);
            mOnTtsCallback.onFailure(msg);
        }
    }
}
