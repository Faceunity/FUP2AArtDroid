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
import android.widget.ImageView;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.fragment.editface.core.ColorValuesChangeListener;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.color.ColorAdapter;
import com.faceunity.p2a_art.ui.NormalDialog;
import com.faceunity.p2a_art.ui.seekbar.DiscreteSeekBar;

import java.util.Arrays;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceSkinFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceSkinFragment.class.getSimpleName();

    private ImageView mResetView;
    private RecyclerView mColorRecycler;
    private LinearLayoutManager mLinearLayoutManager;
    private ColorAdapter mColorAdapter;
    private DiscreteSeekBar mColorSeekBar;

    private double[][] mColorList;
    private double mDefaultValues = 0;
    private int mSelectPos = 0;
    private double mValues = 0;
    private ColorValuesChangeListener mColorValueListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_skin, container, false);

        mResetView = view.findViewById(R.id.edit_face_skin_reset);
        mResetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChangeDeformParam()) {
                    NormalDialog normalDialog = new NormalDialog();
                    normalDialog.setNormalDialogTheme(R.style.FullScreenTheme);
                    normalDialog.setMessageStr("确认将所有参数恢复默认吗？");
                    normalDialog.setNegativeStr("取消");
                    normalDialog.setPositiveStr("确认");
                    normalDialog.show(getChildFragmentManager(), NormalDialog.TAG);
                    normalDialog.setOnClickListener(new NormalDialog.OnSimpleClickListener() {
                        @Override
                        public void onPositiveListener() {
                            mColorAdapter.setSelectPosition(mSelectPos = (int) mDefaultValues);
                            mValues = mDefaultValues - mSelectPos;
                            mColorSeekBar.setProgress((int) (mValues * 100));
                            if (mColorValueListener != null) {
                                mColorValueListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, (double) mSelectPos + mValues);
                            }
                        }
                    });
                }
            }
        });
        mColorRecycler = view.findViewById(R.id.color_recycler);
        mColorRecycler.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mColorRecycler.setAdapter(mColorAdapter = new ColorAdapter(getContext(), mColorList = Arrays.copyOf(ColorConstant.skin_color, ColorConstant.skin_color.length - 1)));
        ((SimpleItemAnimator) mColorRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mColorAdapter.setColorSelectListener(new ColorAdapter.ColorSelectListener() {
            @Override
            public void colorSelectListener(int position) {
                mValues = 0f;
                mSelectPos = position;
                mColorSeekBar.setProgress((int) (mValues * 100));
                if (mColorValueListener != null) {
                    mColorValueListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, (double) mSelectPos + mValues);
                }
                scrollToPosition(position);
            }
        });
        mColorSeekBar = view.findViewById(R.id.color_seek_bar);
        mColorSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                if (mColorValueListener != null) {
                    mColorValueListener.colorValuesChangeListener(mEditFaceBaseFragmentId, 0, (double) mSelectPos + (mValues = 1.0f * (value >= 100 ? 99 : value) / 100));
                }
            }
        });
        final int l30 = getResources().getDimensionPixelSize(R.dimen.x30);
        final int l2 = getResources().getDimensionPixelSize(R.dimen.x2);
        mColorRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int size = mColorList.length;
                int index = parent.getChildAdapterPosition(view);
                int left = index == 0 ? l30 : l30 + l2;
                int right = index == size - 1 ? l30 : 0;
                outRect.set(left, 0, right, 0);
            }
        });

        mColorAdapter.setSelectPosition(mSelectPos);
        mColorSeekBar.setProgress((int) (100 * mValues));
        scrollToPosition(mSelectPos);
        return view;
    }

    public void initDate(double values, ColorValuesChangeListener colorValueListener) {
        mDefaultValues = values;
        mSelectPos = (int) mDefaultValues;
        mValues = mDefaultValues - mSelectPos;
        mColorValueListener = colorValueListener;
    }

    public void scrollToPosition(final int pos) {
        final int itemW = getResources().getDimensionPixelOffset(R.dimen.x80);
        mColorRecycler.post(new Runnable() {
            @Override
            public void run() {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int first = mLinearLayoutManager.findFirstVisibleItemPosition();
                int dx = pos * itemW + itemW / 2 - screenWidth / 2
                        + (first > -1 ? (-first * itemW + mLinearLayoutManager.findViewByPosition(first).getLeft()) : 0);
                mColorRecycler.smoothScrollBy(dx, 0);
            }
        });
    }

    public boolean isChangeDeformParam() {
        return mDefaultValues != (mValues + mSelectPos);
    }
}
