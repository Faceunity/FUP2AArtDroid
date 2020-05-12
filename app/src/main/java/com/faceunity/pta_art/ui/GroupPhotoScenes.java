package com.faceunity.pta_art.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.faceunity.pta_art.R;
import com.faceunity.pta_art.constant.FilePathFactory;
import com.faceunity.pta_art.entity.Scenes;
import com.faceunity.pta_art.utils.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tujh on 2018/12/18.
 */
public class GroupPhotoScenes extends RelativeLayout {
    public static final String TAG = GroupPhotoScenes.class.getSimpleName();

    private View backBtn;
    private ViewPager viewPager;

    public GroupPhotoScenes(@NonNull Context context) {
        this(context, null);
    }

    public GroupPhotoScenes(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupPhotoScenes(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_group_photo_scenes, this, true);

        backBtn = view.findViewById(R.id.group_photo_scenes_back);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackRunnable != null) {
                    mBackRunnable.run();
                }
            }
        });

        TabLayout tabLayout = findViewById(R.id.group_photo_scenes_tablayout);
        viewPager = findViewById(R.id.group_photo_scenes_viewpager);


        List<String> viewPagerTitles = new ArrayList<>();

        List<View> viewPagerViews = new ArrayList<>();

        Scenes[] singleScenes = FilePathFactory.singleScenes();
        Scenes[] multipleScenes = FilePathFactory.multipleScenes();
        Scenes[] animationScenes = FilePathFactory.animationScenes();

        if (singleScenes.length > 0 || multipleScenes.length > 0 || animationScenes.length > 0) {
            int allLength = singleScenes.length + multipleScenes.length + animationScenes.length;

            Scenes[] allScenes = new Scenes[allLength];
            System.arraycopy(singleScenes, 0, allScenes, 0, singleScenes.length);
            System.arraycopy(multipleScenes, 0, allScenes, singleScenes.length, multipleScenes.length);
            System.arraycopy(animationScenes, 0, allScenes, singleScenes.length + multipleScenes.length, animationScenes.length);

            View mAllRecycler = createRecyclerView(getContext(), allScenes);
            viewPagerTitles.add("全部");
            viewPagerViews.add(mAllRecycler);
        }


        if (singleScenes.length > 0) {
            View mSingleRecycler = createRecyclerView(getContext(), FilePathFactory.singleScenes());
            viewPagerTitles.add("单人");
            viewPagerViews.add(mSingleRecycler);
        }

        if (multipleScenes.length > 0) {
            View mMultipleRecycler = createRecyclerView(getContext(), FilePathFactory.multipleScenes());
            viewPagerTitles.add("多人");
            viewPagerViews.add(mMultipleRecycler);
        }

        if (animationScenes.length > 0) {
            View mAnimationRecycler = createRecyclerView(getContext(), FilePathFactory.animationScenes());
            viewPagerTitles.add("动画");
            viewPagerViews.add(mAnimationRecycler);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(viewPagerViews, viewPagerTitles);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(viewPagerAdapter);

    }

    public View createRecyclerView(Context context, Scenes[] scenes) {
        FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_group_photo_scences_recyclerview, viewPager, false);
        RecyclerView recyclerView = frameLayout.findViewById(R.id.group_photo_scences_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new ScenesAdapter(scenes));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        return frameLayout;
    }

    class ScenesAdapter extends RecyclerView.Adapter<ScenesAdapter.ScenesHolder> {
        Scenes[] scenes;

        public ScenesAdapter(Scenes[] scenes) {
            this.scenes = scenes;
        }

        @NonNull
        @Override
        public ScenesAdapter.ScenesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ScenesHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_group_photo_imageview, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ScenesAdapter.ScenesHolder holder, final int position) {
            Glide.with(getContext()).load(scenes[position].resId).into(holder.image);
            holder.tag.setVisibility(scenes[position].isAnimte ? VISIBLE : INVISIBLE);
            holder.image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mScenesSelectListener != null) {
                        mScenesSelectListener.onScenesSelectListener(scenes[position].isAnimte, scenes[position]);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return scenes.length;
        }

        class ScenesHolder extends RecyclerView.ViewHolder {
            ImageView image;
            ImageView tag;

            public ScenesHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.igpi_icon);
                tag = itemView.findViewById(R.id.igpi_gif_tag);
            }
        }
    }

    private Runnable mBackRunnable;

    public void setBackRunnable(Runnable backRunnable) {
        this.mBackRunnable = backRunnable;
    }

    private ScenesSelectListener mScenesSelectListener;

    public void setScenesSelectListener(ScenesSelectListener scenesSelectListener) {
        this.mScenesSelectListener = scenesSelectListener;
    }

    public interface ScenesSelectListener {
        void onScenesSelectListener(boolean isAnim, Scenes scenes);
    }

    /**
     * 设置返回按钮是否可用
     *
     * @param enable
     */
    public void setBackBtnEnable(boolean enable) {
        backBtn.setEnabled(enable);
    }
}
