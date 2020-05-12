package com.faceunity.pta_art.fragment.groupavatar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.BundleRes;
import com.faceunity.pta_art.entity.Scenes;
import com.faceunity.pta_art.fragment.BaseFragment;
import com.faceunity.pta_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.pta_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.pta_art.fragment.groupavatar.core.GroupPhotoScenesItemManager;
import com.faceunity.pta_art.ui.BottomTitleGroup;

import static com.faceunity.pta_art.fragment.groupavatar.core.GroupPhotoScenesItemManager.TITLE_SCENES_2D;
import static com.faceunity.pta_art.fragment.groupavatar.core.GroupPhotoScenesItemManager.TITLE_SCENES_3D;
import static com.faceunity.pta_art.fragment.groupavatar.core.GroupPhotoScenesItemManager.TITLE_SCENES_ANIMATION;
import static com.faceunity.pta_art.fragment.groupavatar.core.GroupPhotoScenesItemManager.TITLE_SCENES_BACKGROUND;


/**
 * Created by jiangyongxing on 2020/3/31.
 * 描述：
 */
public class GroupPhotoScenesFragment extends BaseFragment {

    private int mEditFaceSelectBottomId = TITLE_SCENES_2D;
    private SparseArray<GroupPhotoBaseFragment> mGroupPhotoBaseFragments = new SparseArray<>();
    private boolean needReset = false;
    private BottomTitleGroup bottomTitleGroup;
    private BaseFragment lastFragment;
    private BaseFragment currentFragment;
    private Scenes.ScenesBg scenesBg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.layout_group_photo_scenes_fragemnt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomTitleGroup = view.findViewById(R.id.group_photo_avatar_fragment_BottomTitleGroup);
        GroupPhotoScenesItemManager groupPhotoScenesItemManager = new GroupPhotoScenesItemManager();
        groupPhotoScenesItemManager.init(AvatarPTA.gender_mid);
        int[][] titleIdAndIcons = groupPhotoScenesItemManager.getTitleIdAndIcons();

        bottomTitleGroup.setResIcon(titleIdAndIcons[1], titleIdAndIcons[0], mEditFaceSelectBottomId);

        showFragment(mEditFaceSelectBottomId);

        bottomTitleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) {
                    return;
                }
                showFragment(checkedId);
                mEditFaceSelectBottomId = checkedId;
            }
        });
    }


    public void showFragment(int id) {
        try {
            lastFragment = currentFragment;
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            GroupPhotoBaseFragment show = mGroupPhotoBaseFragments.get(id);
            if (mGroupPhotoBaseFragments.get(mEditFaceSelectBottomId) != null) {
                transaction.hide(mGroupPhotoBaseFragments.get(mEditFaceSelectBottomId));
            }
            if (show == null) {
                switch (id) {
                    case TITLE_SCENES_2D:
                        show = new GroupPhotoScenesItemFragment();
                        ((GroupPhotoScenesItemFragment) show).initData(FilePathFactory.scenes2DBundleRes(),
                                                                       scenesBg == null ? 0 : scenesBg.bg_2d,
                                                                       mItemChangeListener);
                        break;
                    case TITLE_SCENES_3D:
                        show = new GroupPhotoScenesItemFragment();
                        ((GroupPhotoScenesItemFragment) show).initData(FilePathFactory.scenes3dBundleRes(),
                                                                       scenesBg == null ? 0 : scenesBg.bg_3d,
                                                                       mItemChangeListener);
                        break;
                    case TITLE_SCENES_ANIMATION:
                        show = new GroupPhotoScenesItemFragment();
                        ((GroupPhotoScenesItemFragment) show).initData(FilePathFactory.scenesAniBundleRes(),
                                                                       scenesBg == null ? 0 : scenesBg.bg_ani,
                                                                       mItemChangeListener);
                        break;
                    case TITLE_SCENES_BACKGROUND:
                        show = new GroupPhotoScenesBackgroundFragment();
                        break;
                }
                if (show != null) {
                    mGroupPhotoBaseFragments.put(id, show);
                    Bundle data = new Bundle();
                    data.putInt(EditFaceBaseFragment.ID_KEY, id);
                    show.setArguments(data);
                    transaction.add(R.id.group_photo_avatar_fragment_fragment, show);
                }
            } else {
                transaction.show(show);
            }
            currentFragment = show;
            if (lastFragment == null) {
                lastFragment = show;
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ItemChangeListener mItemChangeListener = new ItemChangeListener() {

        private BundleRes bundleRes;

        @Override
        public void itemChangeListener(int id, int pos) {
            switch (id) {
                case TITLE_SCENES_2D:
                    bundleRes = FilePathFactory.scenes2DBundleRes().get(pos);
                    if (scenesBg != null) {
                        scenesBg.bg_2d = pos;
                    }
                    break;
                case TITLE_SCENES_3D:
                    bundleRes = FilePathFactory.scenes3dBundleRes().get(pos);
                    if (scenesBg != null) {
                        scenesBg.bg_3d = pos;
                    }
                    break;
                case TITLE_SCENES_ANIMATION:
                    bundleRes = FilePathFactory.scenesAniBundleRes().get(pos);
                    if (scenesBg != null) {
                        scenesBg.bg_ani = pos;
                    }
                    break;
            }
            if (bundleRes != null && onBgBundleChangeListener != null) {
                onBgBundleChangeListener.onBgBundleChangeListener(bundleRes.path);
            }
        }
    };

    private OnBgBundleChangeListener onBgBundleChangeListener;

    public void needReset(boolean needReset) {
        this.needReset = needReset;
    }

    public void setDefaultBgPosition(Scenes.ScenesBg scenesBg) {
        this.scenesBg = scenesBg.clone();
        if (mItemChangeListener != null) {
            if (scenesBg.bg_2d != 0) {
                mItemChangeListener.itemChangeListener(TITLE_SCENES_2D, scenesBg.bg_2d);
            }
            if (scenesBg.bg_3d != 0) {
                mItemChangeListener.itemChangeListener(TITLE_SCENES_3D, scenesBg.bg_3d);
            }
            if (scenesBg.bg_ani != 0) {
                mItemChangeListener.itemChangeListener(TITLE_SCENES_ANIMATION, scenesBg.bg_ani);
            }
        }

    }

    public interface OnBgBundleChangeListener {
        void onBgBundleChangeListener(String path);
    }

    public void setOnBgBundleChangeListener(OnBgBundleChangeListener onBgBundleChangeListener) {
        this.onBgBundleChangeListener = onBgBundleChangeListener;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && needReset) {
            mEditFaceSelectBottomId = TITLE_SCENES_2D;
            mGroupPhotoBaseFragments.clear();
            bottomTitleGroup.setCheckedById(mEditFaceSelectBottomId);
            showFragment(mEditFaceSelectBottomId);
        }
    }

    public void removeSelectedPosition() {
        if (lastFragment instanceof GroupPhotoScenesItemFragment) {
            ((GroupPhotoScenesItemFragment) lastFragment).setItem(0);
        }
    }
}
