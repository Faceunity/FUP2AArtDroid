package com.faceunity.pta_art.fragment.editface.core.item;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;

import com.faceunity.pta_art.R;
import com.faceunity.pta_art.entity.SpecialBundleRes;
import com.faceunity.pta_art.fragment.editface.core.bean.PairBean;

import java.util.List;
import java.util.Map;

/**
 * Created by tujh on 2019/1/9.
 */
public class MultipleSelectView extends RecyclerView {
    public static final String TAG = MultipleSelectView.class.getSimpleName();

    private static final int spanCount = 5;

    private MultipleItemAdapter mItemAdapter;
    private GridLayoutManager mGridLayoutManager;
    private MultipleItemAdapter.ItemSelectListener mItemSelectListener;

    public MultipleSelectView(@NonNull Context context) {
        this(context, null);
    }

    public MultipleSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(List<SpecialBundleRes> itemList, Map<Integer, PairBean> pairBeanMap, int totalType) {
        mItemAdapter = new MultipleItemAdapter(getContext(), itemList, pairBeanMap, totalType);
        init();
    }

    private void init() {
        setLayoutManager(mGridLayoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false));
        setAdapter(mItemAdapter);
        final int wL = getResources().getDimensionPixelSize(R.dimen.x8);
        final int hL = getResources().getDimensionPixelSize(R.dimen.x16);
        final int topNormalL = getResources().getDimensionPixelSize(R.dimen.x8);
        addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                int index = parent.getChildAdapterPosition(view);
                int left = wL;
                int right = wL;
                int top = index < spanCount ? hL : topNormalL;
                int bottom = index < spanCount ? 0 : topNormalL;
                outRect.set(left, top, right, bottom);
            }
        });

        ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);
        mItemAdapter.setItemSelectListener(new MultipleItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int type, int lastPos, boolean isSel, int position, int realPos) {
                scrollToPosition(position);
                if (mItemSelectListener != null) {
                    mItemSelectListener.itemSelectListener(type, lastPos, isSel, position, realPos);
                }
                return true;
            }
        });
    }

    public void scrollToPosition(final int pos) {
        post(new Runnable() {
            @Override
            public void run() {
                final int topNormalL = getResources().getDimensionPixelSize(R.dimen.x14);
                final int itemW = getResources().getDimensionPixelOffset(R.dimen.x126);
                final int first = mGridLayoutManager.findFirstVisibleItemPosition();
                if (first < 0) return;
                int dy = (int) ((0.5 + pos / spanCount) * (itemW + topNormalL) - getHeight() / 2
                        - (first / spanCount * (itemW + topNormalL) - mGridLayoutManager.findViewByPosition(first).getTop()));
                smoothScrollBy(0, dy);
            }
        });
    }

    public void setItemControllerListener(MultipleItemAdapter.ItemSelectListener itemSelectListener) {
        mItemSelectListener = itemSelectListener;
    }

    public void setItem(int position) {
        mItemAdapter.setSelectPosition(position);
    }
}
