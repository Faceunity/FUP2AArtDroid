package com.faceunity.p2a_art.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.RadioGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.constant.FilePathFactory;
import com.faceunity.p2a_art.core.AvatarHandle;
import com.faceunity.p2a_art.core.P2ACore;
import com.faceunity.p2a_art.core.client.AvatarEditor;
import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.fragment.editface.EditFaceColorItemFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceGlassesFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceItemFragment;
import com.faceunity.p2a_art.fragment.editface.EditShapeFragment;
import com.faceunity.p2a_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditFaceParameter;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditFacePoint;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditFacePointFactory;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditParamFactory;
import com.faceunity.p2a_art.fragment.editface.core.shape.EditPointLayout;
import com.faceunity.p2a_art.fragment.editface.core.shape.ParamRes;
import com.faceunity.p2a_art.ui.BottomTitleGroup;
import com.faceunity.p2a_art.ui.LoadingDialog;
import com.faceunity.p2a_art.ui.NormalDialog;
import com.faceunity.p2a_art.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    private P2ACore mEditP2ACore;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAvatarP2A = mActivity.getShowAvatarP2A().clone();
        mDefaultAvatarP2A = mActivity.getShowAvatarP2A().clone();
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
        mEditFaceTitle.setResStrings(title, title_id, mEditFaceSelectBottomId);
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
        mAvatarHandle.setAvatar(mAvatarP2A);

        mEditP2ACore = new P2ACore(mP2ACore) {
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
            public void onScrollListener(EditFacePoint point, float distanceX, float distanceY) {
                if (mEditFaceParameter != null) {
                    mEditFaceParameter.setParamFaceShape(point, distanceX, distanceY);
                }
                updateSaveBtn();
            }
        });

        mIsFrontBox = view.findViewById(R.id.edit_shape_position);
        mIsFrontBox = view.findViewById(R.id.edit_shape_position);
        mIsFrontBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFront = isChecked;
                updateEditPoint();
            }
        });

        EditFacePointFactory.init(mActivity);
        EditParamFactory.init(mActivity);

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

    public void backToHome(AvatarP2A avatarP2A) {
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
                    case TITLE_FACE_INDEX:
                        show = new EditShapeFragment();
                        double value;
                        if (mAvatarP2A.getSkinColorValue() < 0) {
                            value = mAvatarHandle.fuItemGetParamSkinColorIndex();
                            mAvatarP2A.setSkinColorValue(value);
                            mDefaultAvatarP2A.setSkinColorValue(value);
                        } else {
                            value = mAvatarP2A.getSkinColorValue();
                        }
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamFace, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamFace), ColorConstant.skin_color, value, mColorValuesChangeListener);
                        break;
                    case TITLE_EYE_INDEX:
                        show = new EditShapeFragment();
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamEye, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamEye), ColorConstant.iris_color, mAvatarP2A.getIrisColorValue(), mColorValuesChangeListener);
                        break;
                    case TITLE_MOUTH_INDEX:
                        show = new EditShapeFragment();
                        ((EditShapeFragment) show).initDate(EditParamFactory.mEditParamMouth, mEditFaceStatusChaneListener, checkSelectPos(EditParamFactory.mEditParamMouth), ColorConstant.lip_color, mAvatarP2A.getLipColorValue(), mColorValuesChangeListener);
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
        public void editFacePointChaneListener(int pos, ParamRes res) {
            if (pos == 0) {
                updateEditPoint();
            } else {
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
            switch (id) {
                case TITLE_HAIR_INDEX:
                    mAvatarP2A.setHairIndex(pos);

                    if (mAvatarP2A.getHairFile().contains(Constant.filePath)) {
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
                    //Log.e("ssss", "file=" + mAvatarP2A.getHairFile());
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
            mLoadingDialogHair.dismiss();
            isStartLoading = false;
            mAvatarHandle.setAvatar(mAvatarP2A);
            updateSaveBtn();
        }
    }

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
                case TITLE_FACE_INDEX:
                    mAvatarP2A.setSkinColorValue(values);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_skin_color, ColorConstant.getColor(ColorConstant.skin_color, values));
                    break;
                case TITLE_EYE_INDEX:
                    mAvatarP2A.setIrisColorValue(values);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_iris_color, ColorConstant.getColor(ColorConstant.iris_color, values));
                    break;
                case TITLE_MOUTH_INDEX:
                    mAvatarP2A.setLipColorValue(values);
                    mAvatarHandle.fuItemSetParamFuItemHandler(AvatarHandle.PARAM_KEY_lip_color, ColorConstant.getColor(ColorConstant.lip_color, values));
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
        return (mEditFaceParameter != null && mEditFaceParameter.isShapeChangeValues()) || mAvatarP2A.compare(mDefaultAvatarP2A);
    }

    private LoadingDialog mLoadingDialog;

    private void saveAvatar() {
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getChildFragmentManager(), LoadingDialog.TAG);

        new AvatarEditor(mActivity).saveAvatar(mAvatarP2A, mEditFaceParameter, new AvatarEditor.SaveAvatarListener() {
            @Override
            public void saveComplete(final AvatarP2A newAvatarP2A) {
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
                mEditPointLayout.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                mIsFrontBox.setVisibility(mEditFacePoints == null ? View.GONE : View.VISIBLE);
                mFragmentLayout.setVisibility(mEditFacePoints != null ? View.GONE : View.VISIBLE);
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
}
