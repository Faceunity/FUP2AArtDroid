package com.faceunity.pta_art.fragment.drive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.fragment.BaseFragment;
import com.faceunity.pta_art.fragment.adapter.DriveAdapter;
import com.faceunity.pta_art.ui.BottomTitleGroup;

/**
 * Created by hyj on 2020-04-28.
 * 驱动类基础页
 */
public abstract class BaseDriveFragment extends BaseFragment implements View.OnClickListener {

    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected DriveAdapter adapter;
    protected BottomTitleGroup mBottomTitleGroup;
    protected View v_bg_top;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(getResource(), container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.body_filter_back).setOnClickListener(this);
        initViewData(view);
        initRecycler(view);
        initTitleGroup(view);
    }

    private void initRecycler(View view) {
        v_bg_top = view.findViewById(R.id.v_bg_top);
        mRecyclerView = view.findViewById(R.id.body_drive_bottom_recycler);
        mRecyclerView.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        adapter = initAdapter();
        mRecyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void initTitleGroup(View view) {
        mBottomTitleGroup = view.findViewById(R.id.body_drive_bottom_title);
        initTitleGroupData();
        mBottomTitleGroup.setDefaultCheck();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.body_filter_back) {
            //返回
            onBackPressed();
        } else {
            onClick(v.getId());
        }
    }

    @Override
    public void onBackPressed() {
        backToHome();
        super.onBackPressed();
    }

    /**
     * 根据位置，自动移动到选中位置
     *
     * @param pos
     */
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

    /**
     * 是否隐藏列表视图
     *
     * @param isShow
     */
    public void setViewShowOrHide(boolean isShow) {
        mRecyclerView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mBottomTitleGroup.setVisibility(isShow ? View.VISIBLE : View.GONE);
        v_bg_top.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 布局id
     */
    public abstract int getResource();

    /**
     * 初始化view
     *
     * @param view
     */
    public abstract void initViewData(View view);

    /**
     * 初始化adapter及填充数据
     */
    public abstract DriveAdapter initAdapter();

    /**
     * 初始化TitleGroup及填充数据
     */
    public abstract void initTitleGroupData();

    /**
     * 退到首页
     */
    public abstract void backToHome();

    /**
     * 点击事件处理
     *
     * @param id
     */
    public abstract void onClick(int id);

}
