package com.faceunity.pta_art.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.AvatarARHandle;
import com.faceunity.pta_art.core.PTAARCore;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.ui.BottomTitleGroup;
import com.faceunity.pta_art.utils.KeyboardUtil;
import com.faceunity.pta_art.utils.ToastUtil;
import com.faceunity.pta_art.utils.keyboard.KeyboardHeightObserver;
import com.faceunity.pta_art.utils.keyboard.KeyboardHeightProvider;
import com.faceunity.pta_art.utils.sta.AliTtsHandler;
import com.faceunity.pta_art.utils.sta.MediaPlayerHandler;
import com.faceunity.pta_art.utils.sta.player.BaseMediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.faceunity.pta_art.utils.sta.player.BaseMediaPlayer.TYPE_PLATFORM;

/**
 * 驱动
 * Created by tujh on 2018/8/22.
 */
public class BodyDriveFragment extends BaseFragment
        implements View.OnClickListener, BaseMediaPlayer.OnCompletionListener, BaseMediaPlayer.OnErrorListener, BaseMediaPlayer.OnPreparedListener, KeyboardHeightObserver {
    public static final String TAG = BodyDriveFragment.class.getSimpleName();

    private ImageButton mBack, iv_change_camera;
    private View v_bg;
    //列表
    private BottomTitleGroup mBottomTitleGroup;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private BodyDriveAdapter mBodyDriveAdapter;
    //
    private TextView tv_text_drive,
            tv_ar_drive;

    //驱动模式
    public final static int TYPE_TEXT_DRIVE = 1;//文字驱动
    public final static int TYPE_AR_DRIVE = 3;//ar驱动
    private int mode = -1;//当前模式
    private int oldMode = -1;//前一个模式

    private String[] toneList;
    private String[] toneListId;
    //ar模式
    private PTAARCore mP2AARCore;
    private AvatarARHandle mAvatarARHandle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_body_drive, container, false);

        mBack = view.findViewById(R.id.body_filter_back);
        mBack.setOnClickListener(this);
        iv_change_camera = view.findViewById(R.id.iv_change_camera);
        iv_change_camera.setOnClickListener(this);

        v_bg = view.findViewById(R.id.v_bg);
        tv_text_drive = view.findViewById(R.id.tv_text_drive);
        tv_ar_drive = view.findViewById(R.id.tv_ar_drive);
        tv_text_drive.setOnClickListener(this);
        tv_ar_drive.setOnClickListener(this);

        //文本驱动布局
        v_text_bottom = view.findViewById(R.id.v_text_bottom);
        v_text_bottom.setOnClickListener(this);
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
        initSta();

        mRecyclerView = view.findViewById(R.id.body_drive_bottom_recycler);
        mRecyclerView.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mBodyDriveAdapter = new BodyDriveAdapter();
        mRecyclerView.setAdapter(mBodyDriveAdapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mBottomTitleGroup = view.findViewById(R.id.body_drive_bottom_title);
        mBottomTitleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (mode) {
                    //文字驱动
                    case TYPE_TEXT_DRIVE:
                        switch (checkedId) {
                            case 0:
                                mBodyDriveAdapter.selectStatus(mBodyDriveAdapter.STATUS_TEXT_DRIVE_HEAD);
                                scrollToPosition(mBodyDriveAdapter.selectPos[BodyDriveAdapter.STATUS_TEXT_DRIVE_HEAD]);
                                break;
                            case 1:
                                mBodyDriveAdapter.selectStatus(mBodyDriveAdapter.STATUS_TEXT_DRIVE_TONE);
                                scrollToPosition(mBodyDriveAdapter.selectPos[BodyDriveAdapter.STATUS_TEXT_DRIVE_TONE]);
                                break;
                            default:
                                break;
                        }
                        break;
                    //AR驱动
                    case TYPE_AR_DRIVE:
                        switch (checkedId) {
                            case 0:
                                mBodyDriveAdapter.selectStatus(mBodyDriveAdapter.STATUS_AR_DRIVE_HEAD);
                                scrollToPosition(mBodyDriveAdapter.selectPos[BodyDriveAdapter.STATUS_AR_DRIVE_HEAD]);
                                break;
                            case 1:
                                mBodyDriveAdapter.selectStatus(mBodyDriveAdapter.STATUS_AR_DRIVE_FILTER);
                                scrollToPosition(mBodyDriveAdapter.selectPos[BodyDriveAdapter.STATUS_AR_DRIVE_FILTER]);
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
        });
        mP2AARCore = new PTAARCore(mActivity, mFUP2ARenderer);
        mP2AARCore.setFace_capture(mP2ACore.face_capture);
        mP2ACore.unBind();
        mFUP2ARenderer.setFUCore(mP2AARCore);
        mAvatarARHandle = mP2AARCore.createAvatarARHandle(mAvatarHandle.controllerItem);
        selectMode(TYPE_AR_DRIVE);
        setSoftKeyBoardListener();
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.body_filter_back:
                backToHome();
                break;
            //文字驱动
            case R.id.tv_text_drive:
                selectMode(TYPE_TEXT_DRIVE);
                break;
            case R.id.v_text_bottom:
                break;
            case R.id.iv_re_back:
                KeyboardUtil.hideKeyboard(mActivity, et_input_text);
                if (oldMode == -1) {
                    selectMode(TYPE_AR_DRIVE);
                } else {
                    selectMode(oldMode);
                }
                break;
            //ar驱动
            case R.id.tv_ar_drive:
                selectMode(TYPE_AR_DRIVE);
                break;
            //切换相机
            case R.id.iv_change_camera:
                mCameraRenderer.changeCamera();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backToHome();
    }


    private void backToHome() {
        if (mCameraRenderer != null && mCameraRenderer.isChangeCamera()) {
            return;
        }
        KeyboardUtil.hideKeyboard(mActivity, et_input_text);
        mAvatarARHandle.quitArMode();
        mP2AARCore.setNeedTrackFace(false);
        mP2AARCore.enterFaceDrive(false);

        mAvatarARHandle.quitVoiceMode();

        mMediaPlayerHandler.stopMediaPlayer();
        mActivity.showHomeFragment();
        if (mP2AARCore != null) {
            mP2AARCore.release();
            mP2AARCore = null;
        }
        mP2ACore.bind();
        mFUP2ARenderer.setFUCore(mP2ACore);
    }

    public float[] getLandmarksData() {
        float[] landmarksData = new float[150];
        if (mP2AARCore != null) {
            landmarksData = mP2AARCore.getLandmarksData();
        } else {
            Arrays.fill(landmarksData, 0.0f);
        }
        return landmarksData;
    }

    private void selectMode(int mode) {
        if (mP2AARCore == null) {
            return;
        }
        if (this.mode == mode) {
            return;
        }
        mActivity.setCanClick(false, true);
        this.oldMode = this.mode;
        this.mode = mode;
        mP2AARCore.setMode(mode);
        mP2AARCore.stopPlay();
        mP2AARCore.resetBlendExpression();

        switch (oldMode) {
            case TYPE_TEXT_DRIVE:
                mAvatarARHandle.quitVoiceMode();
                break;
            case TYPE_AR_DRIVE:
                mAvatarARHandle.quitArMode();
                mAvatarARHandle.unBindAndDestory(true);
                break;
        }
        if (mode == TYPE_AR_DRIVE) {
            mAvatarARHandle.unBindAndDestory(false);
        }

        if (mode != TYPE_TEXT_DRIVE) {
            tv_text_drive.setSelected(false);
            tv_ar_drive.setSelected(false);
            showTextDriveBottom(false);
        } else if (mode == TYPE_TEXT_DRIVE) {
            showTextDriveBottom(true);
        }

        mAvatarARHandle.setFilter(FilePathFactory.filterBundleRes()[0].path);
        mMediaPlayerHandler.stopMediaPlayer();

        switch (mode) {
            case TYPE_TEXT_DRIVE:
                mP2AARCore.setNeedTrackFace(false);
                mP2AARCore.enterFaceDrive(false);
                iv_change_camera.setVisibility(View.GONE);
                mAvatarARHandle.enterVoiceMode();

                mBottomTitleGroup.setResStrings(new String[]{"模型", "音色"}, new int[]{0, 1}, 0);
                mBodyDriveAdapter.selectPos[mBodyDriveAdapter.STATUS_TEXT_DRIVE_HEAD] = mActivity.getShowIndex();
                mBodyDriveAdapter.selectPos[mBodyDriveAdapter.STATUS_TEXT_DRIVE_TONE] = 0;
                mBottomTitleGroup.setDefaultCheck();

                mAvatarARHandle.resetAll();
                mP2AARCore.setRenderNum(-1);

                mAvatarARHandle.setAvatarForVoice(mActivity.getShowAvatarP2A(), true, new Runnable() {
                    @Override
                    public void run() {
                        mP2AARCore.setRenderNum(0);
                        mActivity.setCanClick(true, false);
                    }
                });
                break;
            case TYPE_AR_DRIVE:
                iv_change_camera.setVisibility(View.VISIBLE);
                mAvatarARHandle.enterArMode();

                tv_ar_drive.setSelected(true);
                mBottomTitleGroup.setResStrings(new String[]{"模型", "滤镜"}, new int[]{0, 1}, 0);
                mBodyDriveAdapter.selectPos[mBodyDriveAdapter.STATUS_AR_DRIVE_HEAD] = mActivity.getShowIndex();
                mBodyDriveAdapter.selectPos[mBodyDriveAdapter.STATUS_AR_DRIVE_FILTER] = 0;
                mBottomTitleGroup.setDefaultCheck();

                mAvatarARHandle.setARAvatar(mActivity.getShowAvatarP2A(), true, new Runnable() {
                    @Override
                    public void run() {
                        mActivity.setCanClick(true, false);
                    }
                });
                mP2AARCore.setNeedTrackFace(true);
                mP2AARCore.enterFaceDrive(true);
                break;
        }
    }

    /**
     * true:显示文字驱动布局
     *
     * @param isShow
     */
    private View v_text_bottom;
    private ImageView iv_re_back;
    private EditText et_input_text;

    private void showTextDriveBottom(boolean isShow) {
        if (isShow) {
            v_text_bottom.setVisibility(View.VISIBLE);
            iv_re_back.setVisibility(View.VISIBLE);
            et_input_text.setVisibility(View.VISIBLE);
        } else {
            v_text_bottom.setVisibility(View.GONE);
            iv_re_back.setVisibility(View.GONE);
            et_input_text.setVisibility(View.GONE);
        }
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
    public void onResume() {
        super.onResume();
        mKeyboardHeightProvider.setKeyboardHeightObserver(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mP2AARCore != null) {
            mP2AARCore.stopPlay();
            mP2AARCore.resetBlendExpression();
        }
        mMediaPlayerHandler.stopMediaPlayer();
        mKeyboardHeightProvider.setKeyboardHeightObserver(null);
    }


    @Override
    public void onDestroy() {
        mKeyboardHeightProvider.close();
        super.onDestroy();
    }


    //*********************************sta文字处理******************************//
    private String mText;
    private AliTtsHandler mAliTtsHandler;
    private AliTtsHandler.OnTtsCallback mAliTtsCallback;
    private List<float[]> expressionList;
    private MediaPlayerHandler mMediaPlayerHandler;//播放音频

    private void initSta() {
        mAliTtsHandler = new AliTtsHandler(mActivity);
        mAliTtsCallback = new AliTtsCallback();
        mMediaPlayerHandler = new MediaPlayerHandler(mActivity);
        expressionList = new ArrayList<>();
        mMediaPlayerHandler.initPlayer(TYPE_PLATFORM, this, this, this);

        toneList = mActivity.getResources().getStringArray(R.array.speakers);
        toneListId = mActivity.getResources().getStringArray(R.array.speakers_id);
    }

    private void sendText() {
        mText = et_input_text.getText().toString();
        if (!TextUtils.isEmpty(mText)) {
            et_input_text.setText("");
            KeyboardUtil.hideKeyboard(mActivity, et_input_text);
            mAliTtsHandler.configTts(toneListId[mBodyDriveAdapter.selectPos[mBodyDriveAdapter.STATUS_TEXT_DRIVE_TONE]]);
            mActivity.initReordTime();
            mAliTtsHandler.start(mText, mAliTtsCallback);
        }
    }

    @Override
    public void onCompletion(BaseMediaPlayer player) {
        Log.i(TAG, "onCompletion() called");
    }

    @Override
    public void onPrepared(BaseMediaPlayer player) {
        Log.i(TAG, "onPrepared() called");
        mP2AARCore.startPlay(expressionList);
    }

    @Override
    public void onError(BaseMediaPlayer player, int type, String message) {
        Log.e(TAG, "onError() called with: type = [" + type + "], message = [" + message + "]");
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        //软键盘已经显示，做逻辑
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) v_text_bottom.getLayoutParams();
        layoutParams.bottomMargin = height;
        v_text_bottom.setLayoutParams(layoutParams);
    }

    private class AliTtsCallback implements AliTtsHandler.OnTtsCallback {

        @Override
        public void onSuccess(File wavFile, List<float[]> expressionList) {
            BodyDriveFragment.this.expressionList.clear();
            BodyDriveFragment.this.expressionList.addAll(expressionList);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMediaPlayerHandler.setDataSource(wavFile.getAbsolutePath());
                }
            });
        }

        @Override
        public void onSuccess(byte[] pcm, List<float[]> expressionList) {
        }

        @Override
        public void onFailure(String error) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showCenterToast(mActivity, error);
                }
            });
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mAliTtsHandler.release();
    }

    class BodyDriveAdapter extends RecyclerView.Adapter<BodyDriveAdapter.BodyDriveHolder> {

        private int selectStatus = STATUS_TEXT_DRIVE_HEAD;
        private static final int STATUS_TEXT_DRIVE_HEAD = 0; //文字驱动--模型
        private static final int STATUS_TEXT_DRIVE_TONE = 1; //文字驱动--音色
        private static final int STATUS_AR_DRIVE_HEAD = 2;   //AR驱动--模型
        private static final int STATUS_AR_DRIVE_FILTER = 3; //AR驱动--滤镜

        private int[] selectPos = {0, 0, 0, 0};

        public void selectStatus(int selectStatus) {
            this.selectStatus = selectStatus;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public BodyDriveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BodyDriveHolder(LayoutInflater.from(mActivity).inflate(R.layout.layout_ar_filter_bottom_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final BodyDriveHolder holder, final int pos) {
            final int position = holder.getLayoutPosition();
            if (selectStatus == STATUS_TEXT_DRIVE_TONE) {
                holder.mItemImg.setVisibility(View.GONE);
                holder.tv_text.setVisibility(View.VISIBLE);
            } else {
                holder.mItemImg.setVisibility(View.VISIBLE);
                holder.tv_text.setVisibility(View.GONE);
                holder.mItemImg.setBackgroundResource(selectPos[selectStatus] == position ? R.drawable.main_item_select : 0);
            }
            final AvatarPTA AvatarP2A;
            switch (selectStatus) {
                //文字驱动
                case STATUS_TEXT_DRIVE_HEAD:
                    AvatarP2A = mAvatarP2AS.get(position);
                    if (AvatarP2A.getOriginPhotoRes() > 0) {
                        holder.mItemImg.setImageResource(AvatarP2A.getOriginPhotoRes());
                    } else {
                        holder.mItemImg.setImageBitmap(BitmapFactory.decodeFile(AvatarP2A.getOriginPhoto()));
                    }
                    holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.setCanClick(false, true);
                            mAvatarARHandle.setAvatarForVoice(AvatarP2A, true, new Runnable() {
                                @Override
                                public void run() {
                                    mActivity.setCanClick(true, false);
                                }
                            });
                            notifySelectItemChanged(position);
                            scrollToPosition(position);
                        }
                    });
                    break;
                case STATUS_TEXT_DRIVE_TONE:
                    holder.tv_text.setText(toneList[position]);
                    if (selectPos[selectStatus] == position) {
                        holder.tv_text.setSelected(true);
                    } else {
                        holder.tv_text.setSelected(false);
                    }
                    holder.tv_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notifySelectItemChanged(position);
                            scrollToPosition(position);
                        }
                    });
                    break;
                //AR驱动
                case STATUS_AR_DRIVE_HEAD:
                    AvatarP2A = mAvatarP2AS.get(position);
                    if (AvatarP2A.getOriginPhotoRes() > 0) {
                        holder.mItemImg.setImageResource(AvatarP2A.getOriginPhotoRes());
                    } else {
                        holder.mItemImg.setImageBitmap(BitmapFactory.decodeFile(AvatarP2A.getOriginPhoto()));
                    }
                    holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.setCanClick(false, true);
                            mAvatarARHandle.setARAvatar(AvatarP2A, true, new Runnable() {
                                @Override
                                public void run() {
                                    mActivity.setCanClick(true, false);
                                }
                            });
                            notifySelectItemChanged(position);
                            scrollToPosition(position);
                        }
                    });
                    break;
                case STATUS_AR_DRIVE_FILTER:
                    if (position > 0) {
                        holder.mItemImg.setImageResource(FilePathFactory.filterBundleRes()[position].resId);
                    } else {
                        holder.mItemImg.setImageResource(selectPos[selectStatus] == position ? R.drawable.ar_filter_item_none_checked : R.drawable.ar_filter_item_none_normal);
                    }
                    holder.mItemImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAvatarARHandle.setFilter(FilePathFactory.filterBundleRes()[position].path);
                            notifySelectItemChanged(position);
                            scrollToPosition(position);
                        }
                    });
                    break;
            }
        }

        public void notifySelectItemChanged(int position) {
            int old = selectPos[selectStatus];
            notifyItemChanged(selectPos[selectStatus] = position);
            notifyItemChanged(old);
        }

        @Override
        public int getItemCount() {
            switch (selectStatus) {
                //文字驱动
                case STATUS_TEXT_DRIVE_HEAD:
                    return mAvatarP2AS.size();
                case STATUS_TEXT_DRIVE_TONE:
                    return toneList.length;
                //AR驱动
                case STATUS_AR_DRIVE_HEAD:
                    return mAvatarP2AS.size();
                case STATUS_AR_DRIVE_FILTER:
                    return FilePathFactory.filterBundleRes().length;
            }
            return 0;
        }

        class BodyDriveHolder extends RecyclerView.ViewHolder {
            ImageView mItemImg;
            TextView tv_text;

            public BodyDriveHolder(View itemView) {
                super(itemView);
                mItemImg = itemView.findViewById(R.id.bottom_item_img);
                tv_text = itemView.findViewById(R.id.tv_text);
            }
        }

    }

    public void scrollToPosition(final int pos) {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int first = mLinearLayoutManager.findFirstVisibleItemPosition();
                int itemW = getResources().getDimensionPixelSize(R.dimen.x140);
                int dx = pos * itemW + itemW / 2 - screenWidth / 2
                        + (first > -1 ? (-first * itemW + mLinearLayoutManager.findViewByPosition(first).getLeft()) : 0);
                mRecyclerView.smoothScrollBy(dx, 0);
            }
        });
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (v_bg.getVisibility() == View.GONE) {
            showBottom();
        } else {
            hideBottom();
        }
        return true;
    }

    private void hideBottom() {
        mBottomTitleGroup.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        tv_ar_drive.setVisibility(View.GONE);
        tv_text_drive.setVisibility(View.GONE);
        v_bg.setVisibility(View.GONE);

        if (mode == TYPE_TEXT_DRIVE) {
            showTextDriveBottom(false);
        }
        KeyboardUtil.hideKeyboard(mActivity, et_input_text);
    }

    private void showBottom() {
        mBottomTitleGroup.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        tv_ar_drive.setVisibility(View.VISIBLE);
        tv_text_drive.setVisibility(View.VISIBLE);
        v_bg.setVisibility(View.VISIBLE);

        if (mode == TYPE_TEXT_DRIVE) {
            showTextDriveBottom(true);
        }
    }
}
