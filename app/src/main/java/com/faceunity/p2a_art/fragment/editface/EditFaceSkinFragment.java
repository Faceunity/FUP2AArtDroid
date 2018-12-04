package com.faceunity.p2a_art.fragment.editface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.fragment.editface.core.ColorAdapter;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.ui.NormalDialog;
import com.faceunity.p2a_art.ui.seekbar.DiscreteSeekBar;

import java.util.Arrays;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceSkinFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceSkinFragment.class.getSimpleName();

    private static final int[] RESET_RES_ID = new int[]{R.drawable.edit_face_shape_reset_normal};

    private RecyclerView mColorRecycler;
    private ColorAdapter mColorAdapter;
    private DiscreteSeekBar mColorSeekBar;

    private double mDefaultValues = 0;
    private int mSelectPos = 0;
    private double mValues = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_skin, container, false);

        mColorRecycler = view.findViewById(R.id.color_recycler);
        mColorRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mColorRecycler.setAdapter(mColorAdapter = new ColorAdapter(getContext(), Arrays.copyOf(ColorConstant.skin_color, ColorConstant.skin_color.length - 1), RESET_RES_ID));
        ((SimpleItemAnimator) mColorRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mColorAdapter.setItemSelectListener(new ColorAdapter.ItemSelectListener() {
            @Override
            public void itemSelectListener(int position) {
                if (position < RESET_RES_ID.length) {
                    NormalDialog normalDialog = new NormalDialog();
                    normalDialog.setNormalDialogTheme(R.style.FullScreenTheme);
                    normalDialog.setMessageStr("确认将所有参数恢复默认吗？");
                    normalDialog.setNegativeStr("取消");
                    normalDialog.setPositiveStr("确认");
                    normalDialog.show(getChildFragmentManager(), NormalDialog.TAG);
                    normalDialog.setOnClickListener(new NormalDialog.OnSimpleClickListener() {
                        @Override
                        public void onPositiveListener() {
                            mColorAdapter.setSelectPosition(1 + (mSelectPos = (int) mDefaultValues));
                            scrollToPosition(mColorRecycler, mSelectPos + 1);
                            mValues = mDefaultValues - mSelectPos;
                            mColorSeekBar.setProgress((int) (mValues * 100));
                            if (colorControllerListener != null) {
                                colorControllerListener.colorValuesChangeListener(mSelectPos + mValues);
                            }
                        }
                    });
                } else {
                    mValues = 0f;
                    mSelectPos = position - RESET_RES_ID.length;
                    mColorSeekBar.setProgress((int) (mValues * 100));
                    if (colorControllerListener != null) {
                        colorControllerListener.colorValuesChangeListener(mSelectPos + mValues);
                    }
                }
            }
        });
        mColorSeekBar = view.findViewById(R.id.color_seek_bar);
        mColorSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                if (colorControllerListener != null) {
                    colorControllerListener.colorValuesChangeListener(mSelectPos + (mValues = 1.0f * value / 100));
                }
            }
        });

        mDefaultValues = mAvatarP2A.getSkinColorValue() < 0 ? mFUP2ARenderer.fuItemGetParamSkinColorIndex() : mAvatarP2A.getSkinColorValue();
        Log.e(TAG,"mFUP2ARenderer.fuItemGetParamSkinColorIndex() "+ mFUP2ARenderer.fuItemGetParamSkinColorIndex());
        mSelectPos = (int) mDefaultValues;
        mValues = mDefaultValues - mSelectPos;
        mColorAdapter.setSelectPosition(mSelectPos + 1);
        scrollToPosition(mColorRecycler, mSelectPos + 1);
        return view;
    }

    @Override
    public void resetDefaultDeformParam() {
        mAvatarP2A.setSkinColorValue(mDefaultValues);
    }

    @Override
    public boolean isChangeDeformParam() {
        return mDefaultValues != (mValues + mSelectPos);
    }
}
