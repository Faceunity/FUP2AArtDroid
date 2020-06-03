package com.faceunity.pta_art.fragment.drive;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.JsonUtils;
import com.faceunity.pta_art.core.driver.text.AvatarTextDriveHandle;
import com.faceunity.pta_art.core.driver.text.PTATextDriveCore;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.fragment.adapter.DriveAdapter;
import com.faceunity.pta_art.utils.KeyboardUtil;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.pta_art.utils.keyboard.KeyboardHeightObserver;
import com.faceunity.pta_art.utils.keyboard.KeyboardHeightProvider;
import com.faceunity.pta_art.utils.sta.AliTtsHandler;
import com.faceunity.pta_art.utils.sta.MediaPlayerHandler;
import com.faceunity.pta_art.utils.sta.player.BaseMediaPlayer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.faceunity.pta_art.utils.sta.player.BaseMediaPlayer.TYPE_PLATFORM;

/**
 * Created by hyj on 2020-04-28.
 * 文字驱动
 */
public class TextDriveFragment extends BaseDriveFragment implements DriveAdapter.OnListener, KeyboardHeightObserver {
    public static final String TAG = TextDriveFragment.class.getSimpleName();

    private ImageView iv_re_back;
    private EditText et_input_text;
    private View v_bg_bottom;

    public PTATextDriveCore mPTATextDriveCore;
    private AvatarTextDriveHandle mAvatarTextHandle;

    private TTSCompletionListener ttsCompletionListener = new TTSCompletionListener();
    private TTSErrorListener ttsErrorListener = new TTSErrorListener();
    private TTSPreparedListener ttsPreparedListener = new TTSPreparedListener(this);
    private List<String[]> mSpeaks;

    @Override
    public int getResource() {
        return R.layout.fragment_text_drive;
    }

    @Override
    public void initViewData(View view) {
        v_bg_bottom = view.findViewById(R.id.v_bg_bottom);
        iv_re_back = view.findViewById(R.id.iv_re_back);
        iv_re_back.setOnClickListener(this);
        et_input_text = view.findViewById(R.id.et_input_text);
        et_input_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_SEND == actionId) {
                    sendText();
                    return true;
                }
                return false;
            }
        });
        setSoftKeyBoardListener();
        initSta();

        mPTATextDriveCore = new PTATextDriveCore(mActivity, mFUP2ARenderer);
        mPTATextDriveCore.setFace_capture(mP2ACore.face_capture);
        mFUP2ARenderer.setFUCore(mPTATextDriveCore);
        mAvatarTextHandle = mPTATextDriveCore.createAvatarHandle(mAvatarHandle.controllerItem);

        mActivity.setCanClick(false, true);
        AvatarPTA avatarPTA = mActivity.getCurrentDrivenAvatar() == null ? mActivity.getShowAvatarP2A() : mActivity.getCurrentDrivenAvatar();
        mAvatarTextHandle.setAvatarForVoice(avatarPTA, new Runnable() {
            @Override
            public void run() {
                mActivity.setCanClick(true, false);
            }
        });
    }

    @Override
    public DriveAdapter initAdapter() {
        return new DriveAdapter(mActivity, mAvatarP2AS, mSpeaks.get(0), mSpeaks.get(1), this);
    }

    @Override
    public void initTitleGroupData() {
        mBottomTitleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case 0:
                        adapter.selectStatus(DriveAdapter.STATUS_TEXT_DRIVE_HEAD);
                        scrollToPosition(adapter.getPosition());
                        break;
                    case 1:
                        adapter.selectStatus(DriveAdapter.STATUS_TEXT_DRIVE_TONE);
                        scrollToPosition(adapter.getPosition());
                        break;
                    default:
                        break;
                }
            }
        });
        mBottomTitleGroup.setResStrings(new String[]{"模型", "音色"}, new int[]{0, 1}, 0);
        adapter.setDefaultIndex(DriveAdapter.STATUS_TEXT_DRIVE_HEAD, mActivity.getDrivenAvatarShowIndex());
    }

    @Override
    public void backToHome() {
        KeyboardUtil.hideKeyboard(mActivity, et_input_text);
        mPTATextDriveCore.stopPlay();
        mPTATextDriveCore.resetBlendExpression();
        if (mPTATextDriveCore != null) {
            mPTATextDriveCore.release();
            mPTATextDriveCore = null;
        }
        mP2ACore.bind();
        mP2ACore.unBindDefault();
        mFUP2ARenderer.setFUCore(mP2ACore);
        mActivity.setCurrentDrivenAvatar(null);
    }

    @Override
    public void onClick(int id) {
        switch (id) {
            case R.id.iv_re_back:
                KeyboardUtil.hideKeyboard(mActivity, et_input_text);
                mPTATextDriveCore.stopPlay();
                mPTATextDriveCore.resetBlendExpression();
                if (mPTATextDriveCore != null) {
                    mPTATextDriveCore.release();
                    mPTATextDriveCore = null;
                }
                //左下角返回
                if (mActivity.getIsAR()) {
                    mP2ACore.unBindDefault();
                    mActivity.showBaseFragment(ARFragment.TAG);
                } else {
                    mActivity.showBaseFragment(BodyDriveFragment.TAG);
                }
                break;
        }
    }

    //*********************************sta文字处理******************************//
    private String mText;
    private AliTtsHandler mAliTtsHandler;
    private AliTtsHandler.OnTtsCallback mAliTtsCallback;
    private List<float[]> mExpressionList;
    private MediaPlayerHandler mMediaPlayerHandler;//播放音频

    private void initSta() {
        mAliTtsHandler = new AliTtsHandler();
        mAliTtsCallback = new AliTtsCallback(this);
        mMediaPlayerHandler = new MediaPlayerHandler(mActivity);
        mExpressionList = new ArrayList<>();
        mMediaPlayerHandler.initPlayer(TYPE_PLATFORM, ttsPreparedListener, ttsCompletionListener, ttsErrorListener);

        JsonUtils jsonUtils = new JsonUtils();
        mSpeaks = jsonUtils.readSta("sta/config.json");

    }

    private void sendText() {
        mText = et_input_text.getText().toString();
        if (!TextUtils.isEmpty(mText)) {
            et_input_text.setText("");
            KeyboardUtil.hideKeyboard(mActivity, et_input_text);
            mAliTtsHandler.configTts(adapter.getToneId());
            mAliTtsHandler.start(mText, mAliTtsCallback);
        }
    }

    private static class AliTtsCallback implements AliTtsHandler.OnTtsCallback {

        private final WeakReference<TextDriveFragment> textDriveFragmentWeakReference;

        public AliTtsCallback(TextDriveFragment textDriveFragment) {
            textDriveFragmentWeakReference = new WeakReference<>(textDriveFragment);
        }

        @Override
        public void onSuccess(File wavFile, List<float[]> expressionList) {
            TextDriveFragment textDriveFragment = textDriveFragmentWeakReference.get();
            if (textDriveFragment != null) {

                textDriveFragment.mExpressionList.clear();
                textDriveFragment.mExpressionList.addAll(expressionList);
                textDriveFragment.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textDriveFragment.mMediaPlayerHandler.setDataSource(wavFile.getAbsolutePath());
                    }
                });
            }
        }

        @Override
        public void onFailure(String error) {
            TextDriveFragment textDriveFragment = textDriveFragmentWeakReference.get();
            if (textDriveFragment != null) {
                textDriveFragment.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showCenterToast(textDriveFragment.mActivity, error);
                    }
                });
            }

        }
    }


    /****************************************键盘相关*******************************/
    @Override
    public void onPause() {
        super.onPause();
        if (mPTATextDriveCore != null) {
            mPTATextDriveCore.stopPlay();
            mPTATextDriveCore.resetBlendExpression();
        }
        mMediaPlayerHandler.stopMediaPlayer();
        mKeyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mKeyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAliTtsHandler.release();
    }

    @Override
    public void onDestroy() {
        mKeyboardHeightProvider.close();
        super.onDestroy();
    }

    private KeyboardHeightProvider mKeyboardHeightProvider;

    /**
     * 添加软键盘监听
     */
    private void setSoftKeyBoardListener() {
        mKeyboardHeightProvider = new KeyboardHeightProvider(mActivity);
        et_input_text.post(new Runnable() {
            @Override
            public void run() {
                mKeyboardHeightProvider.start();
            }
        });
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        //软键盘已经显示，做逻辑
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) v_bg_bottom.getLayoutParams();
        layoutParams.bottomMargin = height;
        v_bg_bottom.setLayoutParams(layoutParams);

        if (height > 0) {
            setViewShowOrHide(false);
        }
    }

    @Override
    public void onClickHead(int pos, AvatarPTA avatarPTA) {
        mActivity.setCanClick(false, true);
        mActivity.setCurrentDrivenAvatar(avatarPTA);
        mAvatarTextHandle.setAvatarForVoice(avatarPTA, new Runnable() {
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

    }

    @Override
    public void onClickTone(String toneId) {
        JsonUtils jsonUtils = new JsonUtils();

        mExpressionList.clear();
        mExpressionList.addAll(jsonUtils.readStaExpression("sta/" + toneId + ".json"));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AssetManager am = mActivity.getAssets();
                try {
                    AssetFileDescriptor afd = am.openFd("sta/" + toneId + ".mp3");
                    mMediaPlayerHandler.setDataSource(afd);
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        });
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if (((ConstraintLayout.LayoutParams) v_bg_bottom.getLayoutParams()).bottomMargin > 0) {// 键盘显示，需要先隐藏键盘
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(et_input_text.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }
        if (mRecyclerView.getVisibility() == View.VISIBLE) {
            setViewShowOrHide(false);
        } else {
            setViewShowOrHide(true);
        }
        return true;
    }


    public static class TTSCompletionListener implements BaseMediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(BaseMediaPlayer player) {
            Log.i(TAG, "onCompletion() called");
        }
    }


    public static class TTSErrorListener implements BaseMediaPlayer.OnErrorListener {

        @Override
        public void onError(BaseMediaPlayer player, int type, String message) {
            Log.e(TAG, "onError() called with: type = [" + type + "], message = [" + message + "]");

        }
    }

    public static class TTSPreparedListener implements BaseMediaPlayer.OnPreparedListener {

        private final WeakReference<TextDriveFragment> bodyDriveFragmentWeakReference;

        public TTSPreparedListener(TextDriveFragment textDriveFragment) {
            bodyDriveFragmentWeakReference = new WeakReference<>(textDriveFragment);
        }

        @Override
        public void onPrepared(BaseMediaPlayer player) {
            Log.i(TAG, "onPrepared() called");
            TextDriveFragment textDriveFragment = bodyDriveFragmentWeakReference.get();
            if (textDriveFragment != null && textDriveFragment.mPTATextDriveCore != null && textDriveFragment.mExpressionList != null) {
                textDriveFragment.mPTATextDriveCore.startPlay(textDriveFragment.mExpressionList);
            }
        }
    }


}
