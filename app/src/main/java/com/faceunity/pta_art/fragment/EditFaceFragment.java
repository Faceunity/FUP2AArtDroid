package com.faceunity.pta_art.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.ColorConstant;
import com.faceunity.pta_art.constant.Constant;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.core.AvatarHandle;
import com.faceunity.pta_art.core.PTACore;
import com.faceunity.pta_art.core.client.AvatarEditor;
import com.faceunity.pta_art.core.client.PTAClientWrapper;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.entity.RecordEditBean;
import com.faceunity.pta_art.entity.SpecialBundleRes;
import com.faceunity.pta_art.fragment.editface.EditFaceColorItemFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceDecorationFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceGlassesFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceItemFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceMakeUpFragment;
import com.faceunity.pta_art.fragment.editface.EditShapeFragment;
import com.faceunity.pta_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager;
import com.faceunity.pta_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.pta_art.fragment.editface.core.ItemMakeUpChangeListener;
import com.faceunity.pta_art.fragment.editface.core.MakeUpColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.bean.PairBean;
import com.faceunity.pta_art.fragment.editface.core.shape.EditFaceParameter;
import com.faceunity.pta_art.fragment.editface.core.shape.EditFacePoint;
import com.faceunity.pta_art.fragment.editface.core.shape.EditFacePointFactory;
import com.faceunity.pta_art.fragment.editface.core.shape.EditParamFactory;
import com.faceunity.pta_art.fragment.editface.core.shape.EditPointLayout;
import com.faceunity.pta_art.fragment.editface.core.shape.ParamRes;
import com.faceunity.pta_art.helper.RevokeHelper;
import com.faceunity.pta_art.ui.BottomTitleGroup;
import com.faceunity.pta_art.ui.LoadingDialog;
import com.faceunity.pta_art.ui.NormalDialog;
import com.faceunity.pta_art.utils.FileUtil;
import com.faceunity.pta_art.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_BEARD;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_EYE;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_FACE;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_GLASSES;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_MOUTH;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_NOSE;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_SCENES_2D;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_SHOSE;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.EDIT_FACE_TYPE_APPAREL;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.EDIT_FACE_TYPE_MAKEUPS;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.EDIT_FACE_TYPE_PINCH;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_BEARD_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_CLOTHES_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_CLOTHES_LOWER_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_CLOTHES_UPPER_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_DECORATIONS_EAR_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_DECORATIONS_FOOT_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_DECORATIONS_HAND_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_DECORATIONS_HEAD_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_DECORATIONS_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_DECORATIONS_NECK_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_EYEBROW_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_EYELASH_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_EYELINER_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_EYESHADOW_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_EYE_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_FACEMAKEUP_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_FACE_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_GLASSES_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_HAIR_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_HAT_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_LIPGLOSS_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_MAKE_UP;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_MOUTH_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_NOSE_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_PUPIL_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_SCENES_2D;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_SHOE_INDEX;


/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceFragment extends BaseFragment
        implements View.OnClickListener, RevokeHelper.RevokeHelperListener {
    public static final String TAG = EditFaceFragment.class.getSimpleName();

    private AvatarPTA mDefaultAvatarP2A;
    private AvatarPTA mAvatarP2A;

    private ImageButton mSaveBtn;
    private BottomTitleGroup mEditFaceTitle;

    private int mEditFaceSelectBottomId = TITLE_HAIR_INDEX;
    private SparseArray<EditFaceBaseFragment> mEditFaceBaseFragments = new SparseArray<>();

    private EditFaceItemManager mEditFaceItemManager;
    //侧边栏
    private LinearLayout ll_slide_title;
    private TextView tv_slide_edit_face, tv_slide_makeup, tv_slide_apparel;
    private int slide_type = -1;

    private Runnable task;

    private FrameLayout mFragmentLayout;
    private View mFragmentLayoutBg;
    private EditPointLayout mEditPointLayout;
    private EditFacePoint[] mEditFacePoints;
    private EditFaceParameter mEditFaceParameter;
    private CheckBox mIsFrontBox;
    private boolean isResetFront = false;
    private boolean isFront = true;
    private PTACore mEditP2ACore;
    //捏脸撤销按钮
    private LinearLayout ll_redo;
    private ImageView iv_reset;
    private ImageView iv_redo_left, iv_redo_right;
    //形象编辑撤销按钮
    private LinearLayout ll_model_redo;
    private ImageView iv_model_reset, iv_model_redo_left, iv_model_redo_right;
    private RevokeHelper helper;
    private double oldProgressValue;
    private HashMap<Integer, PairBean> decorationPairBeanMap;
    private List<SpecialBundleRes> decorationList;
    private HashMap<Integer, PairBean> markUpPairBeanMap;
    private List<SpecialBundleRes> makeUpList;
    private int defaultSkinColorIndex = -1;
    /**
     * 捏脸项的显示状态，默认是显示的
     */
    private boolean editItemViewVisiable = true;
    /**
     * 记录捏脸项在进入自定义点位捏脸的前选中的是哪一项
     */
    private int lastShapeSelectedPos;
    /**
     * 记录捏脸项（脸型、眼型、嘴型、鼻型）当前选中的是哪一项
     */
    private int currentShapeIndex;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAvatarP2A = mActivity.getShowAvatarP2A().clone();
        mDefaultAvatarP2A = mActivity.getShowAvatarP2A().clone();
        helper = RevokeHelper.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_face, container, false);
        view.findViewById(R.id.edit_face_back).setOnClickListener(this);
        mSaveBtn = view.findViewById(R.id.edit_face_save);
        mSaveBtn.setOnClickListener(this);

        mFragmentLayout = view.findViewById(R.id.edit_face_bottom_layout);
        mFragmentLayoutBg = view.findViewById(R.id.edit_face_fragment_bg);

        mEditFaceTitle = view.findViewById(R.id.edit_face_bottom_title);
        mEditFaceTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1)
                    mEditFaceTitle.clearCheck();
                showFragment(checkedId);
                mEditFaceSelectBottomId = checkedId;
            }
        });

        mEditFaceItemManager = new EditFaceItemManager();
        mEditFaceItemManager.init(mAvatarP2A.getGender());
        int[][] titleIdAndIcons = mEditFaceItemManager.getTitleIdAndIcons(EDIT_FACE_TYPE_PINCH);
        mEditFaceTitle.setResIcon(titleIdAndIcons[1], titleIdAndIcons[0], mEditFaceSelectBottomId);

        //showFragment(mEditFaceSelectBottomId);
        updateSaveBtn();

        mAvatarHandle.setNeedFacePUP(true);
        mAvatarHandle.setAvatar(mAvatarP2A);

        mEditP2ACore = new PTACore(mP2ACore) {
            @Override
            public int onDrawFrame(byte[] img, int tex, int w, int h, int rotation) {
                int fuTex = super.onDrawFrame(img, tex, w, h, rotation);
                if (mEditFacePoints != null) {
                    parsePoint(mEditFacePoints,
                               w,
                               h,
                               view.getWidth(),
                               view.getHeight()
                    );
                    mEditPointLayout.setPointList(mEditFacePoints);
                }
                return fuTex;
            }
        };
        mFUP2ARenderer.setFUCore(mEditP2ACore);
        mEditFaceParameter = new EditFaceParameter(mAvatarHandle);
        mEditPointLayout = view.findViewById(R.id.point_layout);
        mEditPointLayout.setOnScrollListener(new EditPointLayout.OnScrollListener() {

            @Override
            public void onScrollStart(EditFacePoint point) {
                mEditFaceParameter.copyLast(point);
            }

            @Override
            public void onScrollListener(EditFacePoint point, float distanceX, float distanceY) {
                if (mEditFaceParameter != null) {
                    mEditFaceParameter.setParamFaceShape(point, distanceX, distanceY);
                }
                updateSaveBtn();
            }

            @Override
            public void onScrollEnd(boolean isOperate) {
                if (isOperate) {
                    mEditFaceParameter.recordBack();
                    iv_redo_left.setEnabled(true);
                    iv_reset.setEnabled(true);
                }
            }
        });

        mIsFrontBox = view.findViewById(R.id.edit_shape_position);

        iv_reset = view.findViewById(R.id.iv_reset);
        iv_reset.setEnabled(false);
        iv_reset.setOnClickListener(this);

        ll_redo = view.findViewById(R.id.ll_redo);
        iv_redo_left = view.findViewById(R.id.iv_redo_left);
        iv_redo_left.setOnClickListener(this);
        iv_redo_left.setEnabled(false);

        iv_redo_right = view.findViewById(R.id.iv_redo_right);
        iv_redo_right.setOnClickListener(this);
        iv_redo_right.setEnabled(false);

        mIsFrontBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFront = isChecked;
                if (!isResetFront) {
                    updateEditPoint(true);
                } else {
                    isResetFront = false;
                }
            }
        });

        EditFacePointFactory.init(mActivity);
        EditParamFactory.init(mActivity);

        iv_model_reset = view.findViewById(R.id.iv_model_reset);
        iv_model_reset.setEnabled(false);
        iv_model_reset.setOnClickListener(this);

        ll_model_redo = view.findViewById(R.id.ll_model_redo);
        iv_model_redo_left = view.findViewById(R.id.iv_model_redo_left);
        iv_model_redo_left.setOnClickListener(this);
        iv_model_redo_left.setEnabled(false);

        iv_model_redo_right = view.findViewById(R.id.iv_model_redo_right);
        iv_model_redo_right.setOnClickListener(this);
        iv_model_redo_right.setEnabled(false);
        helper.setListener(this);

        ll_slide_title = view.findViewById(R.id.ll_slide_title);
        tv_slide_edit_face = view.findViewById(R.id.tv_slide_edit_face);
        tv_slide_makeup = view.findViewById(R.id.tv_slide_makeup);
        tv_slide_apparel = view.findViewById(R.id.tv_slide_apparel);

        tv_slide_edit_face.setOnClickListener(this);
        tv_slide_makeup.setOnClickListener(this);
        tv_slide_apparel.setOnClickListener(this);

        changeSlideMode(EDIT_FACE_TYPE_PINCH);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (task != null) {
            task.run();
            task = null;
        }
    }

    @Override
    public void onBackPressed() {
        EditFaceBaseFragment editFaceBaseFragment = mEditFaceBaseFragments.get(mEditFaceSelectBottomId);
        if (mFragmentLayout.getVisibility() == View.GONE && editFaceBaseFragment instanceof EditShapeFragment) {
            if (!isFront) {
                isResetFront = true;
                mIsFrontBox.setChecked(true);
            }

            setEditFacePoints(null, false);
            EditShapeFragment shapeFragment = (EditShapeFragment) editFaceBaseFragment;
            shapeFragment.resetSelect();
            mEditFaceParameter.resetToTemp();
            mEditFaceParameter.clearRevoke();
            iv_redo_right.setEnabled(false);
            iv_redo_left.setEnabled(false);
            iv_reset.setEnabled(false);
            mAvatarHandle.setPose(false);
            mAvatarHandle.setAvatar(mAvatarP2A);
        } else if (isChangeValues()) {
            NormalDialog normalDialog = new NormalDialog();
            normalDialog.setNormalDialogTheme(R.style.FullScreenTheme);
            normalDialog.setMessageStr("是否保存当前形象编辑？");
            normalDialog.setNegativeStr("放弃");
            normalDialog.setPositiveStr("保存");
            normalDialog.show(mActivity.getSupportFragmentManager(), NormalDialog.TAG);
            normalDialog.setOnClickListener(new NormalDialog.OnSimpleClickListener() {
                @Override
                public void onPositiveListener() {
                    saveAvatar();
                }

                @Override
                public void onNegativeListener() {
                    backToHome(mDefaultAvatarP2A);
                }
            });
        } else {
            backToHome(mDefaultAvatarP2A);
        }
    }

    public void backToHome(AvatarPTA avatarP2A) {
        helper.clearRevoke();
        mAvatarHandle.clearExpression(avatarP2A, false);
        mActivity.showHomeFragment();
        mAvatarHandle.setNeedFacePUP(false);
        mActivity.setCanController(true);
        mFUP2ARenderer.setFUCore(mP2ACore);

        mActivity.setShowAvatarP2A(avatarP2A);
        mAvatarHandle.setAvatar(avatarP2A, false, true, null);

        mEditFaceParameter.release();
        EditFacePointFactory.release();
        EditParamFactory.release();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_face_back:
                onBackPressed();
                break;
            case R.id.edit_face_save:
                if (mFragmentLayout.getVisibility() == View.GONE) {
                    if (!isFront) {
                        isResetFront = true;
                        mIsFrontBox.setChecked(true);
                    }
                    helper.record(-1, mEditFaceParameter.getTemp(),
                                  mEditFaceItemManager.getBundleName(mEditFaceSelectBottomId), lastShapeSelectedPos);
                    iv_model_redo_left.setEnabled(true);
                    iv_model_reset.setEnabled(true);
                    iv_reset.setEnabled(false);
                    iv_redo_left.setEnabled(false);
                    iv_redo_right.setEnabled(false);
                    mEditFaceParameter.clearRevoke();

                    mAvatarHandle.setPose(false);
                    mAvatarHandle.setAvatar(mAvatarP2A);
                    setEditFacePoints(null, false);
                } else {
                    saveAvatar();
                }
                break;
            //侧边栏
            case R.id.tv_slide_edit_face:
                changeSlideMode(EDIT_FACE_TYPE_PINCH);
                break;
            case R.id.tv_slide_apparel:
                changeSlideMode(EDIT_FACE_TYPE_APPAREL);
                break;
            case R.id.tv_slide_makeup:
                changeSlideMode(EDIT_FACE_TYPE_MAKEUPS);
                break;
            //重置形象
            case R.id.iv_model_reset:
                helper.clearRevoke();
                iv_model_reset.setEnabled(false);
                iv_model_redo_left.setEnabled(false);
                iv_model_redo_right.setEnabled(false);
                mAvatarP2A = mActivity.getShowAvatarP2A().clone();
                mAvatarHandle.setAvatar(mAvatarP2A);
                resetUI();
                mEditFaceParameter.resetDefaultDeformParam();
                mSaveBtn.setEnabled(false);
                // 模型重置，动画不重置
                if (mEditFaceSelectBottomId == TITLE_CLOTHES_INDEX
                        || mEditFaceSelectBottomId == TITLE_CLOTHES_UPPER_INDEX
                        || mEditFaceSelectBottomId == TITLE_CLOTHES_LOWER_INDEX
                        || mEditFaceSelectBottomId == TITLE_SHOE_INDEX
                        || mEditFaceSelectBottomId == TITLE_DECORATIONS_INDEX) {
                    mAvatarHandle.setExpression(mAvatarP2A, new BundleRes(FilePathFactory.EXPRESSION_ANI_DRESS_UP), Integer.MAX_VALUE);
                }
                break;
            //形象撤销
            case R.id.iv_model_redo_left:
                helper.revokeLast();
                iv_model_redo_left.setEnabled(!helper.getRecordBackStackIsEmpty());
                iv_model_redo_right.setEnabled(!helper.getRecordGoAheadStackIsEmpty());
                break;
            case R.id.iv_model_redo_right:
                helper.goAheadLast();
                iv_model_redo_left.setEnabled(!helper.getRecordBackStackIsEmpty());
                iv_model_redo_right.setEnabled(!helper.getRecordGoAheadStackIsEmpty());
                break;
            //重置捏脸
            case R.id.iv_reset:
                mEditFaceParameter.resetToTemp();
                mEditFaceParameter.clearRevoke();
                iv_redo_right.setEnabled(false);
                iv_redo_left.setEnabled(false);
                iv_reset.setEnabled(false);
                break;
            //捏脸撤销
            case R.id.iv_redo_left:
                mEditFaceParameter.revokeLast();
                iv_redo_left.setEnabled(!mEditFaceParameter.getRecordBackStackIsEmpty());
                iv_redo_right.setEnabled(!mEditFaceParameter.getRecordGoAheadStackIsEmpty());
                break;
            case R.id.iv_redo_right:
                mEditFaceParameter.goAheadLast();
                iv_redo_left.setEnabled(!mEditFaceParameter.getRecordBackStackIsEmpty());
                iv_redo_right.setEnabled(!mEditFaceParameter.getRecordGoAheadStackIsEmpty());
                break;
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if (mEditPointLayout.getVisibility() == View.GONE) {

            // 表示当前不是点位捏脸逻辑
            if (editItemViewVisiable) {
                // 表示当前状态是捏脸项展开
                showFragment(-1);
            } else {
                showFragment(mEditFaceSelectBottomId);
            }
        }
        return true;
    }

    /**
     * 切换侧边栏模式
     *
     * @param slide_type
     */
    private void changeSlideMode(int slide_type) {
        if (slide_type == this.slide_type) {
            // 当前点击的就是之前选中的，只有当前的页面为捏脸或者是换装的时候，并且选择项为隐藏的时候，才会取消当前的点击事件
            if (mEditFaceTitle.getVisibility() != View.GONE && mEditFaceSelectBottomId != TITLE_MAKE_UP) {
                return;
            }
        }
        this.slide_type = slide_type;
        tv_slide_edit_face.setSelected(false);
        tv_slide_apparel.setSelected(false);
        tv_slide_makeup.setSelected(false);
        switch (slide_type) {
            case EDIT_FACE_TYPE_PINCH:
                tv_slide_edit_face.setSelected(true);
                break;
            case EDIT_FACE_TYPE_MAKEUPS:
                tv_slide_makeup.setSelected(true);
                break;
            case EDIT_FACE_TYPE_APPAREL:
                tv_slide_apparel.setSelected(true);
                break;
        }
        mEditFaceItemManager.centerTypeSelectedPosition = slide_type;
        showFragment(mEditFaceItemManager.getSelectedFragmentId());
        if (slide_type != EDIT_FACE_TYPE_MAKEUPS) {
            int[][] idAndIcons = mEditFaceItemManager.getTitleIdAndIcons(slide_type);
            mEditFaceTitle.setResIcon(idAndIcons[1], idAndIcons[0], mEditFaceItemManager.getSelectedFragmentId());
            mEditFaceTitle.post(new Runnable() {
                @Override
                public void run() {
                    mEditFaceTitle.smoothScrollToByCheckedId(mEditFaceItemManager.getSelectedFragmentId(), false);
                }
            });
        }

        mEditFaceSelectBottomId = mEditFaceItemManager.getSelectedFragmentId();
    }

    private void updateSaveBtn() {
        if (isChangeValues() || mFragmentLayout.getVisibility() == View.GONE) {
            mSaveBtn.setEnabled(true);
        } else {
            mSaveBtn.setEnabled(false);
        }
    }

    public void showFragment(int id) {
        try {
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            EditFaceBaseFragment show = mEditFaceBaseFragments.get(id);
            if (mEditFaceBaseFragments.get(mEditFaceSelectBottomId) != null) {
                transaction.hide(mEditFaceBaseFragments.get(mEditFaceSelectBottomId));
            }
            if (show == null) {
                switch (id) {
                    case TITLE_HAIR_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.hair_color, (int) mAvatarP2A.getHairColorValue(), mColorValuesChangeListener, FilePathFactory.hairBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getHairIndex(), mItemChangeListener);
                        break;
                    case TITLE_FACE_INDEX:
                        show = new EditShapeFragment();
                        double value = -1;
                        int index = -1;
                        if (mAvatarP2A.getSkinColorValue() < 0) {
                            index = mAvatarHandle.fuItemGetParamSkinColorIndex();
                            defaultSkinColorIndex = index;
                        } else {
                            value = mAvatarP2A.getSkinColorValue();
                        }
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamFace, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamFace), ColorConstant.skin_color, value, mColorValuesChangeListener);
                        value = ((EditShapeFragment) show).setColorPickGradient(value, index);
                        if (index != -1) {
                            mAvatarP2A.setSkinColorValue(value);
                            mDefaultAvatarP2A.setSkinColorValue(value);
                        }
                        break;
                    case TITLE_EYE_INDEX:
                        show = new EditShapeFragment();
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamEye, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamEye), ColorConstant.iris_color, mAvatarP2A.getIrisColorValue(), mColorValuesChangeListener);
                        break;
                    case TITLE_MOUTH_INDEX:
                        show = new EditShapeFragment();
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamMouth, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamMouth));
                        break;
                    case TITLE_NOSE_INDEX:
                        show = new EditShapeFragment();
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamNose, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamNose));
                        break;
                    case TITLE_BEARD_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.beardBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getBeardIndex(), mItemChangeListener);
                        break;
                    // 美妆部分
                    case TITLE_MAKE_UP:
                        show = new EditFaceMakeUpFragment();
                        mEditFaceItemManager.initMakeUpList(mAvatarP2A);
                        markUpPairBeanMap = mEditFaceItemManager.getMarkUpPairBeanMap();
                        makeUpList = mEditFaceItemManager.getMakeUpList();
                        ((EditFaceMakeUpFragment) show).initData(ColorConstant.makeup_color, ColorConstant.lip_color, mMakeUpColorValuesChangeListener,
                                                                 makeUpList, mItemMakeUpChangeListener, markUpPairBeanMap);
                        break;
                    // 换装部分
                    case TITLE_GLASSES_INDEX:
                        show = new EditFaceGlassesFragment();
                        ((EditFaceGlassesFragment) show).initData(ColorConstant.glass_color, (int) mAvatarP2A.getGlassesColorValue(), mColorValuesChangeListener,
                                                                  ColorConstant.glass_frame_color, (int) mAvatarP2A.getGlassesFrameColorValue(),
                                                                  FilePathFactory.glassesBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getGlassesIndex(), mItemChangeListener);
                        break;
                    case TITLE_HAT_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.hatBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getHatIndex(), mItemChangeListener);
                        break;
                    case TITLE_CLOTHES_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.clothesBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getClothesIndex(), mItemChangeListener);
                        break;
                    case TITLE_CLOTHES_UPPER_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.clothUpperBundleRes(), mAvatarP2A.getClothesUpperIndex(), mItemChangeListener);
                        break;
                    case TITLE_CLOTHES_LOWER_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.clothLowerBundleRes(), mAvatarP2A.getClothesLowerIndex(), mItemChangeListener);
                        break;
                    case TITLE_SHOE_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.shoeBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getShoeIndex(), mItemChangeListener);
                        break;
                    case TITLE_DECORATIONS_INDEX:
                        show = new EditFaceDecorationFragment();
                        mEditFaceItemManager.initDecorationList(mAvatarP2A);

                        decorationPairBeanMap = mEditFaceItemManager.getDecorationPairBeanMap();
                        decorationList = mEditFaceItemManager.getDecorationList();
                        ((EditFaceDecorationFragment) show).initData(decorationList, mItemDecorationChangeListener, decorationPairBeanMap);
                        break;
                    case TITLE_SCENES_2D:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.scenes2DBundleRes(), mAvatarP2A.getBackground2DIndex(), mItemChangeListener);
                        break;
                }
                if (show != null) {
                    mEditFaceBaseFragments.put(id, show);
                    show.setAvatarP2A(mAvatarP2A);
                    Bundle data = new Bundle();
                    data.putInt(EditFaceBaseFragment.ID_KEY, id);
                    show.setArguments(data);
                    transaction.add(R.id.edit_face_bottom_layout, show);
                }
            } else {
                transaction.show(show);
            }
            // 当列表隐藏的时候，选择栏也需要隐藏
            if (show == null) {
                editItemViewVisiable = false;
                mEditFaceTitle.setVisibility(View.GONE);
                mFragmentLayoutBg.setVisibility(View.GONE);
            } else {
                editItemViewVisiable = true;
                mEditFaceTitle.setVisibility(id == TITLE_MAKE_UP ? View.GONE : View.VISIBLE);
                mFragmentLayoutBg.setVisibility(View.VISIBLE);
            }
            transaction.commit();
            if (id != -1) {
                mEditFaceItemManager.setSelectedFragmentID(id);
            }
            if (id == -1) {
                mP2ACore.loadWholeBodyCamera();
            } else if (id == TITLE_CLOTHES_INDEX
                    || id == TITLE_CLOTHES_UPPER_INDEX
                    || id == TITLE_CLOTHES_LOWER_INDEX
                    || id == TITLE_SHOE_INDEX
                    || id == TITLE_DECORATIONS_INDEX
                    || id == TITLE_SCENES_2D) {
                mAvatarHandle.setExpression(mAvatarP2A, new BundleRes(FilePathFactory.EXPRESSION_ANI_DRESS_UP), Integer.MAX_VALUE);
                mP2ACore.loadSmallWholeBodyCamera();
            } else {
                mAvatarHandle.clearExpression(mAvatarP2A, true);
                mP2ACore.loadHalfLengthBodyCamera();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void resetUI() {
        EditFaceBaseFragment show = mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
        if (show != null) {
            ((EditFaceColorItemFragment) show).setItem(mAvatarP2A.getHairIndex());
            ((EditFaceColorItemFragment) show).setColorItem((int) mAvatarP2A.getHairColorValue());
        }
        show = mEditFaceBaseFragments.get(TITLE_FACE_INDEX);
        if (show != null) {
            double value = -1;
            int index = -1;
            if (mAvatarP2A.getSkinColorValue() < 0) {
                index = defaultSkinColorIndex;
            } else {
                value = mAvatarP2A.getSkinColorValue();
            }
            value = ((EditShapeFragment) show).setColorPickGradient(value, index);
            if (index != -1) {
                mAvatarP2A.setSkinColorValue(value);
                mDefaultAvatarP2A.setSkinColorValue(value);
            }
            ((EditShapeFragment) show).setItem(checkSelectPos(EditParamFactory.mEditParamFace));
            ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_FACE_INDEX)).setProgress(value);
        }
        show = mEditFaceBaseFragments.get(TITLE_EYE_INDEX);
        if (show != null) {
            ((EditShapeFragment) show).setItem(checkSelectPos(EditParamFactory.mEditParamEye));
            ((EditShapeFragment) show).setColorItem((int) mAvatarP2A.getIrisColorValue());
        }
        show = mEditFaceBaseFragments.get(TITLE_MOUTH_INDEX);
        if (show != null) {
            ((EditShapeFragment) show).setItem(checkSelectPos(EditParamFactory.mEditParamMouth));
        }
        show = mEditFaceBaseFragments.get(TITLE_NOSE_INDEX);
        if (show != null) {
            ((EditShapeFragment) show).setItem(checkSelectPos(EditParamFactory.mEditParamNose));
        }
        show = mEditFaceBaseFragments.get(TITLE_BEARD_INDEX);
        if (show != null) {
            ((EditFaceItemFragment) show).setItem(mAvatarP2A.getBeardIndex());
        }
        // 美妆部分
        show = mEditFaceBaseFragments.get(TITLE_MAKE_UP);
        if (show != null) {
            resetUIForSpecial(TITLE_EYELASH_INDEX, mAvatarP2A.getEyelashIndex(), (int) mAvatarP2A.getEyelashColorValue(), show, markUpPairBeanMap);
            resetUIForSpecial(TITLE_EYEBROW_INDEX, mAvatarP2A.getEyebrowIndex(), (int) mAvatarP2A.getEyebrowColorValue(), show, markUpPairBeanMap);
            resetUIForSpecial(TITLE_EYELINER_INDEX, mAvatarP2A.getEyelinerIndex(), -1, show, markUpPairBeanMap);
            resetUIForSpecial(TITLE_EYESHADOW_INDEX, mAvatarP2A.getEyeshadowIndex(), (int) mAvatarP2A.getEyeshadowColorValue(), show, markUpPairBeanMap);
            resetUIForSpecial(TITLE_PUPIL_INDEX, mAvatarP2A.getPupilIndex(), -1, show, markUpPairBeanMap);
            resetUIForSpecial(TITLE_LIPGLOSS_INDEX, mAvatarP2A.getLipglossIndex(), (int) mAvatarP2A.getLipglossColorValue(), show, markUpPairBeanMap);
            resetUIForSpecial(TITLE_FACEMAKEUP_INDEX, mAvatarP2A.getFaceMakeupIndex(), -1, show, markUpPairBeanMap);

        }
        // 换装部分
        show = mEditFaceBaseFragments.get(TITLE_GLASSES_INDEX);
        if (show != null) {
            ((EditFaceGlassesFragment) show).setGlassesColorItem((int) mAvatarP2A.getGlassesColorValue());
            ((EditFaceGlassesFragment) show).setGlassesFrameColorItem((int) mAvatarP2A.getGlassesFrameColorValue());
            ((EditFaceGlassesFragment) show).setItem(mAvatarP2A.getGlassesIndex());
        }
        show = mEditFaceBaseFragments.get(TITLE_HAT_INDEX);
        if (show != null) {
            ((EditFaceItemFragment) show).setItem(mAvatarP2A.getHatIndex());
        }
        show = mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX);
        if (show != null) {
            ((EditFaceItemFragment) show).setItem(mAvatarP2A.getClothesIndex());
        }
        show = mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX);
        if (show != null) {
            ((EditFaceItemFragment) show).setItem(mAvatarP2A.getClothesUpperIndex());
        }
        show = mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX);
        if (show != null) {
            ((EditFaceItemFragment) show).setItem(mAvatarP2A.getClothesLowerIndex());
        }
        show = mEditFaceBaseFragments.get(TITLE_SHOE_INDEX);
        if (show != null) {
            ((EditFaceItemFragment) show).setItem(mAvatarP2A.getShoeIndex());
        }
        show = mEditFaceBaseFragments.get(TITLE_SCENES_2D);
        if (show != null) {
            ((EditFaceItemFragment) show).setItem(mAvatarP2A.getBackground2DIndex());
        }
        // 配饰部分
        show = mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX);
        if (show != null) {
            resetUIForSpecial(TITLE_DECORATIONS_HAND_INDEX, mAvatarP2A.getDecorationsHandIndex(), -1, show, decorationPairBeanMap);
            resetUIForSpecial(TITLE_DECORATIONS_FOOT_INDEX, mAvatarP2A.getDecorationsFootIndex(), -1, show, decorationPairBeanMap);
            resetUIForSpecial(TITLE_DECORATIONS_NECK_INDEX, mAvatarP2A.getDecorationsNeckIndex(), -1, show, decorationPairBeanMap);
            resetUIForSpecial(TITLE_DECORATIONS_EAR_INDEX, mAvatarP2A.getDecorationsEarIndex(), -1, show, decorationPairBeanMap);
            resetUIForSpecial(TITLE_DECORATIONS_HEAD_INDEX, mAvatarP2A.getDecorationsHeadIndex(), -1, show, decorationPairBeanMap);
        }
    }

    private void resetUIForSpecial(int type, int defaultSelectPos, int defaultSelectColorPos, EditFaceBaseFragment show, HashMap<Integer, PairBean> pairBeanMap) {
        PairBean pairBean = pairBeanMap.get(type);
        // 重置选项
        if (pairBean != null && defaultSelectPos != pairBean.getSelectItemPos() - pairBean.getFrontLength()) {
            if (show instanceof EditFaceDecorationFragment) {
                if (defaultSelectPos == 0) {
                    if (pairBean.getSelectItemPos() != 0) {
                        ((EditFaceDecorationFragment) show).setItem(false, pairBean.getSelectItemPos());
                    }
                } else {
                    ((EditFaceDecorationFragment) show).setItem(false, defaultSelectPos + pairBean.getFrontLength());
                }
            }
            if (show instanceof EditFaceMakeUpFragment) {
                if (defaultSelectPos == 0) {
                    if (pairBean.getSelectItemPos() != 0) {
                        ((EditFaceMakeUpFragment) show).setItem(false, pairBean.getSelectItemPos());
                    }
                } else {
                    ((EditFaceMakeUpFragment) show).setItem(false, defaultSelectPos + pairBean.getFrontLength());
                }

            }
        }
        // 重置颜色
        if (pairBean != null && defaultSelectColorPos > -1 && defaultSelectColorPos != pairBean.getSelectColorPos()) {
            if (show instanceof EditFaceMakeUpFragment) {
                pairBean.setSelectColorPos(defaultSelectPos);
                // 内部会通过type获取到当前当前美妆项的颜色，所以我们需要传递一个type进去，告诉recyclerview当前需要改变的是哪一个美妆项的捏脸值
                ((EditFaceMakeUpFragment) show).setColorItem(type, defaultSelectColorPos);
            }
        }
    }

    private int checkSelectPos(List<ParamRes> paramResList) {
        int pos = 0;
        for (int i = 0; i < paramResList.size(); i++) {
            ParamRes res = paramResList.get(i);
            if (res == null || res.paramMap == null) continue;
            boolean isEq = true;
            for (String key : res.paramMap.keySet()) {
                if (!Objects.equals(res.paramMap.get(key), mEditFaceParameter.getDefaultParamByKey(key))) {
                    isEq = false;
                    break;
                }
            }
            if (isEq) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    EditShapeFragment.EditFaceStatusChaneListener mEditFaceStatusChaneListener = new EditShapeFragment.EditFaceStatusChaneListener() {
        @Override
        public void editFacePointChaneListener(int id, int lastPos, int pos, ParamRes res) {
            currentShapeIndex = pos;
            if (pos == 0) {
                // 表示为自定义捏脸
                EditFaceFragment.this.lastShapeSelectedPos = lastPos;
                mAvatarHandle.setPose(true);
                mAvatarHandle.setAvatar(mAvatarP2A);
                mP2ACore.loadBigHalfLengthBodyCamera();
                updateEditPoint(false);
            } else {
                iv_model_redo_left.setEnabled(true);
                iv_model_reset.setEnabled(true);
                switch (id) {
                    case TITLE_FACE_INDEX:
                        helper.record(id,
                                      BUNDLE_NAME_FACE, lastPos,
                                      "", 0.0, mEditFaceParameter.getMap());
                        break;
                    case TITLE_EYE_INDEX:
                        helper.record(id,
                                      BUNDLE_NAME_EYE, lastPos,
                                      "", 0.0, mEditFaceParameter.getMap());
                        break;
                    case TITLE_MOUTH_INDEX:
                        helper.record(id,
                                      BUNDLE_NAME_MOUTH, lastPos,
                                      "", 0.0, mEditFaceParameter.getMap());
                        break;
                    case TITLE_NOSE_INDEX:
                        helper.record(id,
                                      BUNDLE_NAME_NOSE, lastPos,
                                      "", 0.0, mEditFaceParameter.getMap());
                        break;
                }

                mEditFaceParameter.setParamMap(res.paramMap);
                updateSaveBtn();
            }
        }
    };

    private LoadingDialog mLoadingDialogHair;

    ItemChangeListener mItemChangeListener = new ItemChangeListener() {
        @Override
        public void itemChangeListener(int id, int pos) {
            boolean hasRecord = false;
            iv_model_redo_left.setEnabled(true);
            iv_model_reset.setEnabled(true);
            switch (id) {
                case TITLE_HAIR_INDEX:

                    mEditFaceItemManager.selectedHeadType(id, mAvatarP2A, helper, mEditFaceBaseFragments);
                    mAvatarP2A.setHairIndex(pos);
                    if (deformHair(pos)) return;
                    break;
                case TITLE_BEARD_INDEX:
                    helper.record(TITLE_BEARD_INDEX,
                                  BUNDLE_NAME_BEARD, mAvatarP2A.getBeardIndex(),
                                  "", 0.0);
                    mAvatarP2A.setBeardIndex(pos);
                    break;
                // 换装部分
                case TITLE_GLASSES_INDEX:
                    helper.record(TITLE_GLASSES_INDEX,
                                  BUNDLE_NAME_GLASSES, mAvatarP2A.getGlassesIndex(),
                                  "", 0.0);
                    mAvatarP2A.setGlassesIndex(pos);
                    break;
                case TITLE_HAT_INDEX:
                    mEditFaceItemManager.selectedHeadType(id, mAvatarP2A, helper, mEditFaceBaseFragments);
                    mAvatarP2A.setHatIndex(pos);

                    if (pos > 0 && mAvatarP2A.getHatFile().startsWith(Constant.filePath)) {
                        File file = new File(mAvatarP2A.getHatFile());
                        if (!file.exists() || file.length() <= 0) {
                            if (mLoadingDialogHair == null) {
                                LoadingDialog.Builder builder = new LoadingDialog.Builder(mActivity);
                                builder.setLoadingStr("发帽生成中...");
                                mLoadingDialogHair = builder.create();
                            }
                            mLoadingDialogHair.show();
                            downHat(pos);
                            return;
                        }
                    }
                    break;
                case TITLE_CLOTHES_INDEX:
                    mEditFaceItemManager.selectedClothsType(TITLE_CLOTHES_INDEX, pos, mAvatarP2A, helper, mEditFaceBaseFragments);
                    setBodyLevelForClothes(pos, FilePathFactory.clothesBundleRes(0));
                    break;
                case TITLE_CLOTHES_UPPER_INDEX:
                    mEditFaceItemManager.selectedClothsType(TITLE_CLOTHES_UPPER_INDEX, pos, mAvatarP2A, helper, mEditFaceBaseFragments);
                    setBodyLevelForClothes(pos, FilePathFactory.clothUpperBundleRes());
                    break;
                case TITLE_CLOTHES_LOWER_INDEX:
                    mEditFaceItemManager.selectedClothsType(TITLE_CLOTHES_LOWER_INDEX, pos, mAvatarP2A, helper, mEditFaceBaseFragments);
                    setBodyLevelForClothes(mAvatarP2A.getClothesUpperIndex(), FilePathFactory.clothUpperBundleRes());
                    break;
                case TITLE_SHOE_INDEX:
                    helper.record(TITLE_SHOE_INDEX,
                                  BUNDLE_NAME_SHOSE, mAvatarP2A.getShoeIndex(),
                                  "", 0.0);
                    mAvatarP2A.setShoeIndex(pos);
                    break;

                case TITLE_SCENES_2D:
                    helper.record(TITLE_SCENES_2D,
                                  BUNDLE_NAME_SCENES_2D, mAvatarP2A.getBackground2DIndex(),
                                  "", 0.0);
                    mAvatarP2A.setBackground2DIndex(pos);
                    mAvatarP2A.setBackground3DIndex(-1);
                    mAvatarP2A.setBackgroundAniIndex(-1);
                    break;
            }
            mAvatarHandle.setAvatar(mAvatarP2A);
            updateSaveBtn();
        }
    };

    private boolean deformHair(int pos) {

        if (pos > 0 && mAvatarP2A.getHairFile().startsWith(Constant.filePath)) {
            File file = new File(mAvatarP2A.getHairFile());
            if (!file.exists() || file.length() <= 0) {
                if (mLoadingDialogHair == null) {
                    LoadingDialog.Builder builder = new LoadingDialog.Builder(mActivity);
                    builder.setLoadingStr("头发生成中...");
                    mLoadingDialogHair = builder.create();
                }
                mLoadingDialogHair.show();
                downHair(pos);
                return true;
            }
        }
        return false;
    }

    ItemMakeUpChangeListener mItemMakeUpChangeListener = new ItemMakeUpChangeListener() {
        @Override
        public void itemChangeListener(int id, int type, boolean isSel, int pos, int realPos) {
            iv_model_redo_left.setEnabled(true);
            iv_model_reset.setEnabled(true);
            if (type != TITLE_MAKE_UP) {
                PairBean pairBean = markUpPairBeanMap.get(type);
                if (pairBean != null) {
                    int selectPosition = pairBean.getSelectItemPos() - pairBean.getFrontLength();
                    selectPosition = Math.max(selectPosition, 0);
                    Log.e("jiang", "itemChangeListener:" + pairBean.getSelectColorPos());
                    if (isSel) {
                        // selectPosition > 0 来判断之前是否有同款美妆选择
                        helper.record(type, String.valueOf(type), selectPosition, selectPosition > 0, "", pairBean.getSelectColorPos());
                    } else {
                        int sel = pairBean.getSelectItemPos();
                        if (sel > 0) {
                            sel = selectPosition;
                        }
                        helper.record(type, String.valueOf(type), sel, sel > 0, "", pairBean.getSelectColorPos());
                    }
                }

            }
            switch (type) {
                case TITLE_MAKE_UP:
                    Map<Integer, PairBean> tempPairBean = new HashMap<>();
                    for (Integer key : markUpPairBeanMap.keySet()) {
                        PairBean pairBean = markUpPairBeanMap.get(key);
                        if (pairBean.getSelectItemPos() > 0) {
                            tempPairBean.put(key, pairBean);
                        }
                    }
                    if (tempPairBean.size() > 0) {
                        helper.record(TITLE_MAKE_UP, TITLE_MAKE_UP + "",
                                      0.0, true, tempPairBean);
                        tempPairBean.clear();
                        tempPairBean = null;
                    }
                    mAvatarP2A.setEyelashIndex(0);
                    mAvatarP2A.setEyelinerIndex(0);
                    mAvatarP2A.setEyeshadowIndex(0);
                    mAvatarP2A.setEyebrowIndex(0);
                    mAvatarP2A.setPupilIndex(0);
                    mAvatarP2A.setLipglossIndex(0);
                    mAvatarP2A.setFaceMakeupIndex(0);
                    break;
                // 美妆部分
                case TITLE_EYELASH_INDEX:
                    mAvatarP2A.setEyelashIndex(isSel ? realPos : 0);
                    break;
                case TITLE_EYELINER_INDEX:
                    mAvatarP2A.setEyelinerIndex(isSel ? realPos : 0);
                    break;
                case TITLE_EYESHADOW_INDEX:
                    mAvatarP2A.setEyeshadowIndex(isSel ? realPos : 0);
                    break;
                case TITLE_EYEBROW_INDEX:
                    mAvatarP2A.setEyebrowIndex(isSel ? realPos : 0);
                    break;
                case TITLE_PUPIL_INDEX:
                    mAvatarP2A.setPupilIndex(isSel ? realPos : 0);
                    break;
                case TITLE_LIPGLOSS_INDEX:
                    mAvatarP2A.setLipglossIndex(isSel ? realPos : 0);
                    break;
                case TITLE_FACEMAKEUP_INDEX:
                    mAvatarP2A.setFaceMakeupIndex(isSel ? realPos : 0);
                    break;
            }
            mAvatarHandle.setAvatar(mAvatarP2A);
            updateSaveBtn();
        }
    };

    ItemMakeUpChangeListener mItemDecorationChangeListener = new ItemMakeUpChangeListener() {
        @Override
        public void itemChangeListener(int id, int type, boolean isSel, int pos, int realPos) {
            iv_model_redo_left.setEnabled(true);
            iv_model_reset.setEnabled(true);
            if (type != TITLE_DECORATIONS_INDEX) {
                // 当前选中的数据
                int selectPosition = decorationPairBeanMap.get(type).getSelectItemPos() - decorationPairBeanMap.get(type).getFrontLength();
                selectPosition = Math.max(selectPosition, 0);
                if (isSel) {
                    // selectPosition > 0 来判断之前是否有同款配饰选择
                    if (mEditFaceItemManager.needRecordDecorationOption(type, mAvatarP2A)) {
                        helper.record(type, String.valueOf(type), selectPosition, selectPosition > 0, "", 0.0);
                    }
                } else {
                    int sel = decorationPairBeanMap.get(type).getSelectItemPos();
                    if (sel > 0) {
                        sel = selectPosition;
                    }
                    helper.record(type, String.valueOf(type), sel, sel > 0, "", 0.0);
                }
            }
            switch (type) {
                case TITLE_DECORATIONS_INDEX:
                    Map<Integer, PairBean> tempPairBean = new HashMap<>();
                    for (Integer key : decorationPairBeanMap.keySet()) {
                        PairBean pairBean = decorationPairBeanMap.get(key);
                        if (pairBean.getSelectItemPos() > 0) {
                            tempPairBean.put(key, pairBean);
                        }
                    }
                    if (tempPairBean.size() > 0) {
                        helper.record(TITLE_DECORATIONS_INDEX, TITLE_DECORATIONS_INDEX + "",
                                      0.0, true, tempPairBean);
                        tempPairBean.clear();
                    }
                    mAvatarP2A.setDecorationsEarIndex(0);
                    mAvatarP2A.setDecorationsFootIndex(0);
                    mAvatarP2A.setDecorationsHandIndex(0);
                    mAvatarP2A.setDecorationsHeadIndex(0);
                    mAvatarP2A.setDecorationsNeckIndex(0);
                    break;
                // 配饰
                case TITLE_DECORATIONS_EAR_INDEX:
                    mAvatarP2A.setDecorationsEarIndex(isSel ? realPos : 0);
                    break;
                case TITLE_DECORATIONS_FOOT_INDEX:
                    mAvatarP2A.setDecorationsFootIndex(isSel ? realPos : 0);
                    break;
                case TITLE_DECORATIONS_HAND_INDEX:
                    mAvatarP2A.setDecorationsHandIndex(isSel ? realPos : 0);
                    break;
                case TITLE_DECORATIONS_HEAD_INDEX:
                    boolean needDeformHair = mEditFaceItemManager.selectedHeadType(type, mAvatarP2A, helper, mEditFaceBaseFragments);
                    mAvatarP2A.setDecorationsHeadIndex(isSel ? realPos : 0);
                    if (needDeformHair && deformHair(2)) {
                        return;
                    }
                    break;
                case TITLE_DECORATIONS_NECK_INDEX:
                    mAvatarP2A.setDecorationsNeckIndex(isSel ? realPos : 0);
                    break;

            }
            mAvatarHandle.setAvatar(mAvatarP2A);
            updateSaveBtn();
        }
    };


    private void setBodyLevelForClothes(int pos, List<BundleRes> bundleResList) {
        BundleRes bundleRes = bundleResList.get(pos);
        if (bundleRes != null) {
            mAvatarP2A.setClothesGender(bundleRes.gender);
            mAvatarP2A.setBodyLevel(bundleRes.bodyLevel);
        }
    }

    /**
     * 本地deform头发
     *
     * @param pos
     */
    private void downHair(int pos) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                byte[] objData = FileUtil.readBytes(mAvatarP2A.getHeadFile());
                if (objData == null)
                    return;
                List<BundleRes> hairBundles = FilePathFactory.hairBundleRes(mAvatarP2A.getGender());
                BundleRes hair = hairBundles.get(pos);
                try {
                    PTAClientWrapper.deformHairByServer(mActivity, objData, hair.path, mAvatarP2A.getBundleDir() + hair.name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialogHair.dismiss();
                        mAvatarHandle.setAvatar(mAvatarP2A);
                        updateSaveBtn();
                    }
                });
            }
        });
    }

    /**
     * 本地deform发帽
     *
     * @param pos
     */
    private void downHat(int pos) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                byte[] objData = FileUtil.readBytes(mAvatarP2A.getHeadFile());
                if (objData == null)
                    return;
                List<BundleRes> hatBundles = FilePathFactory.hatBundleRes(mAvatarP2A.getGender());
                BundleRes hat = hatBundles.get(pos);
                try {
                    PTAClientWrapper.deformHairByServer(mActivity, objData, hat.path, mAvatarP2A.getBundleDir() + hat.name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialogHair.dismiss();
                        mAvatarHandle.setAvatar(mAvatarP2A);
                        updateSaveBtn();
                    }
                });
            }
        });
    }

    ColorValuesChangeListener mColorValuesChangeListener = new ColorValuesChangeListener() {
        @Override
        public void colorValuesChangeListener(int id, int index, double values) {
            iv_model_redo_left.setEnabled(true);
            iv_model_reset.setEnabled(true);
            int pos = (int) values;
            switch (id) {
                case TITLE_HAIR_INDEX:
                    helper.record(TITLE_HAIR_INDEX,
                                  "", 0.0,
                                  "hair_color", mAvatarP2A.getHairColorValue());
                    mAvatarP2A.setHairColorValue(pos);
                    double[] hair_color = ColorConstant.hair_color[pos];
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hair_color, Arrays.copyOf(hair_color, 3));
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hair_color_intensity, (float) hair_color[3]);
                    break;
                case TITLE_FACE_INDEX:
                    mAvatarP2A.setSkinColorValue(values);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_skin_color, ColorConstant.getColor(ColorConstant.skin_color, values));
                    break;
                case TITLE_EYE_INDEX:
                    helper.record(TITLE_EYE_INDEX,
                                  "", 0.0,
                                  "eye_color", mAvatarP2A.getIrisColorValue());
                    mAvatarP2A.setIrisColorValue(values);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_iris_color, ColorConstant.getColor(ColorConstant.iris_color, values));
                    break;

                // 换装部分
                case TITLE_GLASSES_INDEX:
                    if (index == EditFaceGlassesFragment.GLASSES_COLOR) {
                        helper.record(TITLE_GLASSES_INDEX,
                                      "", 0.0,
                                      "glass_color", mAvatarP2A.getGlassesColorValue());
                        mAvatarP2A.setGlassesColorValue(pos);
                        mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_glass_color, ColorConstant.glass_color[pos]);
                    } else {
                        helper.record(TITLE_GLASSES_INDEX,
                                      "", 0.0,
                                      "glass_frame_color", mAvatarP2A.getGlassesFrameColorValue());

                        mAvatarP2A.setGlassesFrameColorValue(pos);
                        mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_glass_frame_color, ColorConstant.glass_frame_color[pos]);
                    }
                    break;
                case TITLE_HAT_INDEX:
                    helper.record(TITLE_HAT_INDEX,
                                  "", 0.0,
                                  "hat_color", mAvatarP2A.getHatColorValue());
                    mAvatarP2A.setHatColorValue(pos);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hat_color, ColorConstant.hat_color[pos]);
                    break;

            }
            updateSaveBtn();
        }

        @Override
        public void colorValuesForSeekBarListener(int id, int index, float radio, double[] values) {
            switch (id) {
                case TITLE_FACE_INDEX:
                    mAvatarP2A.setSkinColorValue(radio);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_skin_color, values);
                    break;
            }
            updateSaveBtn();
        }

        @Override
        public void colorValuesChangeStart(int id) {
            switch (id) {
                case TITLE_FACE_INDEX:
                    oldProgressValue = mAvatarP2A.getSkinColorValue();
                    break;
            }
        }

        @Override
        public void colorValuesChangeEnd(int id) {
            iv_model_redo_left.setEnabled(true);
            iv_model_reset.setEnabled(true);
            switch (id) {
                case TITLE_FACE_INDEX:
                    helper.record(TITLE_FACE_INDEX,
                                  "", 0.0,
                                  "face_color", oldProgressValue);
                    break;
            }
        }
    };

    MakeUpColorValuesChangeListener mMakeUpColorValuesChangeListener = new MakeUpColorValuesChangeListener() {
        @Override
        public void colorValuesChangeListener(int id, int type, int index, double values) {
            iv_model_redo_left.setEnabled(true);
            iv_model_reset.setEnabled(true);
            int pos = (int) values;
            double[] makeup_color = getMakeUpColorArray(type)[pos];

            switch (type) {
                // 美妆部分
                case TITLE_EYELASH_INDEX:
                    helper.record(TITLE_EYELASH_INDEX,
                                  "", 0.0,
                                  "eyelash_color", mAvatarP2A.getEyelashColorValue());
                    mAvatarP2A.setEyelashColorValue(pos);
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyelashItem.handle, Arrays.copyOf(makeup_color, 3));
                    break;
                case TITLE_EYESHADOW_INDEX:
                    helper.record(TITLE_EYESHADOW_INDEX,
                                  "", 0.0,
                                  "eyeshadow_color", mAvatarP2A.getEyeshadowColorValue());
                    mAvatarP2A.setEyeshadowColorValue(pos);
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyeshadowItem.handle, Arrays.copyOf(makeup_color, 3));
                    break;
                case TITLE_EYEBROW_INDEX:
                    helper.record(TITLE_EYEBROW_INDEX,
                                  "", 0.0,
                                  "eyebrow_color", mAvatarP2A.getEyebrowColorValue());
                    mAvatarP2A.setEyebrowColorValue(pos);
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyebrowItem.handle, Arrays.copyOf(makeup_color, 3));
                    break;
                case TITLE_LIPGLOSS_INDEX:
                    helper.record(TITLE_LIPGLOSS_INDEX,
                                  "", 0.0,
                                  "lipgloss_color", mAvatarP2A.getLipglossColorValue());
                    mAvatarP2A.setLipglossColorValue(pos);
                    mAvatarHandle.setMakeupColor(mAvatarHandle.lipglossItem.handle, Arrays.copyOf(makeup_color, 3));
                    break;
            }
        }
    };

    /**
     * 获取美妆对应的颜色
     *
     * @param type
     * @return
     */
    private double[][] getMakeUpColorArray(int type) {
        if (type != TITLE_LIPGLOSS_INDEX) {
            return ColorConstant.makeup_color;
        } else {
            return ColorConstant.lip_color;
        }
    }

    private boolean isChangeValues() {
        return (mEditFaceParameter != null && mEditFaceParameter.isShapeChangeValues()) || mAvatarP2A.compare(mDefaultAvatarP2A);
    }

    private LoadingDialog mLoadingDialog;

    private void saveAvatar() {
        mLoadingDialog = new LoadingDialog.Builder(mActivity).create();
        mLoadingDialog.show();

        new AvatarEditor(mActivity).saveAvatar(mAvatarP2A, mEditFaceParameter, new AvatarEditor.SaveAvatarListener() {
            @Override
            public void saveComplete(final AvatarPTA newAvatarP2A) {
                task = new Runnable() {
                    @Override
                    public void run() {
                        mActivity.updateAvatarP2As();
                        mActivity.setShowAvatarP2A(newAvatarP2A);
                        mAvatarHandle.setAvatar(newAvatarP2A, true, new Runnable() {
                            @Override
                            public void run() {
                                if (mLoadingDialog != null && newAvatarP2A.getBundleDir().equals(mActivity.getShowAvatarP2A().getBundleDir())) {
                                    mLoadingDialog.dismiss();
                                    mLoadingDialog = null;
                                    backToHome(newAvatarP2A);
                                }
                            }
                        });
                    }
                };
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mActivity.runOnUiThread(task);
                    task = null;
                }
            }

            @Override
            public void saveFailure() {
                task = new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                        ToastUtil.showCenterToast(mActivity, "模型保存失败，请重试");
                    }
                };
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mActivity.runOnUiThread(task);
                    task = null;
                }
            }
        });
    }

    private void setEditFacePoints(EditFacePoint[] editFacePoints, boolean isStateChange) {
        mEditFacePoints = editFacePoints;
        if (mEditPointLayout == null) return;
        if (editFacePoints == null) {
            mP2ACore.loadHalfLengthBodyCamera();
        } else {
            mP2ACore.loadBigHalfLengthBodyCamera();
        }
        mActivity.setCanController(mEditFacePoints == null);
        mEditPointLayout.post(new Runnable() {
            @Override
            public void run() {

                if (mEditFacePoints != null) {
                    if (!isStateChange)
                        mEditFaceParameter.copy();
                }
                mEditPointLayout.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                mIsFrontBox.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                ll_redo.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                iv_reset.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                mFragmentLayout.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
                mFragmentLayoutBg.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
                ll_slide_title.setVisibility(mEditFacePoints != null ? View.INVISIBLE : View.VISIBLE);
                ll_model_redo.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
                iv_model_reset.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
                mEditFaceTitle.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
                updateSaveBtn();
            }
        });
    }

    /**
     * 是否是点击切换显示状态
     *
     * @param isStateChange
     */
    private void updateEditPoint(boolean isStateChange) {
        setEditFacePoints(EditFacePointFactory.getEditPoints(mEditFaceSelectBottomId, mAvatarP2A.getGender(), isFront),
                          isStateChange);
        if (isFront) {
            mAvatarHandle.resetAllFront();
        } else {
            mAvatarHandle.resetAllSide();
        }
    }

    private void parsePoint(EditFacePoint[] editFacePoints, int width, int height, int widthView, int heightView) {
        for (EditFacePoint point : editFacePoints) {
            Point p = mAvatarHandle.getPointByIndex(point.index);

            int x = p.x;
            int y = p.y;
            y = height - y;

            float sW = (float) widthView / width;
            float sH = (float) heightView / height;
            if (sW > sH) {
                x = (int) (x * sW);
                y = (int) (y * sW + (heightView - height * sW) / 2);
            } else {
                x = (int) (x * sH + (widthView - width * sH) / 2);
                y = (int) (y * sH);
            }

            point.set(x, y);
        }
    }

    //撤销操作回调
    @Override
    public void Revoke(RecordEditBean recordEditBean, RecordEditBean goAheadBean) {
        goAheadBean.setType(recordEditBean.getType());
        if (recordEditBean.getType() == -1) {
            goAheadBean.setBundleName(recordEditBean.getBundleName());

            goAheadBean.setBundleValue(currentShapeIndex);
            goAheadBean.setList(mEditFaceParameter.getMap());
            mEditFaceParameter.reset(recordEditBean.getList());
            String bundleName = recordEditBean.getBundleName();
            int titleIndex = mEditFaceItemManager.getTitleIndex(bundleName);

            EditShapeFragment editFaceBaseFragment = (EditShapeFragment) mEditFaceBaseFragments.get(titleIndex);
            if (editFaceBaseFragment != null) {
                currentShapeIndex = (int) recordEditBean.getBundleValue();
                editFaceBaseFragment.setItem(currentShapeIndex);
            }
            updateSaveBtn();
            return;
        }

        goAheadBean.setBundleName(recordEditBean.getBundleName());
        goAheadBean.setColorName(recordEditBean.getColorName());
        boolean isSel;
        switch (recordEditBean.getType()) {
            case TITLE_HAIR_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setColorValus(mAvatarP2A.getHairColorValue());
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX)).setColorItem((int) recordEditBean.getColorValus());
                    mAvatarP2A.setHairColorValue(recordEditBean.getColorValus());
                    double[] hair_color = ColorConstant.hair_color[(int) recordEditBean.getColorValus()];
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hair_color, Arrays.copyOf(hair_color, 3));
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hair_color_intensity, (float) hair_color[3]);
                } else {
                    mEditFaceItemManager.revokeHeadType(mAvatarP2A, mEditFaceBaseFragments, recordEditBean, goAheadBean);

                    mAvatarP2A.setHairIndex((int) recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    mAvatarHandle.setAvatar(mAvatarP2A);
                }
                break;
            case TITLE_FACE_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    goAheadBean.setColorValus(mAvatarP2A.getSkinColorValue());

                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_FACE_INDEX)).setProgress(recordEditBean.getColorValus());
                    mAvatarP2A.setSkinColorValue(recordEditBean.getColorValus());
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_skin_color, ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_FACE_INDEX)).getSeekBarColorValue(recordEditBean.getColorValus()));
                } else {
                    goAheadBean.setBundleValue(((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_FACE_INDEX)).getSelectPos());
                    goAheadBean.setList(mEditFaceParameter.getMap());
                    currentShapeIndex = (int) recordEditBean.getBundleValue();
                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_FACE_INDEX)).setItem(currentShapeIndex);
                    if ((int) recordEditBean.getBundleValue() == 0) {
                        mEditFaceParameter.reset(recordEditBean.getList());
                        return;
                    } else {
                        mEditFaceParameter.setParamMap((EditParamFactory.mEditParamFace.get((int) recordEditBean.getBundleValue())).paramMap);
                    }
                }
                break;
            case TITLE_EYE_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    goAheadBean.setColorValus(mAvatarP2A.getIrisColorValue());

                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_EYE_INDEX)).setColorItem((int) recordEditBean.getColorValus());
                    mAvatarP2A.setIrisColorValue(recordEditBean.getColorValus());
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_iris_color, ColorConstant.iris_color[(int) recordEditBean.getColorValus()]);
                } else {
                    goAheadBean.setBundleValue(((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_EYE_INDEX)).getSelectPos());
                    goAheadBean.setList(mEditFaceParameter.getMap());
                    currentShapeIndex = (int) recordEditBean.getBundleValue();
                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_EYE_INDEX)).setItem(currentShapeIndex);
                    if ((int) recordEditBean.getBundleValue() == 0) {
                        mEditFaceParameter.reset(recordEditBean.getList());
                        return;
                    } else {
                        mEditFaceParameter.setParamMap((EditParamFactory.mEditParamEye.get((int) recordEditBean.getBundleValue())).paramMap);
                    }
                }
                break;
            case TITLE_MOUTH_INDEX:
                goAheadBean.setBundleValue(((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_MOUTH_INDEX)).getSelectPos());
                goAheadBean.setList(mEditFaceParameter.getMap());
                currentShapeIndex = (int) recordEditBean.getBundleValue();
                ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_MOUTH_INDEX)).setItem(currentShapeIndex);
                if ((int) recordEditBean.getBundleValue() == 0) {
                    mEditFaceParameter.reset(recordEditBean.getList());
                    return;
                } else {
                    mEditFaceParameter.setParamMap((EditParamFactory.mEditParamMouth.get((int) recordEditBean.getBundleValue())).paramMap);
                }
                break;
            case TITLE_NOSE_INDEX:
                goAheadBean.setBundleValue(((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_NOSE_INDEX)).getSelectPos());
                goAheadBean.setList(mEditFaceParameter.getMap());
                currentShapeIndex = (int) recordEditBean.getBundleValue();
                ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_NOSE_INDEX)).setItem(currentShapeIndex);
                if ((int) recordEditBean.getBundleValue() == 0) {
                    mEditFaceParameter.reset(recordEditBean.getList());
                    return;
                } else {
                    mEditFaceParameter.setParamMap((EditParamFactory.mEditParamNose.get((int) recordEditBean.getBundleValue())).paramMap);
                }
                break;
            case TITLE_BEARD_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getBeardIndex());
                mAvatarP2A.setBeardIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_BEARD_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            //美妆部分
            case TITLE_MAKE_UP:
                isSel = recordEditBean.isSel();
                goAheadBean.setSel(!isSel);
                goAheadBean.setPairBeanMap(markUpPairBeanMap);
                if (isSel) {
                    for (Integer key : recordEditBean.getPairBeanMap().keySet()) {
                        int selectPos = recordEditBean.getPairBeanMap().get(key).getSelectItemPos()
                                - recordEditBean.getPairBeanMap().get(key).getFrontLength();
                        switch (key) {
                            case TITLE_EYELASH_INDEX:
                                mAvatarP2A.setEyelashIndex(selectPos);
                                break;
                            case TITLE_EYELINER_INDEX:
                                mAvatarP2A.setEyelinerIndex(selectPos);
                                break;
                            case TITLE_EYESHADOW_INDEX:
                                mAvatarP2A.setEyeshadowIndex(selectPos);
                                break;
                            case TITLE_EYEBROW_INDEX:
                                mAvatarP2A.setEyebrowIndex(selectPos);
                                break;
                            case TITLE_PUPIL_INDEX:
                                mAvatarP2A.setPupilIndex(selectPos);
                                break;
                            case TITLE_LIPGLOSS_INDEX:
                                mAvatarP2A.setLipglossIndex(selectPos);
                                break;
                            case TITLE_FACEMAKEUP_INDEX:
                                mAvatarP2A.setFaceMakeupIndex(selectPos);
                                break;
                        }
                        if (selectPos <= 0) {
                            continue;
                        }
                        ((EditFaceMakeUpFragment) mEditFaceBaseFragments.get(TITLE_MAKE_UP)).setItem(
                                false,
                                recordEditBean.getPairBeanMap().get(key).getSelectItemPos());
                    }
                } else {
                    for (Integer key : recordEditBean.getPairBeanMap().keySet()) {
                        int selectPos = recordEditBean.getPairBeanMap().get(key).getSelectItemPos();
                        if (selectPos <= 0) {
                            continue;
                        }
                        ((EditFaceMakeUpFragment) mEditFaceBaseFragments.get(TITLE_MAKE_UP)).setItem(
                                false,
                                recordEditBean.getPairBeanMap().get(key).getSelectItemPos());
                    }
                    mAvatarP2A.setEyelashIndex(0);
                    mAvatarP2A.setEyelinerIndex(0);
                    mAvatarP2A.setEyeshadowIndex(0);
                    mAvatarP2A.setEyebrowIndex(0);
                    mAvatarP2A.setPupilIndex(0);
                    mAvatarP2A.setLipglossIndex(0);
                    mAvatarP2A.setFaceMakeupIndex(0);
                    ((EditFaceMakeUpFragment) mEditFaceBaseFragments.get(TITLE_MAKE_UP)).setItem(
                            true, 0);
                }
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_EYELASH_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    revokeMakeupColor(TITLE_EYELASH_INDEX, mAvatarHandle.eyelashItem.handle, mAvatarP2A.getEyelashColorValue(), recordEditBean, goAheadBean);
                    mAvatarP2A.setEyelashColorValue(recordEditBean.getColorValus());
                } else {
                    goAheadBean.setSel(mAvatarP2A.getEyelashIndex() > 0);
                    mAvatarP2A.setEyelashIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                    revokeMakeUp(recordEditBean, goAheadBean);
                }
                break;
            case TITLE_EYELINER_INDEX:
                goAheadBean.setSel(mAvatarP2A.getEyelinerIndex() > 0);
                mAvatarP2A.setEyelinerIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                revokeMakeUp(recordEditBean, goAheadBean);
                break;
            case TITLE_EYESHADOW_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    revokeMakeupColor(TITLE_EYESHADOW_INDEX, mAvatarHandle.eyeshadowItem.handle, mAvatarP2A.getEyeshadowColorValue(), recordEditBean, goAheadBean);
                    mAvatarP2A.setEyeshadowColorValue(recordEditBean.getColorValus());
                } else {
                    goAheadBean.setSel(mAvatarP2A.getEyeshadowIndex() > 0);
                    mAvatarP2A.setEyeshadowIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                    revokeMakeUp(recordEditBean, goAheadBean);
                }
                break;
            case TITLE_EYEBROW_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    revokeMakeupColor(TITLE_EYEBROW_INDEX, mAvatarHandle.eyebrowItem.handle, mAvatarP2A.getEyebrowColorValue(), recordEditBean, goAheadBean);
                    mAvatarP2A.setEyebrowColorValue(recordEditBean.getColorValus());
                } else {
                    goAheadBean.setSel(mAvatarP2A.getEyebrowIndex() > 0);
                    mAvatarP2A.setEyebrowIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                    revokeMakeUp(recordEditBean, goAheadBean);
                }
                break;
            case TITLE_PUPIL_INDEX:
                goAheadBean.setSel(mAvatarP2A.getPupilIndex() > 0);
                mAvatarP2A.setPupilIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                revokeMakeUp(recordEditBean, goAheadBean);
                break;
            case TITLE_LIPGLOSS_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    revokeMakeupColor(TITLE_LIPGLOSS_INDEX, mAvatarHandle.lipglossItem.handle, mAvatarP2A.getLipglossColorValue(), recordEditBean, goAheadBean);
                    mAvatarP2A.setLipglossColorValue(recordEditBean.getColorValus());
                } else {
                    goAheadBean.setSel(mAvatarP2A.getLipglossIndex() > 0);
                    mAvatarP2A.setLipglossIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                    revokeMakeUp(recordEditBean, goAheadBean);
                }
                break;
            case TITLE_FACEMAKEUP_INDEX:
                goAheadBean.setSel(mAvatarP2A.getFaceMakeupIndex() > 0);
                mAvatarP2A.setFaceMakeupIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                revokeMakeUp(recordEditBean, goAheadBean);
                break;
            // 换装部分
            case TITLE_GLASSES_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    if (recordEditBean.getColorName().equals("glass_color")) {
                        goAheadBean.setColorValus(mAvatarP2A.getGlassesColorValue());
                        goAheadBean.setColorName("glass_color");

                        mAvatarP2A.setGlassesColorValue(recordEditBean.getColorValus());
                        mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_glass_color, ColorConstant.glass_color[(int) recordEditBean.getColorValus()]);
                        ((EditFaceGlassesFragment) mEditFaceBaseFragments.get(TITLE_GLASSES_INDEX)).setGlassesColorItem((int) recordEditBean.getColorValus());
                    } else {
                        goAheadBean.setColorValus(mAvatarP2A.getGlassesFrameColorValue());
                        goAheadBean.setColorName("glass_frame_color");

                        mAvatarP2A.setGlassesFrameColorValue(recordEditBean.getColorValus());
                        mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_glass_frame_color, ColorConstant.glass_frame_color[(int) recordEditBean.getColorValus()]);
                        ((EditFaceGlassesFragment) mEditFaceBaseFragments.get(TITLE_GLASSES_INDEX)).setGlassesFrameColorItem((int) recordEditBean.getColorValus());
                    }
                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getGlassesIndex());
                    mAvatarP2A.setGlassesIndex((int) recordEditBean.getBundleValue());
                    ((EditFaceGlassesFragment) mEditFaceBaseFragments.get(TITLE_GLASSES_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    mAvatarHandle.setAvatar(mAvatarP2A);
                }
                break;
            case TITLE_HAT_INDEX:
                mEditFaceItemManager.revokeHeadType(mAvatarP2A, mEditFaceBaseFragments, recordEditBean, goAheadBean);
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_CLOTHES_INDEX:

                mEditFaceItemManager.revokeClothesType(mAvatarP2A, mEditFaceBaseFragments, recordEditBean, goAheadBean);
                setBodyLevelForClothes(mAvatarP2A.getClothesIndex(), FilePathFactory.clothesBundleRes(mAvatarP2A.getGender()));
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_CLOTHES_UPPER_INDEX:
            case TITLE_CLOTHES_LOWER_INDEX:
                mEditFaceItemManager.revokeClothesType(mAvatarP2A, mEditFaceBaseFragments, recordEditBean, goAheadBean);
                setBodyLevelForClothes(mAvatarP2A.getClothesUpperIndex(), FilePathFactory.clothUpperBundleRes());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_SHOE_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getShoeIndex());
                mAvatarP2A.setShoeIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_SHOE_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_SCENES_2D:
                goAheadBean.setBundleValue(mAvatarP2A.getBackground2DIndex());
                mAvatarP2A.setBackground2DIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_SCENES_2D)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            // 配饰部分
            case TITLE_DECORATIONS_INDEX:
                isSel = recordEditBean.isSel();
                goAheadBean.setSel(isSel);
                goAheadBean.setPairBeanMap(decorationPairBeanMap);
                if (isSel) {
                    for (Integer key : recordEditBean.getPairBeanMap().keySet()) {
                        int selectPos = recordEditBean.getPairBeanMap().get(key).getSelectItemPos()
                                - recordEditBean.getPairBeanMap().get(key).getFrontLength();
                        switch (key) {
                            case TITLE_DECORATIONS_EAR_INDEX:
                                mAvatarP2A.setDecorationsEarIndex(selectPos);
                                break;
                            case TITLE_DECORATIONS_HAND_INDEX:
                                mAvatarP2A.setDecorationsHandIndex(selectPos);
                                break;
                            case TITLE_DECORATIONS_HEAD_INDEX:
                                mAvatarP2A.setDecorationsHeadIndex(selectPos);
                                break;
                            case TITLE_DECORATIONS_FOOT_INDEX:
                                mAvatarP2A.setDecorationsFootIndex(selectPos);
                                break;
                            case TITLE_DECORATIONS_NECK_INDEX:
                                mAvatarP2A.setDecorationsNeckIndex(selectPos);
                                break;
                        }
                        if (selectPos <= 0) {
                            continue;
                        }
                        ((EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX)).setItem(
                                false,
                                recordEditBean.getPairBeanMap().get(key).getSelectItemPos());
                    }
                } else {
                    for (Integer key : recordEditBean.getPairBeanMap().keySet()) {
                        int selectPos = recordEditBean.getPairBeanMap().get(key).getSelectItemPos();
                        if (selectPos <= 0) {
                            continue;
                        }
                        ((EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX)).setItem(
                                false,
                                recordEditBean.getPairBeanMap().get(key).getSelectItemPos());
                    }
                    mAvatarP2A.setDecorationsEarIndex(0);
                    mAvatarP2A.setDecorationsHandIndex(0);
                    mAvatarP2A.setDecorationsHeadIndex(0);
                    mAvatarP2A.setDecorationsFootIndex(0);
                    mAvatarP2A.setDecorationsNeckIndex(0);
                    ((EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX)).setItem(
                            true, 0);
                }
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_DECORATIONS_EAR_INDEX:
                goAheadBean.setSel(mAvatarP2A.getDecorationsEarIndex() > 0);
                mAvatarP2A.setDecorationsEarIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                revokeDecoration(recordEditBean, goAheadBean);
                break;
            case TITLE_DECORATIONS_HEAD_INDEX:
                mEditFaceItemManager.revokeHeadType(mAvatarP2A, mEditFaceBaseFragments, recordEditBean, goAheadBean);
                goAheadBean.setSel(mAvatarP2A.getDecorationsHeadIndex() > 0);
                mAvatarP2A.setDecorationsHeadIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                if (mAvatarP2A.getHatIndex() == 0) {
                    revokeDecoration(recordEditBean, goAheadBean);
                }
                break;
            case TITLE_DECORATIONS_HAND_INDEX:
                goAheadBean.setSel(mAvatarP2A.getDecorationsHandIndex() > 0);
                mAvatarP2A.setDecorationsHandIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                revokeDecoration(recordEditBean, goAheadBean);
                break;
            case TITLE_DECORATIONS_FOOT_INDEX:
                goAheadBean.setSel(mAvatarP2A.getDecorationsFootIndex() > 0);
                mAvatarP2A.setDecorationsFootIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                revokeDecoration(recordEditBean, goAheadBean);
                break;
            case TITLE_DECORATIONS_NECK_INDEX:
                goAheadBean.setSel(mAvatarP2A.getDecorationsNeckIndex() > 0);
                mAvatarP2A.setDecorationsNeckIndex(recordEditBean.isSel() ? (int) recordEditBean.getBundleValue() : 0);
                revokeDecoration(recordEditBean, goAheadBean);
                break;
        }
        updateSaveBtn();
    }

    private void revokeMakeupColor(int type, int itemHandle, double currentMakeupColorValue, RecordEditBean recordEditBean, RecordEditBean goAheadBean) {
        // 表示当前撤销的为颜色
        goAheadBean.setBundleValue(recordEditBean.getBundleValue());
        goAheadBean.setColorValus(currentMakeupColorValue);
        PairBean pairBean = markUpPairBeanMap.get(type);
        if (pairBean != null) {
            pairBean.setSelectColorPos((int) recordEditBean.getColorValus());
        }
        ((EditFaceMakeUpFragment) mEditFaceBaseFragments.get(TITLE_MAKE_UP)).setColorItem(type, (int) recordEditBean.getColorValus());
        mAvatarHandle.setMakeupColor(itemHandle, ColorConstant.getColor(getMakeUpColorArray(type), recordEditBean.getColorValus()));
    }

    /**
     * 配饰的单一项撤销
     *
     * @param recordEditBean
     * @param goAheadBean
     */
    private void revokeDecoration(RecordEditBean recordEditBean, RecordEditBean goAheadBean) {
        int type = recordEditBean.getType();
        boolean isSel;
        isSel = recordEditBean.isSel();

        int realSelectedPos = decorationPairBeanMap.get(type).getSelectItemPos() - decorationPairBeanMap.get(type).getFrontLength();
        if (isSel) {
            if (decorationPairBeanMap.get(type).getSelectItemPos() > 0) {
                goAheadBean.setBundleValue(realSelectedPos);
            } else {
                goAheadBean.setBundleValue(0);
            }
            ((EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX)).setItem(
                    true,
                    (int) recordEditBean.getBundleValue() + decorationPairBeanMap.get(type).getFrontLength());
        } else {
            goAheadBean.setBundleValue(realSelectedPos);
            ((EditFaceDecorationFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX)).setItem(
                    false, decorationPairBeanMap.get(type).getSelectItemPos());
        }
        mAvatarHandle.setAvatar(mAvatarP2A);
    }


    /**
     * 美妆的单一项撤销
     *
     * @param recordEditBean
     * @param goAheadBean
     */
    private void revokeMakeUp(RecordEditBean recordEditBean, RecordEditBean goAheadBean) {
        int type = recordEditBean.getType();
        boolean isSel;
        isSel = recordEditBean.isSel();

        int realSelectedPos = markUpPairBeanMap.get(type).getSelectItemPos() - markUpPairBeanMap.get(type).getFrontLength();
        if (isSel) {
            if (markUpPairBeanMap.get(type).getSelectItemPos() > 0) {
                goAheadBean.setBundleValue(realSelectedPos);
            } else {
                goAheadBean.setBundleValue(0);
            }
            ((EditFaceMakeUpFragment) mEditFaceBaseFragments.get(TITLE_MAKE_UP)).setItem(
                    true,
                    (int) recordEditBean.getBundleValue() + markUpPairBeanMap.get(type).getFrontLength());
        } else {
            goAheadBean.setBundleValue(realSelectedPos);
            ((EditFaceMakeUpFragment) mEditFaceBaseFragments.get(TITLE_MAKE_UP)).setItem(
                    false, markUpPairBeanMap.get(type).getSelectItemPos());
        }
        mAvatarHandle.setAvatar(mAvatarP2A);
    }
}
