package com.faceunity.p2a_art.fragment;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.faceunity.p2a.P2AClientInterface;
import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.core.AvatarHandle;
import com.faceunity.p2a_art.core.P2AClientWrapper;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.BundleRes;
import com.faceunity.p2a_art.entity.DBHelper;
import com.faceunity.p2a_art.fragment.editface.EditFaceColorItemFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceGlassesFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceItemFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceShapeFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceSkinFragment;
import com.faceunity.p2a_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.p2a_art.ui.BottomTitleGroup;
import com.faceunity.p2a_art.ui.LoadingDialog;
import com.faceunity.p2a_art.ui.NormalDialog;
import com.faceunity.p2a_art.utils.DateUtil;
import com.faceunity.p2a_art.utils.FileUtil;
import com.faceunity.p2a_art.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;


/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceFragment extends BaseFragment
        implements View.OnClickListener {
    public static final String TAG = EditFaceFragment.class.getSimpleName();

    private AvatarP2A mDefaultAvatarP2A;
    private AvatarP2A mAvatarP2A;

    private ImageButton mSaveBtn;
    private BottomTitleGroup mEditFaceTitle;

    public static final int TITLE_HAIR_INDEX = 0;
    public static final int TITLE_SKIN_INDEX = 1;
    public static final int TITLE_FACE_INDEX = 2;
    public static final int TITLE_EYE_INDEX = 3;
    public static final int TITLE_LIP_INDEX = 4;
    public static final int TITLE_NOSE_INDEX = 5;
    public static final int TITLE_BEARD_INDEX = 6;
    public static final int TITLE_EYEBROW_INDEX = 7;
    public static final int TITLE_EYELASH_INDEX = 8;
    public static final int TITLE_GLASSES_INDEX = 9;
    public static final int TITLE_HAT_INDEX = 10;
    public static final int TITLE_CLOTHES_INDEX = 11;
    private static final int EditFaceSelectBottomCount = 12;
    private int mEditFaceSelectBottomId = TITLE_HAIR_INDEX;
    private SparseArray<EditFaceBaseFragment> mEditFaceBaseFragments = new SparseArray<>();

    private static final String[] title_boy = new String[]{"发型", "肤色", "面部", "眼睛", "嘴唇", "鼻子", "胡子", "眉毛", "眼镜", "帽子", "衣服"};
    private static final int[] title_id_boy = new int[]{
            TITLE_HAIR_INDEX,
            TITLE_SKIN_INDEX,
            TITLE_FACE_INDEX,
            TITLE_EYE_INDEX,
            TITLE_LIP_INDEX,
            TITLE_NOSE_INDEX,
            TITLE_BEARD_INDEX,
            TITLE_EYEBROW_INDEX,
            TITLE_GLASSES_INDEX,
            TITLE_HAT_INDEX,
            TITLE_CLOTHES_INDEX
    };
    private static final String[] title_girl = new String[]{"发型", "肤色", "面部", "眼睛", "嘴唇", "鼻子", "眉毛", "睫毛", "眼镜", "帽子", "衣服"};
    private static final int[] title_id_girl = new int[]{
            TITLE_HAIR_INDEX,
            TITLE_SKIN_INDEX,
            TITLE_FACE_INDEX,
            TITLE_EYE_INDEX,
            TITLE_LIP_INDEX,
            TITLE_NOSE_INDEX,
            TITLE_EYEBROW_INDEX,
            TITLE_EYELASH_INDEX,
            TITLE_GLASSES_INDEX,
            TITLE_HAT_INDEX,
            TITLE_CLOTHES_INDEX
    };

    private Runnable task;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAvatarP2A = mActivity.getShowAvatarP2A().clone();
        mDefaultAvatarP2A = mActivity.getShowAvatarP2A().clone();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_face, container, false);
        view.findViewById(R.id.edit_face_back).setOnClickListener(this);
        mSaveBtn = view.findViewById(R.id.edit_face_save);
        mSaveBtn.setOnClickListener(this);

        mEditFaceTitle = view.findViewById(R.id.edit_face_bottom_title);
        if (mAvatarP2A.getGender() == AvatarP2A.gender_boy) {
            mEditFaceTitle.setResStrings(title_boy, title_id_boy, mEditFaceSelectBottomId);
        } else {
            mEditFaceTitle.setResStrings(title_girl, title_id_girl, mEditFaceSelectBottomId);
        }
        mEditFaceTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showFragment(checkedId);
                mEditFaceSelectBottomId = checkedId;
            }
        });

        showFragment(mEditFaceSelectBottomId);
        updateSaveBtn();

        mAvatarHandle.setNeedFacePUP(true);

        setDefaultShapeValues();
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
        if (isChangeValues()) {
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
                    backToHome();

                    mAvatarHandle.setAvatar(mDefaultAvatarP2A);
                }
            });
        } else {
            backToHome();
        }
    }

    public void backToHome() {
        mActivity.showHomeFragment();
        mAvatarHandle.setNeedFacePUP(false);
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
                saveAvatar();
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
        if (isChangeValues()) {
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
            FragmentTransaction transaction = manager.beginTransaction().setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom
            );
            EditFaceBaseFragment show = mEditFaceBaseFragments.get(id);
            if (mEditFaceBaseFragments.get(mEditFaceSelectBottomId) != null) {
                transaction.hide(mEditFaceBaseFragments.get(mEditFaceSelectBottomId));
            }
            if (show == null) {
                switch (id) {
                    case TITLE_SKIN_INDEX:
                        show = new EditFaceSkinFragment();
                        double value;
                        if (mAvatarP2A.getSkinColorValue() < 0) {
                            value = mAvatarHandle.fuItemGetParamSkinColorIndex();
                            mAvatarP2A.setSkinColorValue(value);
                            mDefaultAvatarP2A.setSkinColorValue(value);
                        } else {
                            value = mAvatarP2A.getSkinColorValue();
                        }
                        ((EditFaceSkinFragment) show).initDate(value, mColorValuesChangeListener);
                        break;
                    case TITLE_FACE_INDEX:
                    case TITLE_EYE_INDEX:
                    case TITLE_LIP_INDEX:
                    case TITLE_NOSE_INDEX:
                        show = new EditFaceShapeFragment();
                        ((EditFaceShapeFragment) show).initDate(mShapeValues, mColorValuesChangeListener, new EditFaceShapeFragment.ShapeValuesChaneListener() {
                            @Override
                            public void shapeValuesChaneListener(int id, float value) {
                                updateSaveBtn();
                            }
                        });
                        break;
                    case TITLE_HAIR_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.hair_color, (int) mAvatarP2A.getHairColorValue(), mColorValuesChangeListener,
                                AvatarConstant.hairBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getHairIndex(), mItemChangeListener);
                        break;
                    case TITLE_HAT_INDEX:
                        show = new EditFaceColorItemFragment();
                        ((EditFaceColorItemFragment) show).initData(ColorConstant.hat_color, (int) mAvatarP2A.getHatColorValue(), mColorValuesChangeListener,
                                AvatarConstant.hatBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getHatIndex(), mItemChangeListener);
                        break;
                    case TITLE_BEARD_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(AvatarConstant.beardBundleRes(), mAvatarP2A.getBeardIndex(), mItemChangeListener);
                        break;
                    case TITLE_EYEBROW_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(AvatarConstant.eyebrowBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getEyebrowIndex(), mItemChangeListener);
                        break;
                    case TITLE_EYELASH_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(AvatarConstant.eyelashBundleRes(), mAvatarP2A.getEyelashIndex(), mItemChangeListener);
                        break;
                    case TITLE_CLOTHES_INDEX:
                        show = new EditFaceItemFragment();
                        ((EditFaceItemFragment) show).initData(AvatarConstant.clothesBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getClothesIndex(), mItemChangeListener);
                        break;
                    case TITLE_GLASSES_INDEX:
                        show = new EditFaceGlassesFragment();
                        ((EditFaceGlassesFragment) show).initData(ColorConstant.glass_color, (int) mAvatarP2A.getGlassesColorValue(), mColorValuesChangeListener,
                                ColorConstant.glass_frame_color, (int) mAvatarP2A.getGlassesFrameColorValue(), mColorValuesChangeListener,
                                AvatarConstant.glassesBundleRes(mAvatarP2A.getGender()), mAvatarP2A.getGlassesIndex(), mItemChangeListener);
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
            if (id == -1) {
            } else if (id == TITLE_CLOTHES_INDEX) {
                mAvatarHandle.resetAllMinTop();
            } else {
                mAvatarHandle.resetAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ItemChangeListener mItemChangeListener = new ItemChangeListener() {
        @Override
        public void itemChangeListener(int id, int pos) {
            switch (id) {
                case TITLE_HAIR_INDEX:
                    mAvatarP2A.setHairIndex(pos);
                    break;
                case TITLE_BEARD_INDEX:
                    mAvatarP2A.setBeardIndex(pos);
                    break;
                case TITLE_EYEBROW_INDEX:
                    mAvatarP2A.setEyebrowIndex(pos);
                    break;
                case TITLE_EYELASH_INDEX:
                    mAvatarP2A.setEyelashIndex(pos);
                    break;
                case TITLE_GLASSES_INDEX:
                    mAvatarP2A.setGlassesIndex(pos);
                    break;
                case TITLE_HAT_INDEX:
                    mAvatarP2A.setHatIndex(pos);
                    break;
                case TITLE_CLOTHES_INDEX:
                    mAvatarP2A.setClothesIndex(pos);
                    break;
            }
            mAvatarHandle.setAvatar(mAvatarP2A);
            updateSaveBtn();
        }
    };
    ColorValuesChangeListener mColorValuesChangeListener = new ColorValuesChangeListener() {
        @Override
        public void colorValuesChangeListener(int id, int index, double values) {
            int pos = (int) values;
            switch (id) {
                case TITLE_HAIR_INDEX:
                    mAvatarP2A.setHairColorValue(pos);
                    double[] hair_color = ColorConstant.hair_color[pos];
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hair_color, Arrays.copyOf(hair_color, 3));
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hair_color_intensity, (float) hair_color[3]);
                    break;
                case TITLE_SKIN_INDEX:
                    mAvatarP2A.setSkinColorValue(values);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_skin_color, ColorConstant.getColor(ColorConstant.skin_color, values));
                    break;
                case TITLE_EYE_INDEX:
                    mAvatarP2A.setIrisColorValue(pos);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_iris_color, ColorConstant.iris_color[pos]);
                    break;
                case TITLE_LIP_INDEX:
                    mAvatarP2A.setLipColorValue(pos);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_lip_color, ColorConstant.lip_color[pos]);
                    break;
                case TITLE_GLASSES_INDEX:
                    if (index == EditFaceGlassesFragment.GLASSES_COLOR) {
                        mAvatarP2A.setGlassesColorValue(pos);
                        mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_glass_color, ColorConstant.glass_color[pos]);
                    } else {
                        mAvatarP2A.setGlassesFrameColorValue(pos);
                        mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_glass_frame_color, ColorConstant.glass_frame_color[pos]);
                    }
                    break;
                case TITLE_HAT_INDEX:
                    mAvatarP2A.setHatColorValue(pos);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_hat_color, ColorConstant.hat_color[pos]);
                    break;
            }
            updateSaveBtn();
        }
    };

    private boolean isChangeValues() {
        return isShapeChangeValues() || mAvatarP2A.compare(mDefaultAvatarP2A);
    }

    private boolean isShapeChangeValues() {
        if (mShapeValues == null || mDefaultShapeValues == null) return false;
        for (int i = 0; i < mShapeValues.length; i++) {
            if (mShapeValues[i] != mDefaultShapeValues[i]) {
                return true;
            }
        }
        return false;
    }

    private LoadingDialog mLoadingDialog;
    private AvatarP2A mNewAvatarP2A;

    private void saveAvatar() {
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getChildFragmentManager(), LoadingDialog.TAG);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = new DBHelper(mActivity);
                File dirFile = null;
                try {
                    if (TextUtils.isEmpty(mAvatarP2A.getBundleDir())) {
                        final String dir = Constant.filePath + DateUtil.getCurrentDate() + File.separator;
                        FileUtil.createFile(dir);
                        dirFile = new File(dir);

                        mNewAvatarP2A = mAvatarP2A.clone();
                        mNewAvatarP2A.setBundleDir(dir);

                        FileUtil.copyFileTo(getResources().openRawResource(mAvatarP2A.getOriginPhotoRes()), new File(mNewAvatarP2A.getOriginPhotoThumbNail()));

                        String[] hairShowNows = mAvatarP2A.getHairFileList();
                        BundleRes[] hairBundles = AvatarConstant.hairBundleRes(mNewAvatarP2A.getGender());
                        String[] hairPaths = new String[hairBundles.length];
                        for (int i = 0; i < hairBundles.length; i++) {
                            if (!TextUtils.isEmpty(hairBundles[i].path)) {
                                FileUtil.copyFileTo(mActivity.getAssets().open(hairShowNows[i]), new File(hairPaths[i] = (dir + hairBundles[i].path)));
                            } else {
                                hairPaths[i] = "";
                            }
                        }
                        mNewAvatarP2A.setHairFileList(hairPaths);

                        if (saveAvatarHead()) {
                            FileUtil.copyFileTo(mActivity.getAssets().open(mAvatarP2A.getHeadFile()), new File(mNewAvatarP2A.getHeadFile()));
                        }
                        dbHelper.insertHistory(mNewAvatarP2A);
                    } else {
                        mNewAvatarP2A = mAvatarP2A;
                        saveAvatarHead();
                        dbHelper.updateHistory(mNewAvatarP2A);
                    }

                    task = new Runnable() {
                        @Override
                        public void run() {
                            mActivity.updateAvatarP2As();
                            mActivity.setShowAvatarP2A(mNewAvatarP2A);
                            mAvatarHandle.setAvatar(mNewAvatarP2A, true, new Runnable() {
                                @Override
                                public void run() {
                                    if (mLoadingDialog != null && mNewAvatarP2A.getBundleDir().equals(mActivity.getShowAvatarP2A().getBundleDir())) {
                                        mLoadingDialog.dismiss();
                                        mLoadingDialog = null;
                                        backToHome();
                                    }
                                }
                            });
                        }
                    };
                    if (mLoadingDialog != null && !mLoadingDialog.isStateSaved()) {
                        mActivity.runOnUiThread(task);
                        task = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (dirFile != null) {
                        dbHelper.deleteHistoryByDir(dirFile.getAbsolutePath());
                        if (dirFile.exists()) {
                            dirFile.delete();
                        }
                    }
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
            }
        }).start();
    }

    private boolean saveAvatarHead() throws IOException {
        boolean isChange = isShapeChangeValues();
        if (isChange) {
            P2AClientWrapper.deformAvatarHead(mAvatarP2A.getHeadFile().startsWith(Constant.filePath) ? new FileInputStream(new File(mAvatarP2A.getHeadFile())) : mActivity.getAssets().open(mAvatarP2A.getHeadFile()), mNewAvatarP2A.getHeadFile(), mShapeValues);
        }
        return !isChange;
    }

    private float[] mDefaultShapeValues;
    private float[] mShapeValues;

    private void setDefaultShapeValues() {
        mP2ACore.queueEvent(new Runnable() {
            @Override
            public void run() {
                mDefaultShapeValues = new float[P2AClientInterface.index_count];
                mDefaultShapeValues[P2AClientInterface.index_cheek_narrow] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_cheek_narrow);
                mDefaultShapeValues[P2AClientInterface.index_Head_fat] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Head_fat);
                mDefaultShapeValues[P2AClientInterface.index_Head_shrink] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Head_shrink);
                mDefaultShapeValues[P2AClientInterface.index_Head_stretch] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Head_stretch);
                mDefaultShapeValues[P2AClientInterface.index_HeadBone_shrink] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_HeadBone_shrink);
                mDefaultShapeValues[P2AClientInterface.index_HeadBone_stretch] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_HeadBone_stretch);
                mDefaultShapeValues[P2AClientInterface.index_jaw_lower] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_jaw_lower);
                mDefaultShapeValues[P2AClientInterface.index_jaw_up] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_jaw_up);
                mDefaultShapeValues[P2AClientInterface.index_jawbone_Narrow] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_jawbone_Narrow);
                mDefaultShapeValues[P2AClientInterface.index_jawbone_Wide] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_jawbone_Wide);
                mDefaultShapeValues[P2AClientInterface.index_lipCorner_In] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_lipCorner_In);
                mDefaultShapeValues[P2AClientInterface.index_lipCorner_Out] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_lipCorner_Out);
                mDefaultShapeValues[P2AClientInterface.index_lowerLip_Thick] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_lowerLip_Thick);
                mDefaultShapeValues[P2AClientInterface.index_lowerLip_Thin] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_lowerLip_Thin);
                mDefaultShapeValues[P2AClientInterface.index_lowerLipSide_Thick] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_lowerLipSide_Thick);
                mDefaultShapeValues[P2AClientInterface.index_mouth_Down] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_mouth_Down);
                mDefaultShapeValues[P2AClientInterface.index_mouth_Up] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_mouth_Up);
                mDefaultShapeValues[P2AClientInterface.index_nose_Down] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_nose_Down);
                mDefaultShapeValues[P2AClientInterface.index_nose_UP] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_nose_UP);
                mDefaultShapeValues[P2AClientInterface.index_noseTip_Down] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_noseTip_Down);
                mDefaultShapeValues[P2AClientInterface.index_noseTip_Up] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_noseTip_Up);
                mDefaultShapeValues[P2AClientInterface.index_nostril_In] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_nostril_In);
                mDefaultShapeValues[P2AClientInterface.index_nostril_Out] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_nostril_Out);
                mDefaultShapeValues[P2AClientInterface.index_upperLip_Thick] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_upperLip_Thick);
                mDefaultShapeValues[P2AClientInterface.index_upperLip_Thin] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_upperLip_Thin);
                mDefaultShapeValues[P2AClientInterface.index_upperLipSide_Thick] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_upperLipSide_Thick);
                mDefaultShapeValues[P2AClientInterface.index_Eye_both_in] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_both_in);
                mDefaultShapeValues[P2AClientInterface.index_Eye_both_out] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_both_out);
                mDefaultShapeValues[P2AClientInterface.index_Eye_close] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_close);
                mDefaultShapeValues[P2AClientInterface.index_Eye_down] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_down);
                mDefaultShapeValues[P2AClientInterface.index_Eye_inner_down] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_inner_down);
                mDefaultShapeValues[P2AClientInterface.index_Eye_inner_up] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_inner_up);
                mDefaultShapeValues[P2AClientInterface.index_Eye_open] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_open);
                mDefaultShapeValues[P2AClientInterface.index_Eye_outter_down] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_outter_down);
                mDefaultShapeValues[P2AClientInterface.index_Eye_outter_up] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_outter_up);
                mDefaultShapeValues[P2AClientInterface.index_Eye_up] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_Eye_up);
                mDefaultShapeValues[P2AClientInterface.index_Forehead_Narrow] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_forehead_Narrow);
                mDefaultShapeValues[P2AClientInterface.index_Forehead_Wide] = mAvatarHandle.fuItemGetParamShape(AvatarHandle.PARAM_KEY_forehead_Wide);
                mShapeValues = Arrays.copyOf(mDefaultShapeValues, mDefaultShapeValues.length);
            }
        });
    }
}
