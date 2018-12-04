package com.faceunity.p2a_art.fragment.editface;

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
import com.faceunity.p2a_art.constant.AvatarConstant;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.fragment.editface.core.ColorAdapter;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;
import com.faceunity.p2a_art.fragment.editface.core.ItemAdapter;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceGlassesFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceGlassesFragment.class.getSimpleName();

    private RecyclerView mGlassesRecycler;
    private ItemAdapter mItemAdapter;

    private TextView mFrameColorText;
    private RecyclerView mFrameColorRecycler;
    private ColorAdapter mFrameColorAdapter;

    private TextView mColorText;
    private RecyclerView mColorRecycler;
    private ColorAdapter mColorAdapter;

    private int mDefaultSelectItem;
    private int mDefaultSelectColorFrame;
    private int mDefaultSelectColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_glasses, container, false);

        mGlassesRecycler = view.findViewById(R.id.glasses_recycler);
        mGlassesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mGlassesRecycler.setAdapter(mItemAdapter = new ItemAdapter(getContext(), AvatarConstant.glassesRes(mAvatarP2A.getGender(), mAvatarP2A.getStyle())));
        ((SimpleItemAnimator) mGlassesRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mItemAdapter.setItemSelectListener(new ItemAdapter.ItemSelectListener() {
            @Override
            public boolean itemSelectListener(int position) {
                updateView(position);
                if (itemControllerListener != null) {
                    itemControllerListener.itemSelectListener(position);
                }
                return true;
            }
        });

        mFrameColorText = view.findViewById(R.id.glasses_frame_text);
        mFrameColorRecycler = view.findViewById(R.id.glasses_frame_color_recycler);
        mFrameColorRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mFrameColorRecycler.setAdapter(mFrameColorAdapter = new ColorAdapter(getContext(), ColorConstant.glass_frame_color));
        ((SimpleItemAnimator) mFrameColorRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mFrameColorAdapter.setItemSelectListener(new ColorAdapter.ItemSelectListener() {
            @Override
            public void itemSelectListener(int position) {
                if (colorControllerListener != null) {
                    colorControllerListener.colorChangeListener2(position);
                }
            }
        });

        mColorText = view.findViewById(R.id.glasses_text);
        mColorRecycler = view.findViewById(R.id.glasses_color_recycler);
        mColorRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mColorRecycler.setAdapter(mColorAdapter = new ColorAdapter(getContext(), ColorConstant.glass_color));
        ((SimpleItemAnimator) mColorRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mColorAdapter.setItemSelectListener(new ColorAdapter.ItemSelectListener() {
            @Override
            public void itemSelectListener(int position) {
                if (colorControllerListener != null) {
                    colorControllerListener.colorChangeListener(position);
                }
            }
        });

        mItemAdapter.setSelectPosition(mDefaultSelectItem = mAvatarP2A.getGlassesIndex());
        updateView(mAvatarP2A.getGlassesIndex());
        mFrameColorAdapter.setSelectPosition(mDefaultSelectColorFrame = (int) mAvatarP2A.getGlassesFrameColorValue());
        mColorAdapter.setSelectPosition(mDefaultSelectColor = (int) mAvatarP2A.getGlassesColorValue());
        scrollToPosition(mFrameColorRecycler, mDefaultSelectColorFrame);
        scrollToPosition(mColorRecycler, mDefaultSelectColor);
        return view;
    }

    @Override
    public void resetDefaultDeformParam() {
        mAvatarP2A.setGlassesIndex(mDefaultSelectItem);
        mAvatarP2A.setGlassesFrameColorValue(mDefaultSelectColorFrame);
        mAvatarP2A.setGlassesColorValue(mDefaultSelectColor);
    }

    @Override
    public boolean isChangeDeformParam() {
        return mDefaultSelectItem != mAvatarP2A.getGlassesIndex() ||
                mDefaultSelectColorFrame != mAvatarP2A.getGlassesFrameColorValue() ||
                mDefaultSelectColor != mAvatarP2A.getGlassesColorValue();
    }

    private void updateView(int position) {
        mColorText.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        mColorRecycler.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        mFrameColorText.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        mFrameColorRecycler.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
    }
}
