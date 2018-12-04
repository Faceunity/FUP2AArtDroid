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

import com.faceunity.p2a_art.R;
import com.faceunity.p2a_art.constant.ColorConstant;
import com.faceunity.p2a_art.fragment.EditFaceFragment;
import com.faceunity.p2a_art.fragment.editface.core.ColorAdapter;
import com.faceunity.p2a_art.fragment.editface.core.EditFaceBaseFragment;

/**
 * Created by tujh on 2018/8/22.
 */
public class EditFaceColorFragment extends EditFaceBaseFragment {
    public static final String TAG = EditFaceColorFragment.class.getSimpleName();

    private RecyclerView mColorRecycler;
    private ColorAdapter mColorAdapter;

    private int mDefaultSelectColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_face_color, container, false);

        switch (mEditFaceBaseFragmentId) {
            case EditFaceFragment.TITLE_LIP_INDEX:
                mColorAdapter = new ColorAdapter(getContext(), ColorConstant.lip_color);
                mDefaultSelectColor = mAvatarP2A.getLipColorValue() < 0 ? mFUP2ARenderer.fuItemGetParamLipColorIndex() : (int) mAvatarP2A.getLipColorValue();
                mColorAdapter.setSelectPosition(mDefaultSelectColor);
                break;
            case EditFaceFragment.TITLE_IRIS_INDEX:
                mColorAdapter = new ColorAdapter(getContext(), ColorConstant.iris_color);
                mColorAdapter.setSelectPosition(mDefaultSelectColor = (int) mAvatarP2A.getIrisColorValue());
                break;
        }

        mColorRecycler = view.findViewById(R.id.color_recycler);
        mColorRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mColorRecycler.setAdapter(mColorAdapter);
        ((SimpleItemAnimator) mColorRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mColorAdapter.setItemSelectListener(new ColorAdapter.ItemSelectListener() {
            @Override
            public void itemSelectListener(int position) {
                if (colorControllerListener != null) {
                    colorControllerListener.colorChangeListener(position);
                }
            }
        });

        scrollToPosition(mColorRecycler, mDefaultSelectColor);
        return view;
    }

    @Override
    public void resetDefaultDeformParam() {
        if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_LIP_INDEX)
            mAvatarP2A.setLipColorValue(mDefaultSelectColor);
        else
            mAvatarP2A.setIrisColorValue(mDefaultSelectColor);
    }

    @Override
    public boolean isChangeDeformParam() {
        if (mEditFaceBaseFragmentId == EditFaceFragment.TITLE_LIP_INDEX)
            return mDefaultSelectColor != (mAvatarP2A.getLipColorValue() < 0 ? mFUP2ARenderer.fuItemGetParamLipColorIndex() : mAvatarP2A.getLipColorValue());
        else
            return mDefaultSelectColor != mAvatarP2A.getIrisColorValue();
    }
}
