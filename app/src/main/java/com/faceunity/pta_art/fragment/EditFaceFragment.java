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
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.RecordEditBean;
import com.faceunity.pta_art.fragment.editface.EditFaceColorItemFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceGlassesFragment;
import com.faceunity.pta_art.fragment.editface.EditFaceItemFragment;
import com.faceunity.pta_art.fragment.editface.EditShapeFragment;
import com.faceunity.pta_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    public static final int TITLE_HAIR_INDEX = 0;
    public static final int TITLE_FACE_INDEX = 1;
    public static final int TITLE_EYE_INDEX = 2;
    public static final int TITLE_MOUTH_INDEX = 3;
    public static final int TITLE_NOSE_INDEX = 4;
    public static final int TITLE_BEARD_INDEX = 5;
    public static final int TITLE_EYEBROW_INDEX = 6;
    public static final int TITLE_EYELASH_INDEX = 7;
    public static final int TITLE_GLASSES_INDEX = 8;
    public static final int TITLE_HAT_INDEX = 9;
    public static final int TITLE_CLOTHES_INDEX = 10;
    public static final int TITLE_SHOE_INDEX = 11;
    private static final int EditFaceSelectBottomCount = 12;
    private int mEditFaceSelectBottomId = TITLE_HAIR_INDEX;
    private SparseArray<EditFaceBaseFragment> mEditFaceBaseFragments = new SparseArray<>();

    private static final String[] title_final = new String[]{"发型", "脸型", "眼型", "嘴型", "鼻型", "胡子", "眉毛", "睫毛", "眼镜", "帽子", "衣服", "鞋子"};
    private String[] title;
    private int[] title_id;

    private void updateTitle(int gender) {
        List<Integer> titleT = new ArrayList<>();
        if (FilePathFactory.hairBundleRes(gender).size() > 1)
            titleT.add(TITLE_HAIR_INDEX);
        titleT.add(TITLE_FACE_INDEX);
        titleT.add(TITLE_EYE_INDEX);
        titleT.add(TITLE_MOUTH_INDEX);
        titleT.add(TITLE_NOSE_INDEX);
        if (FilePathFactory.beardBundleRes(gender).size() > 1)
            titleT.add(TITLE_BEARD_INDEX);
        if (FilePathFactory.eyebrowBundleRes(gender).size() > 1)
            titleT.add(TITLE_EYEBROW_INDEX);
        if (FilePathFactory.eyelashBundleRes(gender).size() > 1)
            titleT.add(TITLE_EYELASH_INDEX);
        if (FilePathFactory.glassesBundleRes(gender).size() > 1)
            titleT.add(TITLE_GLASSES_INDEX);
        if (FilePathFactory.hatBundleRes(gender).size() > 1)
            titleT.add(TITLE_HAT_INDEX);
        if (FilePathFactory.clothesBundleRes(gender).size() > 1)
            titleT.add(TITLE_CLOTHES_INDEX);
        if (FilePathFactory.shoeBundleRes(gender).size() > 1)
            titleT.add(TITLE_SHOE_INDEX);

        title = new String[titleT.size()];
        title_id = new int[titleT.size()];

        for (int i = 0; i < titleT.size(); i++) {
            title[i] = title_final[title_id[i] = titleT.get(i)];
        }
    }

    private Runnable task;

    private FrameLayout mFragmentLayout;
    private EditPointLayout mEditPointLayout;
    private EditFacePoint[] mEditFacePoints;
    private EditFaceParameter mEditFaceParameter;
    private CheckBox mIsFrontBox;
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
//    private boolean isModelRedoHide = false;

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
        updateTitle(mAvatarP2A.getGender());
        mEditFaceTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1)
                    mEditFaceTitle.clearCheck();
                showFragment(checkedId);
                mEditFaceSelectBottomId = checkedId;
            }
        });
        mEditFaceTitle.setResStrings(title, title_id, mEditFaceSelectBottomId);

        showFragment(mEditFaceSelectBottomId);
        updateSaveBtn();

        mAvatarHandle.setNeedFacePUP(true);
        mAvatarHandle.setAvatar(mAvatarP2A);

        mEditP2ACore = new PTACore(mP2ACore) {
            @Override
            public int onDrawFrame(byte[] img, int tex, int w, int h) {
                int fuTex = super.onDrawFrame(img, tex, w, h);
                if (mEditFacePoints != null) {
                    parsePoint(mEditFacePoints,
                            mCameraRenderer.getCameraWidth(),
                            mCameraRenderer.getCameraHeight(),
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
                updateEditPoint();
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
            setEditFacePoints(null);
            EditShapeFragment shapeFragment = (EditShapeFragment) editFaceBaseFragment;
            shapeFragment.resetSelect();
            mEditFaceParameter.resetToTemp();
            mEditFaceParameter.clearRevoke();
            iv_redo_right.setEnabled(false);
            iv_redo_left.setEnabled(false);
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
        mActivity.showHomeFragment();
        mAvatarHandle.setNeedFacePUP(false);
        mActivity.setCanController(true);
        mFUP2ARenderer.setFUCore(mP2ACore);
        mAvatarHandle.setAvatar(avatarP2A);
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
                    setEditFacePoints(null);
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
//        if (id == -1) {
//            isModelRedoHide = true;
//        } else {
//            isModelRedoHide = false;
//        }
        try {
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
//            FragmentTransaction transaction = manager.beginTransaction().setCustomAnimations(
//                    R.anim.slide_in_bottom,
//                    R.anim.slide_out_bottom,
//                    R.anim.slide_in_bottom,
//                    R.anim.slide_out_bottom
//            );
            EditFaceBaseFragment show = mEditFaceBaseFragments.get(id);
            if (mEditFaceBaseFragments.get(mEditFaceSelectBottomId) != null) {
                transaction.hide(mEditFaceBaseFragments.get(mEditFaceSelectBottomId));
            }
            if (show == null) {
                switch (id) {
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
                        //((EditShapeFragment) show).setProgress(value);
                        break;
                    case TITLE_EYE_INDEX:
                        show = new EditShapeFragment();
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamEye, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamEye), ColorConstant.iris_color, mAvatarP2A.getIrisColorValue(), mColorValuesChangeListener);
                        //((EditShapeFragment) show).setColorPickGradient();
                        //((EditShapeFragment) show).setProgress(mAvatarP2A.getIrisColorValue());
                        break;
                    case TITLE_MOUTH_INDEX:
                        show = new EditShapeFragment();
                        double mouthValue;
                        if (mAvatarP2A.getLipColorValue() < 0) {
                            mouthValue = mAvatarHandle.fuItemGetParamLipColorIndex();
                            mAvatarP2A.setLipColorValue(mouthValue);
                            mDefaultAvatarP2A.setLipColorValue(mouthValue);
                        } else {
                            mouthValue = mAvatarP2A.getLipColorValue();
                        }
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamMouth, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamMouth), ColorConstant.lip_color, mouthValue, mColorValuesChangeListener);
                        //((EditShapeFragment) show).setColorPickGradient();
                        //((EditShapeFragment) show).setProgress(mouthValue);
                        break;
                    case TITLE_NOSE_INDEX:
                        show = new EditShapeFragment();
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamNose, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamNose));
                        break;
                    case TITLE_HAIR_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.hair_color, (int) mAvatarP2A.getHairColorValue(), mColorValuesChangeListener,
                                FilePathFactory.hairBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getHairIndex(), mItemChangeListener);
                        break;
                    case TITLE_HAT_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.hat_color, (int) mAvatarP2A.getHatColorValue(), mColorValuesChangeListener,
                                FilePathFactory.hatBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getHatIndex(), mItemChangeListener);
                        break;
                    case TITLE_BEARD_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.beardBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getBeardIndex(), mItemChangeListener);
                        break;
                    case TITLE_EYEBROW_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.eyebrowBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getEyebrowIndex(), mItemChangeListener);
                        break;
                    case TITLE_EYELASH_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.eyelashBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getEyelashIndex(), mItemChangeListener);
                        break;
                    case TITLE_CLOTHES_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.clothesBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getClothesIndex(), mItemChangeListener);
                        break;
                    case TITLE_SHOE_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(FilePathFactory.shoeBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getShoeIndex(), mItemChangeListener);
                        break;
                    case TITLE_GLASSES_INDEX:
                        show = new EditFaceGlassesFragment();
                        ((EditFaceGlassesFragment) show).initData(ColorConstant.glass_color, (int) mAvatarP2A.getGlassesColorValue(), mColorValuesChangeListener,
                                ColorConstant.glass_frame_color, (int) mAvatarP2A.getGlassesFrameColorValue(), mColorValuesChangeListener,
                                FilePathFactory.glassesBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getGlassesIndex(), mItemChangeListener);
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
            setEditFacePoints(null);

            if (id == -1 || id == TITLE_FACE_INDEX) {
            } else if (id == TITLE_CLOTHES_INDEX) {
                mAvatarHandle.resetAllMinTop();
            } else if (id == TITLE_SHOE_INDEX) {
                mAvatarHandle.resetAllMinBottom();
            } else {
                mAvatarHandle.resetAll();
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
                updateEditPoint();
            } else {
                iv_model_redo_left.setEnabled(true);
                switch (id) {
                    case TITLE_FACE_INDEX:
                        helper.record(id,
                                "face", lastPos,
                                "", 0.0);
                        break;
                    case TITLE_EYE_INDEX:
                        helper.record(id,
                                "eye", lastPos,
                                "", 0.0);
                        break;
                    case TITLE_MOUTH_INDEX:
                        helper.record(id,
                                "mouth", lastPos,
                                "", 0.0);
                        break;
                    case TITLE_NOSE_INDEX:
                        helper.record(id,
                                "nose", lastPos,
                                "", 0.0);
                        break;
                }

                mEditFaceParameter.setParamMap(res.paramMap);
                updateSaveBtn();
            }
        }
    };

    private LoadingDialog mLoadingDialogHair;
    private boolean isStartLoading = false;

    ItemChangeListener mItemChangeListener = new ItemChangeListener() {
        @Override
        public void itemChangeListener(int id, int pos) {
            iv_model_redo_left.setEnabled(true);
            switch (id) {
                case TITLE_HAIR_INDEX:
                    helper.record(TITLE_HAIR_INDEX,
                            "hair", mAvatarP2A.getHairIndex(),
                            "", 0.0);
                    mAvatarP2A.setHairIndex(pos);

                    if (pos > 0 && mAvatarP2A.getHairFile().startsWith(Constant.filePath)) {
                        File file = new File(mAvatarP2A.getHairFile());
                        if (!file.exists() || file.length() <= 0) {
                            if (mLoadingDialogHair == null) {
                                mLoadingDialogHair = new LoadingDialog();
                                mLoadingDialogHair.setLoadingStr("头发下载中...");
                            }
                            mLoadingDialogHair.show(getChildFragmentManager(), LoadingDialog.TAG);
                            isStartLoading = true;
                            return;
                        }
                    }
                    break;
                case TITLE_BEARD_INDEX:
                    helper.record(TITLE_BEARD_INDEX,
                            "beard", mAvatarP2A.getBeardIndex(),
                            "", 0.0);
                    mAvatarP2A.setBeardIndex(pos);
                    break;
                case TITLE_EYEBROW_INDEX:
                    mAvatarP2A.setEyebrowIndex(pos);
                    break;
                case TITLE_EYELASH_INDEX:
                    mAvatarP2A.setEyelashIndex(pos);
                    break;
                case TITLE_GLASSES_INDEX:
                    helper.record(TITLE_GLASSES_INDEX,
                            "glasses", mAvatarP2A.getGlassesIndex(),
                            "", 0.0);
                    mAvatarP2A.setGlassesIndex(pos);
                    break;
                case TITLE_HAT_INDEX:
                    helper.record(TITLE_HAT_INDEX,
                            "hat", mAvatarP2A.getHatIndex(),
                            "", 0.0);
                    mAvatarP2A.setHatIndex(pos);
                    break;
                case TITLE_CLOTHES_INDEX:
                    helper.record(TITLE_CLOTHES_INDEX,
                            "cloth", mAvatarP2A.getClothesIndex(),
                            "", 0.0);
                    mAvatarP2A.setClothesIndex(pos);
                    break;
                case TITLE_SHOE_INDEX:
                    mAvatarP2A.setShoeIndex(pos);
                    break;
            }
            mAvatarHandle.setAvatar(mAvatarP2A);
            updateSaveBtn();
        }
    };

    @Override
    public void onComplete() {
        if (mLoadingDialogHair != null && isStartLoading) {
            File file = new File(mAvatarP2A.getBundleDir() + "hair_down.json");
            if (file.exists()) {
                try {
                    String info = FileUtil.readTextFile(mAvatarP2A.getBundleDir() + "hair_down.json");
                    JSONObject jsonObject = new JSONObject(info);
                    int hair_down_state = jsonObject.getInt("down_hair_end");
                    if (hair_down_state == 1) {
                        mLoadingDialogHair.dismiss();
                        isStartLoading = false;
                        mAvatarHandle.setAvatar(mAvatarP2A);
                        updateSaveBtn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ColorValuesChangeListener mColorValuesChangeListener = new ColorValuesChangeListener() {
        @Override
        public void colorValuesChangeListener(int id, int index, double values) {
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
                case TITLE_MOUTH_INDEX:
                    iv_model_redo_left.setEnabled(true);
                    helper.record(TITLE_MOUTH_INDEX,
                            "", 0.0,
                            "eye_color", mAvatarP2A.getLipColorValue());
                    mAvatarP2A.setLipColorValue(values);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_lip_color, ColorConstant.getColor(ColorConstant.lip_color, values));
                    break;
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
//                case TITLE_EYE_INDEX:
//                    mAvatarP2A.setIrisColorValue(radio);
//                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_iris_color, values);
//                    break;
//                case TITLE_MOUTH_INDEX:
//                    mAvatarP2A.setLipColorValue(radio);
//                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_lip_color, values);
//                    break;
            }
            updateSaveBtn();
        }

        @Override
        public void colorValuesChangeStart(int id) {
            switch (id) {
                case TITLE_FACE_INDEX:
                    oldProgressValue = mAvatarP2A.getSkinColorValue();
                    break;
//                case TITLE_EYE_INDEX:
//                    oldProgressValue = mAvatarP2A.getIrisColorValue();
//                    break;
//                case TITLE_MOUTH_INDEX:
//                    oldProgressValue = mAvatarP2A.getLipColorValue();
//                    break;
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
//                case TITLE_EYE_INDEX:
//                    helper.record(TITLE_EYE_INDEX,
//                            "", 0.0,
//                            "eye_color", oldProgressValue);
//                    break;
//                case TITLE_MOUTH_INDEX:
//                    helper.record(TITLE_MOUTH_INDEX,
//                            "", 0.0,
//                            "mouth_color", oldProgressValue);
//                    break;
            }
        }
    };

    private boolean isChangeValues() {
        return (mEditFaceParameter != null && mEditFaceParameter.isShapeChangeValues()) || mAvatarP2A.compare(mDefaultAvatarP2A);
    }

    private LoadingDialog mLoadingDialog;

    private void saveAvatar() {
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getChildFragmentManager(), LoadingDialog.TAG);

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
                if (mLoadingDialog != null && !mLoadingDialog.isStateSaved()) {
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
                if (mLoadingDialog != null && !mLoadingDialog.isStateSaved()) {
                    mActivity.runOnUiThread(task);
                    task = null;
                }
            }
        });
    }

    private void setEditFacePoints(EditFacePoint[] editFacePoints) {
        mEditFacePoints = editFacePoints;
        if (mEditPointLayout == null) return;
        if (mEditFacePoints == null) {
            mAvatarHandle.resetAll();
        }
        mActivity.setCanController(mEditFacePoints == null);
        mEditPointLayout.post(new Runnable() {
            @Override
            public void run() {
                updateSaveBtn();
                if (mEditFacePoints != null) {
                    mEditFaceParameter.copy();
                }
                mEditPointLayout.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                mIsFrontBox.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                ll_redo.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                mFragmentLayout.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
                ll_model_redo.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
//                if (isModelRedoHide) {
//                    ll_model_redo.setVisibility(View.GONE);
//                }
                mEditFaceTitle.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void updateEditPoint() {
        setEditFacePoints(EditFacePointFactory.getEditPoints(mEditFaceSelectBottomId, mAvatarP2A.getGender(), isFront));
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
            x = width - x;
            y = height - y;

            float sW = (float) heightView / width;
            float sH = (float) widthView / height;
            if (sW > sH) {
                x = (int) (x * sW);
                y = (int) (y * sW - (height * sW - widthView) / 2);
            } else {
                x = (int) (x * sH - (width * sH - heightView) / 2);
                y = (int) (y * sH);
            }

            point.set(y, x);
        }
    }

    //撤销操作回调
    @Override
    public void Revoke(RecordEditBean recordEditBean, RecordEditBean goAheadBean) {
        goAheadBean.setType(recordEditBean.getType());
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
                    goAheadBean.setBundleValue(mAvatarP2A.getHairIndex());
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
                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_FACE_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    if ((int) recordEditBean.getBundleValue() == 0) {
                        mEditFaceParameter.resetParamMap();
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
                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_EYE_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    if ((int) recordEditBean.getBundleValue() == 0) {
                        mEditFaceParameter.resetParamMap();
                        return;
                    } else {
                        mEditFaceParameter.setParamMap((EditParamFactory.mEditParamEye.get((int) recordEditBean.getBundleValue())).paramMap);
                    }
                }
                break;
            case TITLE_MOUTH_INDEX:
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    goAheadBean.setColorValus(mAvatarP2A.getLipColorValue());

                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_MOUTH_INDEX)).setColorItem((int) recordEditBean.getColorValus());
                    mAvatarP2A.setLipColorValue(recordEditBean.getColorValus());
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_lip_color, ColorConstant.lip_color[(int) recordEditBean.getColorValus()]);
                } else {
                    goAheadBean.setBundleValue(((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_MOUTH_INDEX)).getSelectPos());
                    ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_MOUTH_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    if ((int) recordEditBean.getBundleValue() == 0) {
                        mEditFaceParameter.resetParamMap();
                        return;
                    } else {
                        mEditFaceParameter.setParamMap((EditParamFactory.mEditParamMouth.get((int) recordEditBean.getBundleValue())).paramMap);
                    }
                }
                break;
            case TITLE_NOSE_INDEX:
                goAheadBean.setBundleValue(((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_NOSE_INDEX)).getSelectPos());
                ((EditShapeFragment) mEditFaceBaseFragments.get(TITLE_NOSE_INDEX)).setItem((int) recordEditBean.getBundleValue());
                if ((int) recordEditBean.getBundleValue() == 0) {
                    mEditFaceParameter.resetParamMap();
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
                goAheadBean.setColorValus(mAvatarP2A.getHatColorValue());
                if (TextUtils.isEmpty(recordEditBean.getBundleName())) {
                    goAheadBean.setBundleValue(recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX)).setColorItem((int) recordEditBean.getColorValus());
                    mAvatarP2A.setHatColorValue(recordEditBean.getColorValus());
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hat_color, ColorConstant.hat_color[(int) recordEditBean.getColorValus()]);
                } else {
                    goAheadBean.setBundleValue(mAvatarP2A.getHatIndex());
                    mAvatarP2A.setHatIndex((int) recordEditBean.getBundleValue());
                    ((EditFaceColorItemFragment) mEditFaceBaseFragments.get(TITLE_HAT_INDEX)).setItem((int) recordEditBean.getBundleValue());
                    mAvatarHandle.setAvatar(mAvatarP2A);
                }
                break;
            case TITLE_CLOTHES_INDEX:
                goAheadBean.setBundleValue(mAvatarP2A.getClothesIndex());
                mAvatarP2A.setClothesIndex((int) recordEditBean.getBundleValue());
                ((EditFaceItemFragment) mEditFaceBaseFragments.get(TITLE_CLOTHES_INDEX)).setItem((int) recordEditBean.getBundleValue());
                mAvatarHandle.setAvatar(mAvatarP2A);
                break;
        }
        updateSaveBtn();
    }
}
