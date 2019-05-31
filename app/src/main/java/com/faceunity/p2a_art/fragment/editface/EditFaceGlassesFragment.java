package com.faceunity.p2a_art.fragment.editface;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.entity.BundleRes;
import com.faceunity.p2a_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorSelectView;
import com.faceunity.p2a_art.fragment.editface.core.item.ItemAdapter;

import java.util.List;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceGlassesFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceGlassesFragment.class.getSimpleName();

    private RecyclerView mGlassesRecycler;
    private LinearLayoutManager mGlassesLayoutManager;
    private ItemAdapter mItemAdapter;

    private TextView mFrameColorText;
    private ColorSelectView mFrameColorRecycler;

    private TextView mColorText;
    private ColorSelectView mColorRecycler;

    public static final int GLASSES_COLOR = 0;
    public static final int GLASSES_FRAME_COLOR = 1;

    private List<BundleRes> mItemList;
    private int mDefaultSelectItem;
    private ItemChangeListener mItemSelectListener;
    private double[][] colorFrameList;
    private int mDefaultSelectColorFrame;
    private ColorValuesChangeListener mColorFrameSelectListener;
    private double[][] colorList;
    private int mDefaultSelectColor;
    private ColorValuesChangeListener mColorSelectListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_glasses, container, false);

        mGlassesRecycler = view.findViewById(R.id.glasses_recycler);
        mGlassesRecycler.setLayoutManager(mGlassesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mGlassesRecycler.setAdapter(mItemAdapter = new ItemAdapter(getContext(), R.layout.layout_edit_face_item_glasses) {
            @Override
            public int getRes(int pos) {
                return mItemList.get(pos).resId;
            }

            @Override
            public int getSize() {
                return mItemList.size();
            }
        });
        ((SimpleItemAnimator) mGlassesRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mItemAdapter.setItemSelectListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int position) {
                updateView(position);
                scrollToPosition(position);
                mItemSelectListener.itemChangeListener(mEditFaceBaseFragmentId, position);
                return true;
            }
        });

        final int l22 = getResources().getDimensionPixelSize(R.dimen.x22);
        final int l4 = getResources().getDimensionPixelSize(R.dimen.x4);
        mGlassesRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int size = mItemList.size();
                int index = parent.getChildAdapterPosition(view);
                int left = index == 0 ? l22 : l22 + l4;
                int right = index == size - 1 ? l22 : 0;
                outRect.set(left, 0, right, 0);
            }
        });

        mFrameColorText = view.findViewById(R.id.glasses_frame_text);
        mFrameColorRecycler = view.findViewById(R.id.glasses_frame_color_recycler);
        mFrameColorRecycler.init(colorFrameList, mDefaultSelectColorFrame);
        mFrameColorRecycler.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                mColorSelectListener.colorValuesChangeListener(mEditFaceBaseFragmentId, GLASSES_FRAME_COLOR, position);
            }
        });

        mColorText = view.findViewById(R.id.glasses_text);
        mColorRecycler = view.findViewById(R.id.glasses_color_recycler);
        mColorRecycler.init(colorList, mDefaultSelectColor);
        mColorRecycler.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                mColorSelectListener.colorValuesChangeListener(mEditFaceBaseFragmentId, GLASSES_COLOR, position);
            }
        });

        updateView(mDefaultSelectItem);
        scrollToPosition(mDefaultSelectItem);
        mItemAdapter.setSelectPosition(mDefaultSelectItem);
        return view;
    }

    private void updateView(int position) {
        mColorText.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        mColorRecycler.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        mFrameColorText.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        mFrameColorRecycler.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
    }

    public void scrollToPosition(final int pos) {
        mGlassesRecycler.post(new Runnable() {
            @Override
            public void run() {
                final int itemW = getResources().getDimensionPixelOffset(R.dimen.x156);
                final int l22 = getResources().getDimensionPixelSize(R.dimen.x22);
                final int l4 = getResources().getDimensionPixelSize(R.dimen.x4);
                int first = mGlassesLayoutManager.findFirstVisibleItemPosition();
                if (first < 0) return;
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int dx = (int) ((0.5 + pos) * (itemW + l22) + l4 - screenWidth / 2
                        - (first * (itemW + l22) + l4 - mGlassesLayoutManager.findViewByPosition(first).getLeft()));
                mGlassesRecycler.smoothScrollBy(dx, 0);
            }
        });
    }

    public void initData(double[][] colorList, int defaultSelectColor, ColorValuesChangeListener colorSelectListener,
                         double[][] colorFrameList, int defaultSelectColorFrame, ColorValuesChangeListener colorFrameSelectListener,
                         List<BundleRes> itemList, int defaultSelectItem, ItemChangeListener itemSelectListener) {
        this.colorList = colorList;
        this.mDefaultSelectColor = defaultSelectColor;
        this.mColorSelectListener = colorSelectListener;

        this.colorFrameList = colorFrameList;
        this.mDefaultSelectColorFrame = defaultSelectColorFrame;
        this.mColorFrameSelectListener = colorFrameSelectListener;

        this.mItemList = itemList;
        this.mDefaultSelectItem = defaultSelectItem;
        this.mItemSelectListener = itemSelectListener;
    }
}
