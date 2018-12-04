package com.faceunity.p2a_art.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.constant.Constant;
import com.faceunity.p2a_art.constant.DBHelper;
import com.faceunity.p2a_art.core.AvatarP2A;
import com.faceunity.p2a_art.core.FUP2ARenderer;
import com.faceunity.p2a_art.core.P2AClientWrapper;
import com.faceunity.p2a_art.fragment.editface.EditFaceItemFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceColorFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceGlassesFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceColorItemFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceShapeFragment;
import com.faceunity.p2a_art.fragment.editface.EditFaceSkinFragment;
import com.faceunity.p2a_art.fragment.editface.core.ColorControllerListener;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemControllerListener;
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
        implements View.OnClickListener,
        FUP2ARenderer.OnLoadBodyListener,
        ItemControllerListener,
        ColorControllerListener {
    public static final String TAG = EditFaceFragment.class.getSimpleName();

    private AvatarP2A mAvatarP2A;

    private ImageButton mSaveBtn;

    public static final int TITLE_SHAPE_INDEX = 0;
    public static final int TITLE_SKIN_INDEX = 1;
    public static final int TITLE_LIP_INDEX = 2;
    public static final int TITLE_IRIS_INDEX = 3;
    public static final int TITLE_HAIR_INDEX = 4;
    public static final int TITLE_GLASSES_INDEX = 5;
    public static final int TITLE_BEARD_INDEX = 6;
    public static final int TITLE_CLOTHES_INDEX = 7;
    public static final int TITLE_HAT_INDEX = 8;
    private static final int EditFaceSelectBottomCount = 9;
    private int mEditFaceSelectBottomId = TITLE_SHAPE_INDEX;
    private Runnable task;

    private SparseArray<EditFaceBaseFragment> mEditFaceBaseFragments = new SparseArray<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAvatarP2A = mActivity.getShowAvatarP2A().clone();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_face, container, false);

        view.findViewById(R.id.edit_face_back).setOnClickListener(this);
        mSaveBtn = view.findViewById(R.id.edit_face_save);
        mSaveBtn.setOnClickListener(this);

        BottomTitleGroup mEditFaceBottomTitleGroup = view.findViewById(R.id.edit_face_bottom_title);
        if (mAvatarP2A.getGender() == 0) {
            mEditFaceBottomTitleGroup.setResStrings(new String[]{"美型", "肤色", "唇色", "瞳色", "发型", "胡子", "眼镜", "帽子", "衣服"},
                    new int[]{TITLE_SHAPE_INDEX, TITLE_SKIN_INDEX, TITLE_LIP_INDEX, TITLE_IRIS_INDEX, TITLE_HAIR_INDEX, TITLE_BEARD_INDEX, TITLE_GLASSES_INDEX, TITLE_HAT_INDEX, TITLE_CLOTHES_INDEX},
                    mEditFaceSelectBottomId);
        } else {
            mEditFaceBottomTitleGroup.setResStrings(new String[]{"美型", "肤色", "唇色", "瞳色", "发型", "眼镜", "帽子", "衣服"},
                    new int[]{TITLE_SHAPE_INDEX, TITLE_SKIN_INDEX, TITLE_LIP_INDEX, TITLE_IRIS_INDEX, TITLE_HAIR_INDEX, TITLE_GLASSES_INDEX, TITLE_HAT_INDEX, TITLE_CLOTHES_INDEX},
                    mEditFaceSelectBottomId);
        }
        mEditFaceBottomTitleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showFragment(checkedId);
                mEditFaceSelectBottomId = checkedId;
            }
        });

        showFragment(mEditFaceSelectBottomId);
        updateSaveBtn();
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
    public void backToHome() {
        if (isChangeValues()) {
            NormalDialog normalDialog = new NormalDialog();
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
                    resetChangeValues();
                    mActivity.showHomeFragment();

                    mFUP2ARenderer.loadAvatar(mActivity.getShowAvatarP2A());
                }
            });
        } else {
            mActivity.showHomeFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_face_back:
                backToHome();
                break;
            case R.id.edit_face_save:
                saveAvatar();
                break;
        }
    }

    @Override
    public void onLoadBodyCompleteListener() {
        if (mLoadingDialog != null && mNewAvatarP2A.getBundleDir().equals(mActivity.getShowAvatarP2A().getBundleDir())) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
            mActivity.showHomeFragment();
        }
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

    public void showFragment(Class aClass, int id) {
        try {
            String tag = aClass.getSimpleName() + id;
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            EditFaceBaseFragment show = (EditFaceBaseFragment) manager.findFragmentByTag(tag);
            if (mEditFaceBaseFragments.get(mEditFaceSelectBottomId) != null) {
                transaction.hide(mEditFaceBaseFragments.get(mEditFaceSelectBottomId));
            }
            if (show == null) {
                show = (EditFaceBaseFragment) aClass.newInstance();
                mEditFaceBaseFragments.put(id, show);
                Bundle data = new Bundle();
                data.putInt(EditFaceBaseFragment.ID_KEY, id);
                show.setArguments(data);
                transaction.add(R.id.edit_face_bottom_layout, show, tag);
            } else {
                transaction.show(show);
            }
            transaction.commit();
            show.setAvatarP2A(mAvatarP2A);
            show.setItemControllerListener(this);
            show.setColorControllerListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFragment(int id) {
        switch (id) {
            case TITLE_SHAPE_INDEX:
                showFragment(EditFaceShapeFragment.class, id);
                break;
            case TITLE_SKIN_INDEX:
                showFragment(EditFaceSkinFragment.class, id);
                break;
            case TITLE_LIP_INDEX:
            case TITLE_IRIS_INDEX:
                showFragment(EditFaceColorFragment.class, id);
                break;
            case TITLE_BEARD_INDEX:
            case TITLE_HAT_INDEX:
            case TITLE_HAIR_INDEX:
                showFragment(EditFaceColorItemFragment.class, id);
                break;
            case TITLE_GLASSES_INDEX:
                showFragment(EditFaceGlassesFragment.class, id);
                break;
            case TITLE_CLOTHES_INDEX:
                showFragment(EditFaceItemFragment.class, id);
                break;
        }
    }

    @Override
    public void colorChangeListener(int pos) {
        switch (mEditFaceSelectBottomId) {
            case TITLE_LIP_INDEX:
                mAvatarP2A.setLipColorValue(pos);
                mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_lip_color, ColorConstant.lip_color[pos]);
                break;
            case TITLE_IRIS_INDEX:
                mAvatarP2A.setIrisColorValue(pos);
                mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_iris_color, ColorConstant.iris_color[pos]);
                break;
            case TITLE_BEARD_INDEX:
                mAvatarP2A.setBeardColorValue(pos);
                mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_beard_color, ColorConstant.beard_color[pos]);
                break;
            case TITLE_HAIR_INDEX:
                mAvatarP2A.setHairColorValue(pos);
                double[] hair_color = ColorConstant.hair_color[pos];
                mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_hair_color, Arrays.copyOf(hair_color, 3));
                mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_hair_color_intensity, (float) hair_color[3]);
                break;
            case TITLE_GLASSES_INDEX:
                mAvatarP2A.setGlassesColorValue(pos);
                mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_glass_color, ColorConstant.glass_color[pos]);
                break;
            case TITLE_HAT_INDEX:
                mAvatarP2A.setHatColorValue(pos);
                mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_hat_color, ColorConstant.hat_color[pos]);
                break;
        }
        updateSaveBtn();
    }

    @Override
    public void colorChangeListener2(int pos) {
        if (mEditFaceSelectBottomId == TITLE_GLASSES_INDEX) {
            mAvatarP2A.setGlassesFrameColorValue(pos);
            mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_glass_frame_color, ColorConstant.glass_frame_color[pos]);
        }
        updateSaveBtn();
    }

    @Override
    public void colorValuesChangeListener(double value) {
        switch (mEditFaceSelectBottomId) {
            case TITLE_SHAPE_INDEX:
                break;
            case TITLE_SKIN_INDEX:
                mAvatarP2A.setSkinColorValue(value);
                mFUP2ARenderer.fuItemSetParamFuItemHandler(FUP2ARenderer.PARAM_KEY_skin_color, ColorConstant.getColor(ColorConstant.skin_color, value));
                break;
        }
        updateSaveBtn();
    }

    @Override
    public void itemSelectListener(int pos) {
        switch (mEditFaceSelectBottomId) {
            case TITLE_BEARD_INDEX:
                mAvatarP2A.setBeardIndex(pos);
                break;
            case TITLE_HAIR_INDEX:
                mAvatarP2A.setHairIndex(pos);
                break;
            case TITLE_GLASSES_INDEX:
                mAvatarP2A.setGlassesIndex(pos);
                break;
            case TITLE_CLOTHES_INDEX:
                mAvatarP2A.setClothesIndex(pos);
                break;
            case TITLE_HAT_INDEX:
                mAvatarP2A.setHatIndex(pos);
                break;
        }
        mFUP2ARenderer.loadAvatar(mAvatarP2A);
        updateSaveBtn();
    }

    private boolean isChangeValues() {
        for (int i = 0; i < EditFaceSelectBottomCount; i++) {
            EditFaceBaseFragment fragment = mEditFaceBaseFragments.get(i);
            if (fragment != null && fragment.isChangeDeformParam()) {
                return true;
            }
        }
        return false;
    }

    private void resetChangeValues() {
        for (int i = 0; i < EditFaceSelectBottomCount; i++) {
            EditFaceBaseFragment fragment = mEditFaceBaseFragments.get(i);
            if (fragment != null) {
                fragment.resetDefaultDeformParam();
            }
        }
    }

    private LoadingDialog mLoadingDialog;
    private AvatarP2A mNewAvatarP2A;

    private void saveAvatar() {
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getChildFragmentManager(), LoadingDialog.TAG);

        AsyncTask.execute(new Runnable() {
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
                        String[] hairBundles = AvatarConstant.hairBundle(mNewAvatarP2A.getGender(), mNewAvatarP2A.getStyle());
                        String[] hairPaths = new String[hairBundles.length];
                        for (int i = 0; i < hairBundles.length; i++) {
                            if (!TextUtils.isEmpty(hairBundles[i])) {
                                FileUtil.copyFileTo(mActivity.getAssets().open(hairShowNows[i]), new File(hairPaths[i] = (dir + hairBundles[i])));
                            } else {
                                hairPaths[i] = "";
                            }
                        }
                        mNewAvatarP2A.setHairFileList(hairPaths);

                        saveAvatarHead();
                        EditFaceBaseFragment shape = mEditFaceBaseFragments.get(TITLE_SHAPE_INDEX);
                        if (shape == null || !shape.isChangeDeformParam()) {
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
                            mFUP2ARenderer.loadAvatar(mNewAvatarP2A, true);
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
        });
    }

    private void saveAvatarHead() throws IOException {
        EditFaceShapeFragment shape = (EditFaceShapeFragment) mEditFaceBaseFragments.get(TITLE_SHAPE_INDEX);
        if (shape != null && shape.isChangeDeformParam()) {
            P2AClientWrapper.deformAvatarHead(mAvatarP2A.getHeadFile().startsWith(Constant.filePath) ? new FileInputStream(new File(mAvatarP2A.getHeadFile())) : mActivity.getAssets().open(mAvatarP2A.getHeadFile()), mNewAvatarP2A.getHeadFile(), shape.getShapeValues());
        }
    }
}
