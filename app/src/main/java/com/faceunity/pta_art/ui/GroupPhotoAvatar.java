package com.faceunity.pta_art.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.AvatarPTA;
import com.faceunity.pta_art.entity.Scenes;
import com.faceunity.pta_art.fragment.groupavatar.GroupPhotoAvatarItemFragment;
import com.faceunity.pta_art.fragment.groupavatar.GroupPhotoScenesFragment;
import com.faceunity.pta_art.utils.TabFragmentPagerAdapter;
import com.faceunity.pta_art.utils.Utils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by tujh on 2018/12/18.
 */
public class GroupPhotoAvatar extends RelativeLayout {
    public static final String TAG = GroupPhotoAvatar.class.getSimpleName();

    private TextView mNextBtn;
    private TextView mAvatarPoint;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> titles;
    private ArrayList<Fragment> fragments;
    private GroupPhotoAvatarItemFragment groupPhotoAvatarItemFragment;
    private GroupPhotoScenesFragment groupPhotoScenesItemFragment;

    public GroupPhotoAvatar(@NonNull Context context) {
        this(context, null);
    }

    public GroupPhotoAvatar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupPhotoAvatar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_group_photo_avatar, this, true);
        findViewById(R.id.group_photo_avatar_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackRunnable != null) {
                    mBackRunnable.run();
                }

            }
        });
        mNextBtn = findViewById(R.id.group_photo_avatar_next);
        mNextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isNoFastClick()) {
                    return;
                }
                if (mNextRunnable != null) {
                    mNextRunnable.run();
                }
            }
        });

        tabLayout = findViewById(R.id.group_photo_avatar_tablayout);
        viewPager = findViewById(R.id.group_photo_avatar_viewpager);
        titles = new ArrayList<>();
        titles.add("模型");
        titles.add("背景");
        fragments = new ArrayList<>();
        groupPhotoAvatarItemFragment = new GroupPhotoAvatarItemFragment();
        fragments.add(groupPhotoAvatarItemFragment);
        groupPhotoScenesItemFragment = new GroupPhotoScenesFragment();
        fragments.add(groupPhotoScenesItemFragment);

        groupPhotoAvatarItemFragment.setPointUpdateListener(new GroupPhotoAvatarItemFragment.UpdateUIListener() {
            @Override
            public void onPointUpdateListener(String text) {
                mAvatarPoint.post(new Runnable() {
                    @Override
                    public void run() {
                        mAvatarPoint.setText(text);
                    }
                });
            }

            @Override
            public void onNextBtnUpdateListener(boolean enable) {
                mNextBtn.post(new Runnable() {
                    @Override
                    public void run() {
                        mNextBtn.setEnabled(enable);
                    }
                });

            }
        });

        mAvatarPoint = findViewById(R.id.group_photo_avatar_point);
    }

    private Runnable mBackRunnable;

    public void setBackRunnable(Runnable backRunnable) {
        this.mBackRunnable = backRunnable;
    }

    private Runnable mNextRunnable;

    public void setNextRunnable(Runnable nextRunnable) {
        mNextRunnable = nextRunnable;
    }


    public void setAvatarSelectListener(AvatarSelectListener avatarSelectListener) {
        groupPhotoAvatarItemFragment.setAvatarSelectListener(avatarSelectListener);
    }

    public interface AvatarSelectListener {
        void onAvatarSelectListener(AvatarPTA avatar, boolean isSelect, int roleId);
    }

    public void updateAvatarPoint() {
        groupPhotoAvatarItemFragment.updateAvatarPoint(null);
    }

    public void setScenes(Scenes scenes) {
        groupPhotoAvatarItemFragment.setScenes(scenes);
        viewPager.setCurrentItem(0);
        groupPhotoScenesItemFragment.needReset(true);

    }

    public void selectedDefaultScenesBg(Scenes scenes){
        groupPhotoScenesItemFragment.setDefaultBgPosition(scenes.scenesBg);
    }


    public void updateNextBtn(final boolean isEnabled) {
        groupPhotoAvatarItemFragment.updateNextBtn(isEnabled);
    }


    public Map<Integer, Integer> getUsedRoleId() {
        return groupPhotoAvatarItemFragment.getUsedRoleId();
    }

    public boolean[] getIsSelectList() {
        return groupPhotoAvatarItemFragment.getIsSelectList();
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        TabFragmentPagerAdapter tabFragmentPagerAdapter = new TabFragmentPagerAdapter(fragmentManager, fragments, titles);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(tabFragmentPagerAdapter);
    }

    public void setBgBundleChangeListener(GroupPhotoScenesFragment.OnBgBundleChangeListener listener) {
        groupPhotoScenesItemFragment.setOnBgBundleChangeListener(listener);
    }

    public void removeBgBundlePosition() {
        if (groupPhotoScenesItemFragment != null) {
            groupPhotoScenesItemFragment.removeSelectedPosition();
        }
    }
}
