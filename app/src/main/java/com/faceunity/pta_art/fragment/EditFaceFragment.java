package com.faceunity.pta_art.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.faceunity.pta_art.fragment.editface.EditFaceColorItemFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceGlassesFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceItemFragment;
import com.faceunity.pta_art.fragment.editface.EditShapeFragment;
import com.faceunity.pta_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager;
import com.faceunity.pta_art.fragment.editface.core.ItemChangeListener;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_BEARD;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_CLOTH;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_CLOTHLOWER;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_CLOTHUPPER;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_DECORATIONS;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_EYE;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_EYEBROW;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_EYELASH;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_EYELINER;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_EYESHADOW;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_FACE;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_FACEMAKEUP;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_GLASSES;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_HAIR;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_HAT;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_LIPGLOSS;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_MOUTH;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_NOSE;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_PUPIL;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_SCENES_2D;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_SCENES_3D;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_SCENES_ANIMATION;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.BUNDLE_NAME_SHOSE;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_BEARD_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_CLOTHES_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_CLOTHES_LOWER_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_CLOTHES_UPPER_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_DECORATIONS_INDEX;
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
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_MOUTH_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_NOSE_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_PUPIL_INDEX;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_SCENES_2D;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_SCENES_3D;
import static com.faceunity.pta_art.fragment.editface.core.EditFaceItemManager.TITLE_SCENES_ANIMATION;
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
    private BottomTitleGroup mCenterItemGroup;


    private Runnable task;

    private FrameLayout mFragmentLayout;
    private EditPointLayout mEditPointLayout;
    private EditFacePoint[] mEditFacePoints;
    private EditFaceParameter mEditFaceParameter;
    private CheckBox mIsFrontBox;
    private boolean isResetFront = false;
    private boolean isFront = true;
    private PTACore mEditP2ACore;
    //捏脸撤销按钮
    private LinearLayout ll_redo;
    private ImageView iv_redo_left, iv_redo_right;
    //形象编辑撤销按钮
    private LinearLayout ll_model_redo;
    private ImageView iv_model_redo_left, iv_model_redo_right;
    private RevokeHelper helper;
    private double oldProgressValue;

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
        int[][] titleIdAndIcons = mEditFaceItemManager.getTitleIdAndIcons(EditFaceItemManager.EDIT_FACE_TYPE_PINCH);
        mEditFaceTitle.setResIcon(titleIdAndIcons[1], titleIdAndIcons[0], mEditFaceSelectBottomId);

        mCenterItemGroup = view.findViewById(R.id.edit_face_center_type);
        mCenterItemGroup.setResStrings(mEditFaceItemManager.getCenterTypeStr(), mEditFaceItemManager.getCenterTypeIds(), 0);


        showFragment(mEditFaceSelectBottomId);
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
                }
            }
        });

        mIsFrontBox = view.findViewById(R.id.edit_shape_position);

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

        ll_model_redo = view.findViewById(R.id.ll_model_redo);
        iv_model_redo_left = view.findViewById(R.id.iv_model_redo_left);
        iv_model_redo_left.setOnClickListener(this);
        iv_model_redo_left.setEnabled(false);

        iv_model_redo_right = view.findViewById(R.id.iv_model_redo_right);
        iv_model_redo_right.setOnClickListener(this);
        iv_model_redo_right.setEnabled(false);
        helper.setListener(this);


        mCenterItemGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mEditFaceItemManager.centerTypeSelectedPosition = checkedId;
                int[][] idAndIcons = mEditFaceItemManager.getTitleIdAndIcons(checkedId);
                mEditFaceTitle.setResIcon(idAndIcons[1], idAndIcons[0], mEditFaceItemManager.getSelectedFragmentId());
                mEditFaceTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        mEditFaceTitle.smoothScrollToByCheckedId(mEditFaceItemManager.getSelectedFragmentId(), false);
                    }
                });
                showFragment(mEditFaceItemManager.getSelectedFragmentId());
                mEditFaceSelectBottomId = mEditFaceItemManager.getSelectedFragmentId();
            }
        });
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
                    helper.record(-1, mEditFaceParameter.getTemp());
                    iv_model_redo_left.setEnabled(true);
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
        mEditFaceTitle.clearCheck();
        showFragment(-1);
        return true;
    }

    private void updateSaveBtn() {
        if (isChangeValues() || mFragmentLayout.getVisibility() == View.GONE) {
            mSaveBtn.setAlpha(1.0f);
            mSaveBtn.setEnabled(true);
        } else {
            mSaveBtn.setAlpha(0.5f);
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
                    case TITLE_EYEBROW_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.makeup_color, (int) mAvatarP2A.getEyebrowColorValue(), mColorValuesChangeListener,
                                                                    FilePathFactory.eyebrowBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getEyebrowIndex(), mItemChangeListener);
                        break;
                    case TITLE_EYELASH_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.makeup_color, (int) mAvatarP2A.getEyelashColorValue(), mColorValuesChangeListener,
                                                                    FilePathFactory.eyelashBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getEyelashIndex(), mItemChangeListener);
                        break;
                    case TITLE_BEARD_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.beardBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getBeardIndex(), mItemChangeListener);
                        break;
                    // 美妆部分
                    case TITLE_EYELINER_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.eyelinerBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getEyelinerIndex(), mItemChangeListener);
                        break;
                    case TITLE_EYESHADOW_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.makeup_color, (int) mAvatarP2A.getEyeshadowColorValue(), mColorValuesChangeListener,
                                                                    FilePathFactory.eyeshadowBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getEyeshadowIndex(), mItemChangeListener);
                        break;
                    case TITLE_PUPIL_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.pupilBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getPupilIndex(), mItemChangeListener);
                        break;
                    case TITLE_LIPGLOSS_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.lip_color, (int) mAvatarP2A.getLipglossColorValue(), mColorValuesChangeListener,
                                                                    FilePathFactory.lipglossBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getLipglossIndex(), mItemChangeListener);
                        break;
                    case TITLE_FACEMAKEUP_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.facemakeupBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getFaceMakeupIndex(), mItemChangeListener);
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
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.decorationsBundleRes(), mAvatarP2A.getDecorationsIndex(), mItemChangeListener);
                        break;

                    case TITLE_SCENES_2D:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.scenes2DBundleRes(), mAvatarP2A.getBackground2DIndex(), mItemChangeListener);
                        break;
                    case TITLE_SCENES_3D:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.scenes3dBundleRes(), mAvatarP2A.getBackground3DIndex(), mItemChangeListener);
                        break;
                    case TITLE_SCENES_ANIMATION:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.scenesAniBundleRes(), mAvatarP2A.getBackgroundAniIndex(), mItemChangeListener);
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
            transaction.commit();
            if (id != -1) {
                mEditFaceItemManager.setSelectedFragmentID(id);
                mCenterItemGroup.setVisibility(View.VISIBLE);
            } else {
                mCenterItemGroup.setVisibility(View.GONE);
            }
            if (id == -1) {
                mP2ACore.loadWholeBodyCamera();
            } else if (id == TITLE_CLOTHES_INDEX
                    || id == TITLE_CLOTHES_UPPER_INDEX
                    || id == TITLE_CLOTHES_LOWER_INDEX
                    || id == TITLE_SHOE_INDEX
                    || id == TITLE_DECORATIONS_INDEX) {
                mAvatarHandle.setExpression(mAvatarP2A, new BundleRes(FilePathFactory.EXPRESSION_ANI_DRESS_UP), Integer.MAX_VALUE);
                mP2ACore.loadSmallWholeBodyCamera();

            } else if (id == TITLE_SCENES_2D
                    || id == TITLE_SCENES_3D
                    || id == TITLE_SCENES_ANIMATION) {
                mAvatarHandle.clearExpression(mAvatarP2A, true);
                mP2ACore.loadSmallWholeBodyCamera();
            } else {
                mAvatarHandle.clearExpression(mAvatarP2A, true);
                mP2ACore.loadHalfLengthBodyCamera();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int checkSelectPos(List<ParamRes> paramResList) {
        int pos = 0;
        for (int i = 0; i < paramResList.size(); i++) {
            ParamRes res = paramResList.get(i);
            if (res == null || res.paramMap == null) continue;
            boolean isEq = true;
            for (String key : res.paramMap.keySet()) {
                if (!res.paramMap.get(key).equals(mEditFaceParameter.getParamByKey(key))) {
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
            if (pos == 0) {
                mAvatarHandle.setPose(true);
                mAvatarHandle.setAvatar(mAvatarP2A);
                mP2ACore.loadBigHalfLengthBodyCamera();
                updateEditPoint(false);
            } else {
                iv_model_redo_left.setEnabled(true);
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
            switch (id) {
                case TITLE_HAIR_INDEX:
                    // 主要是考虑到了发帽
                    if (mAvatarP2A.getHatIndex() == 0) {
                        helper.record(TITLE_HAIR_INDEX,
                                      BUNDLE_NAME_HAIR, mAvatarP2A.getHairIndex(),
                                      "", 0.0);
                    } else {
                        helper.record(TITLE_HAT_INDEX,
                                      BUNDLE_NAME_HAT, mAvatarP2A.getHatIndex(),
                                      "", 0.0);
                        mAvatarP2A.setHatIndex(0);
                        EditFaceItemFragment hatFragment = (EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX);
                        if (hatFragment != null) {
                            hatFragment.setItem(0);
                        }

                    }
                    mAvatarP2A.setHairIndex(pos);

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
                            return;
                        }
                    }
                    break;
                case TITLE_EYELASH_INDEX:
                    helper.record(TITLE_EYELASH_INDEX,
                                  BUNDLE_NAME_EYELASH, mAvatarP2A.getEyelashIndex(),
                                  "", 0.0);
                    mAvatarP2A.setEyelashIndex(pos);
                    break;
                case TITLE_EYEBROW_INDEX:
                    helper.record(TITLE_EYEBROW_INDEX,
                                  BUNDLE_NAME_EYEBROW, mAvatarP2A.getEyebrowIndex(),
                                  "", 0.0);
                    mAvatarP2A.setEyebrowIndex(pos);
                    break;
                case TITLE_BEARD_INDEX:
                    helper.record(TITLE_BEARD_INDEX,
                                  BUNDLE_NAME_BEARD, mAvatarP2A.getBeardIndex(),
                                  "", 0.0);
                    mAvatarP2A.setBeardIndex(pos);
                    break;
                // 美妆部分
                case TITLE_EYELINER_INDEX:
                    helper.record(TITLE_EYELINER_INDEX,
                                  BUNDLE_NAME_EYELINER, mAvatarP2A.getEyelinerIndex(),
                                  "", 0.0);
                    mAvatarP2A.setEyelinerIndex(pos);
                    break;
                case TITLE_EYESHADOW_INDEX:
                    helper.record(TITLE_EYESHADOW_INDEX,
                                  BUNDLE_NAME_EYESHADOW, mAvatarP2A.getEyeshadowIndex(),
                                  "", 0.0);
                    mAvatarP2A.setEyeshadowIndex(pos);
                    break;
                case TITLE_PUPIL_INDEX:
                    helper.record(TITLE_PUPIL_INDEX,
                                  BUNDLE_NAME_PUPIL, mAvatarP2A.getPupilIndex(),
                                  "", 0.0);
                    mAvatarP2A.setPupilIndex(pos);
                    break;
                case TITLE_LIPGLOSS_INDEX:
                    helper.record(TITLE_LIPGLOSS_INDEX,
                                  BUNDLE_NAME_LIPGLOSS, mAvatarP2A.getLipglossIndex(),
                                  "", 0.0);
                    mAvatarP2A.setLipglossIndex(pos);
                    break;
                case TITLE_FACEMAKEUP_INDEX:
                    helper.record(TITLE_FACEMAKEUP_INDEX,
                                  BUNDLE_NAME_FACEMAKEUP, mAvatarP2A.getFaceMakeupIndex(),
                                  "", 0.0);
                    mAvatarP2A.setFaceMakeupIndex(pos);
                    break;
                // 换装部分
                case TITLE_GLASSES_INDEX:
                    helper.record(TITLE_GLASSES_INDEX,
                                  BUNDLE_NAME_GLASSES, mAvatarP2A.getGlassesIndex(),
                                  "", 0.0);
                    mAvatarP2A.setGlassesIndex(pos);
                    break;
                case TITLE_HAT_INDEX:
                    // 主要是考虑到了发帽
                    if (mAvatarP2A.getHairIndex() != 0) {
                        helper.record(TITLE_HAIR_INDEX,
                                      BUNDLE_NAME_HAIR, mAvatarP2A.getHairIndex(),
                                      "", 0.0);
                        mAvatarP2A.setHairIndex(0);
                        EditFaceColorItemFragment hatFragment = (EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
                        if (hatFragment != null) {
                            hatFragment.setItem(0);
                        }
                    } else {
                        helper.record(TITLE_HAT_INDEX,
                                      BUNDLE_NAME_HAT, mAvatarP2A.getHatIndex(),
                                      "", 0.0);
                    }
                    mAvatarP2A.setHatIndex(pos);
                    break;
                case TITLE_CLOTHES_INDEX:
                    helper.record(TITLE_CLOTHES_INDEX,
                                  BUNDLE_NAME_CLOTH, mAvatarP2A.getClothesIndex(),
                                  "", 0.0);
                    EditFaceItemFragment clothes_lower_fragment, clothes_upper_fragment;
                    if (pos == 0) {
                        if (mAvatarP2A.getClothesUpperIndex() == 0) {
                            mAvatarP2A.setClothesUpperIndex(1);
                            clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                            if (clothes_upper_fragment != null) {
                                clothes_upper_fragment.setItem(1);
                            }
                            mAvatarP2A.setGender(AvatarPTA.gender_boy);
                        }
                        if (mAvatarP2A.getClothesLowerIndex() == 0) {
                            mAvatarP2A.setClothesLowerIndex(1);
                            clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                            if (clothes_lower_fragment != null) {
                                clothes_lower_fragment.setItem(1);
                            }
                        }
                    } else {
                        if (mAvatarP2A.getClothesUpperIndex() != 0) {
                            mAvatarP2A.setClothesUpperIndex(0);
                            clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                            if (clothes_upper_fragment != null) {
                                clothes_upper_fragment.setItem(0);
                            }
                        }
                        if (mAvatarP2A.getClothesLowerIndex() != 0) {
                            mAvatarP2A.setClothesLowerIndex(0);
                            clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                            if (clothes_lower_fragment != null) {
                                clothes_lower_fragment.setItem(0);
                            }
                        }
                    }
                    mAvatarP2A.setClothesIndex(pos);
                    setBodyLevelForClothes(pos, FilePathFactory.clothesBundleRes(0));
                    break;
                case TITLE_CLOTHES_UPPER_INDEX:

                    if (pos == 0) {
                        if (mAvatarP2A.getClothesIndex() == 0) {
                            ToastUtil.showCenterToast(mActivity,
                                                      "必须有一件上衣");
                            return;
                        }
                    } else {
                        if (mAvatarP2A.getClothesIndex() != 0) {
                            hasRecord = true;
                            helper.record(TITLE_CLOTHES_INDEX,
                                          BUNDLE_NAME_CLOTH, mAvatarP2A.getClothesIndex(),
                                          "", 0.0);
                            mAvatarP2A.setClothesIndex(0);
                            EditFaceItemFragment clothes_suit_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX));
                            if (clothes_suit_fragment != null) {
                                clothes_suit_fragment.setItem(0);
                            }
                        }
                        if (mAvatarP2A.getClothesLowerIndex() == 0) {
                            EditFaceItemFragment clothes_lower_fragment1 = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                            if (clothes_lower_fragment1 != null) {
                                clothes_lower_fragment1.setItem(1);
                            }
                            mAvatarP2A.setClothesLowerIndex(1);
                        }
                    }

                    if (!hasRecord) {
                        helper.record(TITLE_CLOTHES_UPPER_INDEX,
                                      BUNDLE_NAME_CLOTHUPPER, mAvatarP2A.getClothesUpperIndex(),
                                      "", 0.0);
                    }
                    mAvatarP2A.setClothesUpperIndex(pos);
                    setBodyLevelForClothes(pos, FilePathFactory.clothUpperBundleRes());
                    break;
                case TITLE_CLOTHES_LOWER_INDEX:
                    if (pos == 0) {
                        if (mAvatarP2A.getClothesIndex() == 0) {
                            ToastUtil.showCenterToast(mActivity,
                                                      "必须有一件裤子");
                            return;
                        }
                    } else {
                        if (mAvatarP2A.getClothesIndex() != 0) {
                            hasRecord = true;
                            helper.record(TITLE_CLOTHES_INDEX,
                                          BUNDLE_NAME_CLOTH, mAvatarP2A.getClothesIndex(),
                                          "", 0.0);
                            mAvatarP2A.setClothesIndex(0);
                            EditFaceItemFragment clothes_suit_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX));
                            if (clothes_suit_fragment != null) {
                                clothes_suit_fragment.setItem(0);
                            }
                            if (mAvatarP2A.getGender() == AvatarPTA.gender_girl) {
                                mAvatarP2A.setGender(AvatarPTA.gender_boy);
                            }

                        }
                        if (mAvatarP2A.getClothesUpperIndex() == 0) {
                            EditFaceItemFragment clothes_upper_fragment1 = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                            if (clothes_upper_fragment1 != null) {
                                clothes_upper_fragment1.setItem(1);
                            }
                            mAvatarP2A.setClothesUpperIndex(1);
                        }
                    }
                    if (!hasRecord) {
                        helper.record(TITLE_CLOTHES_LOWER_INDEX,
                                      BUNDLE_NAME_CLOTHLOWER, mAvatarP2A.getClothesLowerIndex(),
                                      "", 0.0);
                    }
                    mAvatarP2A.setClothesLowerIndex(pos);
                    break;
                case TITLE_SHOE_INDEX:
                    helper.record(TITLE_SHOE_INDEX,
                                  BUNDLE_NAME_SHOSE, mAvatarP2A.getShoeIndex(),
                                  "", 0.0);
                    mAvatarP2A.setShoeIndex(pos);
                    break;
                case TITLE_DECORATIONS_INDEX:
                    helper.record(TITLE_DECORATIONS_INDEX,
                                  BUNDLE_NAME_DECORATIONS, mAvatarP2A.getDecorationsIndex(),
                                  "", 0.0);
                    mAvatarP2A.setDecorationsIndex(pos);
                    break;

                case TITLE_SCENES_2D:
                    if (mAvatarP2A.getBackgroundAniIndex() != -1) {
                        helper.record(TITLE_SCENES_ANIMATION,
                                      BUNDLE_NAME_SCENES_ANIMATION, mAvatarP2A.getBackgroundAniIndex(),
                                      "", 0.0);
                    } else if (mAvatarP2A.getBackground3DIndex() != -1) {
                        helper.record(TITLE_SCENES_3D,
                                      BUNDLE_NAME_SCENES_3D, mAvatarP2A.getBackground3DIndex(),
                                      "", 0.0);
                    } else {
                        helper.record(TITLE_SCENES_2D,
                                      BUNDLE_NAME_SCENES_2D, mAvatarP2A.getBackground2DIndex(),
                                      "", 0.0);
                    }

                    mAvatarP2A.setBackground2DIndex(pos);
                    mAvatarP2A.setBackground3DIndex(-1);
                    mAvatarP2A.setBackgroundAniIndex(-1);
                    break;

                case TITLE_SCENES_3D:
                    if (mAvatarP2A.getBackgroundAniIndex() != -1) {
                        helper.record(TITLE_SCENES_ANIMATION,
                                      BUNDLE_NAME_SCENES_ANIMATION, mAvatarP2A.getBackgroundAniIndex(),
                                      "", 0.0);
                    } else if (mAvatarP2A.getBackground3DIndex() != -1) {
                        helper.record(TITLE_SCENES_3D,
                                      BUNDLE_NAME_SCENES_3D, mAvatarP2A.getBackground3DIndex(),
                                      "", 0.0);
                    } else {
                        helper.record(TITLE_SCENES_2D,
                                      BUNDLE_NAME_SCENES_3D, mAvatarP2A.getBackground2DIndex(),
                                      "", 0.0);
                    }

                    mAvatarP2A.setBackground3DIndex(pos);
                    mAvatarP2A.setBackground2DIndex(-1);
                    mAvatarP2A.setBackgroundAniIndex(-1);
                    break;

                case TITLE_SCENES_ANIMATION:
                    if (mAvatarP2A.getBackgroundAniIndex() != -1) {
                        helper.record(TITLE_SCENES_ANIMATION,
                                      BUNDLE_NAME_SCENES_ANIMATION, mAvatarP2A.getBackgroundAniIndex(),
                                      "", 0.0);
                    } else if (mAvatarP2A.getBackground3DIndex() != -1) {
                        helper.record(TITLE_SCENES_3D,
                                      BUNDLE_NAME_SCENES_3D, mAvatarP2A.getBackground3DIndex(),
                                      "", 0.0);
                    } else {
                        helper.record(TITLE_SCENES_2D,
                                      BUNDLE_NAME_SCENES_2D, mAvatarP2A.getBackground2DIndex(),
                                      "", 0.0);
                    }

                    mAvatarP2A.setBackgroundAniIndex(pos);
                    mAvatarP2A.setBackground2DIndex(-1);
                    mAvatarP2A.setBackground3DIndex(-1);
                    break;

            }
            mAvatarHandle.setAvatar(mAvatarP2A);
            updateSaveBtn();
        }
    };


    private void setBodyLevelForClothes(int pos, List<BundleRes> bundleResList) {
        BundleRes bundleRes = bundleResList.get(pos);
        if (bundleRes != null) {
            mAvatarP2A.setGender(bundleRes.gender);
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

    ColorValuesChangeListener mColorValuesChangeListener = new ColorValuesChangeListener() {
        @Override
        public void colorValuesChangeListener(int id, int index, double values) {
            iv_model_redo_left.setEnabled(true);
            int pos = (int) values;
            switch (id) {
                case TITLE_HAIR_INDEX:
                    iv_model_redo_left.setEnabled(true);
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
                    iv_model_redo_left.setEnabled(true);
                    helper.record(TITLE_EYE_INDEX,
                                  "", 0.0,
                                  "eye_color", mAvatarP2A.getIrisColorValue());
                    mAvatarP2A.setIrisColorValue(values);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_iris_color, ColorConstant.getColor(ColorConstant.iris_color, values));
                    break;

                //美妆相关
                case TITLE_EYEBROW_INDEX:
                    iv_model_redo_left.setEnabled(true);
                    helper.record(TITLE_EYEBROW_INDEX,
                                  "", 0.0,
                                  "eyebrow_color", mAvatarP2A.getEyebrowColorValue());
                    mAvatarP2A.setEyebrowColorValue(pos);

                    double[] eyebrow_color = ColorConstant.makeup_color[pos];
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyebrowItem.handle, Arrays.copyOf(eyebrow_color, 3));
                    break;
                case TITLE_EYESHADOW_INDEX:
                    iv_model_redo_left.setEnabled(true);
                    helper.record(TITLE_EYESHADOW_INDEX,
                                  "", 0.0,
                                  "eyeshadow_color", mAvatarP2A.getEyeshadowColorValue());
                    mAvatarP2A.setEyeshadowColorValue(pos);

                    double[] eyeshadow_color = ColorConstant.makeup_color[pos];
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyeshadowItem.handle, Arrays.copyOf(eyeshadow_color, 3));
                    break;
                case TITLE_LIPGLOSS_INDEX:
                    iv_model_redo_left.setEnabled(true);
                    helper.record(TITLE_LIPGLOSS_INDEX,
                                  "", 0.0,
                                  "lipgloss_color", mAvatarP2A.getLipglossColorValue());
                    mAvatarP2A.setLipglossColorValue(pos);

                    double[] lipgloss_color = ColorConstant.lip_color[pos];
                    mAvatarHandle.setMakeupColor(mAvatarHandle.lipglossItem.handle, Arrays.copyOf(lipgloss_color, 3));
                    break;
                case TITLE_EYELASH_INDEX:
                    iv_model_redo_left.setEnabled(true);
                    helper.record(TITLE_EYELASH_INDEX,
                                  "", 0.0,
                                  "eyelash_color", mAvatarP2A.getEyelashColorValue());
                    mAvatarP2A.setEyelashColorValue(pos);

                    double[] eyelash_color = ColorConstant.makeup_color[pos];
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyelashItem.handle, Arrays.copyOf(eyelash_color, 3));
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
            switch (id) {
                case TITLE_FACE_INDEX:
                    helper.record(TITLE_FACE_INDEX,
                                  "", 0.0,
                                  "face_color", oldProgressValue);
                    break;
            }
        }
    };

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
                updateSaveBtn();
                if (mEditFacePoints != null) {
                    if (!isStateChange)
                        mEditFaceParameter.copy();
                }
                mEditPointLayout.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                mIsFrontBox.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                ll_redo.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                mFragmentLayout.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
                mCenterItemGroup.setVisibility(mEditFacePoints != null ? View.INVISIBLE : View.VISIBLE);
                ll_model_redo.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
                mEditFaceTitle.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
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
            goAheadBean.setBundleName("0");
            goAheadBean.setList(mEditFaceParameter.getMap());
            mEditFaceParameter.reset(recordEditBean.getList());
            updateSaveBtn();
            return;
        }

        goAheadBean.setBundleName(recordEditBean.getBundleName());
        goAheadBean.setColorName(recordEditBean.getColorName());
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
                    if (mAvatarP2A.getHatIndex() != 0) {
                        goAheadBean.setType(TITLE_HAT_INDEX);
                        goAheadBean.setBundleName(BUNDLE_NAME_HAT);
                        goAheadBean.setBundleValue(mAvatarP2A.getHatIndex());
                        mAvatarP2A.setHatIndex(0);
                        EditFaceItemFragment hatFragment = (EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX);
                        hatFragment.setItem(0);
                    } else {
                        goAheadBean.setBundleValue(mAvatarP2A.getHairIndex());
                    }
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
                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_FACE_INDEX)).setItem((int) recordEditBean.getBundleValue());
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
                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_EYE_INDEX)).setItem((int) recordEditBean.getBundleValue());
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
                ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_MOUTH_INDEX)).setItem((int) recordEditBean.getBundleValue());
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
                ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_NOSE_INDEX)).setItem((int) recordEditBean.getBundleValue());
                if ((int) recordEditBean.getBundleValue() == 0) {
                    mEditFaceParameter.reset(recordEditBean.getList());
                    return;
                } else {
                    mEditFaceParameter.setParamMap((EditParamFactory.mEditParamNose.get((int) recordEditBean.getBundleValue())).paramMap);
                }
                break;
            case TITLE_EYELASH_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setColorValus(mAvatarP2A.getEyelashColorValue());
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_EYELASH_INDEX)).setColorItem((int) recordEditBean.getColorValus());
                    mAvatarP2A.setEyelashColorValue(recordEditBean.getColorValus());

                    double[] eyelash_color = ColorConstant.makeup_color[(int) recordEditBean.getColorValus()];
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyelashItem.handle, Arrays.copyOf(eyelash_color, 3));
                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getEyelashIndex());
                    mAvatarP2A.setEyelashIndex((int) recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_EYELASH_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    mAvatarHandle.setAvatar(mAvatarP2A);
                }
                break;
            case TITLE_EYEBROW_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setColorValus(mAvatarP2A.getEyebrowColorValue());
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_EYEBROW_INDEX)).setColorItem((int) recordEditBean.getColorValus());
                    mAvatarP2A.setEyebrowColorValue(recordEditBean.getColorValus());

                    double[] eyebrow_color = ColorConstant.makeup_color[(int) recordEditBean.getColorValus()];
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyebrowItem.handle, Arrays.copyOf(eyebrow_color, 3));
                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getEyebrowIndex());
                    mAvatarP2A.setEyebrowIndex((int) recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_EYEBROW_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    mAvatarHandle.setAvatar(mAvatarP2A);
                }
                break;
            case TITLE_BEARD_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getBeardIndex());
                mAvatarP2A.setBeardIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_BEARD_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            // 美妆部分
            case TITLE_EYELINER_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getEyelinerIndex());
                mAvatarP2A.setEyelinerIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_EYELINER_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_EYESHADOW_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setColorValus(mAvatarP2A.getEyeshadowColorValue());
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_EYESHADOW_INDEX)).setColorItem((int) recordEditBean.getColorValus());
                    mAvatarP2A.setEyeshadowColorValue(recordEditBean.getColorValus());

                    double[] eyeshadow_color = ColorConstant.makeup_color[(int) recordEditBean.getColorValus()];
                    mAvatarHandle.setMakeupColor(mAvatarHandle.eyeshadowItem.handle, Arrays.copyOf(eyeshadow_color, 3));
                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getEyeshadowIndex());
                    mAvatarP2A.setEyeshadowIndex((int) recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_EYESHADOW_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    mAvatarHandle.setAvatar(mAvatarP2A);
                }
                break;

            case TITLE_FACEMAKEUP_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getFaceMakeupIndex());
                mAvatarP2A.setFaceMakeupIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_FACEMAKEUP_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_LIPGLOSS_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setColorValus(mAvatarP2A.getLipglossColorValue());
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_LIPGLOSS_INDEX)).setColorItem((int) recordEditBean.getColorValus());
                    mAvatarP2A.setLipglossColorValue(recordEditBean.getColorValus());

                    double[] lipgloss_color = ColorConstant.lip_color[(int) recordEditBean.getColorValus()];
                    mAvatarHandle.setMakeupColor(mAvatarHandle.lipglossItem.handle, Arrays.copyOf(lipgloss_color, 3));
                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getLipglossIndex());
                    mAvatarP2A.setLipglossIndex((int) recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_LIPGLOSS_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    mAvatarHandle.setAvatar(mAvatarP2A);
                }
                break;
            case TITLE_PUPIL_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getPupilIndex());
                mAvatarP2A.setPupilIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_PUPIL_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
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
                if (mAvatarP2A.getHairIndex() != 0) {
                    goAheadBean.setType(TITLE_HAIR_INDEX);
                    goAheadBean.setBundleName(BUNDLE_NAME_HAIR);
                    goAheadBean.setBundleValue(mAvatarP2A.getHairIndex());
                    mAvatarP2A.setHairIndex(0);
                    EditFaceColorItemFragment hatFragment = (EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAIR_INDEX);
                    hatFragment.setItem(0);
                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getHatIndex());
                }

                mAvatarP2A.setHatIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_CLOTHES_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getClothesIndex());
                mAvatarP2A.setClothesIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX)).setItem((int) recordEditBean.getBundleValue());

                int pos_cloth = mAvatarP2A.getClothesIndex();
                EditFaceItemFragment clothes_lower_fragment, clothes_upper_fragment;
                if (pos_cloth == 0) {
                    if (mAvatarP2A.getClothesUpperIndex() == 0) {
                        mAvatarP2A.setClothesUpperIndex(1);
                        clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                        if (clothes_upper_fragment != null) {
                            clothes_upper_fragment.setItem(1);
                        }
                        mAvatarP2A.setGender(AvatarPTA.gender_boy);
                    }
                    if (mAvatarP2A.getClothesLowerIndex() == 0) {
                        mAvatarP2A.setClothesLowerIndex(1);
                        clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                        if (clothes_lower_fragment != null) {
                            clothes_lower_fragment.setItem(1);
                        }
                    }
                } else {
                    if (mAvatarP2A.getClothesUpperIndex() != 0) {
                        mAvatarP2A.setClothesUpperIndex(0);
                        clothes_upper_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX));
                        if (clothes_upper_fragment != null) {
                            clothes_upper_fragment.setItem(0);
                        }
                    }
                    if (mAvatarP2A.getClothesLowerIndex() != 0) {
                        mAvatarP2A.setClothesLowerIndex(0);
                        clothes_lower_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                        if (clothes_lower_fragment != null) {
                            clothes_lower_fragment.setItem(0);
                        }
                    }
                }
                setBodyLevelForClothes(pos_cloth, FilePathFactory.clothesBundleRes(mAvatarP2A.getGender()));
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_CLOTHES_UPPER_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getClothesUpperIndex());
                mAvatarP2A.setClothesUpperIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_UPPER_INDEX)).setItem((int) recordEditBean.getBundleValue());

                int pos_cloth_upper = mAvatarP2A.getClothesUpperIndex();
                if (pos_cloth_upper == 0) {
                    if (mAvatarP2A.getClothesIndex() == 0) {
                        ToastUtil.showCenterToast(mActivity,
                                                  "必须有一件上衣");
                        return;
                    }
                } else {
                    if (mAvatarP2A.getClothesIndex() != 0) {

                        mAvatarP2A.setClothesIndex(0);
                        EditFaceItemFragment clothes_suit_fragment = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX));
                        if (clothes_suit_fragment != null) {
                            clothes_suit_fragment.setItem(0);
                        }
                    }
                    if (mAvatarP2A.getClothesLowerIndex() == 0) {
                        EditFaceItemFragment clothes_lower_fragment1 = ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX));
                        if (clothes_lower_fragment1 != null) {
                            clothes_lower_fragment1.setItem(1);
                        }
                        mAvatarP2A.setClothesLowerIndex(1);
                    }
                }
                setBodyLevelForClothes(pos_cloth_upper, FilePathFactory.clothUpperBundleRes());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_CLOTHES_LOWER_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getClothesLowerIndex());
                mAvatarP2A.setClothesLowerIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_LOWER_INDEX)).setItem((int) recordEditBean.getBundleValue());

                int pos_cloth_lower = mAvatarP2A.getClothesLowerIndex();
                if (pos_cloth_lower == 0) {
                    if (mAvatarP2A.getClothesIndex() == 0) {
                        ToastUtil.showCenterToast(mActivity,
                                                  "必须有一件裤子");
                        return;
                    }
                } else {
                    if (mAvatarP2A.getClothesIndex() != 0) {
                        mAvatarP2A.setClothesIndex(0);
                    }
                    if (mAvatarP2A.getClothesUpperIndex() == 0) {
                        mAvatarP2A.setClothesUpperIndex(1);
                    }
                }

                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_SHOE_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getShoeIndex());
                mAvatarP2A.setShoeIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_SHOE_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_DECORATIONS_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getDecorationsIndex());
                mAvatarP2A.setDecorationsIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_DECORATIONS_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_SCENES_2D:
                goAheadBean.setBundleValue(mAvatarP2A.getBackground2DIndex());
                mAvatarP2A.setBackground2DIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_SCENES_2D)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_SCENES_3D:
                goAheadBean.setBundleValue(mAvatarP2A.getBackground3DIndex());
                mAvatarP2A.setBackground3DIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_SCENES_3D)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
            case TITLE_SCENES_ANIMATION:
                goAheadBean.setBundleValue(mAvatarP2A.getBackgroundAniIndex());
                mAvatarP2A.setBackgroundAniIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_SCENES_ANIMATION)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
        }
        updateSaveBtn();
    }
}
